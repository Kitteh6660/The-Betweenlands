package thebetweenlands.common.entity.projectiles;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.EntityRootGrabber;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFace;
import thebetweenlands.common.registries.ItemRegistry;

public class EntitySapSpit extends EntityThrowable {
	protected float damage;

	public EntitySapSpit(World worldIn) {
		super(worldIn);
	}

	public EntitySapSpit(World worldIn, LivingEntity throwerIn, float damage) {
		super(worldIn, throwerIn);
		this.damage = damage;
	}

	@Override
	public void tick() {
		super.tick();

		if(this.level.isClientSide()) {
			this.world.addParticle(ParticleTypes.ITEM_CRACK, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D, Item.getIdFromItem(ItemRegistry.SAP_SPIT));
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		if(id == 3) {
			for(int i = 0; i < 16; ++i) {
				this.world.addParticle(ParticleTypes.ITEM_CRACK, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D, Item.getIdFromItem(ItemRegistry.SAP_SPIT));
			}
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if(!this.level.isClientSide() && result.entityHit != this.thrower && result.entityHit instanceof EntitySpiritTreeFace == false && result.entityHit instanceof EntityRootGrabber == false) {
			if(result.entityHit != null) {
				result.entityHit.hurt(DamageSource.causeThrownDamage(this, this.getThrower()), this.damage);
			}
			
			this.world.setEntityState(this, (byte)3);
			this.remove();
		}
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);

		nbt.putFloat("damage", this.damage);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);

		this.damage = nbt.getFloat("damage");
	}
}
