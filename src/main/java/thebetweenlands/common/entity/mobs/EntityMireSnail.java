package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import thebetweenlands.common.entity.EntityAnimalBL;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityMireSnail extends EntityAnimalBL {
	private static final DataParameter<Boolean> HAS_MATED = EntityDataManager.createKey(EntityMireSnail.class, DataSerializers.BOOLEAN);
	int shagCount = 0;

	public EntityMireSnail(World world) {
		super(world);
		setPathPriority(PathNodeType.WATER, -1.0f);
		setSize(0.75F, 0.6F);
		stepHeight = 0.0F;
	}
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 1.2D));
		tasks.addTask(2, new EntityAIMate(this, 1.0D));
		tasks.addTask(3, new EntityAITempt(this, 1.0D, ItemRegistry.SLUDGE_BALL, false));
		tasks.addTask(3, new EntityAITempt(this, 1.0D, ItemRegistry.SAP_SPIT, false));
		tasks.addTask(5, new EntityAIWander(this, 0.85D));	
		tasks.addTask(6, new EntityAIWatchClosest(this, PlayerEntity.class, 6.0F));
		tasks.addTask(7, new EntityAILookIdle(this));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(HAS_MATED, false);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.18D);
		getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(5.0D);
		getEntityAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SNAIL_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.SNAIL_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SNAIL_DEATH;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.MIRE_SNAIL;
	}

	@Override
	public boolean processInteract(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!stack.isEmpty() && isBreedingItem(stack) && !shagging()) {
			player.swingArm(hand);
			setHasMated(true);
			return super.processInteract(player, hand);
		}

		return super.processInteract(player, hand);
	}

	public boolean shagging() {
		return isInLove();
	}

	@Override
	public boolean isBreedingItem(ItemStack is) {
		return is != null && (is.getItem() == ItemRegistry.SLUDGE_BALL || is.getItem() == ItemRegistry.SAP_SPIT);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return new EntityMireSnailEgg(world);
	}

	public void setHasMated(boolean hasMated) {
		dataManager.set(HAS_MATED, hasMated);
	}

	public boolean hasMated() {
		return dataManager.get(HAS_MATED);
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putBoolean("hasMated", hasMated());
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		if(nbt.contains("hasMated")) {
			setHasMated(nbt.getBoolean("hasMated"));
		}
	}

}
