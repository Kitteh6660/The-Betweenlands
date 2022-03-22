package thebetweenlands.common.item.farming;

import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;
import thebetweenlands.client.tab.BLCreativeTabs;


public class ItemPlantableSeeds extends ItemSeeds {
	protected final Supplier<BlockState> crops;
	protected final Predicate<BlockState> soilMatcher;

	public ItemPlantableSeeds(Supplier<BlockState> crops) {
		this(crops, null);
	}

	public ItemPlantableSeeds(Supplier<BlockState> crops, @Nullable Predicate<BlockState> soilMatcher) {
		super(null, null);
		this.crops = crops;
		this.soilMatcher = soilMatcher;
		this.setCreativeTab(BLCreativeTabs.PLANTS);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = playerIn.getItemInHand(hand);
		BlockState state = worldIn.getBlockState(pos);
		BlockBush bush = this.crops.get().getBlock() instanceof BlockBush ? (BlockBush) this.crops.get().getBlock() : null;
		BlockPos up = pos.above();
		if (facing == Direction.UP && playerIn.mayUseItemAt(pos.offset(facing), facing, stack) && 
				(bush == null ? state.getBlock().canSustainPlant(state, worldIn, pos, Direction.UP, this) : bush.canSustainPlant(state, worldIn, pos, Direction.UP, bush)) 
				&& worldIn.isEmptyBlock(up)
				&& (this.soilMatcher == null || this.soilMatcher.test(state))) {
			BlockState plantState = this.crops.get();
			worldIn.setBlockState(up, plantState);
			if (playerIn instanceof ServerPlayerEntity) {
				CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerIn, up, stack);
			}
			this.onPlant(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ, plantState);
			stack.shrink(1);
			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.FAIL;
		}
	}

	protected void onPlant(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult, BlockState state) {
		
	}
	
	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos){
		return PlantType.Crop;
	}

	@Override
	public BlockState getPlant(IBlockReader world, BlockPos pos) {
		return this.crops.get();
	}
}
