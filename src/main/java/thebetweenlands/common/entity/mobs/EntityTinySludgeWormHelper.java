package thebetweenlands.common.entity.mobs;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.common.registries.LootTableRegistry;

public class EntityTinySludgeWormHelper extends EntityTinySludgeWorm implements IEntityOwnable {
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityTinySludgeWormHelper.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	public EntityTinySludgeWormHelper(World world) {
		super(world, false);
		experienceValue = 0;
	}

	@Override
	protected void registerGoals() {
		tasks.addGoal(0, new EntityAIAttackMelee(this, 1, false));
		tasks.addGoal(1, new EntityAIWander(this, 0.8D, 1));
		
		targetTasks.addGoal(0, new EntityAIHurtByTarget(this, false) {
			@Override
			protected void setEntityAttackTarget(EntityCreature creatureIn, LivingEntity target) {
				if(target != EntityTinySludgeWormHelper.this.getOwner()) {
					super.setEntityAttackTarget(creatureIn, target);
				}
			}
		});
		targetTasks.addGoal(1, new EntityAINearestAttackableTarget<>(this, LivingEntity.class, 2, true, true, entity -> entity instanceof IMob));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(OWNER_UNIQUE_ID, Optional.absent());
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0D);
		getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.0D);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.TINY_SLUDGE_WORM_HELPER;
	}

	@Override
	public boolean canAttackClass(Class<? extends LivingEntity> entity) {
		return !EntityTinySludgeWormHelper.class.isAssignableFrom(entity);
	}

	@Override
	@Nullable
	public UUID getOwnerId() {
		return this.entityData.get(OWNER_UNIQUE_ID).orNull();
	}

	@Override
	@Nullable
	public LivingEntity getOwner() {
		try {
			UUID uuid = this.getOwnerId();
			return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
		} catch (IllegalArgumentException var2) {
			return null;
		}
	}

	@Override
	public void writeEntityToNBT(CompoundNBT compound) {
		super.writeEntityToNBT(compound);

		if (this.getOwnerId() == null) {
			compound.putString("OwnerUUID", "");
		} else {
			compound.putString("OwnerUUID", this.getOwnerId().toString());
		}
	}

	public void setOwnerId(@Nullable UUID ownerUuid) {
		this.entityData.set(OWNER_UNIQUE_ID, Optional.fromNullable(ownerUuid));
	}

	@Override
	public void readEntityFromNBT(CompoundNBT compound) {
		super.readEntityFromNBT(compound);

		String s;
		if (compound.contains("OwnerUUID", 8)) {
			s = compound.getString("OwnerUUID");
		} else {
			String s1 = compound.getString("Owner");
			s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
		}

		if (!s.isEmpty()) {
			try {
				this.setOwnerId(UUID.fromString(s));
			} catch (Throwable var4) {
				this.setOwnerId(null);
			}
		}
	}
}
