package thebetweenlands.common.block.container;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
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
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.item.ItemBlockEnum.IGenericMetaSelector;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityLootInventory;
import thebetweenlands.common.tile.TileEntityLootUrn;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockLootUrn extends BasicBlock implements ITileEntityProvider, ICustomItemBlock, ISubtypeItemBlockModelDefinition, IStateMappedBlock {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Plane.HORIZONTAL);
	public static final PropertyEnum<EnumLootUrn> VARIANT = PropertyEnum.create("type", EnumLootUrn.class);

	public BlockLootUrn(Properties properties) {
		super(properties);
		/*super(material);
		setHardness(0.4f);
		setSoundType(SoundType.GLASS);
		setHarvestLevel("pickaxe", 0);*/
		this.setDefaultState(this.blockState.getBaseState().setValue(FACING, Direction.NORTH).setValue(VARIANT, EnumLootUrn.URN_1));
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
	public EnumOffsetType getOffsetType() {
		return EnumOffsetType.XZ;
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
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityLootUrn();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumLootUrn.URN_1.getMetadata(Direction.SOUTH)));
		list.add(new ItemStack(this, 1, EnumLootUrn.URN_2.getMetadata(Direction.SOUTH)));
		list.add(new ItemStack(this, 1, EnumLootUrn.URN_3.getMetadata(Direction.SOUTH)));
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
	protected BlockStateContainer createBlockState() {
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
		state = state.setValue(VARIANT, EnumLootUrn.byMetadata(stack.getItemDamage()));
		worldIn.setBlockState(pos, state, 3);
		TileEntity tile = worldIn.getBlockEntity(pos);
		if (tile instanceof TileEntityLootUrn) {
			tile.setChanged();
		}
	}

	@Override
	public ActionResultType use(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
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
							worldIn.spawnEntity(item);
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
	public void onPlayerDestroy(World worldIn, BlockPos pos, BlockState state) {
		if (!worldIn.isClientSide()) {
			if (worldIn.rand.nextInt(3) == 0) {
				EntityTermite entity = new EntityTermite(worldIn);
				entity.getEntityAttribute(EntityTermite.SMALL).setBaseValue(1);
				entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
				worldIn.spawnEntity(entity);
			}
		}
		super.onPlayerDestroy(worldIn, pos, state);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	public enum EnumLootUrn implements IStringSerializable, IGenericMetaSelector {
		URN_1("1"),
		URN_2("2"),
		URN_3("3");

		private final String name;

		private EnumLootUrn(String name) {
			this.name = name.toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata(Direction facing) {
			return facing.getHorizontalIndex() | (this.ordinal() << 2);
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumLootUrn byMetadata(int metadata) {
			metadata >>= 2;
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public boolean isMetadataMatching(int meta) {
			return byMetadata(meta) == this;
		}
	}

	@Override
	public int getSubtypeNumber() {
		return EnumLootUrn.values().length * 4;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + EnumLootUrn.byMetadata(meta).getName();
	}

	@Override
	public BlockItem getItemBlock() {
		return ItemBlockEnum.create(this, EnumLootUrn.class);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(Builder builder) {
		builder.ignore(VARIANT).withPropertySuffix(VARIANT, e -> e.getName());
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return false;
	}
}
