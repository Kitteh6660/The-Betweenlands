package thebetweenlands.common.block.plant;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockMoss extends BlockDirectional implements IShearable, ISickleHarvestable, ITintedBlock {
    protected static final AxisAlignedBB MOSS_UP_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 0.2D, 1.0D);
    protected static final AxisAlignedBB MOSS_DOWN_AABB = Block.box(0.0D, 0.8D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB MOSS_WEST_AABB = Block.box(0.8D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB MOSS_EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 0.2D, 1.0D, 1.0D);
    protected static final AxisAlignedBB MOSS_SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.2D);
    protected static final AxisAlignedBB MOSS_NORTH_AABB = Block.box(0.0D, 0.0D, 0.8D, 1.0D, 1.0D, 1.0D);

    protected ItemStack sickleHarvestableDrop;
    protected boolean isReplaceable = false;

    public final boolean spreading;
    
    public BlockMoss(boolean spreading) {
        super(Material.PLANTS);
        this.spreading = spreading;
        this.setHardness(0.2F);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(BLCreativeTabs.PLANTS);
        this.setTickRandomly(true);
    }

    public BlockMoss setSickleDrop(ItemStack drop) {
        this.sickleHarvestableDrop = drop;
        return this;
    }

    public BlockMoss setReplaceable(boolean replaceable) {
        this.isReplaceable = replaceable;
        return this;
    }

    @Override
    public boolean isReplaceable(IBlockReader worldIn, BlockPos pos) {
        return this.isReplaceable;
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return this.defaultBlockState().setValue(FACING, Direction.byIndex(meta));
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
        if (this.canPlaceAt(world, pos, facing)) {
            return this.defaultBlockState().setValue(FACING, facing);
        } else {
            for (Direction Direction : Direction.VALUES) {
                if (world.isSideSolid(pos.offset(Direction.getOpposite()), Direction, true)) {
                    return this.defaultBlockState().setValue(FACING, Direction);
                }
            }
            return this.defaultBlockState();
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (Direction Direction : FACING.getAllowedValues()) {
            if (this.canPlaceAt(worldIn, pos, Direction)) {
                return true;
            }
        }

        return false;
    }

    private boolean canPlaceAt(World worldIn, BlockPos pos, Direction facing) {
        BlockPos blockPos = pos.offset(facing.getOpposite());
        boolean flag = facing.getAxis().isHorizontal();
        return flag && worldIn.isSideSolid(blockPos, facing, true) || ((facing.equals(Direction.DOWN) || facing.equals(Direction.UP)) && this.canPlaceOn(worldIn, blockPos));
    }

    private boolean canPlaceOn(World worldIn, BlockPos pos) {
        BlockState state = worldIn.getBlockState(pos);
        if (state.isSideSolid(worldIn, pos, Direction.UP)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Nullable
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockReader world, BlockPos pos) {
        return item.getItem() == ItemRegistry.SYRMORITE_SHEARS;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
        return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
    }

    @Override
    public boolean isHarvestable(ItemStack item, IBlockReader world, BlockPos pos) {
        return true;
    }

    @Override
    public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
        return this.sickleHarvestableDrop != null ? ImmutableList.of(this.sickleHarvestableDrop.copy()) : ImmutableList.of();
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (this.checkForDrop(worldIn, pos, state)) {
            Direction facing = (Direction) state.getValue(FACING);
            Direction.Axis axis = facing.getAxis();
            Direction oppositeFacing = facing.getOpposite();
            boolean shouldDrop = false;
            if (axis.isHorizontal() && !worldIn.isSideSolid(pos.offset(oppositeFacing), facing, true)) {
                shouldDrop = true;
            } else if (axis.isVertical() && !this.canPlaceOn(worldIn, pos.offset(oppositeFacing))) {
                shouldDrop = true;
            }
            if (shouldDrop) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, BlockState state) {
        this.checkForDrop(worldIn, pos, state);
    }

    protected boolean checkForDrop(World worldIn, BlockPos pos, BlockState state) {
        if (state.getBlock() == this && this.canPlaceAt(worldIn, pos, (Direction) state.getValue(FACING))) {
            return true;
        } else {
            if (worldIn.getBlockState(pos).getBlock() == this) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
            return false;
        }
    }

    @Override
    public BlockState withRotation(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate((Direction) state.getValue(FACING)));
    }

    @Override
    public BlockState withMirror(BlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation((Direction) state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING});
    }

    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
        switch ((Direction) state.getValue(FACING)) {
            default:
            case EAST:
                return MOSS_EAST_AABB;
            case WEST:
                return MOSS_WEST_AABB;
            case SOUTH:
                return MOSS_SOUTH_AABB;
            case NORTH:
                return MOSS_NORTH_AABB;
            case UP:
                return MOSS_UP_AABB;
            case DOWN:
                return MOSS_DOWN_AABB;
        }
    }


    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
	public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
    	if(this.spreading) {
	    	BlockPos.Mutable checkPos = new BlockPos.Mutable();
			byte radius = 2;
	    	int attempt = 0;
			for (int xx = pos.getX() - radius; xx <= pos.getX() + radius; ++xx) {
				for (int zz = pos.getZ() - radius; zz <= pos.getZ() + radius; ++zz) {
					for (int yy = pos.getY() - radius; yy <= pos.getY() + radius; ++yy) {
						if (!world.isBlockLoaded(checkPos.setPos(xx, yy, zz))) {
							return;
						}
					}
				}
			}
			if (rand.nextInt(3) == 0) {
				int maxNearbyMossBlocks = 6;
				for (int xx = pos.getX() - radius; xx <= pos.getX() + radius; ++xx) {
					for (int zz = pos.getZ() - radius; zz <= pos.getZ() + radius; ++zz) {
						for (int yy = pos.getY() - radius; yy <= pos.getY() + radius; ++yy) {
							if (world.getBlockState(checkPos.setPos(xx, yy, zz)).getBlock() == this) {
								--maxNearbyMossBlocks;
								if (maxNearbyMossBlocks <= 0) {
									return;
								}
							}
						}
					}
				}
				for (attempt = 0; attempt < 30; attempt++) {
					int xx = pos.getX() + rand.nextInt(3) - 1;
					int yy = pos.getY() + rand.nextInt(3) - 1;
					int zz = pos.getZ() + rand.nextInt(3) - 1;
					int offsetDir = 0;
					if (xx != pos.getX()) offsetDir++;
					if (yy != pos.getY()) offsetDir++;
					if (zz != pos.getZ()) offsetDir++;
					if (offsetDir > 1)
						continue;
					BlockPos offsetPos = new BlockPos(xx, yy, zz);
					if (world.isEmptyBlock(offsetPos)) {
						Direction facing = Direction.byIndex(rand.nextInt(Direction.VALUES.length));
						Direction.Axis axis = facing.getAxis();
						Direction oppositeFacing = facing.getOpposite();
						boolean isInvalid = false;
						if (axis.isHorizontal() && !world.isSideSolid(offsetPos.offset(oppositeFacing), facing, true)) {
							isInvalid = true;
						} else if (axis.isVertical() && !this.canPlaceOn(world, offsetPos.offset(oppositeFacing))) {
							isInvalid = true;
						}
						if (!isInvalid) {
							world.setBlockState(offsetPos, this.defaultBlockState().setValue(BlockMoss.FACING, facing));
							break;
						}
					}
				}
			} else if(rand.nextInt(20) == 0) {
				world.setBlockToAir(pos);
			}
    	}
	}
    
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }

    @Override
	public int getColorMultiplier(BlockState state, IBlockReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
	}
}
