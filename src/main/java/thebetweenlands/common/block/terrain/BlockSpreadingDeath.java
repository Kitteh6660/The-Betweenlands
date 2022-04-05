package thebetweenlands.common.block.terrain;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public abstract class BlockSpreadingDeath extends Block {
	
	public static final BooleanProperty INACTIVE = BooleanProperty.create("inactive");

	public BlockSpreadingDeath(Properties properties) {
		super(properties);
		/*super(material);
		this.registerDefaultState(this.defaultBlockState().setValue(INACTIVE, false));*/
		//this.setTickRandomly(true);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return !state.getValue(INACTIVE);
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] { INACTIVE });
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(INACTIVE) ? 1 : 0;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(INACTIVE, (meta & 1) == 1);
	}

	@Override
	public void breakBlock(World level, BlockPos pos, BlockState state) {
		super.breakBlock(level, pos, state);

		if(!level.isClientSide()) {
			this.checkAndRevertBiome(level, pos);
		}
	}

	@Override
	public void onPlace(World level, BlockPos pos, BlockState state) {
		super.onPlace(level, pos, state);
		int spreadTime = this.getScheduledSpreadTime(level, pos, state);
		if(spreadTime > 0) {
			level.scheduleTick(pos, this, spreadTime);
		}
	}

	@Override
	public void updateTick(World level, BlockPos pos, BlockState state, Random rand) {
		if(!level.isClientSide()) {
			if(!state.getValue(INACTIVE) && this.shouldSpread(level, pos, state)) {
				boolean spread = false;
				for(int i = 0; i < 16; ++i) {
					BlockPos target = pos.offset(rand.nextInt(3) - 1, rand.nextInt(3) - 1, rand.nextInt(3) - 1);

					if(level.isLoaded(target)) {
						BlockState offsetState = level.getBlockState(target);

						if(offsetState.getBlock() != this && this.canSpreadInto(level, pos, state, target, offsetState)) {
							this.spreadInto(level, pos, state, target, offsetState);
							if(this.getSpreadingBiome() != null) {
								this.convertBiome(level, target, this.getSpreadingBiome());
							}
							spread = true;
						}
					}
				}
				if(!spread) {
					for(int i = 0; i < 16; ++i) {
						BlockPos target = pos.offset(rand.nextInt(5) - 2, rand.nextInt(5) - 2, rand.nextInt(5) - 2);

						if(level.isLoaded(target)) {
							BlockState offsetState = level.getBlockState(target);

							if(offsetState.getBlock() != this && this.canSpreadInto(level, pos, state, target, offsetState)) {
								this.spreadInto(level, pos, state, target, offsetState);
								if(this.getSpreadingBiome() != null) {
									this.convertBiome(level, target, this.getSpreadingBiome());
								}
								spread = true;
							}
						}
					}
				}

				int spreadTime = this.getScheduledSpreadTime(world, pos, state);
				if(spreadTime > 0) {
					level.scheduleUpdate(pos, this, spreadTime);
				}
			}

			if(level.random.nextInt(6) == 0) {
				level.setBlockState(pos, state.setValue(INACTIVE, true));
			}

			if(this.getSpreadingBiome() != null && rand.nextInt(3) == 0 && level.getBiome(pos) != this.getSpreadingBiome()) {
				this.convertBiome(level, pos, this.getSpreadingBiome());
			}
		}
	}

	protected boolean shouldSpread(World world, BlockPos pos, BlockState state) {
		return true;
	}
	
	public boolean canSpreadInto(World world, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState) {
		BlockState offsetStateUp = world.getBlockState(offsetPos.above());
		return offsetStateUp.getBlock() != this && !offsetStateUp.isNormalCube() && (this.getPreviousBiome() == null || world.getBiome(offsetPos) == this.getPreviousBiome());
	}

	public abstract void spreadInto(World world, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState);

	protected int getScheduledSpreadTime(World world, BlockPos pos, BlockState state) {
		return -1;
	}

	@Nullable
	public Biome getSpreadingBiome() {
		return null;
	}

	@Nullable
	public Biome getPreviousBiome() {
		return null;
	}

	protected void checkAndRevertBiome(World world, BlockPos pos) {
		if(this.getPreviousBiome() != null && this.getSpreadingBiome() != null && world.getBiome(pos) == this.getSpreadingBiome()) {
			this.convertBiome(world, pos, this.getPreviousBiome());
		}
	}

	protected void convertBiome(World world, BlockPos pos, Biome biome) {
		Chunk chunk = world.getChunk(pos);
		byte[] biomes = chunk.getBiomes().clone();
		int index = (pos.getZ() & 15) << 4 | (pos.getX() & 15);
		biomes[index] = (byte) (Biome.getIdForBiome(biome) & 255);
		chunk.setBiomeArray(biomes);
		chunk.setChanged();
	}
}
