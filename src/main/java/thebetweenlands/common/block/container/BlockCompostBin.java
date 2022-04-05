package thebetweenlands.common.block.container;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.tile.TileEntityCompostBin;


public class BlockCompostBin extends Block {
	
	public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;

	public BlockCompostBin(Properties properties) {
		super(properties);
		/*super(Material.WOOD);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
		return this.defaultBlockState().setValue(FACING, placer.getDirection().rotateYCCW());
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlockState(pos, state.setValue(FACING, placer.getDirection().rotateYCCW()), 2);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader level) {
		return new TileEntityCompostBin();
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction facing = Direction.byIndex(meta);

		if (facing.getAxis() == Direction.Axis.Y) {
			facing = Direction.NORTH;
		}

		return this.defaultBlockState().setValue(FACING, facing);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, PlayerEntity playerIn) {
		if(!level.isClientSide()) {
			if (world.getBlockEntity(pos) instanceof TileEntityCompostBin) {
				TileEntityCompostBin tile = (TileEntityCompostBin) world.getBlockEntity(pos);
				tile.setOpen(!tile.isOpen());
				world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
				tile.setChanged();
			}
		}
	}
	
	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand,  Direction side, BlockRayTraceResult hitResult) {
		ItemStack heldItem = player.getItemInHand(hand);

		if (!level.isClientSide()) {
			if (world.getBlockEntity(pos) instanceof TileEntityCompostBin) {
				TileEntityCompostBin tile = (TileEntityCompostBin) world.getBlockEntity(pos);
				boolean open = tile.isOpen();

				boolean interacted = false;
				
				if(open) {
					if(tile.getCompostedAmount() > 0) {
						if (tile.removeCompost(TileEntityCompostBin.COMPOST_PER_ITEM)) {
							world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), EnumItemMisc.COMPOST.create(1)));
							interacted = true;
						}
					}
					
					if(!interacted && !heldItem.isEmpty()) {
						ICompostBinRecipe compostRecipe = CompostRecipe.getCompostRecipe(heldItem);
						if (compostRecipe != null) {
							int amount = compostRecipe.getCompostAmount(heldItem);
							int time = compostRecipe.getCompostingTime(heldItem);
							switch (tile.addItemToBin(heldItem, amount, time, true)) {
							case 1:
								tile.addItemToBin(heldItem, amount, time, false);
								if (!player.isCreative()) {
									player.getItemInHand(hand).shrink(1);
								}
								break;
							case -1:
							default:
								player.displayClientMessage(new TranslationTextComponent("chat.compost.full"), true);
								break;
							}
						} else {
							player.displayClientMessage(new TranslationTextComponent("chat.compost.not.compostable"), true);
						}
						interacted = true;
					}
				}
			}
		}
		
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
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);

		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileEntity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public void animateTick(BlockState stateIn, World world, BlockPos pos, Random rand) {
		if (world.getBlockEntity(pos) instanceof TileEntityCompostBin) {
			TileEntityCompostBin tile = (TileEntityCompostBin) world.getBlockEntity(pos);
			if(!tile.isOpen() && !tile.isEmpty()) {
				BLParticles.DIRT_DECAY.spawn(world, pos.getX() + 0.2F + rand.nextFloat() * 0.62F, pos.getY() + rand.nextFloat() * 0.75F, pos.getZ() + 0.2F + rand.nextFloat() * 0.6F);
			}
		}
	}
}
