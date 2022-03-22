package thebetweenlands.common.item.misc;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.util.NBTHelper;

import java.util.List;
import java.util.Random;

public class ItemShimmerStone extends Item {
	private static final int MAX_SHIMMER_TICKS = 8;

	public ItemShimmerStone() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
		this.addPropertyOverride(new ResourceLocation("shimmer"), (stack, worldIn, entityIn) ->
				NBTHelper.getStackNBTSafe(stack).getBoolean("shimmering") ? 1.0F : 0.0F);
		this.setMaxStackSize(1);
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player) {
		this.triggerAdvancement(player);
		return true;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		this.updateShimmer(stack, world.rand);
		super.onUpdate(stack, world, entity, itemSlot, isSelected);
	}

	@Override
	public boolean onEntityItemUpdate(ItemEntity ItemEntity) {
		this.updateShimmer(ItemEntity.getItem(), ItemEntity.world.rand);
		return super.onEntityItemUpdate(ItemEntity);
	}

	/**
	 * Updates the item shimmer
	 * @param stack
	 * @param rand
	 */
	protected void updateShimmer(ItemStack stack, Random rand) {
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
		boolean shimmer = nbt.getBoolean("shimmering");

		if(!shimmer) {
			if(rand.nextInt(30) == 0) {
				nbt.putBoolean("shimmering", true);
			}
		} else {
			int ticks = nbt.getInt("shimmeringTicks");
			if(ticks < MAX_SHIMMER_TICKS) {
				nbt.putInt("shimmeringTicks", ticks + 1);
			} else {
				nbt.putBoolean("shimmering", false);
				nbt.putInt("shimmeringTicks", 0);
			}
		}
	}

	private static final ImmutableList<String> STACK_NBT_EXCLUSIONS = ImmutableList.of("shimmering", "shimmeringTicks");

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !NBTHelper.areItemStackTagsEqual(oldStack, newStack, STACK_NBT_EXCLUSIONS);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!world.isClientSide()) {
			double px = player.getX();
			double py = player.getY() + player.getEyeHeight();
			double pz = player.getZ();
			px -= (double)(MathHelper.cos(player.yRot / 180.0F * (float)Math.PI) * 0.16F);
			py -= 0.25D;
			pz -= (double)(MathHelper.sin(player.yRot / 180.0F * (float)Math.PI) * 0.16F);
			float strength = 0.8F;
			double mx = (double)(-MathHelper.sin(player.yRot / 180.0F * (float)Math.PI) * MathHelper.cos(player.xRot / 180.0F * (float)Math.PI) * strength);
			double mz = (double)(MathHelper.cos(player.yRot / 180.0F * (float)Math.PI) * MathHelper.cos(player.xRot / 180.0F * (float)Math.PI) * strength);
			double my = (double)(-MathHelper.sin((player.xRot - 8) / 180.0F * (float)Math.PI) * strength);
			ItemEntity itemEntity = new ItemEntity(world, px, py, pz, new ItemStack(this));
			itemEntity.motionX = mx;
			itemEntity.motionY = my;
			itemEntity.motionZ = mz;
			itemEntity.setPickupDelay(20);
			world.spawnEntity(itemEntity);

			if (!player.isCreative()) {
				stack.shrink(1);
			}
			
			this.triggerAdvancement(player);
		}

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}
	
	protected void triggerAdvancement(PlayerEntity player) {
		if (player != null && !player.world.isClientSide() && player instanceof ServerPlayerEntity && player.world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
            BlockPos pos = player.getPosition();
            List<EntityPeatMummy> mummies = player.world.getEntitiesOfClass(EntityPeatMummy.class, new AxisAlignedBB(pos, pos).grow(20));
            if (mummies.size() > 0)
                AdvancementCriterionRegistry.DROP_SHIMMERSTONE.trigger((ServerPlayerEntity) player);
        }
	}
}