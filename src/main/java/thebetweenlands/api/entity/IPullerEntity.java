package thebetweenlands.api.entity;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import thebetweenlands.common.entity.draeton.DraetonPhysicsPart;
import thebetweenlands.common.entity.draeton.EntityDraeton;

public interface IPullerEntity {
	@Nullable
	public EntityDraeton getCarriage();
	
	public void setPuller(EntityDraeton carriage, DraetonPhysicsPart puller);

	public float getPull(float pull);

	public float getCarriageDrag(float drag);

	public float getDrag(float drag);

	public Entity createReleasedEntity();
	
	public void spawnReleasedEntity();
}