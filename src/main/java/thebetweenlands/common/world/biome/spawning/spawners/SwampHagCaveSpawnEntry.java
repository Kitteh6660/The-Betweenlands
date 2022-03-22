package thebetweenlands.common.world.biome.spawning.spawners;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.Attributes.ModifiableAttributeInstance;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntitySwampHag;

public class SwampHagCaveSpawnEntry extends CaveSpawnEntry {
	public SwampHagCaveSpawnEntry(int id, short baseWeight) {
		super(id, EntitySwampHag.class, EntitySwampHag::new, baseWeight);
	}

	@Override
	public MobEntity createEntity(World world) {
		MobEntity entity = super.createEntity(world);
		float multiplier = (float)this.getWeight() / (float)this.getBaseWeight();
		ModifiableAttributeInstance movementAttr = entity.getEntityAttribute(Attributes.MOVEMENT_SPEED);
		movementAttr.setBaseValue(movementAttr.getBaseValue() + 0.075D * multiplier);
		ModifiableAttributeInstance attackAttr = entity.getEntityAttribute(Attributes.ATTACK_DAMAGE);
		attackAttr.setBaseValue(attackAttr.getBaseValue() + 5.0D * multiplier);
		return entity;
	}
}
