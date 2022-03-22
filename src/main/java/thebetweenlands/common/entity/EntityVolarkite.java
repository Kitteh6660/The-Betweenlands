package thebetweenlands.common.entity;

import java.util.Iterator;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.item.misc.ItemVolarkite;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.PlayerUtil;

public class EntityVolarkite extends Entity {
	public float prevRotationRoll;
	public float rotationRoll;

	protected int updraftTicks = 0;
	protected int downdraftTicks = 0;
	protected int draftSourcePos = 0;

	protected int userInAirTicks = 20;

	public EntityVolarkite(World world) {
		super(world);
		this.setSize(0.6F, 1.8F);
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	public void load(CompoundNBT nbt) {
	}

	@Override
	public void save(CompoundNBT nbt) {
	}

	@Override
	public double getMountedYOffset() {
		return 0.01D + (this.getControllingPassenger() != null ? -this.getControllingPassenger().getStepY() : 0);
	}

	@Override
	public double getStepY() {
		return this.getRidingEntity() != null ? -this.getRidingEntity().getMountedYOffset() : 0;
	}

	public void handleRiderDismount(LivingEntity rider) {
		float yaw = rider.yRot;
		float pitch = rider.xRot;

		rider.dismountRidingEntity();

		//Set rider's position to volarkite position
		rider.moveTo(this.getX(), this.getY(), this.getZ(), yaw, pitch);
		rider.motionX = this.motionX;
		rider.motionY = this.motionY;
		rider.motionZ = this.motionZ;
		rider.onGround = this.onGround;
	}

	@Override
	public void updatePassenger(Entity passenger) {
		super.updatePassenger(passenger);

		PlayerUtil.resetFloating(passenger);
		PlayerUtil.resetVehicleFloating(passenger);
	}

	@Override
	public void onEntityUpdate() {
		this.xOld = this.getX();
		this.yOld = this.getY();
		this.zOld = this.getZ();
		this.prevRotationPitch = this.xRot;
		this.prevRotationYaw = this.yRot;
		this.prevRotationRoll = this.rotationRoll;

		Entity passenger = this.getControllingPassenger();
		Entity riding = this.getRidingEntity();

		if(!this.level.isClientSide()) {
			//Allow player to "dismount" when the volarkite is just riding the player while walking
			if(riding != null && riding.isCrouching()) {
				this.dismountRidingEntity();
			}

			boolean hasUpdraft = this.updraftTicks > 0;

			if(this.onGround && !hasUpdraft) {
				this.userInAirTicks = 0;

				if(passenger != null) {
					this.removePassengers();
					this.startRiding(passenger, true);
				}
			} else {
				if(riding != null && (hasUpdraft || (riding.motionY < 0 && this.userInAirTicks++ > 3 && riding.fallDistance > 0.55f))) {
					double mx = riding.motionX;
					double my = riding.motionY;
					double mz = riding.motionZ;

					this.dismountRidingEntity();

					riding.startRiding(this);

					this.motionX = mx;
					this.motionY = my;
					this.motionZ = mz;
					this.velocityChanged = true;

					this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(riding));
				}
			}

			if(this.getServer() != null && this.getRidingEntity() != null) {
				this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(this.getRidingEntity()));
			}
		}

		if(riding != null) {
			this.yRot = riding.yRot;
		}

		double targetMotionY = -0.04D;

		this.motionY = targetMotionY + (this.motionY - targetMotionY) * 0.92D;

		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		this.handleWaterMovement();

		float invFriction = 1.0F;

		if(this.onGround) {
			invFriction *= 0.8F;
		}
		if(this.isInWater() || this.isInLava()) {
			invFriction *= 0.8F;
			if(this.world.getBlockState(new BlockPos(this.getX(), this.getY() + this.height + 0.75D, this.getZ())).getMaterial().isLiquid()) {
				invFriction *= 0.5F; 
			}
		}

		this.motionX *= invFriction;
		this.motionY *= invFriction;
		this.motionZ *= invFriction;

		Entity controller = passenger != null ? passenger : riding;

		Vector3d kiteDir = new Vector3d(Math.cos(Math.toRadians(this.yRot + 90)), 0, Math.sin(Math.toRadians(this.yRot + 90)));

		double rotIncr = 0;

		boolean hasValidUser = false;

		if(controller != null) {
			controller.fallDistance = 0;

			if(this.motionY < 0 && !this.onGround) {
				double speedBoost = -this.motionY * 0.1D + MathHelper.clamp(Math.sin(Math.toRadians(this.xRot)) * 0.5F, -0.02D, 0.02D);

				this.motionX += kiteDir.x * (speedBoost + 0.01D);
				this.motionZ += kiteDir.z * (speedBoost + 0.01D);

				this.velocityChanged = true;
			}

			Vector3d controllerDir = new Vector3d(Math.cos(Math.toRadians(controller.yRot + 90)), 0, Math.sin(Math.toRadians(controller.yRot + 90)));
			double rotDiff = Math.toDegrees(Math.acos(kiteDir.dotProduct(controllerDir))) * -Math.signum(kiteDir.cross(controllerDir).y);
			rotIncr = MathHelper.clamp(rotDiff * 0.05D, -1.0D, 1.0D);
			this.yRot += rotIncr;

			if(!this.onGround && controller instanceof LivingEntity) {
				float forward = ((LivingEntity) controller).zza;
				if(forward > 0.1F) {
					this.xRot = 20.0F + (this.xRot - 20.0F) * 0.9F;
					this.motionY -= 0.01D;
				} else if(forward < -0.1F) {
					this.xRot = -20.0F + (this.xRot + 20.0F) * 0.9F;
				}
			}

			Iterator<ItemStack> it = controller.getHeldEquipment().iterator();
			while(it.hasNext()) {
				ItemStack stack = it.next();
				if(!stack.isEmpty() && stack.getItem() instanceof ItemVolarkite && ((ItemVolarkite) stack.getItem()).canRideKite(stack, controller)) {
					hasValidUser = true;
					break;
				}
			}
		}

