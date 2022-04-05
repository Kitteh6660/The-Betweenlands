package thebetweenlands.common.block.farming;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockMiddleFruitBush extends BlockGenericCrop {
	
	public BlockMiddleFruitBush(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		if(state.getValue(AGE) >= 15) {
			if(!world.isClientSide()) {
				this.dropBlockAsItem(world, pos, state, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, playerIn.getItemInHand(hand)));
				world.setBlockAndUpdate(pos, state.setValue(AGE, 8));
				this.harvestAndUpdateSoil(world, pos, 10);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public int getCropDrops(IBlockReader world, BlockPos pos, Random rand, int fortune) {
		BlockState state = world.getBlockState(pos);
		if(state.getValue(AGE) >= 15) {
			return 1 + rand.nextInt(3 + fortune);
		}
		return 0;
	}

	@Override
	protected float getGrowthChance(World world, BlockPos pos, BlockState state, Random rand) {
		return 0.25F;
	}

	@Override
	protected IntegerProperty createStageProperty() {
		return IntegerProperty.create("stage", 0, 5);
	}

	@Override
	public ItemStack getSeedDrop(IBlockReader world, BlockPos pos, Random rand) {
		return new ItemStack(ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS.get());	
	}

	@Override
	public ItemStack getCropDrop(IBlockReader world, BlockPos pos, Random rand) {
		return this.isDecayed(world, pos) ? ItemStack.EMPTY : new ItemStack(ItemRegistry.MIDDLE_FRUIT.get());
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS.get());
	}

}
