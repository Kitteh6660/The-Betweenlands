package thebetweenlands.common.entity;

import java.util.List;

import io.netty.buffer.PacketBuffer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleLightningArc;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import net.minecraft.world.server.ChunkManager;

public class EntityBLLightningBolt extends LightningBoltEntity implements IEntityAdditionalSpawnData {
	private static final byte EVENT_STRIKE = 80;

	private BlockPos startPos = BlockPos.ZERO;
	private int delay = 60;
	private boolean isFloatingTarget;
	
	private boolean effectOnly;
	
	public EntityBLLightningBolt(World world) {
		super(world, 0, 0, 0, true);
		this.setSize(1, 1);
		this.fireImmune = true;
	}

	public EntityBLLightningBolt(World world, double x, double y, double z, int delay, boolean isFloatingTarget, boolean effectOnly) {
		super(world, x, y, z, true);
		this.setSize(1, 1);
		this.fireImmune = true;
		this.delay = Math.max(8, delay);
		this.startPos = new BlockPos(x, y, z).add(level.random.nextInt(40) - 20, 80, level.random.nextInt(40) - 20);
		this.isFloatingTarget = isFloatingTarget;
		this.effectOnly = effectOnly;
	}

	@Override
	public void moveTo(double x, double y, double z, float yaw, float pitch) {
		super.moveTo(x, y, z, yaw, pitch);
		
		if(BlockPos.ORIGIN.equals(this.startPos)) {
			this.startPos = new BlockPos(x, y, z).add(level.random.nextInt(40) - 20, 80, level.random.nextInt(40) - 20);
		}
	}
	
	@Override
	protected void defineSynchedData() {

	}

	@Override
	public boolean writeToNBTOptional(CompoundNBT compound) {
		//don't save
		return false;
	}

	@Override
	public void load(CompoundNBT compound) {
		// No data here.
	}

	@Override
	public boolean save(CompoundNBT compound) { 
		return false; 
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_STRIKE) {
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, this.createParticle(new Vector3d(this.startPos.getX() + 0.5f, this.startPos.getY(), this.startPos.getZ() + 0.5f), this.getPositionVector()));

			if(this.isFloatingTarget) {
				BlockPos ground = this.level.getHeight(this.getPosition());
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, this.createParticle(this.getPositionVector(), new Vector3d(ground.getX() + 0.5f, ground.getY(), ground.getZ() + 0.5f)));
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private ParticleLightningArc createParticle(Vector3d start, Vector3d end) {
		ParticleLightningArc particle = (ParticleLightningArc) BLParticles.LIGHTNING_ARC.create(this.level, start.x, start.y, start.z, 
				ParticleArgs.get().withColor(0.5f, 0.4f, 1.0f, 0.9f).withData(end));

		particle.setBaseSize(0.8f);
		particle.setSubdivs(15, 4);
		particle.setOffsets(4.0f, 0.8f);
		particle.setSplits(3);
		particle.setSplitSpeed(0.1f, 0.65f);
		particle.setLengthDecay(0.1f);
		particle.setSizeDecay(0.3f);
		particle.setLifetime(20);

		return particle;
	}

