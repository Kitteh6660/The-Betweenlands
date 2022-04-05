package thebetweenlands.common.item.farming;

import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class ItemPlantableSeeds extends Item implements IPlantable {
	
	protected final Supplier<BlockState> crops;
	protected final Predicate<BlockState> soilMatcher;

	public ItemPlantableSeeds(Supplier<BlockState> crops, Properties properties) {
		this(crops, null, properties);
	}

	public ItemPlantableSeeds(Supplier<BlockState> crops, @Nullable Predicate<BlockState> soilMatcher, Properties properties) {
		super(properties);
		this.crops = crops;
		this.soilMatcher = soilMatcher;
		//this.setCreativeTab(BLCreativeTabs.PLANTS);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Hand hand = context.getHand();
		Direction facing = context.getClickedFace();
		
		ItemStack stack = player.getItemInHand(hand);
		BlockState state = world.getBlockState(pos);
		BushBlock bush = this.crops.get().getBlock() instanceof BushBlock ? (BushBlock) this.crops.get().getBlock() : null;
		BlockPos up = pos.above();
		if (facing == Direction.UP && player.mayUseItemAt(pos.relative(facing), facing, stack) && 
				(bush == null ? state.getBlock().canSustainPlant(state, world, pos, Direction.UP, this) : bush.canSustainPlant(state, world, pos, Direction.UP, bush)) && world.isEmptyBlock(up) && (this.soilMatcher == null || this.soilMatcher.test(state))) {
			BlockState plantState = this.crops.get();
			world.setBlockAndUpdate(up, plantState);
			if (player instanceof ServerPlayerEntity) {
				CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, up, stack);
			}
			this.onPlant(context);
			stack.shrink(1);
			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.FAIL;
		}
	}

	protected void onPlant(ItemUseContext context) {
		
	}
	
	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos){
		return PlantType.CROP;
	}

	@Override
	public BlockState getPlant(IBlockReader world, BlockPos pos) {
		return this.crops.get();
	}
}
