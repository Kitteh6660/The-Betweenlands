package thebetweenlands.common.block.container;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.ITileEntityProvider;
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
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.wrapper.InvWrapper;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityLootInventory;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockLootPot extends Block implements ITileEntityProvider, ICustomItemBlock, ISubtypeItemBlockModelDefinition, IStateMappedBlock {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
	public static final EnumProperty<EnumLootPot> VARIANT = EnumProperty.create("type", EnumLootPot.class);

	public BlockLootPot(Properties properties) {
		super(properties);
		/*super(material);
		setHardness(0.4f);
		setSoundType(SoundType.GLASS);
		setHarvestLevel("pickaxe", 0);*/
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(VARIANT, EnumLootPot.POT_1));
	}
	
	@Nullable
	public static TileEntityLootPot getBlockEntity(IBlockReader world, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if(tile instanceof TileEntityLootPot) {
			return (TileEntityLootPot) tile;
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
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(BlockState state) {
		return false;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader level) {
		return new TileEntityLootPot();
	}

	//TODO: Remove this and apply the Flattening.
	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumLootPot.POT_1.getMetadata(Direction.SOUTH)));
		list.add(new ItemStack(this, 1, EnumLootPot.POT_2.getMetadata(Direction.SOUTH)));
		list.add(new ItemStack(this, 1, EnumLootPot.POT_3.getMetadata(Direction.SOUTH)));
	}

	@Override
	public ItemStack getItem(World level, BlockPos pos, BlockState state) {
		return new ItemStack(this, 1, ((EnumLootPot) state.getValue(VARIANT)).getMetadata(Direction.NORTH));
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(VARIANT, EnumLootPot.byMetadata(meta)).setValue(FACING, Direction.byHorizontalIndex(meta & 3));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return ((EnumLootPot) state.getValue(VARIANT)).getMetadata(state.getValue(FACING));
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
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		int rotation = MathHelper.floor(placer.yRot * 4.0F / 360.0F + 0.5D) & 3;
		state = state.setValue(FACING, Direction.byHorizontalIndex(rotation));
		state = state.setValue(VARIANT, EnumLootPot.byMetadata(stack.getDamageValue()));
		level.setBlockState(pos, state, 3);
		TileEntity tile = level.getBlockEntity(pos);
		if (tile instanceof TileEntityLootPot) {
			((TileEntityLootPot) tile).setModelRotationOffset(level.random.nextInt(41) - 20);
			tile.setChanged();
		}
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hitResult) {
		if(!level.isClientSide()) {
			if (level.getBlockEntity(pos) instanceof TileEntityLootPot) {
				TileEntityLootPot tile = (TileEntityLootPot) level.getBlockEntity(pos);
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
							ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
							item.motionX = item.motionY = item.motionZ = 0D;
							level.addFreshEntity(item);
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
	public void harvestBlock(World level, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		super.harvestBlock(level, player, pos, state, te, stack);
		IInventory tile = (IInventory) level.getBlockEntity(pos);
		if (tile != null) {
			((TileEntityLootInventory) tile).fillInventoryWithLoot(player);
		}
	}

	@Override
	public void breakBlock(World level, BlockPos pos, BlockState state) {
		IInventory tile = (IInventory) level.getBlockEntity(pos);
		if (tile != null) {
			InventoryHelper.dropInventoryItems(level, pos, tile);
		}
		super.breakBlock(level, pos, state);
	}

	@Override
	public void onPlayerDestroy(World level, BlockPos pos, BlockState state) {
		if (!level.isClientSide()) {
			if (level.random.nextInt(3) == 0) {
				EntityTermite entity = new EntityTermite(level);
				entity.getAttribute(EntityTermite.SMALL).setBaseValue(1);
				entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
				level.addFreshEntity(entity);
			}
		}
		super.onPlayerDestroy(level, pos, state);
	}

	@Override
	public void onBlockHarvested(World level, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBlockHarvested(level, pos, state, player);
	}

	public enum EnumLootPot implements IStringSerializable, IGenericMetaSelector {
		POT_1("1"),
		POT_2("2"),
		POT_3("3");

		private final String name;

		private EnumLootPot(String name) {
			this.name = name.toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata(Direction facing) {
			return facing.getHorizontalIndex() | (this.ordinal() << 2);
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumLootPot byMetadata(int metadata) {
			metadata >>= 2;
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}

		@Override
		public ITextComponent getName() {
			return this.name;
		}

		@Override
		public boolean isMetadataMatching(int meta) {
			return byMetadata(meta) == this;
		}
	}

	@Override
	public int getSubtypeNumber() {
		return EnumLootPot.values().length * 4;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + EnumLootPot.byMetadata(meta).getName();
	}

	@Override
	public BlockItem getItemBlock() {
		return ItemBlockEnum.create(this, EnumLootPot.class);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(Builder builder) {
		builder.ignore(VARIANT).withPropertySuffix(VARIANT, e -> e.getName());
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader level, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return false;
	}
}
