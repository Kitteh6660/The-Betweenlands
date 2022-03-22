package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockSulfurScrivenerMark extends BlockScrivenerMark {
	public static final BooleanProperty BURNING = BooleanProperty.create("burning");

	public BlockSulfurScrivenerMark() {
		this.setDefaultState(this.blockState.getBaseState().setValue(BURNING, false).setValue(NORTH_SIDE, false).setValue(EAST_SIDE, false).setValue(SOUTH_SIDE, false).setValue(WEST_SIDE, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[] { BURNING, NORTH_SIDE, EAST_SIDE, SOUTH_SIDE, WEST_SIDE }, new IUnlistedProperty[0]));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(BURNING) ? 1 : 0;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(BURNING, meta == 1);
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return ItemRegistry.ITEMS_MISC;
	}

	@Override
	public int damageDropped(BlockState state) {
		return EnumItemMisc.SULFUR.getID();
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

		if(!worldIn.isClientSide() && worldIn.getBlockState(fromPos).getBlock() == Blocks.FIRE) {
			this.setOnFire(worldIn, pos, state);
		}
	}

	public void setOnFire(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.setValue(BURNING, true));
		world.scheduleUpdate(pos, state.getBlock(), 1);
		world.addBlockEvent(pos, this, 10, 0);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if(state.getValue(BURNING)) {
			for(Direction offset : Direction.HORIZONTALS) {
				for(int yo = -1; yo <= 1; yo++) {
					BlockPos offsetPos = pos.offset(offset.getStepX(), offset.getStepY() + yo, offset.getStepZ());

					BlockState offsetState = worldIn.getBlockState(offsetPos);

					if(offsetState.getBlock() instanceof BlockSulfurScrivenerMark) {
						((BlockSulfurScrivenerMark) offsetState.getBlock()).setOnFire(worldIn, offsetPos, offsetState);
					}
				}
			}

			worldIn.addBlockEvent(pos, this, 10, 1);

			worldIn.setBlockState(pos, BlockRegistry.SCRIVENER_BURNT_MARK.defaultBlockState());
		}
	}

	@Override
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		if(worldIn.isClientSide() && id == 10) {
			for(int i = 0; i < 3; ++i) {
				double d0 = (double)pos.getX() + worldIn.rand.nextDouble();
				double d1 = (double)pos.getY() + worldIn.rand.nextDouble() * 0.5D;
				double d2 = (double)pos.getZ() + worldIn.rand.nextDouble();
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(stateIn.getValue(BURNING)) {
			for(int i = 0; i < 3; ++i) {
				double d0 = (double)pos.getX() + rand.nextDouble();
				double d1 = (double)pos.getY() + rand.nextDouble() * 0.5D;
				double d2 = (double)pos.getZ() + rand.nextDouble();
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
	}
}