	@Override
	public void tick() {
		if(!this.level.isClientSide()) {
			this.setFlag(6, this.isGlowing());
		}

		this.onEntityUpdate();

		this.delay = Math.max(this.delay - 1, 0);

		if(this.delay == 6) {
			this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundRegistry.THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
			this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundRegistry.LIGHTNING, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);

			this.level.setEntityState(this, EVENT_STRIKE);
		} else if(this.delay > 0 && this.delay <= 4) {
			if(this.level.isClientSide()) {
				this.level.setLastLightningBolt(2);
			} else if(!this.effectOnly) {
				if(this.delay == 4) {
					BlockPos blockpos = new BlockPos(this);

					if(!this.level.isClientSide() && this.level.getGameRules().getBoolean("doFireTick") && (this.level.getDifficulty() == Difficulty.NORMAL || this.level.getDifficulty() == Difficulty.HARD) && this.level.isAreaLoaded(blockpos, 10)) {
						if(this.level.getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(this.level, blockpos)) {
							this.level.setBlockState(blockpos, Blocks.FIRE.defaultBlockState());
						}

						for(int i = 0; i < 4; ++i) {
							BlockPos blockpos1 = blockpos.add(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);

							if(this.level.getBlockState(blockpos1).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(this.level, blockpos1)) {
								this.level.setBlockState(blockpos1, Blocks.FIRE.defaultBlockState());
							}
						}
					}
				}


				Vector3d start = new Vector3d(this.startPos.getX() + 0.5f, this.startPos.getY(), this.startPos.getZ());
				Vector3d end = this.getPositionVector();

				Vector3d diff = end.subtract(start);
				Vector3d dir = diff.normalize();

				double length = diff.length();

				double range = 5.0D;

				int steps = MathHelper.ceil(length / range / 2);
				for(int i = 0; i < steps;i++) {
					Vector3d checkPos = start.add(diff.scale(1 / (float)steps * (i + 1)));

					List<Entity> nearbyEntities = this.level.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(checkPos.x - range, checkPos.y - range, checkPos.z - range, checkPos.x + range, checkPos.y + range, checkPos.z + range));

					for(Entity entity : nearbyEntities) {
						if(entity instanceof EntityLightningBolt == false) {
							Vector3d entityPos = entity.getPositionVector();

							Vector3d projection = start.add(dir.scale(dir.dotProduct(entityPos.subtract(start))));

							if(projection.subtract(entityPos).length() < range) {

								if(entity instanceof ItemEntity) {
									ItemEntity ItemEntity = (ItemEntity) entity;
									ItemStack stack = ItemEntity.getItem();
									Item item = stack.getItem();

									if(item == ItemRegistry.ANGLER_TOOTH_ARROW || item == ItemRegistry.BASILISK_ARROW || item == ItemRegistry.OCTINE_ARROW || item == ItemRegistry.POISONED_ANGLER_TOOTH_ARROW || item == ItemRegistry.SLUDGE_WORM_ARROW) {
										if(this.level.random.nextInt(5) == 0) {
											int converted = this.level.random.nextInt(Math.min(stack.getCount(), 5)) + 1;

											stack.shrink(converted);
											if(stack.isEmpty()) {
												ItemEntity.remove();
											} else {
												ItemEntity.setItem(stack);
											}

											ItemEntity arrows = new ItemEntity(this.level, ItemEntity.getX(), ItemEntity.getY(), ItemEntity.getZ(), new ItemStack(ItemRegistry.SHOCK_ARROW, converted));
											this.level.spawnEntity(arrows);
										}
									} else if(item == ItemRegistry.CHIROBARB_ERUPTER) {
										ItemEntity.setItem(new ItemStack(ItemRegistry.CHIROBARB_SHOCK_ERUPTER, stack.getCount()));
									}
									
								} else if(!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, this)) {
									entity.onStruckByLightning(this);
									
									if(this.isFloatingTarget && entity instanceof ServerPlayerEntity) {
										AdvancementCriterionRegistry.STRUCK_BY_LIGHTNING_WHILE_FLYING.trigger((ServerPlayerEntity) entity);
									}
								}

							}
						}
					}
				}
			}
		} else if(this.delay == 0 && !this.level.isClientSide()) {
			this.remove();
		}

		if(this.level.isClientSide()) {
			this.spawnArcs();
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnArcs() {
		Entity view = Minecraft.getInstance().getRenderViewEntity();

		if(view != null && (this.delay < 30 || this.tickCount % (this.delay / 20 + 1) == 0)) {
			float dst = view.getDistance(this);

			if(dst < 100) {
				float ox = (this.level.random.nextFloat() - 0.5f) * 4;
				float oy;
				if(this.isFloatingTarget) {
					oy = (this.level.random.nextFloat() - 0.5f) * 4;
				} else {
					oy = this.level.random.nextFloat() * 2;
				}
				float oz = (this.level.random.nextFloat() - 0.5f) * 4;

				ParticleLightningArc particle = (ParticleLightningArc) BLParticles.LIGHTNING_ARC.create(this.level, this.getX(), this.getY(), this.getZ(), 
						ParticleArgs.get()
						.withColor(0.5f, 0.4f, 1.0f, 0.9f)
						.withData(new Vector3d(this.getX() + ox, this.getY() + oy, this.getZ() + oz)));

				if(dst > 30) {
					//lower quality
					particle.setBaseSize(0.1f);
					particle.setSubdivs(2, 1);
					particle.setSplits(2);
				}

				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, particle);

				if(dst < 16) {
					this.level.playSound(this.getX(), this.getY(), this.getZ(), SoundRegistry.ZAP, SoundCategory.AMBIENT, 1, 1, false);
				}
			}
		}
	}

	@Override
	public void writeSpawnData(PacketBuffer buf) {
		buf.writeBoolean(this.isFloatingTarget);
		buf.writeInt(this.delay);
		buf.writeInt(this.startPos.getX());
		buf.writeInt(this.startPos.getY());
		buf.writeInt(this.startPos.getZ());
	}

	@Override
	public void readSpawnData(PacketBuffer buf) {
		this.isFloatingTarget = buf.readBoolean();
		this.delay = buf.readInt();
		this.startPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}
}
