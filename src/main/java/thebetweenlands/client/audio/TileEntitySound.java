package thebetweenlands.client.audio;

import java.util.function.Predicate;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntitySound<T extends TileEntity> extends SafeStreamSound {
	public final T tile;
	public final BlockPos pos;
	public final Predicate<T> isPlaying;

	private boolean fadeOut = false;

	public TileEntitySound(SoundEvent sound, SoundCategory category, T tile, Predicate<T> isPlaying) {
		super(sound, category);
		this.looping = true;
		this.attenuation = AttenuationType.LINEAR;
		this.tile = tile;
		this.isPlaying = isPlaying;
		this.pos = tile.getBlockPos();
		this.x = this.pos.getX() + 0.5F;
		this.y = this.pos.getY() + 0.5F;
		this.z = this.pos.getZ() + 0.5F;
	}

	@Override
	public void tick() {
		super.tick();
		
		if(this.fadeOut || this.tile == null || !this.tile.hasLevel() || !this.tile.getLevel().isLoaded(this.tile.getBlockPos()) || this.tile.getLevel().getBlockEntity(this.tile.getBlockPos()) != this.tile || !this.isPlaying.test(this.tile)) {
			this.looping = false;
			this.fadeOut = true;

			this.volume -= 0.05F;
			if(this.volume <= 0.0F) {
				this.stopped = true;
				this.volume = 0;
			}
		}
	}

	/**
	 * Stops the sound immediately without fading out
	 */
	public void stopImmediately() {
		this.stopped = true;
		this.looping = false;
	}

	/**
	 * Stops the sound and makes it fade out
	 */
	/*public void stop() {
		this.fadeOut = true;
	}*/

	/**
	 * Cancels the fade out
	 */
	public void cancelFade() {
		this.fadeOut = false;
	}

	/**
	 * Returns whether this sound is currently stopped or fading out
	 * @return
	 */
	public boolean isStopping() {
		return this.stopped || this.fadeOut;
	}
}
