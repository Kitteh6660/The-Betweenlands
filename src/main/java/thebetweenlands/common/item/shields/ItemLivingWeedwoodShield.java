package thebetweenlands.common.item.shields;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.item.ILivingWeedwoodShieldCapability;
import thebetweenlands.common.entity.projectiles.EntitySapSpit;
import thebetweenlands.common.network.clientbound.MessageLivingWeedwoodShieldSpit;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemLivingWeedwoodShield extends ItemWeedwoodShield {
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if(entityIn instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entityIn;

			if(!worldIn.isClientSide()) {
				boolean mainhand = living.getItemInHand(Hand.MAIN_HAND) == stack;
				boolean offhand = living.getItemInHand(Hand.OFF_HAND) == stack;
				if(living.isActiveItemStackBlocking() && (mainhand || offhand)) {
					int spitCooldown = this.getSpitCooldown(stack);
					if(spitCooldown <= 0) {
						this.trySpit(stack, worldIn, living, mainhand ? Hand.MAIN_HAND : Hand.OFF_HAND);
						this.setSpitCooldown(stack, 30 + worldIn.rand.nextInt(30));
					} else {
						this.setSpitCooldown(stack, spitCooldown - 1);
					}

					if(worldIn.rand.nextInt(60) == 0) {
						worldIn.playSound(null, entityIn.getX(), entityIn.getY() + entityIn.height / 2, entityIn.getZ(), SoundRegistry.SPIRIT_TREE_FACE_SMALL_LIVING, SoundCategory.PLAYERS, 0.35F, 1.4F);
					}
				}
			} else {
				int spitTicks = this.getSpitTicks(stack);
				if(spitTicks > 0) {
					this.setSpitTicks(stack, spitTicks - 1);
				}
			}
		}
	}

	@Override
	protected void onShieldBreak(ItemStack stack, LivingEntity attacked, Hand hand, DamageSource source) {
		super.onShieldBreak(stack, attacked, hand, source);

		if(this.getBurningTicks(stack) == 0) {
			attacked.setItemInHand(hand, new ItemStack(ItemRegistry.SPIRIT_TREE_FACE_SMALL_MASK));
		}
	}

	public void setSpitTicks(ItemStack stack, int ticks) {
		ILivingWeedwoodShieldCapability cap = stack.getCapability(CapabilityRegistry.CAPABILITY_LIVING_WEEDWOOD_SHIELD, null);
		if(cap != null) {
			cap.setSpitTicks(ticks);
		}
	}

	public int getSpitTicks(ItemStack stack) {
		ILivingWeedwoodShieldCapability cap = stack.getCapability(CapabilityRegistry.CAPABILITY_LIVING_WEEDWOOD_SHIELD, null);
		if(cap != null) {
			return cap.getSpitTicks();
		}
		return 0;
	}

	public void setSpitCooldown(ItemStack stack, int ticks) {
		ILivingWeedwoodShieldCapability cap = stack.getCapability(CapabilityRegistry.CAPABILITY_LIVING_WEEDWOOD_SHIELD, null);
		if(cap != null) {
			cap.setSpitCooldown(ticks);
		}
	}

	public int getSpitCooldown(ItemStack stack) {
		ILivingWeedwoodShieldCapability cap = stack.getCapability(CapabilityRegistry.CAPABILITY_LIVING_WEEDWOOD_SHIELD, null);
		if(cap != null) {
			return cap.getSpitCooldown();
		}
		return 0;
	}

	protected boolean trySpit(ItemStack stack, World world, LivingEntity owner, Hand hand) {
		LivingEntity target = null;
		List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(8, 3, 8), IMob.MOB_SELECTOR::apply);

		Vector3d lookVec = owner.getLookVec().normalize();

		for(LivingEntity e : entities) {
			if(Math.toDegrees(Math.acos(lookVec.dotProduct(e.getPositionEyes(1).subtract(owner.getPositionEyes(1)).normalize()))) <= 70) {
				if(target == null || target.getDistance(owner) > e.getDistance(owner)) {
					target = e;
				}
			}
		}

		if(target != null) {
			float yaw = -(180 - owner.renderYawOffset);
			Vector3d bodyForward = new Vector3d(MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI), 0, MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI));
			Vector3d up = new Vector3d(0, 1, 0);
			Vector3d right = bodyForward.cross(up);
			Vector3d offset = new Vector3d(bodyForward.x, owner.getEyeHeight(), bodyForward.z).add(right.scale(hand == Hand.MAIN_HAND ? 0.35D : -0.35D).add(0, lookVec.y * 0.5D - 0.4D, 0).add(bodyForward.scale(-0.1D)));

			EntitySapSpit spit = new EntitySapSpit(world, owner, 4.5F);
			spit.setPosition(owner.getX() + owner.motionX + offset.x, owner.getY() + offset.y, owner.getZ() + owner.motionZ + offset.z);

			double dx = target.getX() - spit.getX();
			double dy = target.getBoundingBox().minY + (double)(target.height / 3.0F) - spit.getY();
			double dz = target.getZ() - spit.getZ();
			double dist = (double)MathHelper.sqrt(dx * dx + dz * dz);
			spit.shoot(dx, dy + dist * 0.20000000298023224D, dz, 1, 1);

			world.addFreshEntity(spit);

			world.playLocalSound(null, spit.getX(), spit.getY(), spit.getZ(), SoundRegistry.SPIRIT_TREE_FACE_SMALL_SPIT, SoundCategory.PLAYERS, 1, 1);

			TheBetweenlands.networkWrapper.sendToAllAround(new MessageLivingWeedwoodShieldSpit(owner, hand == Hand.MAIN_HAND, 15), new TargetPoint(owner.dimension, owner.getX(), owner.getY(), owner.getZ(), 64));

			return true;
		}

		return false;
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
}
