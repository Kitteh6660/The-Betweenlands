package thebetweenlands.common.block.container;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.TileEntityWindChime;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class BlockWindChime extends ContainerBlock {
	protected static final AxisAlignedBB AABB = Block.box(0.15D, 0.0D, 0.15D, 0.85D, 1.0D, 0.85D);

	public BlockWindChime() {
		super(Material.WOOD);
		this.setSoundType(SoundType.WOOD);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		setHardness(0.5F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityWindChime();
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && (worldIn.isSideSolid(pos.above(), Direction.DOWN) || worldIn.getBlockState(pos.above()).getBlockFaceShape(worldIn, pos.above(), Direction.DOWN) != BlockFaceShape.UNDEFINED);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, BlockState state) {
		if(!worldIn.isSideSolid(pos.above(), Direction.DOWN) && worldIn.getBlockState(pos.above()).getBlockFaceShape(worldIn, pos.above(), Direction.DOWN) == BlockFaceShape.UNDEFINED) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.defaultBlockState(), 3);
		}
	}

	@Override
	public ActionResultType use(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		if(hand == Hand.MAIN_HAND && playerIn.isCrouching()) {

			if(!worldIn.isClientSide()) {
				TileEntity tile = worldIn.getBlockEntity(pos);

				if(tile instanceof TileEntityWindChime) {
					TileEntityWindChime chime = (TileEntityWindChime) tile;

					ResourceLocation newAttunement = chime.cycleAttunedEvent();

					IEnvironmentEvent attunedEvent;
					if(newAttunement != null) {
						attunedEvent = BetweenlandsWorldStorage.forWorld(worldIn).getEnvironmentEventRegistry().getEvent(newAttunement);
					} else {
						attunedEvent = null;
					}

					if(newAttunement != null) {
						playerIn.sendStatusMessage(new TranslationTextComponent("chat.wind_chime.changed_attunement", new TranslationTextComponent(attunedEvent.getLocalizationEventName())), true);
					} else {
						playerIn.sendStatusMessage(new TranslationTextComponent("chat.wind_chime.removed_attunement"), true);
					}
				}
			}

			playerIn.swingArm(hand);

			return true;
		}

		return false;
	}
}
