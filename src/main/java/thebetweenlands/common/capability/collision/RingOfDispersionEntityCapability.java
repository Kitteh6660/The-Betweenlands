package thebetweenlands.common.capability.collision;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.item.equipment.ItemRingOfDispersion;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class RingOfDispersionEntityCapability extends EntityCapability<RingOfDispersionEntityCapability, IEntityCustomCollisionsCapability, PlayerEntity> implements IEntityCustomCollisionsCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "ring_of_dispersion");
	}

	@Override
	protected Capability<IEntityCustomCollisionsCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS;
	}

	@Override
	protected Class<IEntityCustomCollisionsCapability> getCapabilityClass() {
		return IEntityCustomCollisionsCapability.class;
	}

	@Override
	protected RingOfDispersionEntityCapability getDefaultCapabilityImplementation() {
		return new RingOfDispersionEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof PlayerEntity;
	}






	private boolean isPhasing;
	private double viewObstructionDistance;
	private double obstructionDistance;

	@Override
	public boolean isPhasing() {
		return this.isPhasing;
	}

	@Override
	public double getViewObstructionDistance() {
		return this.viewObstructionDistance;
	}

	@Override
	public double getViewObstructionCheckDistance() {
		return 0.25D;
	}

	@Override
	public double getObstructionDistance() {
		return this.obstructionDistance;
	}

	@Override
	public double getObstructionCheckDistance() {
		return 0.25D;
	}

	public static ItemStack getRing(PlayerEntity player) {
		IEquipmentCapability cap = (IEquipmentCapability) player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if (cap != null) {
			IInventory inv = cap.getInventory(EnumEquipmentInventory.RING);

			for(int i = 0; i < inv.getContainerSize(); i++) {
				ItemStack stack = inv.getItem(i);

				if(!stack.isEmpty() && stack.getItem() instanceof ItemRingOfDispersion) {
					return stack;
				}
			}
		}

		return ItemStack.EMPTY;
	}

	public static double calculateAABBDistance(AxisAlignedBB aabb1, AxisAlignedBB aabb2) {
		double dist;

		if(aabb1.intersects(aabb2)) {
			double dx = Math.max(aabb1.minX - aabb2.maxX, aabb2.minX - aabb1.maxX);
			double dy = Math.max(aabb1.minY - aabb2.maxY, aabb2.minY - aabb1.maxY);
			double dz = Math.max(aabb1.minZ - aabb2.maxZ, aabb2.minZ - aabb1.maxZ);
			dist = Math.max(dx, Math.max(dy, dz));
		} else {
			double dx = Math.max(0, Math.max(aabb1.minX - aabb2.maxX, aabb2.minX - aabb1.maxX));
			double dy = Math.max(0, Math.max(aabb1.minY - aabb2.maxY, aabb2.minY - aabb1.maxY));
			double dz = Math.max(0, Math.max(aabb1.minZ - aabb2.maxZ, aabb2.minZ - aabb1.maxZ));
			dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
		}

		return dist;
	}

	@Override
	public void getCustomCollisionBoxes(CollisionBoxHelper collisionBoxHelper, AxisAlignedBB aabb,
			List<AxisAlignedBB> collisionBoxes) {

		this.isPhasing = false;
		this.viewObstructionDistance = Double.MAX_VALUE;
		this.obstructionDistance = Double.MAX_VALUE;

		PlayerEntity player = this.getEntity();
		ItemStack stack = getRing(player);

		if(!stack.isEmpty()) {
			ItemRingOfDispersion item = (ItemRingOfDispersion) stack.getItem();

			AtomicBoolean ringActiveState = new AtomicBoolean(false);

			if(item.canPhase(player, stack)) {
				//Remove all normally collected collision boxes because they
				//need to be filtered
				collisionBoxes.clear();

				final double floor = player.getY() + 0.01D;

				final AxisAlignedBB originalAabb = aabb;
				final AxisAlignedBB viewAabb = new AxisAlignedBB(player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), player.getX(), player.getY() + player.getEyeHeight(), player.getZ()).inflate(0.25D);

				final double checkReach = Math.max(this.getViewObstructionCheckDistance(), this.getObstructionCheckDistance());

				collisionBoxHelper.getBlockCollisions(player, aabb.inflate(checkReach, 0, checkReach).expandTowards(0, checkReach, 0), EntityCollisionPredicate.ALL, new BlockCollisionPredicate() {
					@Override
					public boolean isColliding(Entity entity, AxisAlignedBB aabb, BlockPos.Mutable pos, BlockState state, @Nullable AxisAlignedBB blockAabb) {
						if(blockAabb == null) {
							return true;
						}

						boolean isCollisionForced = false;

						if(blockAabb.maxY < floor) {
							isCollisionForced = true;
						}

						if(!isCollisionForced && state.getPlayerRelativeBlockHardness(player, player.level, pos) < 0.0001F) {
							isCollisionForced = true;
						}

						if(!isCollisionForced) {
							double playerDist = calculateAABBDistance(blockAabb, originalAabb);

							if(playerDist < RingOfDispersionEntityCapability.this.getObstructionCheckDistance() && playerDist < RingOfDispersionEntityCapability.this.obstructionDistance) {
								RingOfDispersionEntityCapability.this.obstructionDistance = playerDist;
							}

							double viewDist = calculateAABBDistance(blockAabb, viewAabb);

							if(viewDist < RingOfDispersionEntityCapability.this.getViewObstructionCheckDistance() && viewDist < RingOfDispersionEntityCapability.this.viewObstructionDistance) {
								RingOfDispersionEntityCapability.this.viewObstructionDistance = viewDist;
							}
						}

						if(originalAabb.intersects(blockAabb)) {
							if(isCollisionForced) {
								return true;
							}

							ringActiveState.set(true);
							RingOfDispersionEntityCapability.this.isPhasing = true;

							return false;
						}

						return false;
					}
				}, collisionBoxes);
			}

			boolean newRingActiveState = ringActiveState.get();
			if(item.isActive(stack) != newRingActiveState) {
				item.setActive(stack, newRingActiveState);
			}
		}
	}

	@SubscribeEvent
	public static void onSPPlayerPushOut(PlayerSPPushOutOfBlocksEvent event) {
		PlayerEntity player = event.getPlayerEntity();

		IEntityCustomCollisionsCapability cap = (IEntityCustomCollisionsCapability) player.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);

		if(cap != null && cap.isPhasing()) {
			ItemStack stack = getRing(player);

			if(!stack.isEmpty()) {
				ItemRingOfDispersion item = (ItemRingOfDispersion) stack.getItem();

				if(item.canPhase(player, stack)) {
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerUpdate(PlayerTickEvent event) {
		if(event.phase == Phase.END) {
			PlayerEntity player = event.player;
	
			if(player != TheBetweenlands.proxy.getClientPlayer()) {
				IEntityCustomCollisionsCapability cap = (IEntityCustomCollisionsCapability) player.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);
	
				if(cap != null) {
					//For other players than the client player the collision logic isn't run
					//so we need to force it to be run to do the obstruction distance calculations
					player.level.getBlockCollisions(player, player.getBoundingBox());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingAttacked(LivingAttackEvent event) {
		if(event.getEntity() instanceof PlayerEntity && event.getSource().getMsgId().equals(DamageSource.IN_WALL.getMsgId())) {
			PlayerEntity player = (PlayerEntity) event.getEntity();

			IEntityCustomCollisionsCapability cap = (IEntityCustomCollisionsCapability) player.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);

			if(cap != null && cap.isPhasing()) {
				ItemStack stack = getRing(player);

				if(!stack.isEmpty()) {
					ItemRingOfDispersion item = (ItemRingOfDispersion) stack.getItem();

					if(item.canPhase(player, stack)) {
						event.setCanceled(true);
					}
				}
			}
		}
	}
}
