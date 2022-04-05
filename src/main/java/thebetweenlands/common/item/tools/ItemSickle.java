package thebetweenlands.common.item.tools;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

import javax.annotation.Nullable;

public class ItemSickle extends Item implements ICorrodible, IAnimatorRepairable, IVanishable {
	
	public ItemSickle(Properties properties) {
		super(properties);
		/*this.setTranslationKey("thebetweenlands.sickle");
		this.setMaxStackSize(1);
		this.setMaxDamage(2500);
		this.setCreativeTab(BLCreativeTabs.GEARS);*/
		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		boolean shouldDrop = player.level.random.nextFloat() <= 1.0F * CorrosionHelper.getModifier(itemstack);
		if (player.level.isClientSide() || player.isCreative() || !shouldDrop)
			return false;
		Block block = player.level.getBlockState(pos).getBlock();
		if (block instanceof ISickleHarvestable) {
			ISickleHarvestable target = (ISickleHarvestable)block;
			if (target.isHarvestable(itemstack, player.level, pos)) {
				List<ItemStack> drops = target.getHarvestableDrops(itemstack, player.level, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE, player));
				if(drops == null || drops.isEmpty())
					return false;
				Random rand = new Random();
				for(ItemStack stack : drops) {
					float offset = 0.7F;
					double rx  = (double)(rand.nextFloat() * offset) + (double)(1.0F - offset) * 0.5D;
					double ry = (double)(rand.nextFloat() * offset) + (double)(1.0F - offset) * 0.5D;
					double rz = (double)(rand.nextFloat() * offset) + (double)(1.0F - offset) * 0.5D;
					ItemEntity ItemEntity = new ItemEntity(player.level, (double)pos.getX() + rx, (double)pos.getY() + ry, (double)pos.getZ() + rz, stack);
					ItemEntity.setDefaultPickUpDelay();
					player.level.addFreshEntity(ItemEntity);
				}
				itemstack.damageItem(1, player);
				block.onBlockHarvested(player.level, pos, player.level.getBlockState(pos), player);
				if (player instanceof ServerPlayerEntity)
					AdvancementCriterionRegistry.SICKLE_USE.trigger((ServerPlayerEntity) player);
				player.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				player.awardStat(Stats.getBlockStats(block), 1);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return CorrosionHelper.shouldCauseBlockBreakReset(oldStack, newStack);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return CorrosionHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return CorrosionHelper.getDestroySpeed(super.getDestroySpeed(stack, state), stack, state);
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		CorrosionHelper.updateCorrosion(itemStack, world, holder, slot, isHeldItem);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		CorrosionHelper.addCorrosionTooltips(stack, tooltip, flagIn.isAdvanced());
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return 6;
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return 16;
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return 12;
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return 32;
	}
}
