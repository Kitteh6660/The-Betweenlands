package thebetweenlands.common.block.terrain;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.event.EventWinter;

public class BlockBlackIce extends Block 
{
	public BlockBlackIce(Properties properties) {
		super(properties);
		/*super(Material.ICE, false);
		this.setHardness(0.5F);
		this.setLightOpacity(3);
		this.setSoundType(SoundType.GLASS);
		this.slipperiness = 0.98F;
		this.setTickRandomly(true);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	/*@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}*/

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		player.awardStat(StatList.getBlockStats(this));
		player.causeFoodExhaustion(0.005F);

		if (this.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
			java.util.List<ItemStack> items = new java.util.ArrayList<ItemStack>();
			items.add(this.getSilkTouchDrop(state));

			net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
			for (ItemStack is : items) {
				spawnAsEntity(worldIn, pos, is);
			}
		} else {
			int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
			harvesters.set(player);
			this.dropBlockAsItem(worldIn, pos, state, i);
			harvesters.set(null);
			Material material = worldIn.getBlockState(pos.below()).getMaterial();

			if (material.blocksMotion() || material.isLiquid()) {
				worldIn.setBlockAndUpdate(pos, BlockRegistry.SWAMP_WATER.defaultBlockState());
			}
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if (!EventWinter.isFroooosty(worldIn) || (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11 - this.defaultBlockState().getLightOpacity())) {
			this.turnIntoWater(worldIn, pos);
		}
	}

	protected void turnIntoWater(World worldIn, BlockPos pos) {
		worldIn.setBlockAndUpdate(pos, BlockRegistry.SWAMP_WATER.defaultBlockState());
		worldIn.neighborChanged(pos, BlockRegistry.SWAMP_WATER, pos);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.NORMAL;
	}
}