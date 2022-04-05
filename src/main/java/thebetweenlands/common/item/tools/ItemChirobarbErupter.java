package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;


public class ItemChirobarbErupter extends Item {
	public final boolean electric;

	public ItemChirobarbErupter(boolean electric, Properties properties) {
		super(properties);
		this.electric = electric;
		/*this.maxStackSize = 1;
		this.setMaxDamage(64);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);*/
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.chirobarb_erupter.usage"), 0));
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeldItem) {
		if(!level.isClientSide()) {
			if (!stack.hasTag())
				stack.setTag(new CompoundNBT());
			if (!stack.getTag().contains("shooting"))
				stack.getTag().putBoolean("shooting", false);
			if (!stack.getTag().contains("rotation"))
				stack.getTag().putInt("rotation", 0);


			if (stack.getTag().getBoolean("shooting") && entity instanceof LivingEntity) {
				stack.getTag().putInt("rotation", stack.getTag().getInt("rotation") + 30);

				if (stack.getTag().getInt("rotation") > 720) {
					stack.getTag().putInt("rotation", 0);
					stack.getTag().putBoolean("shooting", false);
				} else if (stack.getTag().getInt("rotation") % 30 == 0) {
					EntityBLArrow arrow = new EntityBLArrow(world, (LivingEntity) entity);

					arrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;

					arrow.setDamage(6);

					if(this.electric) {
						arrow.setType(EnumArrowType.CHIROMAW_SHOCK_BARB);
					} else {
						arrow.setType(EnumArrowType.CHIROMAW_BARB);
					}

					double angle = Math.toRadians(entity.yRot + stack.getTag().getInt("rotation") - 30F);
					double dx = -Math.sin(angle);
					double dz = Math.cos(angle);
					double offsetX = dx * 1.5D;
					double offsetZ = dz * 1.5D;

					List<Entity> nearbyEntities = world.getEntitiesInAABBexcluding(entity, entity.getBoundingBox().inflate(12, 0, 12), 
							e -> e instanceof LivingEntity && e instanceof IMob && Math.abs(e.getEntityData().getInt("thebetweenlands.chirobarb_erupter.lastTargetted") - e.tickCount) >= 60);

					arrow.setPosition(entity.getX() + offsetX, entity.getY() + entity.height * 0.75D, entity.getZ() + offsetZ);

					Entity closestNearby = null;
					double closestNearbyAngle = 0;
					double closestNearbyDstSq = Double.MAX_VALUE;

					for(Entity nearby : nearbyEntities) {
						Vector3d pos = nearby.getDeltaMovement().add(0, nearby.height / 2, 0);

						Vector3d diff = pos.subtract(entity.getPositionEyes(1));
						double dstSq = diff.lengthSqr();

						Vector3d dir = new Vector3d(diff.x, 0, diff.z).normalize();
						double angleDiff = Math.acos(dir.x * dx + dir.z * dz);

						if(dstSq < closestNearbyDstSq && Math.abs(diff.y) < 2 && angleDiff <= Math.toRadians(15.0f)) {
							closestNearby = nearby;
							closestNearbyDstSq = dstSq;
							Vector3d trajectory = pos.subtract(arrow.getDeltaMovement()).normalize();
							closestNearbyAngle = Math.toDegrees(Math.atan2(trajectory.z, trajectory.x)) - 90;
						}
					}

					float velocity = this.electric ? 1.4F : 1.1F;

					if(closestNearby != null) {
						closestNearby.getEntityData().putInt("thebetweenlands.chirobarb_erupter.lastTargetted", closestNearby.tickCount);

						arrow.shoot(entity, 0F, (float)closestNearbyAngle, 1.5F, velocity, 0F);
					} else {
						arrow.shoot(entity, 0F, entity.yRot + stack.getTag().getInt("rotation") - 30F, 1.5F, velocity, 0F);
					}

					world.playLocalSound(null, entity.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE, SoundCategory.NEUTRAL, 0.25F, 1F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.8F);
					world.addFreshEntity(arrow);
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!stack.hasTag()) {
			stack.setTag(new CompoundNBT());
			return new ActionResult<ItemStack>(ActionResultType.PASS, player.getItemInHand(hand));
		}

		if(player.getCooldowns().isOnCooldown(this))
			return new ActionResult<ItemStack>(ActionResultType.PASS, player.getItemInHand(hand));

		if (!stack.getTag().getBoolean("shooting")) {
			if (!level.isClientSide()) {
				stack.hurtAndBreak(1, player, (entity) -> {
					entity.broadcastBreakEvent(player.getUsedItemHand());
				});
				player.getCooldowns().addCooldown(this, 60);
				stack.getTag().putBoolean("shooting", true);
				stack.getTag().putInt("rotation", 0);
				world.playLocalSound(null, player.blockPosition(), SoundRegistry.CHIROBARB_ERUPTER, SoundCategory.NEUTRAL, 1F, 1F + (random.nextFloat() - random.nextFloat()) * 0.8F);
			}
			player.swing(hand);
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, player.getItemInHand(hand));
		}
		return new ActionResult<ItemStack>(ActionResultType.PASS, player.getItemInHand(hand));
	}

	private static final ImmutableList<String> STACK_NBT_EXCLUSIONS = ImmutableList.of("shooting", "rotation");

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !NBTHelper.areItemStackTagsEqual(oldStack, newStack, STACK_NBT_EXCLUSIONS);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isFoil(ItemStack stack) {
		return this.electric;
	}
}
