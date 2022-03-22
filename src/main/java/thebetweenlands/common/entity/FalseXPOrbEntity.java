package thebetweenlands.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.common.item.equipment.ItemRing;

public class FalseXPOrbEntity extends ExperienceOrbEntity implements IEntityAdditionalSpawnData {
	
	private UUID playerUUID;

	public FalseXPOrbEntity(EntityType<? extends ExperienceOrbEntity> entity, World worldIn) {
		super(entity, worldIn);
	}

	public FalseXPOrbEntity(World worldIn, double x, double y, double z, int expValue, @Nullable UUID playerUuid) {
		super(worldIn, x, y, z, expValue);
		this.playerUUID = playerUuid;
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);

		if(this.playerUUID != null) {
			compound.putUUID("PlayerUuid", this.playerUUID);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);

		if(compound.hasUUID("PlayerUuid")) {
			this.playerUUID = compound.getUUID("PlayerUuid");
		}
	}

	@Override
	public void playerTouch(PlayerEntity player) {
		if(this.playerUUID == null || this.playerUUID.equals(player.getUUID())) {
			if(this.value < 0 && !this.level.isClientSide() && this.throwTime == 0 && player.takeXpDelay == 0) {
				if(MinecraftForge.EVENT_BUS.post(new PlayerXpEvent(player))) {
					return;
				}

				player.takeXpDelay = 2;
				player.take(this, 1);

				this.value = Math.min(this.value + ItemRing.removeXp(player, -this.value), 0);

				if(this.value >= 0) {
					this.remove();
				}
			} else {
				super.playerTouch(player);
			}
		}
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeInt(Math.abs(this.value));
	}

	@Override
	public void readSpawnData(PacketBuffer buffer) {
		this.value = buffer.readInt();
	}
}
