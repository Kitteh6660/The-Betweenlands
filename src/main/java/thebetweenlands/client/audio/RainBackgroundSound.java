package thebetweenlands.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.AmbienceSoundPlayHandler;

@OnlyIn(Dist.CLIENT)
public class RainBackgroundSound extends MovingSound {
	public RainBackgroundSound(SoundEvent sound, SoundCategory category) {
		super(sound, category);
		this.attenuationType = AttenuationType.NONE;
	}

	@Override
	public void update() {
		this.updateSound();
	}

	private void updateSound() {
		Entity view = Minecraft.getInstance().getRenderViewEntity();
		if(view != null) {
			this.xPosF = AmbienceSoundPlayHandler.getRelativeRainX() + (float)view.getX();
			this.yPosF = AmbienceSoundPlayHandler.getRelativeRainY() + (float)view.getY();
			this.zPosF = AmbienceSoundPlayHandler.getRelativeRainZ() + (float)view.getZ();
			this.pitch = 1.0f - AmbienceSoundPlayHandler.getRainAbove() * 0.5f;
			this.volume = (0.5f - AmbienceSoundPlayHandler.getRainAbove() * 0.4f) * AmbienceSoundPlayHandler.getRainVolume();
		}
	}
}
