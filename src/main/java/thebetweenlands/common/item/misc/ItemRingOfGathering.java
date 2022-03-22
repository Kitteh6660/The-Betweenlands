package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IRingOfGatheringMinion;
import thebetweenlands.api.storage.IOfflinePlayerDataHandler;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.item.equipment.ItemRing;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.common.world.storage.OfflinePlayerHandlerImpl;

public class ItemRingOfGathering extends ItemRing 
{
	public static final String NBT_OFFLINE_PLAYER_DATA_EQUIPMENT_KEY = "GatheringRingEquipped";
	public static final String NBT_OFFLINE_PLAYER_DATA_LIST_KEY = "GatheringRingList";

	public static final String NBT_SYNC_COUNT_KEY = "GatheringRingCountSync";
	public static final String NBT_LAST_USER_UUID_KEY = "LastUserUuid";

	public static final String NBT_LAST_TELEPORT_TICKS = "LastTeleportTicks";

	@Nullable
	public UUID getLastUserUuid(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		if(nbt != null && nbt.hasUUID(NBT_LAST_USER_UUID_KEY)) {
			return nbt.getUUID(NBT_LAST_USER_UUID_KEY);
		}
		return null;
	}

	public boolean isRingEquipped(UUID playerUuid) {
		IOfflinePlayerDataHandler handler = OfflinePlayerHandlerImpl.getHandler();
		if(handler != null) {
			CompoundNBT nbt = handler.getOfflinePlayerData(playerUuid);
			if(nbt != null && nbt.contains(NBT_OFFLINE_PLAYER_DATA_EQUIPMENT_KEY, Constants.NBT.TAG_BYTE)) {
				return nbt.getBoolean(NBT_OFFLINE_PLAYER_DATA_EQUIPMENT_KEY);
			}
		}
		return false;
	}

	public boolean setRingEquipped(UUID playerUuid, boolean equipped) {
		IOfflinePlayerDataHandler handler = OfflinePlayerHandlerImpl.getHandler();
		if(handler != null) {
			CompoundNBT nbt = handler.getOfflinePlayerData(playerUuid);
			if(nbt == null) {
				nbt = new CompoundNBT();
			}
			nbt.putBoolean(NBT_OFFLINE_PLAYER_DATA_EQUIPMENT_KEY, equipped);
			handler.setOfflinePlayerData(playerUuid, nbt);
			return true;
		}
		return false;
	}

	public boolean hasSpace(UUID playerUuid) {
		return this.getEntryCount(playerUuid) < this.getCapacity();
	}

	public int getEntryCount(UUID playerUuid) {
		IOfflinePlayerDataHandler handler = OfflinePlayerHandlerImpl.getHandler();
		if(handler != null) {
			CompoundNBT nbt = handler.getOfflinePlayerData(playerUuid);

			if(nbt != null && nbt.contains(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_LIST)) {
				ListNBT list = nbt.getList(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_COMPOUND);

				return list.size();
			}
		}

		return 0;
	}

	public static class RingEntityEntry {
		public final ResourceLocation id;
		public final CompoundNBT nbt;
		public final boolean respawnByAnimator;
		public final int animatorLifeCrystalCost;
		public final int animatorSulfurCost;

		public RingEntityEntry(ResourceLocation id, CompoundNBT nbt) {
			this.id = id;
			this.nbt = nbt;
			this.respawnByAnimator = false;
			this.animatorLifeCrystalCost = 0;
			this.animatorSulfurCost = 0;
		}

		public RingEntityEntry(ResourceLocation id, CompoundNBT nbt, int animatorLifeCrystalCost, int animatorSulfurCost) {
			this.id = id;
			this.nbt = nbt;
			this.respawnByAnimator = true;
			this.animatorLifeCrystalCost = animatorLifeCrystalCost;
			this.animatorSulfurCost = animatorSulfurCost;
		}
	}

