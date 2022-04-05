package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.util.AdvancedStateMap;

public class BlockMarshMarigoldStalk extends BlockStackablePlantUnderwater {
	
	public BlockMarshMarigoldStalk(Properties properties) {
		super(properties);
		this.harvestAll = true;
		this.setMaxHeight(1);
		//this.setCreativeTab(null);
	}

	@Override
	protected boolean isSamePlant(BlockState blockState) {
		return super.isSamePlant(blockState) || blockState.getBlock() == BlockRegistry.MARSH_MARIGOLD_FLOWER.get();
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, BlockState state) {
		return super.canBlockStay(worldIn, pos, state) && worldIn.getBlockState(pos.above()).getBlock() == BlockRegistry.MARSH_MARIGOLD_FLOWER.get();
	}
	
	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlockAndUpdate(pos, BlockRegistry.MARSH_MARIGOLD_STALK.get().defaultBlockState());
		worldIn.setBlockAndUpdate(pos.above(), BlockRegistry.MARSH_MARIGOLD_FLOWER.get().defaultBlockState());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return worldIn.isEmptyBlock(pos.above()) && super.canPlaceBlockAt(worldIn, pos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(IS_TOP, IS_BOTTOM);
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(BlockRegistry.MARSH_MARIGOLD_FLOWER)));
	}
	
	@Override
	public boolean isFarmable(World world, BlockPos pos, BlockState state) {
		return true;
	}
	
	@Override
	public boolean canSpreadTo(World world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		return super.canSpreadTo(world, pos, state, targetPos, rand) && world.isEmptyBlock(targetPos.above());
	}
	
	@Override
	public void spreadTo(World world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		super.spreadTo(world, pos, state, targetPos, rand);
		world.setBlockAndUpdate(targetPos.above(), BlockRegistry.MARSH_MARIGOLD_FLOWER.get().defaultBlockState());
	}
	
	@Override
	public void decayPlant(World world, BlockPos pos, BlockState state, Random rand) {
		super.decayPlant(world, pos, state, rand);
		if(world.getBlockState(pos.above()).getBlock() == BlockRegistry.MARSH_MARIGOLD_FLOWER.get()) {
			world.setBlockToAir(pos.above());
		}
	}
	
	@Override
	public BlockItem getItemBlock() {
		return null;
	}
	
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(BlockRegistry.MARSH_MARIGOLD_FLOWER);
	}
}
