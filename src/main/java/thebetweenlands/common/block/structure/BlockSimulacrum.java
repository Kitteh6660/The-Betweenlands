package thebetweenlands.common.block.structure;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.DirectionProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ItemBlockEnum.IGenericMetaSelector;
import thebetweenlands.common.item.ItemRenamableBlockEnum;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;
import thebetweenlands.common.tile.TileEntitySimulacrum;
import thebetweenlands.util.AdvancedStateMap.Builder;
import thebetweenlands.util.NBTHelper;

public class BlockSimulacrum extends ContainerBlock implements IStateMappedBlock, ICustomItemBlock, ISubtypeItemBlockModelDefinition {
	protected static final AxisAlignedBB AABB = Block.box(0.15D, 0.0D, 0.15D, 0.85D, 1.0D, 0.85D);

	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

	public BlockSimulacrum(Material material, SoundType soundType) {
		super(material);
		this.setHardness(10.0F);
		this.setResistance(10000.0F);
		this.setSoundType(soundType);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, VARIANT);
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		return AABB;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(VARIANT, Variant.byMetadata(meta)).setValue(FACING, Direction.byHorizontalIndex(meta & 3));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(VARIANT).getMetadata(state.getValue(FACING));
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		int rotation = MathHelper.floor(placer.yRot * 4.0F / 360.0F + 0.5D + 2) & 3;
		state = state.setValue(FACING, Direction.byHorizontalIndex(rotation));
		state = state.setValue(VARIANT, Variant.byMetadata(stack.getItemDamage()));
		worldIn.setBlockState(pos, state, 3);

		TileEntity tile = worldIn.getBlockEntity(pos);
		if(tile instanceof TileEntitySimulacrum) {
			((TileEntitySimulacrum) tile).setEffect(TileEntitySimulacrum.Effect.byId(NBTHelper.getStackNBTSafe(stack).getInt("simulacrumEffectId")));
			((TileEntitySimulacrum) tile).setActive(true);
			if(stack.hasDisplayName()) {
				((TileEntitySimulacrum) tile).setCustomName(stack.getDisplayName());
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
		return this.defaultBlockState();
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.isSideSolid(pos.below(), Direction.UP);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, BlockState state) {
		if(!worldIn.isSideSolid(pos.below(), Direction.UP)) {
			this.dropAt(worldIn, pos, state);
			worldIn.setBlockState(pos, Blocks.AIR.defaultBlockState(), 3);
		}
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
		return new TileEntitySimulacrum();
	}

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(VARIANT).withPropertySuffix(VARIANT, v -> v.name);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, Variant.ONE.getMetadata(Direction.NORTH)));
		list.add(new ItemStack(this, 1, Variant.TWO.getMetadata(Direction.NORTH)));
		list.add(new ItemStack(this, 1, Variant.THREE.getMetadata(Direction.NORTH)));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, BlockState state) {
		ItemStack stack = new ItemStack(this, 1, state.getValue(VARIANT).getMetadata(Direction.NORTH));

		TileEntity tile = worldIn.getBlockEntity(pos);
		if(tile instanceof TileEntitySimulacrum) {
			CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
			nbt.putInt("simulacrumEffectId", ((TileEntitySimulacrum) tile).getEffect().id);
		}

		return stack;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
	}

	@Override
	public List<ItemStack> getDrops(IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		return Collections.emptyList();
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if(!worldIn.isClientSide() && !player.isCreative() && worldIn.getGameRules().getBoolean("doTileDrops")) {
			this.dropAt(worldIn, pos, state);
		}
	}
	
	private void dropAt(World worldIn, BlockPos pos, BlockState state) {
		ItemStack stack = new ItemStack(this, 1, state.getValue(VARIANT).getMetadata(Direction.NORTH));

		TileEntity tile = worldIn.getBlockEntity(pos);
		if(tile instanceof TileEntitySimulacrum) {
			if(!((TileEntitySimulacrum) tile).isActive()) {
				worldIn.playSound(null, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.SIMULACRUM_BREAK, SoundCategory.BLOCKS, 1, 0.95f + worldIn.rand.nextFloat() * 0.1f);
			}
			
			CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
			nbt.putInt("simulacrumEffectId", ((TileEntitySimulacrum) tile).getEffect().id);

			String customName = ((TileEntitySimulacrum) tile).getCustomName();
			if(customName.length() > 0) {
				stack.setStackDisplayName(customName);
			}
		}

		InventoryHelper.spawnItemStack(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
	}

	@Override
	public int getSubtypeNumber() {
		return Variant.values().length * 4;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + Variant.byMetadata(meta).getName();
	}

	@Override
	public BlockItem getItemBlock() {
		BlockItem item = ItemRenamableBlockEnum.create(this, Variant.class);
		item.setMaxStackSize(1);
		return item;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCreative()) {
			tooltip.add(I18n.get("tooltip.bl.simulacrum.effect", I18n.get("tooltip.bl.simulacrum.effect." + TileEntitySimulacrum.Effect.byId(NBTHelper.getStackNBTSafe(stack).getInt("simulacrumEffectId")).name)));
		}
	}

	@Override
	public ActionResultType use(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		if(hand == Hand.MAIN_HAND && playerIn.isCreative() && playerIn.isCrouching()) {

			if(!worldIn.isClientSide()) {
				TileEntity tile = worldIn.getBlockEntity(pos);

				if(tile instanceof TileEntitySimulacrum) {
					TileEntitySimulacrum simulacrum = (TileEntitySimulacrum) tile;

					TileEntitySimulacrum.Effect nextEffect = TileEntitySimulacrum.Effect.values()[(simulacrum.getEffect().ordinal() + 1) % TileEntitySimulacrum.Effect.values().length];

					simulacrum.setEffect(nextEffect);

					playerIn.sendStatusMessage(new TranslationTextComponent("chat.simulacrum.changed_effect", new TranslationTextComponent("tooltip.bl.simulacrum.effect." + nextEffect.name)), true);
				}
			}

			playerIn.swingArm(hand);

			return true;
		}

		return false;
	}

	public enum Variant implements IStringSerializable, IGenericMetaSelector {
		ONE("1"),
		TWO("2"),
		THREE("3");

		private final String name;

		private Variant(String name) {
			this.name = name.toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata(Direction facing) {
			return facing.getHorizontalIndex() | (this.ordinal() << 2);
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static Variant byMetadata(int metadata) {
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
}
