package thebetweenlands.common.block.structure;

import java.util.Locale;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockCarvedMudBrick extends Block {

	public static final EnumProperty<EnumCarvedMudBrickType> VARIANT = EnumProperty.<EnumCarvedMudBrickType>create("variant", EnumCarvedMudBrickType.class);
	
	public BlockCarvedMudBrick(Properties properties) {
		super(properties);
		/*super(Material.ROCK);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
		this.registerDefaultState(this.defaultBlockState().setValue(VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED));
	}

	public static enum EnumCarvedMudBrickType implements IStringSerializable {
		MUD_BRICKS_CARVED,
		MUD_BRICKS_CARVED_DECAY_1,
		MUD_BRICKS_CARVED_DECAY_2,
		MUD_BRICKS_CARVED_DECAY_3,
		MUD_BRICKS_CARVED_DECAY_4,
		MUD_BRICKS_CARVED_EDGE,
		MUD_BRICKS_CARVED_EDGE_DECAY_1,
		MUD_BRICKS_CARVED_EDGE_DECAY_2,
		MUD_BRICKS_CARVED_EDGE_DECAY_3,
		MUD_BRICKS_CARVED_EDGE_DECAY_4,
		MUD_BRICKS_DECAY_1,
		MUD_BRICKS_DECAY_2,
		MUD_BRICKS_DECAY_3,
		MUD_BRICKS_DECAY_4;

		private final String name;

		private EnumCarvedMudBrickType() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumCarvedMudBrickType byMetadata(int metadata) {
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
	public BlockItem getItemBlock() {
		return ItemBlockEnum.create(this, EnumCarvedMudBrickType.class);
	}

	@Override
	public int getSubtypeNumber() {
		return EnumCarvedMudBrickType.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return EnumCarvedMudBrickType.values()[meta].getName();
	}
}