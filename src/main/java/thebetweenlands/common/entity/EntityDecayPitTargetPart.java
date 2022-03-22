package thebetweenlands.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityDecayPitTargetPart extends MultiPartEntityPart {
	public final boolean isShield;

	public EntityDecayPitTargetPart(IEntityMultiPart parent, String partName, float width, float height, boolean isShield) {
		super(parent, partName, width, height);
		setSize(width, height);
		this.isShield = isShield;
	}

	@Override
	public String getName() {
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
	public void save(CompoundNBT compound) {
	}
}