	public boolean addEntry(UUID playerUuid, RingEntityEntry entry) {
		IOfflinePlayerDataHandler handler = OfflinePlayerHandlerImpl.getHandler();
		if(handler != null) {
			CompoundNBT nbt = handler.getOfflinePlayerData(playerUuid);
			if(nbt == null) {
				nbt = new CompoundNBT();
			}

			ListNBT list = nbt.getList(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_COMPOUND);

			CompoundNBT entryNbt = new CompoundNBT();
			entryNbt.putString("id", entry.id.toString());
			entryNbt.setTag("data", entry.nbt);

			entryNbt.putBoolean("respawnByAnimator", entry.respawnByAnimator);
			if(entry.respawnByAnimator) {
				entryNbt.putInt("animatorLifeCrystalCost", entry.animatorLifeCrystalCost);
				entryNbt.putInt("animatorSulfurCost", entry.animatorSulfurCost);
			}

			list.appendTag(entryNbt);

			nbt.setTag(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, list);

			handler.setOfflinePlayerData(playerUuid, nbt);

			return true;
		}

		return false;
	}

	@Nullable
	public RingEntityEntry getEntry(UUID playerUuid, boolean fromAnimator, Predicate<RingEntityEntry> predicate, boolean remove) {
		IOfflinePlayerDataHandler handler = OfflinePlayerHandlerImpl.getHandler();
		if(handler != null) {
			CompoundNBT nbt = handler.getOfflinePlayerData(playerUuid);

			if(nbt != null && nbt.contains(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_LIST)) {
				ListNBT list = nbt.getList(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_COMPOUND);

				if(list.size() > 0) {
					for(int i = 0; i < list.size(); i++) {
						CompoundNBT entryNbt = list.getCompound(i);

						if(entryNbt.contains("id", Constants.NBT.TAG_STRING) && entryNbt.getBoolean("respawnByAnimator") == fromAnimator) {
							RingEntityEntry entry;

							if(fromAnimator) {
								entry = new RingEntityEntry(new ResourceLocation(entryNbt.getString("id")), entryNbt.getCompoundTag("data"), entryNbt.getInt("animatorLifeCrystalCost"), entryNbt.getInt("animatorSulfurCost"));
							} else {
								entry = new RingEntityEntry(new ResourceLocation(entryNbt.getString("id")), entryNbt.getCompoundTag("data"));
							}

							if(predicate.test(entry)) {
								if(remove) {
									list.removeTag(i);

									nbt.setTag(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, list);

									handler.setOfflinePlayerData(playerUuid, nbt);
								}

								return entry;
							}
						}
					}
				}
			}
		}

		return null;
	}

	@Nullable
	public Entity returnEntityFromRing(double x, double y, double z, Entity user, UUID playerUuid, boolean fromAnimator) {
		List<Entity> returnedEntity = new ArrayList<>();

		this.getEntry(playerUuid, fromAnimator, entry -> {
			Entity entity = EntityList.createEntityByIDFromName(entry.id, user.world);

			entity.moveTo(x, y, z, user.level.random.nextFloat() * 360, 0);

			if(entity instanceof IRingOfGatheringMinion && ((IRingOfGatheringMinion) entity).returnFromRing(user, entry.nbt)) {
				returnedEntity.add(entity);
				return true;
			}

			return false;
		}, true);

		return returnedEntity.isEmpty() ? null : returnedEntity.get(0);
	}

