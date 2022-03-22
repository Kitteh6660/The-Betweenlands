package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityTamedSpiritTreeFace extends EntitySpiritTreeFaceSmallBase {
	public EntityTamedSpiritTreeFace(World world) {
		super(world);
	}

	@Override
	protected void initEntityAI() {
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<LivingEntity>(this, LivingEntity.class, 10, false, false, e -> IMob.VISIBLE_MOB_SELECTOR.apply(e) && e instanceof EntityTamedSpiritTreeFace == false));

		this.tasks.addTask(0, new AITrackTargetSpiritTreeFace(this, true, 16.0D));
		this.tasks.addTask(1, new AIAttackMelee(this, 1, true));
		this.tasks.addTask(2, new AISpit(this, 5.0F, 30, 70) {
			@Override
			protected float getSpitDamage() {
				return (float) EntityTamedSpiritTreeFace.this.getEntityAttribute(Attributes.ATTACK_DAMAGE).getAttributeValue();
			}
		});
		this.tasks.addTask(3, new AIWanterTamedSpiritTreeFace(this, 8, 0.33D, 200));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, PlayerEntity.class, 8.0F) {
			@Override
			public void updateTask() {
				EntityTamedSpiritTreeFace.this.getLookHelper().setSpeed(0.33D);
				super.updateTask();
			}
		});
		this.tasks.addTask(5, new EntityAILookIdle(this) {
			@Override
			public void updateTask() {
				EntityTamedSpiritTreeFace.this.getLookHelper().setSpeed(0.33D);
				super.updateTask();
			}
		});
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16.0D);
		this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(50);
		this.getEntityAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	protected void fixUnsuitablePosition(int violatedChecks) {
		if(this.isAnchored() && (violatedChecks & AnchorChecks.BLOCKS) != 0) {
			this.setAnchored(false);
		}
		super.fixUnsuitablePosition(violatedChecks);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source.getImmediateSource() instanceof PlayerEntity) {
			return super.attackEntityFrom(source, amount);
		}
		return false;
	}
	
	@Override
	protected boolean processInteract(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		
		boolean holdsEquipment = hand == Hand.MAIN_HAND && !stack.isEmpty() && (stack.getItem() instanceof IEquippable || stack.getItem() == ItemRegistry.AMULET_SLOT);
		if(holdsEquipment) {
			return true;
		}
			
		if(!stack.isEmpty() && EnumItemMisc.COMPOST.isItemOf(stack)) {
			if(this.getHealth() < this.getMaxHealth()) {
				if(!this.level.isClientSide()) {
					this.heal(4);
				} else {
					for (int i = 0; i < 7; ++i) {
			            double d0 = this.random.nextGaussian() * 0.02D;
			            double d1 = this.random.nextGaussian() * 0.02D;
			            double d2 = this.random.nextGaussian() * 0.02D;
			            this.world.spawnParticle(EnumParticleTypes.HEART, this.getX() + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width, this.getY() + 0.5D + (double)(this.random.nextFloat() * this.height), this.getZ() + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
			        }
				}
			}
			
			if(!this.level.isClientSide()) {
				if(this.random.nextBoolean()) {
					this.entityDropItem(new ItemStack(ItemRegistry.SAP_SPIT, 1 + this.random.nextInt(3)), this.height / 2);
					
					this.playSpitSound();
				}
			
				stack.shrink(1);
			} else {
				for (int i = 0; i < 4; ++i) {
		            double d0 = this.random.nextGaussian() * 0.02D;
		            double d1 = this.random.nextGaussian() * 0.02D;
		            double d2 = this.random.nextGaussian() * 0.02D;
		            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.getX() + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width, this.getY() + 0.5D + (double)(this.random.nextFloat() * this.height), this.getZ() + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
		        }
				
			}
			
			return true;
		}
		
		return false;
	}

	public static class AIWanterTamedSpiritTreeFace extends AIWander<EntityTamedSpiritTreeFace> {
		public AIWanterTamedSpiritTreeFace(EntityTamedSpiritTreeFace entity, double range, double speed) {
			super(entity, range, speed);
		}

		public AIWanterTamedSpiritTreeFace(EntityTamedSpiritTreeFace entity, double range, double speed, int chance) {
			super(entity, range, speed, chance);
		}

		@Override
		protected boolean canMove() {
			return this.entity.isActive() && !this.entity.isAttacking();
		}
	}
}
