package thebetweenlands.common.item.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityVolarkite;
import thebetweenlands.util.NBTHelper;

public class ItemVolarkite extends Item {
	public ItemVolarkite() {
		this.setCreativeTab(BLCreativeTabs.GEARS);

		this.setMaxStackSize(1);
		this.setMaxDamage(300);

		this.addPropertyOverride(new ResourceLocation("using"), (stack, worldIn, entityIn) -> {
			if(entityIn != null && (entityIn.getRidingEntity() instanceof EntityVolarkite || entityIn.getPassengers().stream().filter(e -> e instanceof EntityVolarkite).findAny().isPresent())) {
				return stack.getTag() != null && stack.getTag().getBoolean("using_kite") ? 1 : 0;
			}
			return 0;
		});
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if(!world.isClientSide()) {
			if(!player.isRiding() && player.getRecursivePassengersByType(EntityVolarkite.class).isEmpty()) {
				EntityVolarkite entity = new EntityVolarkite(world);
				entity.moveTo(player.getX(), player.getY(), player.getZ(), player.yRot, 0);
				entity.motionX = player.motionX;
				entity.motionY = player.motionY;
				entity.motionZ = player.motionZ;
				entity.velocityChanged = true;

				world.spawnEntity(entity);

				player.startRiding(entity);

				world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1, 1);
			}
		}

		return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, world, entity, itemSlot, isSelected);

		CompoundNBT tag = NBTHelper.getStackNBTSafe(stack);

		boolean usingKite = false;

		boolean isRidingKite = entity.getRidingEntity() instanceof EntityVolarkite;

		if(entity instanceof LivingEntity && (isRidingKite || entity.getPassengers().stream().filter(e -> e instanceof EntityVolarkite).findAny().isPresent())) {
			LivingEntity living = (LivingEntity) entity;

			boolean isMainHand = stack == living.getItemInHand(Hand.MAIN_HAND);
			boolean isOffHand = stack == living.getItemInHand(Hand.OFF_HAND);
			boolean hasOffHand = !living.getItemInHand(Hand.OFF_HAND).isEmpty() && living.getItemInHand(Hand.OFF_HAND).getItem() instanceof ItemVolarkite;
			if((isMainHand || isOffHand) && ((isMainHand && !hasOffHand) || isOffHand)) {
				if(!world.isClientSide() && isRidingKite && entity.tickCount % 20 == 0) {
					stack.damageItem(1, (LivingEntity) entity);
				}

				usingKite = true;
				tag.putBoolean("using_kite", true);
			}
		}

		if(!usingKite) {
			if(tag.getBoolean("using_kite")) {
				tag.putBoolean("using_kite", false);

				if(entity instanceof PlayerEntity) {
					((PlayerEntity) entity).getCooldownTracker().setCooldown(stack.getItem(), 20);
				}
			}
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem() || slotChanged;
	}

	public boolean canRideKite(ItemStack stack, Entity entity) {
		return true;
	}
}