	public int getCapacity() {
		return 6;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.ring.gathering.bonus"), 0));
		if (Screen.hasShiftDown()) {
			String toolTip = I18n.get("tooltip.bl.ring.gathering", KeyBindRegistry.RADIAL_MENU.getName(), KeyBindRegistry.USE_RING.getName(), KeyBindRegistry.USE_SECONDARY_RING.getName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.get("tooltip.bl.press.shift"));
		}
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		if(nbt != null && nbt.contains(NBT_SYNC_COUNT_KEY, Constants.NBT.TAG_BYTE)) {
			return nbt.getByte(NBT_SYNC_COUNT_KEY) > 0;
		}
		return false;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		if(nbt != null && nbt.contains(NBT_SYNC_COUNT_KEY, Constants.NBT.TAG_BYTE)) {
			return 1 - nbt.getByte(NBT_SYNC_COUNT_KEY) / (float)this.getCapacity();
		}
		return 1;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		int count = 0;
		if(nbt != null && nbt.contains(NBT_SYNC_COUNT_KEY, Constants.NBT.TAG_BYTE)) {
			count = nbt.getByte(NBT_SYNC_COUNT_KEY);
		}
		int maxCount = this.getCapacity();
		if(count == maxCount - 1) {
			return 0xFFAA00;
		} else if(count == maxCount) {
			return 0xFF2020;
		}
		return 0xFFFFFF;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, PlayerEntity player, Entity target) {
		return stack.getTag() == null || stack.getTag().getByte(NBT_SYNC_COUNT_KEY) <= 0 || player.isCrouching();
	}

	@Override
	public boolean canEquip(ItemStack stack, PlayerEntity player, Entity target) {
		return player == target;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) {
		if(entity instanceof PlayerEntity) {
			this.setRingEquipped(entity.getUUID(), true);
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
		if(entity instanceof PlayerEntity) {
			this.setRingEquipped(entity.getUUID(), false);
		}
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(entity.tickCount % 5 == 0) {
			this.updateStackEntryCount(entity.level, stack, entity);
		}

		this.updateLastUserUuid(entity.level, stack, entity);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if(entityIn.tickCount % 5 == 0) {
			this.updateStackEntryCount(worldIn, stack, entityIn);
		}

		this.updateLastUserUuid(worldIn, stack, entityIn);
	}

	protected void updateLastUserUuid(World world, ItemStack stack, Entity entity) {
		CompoundNBT nbt = stack.getTag();
		if(nbt == null) {
			stack.setTag(nbt = new CompoundNBT());
		}
		nbt.putUUID(NBT_LAST_USER_UUID_KEY, entity.getUUID());
		stack.setTag(nbt);
	}

	protected void updateStackEntryCount(World worldIn, ItemStack stack, Entity entityIn) {
		if(!worldIn.isClientSide() && entityIn instanceof PlayerEntity) {
			int count = this.getEntryCount(entityIn.getUUID());

			CompoundNBT nbt = stack.getTag();
			if(nbt == null) {
				stack.setTag(nbt = new CompoundNBT());
			}

			int syncCounter = nbt.getByte(NBT_SYNC_COUNT_KEY);

			if(syncCounter != count) {
				nbt.putByte(NBT_SYNC_COUNT_KEY, (byte) count);
				stack.setTag(nbt);
			}
		}
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);

		if(this.activateEffect(playerIn, stack)) {
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getItemInHand(handIn));
		}

