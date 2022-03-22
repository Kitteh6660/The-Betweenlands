package thebetweenlands.client.audio;

import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.audio.IEntitySound;

@OnlyIn(Dist.CLIENT)
public class EntitySound<T extends Entity> extends SafeStreamSound implements IEntitySound {
	public final T entity;
	public final Predicate<T> isPlaying;

	protected boolean fadeOut = false;

	public EntitySound(SoundEvent sound, SoundCategory category, T entity, Predicate<T> isPlaying) {
		this(sound, category, entity, isPlaying, 0.4f);
	}
	
	public EntitySound(SoundEvent sound, SoundCategory category, T entity, Predicate<T> isPlaying, float volume) {
		super(sound, category);
		this.repeat = true;
		this.attenuationType = AttenuationType.LINEAR;
		this.entity = entity;
		this.isPlaying = isPlaying;
		this.xPosF = (float) this.entity.getX();
		this.yPosF = (float) this.entity.getY();
		this.zPosF = (float) this.entity.getZ();
		this.volume = volume;
	}

	@Override
	public void update() {
		super.update();
		
		this.xPosF = (float) this.entity.getX();
		this.yPosF = (float) this.entity.getY();
		this.zPosF = (float) this.entity.getZ();
		
		Entity view = Minecraft.getInstance().getRenderViewEntity();
		
		if(this.fadeOut || this.entity == null || !this.entity.isEntityAlive() || this.entity.isDead || !this.entity.world.isBlockLoaded(this.entity.getPosition())
				|| !this.isPlaying.test(this.entity) || view == null || this.entity.getDistance(view) > Math.max(16, this.volume * 16)) {
			this.fadeOut = true;

			this.volume -= 0.05F;
			if(this.volume <= 0.0F) {
				this.repeat = false;
				this.donePlaying = true;
				this.volume = 0;
			}
		}
	}

	/**
	 * Stops the sound immediately without fading out
	 */
	public void stopImmediately() {
		this.donePlaying = true;
		this.repeat = false;
	}

	/**
	 * Stops the sound and makes it fade out
	 */
	public void stop() {
		this.fadeOut = true;
	}

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
		return this.donePlaying || this.fadeOut;
	}

	@Override
	public Entity getMusicEntity() {
		return this.entity;
	}

	@Override
	public void stopEntityMusic() {
		this.stop();
	}
}
