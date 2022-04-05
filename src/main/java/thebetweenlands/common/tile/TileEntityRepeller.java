package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IBLBoss;
import thebetweenlands.common.entity.mobs.EntityWight;

public class TileEntityRepeller extends TileEntity implements ITickableTileEntity {
	
	private static final float MAX_RADIUS = 18.0F;
	private static final int DEPLOY_TIME = 80;

	protected boolean hasShimmerstone = false;
	protected int fuel = 0;
	protected boolean running = false;
	
	private boolean prevRunning = false;
	private float lastRadius = 0.0F;
	private float radius = 0.0F;
	private int deployTicks = 0;
	private int radiusState = 0;
	private float accumulatedCost = 0.0F;

	public int renderTicks = 0;

	public void setRadiusState(int state) {
		if(this.running && state % 4 != this.radiusState)
			this.deployTicks = 0;
		this.radiusState = state % 4;
		this.setChanged();
		BlockState blockState = this.level.getBlockState(this.worldPosition);
		this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, 3);
	}
	
	public int getRadiusState() {
		return this.radiusState;
	}

	public void cycleRadiusState() {
		this.radiusState = (this.radiusState + 1) % 4;
		if(this.running)
			this.deployTicks = 0;
		this.setChanged();
		BlockState blockState = this.level.getBlockState(this.worldPosition);
		this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, 3);
	}

	public void addShimmerstone() {
		this.hasShimmerstone = true;
		this.setChanged();
		BlockState blockState = this.level.getBlockState(this.worldPosition);
		this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, 3);
	}

	public boolean hasShimmerstone() {
		return this.hasShimmerstone;
	}

	public void removeShimmerstone() {
		this.hasShimmerstone = false;
		this.setChanged();
		BlockState blockState = this.level.getBlockState(this.worldPosition);
		this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, 3);
	}

	public int getMaxFuel() {
		return 10000;
	}

	public int addFuel(int amount) {
		if(amount != 0) {
			int canAdd = this.getMaxFuel() - this.fuel;
			if(canAdd > 0) {
				int added = Math.min(canAdd, amount);
				this.fuel += added;
				BlockState blockState = this.level.getBlockState(this.worldPosition);
				this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, 3);
				this.setChanged();
				return added;
			}
		}
		return 0;
	}

	public int removeFuel(int amount) {
		int removed = Math.min(this.fuel, amount);
		if(amount != 0) {
			this.fuel -= amount;
			BlockState blockState = this.level.getBlockState(this.worldPosition);
			this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, 3);
			this.setChanged();
		}
		return removed;
	}
	
	public int getFuel() {
		return this.fuel;
	}

	public void emptyFuel() {
		this.fuel = 0;
	}

	public boolean isRunning() {
		return this.running || this.radius > 0.0F;
	}

	public float getRadius(float partialTicks) {
		return this.lastRadius + (this.radius - this.lastRadius) * partialTicks;
	}

	@Override
	public void tick() {
		if(!this.level.isClientSide()) {
			if(this.fuel > 0) {
				if(this.fuel <= 0) {
					this.fuel = 0;
					this.setChanged();
				}
			}
			if(this.fuel > 0 && this.hasShimmerstone) {
				if(!this.running) {
					this.running = true;
					BlockState blockState = this.level.getBlockState(this.worldPosition);
					this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, 3);
					this.setChanged();
				}
			} else if(this.fuel <= 0 || !this.hasShimmerstone) {
				if(this.running) {
					this.running = false;
					BlockState blockState = this.level.getBlockState(this.worldPosition);
					this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, 3);
					this.setChanged();
				}
			}
			if(this.fuel < 0) {
				this.fuel = 0;
				BlockState blockState = this.level.getBlockState(this.worldPosition);
				this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, 3);
				this.setChanged();
			} else {
				float fuelCost = 0;
				double centerX = this.worldPosition.getX() + 0.5F;
				double centerY = this.worldPosition.getY() + 1.15F;
				double centerZ = this.worldPosition.getZ() + 0.5F;
				AxisAlignedBB affectedBB = new AxisAlignedBB(this.worldPosition.getX() - this.radius - 5.0F, this.worldPosition.getY() - this.radius - 5.0F, this.worldPosition.getZ() - this.radius - 5.0F, this.worldPosition.getX() + this.radius + 5.0F, this.worldPosition.getY() + this.radius + 5.0F, this.worldPosition.getZ() + this.radius + 5.0F);
				List<Entity> affectedEntities = this.level.getEntitiesOfClass(Entity.class, affectedBB);
				for(Entity entity : affectedEntities) {
					if(entity instanceof IMob && entity instanceof EntityWight == false && entity instanceof IBLBoss == false) {
						Vector3d closestPoint = this.getClosestAABBCorner(entity.getBoundingBox(), centerX, centerY, centerZ);
						if(closestPoint.squareDistanceTo(centerX, centerY, centerZ) < this.radius*this.radius) {
							double diffX = entity.getX() - centerX;
							double diffY = entity.getY() - centerY;
							double diffZ = entity.getZ() - centerZ;
							entity.move(MoverType.PISTON, diffX*0.1F, 0.0F, diffZ*0.1F);
							double len = Math.sqrt(diffX*diffX + diffY*diffY + diffZ*diffZ);
							double speed = (this.radius - len) / this.radius * 1.5F + 0.5F;
							entity.motionX = (float)(diffX / len) * speed;
							entity.motionZ = (float)(diffZ / len) * speed;
							if(entity instanceof LivingEntity) {
								((LivingEntity)entity).addEffect(new EffectInstance(Effects.SLOWNESS, 10, 6));
							}
							if(!entity.collidedHorizontally) {
								fuelCost += 0.00028F * (this.radiusState / 1.5F + 1);
							}
						}
					}
					if(entity instanceof IProjectile || entity instanceof EntityFireball) {
						Vector3d closestPoint = this.getClosestAABBCorner(entity.getBoundingBox(), centerX, centerY, centerZ);
						if(closestPoint.squareDistanceTo(centerX, centerY, centerZ) < this.radius*this.radius) {
							double velocity = Math.sqrt(entity.motionX*entity.motionX + entity.motionY*entity.motionY + entity.motionZ*entity.motionZ);
							double diffX = entity.getX() - centerX;
							double diffY = entity.getY() - centerY;
							double diffZ = entity.getZ() - centerZ;
							double len = Math.sqrt(diffX*diffX + diffY*diffY + diffZ*diffZ);
							entity.move(MoverType.PISTON, diffX*0.1F, 0.0F, diffZ*0.1F);
							entity.motionX = (float)(diffX / len) * velocity;
							entity.motionY = (float)(diffY / len) * velocity;
							entity.motionZ = (float)(diffZ / len) * velocity;
							if(entity instanceof IProjectile) {
								((IProjectile)entity).shoot(diffX / len, diffY / len, diffZ / len, 1.0F, 1.0F);
							}
							entity.velocityChanged = true;
							if(!entity.collidedHorizontally && !entity.collidedVertically && !entity.onGround) {
								fuelCost += 0.0004F * (this.radiusState / 1.5F + 1);
							}
						}
					}
				}

				boolean fuelConsumed = false;
				
				//Limit fuel cost per tick
				this.accumulatedCost += Math.min(fuelCost, 0.00125F) * 1000;
				while(this.accumulatedCost > 1.0F) {
					this.accumulatedCost -= 1.0F;
					this.fuel--;
					fuelConsumed = true;
				}

				if(fuelConsumed) {
					BlockState blockState = this.level.getBlockState(this.worldPosition);
					this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, 3);
				}
				
				this.setChanged();
			}
		} else {
			this.renderTicks++;
		}

		if(this.prevRunning != this.running) {
			this.deployTicks = 0;
			this.setChanged();
		}
		this.prevRunning = this.running;

		float desiredRadius = MAX_RADIUS / 4.0F * (this.radiusState + 1);
		this.lastRadius = this.radius;
		if(this.running && this.radius < desiredRadius && this.deployTicks < DEPLOY_TIME) {
			this.deployTicks++;
			this.radius = (float) this.easeInOut(this.deployTicks, this.radius, desiredRadius, DEPLOY_TIME);
			if(this.radius > desiredRadius) {
				this.radius = desiredRadius;
			}
			this.setChanged();
		} else if((!this.running && this.radius > 0.0F) || this.radius > desiredRadius) {
			this.deployTicks++;
			this.radius = (float) this.easeInOut(this.deployTicks, !this.running ? desiredRadius : this.radius, -desiredRadius, DEPLOY_TIME);
			if(!this.running && this.radius < 0.0F) {
				this.radius = 0.0F;
			} else if(this.running && this.radius < desiredRadius) {
				this.radius = desiredRadius;
			}
			this.setChanged();
		}
	}

	protected Vector3d getClosestAABBCorner(AxisAlignedBB bb, double centerX, double centerY, double centerZ) {
		Vector3d center = new Vector3d(centerX, centerY, centerZ);
		Vector3d closest = null;
		for(int bcx = 0; bcx <= 1; bcx++) {
			for(int bcy = 0; bcy <= 1; bcy++) {
				for(int bcz = 0; bcz <= 1; bcz++) {
					double cx = bcx == 1 ? bb.maxX : bb.minX;
					double cy = bcy == 1 ? bb.maxY : bb.minY;
					double cz = bcz == 1 ? bb.maxZ : bb.minZ;
					Vector3d current = new Vector3d(cx, cy, cz);
					if(closest == null || current.distanceTo(center) < closest.distanceTo(center)) {
						closest = current;
					}
				}
			}
		}
		return closest;
	}

	protected double easeInOut(float t, float b, float c, float d) {
		t /= d/2;
		if (t < 1) return c/2*t*t*t*t*t + b;
		t -= 2;
		return c/2*(t*t*t*t*t + 2) + b;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		double radius = this.getRadius(0.0F);
		return new AxisAlignedBB(this.worldPosition).inflate(radius, radius, radius).expandTowards(0, 1, 0);
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.fuel = nbt.getInt("fuel");
		this.hasShimmerstone = nbt.getBoolean("hasShimmerstone");
		this.deployTicks = nbt.getInt("deployTicks");
		this.radius = nbt.getFloat("radius");
		this.running = nbt.getBoolean("running");
		this.prevRunning = this.running;
		this.radiusState = nbt.getInt("radiusState");
		this.accumulatedCost = nbt.getFloat("accumulatedCost");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("fuel", this.fuel);
		nbt.putBoolean("hasShimmerstone", this.hasShimmerstone);
		nbt.putInt("deployTicks", this.deployTicks);
		nbt.putFloat("radius", this.radius);
		nbt.putBoolean("running", this.running);
		nbt.putInt("radiusState", this.radiusState);
		nbt.putFloat("accumulatedCost", this.accumulatedCost);
		return nbt;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("fuel", this.fuel);
		nbt.putBoolean("hasShimmerstone", this.hasShimmerstone);
		nbt.putBoolean("running", this.running);
		nbt.putInt("deployTicks", this.deployTicks);
		nbt.putFloat("radius", this.radius);
		nbt.putInt("radiusState", this.radiusState);
		return new SUpdateTileEntityPacket(this.getBlockPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getTag();
		this.fuel = nbt.getInt("fuel");
		this.hasShimmerstone = nbt.getBoolean("hasShimmerstone");
		this.deployTicks = nbt.getInt("deployTicks");
		this.radius = nbt.getFloat("radius");
		this.running = nbt.getBoolean("running");
		this.prevRunning = this.running;
		this.radiusState = nbt.getInt("radiusState");
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt("fuel", this.fuel);
		nbt.putBoolean("hasShimmerstone", this.hasShimmerstone);
		nbt.putBoolean("running", this.running);
		nbt.putInt("deployTicks", this.deployTicks);
		nbt.putFloat("radius", this.radius);
		nbt.putInt("radiusState", this.radiusState);
		return nbt;
	}
}