		return new ActionResult<ItemStack>(ActionResultType.PASS, playerIn.getItemInHand(handIn));
	}

	@Override
	public void onKeybindState(PlayerEntity player, ItemStack stack, IInventory inventory, boolean active) {
		if(active) {
			this.activateEffect(player, stack);
		}
	}

	protected boolean activateEffect(PlayerEntity player, ItemStack stack) {
		CompoundNBT nbt = stack.getTag();

		if(nbt != null) {
			if(nbt.getByte(NBT_SYNC_COUNT_KEY) > 0) {
				if(this.getEntry(player.getUUID(), false, e -> true, false) != null) {
					if(!player.level.isClientSide()) {
						if(removeXp(player, 15) >= 15) {
							if(this.returnEntityFromRing(player.getX(), player.getY(), player.getZ(), player, player.getUUID(), false) != null) {
								return true;
							}
						} else {
							player.sendStatusMessage(new TranslationTextComponent("chat.ring_of_gathering.not_enough_xp"), true);
						}
					}
				} else {
					if(!player.level.isClientSide() && this.getEntryCount(player.getUUID()) > 0) {
						player.sendStatusMessage(new TranslationTextComponent("chat.ring_of_gathering.animator"), true);
					}
				}
			}

			boolean teleported = false;

			boolean missingTeleportXp = false;

			//Teleport loaded pets back to player
			long lastTeleportTicks = nbt.getLong(NBT_LAST_TELEPORT_TICKS);
			if(Math.abs(player.level.getGameTime() - lastTeleportTicks) > 20) {
				nbt.setLong(NBT_LAST_TELEPORT_TICKS, player.level.getGameTime());

				UUID thisPlayerUuid = player.getUUID();

				List<Entity> ownedEntities = player.level.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(256), e -> e instanceof IRingOfGatheringMinion);
				for(Entity ownedEntity : ownedEntities) {
					IRingOfGatheringMinion minion = (IRingOfGatheringMinion) ownedEntity;

					UUID playerUuid = minion.getRingOwnerId();

					if(playerUuid != null && playerUuid.equals(thisPlayerUuid)) {
						if(minion.shouldReturnOnCall()) {
							if(removeXp(player, 5) >= 5) {
								minion.returnToCall(player);
								teleported = true;
							} else {
								missingTeleportXp = true;
							}
						}
					}
				}
			}

			if(missingTeleportXp) {
				player.sendStatusMessage(new TranslationTextComponent("chat.ring_of_gathering.not_enough_xp"), true);
			}

			if(teleported) {
				return true;
			}
		}

		return false;
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		Entity entity = event.getEntity();

		if(!entity.level.isClientSide() && entity instanceof IRingOfGatheringMinion) {
			IRingOfGatheringMinion minion = (IRingOfGatheringMinion) entity;

			UUID playerUuid = minion.getRingOwnerId();

			if(playerUuid != null && minion.shouldReturnOnDeath(entity.level.getPlayerEntityByUUID(playerUuid) != null) && returnMinionToRing(minion)) {
				//Don't spawn loot etc.
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		if(!event.getWorld().isClientSide()) {
			Chunk chunk = event.getChunk();

			//Return minions that were lost due to chunk unloading back to ring

			List<Entity> entities = new ArrayList<>();

			int bx = chunk.x * 16;
			int bz = chunk.z * 16;

			chunk.getEntitiesOfTypeWithinAABB(Entity.class, new AxisAlignedBB(bx - 1, -999999, bz - 1, bx + 17, 999999, bz + 17), entities, e -> e instanceof IRingOfGatheringMinion);

			for(Entity entity : entities) {
				IRingOfGatheringMinion minion = (IRingOfGatheringMinion) entity;

				UUID playerUuid = minion.getRingOwnerId();

				if(playerUuid != null && minion.shouldReturnOnUnload(entity.level.getPlayerEntityByUUID(playerUuid) != null)) {
					returnMinionToRing(minion);
				}
			}
		}
	}

	private static boolean returnMinionToRing(IRingOfGatheringMinion minion) {
		Entity entity = (Entity) minion;

		if(!entity.level.isClientSide()) {
			ResourceLocation id = EntityList.getKey(entity);

			if(id != null) {
				UUID playerUuid = minion.getRingOwnerId();

				if(playerUuid != null && ItemRegistry.RING_OF_GATHERING.isRingEquipped(playerUuid) && ItemRegistry.RING_OF_GATHERING.hasSpace(playerUuid)) {
					CompoundNBT entityNbt = minion.returnToRing(playerUuid);

					RingEntityEntry entry;

					if(minion.isRespawnedByAnimator()) {
						entry = new RingEntityEntry(id, entityNbt, minion.getAnimatorLifeCrystalCost(), minion.getAnimatorSulfurCost());
					} else {
						entry = new RingEntityEntry(id, entityNbt);
					}

					if(ItemRegistry.RING_OF_GATHERING.addEntry(playerUuid, entry)) {
						//Minion was successfully returned to ring and can be removed without dropping anything
						entity.ejectPassengers();
						entity.setDropItemsWhenDead(false);
						entity.remove();

						return true;
					}
				}
			}
		}

		return false;
	}
}
