package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;

import java.util.Collections;
import java.util.List;

public class BlockFallenLeaves extends BushBlock implements IForgeShearable, ISickleHarvestable {
	
	private static final VoxelShape BOUNDS = Block.box(0.0F, 0.0F, 0.0F, 1.0F, 0.05F, 1.0F);

	public BlockFallenLeaves(Properties properties) {
		super(properties);
		/*this.setHardness(0.1F);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(BLCreativeTabs.PLANTS);
		this.type = blockName;
		this.setTranslationKey("thebetweenlands." + type);*/
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext context) {
		return BOUNDS;
	}

	@Override
	public boolean isShearable(ItemStack item, World world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> onSheared(PlayerEntity player, ItemStack item, World world, BlockPos pos, int fortune) {
		return Collections.singletonList(new ItemStack(this));
	}

	@Override
	public boolean isHarvestable(ItemStack item, IWorldReader world, BlockPos pos) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return Collections.singletonList(EnumItemPlantDrop.GENERIC_LEAF.create(1));
	}
}