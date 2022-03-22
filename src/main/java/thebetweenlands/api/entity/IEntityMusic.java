package thebetweenlands.api.entity;

import javax.annotation.Nullable;

import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.audio.IEntitySound;
import thebetweenlands.client.audio.EntityMusicSound;
import thebetweenlands.common.sound.BLSoundEvent;

public interface IEntityMusic {
	@Nullable
	@Deprecated
	public BLSoundEvent getMusicFile(PlayerEntity listener);

	public double getMusicRange(PlayerEntity listener);

	public boolean isMusicActive(PlayerEntity listener);

	@OnlyIn(Dist.CLIENT)
	@Nullable
	public default IEntitySound getMusicSound(PlayerEntity listener) {
		BLSoundEvent sound = this.getMusicFile(listener);
		return new EntityMusicSound<Entity>(sound, SoundCategory.MUSIC, (Entity) this, 1, AttenuationType.NONE);
	}

	public default int getMusicLayer(PlayerEntity listener) {
		return 0;
	}
	
	public default boolean canInterruptOtherEntityMusic(PlayerEntity listener) {
		return true;
	}
}
