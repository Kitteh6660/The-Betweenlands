package thebetweenlands.common.entity;

import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;

public class EntityDecayPitTargetPart extends PartEntity<Entity> {
	
	public final boolean isShield;

	public EntityDecayPitTargetPart(Multipart parent, String partName, float width, float height, boolean isShield) {
		super(parent, partName, width, height);
		setSize(width, height);
		this.isShield = isShield;
	}

	@Override
	public ITextComponent getName() {
		return I18n.get("entity.thebetweenlands.decay_pit_target.name");
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isShield;
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return getBoundingBox();
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return getBoundingBox();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return getBoundingBox();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return parent.attackEntityFromPart(this, source, amount);
	}

	@Override
	public boolean isEntityEqual(Entity entity) {
		return this == entity || parent == entity;
	}

	@Override
	public void load(CompoundNBT compound) {
	}

	@Override
	public boolean save(CompoundNBT compound) {
		return true;
	}
}
