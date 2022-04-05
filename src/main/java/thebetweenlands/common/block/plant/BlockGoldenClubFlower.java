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
	
	public BlockGoldenClubFlower(Properties properties) {
		super(properties);
		this.harvestAll = true;
		this.setMaxHeight(1);
	}

	@Override
	protected boolean isSamePlant(BlockState blockState) {
		return super.isSamePlant(blockState) || blockState.getBlock() == BlockRegistry.GOLDEN_CLUB_STALK.get();
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return state.getBlock() == BlockRegistry.GOLDEN_CLUB_STALK.get();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public OffsetType getOffsetType() {
		return OffsetType.NONE;
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlockAndUpdate(pos, BlockRegistry.GOLDEN_CLUB_STALK.get().defaultBlockState());
		worldIn.setBlockAndUpdate(pos.above(), BlockRegistry.GOLDEN_CLUB_FLOWER.get().defaultBlockState());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return BlockRegistry.GOLDEN_CLUB_STALK.get().canPlaceBlockAt(worldIn, pos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(IS_TOP, IS_BOTTOM);
	}
}
