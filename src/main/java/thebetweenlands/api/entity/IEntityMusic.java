package thebetweenlands.api.entity;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import thebetweenlands.common.sound.BLSoundEvent;

//TODO: Determine if this need updating.
public interface IEntityMusic {
	@Nullable
	@Deprecated
	public BLSoundEvent getMusicFile(Player listener);

	public double getMusicRange(Player listener);

	public boolean isMusicActive(Player listener);

	/*@OnlyIn(Dist.CLIENT)
	@Nullable
	public default IEntitySound getMusicSound(Player listener) {
		BLSoundEvent sound = this.getMusicFile(listener);
		return new EntityMusicSound<Entity>>(sound, SoundSource.MUSIC, (Entity) this, 1, Attenuation.NONE);
	}*/

	public default int getMusicLayer(Player listener) {
		return 0;
	}
	
	public default boolean canInterruptOtherEntityMusic(Player listener) {
		return true;
	}
}
