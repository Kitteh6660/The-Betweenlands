package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import io.netty.buffer.PacketBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.api.capability.IPuppeteerCapability;
import thebetweenlands.api.capability.ProtectionShield;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntityFortressBossBlockade extends EntityMob implements IEntityBL, IEntityAdditionalSpawnData {
	protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.<Optional<UUID>>createKey(EntityFortressBossBlockade.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<Float> SIZE = EntityDataManager.<Float>createKey(EntityFortressBossBlockade.class, DataSerializers.FLOAT);
	protected static final DataParameter<Float> ROTATION = EntityDataManager.<Float>createKey(EntityFortressBossBlockade.class, DataSerializers.FLOAT);

	private Entity cachedOwner;

	private float prevRotation = 0.0F;
	private float rotation = 0.0F;
	private int despawnTicks = 0;
	private int maxDespawnTicks = 160;

	public EntityFortressBossBlockade(World world) {
		super(world);
		this.setSize(1.0F, 0.2F);
		this.prevRotation = this.rotation = world.rand.nextFloat() * 360.0f;
	}

	public EntityFortressBossBlockade(World world, Entity source) {
		super(world);
		this.setSize(1.0F, 0.2F);
		this.setOwner(source);
		this.prevRotation = this.rotation = world.rand.nextFloat() * 360.0f;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getDataManager().register(OWNER, Optional.absent());
		this.getDataManager().register(SIZE, 1.0F);
		this.getDataManager().register(ROTATION, this.rotation);
	}

	public void setOwner(@Nullable Entity entity) {
		this.getDataManager().set(OWNER, entity == null ? Optional.absent() : Optional.of(entity.getUUID()));
	}

	@Nullable
	public UUID getOwnerUUID() {
		Optional<UUID> uuid = this.getDataManager().get(OWNER);
		return uuid.isPresent() ? uuid.get() : null;
	}

	@Nullable
	public Entity getOwner() {
		UUID uuid = this.getOwnerUUID();
		if(uuid == null) {
			this.cachedOwner = null;
		} else if(this.cachedOwner == null || !this.cachedOwner.isEntityAlive() || !this.cachedOwner.getUUID().equals(uuid)) {
			this.cachedOwner = null;
			for(Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().grow(64.0D, 64.0D, 64.0D))) {
				if(entity.getUUID().equals(uuid)) {
					this.cachedOwner = entity;
					break;
				}
			}
		}
		return this.cachedOwner;
	}

	public void setTriangleSize(float size) {
		this.getDataManager().set(SIZE, size);
		if(this.level.isClientSide()) {
			double prevX = this.getX();
			double prevZ = this.getZ();
			this.setSize(size*2, this.height);
			this.setPosition(prevX, this.getY(), prevZ);
		}
	}

	public float getTriangleSize() {
		return this.getDataManager().get(SIZE);
	}

	public void setMaxDespawnTicks(int ticks) {
		this.maxDespawnTicks = ticks;
	}

	public int getMaxDespawnTicks() {
		return this.maxDespawnTicks;
	}

	public int getDespawnTicks() {
		return this.despawnTicks;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putFloat("triangleSize", this.getTriangleSize());
		nbt.putFloat("triangleRotation", this.getDataManager().get(ROTATION));
		nbt.putInt("despawnTicks", this.despawnTicks);
		nbt.putInt("maxDespawnTicks", this.maxDespawnTicks);
		if(this.getOwnerUUID() != null) {
			nbt.putUUID("owner", this.getOwnerUUID());
		}
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		this.setTriangleSize(nbt.getFloat("triangleSize"));
		this.getDataManager().set(ROTATION, nbt.getFloat("triangleRotation"));
		this.despawnTicks = nbt.getInt("despawnTicks");
		this.maxDespawnTicks = nbt.getInt("maxDespawnTicks");
		if(nbt.hasUUID("owner")) {
			this.getDataManager().set(OWNER, Optional.of(nbt.getUUID("owner")));
		} else {
			this.getDataManager().set(OWNER, Optional.absent());
		}
	}
	
	protected boolean isPlayerControlled() {
		return this.getOwner() instanceof PlayerEntity;
	}

	@Override
	public void tick() {
		if(!this.level.isClientSide() && (this.world.getDifficulty() == EnumDifficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isEntityAlive()))) {
			this.remove();
			return;
		}

		this.setTriangleSize(this.getTriangleSize());

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;

		super.tick();

		if(!this.level.isClientSide()) {
			this.despawnTicks++;
			if(this.despawnTicks >= this.getMaxDespawnTicks()) {
				this.remove();
			}

			this.rotation += 1.0F;
			this.getDataManager().set(ROTATION, this.rotation);

			List<LivingEntity> targets = this.world.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().grow(this.getTriangleSize()*2, 0, this.getTriangleSize()*2));
			for(LivingEntity target : targets) {
				if(target != this.getOwner()) {
					Vector3d[] vertices = this.getTriangleVertices(1);
					
					if(EntityFortressBoss.rayTraceTriangle(new Vector3d(target.getX() - this.getX(), 1, target.getZ() - this.getZ()), new Vector3d(0, -2, 0), vertices[0], vertices[1], vertices[2])) {
						
						if(this.isPlayerControlled()) {
							IPuppetCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
							
							Entity owner = this.getOwner();
							
							if(cap != null && cap.getPuppeteer() == owner && owner instanceof PlayerEntity) {
								PlayerEntity player = (PlayerEntity) owner;
								
								IPuppeteerCapability playerCap = player.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
								if(playerCap != null) {
									ProtectionShield shield = playerCap.getShield();
									
									if(shield != null) {
										float healthPercent = 0.3f;
										
										if(target.getHealth() * (1 - healthPercent) > 5.0f) {
											float healthCost = target.getHealth() * healthPercent;
											float prevHealth = target.getHealth();
											
											if(target.attackEntityFrom(DamageSource.MAGIC, healthCost) && (prevHealth - target.getHealth()) >= healthCost * 0.5f) {
												List<Integer> indices = new ArrayList<>();
												for(int i = 0; i < 20; i++) {
													indices.add(i);
												}
												Collections.shuffle(indices, this.rand);
												
												for(int index : indices) {
													if(!shield.isActive(index)) {
														shield.setActive(index, true);
														break;
													}
												}
												
												this.maxDespawnTicks = this.despawnTicks + 8;
											}
										}
									}
								}
							}
						} else if(target instanceof PlayerEntity)  {
							float damage = (float) this.getEntityAttribute(Attributes.ATTACK_DAMAGE).getAttributeValue();
							if(target.attackEntityFrom(DamageSource.MAGIC, damage) && this.getOwner() != null && this.getOwner() instanceof LivingEntity) {
								LivingEntity owner = (LivingEntity) this.getOwner();
								if(owner.getHealth() < owner.getMaxHealth() - damage) {
									owner.heal(damage * 3.0F);
								}
							}
						}
						
					}
				}
			}
		} else {
			this.prevRotation = this.rotation;
			this.rotation = this.getDataManager().get(ROTATION);

			for(int c = 0; c < 4; c++) {
				float r1 = this.world.rand.nextFloat();
				float r2 = this.world.rand.nextFloat();
				this.rotation += 15;
				Vector3d[] vertices = this.getTriangleVertices(1);
				this.rotation -= 15;
				double xc = 0, zc = 0;
				for(int i = 0; i < 3; i++) {
					Vector3d vertex = vertices[i];
					switch(i) {
					default:
					case 0:
						xc += vertex.x * (1 - Math.sqrt(r1));
						zc += vertex.z * (1 - Math.sqrt(r1));
						break;
					case 1:
						xc += (Math.sqrt(r1) * (1 - r2)) * vertex.x;
						zc += (Math.sqrt(r1) * (1 - r2)) * vertex.z;
						break;
					case 2:
						xc += (Math.sqrt(r1) * r2) * vertex.x;
						zc += (Math.sqrt(r1) * r2) * vertex.z;
						break;
					}
				}
				Vector3d rp = new Vector3d(xc, vertices[0].y, zc);

				double sx = this.getX() + rp.x;
				double sy = this.getY() + rp.y + 4;
				double sz = this.getZ() + rp.z;
				double ex = this.getX() + rp.x;
				double ey = this.getY() + rp.y;
				double ez = this.getZ() + rp.z;

				if(this.getOwner() != null) {
					sx = this.getOwner().getX();
					sy = this.getOwner().getBoundingBox().minY + (this.getOwner().getBoundingBox().maxY - this.getOwner().getBoundingBox().minY) / 2.0D;
					sz = this.getOwner().getZ();
				}

				if(!this.isPlayerControlled()) {
					this.world.spawnParticle(EnumParticleTypes.PORTAL, sx, sy, sz, ex - sx, ey - sy, ez - sz);
				}
			}
		}
	}

	@Override
	public void travel(float strafe, float up, float forward) {
		if (this.isInWater()) {
			this.moveRelative(strafe, up, forward, 0.02F);
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.800000011920929D;
			this.motionY *= 0.800000011920929D;
			this.motionZ *= 0.800000011920929D;
		} else {
			float friction = 0.91F;

			if (this.onGround) {
				friction = this.world.getBlockState(new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.getZ()))).getBlock().slipperiness * 0.91F;
			}

			float groundFriction = 0.16277136F / (friction * friction * friction);
			this.moveRelative(strafe, up, forward, this.onGround ? 0.1F * groundFriction : 0.02F);
			friction = 0.91F;

			if (this.onGround) {
				friction = this.world.getBlockState(new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.getZ()))).getBlock().slipperiness * 0.91F;
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)friction;
			this.motionY *= (double)friction;
			this.motionZ *= (double)friction;
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double dx = this.getX() - this.xOld;
		double dz = this.getZ() - this.zOld;
		float distanceMoved = MathHelper.sqrt(dx * dx + dz * dz) * 4.0F;

		if (distanceMoved > 1.0F) {
			distanceMoved = 1.0F;
		}

		this.limbSwingAmount += (distanceMoved - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	public Vector3d[] getTriangleVertices(float partialTicks) {
		Vector3d[] vertices = new Vector3d[3];
		double rot = Math.toRadians(this.prevRotation + (this.rotation - this.prevRotation) * partialTicks);
		double angle = Math.PI * 2.0D / 3.0D;
		for(int i = 0; i < 3; i++) {
			double sin = Math.sin(angle * i + rot);
			double cos = Math.cos(angle * i + rot);
			vertices[i] = new Vector3d(sin * this.getTriangleSize(), 0, cos * this.getTriangleSize());
		}
		return vertices;
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		return false;
	}

	@Override
	protected void collideWithEntity(Entity entityIn) { }
	
	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
        return !DamageSource.OUT_OF_WORLD.getDamageType().equals(source.getDamageType()) && source.getImmediateSource() instanceof EntityShockwaveBlock == false;
    }

	@Override
    public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeFloat(this.rotation);
	}

	@Override
	public void readSpawnData(PacketBuffer buffer) {
		this.prevRotation = this.rotation = buffer.readFloat();
		this.dataManager.set(ROTATION, this.rotation);
	}
}
