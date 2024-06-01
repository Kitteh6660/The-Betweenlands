package thebetweenlands.api.audio;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.Entity;

public interface IEntitySound extends SoundInstance {
	public Entity getMusicEntity();
	
	public void stopEntityMusic();
}
