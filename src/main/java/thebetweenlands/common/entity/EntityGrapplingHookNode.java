package thebetweenlands.common.entity;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.PacketBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.item.misc.ItemGrapplingHook;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.PlayerUtil;

public class EntityGrapplingHookNode extends Entity implements IEntityAdditionalSpawnData {
	private static final DataParameter<Integer> DW_PREV_NODE = EntityDataManager.createKey(EntityGrapplingHookNode.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> DW_NEXT_NODE = EntityDataManager.createKey(EntityGrapplingHookNode.class, DataSerializers.VARINT);
	private static final DataParameter<Float> DW_CURRENT_ROPE_LENGTH = EntityDataManager.createKey(EntityGrapplingHookNode.class, DataSerializers.FLOAT);
	private static final DataParameter<Boolean> DW_ATTACHED = EntityDataManager.createKey(EntityGrapplingHookNode.class, DataSerializers.BOOLEAN);

	private UUID nextNodeUUID;
	private UUID prevNodeUUID;

	private int cachedPrevNodeDW;
	private int cachedNextNodeDW;

	private Entity cachedNextNodeEntity;
	private Entity cachedPrevNodeEntity;

	protected boolean isExtending = false;
	protected boolean climbing = false;

	protected double correctionX;
	protected double correctionY;
	protected double correctionZ;

	protected int pullCounter = 0;

	/**
	 * Number of nodes the grappling hook rope has.
	 * Only updated on mount node!
	 */
	protected int nodeCount = 0;
	
	/**
	 * Only set on mount node
	 */
	protected int maxNodeCount;

	protected Vector3d prevWeightPos;
	protected Vector3d weightPos;

	public EntityGrapplingHookNode(World world) {
		super(world);
		this.setSize(0.1F, 0.1F);
	}

	public EntityGrapplingHookNode(World world, int nodeCount, int maxNodeCount) {
		super(world);
		this.setSize(0.1F, 0.1F);
		this.nodeCount = nodeCount;
		this.maxNodeCount = maxNodeCount;
	}

	@Override
	protected void defineSynchedData() {
		this.getDataManager().register(DW_PREV_NODE, -1);
		this.cachedPrevNodeDW = -1;
		this.getDataManager().register(DW_NEXT_NODE, -1);
		this.cachedNextNodeDW = -1;
		this.getDataManager().register(DW_CURRENT_ROPE_LENGTH, (float) this.getDefaultRopeLength());
		this.getDataManager().register(DW_ATTACHED, false);
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.setNextNodeUUID(nbt.hasUUID("nextNodeUUID") ? nbt.getUUID("nextNodeUUID") : null);
		this.setPreviousNodeUUID(nbt.hasUUID("previousNodeUUID") ? nbt.getUUID("previousNodeUUID") : null);
		if(nbt.contains("ropeLength", Constants.NBT.TAG_FLOAT)) {
			this.setCurrentRopeLength(nbt.getFloat("ropeLength"));
		} else {
			this.setCurrentRopeLength((float) this.getDefaultRopeLength());
		}
		this.nodeCount = nbt.getInt("nodeCount");
		this.maxNodeCount = nbt.getInt("maxNodeCount");
	}

	@Override
	public void save(CompoundNBT nbt) {
		if(this.getNextNodeUUID() != null) {
			nbt.putUUID("nextNodeUUID", this.getNextNodeUUID());
		}
		if(this.getPreviousNodeUUID() != null) {
			nbt.putUUID("previousNodeUUID", this.getPreviousNodeUUID());
		}
		nbt.putFloat("ropeLength", this.getCurrentRopeLength());
		nbt.putInt("nodeCount", this.nodeCount);
		nbt.putInt("maxNodeCount", this.maxNodeCount);
	}

	@Override
	public double getMountedYOffset() {
		return 0.01D + (this.getControllingPassenger() != null ? -this.getControllingPassenger().getStepY() : 0);
	}

	@Override
	public void onEntityUpdate() {
		if(this.tickCount < 2) {
			//Stupid EntityTrackerEntry is broken and desyncs server position.
			//Tracker updates server side position but *does not* send the change to the client
			//when tracker.updateCounter == 0, causing a desync until the next force teleport
			//packet.......
			//By not moving the entity until then it works.
			return;
		}

		if(this.isMountNode()) {
			this.setSize(0.6F, 1.8F);
		} else {
			this.setSize(0.1F, 0.1F);
		}

		this.xOld = this.getX();
		this.yOld = this.getY();
		this.zOld = this.getZ();

		boolean attached = this.isAttached();

		if(!this.level.isClientSide()) {
			this.getDataManager().set(DW_ATTACHED, attached);
		}

		Entity nextNode;
		Entity prevNode;

		if(!this.level.isClientSide()) {
			nextNode = this.getNextNodeByUUID();
			prevNode = this.getPreviousNodeByUUID();

			if(nextNode != null && nextNode.getEntityId() != this.cachedNextNodeDW) {
				this.getDataManager().set(DW_NEXT_NODE, nextNode.getEntityId());
				this.cachedNextNodeDW = nextNode.getEntityId();
			} else if(nextNode == null && this.cachedNextNodeDW != -1) {
				this.getDataManager().set(DW_NEXT_NODE, -1);
				this.cachedNextNodeDW = -1;
			}

			if(prevNode != null && prevNode.getEntityId() != this.cachedPrevNodeDW) {
				this.getDataManager().set(DW_PREV_NODE, prevNode.getEntityId());
				this.cachedPrevNodeDW = prevNode.getEntityId();
			} else if(prevNode == null && this.cachedPrevNodeDW != -1) {
				this.getDataManager().set(DW_PREV_NODE, -1);
				this.cachedPrevNodeDW = -1;
			}
		} else {
			nextNode = this.getNextNodeClient();
			prevNode = this.getPreviousNodeClient();
		}

		if(!this.isMountNode() || prevNode == null) {
			this.setCurrentRopeLength((float) this.getDefaultRopeLength());
		}

		if(!this.level.isClientSide()) {
			if(nextNode != null) {

				if(nextNode instanceof EntityGrapplingHookNode && ((EntityGrapplingHookNode) nextNode).isMountNode()) {
					EntityGrapplingHookNode mountNode = ((EntityGrapplingHookNode) nextNode);

					if(mountNode.isExtending && nextNode.getY() < this.getY() && nextNode.getDistance(this.getX(), this.getY(), this.getZ()) > this.getDefaultRopeLength() - 0.2D) {
						if(mountNode.nodeCount < mountNode.maxNodeCount) {
							Vector3d connection = this.getConnectionToNext();
							if(connection != null) {
								Vector3d newPos = mountNode.getPositionVector().add(connection.scale(-0.5D)).add(0, 0.1D, 0);

								RayTraceResult result = this.world.rayTraceBlocks(mountNode.getPositionVector().add(0, mountNode.height, 0), newPos, false);

								if(result != null && result.typeOfHit == Type.BLOCK && result.hitVec.squareDistanceTo(mountNode.getPositionVector().add(0, mountNode.height, 0)) < newPos.squareDistanceTo(mountNode.getPositionVector().add(0, mountNode.height, 0))) {
									newPos = result.hitVec.add(result.hitVec.subtract(this.getPositionVector().add(0, this.height, 0)).normalize().scale(0.1D));
								}

								EntityGrapplingHookNode newNode = this.extendRope(mountNode, newPos.x, newPos.y, newPos.z);

								if(newNode != null) {
									newNode.setCurrentRopeLength((float) connection.length() / 4);
									this.setCurrentRopeLength((float) connection.length() / 4);

									if(mountNode.getCurrentRopeLength() < this.getDefaultRopeLength() - 0.05F) {
										//TODO This should only happen when reeling in
										mountNode.setCurrentRopeLength(0.05F);
									}
								}
							}
						} else {
							Entity controller = mountNode.getControllingPassenger();
							if(controller instanceof PlayerEntity) {
								((PlayerEntity) controller).sendStatusMessage(new TranslationTextComponent("chat.grappling_hook.max_length"), true);
							}
						}
					}
				}

				if(nextNode.getDistance(this.getX(), this.getY() + this.height - nextNode.height, this.getZ()) > this.getMaxRopeLength()) {
					EntityGrapplingHookNode mountNode = this.getMountNode();
					if(mountNode != null) {
						Entity controller = mountNode.getControllingPassenger();

						if(controller instanceof PlayerEntity) {
							((PlayerEntity) controller).sendStatusMessage(new TranslationTextComponent("chat.grappling_hook.disconnected"), true);
						}

						if(controller instanceof LivingEntity) {
							Iterator<ItemStack> it = ((LivingEntity) controller).getHeldEquipment().iterator();
							while(it.hasNext()) {
								ItemStack stack = it.next();
								if(!stack.isEmpty() && stack.getItem() instanceof ItemGrapplingHook) {
									((ItemGrapplingHook) stack.getItem()).onGrapplingHookRipped(stack, controller);
								}
							}
						}
					}

					this.setNextNode(null);
				}
			}
		}

		float friction = 1.0F;

		if(this.onGround || this.collidedHorizontally || this.collidedVertically) {
			friction = 0.5F;
		}

		this.motionY *= 0.98D * friction;
		this.motionX *= 0.98D * friction;
		this.motionZ *= 0.98D * friction;

		this.correctionX *= 0.5D * friction;
		this.correctionY *= 0.5D * friction;
		this.correctionZ *= 0.5D * friction;

		if(!attached) {
			this.handleWaterMovement();
			this.move(MoverType.SELF, this.motionX + this.correctionX, this.motionY + this.correctionY, this.motionZ + this.correctionZ);
			this.pushOutOfBlocks(this.getX(), this.getY(), this.getZ());

			//Check if it is now attached after move and should play sound
			if(this.isAttached()) {
				this.playSound(SoundRegistry.ROPE_GRAB, 0.6F, 0.8F + this.world.rand.nextFloat() * 0.3F);
			}
		}

		boolean isMovable = this.isMovable();

		if(isMovable && !this.climbing) {
			this.motionY -= 0.08D;
		}

		if(prevNode instanceof EntityGrapplingHookNode) {
			boolean isPullable = this.isPullable();

			if(isPullable && isMovable) {
				if(nextNode instanceof EntityGrapplingHookNode) {
					this.constrainMotion(prevNode, nextNode, 0.99D, 0.0D, 1.0D);
				}

				this.constrainMotion(prevNode, prevNode, 0.99D, -Double.MAX_VALUE, 0.1D);
			}

			Vector3d diff = prevNode.getPositionVector().add(0, prevNode.height, 0).subtract(this.getPositionVector().add(0, this.height, 0));

			if(diff.length() > this.getCurrentRopeLength()) {
				double correction = diff.length() - this.getCurrentRopeLength();

				Vector3d forceVec = diff.normalize().scale(correction * 0.5D);

				EntityGrapplingHookNode other = (EntityGrapplingHookNode) prevNode;

				boolean isThisCorrectable = isPullable && isMovable;
				boolean isOtherCorrectable = other.isPullable() && other.isMovable();

				float factor = !isThisCorrectable || !isOtherCorrectable ? 2.0f : 1.0f;

				if(isThisCorrectable) {
					this.correctionX += forceVec.x * factor;
					this.correctionY += forceVec.y * factor;
					this.correctionZ += forceVec.z * factor;
				}

				if(isOtherCorrectable) {
					other.correctionX += -forceVec.x * factor;
					other.correctionY += -forceVec.y * factor;
					other.correctionZ += -forceVec.z * factor;
				}
			}
		}

		this.velocityChanged = true;

		if(!this.isMovable()) {
			this.motionX = 0.0D;
			this.motionY = 0.0D;
			this.motionZ = 0.0D;
		}

		this.checkForEntityCollisions(prevNode, nextNode);

		if(this.level.isClientSide() && this.isMountNode()) {
			this.updateWeight();
		}

		this.climbing = false;

		Entity controller = this.getControllingPassenger();

		if(controller instanceof LivingEntity) {
			this.handleControllerMovement((LivingEntity) controller);
		}

		boolean hasValidUser = false;

		if(controller != null && this.isMountNode()) {
			Iterator<ItemStack> it = controller.getHeldEquipment().iterator();
			while(it.hasNext()) {
				ItemStack stack = it.next();
				if(!stack.isEmpty() && stack.getItem() instanceof ItemGrapplingHook && ((ItemGrapplingHook) stack.getItem()).canRideGrapplingHook(stack, controller)) {
					hasValidUser = true;
					break;
				}
			}
		}

		if(!this.level.isClientSide() && (nextNode == null || (this.isMountNode() && (!hasValidUser || prevNode == null)))) {
			this.onKillCommand();
		}

		this.firstUpdate = false;
	}

	protected void updateWeight() {
		final double weightRopeLength = 2D;

		Vector3d tether = this.getPositionVector().add(0, this.height, 0);

		if(this.weightPos == null) {
			this.prevWeightPos = this.weightPos = tether.add(0, -weightRopeLength, 0);
		}

		this.prevWeightPos = this.weightPos;

		this.weightPos = this.weightPos.add(0, -0.5D, 0);

		if(this.weightPos.distanceTo(tether) > weightRopeLength) {
			this.weightPos = tether.add(this.weightPos.subtract(tether).normalize().scale(weightRopeLength));
		}
	}

	protected void constrainMotion(Entity parentNode, Entity constraintNode, double ropeFriction, double constraintMin, double constraintDampening) {
		Vector3d nextPoint = new Vector3d(this.getX() + this.motionX - parentNode.motionX, this.getY() + this.height + this.motionY - parentNode.motionY, this.getZ() + this.motionZ - parentNode.motionZ);

		Vector3d tetherPoint = new Vector3d(constraintNode.getX(), constraintNode.getY() + constraintNode.height, constraintNode.getZ());
		float currentRopeLength = this.getCurrentRopeLength();

		if(tetherPoint.distanceTo(nextPoint) >= currentRopeLength) {
			Vector3d constrainedPoint = nextPoint.subtract(tetherPoint).normalize();

			constrainedPoint = constrainedPoint.scale(currentRopeLength).add(tetherPoint.x, tetherPoint.y, tetherPoint.z);

			Vector3d fwd = tetherPoint.subtract(constrainedPoint).normalize();

			Vector3d relMotion = new Vector3d(this.motionX - parentNode.motionX, this.motionY - parentNode.motionY, this.motionZ - parentNode.motionZ);

			Vector3d side = fwd.cross(new Vector3d(0, 1, 0)).normalize();
			Vector3d up = side.cross(fwd).normalize();

			Vector3d newMotion = side.scale(side.dotProduct(relMotion) * 1F).add(up.scale(up.dotProduct(relMotion) * 1F)).add(fwd.scale(Math.max(constraintMin, fwd.dotProduct(relMotion) * constraintDampening)));

			this.motionX = (parentNode.motionX + newMotion.x) * ropeFriction;
			this.motionY = (parentNode.motionY + newMotion.y) * ropeFriction;
			this.motionZ = (parentNode.motionZ + newMotion.z) * ropeFriction;
		}
	}

	protected boolean isMovable() {
		return !this.isAttached() && !this.onGround && !this.inWater;
	}

	protected boolean isPullable() {
		return !this.isMountNode() || this.getCurrentRopeLength() < this.getDefaultRopeLength() - 0.05D;
	}

	@Override
	public void updatePassenger(Entity passenger) {
		super.updatePassenger(passenger);
		
		PlayerUtil.resetFloating(passenger);
	}
	
	protected void handleControllerMovement(LivingEntity controller) {
		this.isExtending = false;

		controller.fallDistance = 0;

		if(!this.level.isClientSide()) {
			if(controller.isJumping) {
				if(controller.zza > 0) {
					boolean canReelIn = false;

					//Only let player reel in once at least one node has attached.
					//To prevent the grappling hook from being abused for flight.
					Entity checkRopeNode = this.getPreviousNode();
					while(checkRopeNode instanceof EntityGrapplingHookNode) {
						if(((EntityGrapplingHookNode) checkRopeNode).isAttached()) {
							canReelIn = true;
							break;
						}

						checkRopeNode = ((EntityGrapplingHookNode) checkRopeNode).getPreviousNode();
					}

					if(canReelIn) {
						Entity prevNode = this.getPreviousNode();

						if(prevNode instanceof EntityGrapplingHookNode) {
							Vector3d dir = prevNode.getPositionVector().subtract(this.getPositionVector()).normalize();

							float prevStepHeight = this.stepHeight;
							this.stepHeight = 1.25f;

							//On ground required for step to work
							this.onGround = true;
							this.move(MoverType.SELF, dir.x * 0.25D, dir.y * 0.25D, dir.z * 0.52D);

							if(this.collidedHorizontally && dir.y > 0) {
								this.onGround = true;
								this.move(MoverType.SELF, 0, 0.2D, 0);
								this.climbing = true;
							}

							this.stepHeight = prevStepHeight;

							if(prevNode.getBoundingBox().intersects(this.getBoundingBox())) {
								((EntityGrapplingHookNode) prevNode).removeNode(this);
								this.setCurrentRopeLength((float) this.getDefaultRopeLength() - 0.1F);
							} else {
								this.setCurrentRopeLength(Math.min((float) this.getDefaultRopeLength() - 0.1F, (float) prevNode.getDistance(this.getX(), this.getY() + this.height - prevNode.height, this.getZ())));
							}

							if(this.pullCounter % 24 == 0) {
								this.world.playSound(null, controller.getX(), controller.getY(), controller.getZ(), SoundRegistry.ROPE_PULL, SoundCategory.PLAYERS, 1.5F, 1);
							}

							this.pullCounter++;
						}
					}
				} else if(controller.zza < 0) {
					this.pullCounter = 0;

					this.setCurrentRopeLength(Math.min((float) this.getDefaultRopeLength() - 0.1F, this.getCurrentRopeLength() + 0.2F));
					this.isExtending = true;
				}
			} else {
				this.pullCounter = 0;

				if((Math.abs(controller.zza) > 0.05D || Math.abs(controller.xxa) > 0.05D) && !this.onGround) {
					int count = 0;

					double swingX = 0;
					double swingZ = 0;

					if(controller.zza > 0) {
						swingX += Math.cos(Math.toRadians(controller.yRot + 90));
						swingZ += Math.sin(Math.toRadians(controller.yRot + 90));
						count++;
					}
					if(controller.zza < 0) {
						swingX += Math.cos(Math.toRadians(controller.yRot - 90));
						swingZ += Math.sin(Math.toRadians(controller.yRot - 90));
						count++;
					}
					if(controller.xxa > 0) {
						swingX += Math.cos(Math.toRadians(controller.yRot));
						swingZ += Math.sin(Math.toRadians(controller.yRot));
						count++;
					} 
					if(controller.xxa < 0){
						swingX += Math.cos(Math.toRadians(controller.yRot + 180));
						swingZ += Math.sin(Math.toRadians(controller.yRot + 180));
						count++;
					}

					swingX /= count;
					swingZ /= count;

					double swingStrength = 0.05D;

					this.motionX += swingX * swingStrength;
					this.motionZ += swingZ * swingStrength;

					int incr = 0;
					Entity prev = this.getPreviousNode();
					while(prev instanceof EntityGrapplingHookNode && !((EntityGrapplingHookNode) prev).isAttached()) {
						if(!prev.onGround) {
							prev.motionX += swingX * swingStrength / (1 + incr * 2);
							prev.motionZ += swingZ * swingStrength / (1 + incr * 2);
						}

						prev = ((EntityGrapplingHookNode) prev).getPreviousNode();

						incr++;
					}
				}
			}
		}
	}

	protected void checkForEntityCollisions(Entity prevNode, Entity nextNode) {
		if(!this.level.isClientSide() && prevNode != null) {
			double velocity = Math.sqrt((this.getX()-this.xOld)*(this.getX()-this.xOld) + (this.getY()-this.yOld)*(this.getY()-this.yOld) + (this.getZ()-this.zOld)*(this.getZ()-this.zOld));

			Entity mountNode = this.getMountNode();
			if(mountNode != null) {
				Entity user = mountNode.getControllingPassenger();

				if(user != null && velocity > 0.25D) {
					List<LivingEntity> entities = this.world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(this.getX(), this.getY(), this.getZ(), prevNode.getX(), prevNode.getY(), prevNode.getZ()));

					for(LivingEntity entity : entities) {
						if(entity != user) {
							RayTraceResult intersect = entity.getBoundingBox().calculateIntercept(new Vector3d(this.getX(), this.getY(), this.getZ()), new Vector3d(prevNode.getX(), prevNode.getY(), prevNode.getZ()));

							if(intersect != null) {
								DamageSource source;

								if(user instanceof PlayerEntity) {
									source = new EntityDamageSourceIndirect("player", this, user);
								} else {
									source = new EntityDamageSourceIndirect("mob", this, user);
								}

								entity.attackEntityFrom(source, 3.0F + (float) Math.min((velocity - 0.25D) * 1.5D, 4));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isMountNode();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 4096.0D;
	}

	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
	}

	@Override
	public boolean canPassengerSteer() {
		return false;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);

		passenger.motionX = this.motionX * 1.5D;
		passenger.motionY = this.motionY;
		passenger.motionZ = this.motionZ * 1.5D;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		//No fall damage to node or rider
	}

	public boolean isAttached() {
		if(this.level.isClientSide()) {
			return this.getDataManager().get(DW_ATTACHED);
		}
		return !this.isMountNode() /*&& this.getPreviousNode() == null*/ && !this.world.getCollisionBoxes(this, this.getBoundingBox().grow(0.1D, 0.1D, 0.1D)).isEmpty();
	}

	public EntityGrapplingHookNode extendRope(Entity entity, double x, double y, double z) {
		EntityGrapplingHookNode mountNode = this.getMountNode();

		if(mountNode != null && mountNode.nodeCount < mountNode.maxNodeCount) {
			EntityGrapplingHookNode ropeNode = new EntityGrapplingHookNode(this.world);
			ropeNode.moveTo(x, y, z, 0, 0);

			ropeNode.setPreviousNode(this);
			this.setNextNode(ropeNode);

			ropeNode.setNextNode(entity);

			if(entity instanceof EntityGrapplingHookNode) {
				((EntityGrapplingHookNode) entity).setPreviousNode(ropeNode);
			}

			this.world.spawnEntity(ropeNode);

			mountNode.nodeCount++;

			return ropeNode;
		}

		return null;
	}

	public void removeNode(Entity nextConnectionNode) {
		Entity prevNode = this.getPreviousNodeByUUID();

		if(prevNode != null && prevNode instanceof EntityGrapplingHookNode) {
			((EntityGrapplingHookNode)prevNode).setNextNode(nextConnectionNode);

			if(nextConnectionNode instanceof EntityGrapplingHookNode) {
				((EntityGrapplingHookNode) nextConnectionNode).setPreviousNode(prevNode);
			}
		}

		EntityGrapplingHookNode mountNode = this.getMountNode();
		if(mountNode != null) {
			mountNode.nodeCount = Math.max(0, mountNode.nodeCount - 1);
		}

		this.onKillCommand();
	}

	public Vector3d getConnectionToNext() {
		Entity nextNode;
		if(this.level.isClientSide()) {
			nextNode = this.getNextNodeClient();
		} else {
			nextNode = this.getNextNodeByUUID();
		}
		if(nextNode != null) {
			return new Vector3d(nextNode.getX() - this.getX(), nextNode.getY() + nextNode.height - (this.getY() + this.height), nextNode.getZ() - this.getZ());
		}
		return null;
	}

	public void setNextNodeUUID(UUID uuid) {
		this.nextNodeUUID = uuid;
		if(this.cachedNextNodeEntity != null && !this.cachedNextNodeEntity.getUUID().equals(uuid)) {
			this.cachedNextNodeEntity = null;
		}
	}

	public UUID getNextNodeUUID() {
		return this.nextNodeUUID;
	}

	public void setNextNode(Entity entity) {
		this.cachedNextNodeEntity = entity;
		this.setNextNodeUUID(entity == null ? null : entity.getUUID());
	}

	public Entity getNextNodeByUUID() {
		if(this.cachedNextNodeEntity != null && this.cachedNextNodeEntity.isEntityAlive() && this.cachedNextNodeEntity.getUUID().equals(this.nextNodeUUID)) {
			return this.cachedNextNodeEntity;
		} else {
			UUID uuid = this.nextNodeUUID;
			Entity entity = uuid == null ? null : this.getEntityByUUID(uuid);
			this.cachedNextNodeEntity = entity;
			return entity;
		}
	}

	public void setPreviousNodeUUID(UUID uuid) {
		this.prevNodeUUID = uuid;
		if(this.cachedPrevNodeEntity != null && !this.cachedPrevNodeEntity.getUUID().equals(uuid)) {
			this.cachedPrevNodeEntity = null;
		}
	}

	public UUID getPreviousNodeUUID() {
		return this.prevNodeUUID;
	}

	public void setPreviousNode(Entity entity) {
		this.cachedPrevNodeEntity = entity;
		this.setPreviousNodeUUID(entity == null ? null : entity.getUUID());
	}

	public Entity getPreviousNodeByUUID() {
		if(this.cachedPrevNodeEntity != null && this.cachedPrevNodeEntity.isEntityAlive() && this.cachedPrevNodeEntity.getUUID().equals(this.prevNodeUUID)) {
			return this.cachedPrevNodeEntity;
		} else {
			UUID uuid = this.prevNodeUUID;
			Entity entity = uuid == null ? null : this.getEntityByUUID(uuid);
			this.cachedPrevNodeEntity = entity;
			return entity;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public Entity getNextNodeClient() {
		if(this.cachedNextNodeEntity == null || !this.cachedNextNodeEntity.isEntityAlive() || this.cachedNextNodeEntity.getEntityId() != this.getDataManager().get(DW_NEXT_NODE)) {
			Entity entity = this.world.getEntityByID(this.getDataManager().get(DW_NEXT_NODE));
			this.cachedNextNodeEntity = entity;
			return entity;
		}
		return this.cachedNextNodeEntity;
	}

	@OnlyIn(Dist.CLIENT)
	public Entity getPreviousNodeClient() {
		if(this.cachedPrevNodeEntity == null || !this.cachedPrevNodeEntity.isEntityAlive() || this.cachedPrevNodeEntity.getEntityId() != this.getDataManager().get(DW_PREV_NODE)) {
			Entity entity = this.world.getEntityByID(this.getDataManager().get(DW_PREV_NODE));
			this.cachedPrevNodeEntity = entity;
			return entity;
		}
		return this.cachedPrevNodeEntity;
	}

	public Entity getNextNode() {
		if(this.level.isClientSide()) {
			return this.getNextNodeClient();
		} else {
			return this.getNextNodeByUUID();
		}
	}

	public Entity getPreviousNode() {
		if(this.level.isClientSide()) {
			return this.getPreviousNodeClient();
		} else {
			return this.getPreviousNodeByUUID();
		}
	}

	private Entity getEntityByUUID(UUID uuid) {
		for(Entity entity : (List<Entity>) this.world.getEntitiesOfClass(Entity.class, this.getBoundingBox().grow(24, 24, 24))) {
			if (uuid.equals(entity.getUUID())) {
				return entity;
			}
		}
		return null;
	}

	public boolean isMountNode() {
		return this.getNextNode() instanceof LivingEntity;
	}

	public float getCurrentRopeLength() {
		return this.dataManager.get(DW_CURRENT_ROPE_LENGTH);
	}

	public void setCurrentRopeLength(float length) {
		this.dataManager.set(DW_CURRENT_ROPE_LENGTH, length);
	}

	protected float getDefaultRopeLength() {
		return 2.0F;
	}

	protected float getMaxRopeLength() {
		return 12.0F;
	}

	//TODO Cache this somehow?
	public EntityGrapplingHookNode getMountNode() {
		Entity node = this;
		while(node instanceof EntityGrapplingHookNode) {
			EntityGrapplingHookNode hookNode = (EntityGrapplingHookNode) node;

			if(hookNode.isMountNode()) {
				return hookNode;
			}

			node = hookNode.getNextNode();
		}

		return null;
	}

	public Vector3d getWeightPos(float partialTicks) {
		if(this.weightPos == null) {
			return new Vector3d(this.xOld + (this.getX() - this.xOld) * partialTicks, this.yOld + (this.getY() - this.yOld) * partialTicks, this.zOld + (this.getZ() - this.zOld) * partialTicks);
		} else {
			return this.prevWeightPos.add(this.weightPos.subtract(this.prevWeightPos).scale(partialTicks));
		}
	}

	@Override
	public void writeSpawnData(PacketBuffer buf) {
		buf.writeInt(this.maxNodeCount);
	}

	@Override
	public void readSpawnData(PacketBuffer buf) {
		this.maxNodeCount = buf.readInt();
	}
}
