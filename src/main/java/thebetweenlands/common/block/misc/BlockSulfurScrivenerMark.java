package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockSulfurScrivenerMark extends BlockScrivenerMark {
	
	public static final BooleanProperty BURNING = BooleanProperty.create("burning");

	public BlockSulfurScrivenerMark(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(BURNING, false).setValue(NORTH_SIDE, false).setValue(EAST_SIDE, false).setValue(SOUTH_SIDE, false).setValue(WEST_SIDE, false));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return this.getConnectedTextureBlockStateContainer(new BlockState(this, new Property[] { BURNING, NORTH_SIDE, EAST_SIDE, SOUTH_SIDE, WEST_SIDE }, new Property[0]));
	}

	//TODO: Remove this code.
	/*@Override
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
	}*/

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

		if(!worldIn.isClientSide() && worldIn.getBlockState(fromPos).getBlock() == Blocks.FIRE) {
			this.setOnFire(worldIn, pos, state);
		}
	}

	public void setOnFire(World world, BlockPos pos, BlockState state) {
		world.setBlockAndUpdate(pos, state.setValue(BURNING, true));
		world.scheduleUpdate(pos, state.getBlock(), 1);
		world.blockEvent(pos, this, 10, 0);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if(state.getValue(BURNING)) {
			for(Direction offset : Direction.Plane.HORIZONTAL) {
				for(int yo = -1; yo <= 1; yo++) {
					BlockPos offsetPos = pos.offset(offset.getStepX(), offset.getStepY() + yo, offset.getStepZ());

					BlockState offsetState = worldIn.getBlockState(offsetPos);

					if(offsetState.getBlock() instanceof BlockSulfurScrivenerMark) {
						((BlockSulfurScrivenerMark) offsetState.getBlock()).setOnFire(worldIn, offsetPos, offsetState);
					}
				}
			}

			worldIn.blockEvent(pos, this, 10, 1);

			worldIn.setBlockState(pos, BlockRegistry.SCRIVENER_BURNT_MARK.defaultBlockState());
		}
	}

	@Override
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		if(worldIn.isClientSide() && id == 10) {
			for(int i = 0; i < 3; ++i) {
				double d0 = (double)pos.getX() + worldIn.random.nextDouble();
				double d1 = (double)pos.getY() + worldIn.random.nextDouble() * 0.5D;
				double d2 = (double)pos.getZ() + worldIn.random.nextDouble();
				worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(stateIn.getValue(BURNING)) {
			for(int i = 0; i < 3; ++i) {
				double d0 = (double)pos.getX() + rand.nextDouble();
				double d1 = (double)pos.getY() + rand.nextDouble() * 0.5D;
				double d2 = (double)pos.getZ() + rand.nextDouble();
				worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
	}
}
