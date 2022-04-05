package thebetweenlands.common.entity;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import net.minecraft.entity.monster.CaveSpiderEntity;

public class EntityRopeNode extends Entity {
	
	private static final DataParameter<Integer> DW_PREV_NODE = EntityDataManager.defineId(EntityRopeNode.class, DataSerializers.INT);
	private static final DataParameter<Integer> DW_NEXT_NODE = EntityDataManager.defineId(EntityRopeNode.class, DataSerializers.INT);

	public static final double ROPE_LENGTH = 4.0D;
	public static final double ROPE_LENGTH_MAX = 12.0D;

	private boolean canExtend = true;
	private boolean pickUp = false;
	private int despawnTimer = 0;

	private UUID nextNodeUUID;
	private UUID prevNodeUUID;

	private int cachedPrevNodeDW;
	private int cachedNextNodeDW;

	private Entity cachedNextNodeEntity;
	private Entity cachedPrevNodeEntity;

	private BlockPos lightBlock = null;
	
	public EntityRopeNode(World world) {
		super(world);
		this.setSize(0.1F, 0.1F);
	}

	@Override
	protected void defineSynchedData() {
		this.getEntityData().define(DW_PREV_NODE, -1);
		this.cachedPrevNodeDW = -1;
		this.getEntityData().define(DW_NEXT_NODE, -1);
		this.cachedNextNodeDW = -1;
	}

	@Override 
	public void load(CompoundNBT nbt) {
		this.setNextNodeUUID(nbt.hasUUID("nextNodeUUID") ? nbt.getUUID("nextNodeUUID") : null);
		this.setPreviousNodeUUID(nbt.hasUUID("previousNodeUUID") ? nbt.getUUID("previousNodeUUID") : null);
		this.pickUp = nbt.getBoolean("pickUp");
		this.canExtend = nbt.getBoolean("canExtend");
		this.despawnTimer = nbt.getInt("despawnTimer");
		if(nbt.contains("lightBlock", Constants.NBT.TAG_LONG)) {
			this.lightBlock = BlockPos.of(nbt.getLong("lightBlock"));
		} else {
			this.lightBlock = null;
		}
	}

	@Override
	public void save(CompoundNBT nbt) {
		if(this.getNextNodeUUID() != null) {
			nbt.putUUID("nextNodeUUID", this.getNextNodeUUID());
		}
		if(this.getPreviousNodeUUID() != null) {
			nbt.putUUID("previousNodeUUID", this.getPreviousNodeUUID());
		}
		nbt.putBoolean("pickUp", this.pickUp);
		nbt.putBoolean("canExtend", this.canExtend);
		nbt.putInt("despawnTimer", this.despawnTimer);
		if(this.lightBlock != null) {
			nbt.setLong("lightBlock", this.lightBlock.toLong());
		}
	}

	@Override
	public void onEntityUpdate() {
		this.xOld = this.getX();
		this.yOld = this.getY();
		this.zOld = this.getZ();

		boolean attached = this.isAttached();

		if(!attached) {
			this.handleWaterMovement();
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		}

		boolean prevAttached = attached;
		attached = this.isAttached();

		if(attached && !prevAttached) {
			this.world.playLocalSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_METAL_STEP, SoundCategory.PLAYERS, 1, 1.5F);
		}

		Entity nextNode;
		Entity prevNode;

		if(!this.level.isClientSide()) {
			nextNode = this.getNextNodeByUUID();
			prevNode = this.getPreviousNodeByUUID();

			if(nextNode != null && nextNode.getEntityId() != this.cachedNextNodeDW) {
				this.getEntityData().set(DW_NEXT_NODE, nextNode.getEntityId());
				this.cachedNextNodeDW = nextNode.getEntityId();
			} else if(nextNode == null && this.cachedNextNodeDW != -1) {
				this.getEntityData().set(DW_NEXT_NODE, -1);
				this.cachedNextNodeDW = -1;
			}

			if(prevNode != null && prevNode.getEntityId() != this.cachedPrevNodeDW) {
				this.getEntityData().set(DW_PREV_NODE, prevNode.getEntityId());
				this.cachedPrevNodeDW = prevNode.getEntityId();
			} else if(prevNode == null && this.cachedPrevNodeDW != -1) {
				this.getEntityData().set(DW_PREV_NODE, -1);
				this.cachedPrevNodeDW = -1;
			}
			
			if(this.isEntityAlive()) {
				if(attached) {
					BlockPos pos = this.getPosition();
					
					if(this.lightBlock != null && (this.lightBlock.getX() != pos.getX() || this.lightBlock.getY() != pos.getY() || this.lightBlock.getZ() != pos.getZ())) {
						if(this.world.isBlockLoaded(this.lightBlock) && this.world.getBlockState(this.lightBlock).getBlock() == BlockRegistry.CAVING_ROPE_LIGHT) {
							this.world.setBlockToAir(this.lightBlock);
						}
						
						this.lightBlock = null;
					}
					
					if(this.lightBlock == null) {
						if(this.world.isEmptyBlock(pos)) {
							this.world.setBlockState(pos, BlockRegistry.CAVING_ROPE_LIGHT.defaultBlockState());
							this.lightBlock = pos;
						}
					}
				} else if(this.lightBlock != null) {
					if(this.world.isBlockLoaded(this.lightBlock) && this.world.getBlockState(this.lightBlock).getBlock() == BlockRegistry.CAVING_ROPE_LIGHT) {
						this.world.setBlockToAir(this.lightBlock);
					}
					
					this.lightBlock = null;
				}
			}
		} else {
			nextNode = this.getNextNode();
			prevNode = this.getPreviousNode();
		}

