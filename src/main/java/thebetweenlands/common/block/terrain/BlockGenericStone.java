package thebetweenlands.common.block.terrain;

import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.EnumProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockGenericStone extends Block implements BlockRegistry.ICustomItemBlock, BlockRegistry.ISubtypeItemBlockModelDefinition{
	public static final EnumProperty<EnumStoneType> VARIANT = EnumProperty.<EnumStoneType>create("variant", EnumStoneType.class);

	public BlockGenericStone() {
		super(Material.ROCK);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		this.registerDefaultState(this.defaultBlockState().setValue(VARIANT, EnumStoneType.CORRUPT_BETWEENSTONE));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		for (EnumStoneType type : EnumStoneType.values())
			list.add(new ItemStack(this, 1, type.getMetadata()));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	protected ItemStack createStackedBlock(BlockState state) {
		return new ItemStack(Item.getItemFromBlock(this), 1, ((EnumStoneType)state.getValue(VARIANT)).getMetadata());
	}

	@Override
	public int damageDropped(BlockState state) {
		return ((EnumStoneType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(VARIANT, EnumStoneType.byMetadata(meta));
	}

	@Override
	public BlockItem getItemBlock() {
		return ItemBlockEnum.create(this, EnumStoneType.class);
	}

	public static enum EnumStoneType implements IStringSerializable {
		CORRUPT_BETWEENSTONE;

		private final String name;

		private EnumStoneType() {
			this.name = this.name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		public static EnumStoneType byMetadata(int metadata) {
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}

		@Override
		public ITextComponent getName() {
			return this.name;
		}
	}

	@Override
	public int getSubtypeNumber() {
		return EnumStoneType.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return EnumStoneType.byMetadata(meta).getName();
	}
}
