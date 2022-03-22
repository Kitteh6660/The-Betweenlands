package thebetweenlands.common.item.shields;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.tools.ItemBLShield;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemWeedwoodShield extends ItemBLShield {
	public ItemWeedwoodShield() {
		super(BLMaterialRegistry.TOOL_WEEDWOOD);
		this.addPropertyOverride(new ResourceLocation("burning"), (stack, worldIn, entityIn) ->
				((ItemWeedwoodShield)stack.getItem()).getBurningTicks(stack) > 0 ? 1.0F : 0.0F);
	}

	public void setBurningTicks(ItemStack stack, int ticks) {
		stack.setTagInfo("burningTicks", new NBTTagInt(ticks));
	}

	public int getBurningTicks(ItemStack stack) {
		CompoundNBT tag = stack.getTag();
		return tag != null && tag.contains("burningTicks", Constants.NBT.TAG_INT) ? tag.getInt("burningTicks") : 0;
	}

	@Override
	public void onAttackBlocked(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		super.onAttackBlocked(stack, attacked, damage, source);
		if(!attacked.world.isClientSide() && source.getTrueSource() != null) {
			Entity attacker;
			if(source instanceof EntityDamageSourceIndirect) {
				attacker = ((EntityDamageSourceIndirect)source).getTrueSource();
			} else {
				attacker = source.getTrueSource();
			}
			if((attacker.isOnFire() || attacker instanceof EntitySmallFireball) && attacked.world.rand.nextFloat() < 0.5F) {
				this.setBurningTicks(stack, 80);
			} else if(attacker instanceof LivingEntity && attacked.world.rand.nextFloat() < 0.25F) {
				ItemStack activeItem = ((LivingEntity)attacker).getActiveItemStack();
				if(!activeItem.isEmpty()) {
					Item item = activeItem.getItem();
					if(item == ItemRegistry.OCTINE_AXE || item == ItemRegistry.OCTINE_PICKAXE || item == ItemRegistry.OCTINE_SHIELD || 
							item == ItemRegistry.OCTINE_SHOVEL || item == ItemRegistry.OCTINE_SWORD)
						stack.setTagInfo("burningTicks", new NBTTagInt(120));
				}
			}
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if(!worldIn.isClientSide() && entityIn != null) {
			int burningTicks = this.getBurningTicks(stack);
			if(burningTicks > 0) {
				this.setBurningTicks(stack, burningTicks - 1);
				if(burningTicks % 5 == 0)
					worldIn.playSound((PlayerEntity)null, (double)((float)entityIn.getX()), (double)((float)entityIn.getY() + entityIn.getEyeHeight()), (double)((float)entityIn.getZ()), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + worldIn.rand.nextFloat(), worldIn.rand.nextFloat() * 0.7F + 0.3F);
				if(burningTicks % 10 == 0 && worldIn.rand.nextFloat() < 0.3F)
					entityIn.world.setEntityState(entityIn, (byte)30);
				if(burningTicks % 3 == 0 && entityIn instanceof LivingEntity)
					stack.damageItem(1, (LivingEntity)entityIn);
				if(stack.getCount() <= 0 && entityIn instanceof LivingEntity) {
					if(entityIn instanceof PlayerEntity) {
						((PlayerEntity)entityIn).inventory.setItem(itemSlot, ItemStack.EMPTY);
						LivingEntity entityLiving = (LivingEntity) entityIn;
						if(entityLiving.getHeldItemOffhand() == stack)
							entityLiving.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	@Override
	public boolean onEntityItemUpdate(ItemEntity ItemEntity) {
		ItemStack stack = ItemEntity.getItem();
		if(!ItemEntity.world.isClientSide()) {
			int burningTicks = this.getBurningTicks(stack);
			if(burningTicks > 0) {
				this.setBurningTicks(stack, burningTicks - 1);
				if(burningTicks % 5 == 0)
					ItemEntity.world.playSound((PlayerEntity)null, (double)((float)ItemEntity.getX()), (double)((float)ItemEntity.getY() + ItemEntity.height / 2.0F), (double)((float)ItemEntity.getZ()), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + ItemEntity.world.rand.nextFloat(), ItemEntity.world.rand.nextFloat() * 0.7F + 0.3F);
				if(burningTicks % 3 == 0) {
					if (stack.attemptDamageItem(1, ItemEntity.world.rand, null)) {
						this.renderBrokenItemStack(ItemEntity.world, ItemEntity.getX(), ItemEntity.getY() + ItemEntity.height / 2.0F, ItemEntity.getZ(), stack);
						stack.shrink(1);
						if (stack.getCount() < 0) {
							stack.setCount(0);
						}
						ItemEntity.remove();
					}
				}
			}
		}
		return false;
	}

	private static final ImmutableList<String> STACK_NBT_EXCLUSIONS = ImmutableList.of("burningTicks");

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		boolean wasBurning = ((ItemWeedwoodShield)oldStack.getItem()).getBurningTicks(oldStack) > 0;
		boolean isOnFire = newStack.getItem() instanceof ItemWeedwoodShield ? ((ItemWeedwoodShield)newStack.getItem()).getBurningTicks(newStack) > 0 : false;
		return (super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !isOnFire || isOnFire != wasBurning) || !NBTHelper.areItemStackTagsEqual(oldStack, newStack, STACK_NBT_EXCLUSIONS);
	}

	protected void renderBrokenItemStack(World world, double x, double y, double z, ItemStack stack) {
		world.playSound((PlayerEntity)null, x, y, z, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.NEUTRAL, 0.8F, 0.8F + world.rand.nextFloat() * 0.4F);
		for (int i = 0; i < 5; ++i) {
			Vector3d motion = new Vector3d(((double)world.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			world.spawnParticle(EnumParticleTypes.ITEM_CRACK, x, y, z, motion.x, motion.y + 0.05D, motion.z, new int[] {Item.getIdFromItem(stack.getItem())});
		}
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
		ItemStack itemStackIn = playerIn.getItemInHand(hand);
		boolean isOnFire = ((ItemWeedwoodShield)itemStackIn.getItem()).getBurningTicks(itemStackIn) > 0;
		return isOnFire ? new ActionResult<ItemStack>(ActionResultType.PASS, itemStackIn) : super.onItemRightClick(worldIn, playerIn, hand);
	}
}
