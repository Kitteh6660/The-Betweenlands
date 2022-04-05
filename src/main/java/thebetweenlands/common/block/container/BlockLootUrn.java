package thebetweenlands.common.block.container;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.wrapper.InvWrapper;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityLootInventory;
import thebetweenlands.common.tile.TileEntityLootUrn;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockLootUrn extends Block {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Plane.HORIZONTAL);

	public BlockLootUrn(Properties properties) {
		super(properties);
		/*super(material);
		setHardness(0.4f);
		setSoundType(SoundType.GLASS);
		setHarvestLevel("pickaxe", 0);*/
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
	}
	
	@Nullable
	public static TileEntityLootUrn getBlockEntity(IBlockReader world, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if(tile instanceof TileEntityLootUrn) {
			return (TileEntityLootUrn) tile;
		}
		return null;
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
	public OffsetType getOffsetType() {
		return OffsetType.XZ;
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
	public TileEntity newBlockEntity(IBlockReader level) {
		return new TileEntityLootUrn();
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(this, 1, ((EnumLootUrn) state.getValue(VARIANT)).getMetadata(Direction.NORTH));
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(VARIANT, EnumLootUrn.byMetadata(meta)).setValue(FACING, Direction.byHorizontalIndex(meta & 3));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return ((EnumLootUrn) state.getValue(VARIANT)).getMetadata(state.getValue(FACING));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[]{VARIANT, FACING});
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		int rotation = MathHelper.floor(placer.yRot * 4.0F / 360.0F + 0.5D) & 3;
		state = state.setValue(FACING, Direction.byHorizontalIndex(rotation));
		state = state.setValue(VARIANT, EnumLootUrn.byMetadata(stack.getDamageValue()));
		worldIn.setBlockState(pos, state, 3);
		TileEntity tile = worldIn.getBlockEntity(pos);
		if (tile instanceof TileEntityLootUrn) {
			tile.setChanged();
		}
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hitResult) {
		if(!worldIn.isClientSide()) {
			if (worldIn.getBlockEntity(pos) instanceof TileEntityLootUrn) {
				TileEntityLootUrn tile = (TileEntityLootUrn) worldIn.getBlockEntity(pos);
				InvWrapper wrapper = new InvWrapper(tile);
				if (!playerIn.getItemInHand(hand).isEmpty()) {
					ItemStack stack = playerIn.getItemInHand(hand);
					ItemStack prevStack = stack.copy();
					for(int i = 0; i < wrapper.getSlots() && !stack.isEmpty(); i++) {
						stack = wrapper.insertItem(i, stack, false);
					}
					if(stack.isEmpty() || stack.getCount() != prevStack.getCount()) {
						if(!playerIn.isCreative()) {
							playerIn.setItemInHand(hand, stack);
						}
						return true;
					}
				} else if(playerIn.isCrouching() && hand == Hand.MAIN_HAND) {
					for(int i = 0; i < wrapper.getSlots(); i++) {
						ItemStack extracted = wrapper.extractItem(i, 1, false);
						if(!extracted.isEmpty()) {
							ItemEntity item = new ItemEntity(worldIn, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
							item.motionX = item.motionY = item.motionZ = 0D;
							worldIn.addFreshEntity(item);
							return true;
						}
					}
				}
			}
		} else {
			return true;
		}
		return false;
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		IInventory tile = (IInventory) worldIn.getBlockEntity(pos);
		if (tile != null) {
			((TileEntityLootInventory) tile).fillInventoryWithLoot(player);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		IInventory tile = (IInventory) worldIn.getBlockEntity(pos);
		if (tile != null) {
			InventoryHelper.dropInventoryItems(worldIn, pos, tile);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!worldIn.isClientSide()) {
			if (worldIn.random.nextInt(3) == 0) {
				EntityTermite entity = new EntityTermite(worldIn);
				entity.getAttribute(EntityTermite.SMALL).setBaseValue(1);
				entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
				worldIn.addFreshEntity(entity);
			}
		}
		super.playerWillDestroy(worldIn, pos, state, player);
	}

}
