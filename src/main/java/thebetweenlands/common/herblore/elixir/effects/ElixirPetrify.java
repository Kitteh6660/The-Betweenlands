package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ElixirPetrify extends ElixirEffect {
	public ElixirPetrify(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "c28c1955-14a1-49fb-9444-5fea9d83c75e", -1.0D, 2);
	}

	@Override
	protected void performEffect(LivingEntity entity, int strength) {
		CompoundNBT nbt = entity.getEntityData();
		
		if(nbt.getInt("thebetweenlands.petrify.ticks") != entity.tickCount - 1) {
			nbt.putFloat("thebetweenlands.petrify.yaw", entity.yRot);
			nbt.putFloat("thebetweenlands.petrify.yawHead", entity.rotationYawHead);
			nbt.putFloat("thebetweenlands.petrify.pitch", entity.xRot);
		}
		nbt.putInt("thebetweenlands.petrify.ticks", entity.tickCount);
		
		entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), nbt.getFloat("thebetweenlands.petrify.yaw"), nbt.getFloat("thebetweenlands.petrify.pitch"));
		entity.rotationYawHead = nbt.getFloat("thebetweenlands.petrify.yawHead");
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		return true;
	}
}
