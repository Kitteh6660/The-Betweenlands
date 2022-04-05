package thebetweenlands.common.block.container;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.entity.mobs.EntityAshSprite;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityMudBrickAlcove;
import thebetweenlands.util.StatePropertyHelper;

public class BlockMudBrickAlcove extends Block implements ITileEntityProvider {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

	public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 6);
	public static final BooleanProperty TOP_COBWEB = BooleanProperty.create("top_cobweb");
	public static final BooleanProperty BOTTOM_COBWEB = BooleanProperty.create("bottom_cobweb");
	public static final BooleanProperty SMALL_CANDLE = BooleanProperty.create("small_candle");
	public static final BooleanProperty BIG_CANDLE = BooleanProperty.create("big_candle");

	public static final BooleanProperty HAS_URN = BooleanProperty.create("urn");

	public BlockMudBrickAlcove(Properties properties) {
		super(properties);
		/*super(material);
		setLightOpacity(255);
		setHardness(0.4f);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);*/
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HAS_URN, true));
	}

	@Nullable
	public static TileEntityMudBrickAlcove getBlockEntity(IBlockReader world, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if(tile instanceof TileEntityMudBrickAlcove) {
			return (TileEntityMudBrickAlcove) tile;
		}
		return null;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new ExtendedBlockState(this, new IProperty[] {FACING, HAS_URN}, new IUnlistedProperty[] {LEVEL, TOP_COBWEB, BOTTOM_COBWEB, SMALL_CANDLE, BIG_CANDLE});
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		TileEntityMudBrickAlcove tile = StatePropertyHelper.getTileEntityThreadSafe(worldIn, pos, TileEntityMudBrickAlcove.class);
		if(tile != null) {
			state = state.setValue(HAS_URN, tile.hasUrn);
		}
		return state;
	}

	@Override
	public BlockState getExtendedState(BlockState oldState, IBlockReader worldIn, BlockPos pos) {
		BlockState extended = (BlockState) oldState;

		TileEntityMudBrickAlcove tile = StatePropertyHelper.getTileEntityThreadSafe(worldIn, pos, TileEntityMudBrickAlcove.class);
		if(tile != null) {
			extended = extended.setValue(TOP_COBWEB, tile.topWeb)
					.setValue(BOTTOM_COBWEB, tile.bottomWeb)
					.setValue(SMALL_CANDLE, tile.smallCandle)
					.setValue(BIG_CANDLE, tile.bigCandle)
					.setValue(LEVEL, tile.dungeonLevel);
		}

		return extended;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return new TileEntityMudBrickAlcove();
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return defaultBlockState().setValue(FACING, Direction.byHorizontalIndex(meta));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
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
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void onPlace(World world, BlockPos pos, BlockState state) {
		TileEntityMudBrickAlcove tile = getBlockEntity(world, pos);
		if (tile != null) {
			tile.setUpGreeble();
		}
		world.sendBlockUpdated(pos, state, state, 3);
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, PlayerEntity player) {
		if (!level.isClientSide()) {
			TileEntityMudBrickAlcove tile = getBlockEntity(world, pos);

			if (tile != null && tile.hasUrn) {
				BlockState state = world.getBlockState(pos);
				RayTraceResult ray = this.rayTrace(pos, player.getPositionEyes(1), player.getPositionEyes(1).add(player.getLookVec().scale(10)), state.getBoundingBox(world, pos));
				
				if(ray != null && state.getValue(FACING) == ray.sideHit) {
					BlockPos offsetPos = pos.offset(ray.sideHit);

					tile.fillInventoryWithLoot(player);
					InventoryHelper.dropInventoryItems(world, offsetPos, tile);
					
					if (world.random.nextInt(3) == 0) {
						EntityAshSprite entity = new EntityAshSprite (world); //ash sprite here :P
						entity.moveTo(offsetPos.getX() + 0.5D, offsetPos.getY(), offsetPos.getZ() + 0.5D, 0.0F, 0.0F);
						entity.setBoundOrigin(offsetPos);
						world.addFreshEntity(entity);
					}
					
					world.playLocalSound(null, pos, soundType.getBreakSound(), SoundCategory.BLOCKS, 0.5F, 1F);
					world.levelEvent(null, 2001, pos, Block.getIdFromBlock(BlockRegistry.MUD_FLOWER_POT)); //this will do unless we want specific particles
					
					tile.hasUrn = false;
					world.sendBlockUpdated(pos, state, state, 2);
				}
			}
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader world, BlockState state, BlockPos pos, Direction face) {
		Direction facing = state.getValue(FACING);
		return facing.getOpposite() == face ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		Direction facing = base_state.getValue(FACING);
		return facing.getOpposite() == side;
	}
}
