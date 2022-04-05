package thebetweenlands.client.audio.ambience;

import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AmbienceSound extends MoveableSound {
	
	private boolean fadeOut = false;
	private boolean isLowPriority = false;

	public final PlayerEntity player;
	public final AmbienceType type;
	public final AmbienceManager mgr;

	public AmbienceSound(SoundEvent sound, SoundCategory category, AmbienceType type, PlayerEntity player, AmbienceManager mgr) {
		super(sound, category);
		this.type = type;
		this.player = player;
		this.repeat = true;
		this.attenuationType = AttenuationType.NONE;
		this.volume = 0.1F; //Start at 0.1 and fade in
		this.pitch = this.type.getPitch();
		this.mgr = mgr;
	}

	@Override
	public void update() {
		if(this.player == null || this.player.level == null) {
			this.stopImmediately();
			return;
		}

		this.xPosF = (float) this.player.getX();
		this.yPosF = (float) this.player.getY();
		this.zPosF = (float) this.player.getZ();

		if(!this.donePlaying)
			this.pitch = this.type.getPitch();

		float desiredVolume = this.type.getVolume();
		int fadeTicks = Math.max(this.type.getFadeTime(), 1);
		float incr = Math.max((this.volume + Math.abs(desiredVolume - this.volume)) / (float)fadeTicks, 0.001F);

		if(this.isStopping())
			desiredVolume = 0.0F;

		if(this.isLowPriority)
			desiredVolume = Math.min(desiredVolume, this.mgr.getLowerPriorityVolume());

		if(this.volume > desiredVolume) {
			this.volume -= incr;
			if(this.volume < desiredVolume) {
				this.volume = desiredVolume;
				if(this.isStopping())
					this.donePlaying = true;
			}
		} else if(this.volume < desiredVolume) {
			this.volume += incr;
			if(this.volume > desiredVolume) {
				this.volume = desiredVolume;
			}
		}

		if(this.volume <= 0.0F && this.isStopping())
			this.donePlaying = true;

		if(this.donePlaying)
			this.repeat = false;
	}

	/**
	 * Stops the ambience and makes it fade out
	 */
	public void stop() {
		this.fadeOut = true;
	}

	/**
	 * Returns whether this sound is currently fading out
	 */
	public boolean isFadingOut() {
		return this.fadeOut;
	}

	/**
	 * Cancels the fade out
	 */
	public void cancelFade() {
		this.fadeOut = false;
	}

	/**
	 * Stops the ambience immediately without fading out
	 */
	public void stopImmediately() {
		this.donePlaying = true;
		this.repeat = false;
	}

	/**
	 * Returns whether this ambience is currently stopped or fading out
	 * @return
	 */
	public boolean isStopping() {
		return this.donePlaying || this.fadeOut || !this.type.isActive();
	}

	void setLowPriority(boolean lowPriority) {
		this.isLowPriority = lowPriority;
	}
}
