package thebetweenlands.common.entity.mobs;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class EntitySporeJet extends Entity {

	public EntitySporeJet(World world) {
		super(world);
		setSize(1.6F, 3.0F);
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide()) {
			if (tickCount > 15)
				remove();
		} else {
			if(this.tickCount == 1) {
				this.spawnSporeJetParticles();
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnSporeJetParticles() {
		for (double yy = this.getY() + 0.625F; yy < this.getY() + 0.625F + 2D; yy += 0.5D) {
			double d0 = this.getX() - 0.075F;
			double d1 = yy;
			double d2 = this.getZ() - 0.075F;
			double d3 = this.getX() + 0.075F;
			double d4 = this.getZ() + 0.075F;
			double d5 = this.getX();
			double d6 = yy + 0.25F;
			double d7 = this.getZ();
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d4, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d3, d1, d2, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d3, d1, d4, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d5, d6, d7, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
		}
	}
	
	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return false;
	}

	@Override
	public void onCollideWithPlayer(PlayerEntity player) {
		if (!level.isClientSide()) {
			if (player.getBoundingBox().maxY >= getBoundingBox().minY && player.getBoundingBox().minY <= getBoundingBox().maxY)
				if (player.getBoundingBox().maxX >= getBoundingBox().minX && player.getBoundingBox().minX <= getBoundingBox().maxX)
					if (player.getBoundingBox().maxZ >= getBoundingBox().minZ && player.getBoundingBox().minZ <= getBoundingBox().maxZ) {
						//((LivingEntity) player).addEffect(new EffectInstance(Effects.POISON, 5 * 20, 0));
						ItemStack stack = player.getMainHandItem();
						if (!stack.isEmpty())
							player.drop(true);
					}
		}
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	public void load(CompoundNBT compound) {
	}

	@Override
	public void save(CompoundNBT compound) {
	}

}