package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.util.AdvancedStateMap;

import java.util.Random;

public class BlockLeavesBetweenlands extends LeavesBlock implements IStateMappedBlock {
	private int[] decayBlockCache;

	public BlockLeavesBetweenlands(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, Integer.valueOf(7)).setValue(PERSISTENT, Boolean.valueOf(false)));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return !Minecraft.getInstance().gameSettings.fancyGraphics && blockAccess.getBlockState(pos.offset(side)).getBlock() == this ? false : internalShouldSideBeRendered(blockState, blockAccess, pos, side);
	}
	
	@Override
	public void beginLeavesDecay(BlockState state, World world, BlockPos pos) {
		super.beginLeavesDecay(state, world, pos);
	}

	@OnlyIn(Dist.CLIENT)
	private boolean internalShouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		AxisAlignedBB axisalignedbb = blockState.getBoundingBox(blockAccess, pos);
		switch (side) {
			case DOWN:
				if (axisalignedbb.minY > 0.0D) return true;
				break;
			case UP:
				if (axisalignedbb.maxY < 1.0D) return true;
				break;
			case NORTH:
				if (axisalignedbb.minZ > 0.0D) return true;
				break;
			case SOUTH:
				if (axisalignedbb.maxZ < 1.0D) return true;
				break;
			case WEST:
				if (axisalignedbb.minX > 0.0D) return true;
				break;
			case EAST:
				if (axisalignedbb.maxX < 1.0D) return true;
		}
		return !blockAccess.getBlockState(pos.offset(side)).doesSideBlockRendering(blockAccess, pos.offset(side), side.getOpposite());
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return Blocks.OAK_LEAVES.isOpaqueCube(state);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return Blocks.OAK_LEAVES.getRenderLayer();
	}

	@Override
	public NonNullList<ItemStack> onSheared(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		return NonNullList.withSize(1, new ItemStack(this));
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return defaultBlockState().setValue(PERSISTENT, (meta & 4) == 0).setValue(CHECK_DECAY, (meta & 8) > 0);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(PERSISTENT, false);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, CHECK_DECAY, PERSISTENT);
	}

	@Override
	public EnumType getWoodType(int meta) {
		return EnumType.OAK;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if(world.rand.nextInt(160) == 0) {
			if(world.isEmptyBlock(pos.below())) {
				BLParticles.WEEDWOOD_LEAF.spawn(world, pos.getX() + rand.nextFloat(), pos.getY(), pos.getZ() + rand.nextFloat(), ParticleArgs.get().withScale(1.0F + rand.nextFloat() * 1.25F));
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(BlockLeavesBetweenlands.DISTANCE, BlockLeavesBetweenlands.PERSISTENT);		
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if (!worldIn.isClientSide()) {
			if (state.getValue(CHECK_DECAY) && state.getValue(PERSISTENT)) {
				byte logReach = 5;
				int checkRadius = logReach + 1;
				byte cacheSize = 32;
				int cacheSquared = cacheSize * cacheSize;
				int cacheHalf = cacheSize / 2;

				if (this.decayBlockCache == null) {
					this.decayBlockCache = new int[cacheSize * cacheSize * cacheSize];
				}

				//states:
				//0: can sustain leaves
				//-1: can't sustain leaves
				//-2: is leaves block

				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();

				if (worldIn.isAreaLoaded(new BlockPos(x - checkRadius, y - checkRadius, z - checkRadius), new BlockPos(x + checkRadius, y + checkRadius, z + checkRadius))) {
					BlockPos.Mutable BlockPos.Mutable = new BlockPos.Mutable();

					//Pupulate block cache
					for (int xo = -logReach; xo <= logReach; ++xo) {
						for (int yo = -logReach; yo <= logReach; ++yo) {
							for (int zo = -logReach; zo <= logReach; ++zo) {
								BlockState blockState = worldIn.getBlockState(BlockPos.Mutable.setPos(x + xo, y + yo, z + zo));
								Block block = blockState.getBlock();

								if (!block.canSustainLeaves(blockState, worldIn, BlockPos.Mutable.setPos(x + xo, y + yo, z + zo))) {
									if (block.isLeaves(blockState, worldIn, BlockPos.Mutable.setPos(x + xo, y + yo, z + zo))) {
										this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] = -2;
									} else {
										this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] = -1;
									}
								} else {
									this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] = 0;
								}
							}
						}
					}

					//Iterate multiple times over the block cache
					for (int distancePass = 1; distancePass <= logReach; ++distancePass) {
						for (int xo = -logReach; xo <= logReach; ++xo) {
							for (int yo = -logReach; yo <= logReach; ++yo) {
								for (int zo = -logReach; zo <= logReach; ++zo) {
									//If value != distancePass - 1 then it's not connected to a log
									if (this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] == distancePass - 1) {
										//Check for adjacent leaves and set their value to the current distance pass

										if (this.decayBlockCache[(xo + cacheHalf - 1) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] == -2) {
											this.decayBlockCache[(xo + cacheHalf - 1) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] = distancePass;
										}

										if (this.decayBlockCache[(xo + cacheHalf + 1) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] == -2) {
											this.decayBlockCache[(xo + cacheHalf + 1) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] = distancePass;
										}

										if (this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf - 1) * cacheSize + zo + cacheHalf] == -2) {
											this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf - 1) * cacheSize + zo + cacheHalf] = distancePass;
										}

										if (this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf + 1) * cacheSize + zo + cacheHalf] == -2) {
											this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf + 1) * cacheSize + zo + cacheHalf] = distancePass;
										}

										if (this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + (zo + cacheHalf - 1)] == -2) {
											this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + (zo + cacheHalf - 1)] = distancePass;
										}

										if (this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf + 1] == -2) {
											this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf + 1] = distancePass;
										}
									}
								}
							}
						}
					}
				}

				//Get distance to log at center block
				int distanceToLog = this.decayBlockCache[cacheHalf * cacheSquared + cacheHalf * cacheSize + cacheHalf];

				if (distanceToLog >= 0) {
					worldIn.setBlockState(pos, state.setValue(CHECK_DECAY, Boolean.FALSE), 4);
				} else {
					this.removeLeaves(worldIn, pos);
				}
			}
		}
	}

	protected void removeLeaves(World world, BlockPos pos) {
		this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
		world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	}
}