		if(!this.onGround && Math.abs(rotIncr) > 0.1D) {
			this.rotationRoll = (float) (rotIncr * 15 + (this.rotationRoll - rotIncr * 15) * 0.9D);
		} else {
			this.rotationRoll *= 0.9F;
		}

		this.xRot *= 0.9F;

		this.updateUpdraft();

		double speed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

		if(speed > 0.1D) {
			double dx = this.motionX / speed;
			double dz = this.motionZ / speed;

			this.motionX = (kiteDir.x + (dx - kiteDir.x) * 0.9D) * speed;
			this.motionZ = (kiteDir.z + (dz - kiteDir.z) * 0.9D) * speed;

			double maxSpeed = 0.6D;
			if(speed > maxSpeed) {
				double targetX = dx * maxSpeed;
				double targetZ = dz * maxSpeed;

				this.motionX = targetX + (this.motionX - targetX) * 0.8D;
				this.motionZ = targetZ + (this.motionZ - targetZ) * 0.8D;
			}

			this.velocityChanged = true;
		}

		if(!this.level.isClientSide() && !hasValidUser) {
			this.remove();
		}

		this.firstUpdate = false;
	}

	protected void updateUpdraft() {
		int range = 10;

		PooledMutableBlockPos pos = PooledMutableBlockPos.retain();
		pos.setPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getY()), MathHelper.floor(this.getZ()));

		for(int i = 0; i <= range; i++) {
			BlockState state = this.world.getBlockState(pos);

			Block block = state.getBlock();

			boolean hasSource = false;

			if(block instanceof IFluidBlock) {
				Fluid fluid = ((IFluidBlock) block).getFluid();
				if(fluid.getTemperature() > 373 /*roughly 100°C*/) {
					this.updraftTicks = 25;
					hasSource = true;
				} else if(fluid.getTemperature() < 272.15 /*roughly -1°C*/) {
					this.downdraftTicks = 25;
					hasSource = true;
				}
			} else if(state.getMaterial() == Material.FIRE || state.getMaterial() == Material.LAVA || block instanceof BlockFire || block == BlockRegistry.OCTINE_ORE || block == BlockRegistry.OCTINE_BLOCK) {
				this.updraftTicks = 25;
				hasSource = true;
			} else if(state.getMaterial() == Material.ICE || state.getMaterial() == Material.SNOW || state.getMaterial() == Material.CRAFTED_SNOW || state.getMaterial() == Material.PACKED_ICE) {
				this.downdraftTicks = 25;
				hasSource = true;
			} else if(!block.isAir(state, this.world, pos)) {
				break;
			}

			if(hasSource) {
				this.draftSourcePos = pos.getY();
				break;
			}

			pos.setPos(pos.getX(), pos.getY() - 1, pos.getZ());
		}

		pos.release();

		if(this.updraftTicks > 0 || this.downdraftTicks > 0) {
			if(this.motionY < 1.0D) {
				this.motionY += this.downdraftTicks > 0 ? -0.03D : 0.1D;
			}

			if(this.level.isClientSide()) {
				for(int i = 0; i < (this.downdraftTicks > 0 ? 2 : 10); i++) {
					float offsetX = this.world.rand.nextFloat() - 0.5F;
					float offsetZ = this.world.rand.nextFloat() - 0.5F;

					float len = (float)Math.sqrt(offsetX*offsetX + offsetZ*offsetZ);

					offsetX /= len;
					offsetZ /= len;

					this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.getX() + offsetX, this.draftSourcePos + (this.getY() + (this.downdraftTicks > 0 ? 2.4D : 1) - this.draftSourcePos) * this.world.rand.nextFloat(), this.getZ() + offsetZ, this.motionX, this.motionY + (this.downdraftTicks > 0 ? -0.15D : 0.25D), this.motionZ);
				}
			}
		}

		if(this.updraftTicks > 0) {
			this.updraftTicks--;
		}

		if(this.downdraftTicks > 0) {
			this.downdraftTicks--;
		}
	}

	@Override
	public void tick() {
		super.tick();

		while(this.yRot - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while(this.yRot - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}
	}

	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
	}

	@Override
	public boolean canPassengerSteer() {
		return true;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);

		passenger.fallDistance = 0;

		passenger.motionX = this.motionX;
		passenger.motionY = this.motionY;
		passenger.motionZ = this.motionZ;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		//No fall damage to node or rider
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return super.isInRangeToRenderDist(distance) || (this.getControllingPassenger() != null && this.getControllingPassenger().isInRangeToRenderDist(distance));
	}

	private static boolean isMountingEvent = false;

	@SubscribeEvent
	public static void onMountEvent(EntityMountEvent event) {
		if(!isMountingEvent) {
			isMountingEvent = true;

			try {
				if(event.isDismounting()) {
					Entity mount = event.getEntityBeingMounted();
					Entity rider = event.getEntityMounting();

					if(mount instanceof EntityVolarkite && rider instanceof LivingEntity) {
						event.setCanceled(true);
						((EntityVolarkite) mount).handleRiderDismount((LivingEntity) rider);
					}
				}
			} finally {
				isMountingEvent = false;
			}
		}
	}
}
