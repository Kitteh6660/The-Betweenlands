package thebetweenlands.common.item.equipment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.capability.IFlightCapability;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.network.serverbound.MessageFlightState;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRingOfFlight extends ItemRing {
	
	public ItemRingOfFlight() {
		this.setMaxDamage(1800);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.ring.flight.bonus"), 0));
		if (GuiScreen.hasShiftDown()) {
			String toolTip = I18n.get("tooltip.bl.ring.flight", KeyBindRegistry.RADIAL_MENU.getDisplayName(), Minecraft.getInstance().gameSettings.keyBindJump.getDisplayName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.get("tooltip.bl.press.shift"));
		}
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			IFlightCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FLIGHT, null);
			if(cap != null) {
				cap.setFlightRing(true);
				if(!cap.canFlyWithoutRing(player) && cap.canFlyWithRing(player, stack)) {
					double flightHeight = 3.5D;
					if(player.level.isClientSide() || cap.isFlying()) {
						player.abilities.mayfly = true;
					}
					boolean isFlying = cap.isFlying();
					CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
					if(!entity.onGround) {
						if(isFlying) {
							cap.onFlightTick(player, stack, !nbt.getBoolean("ringActive"));

							nbt.putBoolean("ringActive", true);

							if(entity.isCrouching()) {
								entity.motionY = -0.2F;
							} else {
								double actualPosY = entity.getY();
								double height = this.getGroundHeight(player);
								Vector3d dir = new Vector3d(entity.getLookVec().x, 0, entity.getLookVec().z).normalize();

								if(player.zza > 0) {
									entity.motionX += dir.x * 0.02F;
									entity.motionZ += dir.z * 0.02F;
								}

								double my = 0.0D;
								boolean moveUp = false;
								if(player.level.isClientSide()) {
									if(player instanceof EntityPlayerSP) {
										moveUp = ((EntityPlayerSP)player).movementInput.jump;
									}
								}
								if(moveUp) {
									my = 0.1D;
								}
								double mul = ((height - (actualPosY - flightHeight)));
								entity.motionY = my * mul;
								if(actualPosY - flightHeight > height && entity.motionY > 0) {
									entity.motionY = 0;
								}
								if(actualPosY - flightHeight > height) {
									entity.motionY = -Math.min(actualPosY - flightHeight - height, 2.0D) / 8.0F;
								}
							}

							entity.fallDistance = 0.0F;

							if(!entity.onGround && entity.level.isClientSide()) {
								if(cap.getFlightTime() > 40) {
									BLParticles.LEAF_SWIRL.spawn(entity.world, entity.getX(), entity.getY(), entity.getZ(), ParticleArgs.get().withData(400, 0.0F, entity));
								} else {
									for(int i = 0; i < 5; i++) {
										BLParticles.LEAF_SWIRL.spawn(entity.world, entity.getX(), entity.getY(), entity.getZ(), ParticleArgs.get().withData(400, 1.0F - (cap.getFlightTime() + i / 5.0F) / 40.0F, entity));
									}
								}
							}
						} else {
							nbt.putBoolean("ringActive", false);
						}
					} else {
						cap.setFlying(false);
						nbt.putBoolean("ringActive", false);
					}
				} else {
					if(!player.level.isClientSide()) {
						CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
						nbt.putBoolean("ringActive", false);
						if(cap.isFlying()) {
							cap.setFlying(false);
						}
					}
					if(cap.isFlying() && !player.onGround && player.level.isClientSide()) {
						if(cap.getFlightTime() > 40) {
							BLParticles.LEAF_SWIRL.spawn(entity.world, entity.getX(), entity.getY(), entity.getZ(), ParticleArgs.get().withData(400, 0.0F, entity));
						} else {
							for(int i = 0; i < 5; i++) {
								BLParticles.LEAF_SWIRL.spawn(entity.world, entity.getX(), entity.getY(), entity.getZ(), ParticleArgs.get().withData(400, 1.0F - (cap.getFlightTime() + i / 5.0F) / 40.0F, entity));
							}
						}
					}
				}
			}
		}
	}

	private double getGroundHeight(PlayerEntity player) {
		RayTraceResult result = player.level.rayTraceBlocks(new Vector3d(player.getX(), player.getY(), player.getZ()), new Vector3d(player.getX(), player.getY() - 64, player.getZ()), true);
		if(result != null && result.typeOfHit == Type.BLOCK) {
			return result.hitVec.y;
		}
		return -512.0D;
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if(event.player != null) {
			PlayerEntity player = event.player;
			if(!player.isCreative()) {
				IFlightCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FLIGHT, null);
				if(cap != null) {
					if(cap.isFlying()) {
						cap.setFlightTime(cap.getFlightTime() + 1);
					}
					ItemStack flightRing = ItemStack.EMPTY;

					IEquipmentCapability equipmentCap = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
					if(equipmentCap != null) {
						IInventory inv = equipmentCap.getInventory(EnumEquipmentInventory.RING);
						for(int i = 0; i < inv.getContainerSize(); i++) {
							ItemStack stack = inv.getItem(i);
							if(!stack.isEmpty() && stack.getItem() == ItemRegistry.RING_OF_FLIGHT) {
								flightRing = stack;
								break;
							}
						}
					}

					if(!flightRing.isEmpty() && player.level.isClientSide()) {
						if(!cap.canFlyWithoutRing(player) && cap.canFlyWithRing(player, flightRing)) {
							if(event.phase == Phase.START) {
								player.abilities.mayfly = false;
							} else {
								if(player.abilities.flying) {
									cap.setFlying(!cap.isFlying());
									if(player == TheBetweenlands.proxy.getClientPlayer()) {
										TheBetweenlands.networkWrapper.sendToServer(new MessageFlightState(cap.isFlying()));
									}
								}
							}
						} else if(cap.isFlying()) {
							cap.setFlying(false);
							if(player == TheBetweenlands.proxy.getClientPlayer()) {
								TheBetweenlands.networkWrapper.sendToServer(new MessageFlightState(cap.isFlying()));
							}
						}
					}
					if(player == TheBetweenlands.proxy.getClientPlayer() && player.tickCount % 20 == 0) {
						TheBetweenlands.networkWrapper.sendToServer(new MessageFlightState(cap.isFlying()));
					}
					if(event.phase == Phase.END) {
						if(flightRing.isEmpty() || !cap.isFlying()) {
							if(cap.getFlightRing()) {
								if(!cap.canFlyWithoutRing(player)) {
									player.abilities.flying = false;
									player.abilities.mayfly = false;
									if(player.level.isClientSide()) {
										player.abilities.setFlyingSpeed(0.05F);
									}
								}
								cap.setFlightRing(false);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { 
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.putBoolean("ringActive", false);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean("ringActive");
	}
}
