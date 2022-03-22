package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.PacketBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.item.equipment.ItemRingOfSummoning;

public class EntityMummyArm extends EntityCreature implements IEntityBL, IEntityAdditionalSpawnData {
	private static final DataParameter<Integer> OWNER_ID = EntityDataManager.<Integer>createKey(EntityMummyArm.class, DataSerializers.VARINT);

	private Entity owner;
	private UUID ownerUUID;

	public int attackSwing = 0;

	private int spawnTicks = 0;

	private int despawnTicks = 0;

	private int deathTicks = 0;

	private double yOffset = 0.0D;

	public EntityMummyArm(World world) {
		super(world);
		this.setSize(0.7F, 0.7F);
	}

	public EntityMummyArm(World world, PlayerEntity player) {
		super(world);
		this.setSize(0.7F, 0.7F);
		this.setPlayerOwner(player);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getDataManager().register(OWNER_ID, -1);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
		this.yRot = this.world.rand.nextFloat() * 360.0F;
		return data;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getAttributeMap().registerAttribute(Attributes.ATTACK_DAMAGE);
		this.getEntityAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.0F);
		this.getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	public void setPlayerOwner(@Nullable PlayerEntity owner) {
		this.owner = owner;
		this.ownerUUID = owner == null ? null : owner.getUUID();
		this.getDataManager().set(OWNER_ID, owner == null ? -1 : owner.getEntityId());
	}

	public boolean hasPlayerOwner() {
		return this.ownerUUID != null;
	}

	@Nullable
	public Entity getPlayerOwner() {
		if(!this.level.isClientSide()) {
			if(this.owner != null && this.owner.getUUID().equals(this.ownerUUID)) {
				return this.owner;
			} else {
				this.owner = this.ownerUUID == null ? null : this.getEntityByUUID(this.ownerUUID);
				return this.owner;
			}
		} else {
			if(this.owner != null && this.owner.getEntityId() != this.getDataManager().get(OWNER_ID)) {
				return this.owner;
			} else {
				int id = this.getDataManager().get(OWNER_ID);
				this.owner = id < 0 ? null : this.world.getEntityByID(id);
				return this.owner;
			}
		}
	}

	private Entity getEntityByUUID(UUID uuid) {
		for (int i = 0; i < this.world.loadedEntityList.size(); ++i) {
			Entity entity = (Entity)this.world.loadedEntityList.get(i);
			if (uuid.equals(entity.getUUID())) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public void tick() {
		super.tick();

		BlockPos pos = this.getPosition().below(1);
		BlockState blockState = this.world.getBlockState(pos);

		if(!this.level.isClientSide()) {
			if(blockState.getBlock() == Blocks.AIR || (this.hasPlayerOwner() && !blockState.isSideSolid(this.world, pos, Direction.UP))) {
				this.remove();
			}

			Entity owner = this.getPlayerOwner();

			if(this.hasPlayerOwner() && (owner == null || owner.getDistance(this) > 32.0D)) {
				this.setHealth(0);
			} else if(owner instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) owner;
				if(!ItemRingOfSummoning.isRingActive(player)) {
					this.setHealth(0);
				}
			}

			if(this.despawnTicks >= 150) {
				this.setHealth(0);
			} else {
				if(this.spawnTicks >= 40) {
					this.despawnTicks++;
				}
			}
		}

		if(this.deathTicks > 0) {
			this.yOffset = -this.deathTicks / 40.0F;
		} else if(this.spawnTicks >= 40) {
			this.yOffset = 0.0F;
		}

		if(this.isEntityAlive()) {
			if(this.spawnTicks >= 4) {
				List<LivingEntity> targets = this.world.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
				for(LivingEntity target : targets) {

					boolean isValidTarget;
					if(this.hasPlayerOwner()) {
						isValidTarget = target != this && target != this.getPlayerOwner() && (target instanceof EntityMob || target instanceof IMob);
					} else {
						isValidTarget = target instanceof PlayerEntity;
					}
					if(isValidTarget) {
						target.setInWeb();

						if(target.hurtResistantTime < 10) {
							DamageSource damageSource;
							Entity owner = this.getPlayerOwner();
							if(owner != null) {
								damageSource = new EntityDamageSourceIndirect("mob", this, owner);
							} else {
								damageSource = DamageSource.causeMobDamage(this);
							}

							target.attackEntityFrom(damageSource, (float) this.getEntityAttribute(Attributes.ATTACK_DAMAGE).getAttributeValue());

							if(this.attackSwing <= 0) {
								this.attackSwing = 20;
							}
						}
					}
				}
			}

			if(this.spawnTicks < 40) {
				this.spawnTicks++;
				this.yOffset = -1 + this.spawnTicks / 40.0F;
			} else {
				this.yOffset = 0.0F;
			}

			if(this.attackSwing > 0) {
				this.attackSwing--;
			}
		}

		if(this.level.isClientSide() && this.random.nextInt(this.yOffset < 0.0F ? 2 : 8) == 0) {
			if(blockState.getBlock() != Blocks.AIR) {
				double px = this.getX();
				double py = this.getY();
				double pz = this.getZ();
				for (int i = 0, amount = 2 + this.random.nextInt(this.yOffset < 0.0F ? 8 : 3); i < amount; i++) {
					double ox = this.random.nextDouble() * 0.1F - 0.05F;
					double oz = this.random.nextDouble() * 0.1F - 0.05F;
					double motionX = this.random.nextDouble() * 0.2 - 0.1;
					double motionY = this.random.nextDouble() * 0.1 + 0.1;
					double motionZ = this.random.nextDouble() * 0.2 - 0.1;
					this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(blockState));
				}
			}
		}
	}

	@Override
	public void travel(float strafe, float up,  float forward) { }

	@Override
	public void applyEntityCollision(Entity entity) { }

	@Override
	protected void collideWithEntity(Entity entity) { }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public double getStepY() {
		return this.yOffset;
	}

	@Override
	protected void onDeathUpdate() {
		this.deathTicks++;

		if(!this.level.isClientSide() && this.deathTicks >= 40) {
			this.remove();
		}
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		if(this.ownerUUID != null) {
			nbt.putUUID("ownerUUID", this.ownerUUID);
		}
		nbt.putInt("spawnTicks", this.spawnTicks);
		nbt.putInt("despawnTicks", this.despawnTicks);
		nbt.putInt("deathTicks", this.deathTicks);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		if(nbt.hasUUID("ownerUUID")) {
			this.ownerUUID = nbt.getUUID("ownerUUID");
		}
		this.spawnTicks = nbt.getInt("spawnTicks");
		this.despawnTicks = nbt.getInt("despawnTicks");
		this.deathTicks = nbt.getInt("deathTicks");
	}

	@Override
	public void writeSpawnData(PacketBuffer buf) {
		PacketBuffer packet = new PacketBuffer(buf);
		packet.writeBoolean(this.ownerUUID != null);
		if(this.ownerUUID != null) {
			packet.writeUniqueId(this.ownerUUID);
		}
	}

	@Override
	public void readSpawnData(PacketBuffer buf) {
		PacketBuffer packet = new PacketBuffer(buf);
		if(packet.readBoolean()) {
			this.ownerUUID = packet.readUniqueId();
		} else {
			this.ownerUUID = null;
		}
	}
}
