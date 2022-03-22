package thebetweenlands.common.entity.mobs;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;
import net.minecraft.util.SoundEvent;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySludgeJet extends Entity {

	public EntitySludgeJet(World world) {
		super(world);
		setSize(1F, 2.5F);
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide()) {
			if (tickCount > 20)
				remove();
		} else {
			if(this.tickCount == 1) {
				this.spawnSludgeJetParticles(0.0f);
				this.spawnSludgeJetParticles(0.125f);
				this.spawnSludgeJetParticles(0.25f);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnSludgeJetParticles(float yOffset) {
		for (double yy = this.getY() + yOffset; yy < this.getY() + yOffset + 2.5D; yy += 0.5D) {
			double d0 = this.getX() - 0.075F;
			double d1 = yy;
			double d2 = this.getZ() - 0.075F;
			double d3 = this.getX() + 0.075F;
			double d4 = this.getZ() + 0.075F;
			double d5 = this.getX();
			double d6 = yy + 0.25F;
			double d7 = this.getZ() + yOffset;
			double d8 = this.getY();

			BLParticles.TAR_BEAST_DRIP.spawn(world, d0, d1, d4, ParticleArgs.get().withMotion(10f * (yy - d8) * (rand.nextFloat() - 0.5f), 4f * (yy - d8), 10f * (yy - d8) * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			BLParticles.TAR_BEAST_DRIP.spawn(world, d3, d1, d2, ParticleArgs.get().withMotion(10f * (yy - d8) * (rand.nextFloat() - 0.5f), 4f * (yy - d8), 10f * (yy - d8) * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			BLParticles.TAR_BEAST_DRIP.spawn(world, d3, d1, d4, ParticleArgs.get().withMotion(10f * (yy - d8) * (rand.nextFloat() - 0.5f), 4f * (yy - d8), 10f * (yy - d8) * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			BLParticles.TAR_BEAST_DRIP.spawn(world, d0, d1, d2, ParticleArgs.get().withMotion(10f * (yy - d8) * (rand.nextFloat() - 0.5f), 4f * (yy - d8), 10f * (yy - d8) * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			BLParticles.TAR_BEAST_DRIP.spawn(world, d5, d6, d7, ParticleArgs.get().withMotion(10f * (yy - d8) * (rand.nextFloat() - 0.5f), 4f * (yy - d8), 10f * (yy - d8) * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			BLParticles.TAR_BEAST_DRIP.spawn(world, d0, d1, d2, ParticleArgs.get().withMotion(10f * (yy - d8) * (rand.nextFloat() - 0.5f), 4f * (yy - d8), 10f * (yy - d8) * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
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

		if (player.getBoundingBox().maxY >= getBoundingBox().minY && player.getBoundingBox().minY <= getBoundingBox().maxY)
			if (player.getBoundingBox().maxX >= getBoundingBox().minX && player.getBoundingBox().minX <= getBoundingBox().maxX)
				if (player.getBoundingBox().maxZ >= getBoundingBox().minZ && player.getBoundingBox().minZ <= getBoundingBox().maxZ) {
					if (!level.isClientSide()) {
						if (player.isPotionActive(ElixirEffectRegistry.EFFECT_DECAY.getPotionEffect()))
							player.addEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(60, 3));
						IDecayCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
						if (cap != null)
							cap.getDecayStats().addDecayAcceleration(0.1F);

					}
					if (level.isClientSide())
						player.addVelocity(0, 0.2D, 0);
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