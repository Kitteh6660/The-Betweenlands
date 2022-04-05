package thebetweenlands.common.block.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntityTarBeast;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.AdvancedStateMap;

import javax.annotation.Nonnull;

public class TarFluid extends FlowingFluid implements IStateMappedBlock, ICustomItemBlock {
	
	public TarFluid() {
		super();
		//super(FluidRegistry.TAR, BLMaterialRegistry.TAR);
	}

	@Override
	public boolean canDisplace(IBlockReader world, BlockPos pos) {
		if (world.getBlockState(pos).getMaterial().isLiquid())
			return false;
		return super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		if (world.getBlockState(pos).getMaterial().isLiquid())
			return false;
		return super.displaceIfPossible(world, pos);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity && !(entity instanceof EntityTarBeast) && !(entity instanceof PlayerEntity && ((PlayerEntity)entity).isCreative())) {
			double liquidHeight = (double)((float)(pos.getY() + 1) - BlockLiquid.getLiquidHeightPercent(((Integer)state.getValue(BlockLiquid.LEVEL)).intValue()));
			if (entity.getY() + entity.getEyeHeight() < liquidHeight) {
				((LivingEntity) entity).hurt(DamageSource.DROWN, 2.0F);
			}
		}
	}

	@Override
	public void onPlace(World world, BlockPos pos, BlockState state) {
		this.solidifyTar(world, pos);
		super.onPlace(world, pos, state);
	}

	@Override
	public void neighborChanged(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock, @Nonnull BlockPos neighbourPos) {

		this.solidifyTar(world, pos);
		super.neighborChanged(state, world, pos, neighborBlock, neighbourPos);
	}

	private void solidifyTar(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() == this) {
			boolean placeTar = false;

			if (!placeTar && world.getBlockState(pos.offset(0, 0, -1)).getMaterial() == Material.WATER)
				placeTar = true;

			if (!placeTar && world.getBlockState(pos.offset(0, 0, 1)).getMaterial() == Material.WATER)
				placeTar = true;

			if (!placeTar && world.getBlockState(pos.offset(-1, 0, 0)).getMaterial() == Material.WATER)
				placeTar = true;

			if (!placeTar && world.getBlockState(pos.offset(1, 0, 0)).getMaterial() == Material.WATER)
				placeTar = true;

			if (!placeTar && world.getBlockState(pos.above()).getMaterial() == Material.WATER)
				placeTar = true;

			if (!placeTar && world.getBlockState(pos.below()).getMaterial() == Material.WATER) {
				//Set water block below to solid tar
				world.setBlockAndUpdate(pos.below(), BlockRegistry.TAR_SOLID.defaultBlockState());
			}

			if (placeTar) {
				world.setBlockAndUpdate(pos, BlockRegistry.TAR_SOLID.defaultBlockState());
				if(level.isClientSide()) {
					playEffects(world, pos.getX(), pos.getY(), pos.getZ());
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected void playEffects(World world, int x, int y, int z) {
		world.playLocalSound(null, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

		for (int l = 0; l < 8; ++l) {
			world.addParticle(ParticleTypes.SMOKE_LARGE, x + Math.random(), y + 1.2D, z + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(BlockTar.LEVEL);
	}

	@Override
	public Boolean isEntityInsideMaterial(IBlockReader world, BlockPos blockpos, BlockState state, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
		if(entity instanceof EntityTarBeast == false && materialIn == Material.WATER) {
			double liquidHeight = (double)((float)(blockpos.getY() + 1) - BlockLiquid.getLiquidHeightPercent(((Integer)state.getValue(BlockLiquid.LEVEL)).intValue()));
			if(testingHead) {
				double liquidHeightBelow = 0;
				if(world.getBlockState(blockpos.above()).getBlock() == state.getBlock()) {
					liquidHeightBelow = (double)((float)(blockpos.getY() + 2) - BlockLiquid.getLiquidHeightPercent(((Integer)world.getBlockState(blockpos.above()).getValue(BlockLiquid.LEVEL)).intValue()));
				}
				return entity.getY() + entity.getEyeHeight() < 0.1D + liquidHeight || entity.getY() + entity.getEyeHeight() < 0.1D + liquidHeightBelow;
			} else {
				return entity.getBoundingBox().maxY >= liquidHeight && entity.getBoundingBox().minY < liquidHeight;
			}
		}
		return null;
	}

	@Override
	public Vector3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vector3d motion) {
		BlockPos entityPos = new BlockPos(entityIn.getX(), entityIn.getY() + 0.5D, entityIn.getZ());
		if(entityIn instanceof EntityTarBeast == false && pos.equals(entityPos) /*make sure it's only changed once*/) {
			entityIn.motionX *= 0.6D;
			entityIn.motionY *= 0.8D;
			entityIn.motionY -= 0.0175D;
			entityIn.motionZ *= 0.6D;
		}
		return new Vector3d(0, 0, 0);
	}
	
	@Override
	public BlockItem getItemBlock() {
		return null;
	}
}