package thebetweenlands.common.entity.mobs;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySiltCrab extends EntityMob implements IEntityBL {

	private EntityAIAttackMelee aiAttack;
	private EntityAIAvoidEntity<PlayerEntity> aiRunAway;
	private EntityAINearestAttackableTarget<PlayerEntity> aiTarget;

	private int aggroCooldown = 200;
	private boolean canAttack = false;

	public EntitySiltCrab(World world) {
		super(world);
		this.setSize(0.8F, 0.6F);
		this.stepHeight = 2;
	}

	@Override
	protected void registerGoals() {
		this.aiAttack = new EntityAIAttackMelee(this, 1.0D, true);
		this.aiRunAway = new EntityAIAvoidEntity<PlayerEntity>(this, PlayerEntity.class, 10.0F, 0.7D, 0.7D);
		this.aiTarget =  new EntityAINearestAttackableTarget<PlayerEntity>(this, PlayerEntity.class, true);

		this.goalSelector.addGoal(0, this.aiAttack);
		this.goalSelector.addGoal(1, this.aiRunAway);
		this.goalSelector.addGoal(2, new EntityAIWander(this, 1.0D));
		this.goalSelector.addGoal(3, new EntityAILookIdle(this));
		this.goalSelector.addGoal(4, new EntityAIAttackOnCollide(this));

		this.targetSelector.addGoal(0, new EntityAIHurtByTarget(this, true));
		this.targetSelector.addGoal(1, this.aiTarget);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35D);
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0D);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.0D);
		this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 5;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level.isClientSide()) {
			if (this.aggroCooldown == 200 && !this.canAttack) {
				this.goalSelector.removeTask(this.aiRunAway);
				this.goalSelector.addGoal(0, this.aiAttack);
				this.targetSelector.addGoal(1, this.aiTarget);
				this.canAttack = true;
			}

			if (this.aggroCooldown == 0 && this.canAttack) {
				this.goalSelector.removeTask(this.aiAttack);
				this.targetSelector.removeTask(this.aiTarget);
				this.goalSelector.addGoal(1, this.aiRunAway);
				this.canAttack = false;
			}

			if (this.aggroCooldown < 201)
				this.aggroCooldown++;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return !source.equals(DamageSource.DROWN) && super.hurt(source, damage);
	}

	@Override
	public void onCollideWithPlayer(PlayerEntity player) {
		if (!this.level.isClientSide() && getDistance(player) <= 1.5F && this.canAttack) {
			this.aggroCooldown = 0;
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean attacked;
		if(attacked = super.attackEntityAsMob(entityIn)) {
			this.playSound(SoundRegistry.CRAB_SNIP, 1, 1);
		}
		return attacked;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SILT_CRAB;
	}
	
	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }
}
