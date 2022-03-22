package thebetweenlands.common.block.plant;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockGoldenClubFlower extends BlockStackablePlant {
	public BlockGoldenClubFlower() {
		this.harvestAll = true;
		this.setMaxHeight(1);
	}

	@Override
	protected boolean isSamePlant(BlockState blockState) {
		return super.isSamePlant(blockState) || blockState.getBlock() == BlockRegistry.GOLDEN_CLUB_STALK;
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return state.getBlock() == BlockRegistry.GOLDEN_CLUB_STALK;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlockState(pos, BlockRegistry.GOLDEN_CLUB_STALK.defaultBlockState());
		worldIn.setBlockState(pos.above(), BlockRegistry.GOLDEN_CLUB_FLOWER.defaultBlockState());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return BlockRegistry.GOLDEN_CLUB_STALK.canPlaceBlockAt(worldIn, pos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(IS_TOP, IS_BOTTOM);
	}
}