		if(!this.level.isClientSide()) {
			if(nextNode instanceof PlayerEntity) {
				if(nextNode.getDistance(this) > 1.5D) {
					this.pickUp = true;
				}
				if(this.pickUp && nextNode.getBoundingBox().inflate(0.4D, 0.4D, 0.4D).intersects(this.getBoundingBox())) {
					this.removeNode(nextNode);
					PlayerEntity player = (PlayerEntity) nextNode;
					if(player.inventory.add(new ItemStack(ItemRegistry.CAVING_ROPE, 1))) {
						this.world.playLocalSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
					} else {
						ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), new ItemStack(ItemRegistry.CAVING_ROPE, 1));
						itemEntity.setPickUpDelay(0);
						this.world.addFreshEntity(itemEntity);
					}
				}
				if(nextNode.getDistance(this) < ROPE_LENGTH - 1) {
					this.canExtend = true;
				}
				if(this.canExtend && nextNode.getDistance(this) > ROPE_LENGTH + 1) {
					IInventory inventory = ((PlayerEntity)nextNode).inventory;
					int invSize = inventory.getContainerSize();
					for(int i = 0; i < invSize; ++i) {
						ItemStack stack = inventory.getItem(i);
						if(!stack.isEmpty() && stack.getItem() == ItemRegistry.CAVING_ROPE) {
							stack.shrink(1);
							inventory.setItem(i, stack.getCount() > 0 ? stack : ItemStack.EMPTY);
							Vector3d connection = this.getConnectionToNext();
							if(connection != null) {
								Vector3d newPos = nextNode.getDeltaMovement().add(connection.scale(-0.5D)).add(0, 0.1D, 0);
								RayTraceResult result = this.world.rayTraceBlocks(nextNode.getDeltaMovement(), newPos, false);
								if(result != null && result.typeOfHit == Type.BLOCK && result.hitVec.squareDistanceTo(nextNode.getDeltaMovement()) < newPos.squareDistanceTo(nextNode.getDeltaMovement())) {
									newPos = result.hitVec.add(result.hitVec.subtract(this.getDeltaMovement()).normalize().scale(0.1D));
								}
								EntityRopeNode rope = this.extendRope(nextNode, newPos.x, newPos.y, newPos.z);
								if(rope.isAttached()) {
									this.world.playLocalSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_METAL_STEP, SoundCategory.PLAYERS, 1, 1.5F);
								}
								break;
							}
						}
					}
				}
				if(nextNode.getDistance(this) > ROPE_LENGTH_MAX) {
					if(nextNode instanceof PlayerEntity) {
						((PlayerEntity) nextNode).displayClientMessage(new TranslationTextComponent("chat.rope.disconnected"), true);
					}
					this.setNextNode(null);
				}
			}
		}

		this.motionY *= 0.88D;
		this.motionX *= 0.88D;
		this.motionZ *= 0.88D;

		if(!attached && !this.onGround && !this.inWater) {
			boolean isFloating = false;

			if(nextNode != null && this.getDistance(nextNode) >= ROPE_LENGTH) {
				Vector3d connection = this.getConnectionToNext();
				if(connection != null) {
					double mx = connection.x * 0.02D;
					double my = connection.y * 0.02D;
					double mz = connection.z * 0.02D;
					double len = Math.sqrt(mx*mx + my*my + mz*mz);
					if(len > 0.5D) {
						mx /= len * 0.5D;
						my /= len * 0.5D;
						mz /= len * 0.5D;
					}
					if(prevNode != null && prevNode.getDistance(this.getX() + mx, this.getY() + my, this.getZ() + mz) < ROPE_LENGTH + 1) {
						this.motionX += mx;
						this.motionZ += mz;
						this.motionY += my;
						isFloating = true;
					}
				}
			}

			if(!isFloating) {
				this.motionY -= 0.28D;
			}

			if(nextNode != null) {
				double mx = this.motionX;
				double my = this.motionY;
				double mz = this.motionZ;
				Vector3d nextPoint = new Vector3d(this.getX() + mx, this.getY() + my, this.getZ() + mz);
				Vector3d tetherPoint = new Vector3d(nextNode.getX(), nextNode.getY(), nextNode.getZ());
				if(tetherPoint.distanceTo(nextPoint) >= ROPE_LENGTH) {
					Vector3d constrainedPoint = nextPoint.subtract(tetherPoint).normalize();
					constrainedPoint = new Vector3d(
							constrainedPoint.x * ROPE_LENGTH, 
							constrainedPoint.y * ROPE_LENGTH, 
							constrainedPoint.z * ROPE_LENGTH).add(tetherPoint.x, tetherPoint.y, tetherPoint.z);
					Vector3d diff = new Vector3d(this.getX(), this.getY(), this.getZ()).subtract(constrainedPoint);
					this.motionX = -diff.x;
					this.motionY = -diff.y;
					this.motionZ = -diff.z;
				}
			}

			if(prevNode != null) {
				double mx = this.motionX;
				double my = this.motionY;
				double mz = this.motionZ;
				Vector3d nextPoint = new Vector3d(this.getX() + mx, this.getY() + my, this.getZ() + mz);
				Vector3d tetherPoint = new Vector3d(prevNode.getX(), prevNode.getY(), prevNode.getZ());
				if(tetherPoint.distanceTo(nextPoint) >= ROPE_LENGTH) {
					Vector3d constrainedPoint = nextPoint.subtract(tetherPoint).normalize();
					constrainedPoint = new Vector3d(
							constrainedPoint.x * ROPE_LENGTH, 
							constrainedPoint.y * ROPE_LENGTH, 
							constrainedPoint.z * ROPE_LENGTH).add(tetherPoint.x, tetherPoint.y, tetherPoint.z);
					Vector3d diff = new Vector3d(this.getX(), this.getY(), this.getZ()).subtract(constrainedPoint).scale(0.8D);
					this.motionX = -diff.x;
					this.motionY = -diff.y;
					this.motionZ = -diff.z;
				}
			}

			double speed = Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
			this.motionX *= Math.min(speed, 0.05D) / 0.05D;
			this.motionY *= Math.min(speed, 0.05D) / 0.05D;
			this.motionZ *= Math.min(speed, 0.05D) / 0.05D;
		} else {
			this.motionX = 0.0D;
			this.motionY = 0.0D;
			this.motionZ = 0.0D;
		}

		if(!this.level.isClientSide()) {
			if(nextNode == null) {
				if(prevNode == null) {
					this.onKillCommand();
				} else {
					this.despawnTimer++;
					if(this.despawnTimer >= BetweenlandsConfig.GENERAL.cavingRopeDespawnTime * 20) {
						if(prevNode != null && prevNode instanceof EntityRopeNode) {
							EntityRopeNode prevRopeNode = (EntityRopeNode) prevNode;
							prevRopeNode.setNextNode(null);
							prevRopeNode.despawn(); 
						}
						this.onKillCommand();
					}
				}
			} else {
				this.despawnTimer = 0;
			}
		}
	}


	@Override
	public boolean processInitialInteract(PlayerEntity player,  Hand hand) {
		if(!this.level.isClientSide()) {
			Entity prevNode = this.getPreviousNodeByUUID();
			Entity nextNode = this.getNextNodeByUUID();

			if(prevNode != null) {
				if(nextNode == null) {
					EntityRopeNode connectedRopeNode = null;
					for(Entity e : (List<Entity>) player.world.loadedEntityList) {
						if(e instanceof EntityRopeNode) {
							EntityRopeNode ropeNode = (EntityRopeNode) e;
							if(ropeNode.getNextNodeByUUID() == player) {
								connectedRopeNode = ropeNode;
								break;
							}
						}
					}
					if(connectedRopeNode != null) {
						player.displayClientMessage(new TranslationTextComponent("chat.rope.already_connected"), true);
						return false;
					}

					this.setNextNode(player);

					return true;
				} else if(nextNode instanceof EntityRopeNode == false) {
					this.setNextNode(null);
					return true;
				}
			}

			if(nextNode instanceof EntityRopeNode) {
				EntityRopeNode endNode = (EntityRopeNode) nextNode;
				while(endNode.getNextNodeByUUID() instanceof EntityRopeNode && endNode.getNextNodeByUUID() != this) {
					endNode = (EntityRopeNode) endNode.getNextNodeByUUID();
				}
				if(endNode.getNextNodeByUUID() == null && endNode.getPreviousNodeByUUID() instanceof EntityRopeNode) {
					((EntityRopeNode) endNode.getPreviousNodeByUUID()).setNextNode(null);
					endNode.remove();

					if(player.inventory.add(new ItemStack(ItemRegistry.CAVING_ROPE, 1))) {
						this.world.playLocalSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
					} else {
						ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), new ItemStack(ItemRegistry.CAVING_ROPE, 1));
						itemEntity.setPickUpDelay(0);
						this.world.addFreshEntity(itemEntity);
					}

					return true;
				}
			}
		} else {
			return true;
		}

		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	
	@Override
	public void remove() {
		super.remove();
		
		if(this.lightBlock != null && this.world.isBlockLoaded(this.lightBlock) && this.world.getBlockState(this.lightBlock).getBlock() == BlockRegistry.CAVING_ROPE_LIGHT) {
			this.world.setBlockToAir(this.lightBlock);
		}
		
		this.lightBlock = null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 1024.0D;
	}

	public void removeNode(Entity nextConnectionNode) {
		Entity prevNode = this.getPreviousNodeByUUID();
		if(prevNode != null && prevNode instanceof EntityRopeNode) {
			((EntityRopeNode)prevNode).setNextNode(nextConnectionNode);
			((EntityRopeNode)prevNode).canExtend = false;
		}
		this.onKillCommand();
	}

	public void despawn() {
		this.despawnTimer = BetweenlandsConfig.GENERAL.cavingRopeDespawnTime * 20;
	}

	public boolean isAttached() {
		return !this.world.getBlockCollisions(this, this.getBoundingBox().inflate(0.1D, 0.1D, 0.1D)).isEmpty();
	}

	public EntityRopeNode extendRope(Entity entity, double x, double y, double z) {
		EntityRopeNode ropeNode = new EntityRopeNode(this.world);
		ropeNode.moveTo(x, y, z, 0, 0);
		ropeNode.setPreviousNode(this);
		ropeNode.setNextNode(entity);
		this.setNextNode(ropeNode);
		this.world.addFreshEntity(ropeNode);
		if (entity instanceof ServerPlayerEntity)
			AdvancementCriterionRegistry.CAVINGROPE_PLACED.trigger((ServerPlayerEntity) entity);
		return ropeNode;
	}

	public Vector3d getConnectionToNext() {
		Entity nextNode;
		if(this.level.isClientSide()) {
			nextNode = this.getNextNode();
		} else {
			nextNode = this.getNextNodeByUUID();
		}
		if(nextNode != null) {
			return new Vector3d(nextNode.getX() - this.getX(), nextNode.getY() - this.getY(), nextNode.getZ() - this.getZ());
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
	public Entity getNextNode() {
		if(this.cachedNextNodeEntity == null || !this.cachedNextNodeEntity.isEntityAlive() || this.cachedNextNodeEntity.getEntityId() != this.getEntityData().get(DW_NEXT_NODE)) {
			Entity entity = this.world.getEntityByID(this.getEntityData().get(DW_NEXT_NODE));
			this.cachedNextNodeEntity = entity;
			return entity;
		}
		return this.cachedNextNodeEntity;
	}

	@OnlyIn(Dist.CLIENT)
	public Entity getPreviousNode() {
		if(this.cachedPrevNodeEntity == null || !this.cachedPrevNodeEntity.isEntityAlive() || this.cachedPrevNodeEntity.getEntityId() != this.getEntityData().get(DW_PREV_NODE)) {
			Entity entity = this.world.getEntityByID(this.getEntityData().get(DW_PREV_NODE));
			this.cachedPrevNodeEntity = entity;
			return entity;
		}
		return this.cachedPrevNodeEntity;
	}

	private Entity getEntityByUUID(UUID uuid) {
		for(Entity entity : (List<Entity>) this.world.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(24, 24, 24))) {
			if (uuid.equals(entity.getUUID())) {
				return entity;
			}
		}
		return null;
	}
}
