package thebetweenlands.common.block.container;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.properties.DirectionProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.property.PropertyBoolUnlisted;
import thebetweenlands.common.block.property.PropertyIntegerUnlisted;
import thebetweenlands.common.entity.mobs.EntityAshSprite;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityMudBrickAlcove;
import thebetweenlands.util.StatePropertyHelper;

public class BlockMudBrickAlcove extends BasicBlock implements ITileEntityProvider {
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

	public static final PropertyIntegerUnlisted LEVEL = new PropertyIntegerUnlisted("level");
	public static final IUnlistedProperty<Boolean> TOP_COBWEB = new PropertyBoolUnlisted("top_cobweb");
	public static final IUnlistedProperty<Boolean> BOTTOM_COBWEB = new PropertyBoolUnlisted("bottom_cobweb");
	public static final IUnlistedProperty<Boolean> SMALL_CANDLE = new PropertyBoolUnlisted("small_candle");
	public static final IUnlistedProperty<Boolean> BIG_CANDLE = new PropertyBoolUnlisted("big_candle");

	public static final IProperty<Boolean> HAS_URN = BooleanProperty.create("urn");

	public BlockMudBrickAlcove() {
		this(Material.ROCK);
	}

	public BlockMudBrickAlcove(Material material) {
		super(material);
		setLightOpacity(255);
		setHardness(0.4f);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);
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
	protected BlockStateContainer createBlockState() {
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
		IExtendedBlockState extended = (IExtendedBlockState) oldState;

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
	public TileEntity createNewTileEntity(World world, int meta) {
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
	public void onBlockAdded(World world, BlockPos pos, BlockState state) {
		TileEntityMudBrickAlcove tile = getBlockEntity(world, pos);
		if (tile != null) {
			tile.setUpGreeble();
		}
		world.sendBlockUpdated(pos, state, state, 3);
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, PlayerEntity player) {
		if (!world.isClientSide()) {
			TileEntityMudBrickAlcove tile = getBlockEntity(world, pos);

			if (tile != null && tile.hasUrn) {
				BlockState state = world.getBlockState(pos);
				RayTraceResult ray = this.rayTrace(pos, player.getPositionEyes(1), player.getPositionEyes(1).add(player.getLookVec().scale(10)), state.getBoundingBox(world, pos));
				
				if(ray != null && state.getValue(FACING) == ray.sideHit) {
					BlockPos offsetPos = pos.offset(ray.sideHit);

					tile.fillInventoryWithLoot(player);
					InventoryHelper.dropInventoryItems(world, offsetPos, tile);
					
					if (world.rand.nextInt(3) == 0) {
						EntityAshSprite entity = new EntityAshSprite (world); //ash sprite here :P
						entity.moveTo(offsetPos.getX() + 0.5D, offsetPos.getY(), offsetPos.getZ() + 0.5D, 0.0F, 0.0F);
						entity.setBoundOrigin(offsetPos);
						world.spawnEntity(entity);
					}
					
					world.playSound(null, pos, blockSoundType.getBreakSound(), SoundCategory.BLOCKS, 0.5F, 1F);
					world.playEvent(null, 2001, pos, Block.getIdFromBlock(BlockRegistry.MUD_FLOWER_POT)); //this will do unless we want specific particles
					
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
