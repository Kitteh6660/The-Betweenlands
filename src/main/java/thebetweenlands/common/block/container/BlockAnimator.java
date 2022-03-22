package thebetweenlands.common.block.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.DirectionProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.tile.TileEntityAnimator;

public class BlockAnimator extends ContainerBlock {
	public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;

	public BlockAnimator() {
		super(Material.ROCK);
		setHardness(2.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityAnimator();
	}


	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand){
		return this.defaultBlockState().setValue(FACING, placer.getDirection());
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlockState(pos, state.setValue(FACING, placer.getDirection()), 2);
	}

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, BlockRayTraceResult hitResult){
		if (world.isClientSide()) {
			return true;
		}
		if (world.getBlockEntity(pos) instanceof TileEntityAnimator) {
			TileEntityAnimator animator = (TileEntityAnimator) world.getBlockEntity(pos);
			if (!animator.itemAnimated) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ANIMATOR, world, pos.getX(), pos.getY(), pos.getZ());
			} else {
				IAnimatorRecipe recipe = AnimatorRecipe.getRecipe(animator.itemToAnimate);
				if (recipe == null || recipe.onRetrieved(player, pos, animator.itemToAnimate)) {
					player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ANIMATOR, world, pos.getX(), pos.getY(), pos.getZ());
				}
				animator.fuelConsumed = 0;
			}
			animator.itemToAnimate = ItemStack.EMPTY;
			animator.itemAnimated = false;
		}

		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, BlockState state) {
		TileEntity tileEntity = world.getBlockEntity(pos);

		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(world, pos, (IInventory)tileEntity);
			world.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		TileEntityAnimator te = (TileEntityAnimator) worldIn.getBlockEntity(pos);
		if (te != null && te.isRunning()) {
			int meta = te.getBlockMetadata();

			double xOff = 0;
			double zOff = 0;

			switch (meta) {
			case 0:
				xOff = -0.5F;
				zOff = 0.14F;
				break;
			case 1:
				xOff = -0.14F;
				zOff = -0.5F;
				break;
			case 2:
				xOff = 0.5F;
				zOff = -0.14F;
				break;
			case 3:
				xOff = 0.14F;
				zOff = 0.5F;
				break;
			}

			// Runes
			List<Vector3d> points = new ArrayList<Vector3d>();
			points.add(new Vector3d(te.getPos().getX() + 0.5D + (rand.nextFloat() - 0.5F) * 0.3D + xOff, te.getPos().getY() + 0.9, te.getPos().getZ() + 0.5 + (rand.nextFloat() - 0.5F) * 0.3D + zOff));
			points.add(new Vector3d(te.getPos().getX() + 0.5D + (rand.nextFloat() - 0.5F) * 0.3D + xOff, te.getPos().getY() + 1.36, te.getPos().getZ() + 0.5 + (rand.nextFloat() - 0.5F) * 0.3D + zOff));
			points.add(new Vector3d(te.getPos().getX() + 0.5D, te.getPos().getY() + 1.45D, te.getPos().getZ() + 0.5D));
			BLParticles.ANIMATOR.spawn(worldIn, te.getPos().getX(), te.getPos().getY() + 0.9, te.getPos().getZ() + 0.65, ParticleArgs.get().withData(points));
			BLParticles.SMOKE.spawn(worldIn, te.getPos().getX() + 0.5 + rand.nextFloat() * 0.3D - 0.15D, te.getPos().getY() + 0.3, te.getPos().getZ() + 0.5 + rand.nextFloat() * 0.3D - 0.15D);
		}
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(FACING, Direction.byHorizontalIndex(meta));
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World world, BlockPos pos) {
		TileEntity tileEntityAnimator = world.getBlockEntity(pos);
		if (tileEntityAnimator instanceof TileEntityAnimator ) {
			return Math.round(((float) ((TileEntityAnimator) tileEntityAnimator).fuelConsumed / (float) ((TileEntityAnimator) tileEntityAnimator).requiredFuelCount) * 16.0f);
		}
		return 0;
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
}