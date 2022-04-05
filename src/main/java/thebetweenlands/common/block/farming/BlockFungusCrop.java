package thebetweenlands.common.block.farming;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockFungusCrop extends BlockGenericCrop {
	
	public BlockFungusCrop(Properties properties) {
		super(properties);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);

		if(this.isDecayed(worldIn, pos)) {
			if(!worldIn.isClientSide() && state.getValue(AGE) >= 15 && rand.nextInt(6) == 0) {
				EntitySporeling sporeling = new EntitySporeling(worldIn);
				sporeling.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
				worldIn.addFreshEntity(sporeling);
				worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				this.harvestAndUpdateSoil(worldIn, pos, 5);

				for (ServerPlayerEntity playerMP : worldIn.getEntitiesOfClass(ServerPlayerEntity.class, Block.box(pos, pos).inflate(10.0D, 5.0D, 10.0D))) {
					AdvancementCriterionRegistry.SPORELING_HATCH.trigger(playerMP);
                }
			}
		}
	}

	@Override
	protected float getGrowthChance(World world, BlockPos pos, BlockState state, Random rand) {
		return 0.9F;
	}

	@Override
	public int getCropDrops(IBlockReader world, BlockPos pos, Random rand, int fortune) {
		BlockState state = world.getBlockState(pos);
		if(state.getValue(AGE) >= 15) {
			return 1 + (fortune > 0 ? rand.nextInt(1 + fortune) : 0);
		}
		return 0;
	}

	@Override
	public ItemStack getSeedDrop(IBlockReader world, BlockPos pos, Random rand) {
		return new ItemStack(ItemRegistry.SPORES);	
	}

	@Override
	public ItemStack getCropDrop(IBlockReader world, BlockPos pos, Random rand) {
		return this.isDecayed(world, pos) ? ItemStack.EMPTY : new ItemStack(ItemRegistry.YELLOW_DOTTED_FUNGUS);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(ItemRegistry.SPORES);
	}

	@Override
	public BlockItem getItemBlock() {
		return null;
	}
}
