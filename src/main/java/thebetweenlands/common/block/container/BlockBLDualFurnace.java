package thebetweenlands.common.block.container;

import java.util.Random;

import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityBLDualFurnace;

public class BlockBLDualFurnace extends ContainerBlock {
	
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;
   	private static boolean keepInventory;

    public BlockBLDualFurnace(Properties properties) {
    	super(properties);
        /*super(Material.ROCK);
        this.isOnFire = isOnFire;
        setHardness(3.5F);
        setSoundType(SoundType.STONE);
        if(!isOnFire)
        	setCreativeTab(BLCreativeTabs.BLOCKS);*/
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

	@Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(BlockRegistry.SULFUR_FURNACE_DUAL);
    }

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return true;
	}

	@Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public void onPlace(World worldIn, BlockPos pos, BlockState state) {
        setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(World worldIn, BlockPos pos, BlockState state) {
        if (!worldIn.isClientSide()) {
            BlockState iblockstate = worldIn.getBlockState(pos.north());
            BlockState iblockstate1 = worldIn.getBlockState(pos.south());
            BlockState iblockstate2 = worldIn.getBlockState(pos.west());
            BlockState iblockstate3 = worldIn.getBlockState(pos.east());
            Direction Direction = (Direction)state.getValue(FACING);

            if (Direction == Direction.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock())
                Direction = Direction.SOUTH;

            else if (Direction == Direction.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock())
                Direction = Direction.NORTH;

            else if (Direction == Direction.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock())
                Direction = Direction.EAST;

            else if (Direction == Direction.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock())
                Direction = Direction.WEST;

            worldIn.setBlockState(pos, state.setValue(FACING, Direction), 2);
        }
    }

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, BlockRayTraceResult hitResult) {

		if (world.isClientSide())
			return true;
		else {
			TileEntityBLDualFurnace tileentityfurnace = (TileEntityBLDualFurnace)world.getBlockEntity(pos);
            if (tileentityfurnace != null)
            	player.openGui(TheBetweenlands.instance, CommonProxy.GUI_BL_DUAL_FURNACE, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
		}
	}

	public static void setState(boolean active, World world, BlockPos pos) {
		BlockState iblockstate = world.getBlockState(pos);
		TileEntity tileentity = world.getBlockEntity(pos);
		keepInventory = true;

		if (active)
			world.setBlockState(pos, BlockRegistry.SULFUR_FURNACE_DUAL_ACTIVE.defaultBlockState().setValue(FACING, iblockstate.getValue(FACING)), 3);
		else
			world.setBlockState(pos, BlockRegistry.SULFUR_FURNACE_DUAL.defaultBlockState().setValue(FACING, iblockstate.getValue(FACING)), 3);

		keepInventory = false;

		if (tileentity != null) {
			tileentity.validate();
			world.setTileEntity(pos, tileentity);
		}
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
        return new TileEntityBLDualFurnace();
    }

	public BlockState onBlockPlaced(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		return this.defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlockState(pos, state.setValue(FACING, placer.getDirection().getOpposite()), 2);

		if (stack.hasDisplayName())
			((TileEntityBLDualFurnace)world.getBlockEntity(pos)).setStackDisplayName(stack.getDisplayName());
	}

	@Override
    public void breakBlock(World world, BlockPos pos, BlockState state) {
		 if (!keepInventory) {
            TileEntity tileentity = world.getBlockEntity(pos);

            if (tileentity instanceof TileEntityBLDualFurnace) {
                InventoryHelper.dropInventoryItems(world, pos, (TileEntityBLDualFurnace)tileentity);
                world.updateComparatorOutputLevel(pos, this);
            }
        }
        super.breakBlock(world, pos, state);
    }

	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("incomplete-switch")
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (this.isOnFire) {
			Direction Direction = (Direction) stateIn.getValue(FACING);
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = (double) pos.getY() + 0.25D + rand.nextDouble() * 6.0D / 16.0D;
			double d2 = (double) pos.getZ() + 0.5D;
			double d3 = 0.52D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;

			if (rand.nextDouble() < 0.1D) {
				worldIn.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}

			switch (Direction) {
			case WEST:
				worldIn.addParticle(ParticleTypes.SMOKE, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
				worldIn.addParticle(ParticleTypes.FLAME, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
				break;
			case EAST:
				worldIn.addParticle(ParticleTypes.SMOKE, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
				worldIn.addParticle(ParticleTypes.FLAME, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
				break;
			case NORTH:
				worldIn.addParticle(ParticleTypes.SMOKE, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
				worldIn.addParticle(ParticleTypes.FLAME, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
				break;
			case SOUTH:
				worldIn.addParticle(ParticleTypes.SMOKE, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
				worldIn.addParticle(ParticleTypes.FLAME, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World world, BlockPos pos) {
		return Container.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
	}

	@Override
	public ItemStack getItem(World world, BlockPos pos, BlockState state) {
		return new ItemStack(BlockRegistry.SULFUR_FURNACE_DUAL.get());
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction Direction = Direction.byIndex(meta);

		if (Direction.getAxis() == Axis.Y) {
			Direction = Direction.NORTH;
		}

		return defaultBlockState().setValue(FACING, Direction);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return ((Direction) state.getValue(FACING)).getIndex();
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
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return face == Direction.UP ? BlockFaceShape.CENTER_SMALL : BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public BlockItem getItemBlock() {
		if(this.isOnFire) {
			return null;
		}
		return ICustomItemBlock.getDefaultItemBlock(this);
	}
}