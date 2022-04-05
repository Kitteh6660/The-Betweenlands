package thebetweenlands.common.capability.playermounts;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class PlayerMountsEntityCapability extends EntityCapability<PlayerMountsEntityCapability, IPlayerMountsEntityCapability, PlayerEntity> implements IPlayerMountsEntityCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "player_mounts");
	}

	@Override
	protected Capability<IPlayerMountsEntityCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_PLAYER_MOUNTS;
	}

	@Override
	protected Class<IPlayerMountsEntityCapability> getCapabilityClass() {
		return IPlayerMountsEntityCapability.class;
	}

	@Override
	protected PlayerMountsEntityCapability getDefaultCapabilityImplementation() {
		return new PlayerMountsEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof PlayerEntity;
	}



	private List<CompoundNBT> queuedPassengers = new ArrayList<>();

	@Override
	public List<CompoundNBT> getQueuedPassengers() {
		return this.queuedPassengers;
	}

	@Override
	public void save(CompoundNBT nbt) {
		PlayerEntity player = this.getEntity();

		if(player.hasOnePlayerPassenger()) {
			ListNBT passengers = new ListNBT();

			for(Entity entity : player.getPassengers()) {
				if(entity instanceof IEntityBL) {
					CompoundNBT passengerNbt = new CompoundNBT();

					if(entity.saveAsPassenger(passengerNbt)) {
						passengers.add(passengerNbt);
					}
				}
			}

			if(!passengers.isEmpty()) {
				nbt.put("PlayerPassengers", passengers);
			}
		}
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.queuedPassengers.clear();

		if(nbt.contains("PlayerPassengers", Constants.NBT.TAG_LIST)) {
			ListNBT passengers = nbt.getList("PlayerPassengers", Constants.NBT.TAG_COMPOUND);

			for(int i = 0; i < passengers.size(); ++i) {
				this.queuedPassengers.add(passengers.getCompound(i));
			}
		}
	}

	/**
	 * Prevents player mounts from being removed before player is saved to NBT
	 */
	@SubscribeEvent
	public static void onMountEvent(EntityMountEvent event) {
		if(event.isDismounting()) {
			Entity mount = event.getEntityBeingMounted();
			Entity rider = event.getEntityMounting();

			if(mount instanceof ServerPlayerEntity && rider instanceof IEntityBL) {
				ServerPlayerEntity player = (ServerPlayerEntity) mount;

				if(player.hasDisconnected()) {
					event.setCanceled(true);
				}
			}
		}
	}

	/**
	 * Spawns the player passengers from the NBT tags
	 */
	@SubscribeEvent
	public static void onPlayerJoin(PlayerLoggedInEvent event) {
		PlayerEntity player = event.getPlayer();
		if(!player.level.isClientSide()) {
			IPlayerMountsEntityCapability cap = (IPlayerMountsEntityCapability) player.getCapability(CapabilityRegistry.CAPABILITY_PLAYER_MOUNTS, null);

			if(cap != null) {
				for(CompoundNBT nbt : cap.getQueuedPassengers()) {
					Entity entity = AnvilChunkLoader.readWorldEntity(nbt, player.level, true);
					if(entity != null) {
						entity.startRiding(player, true);
					}
				}
			}
		}
	}
}