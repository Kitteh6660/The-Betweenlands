package thebetweenlands.common.block.terrain;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.armor.RubberBootsItem;

import javax.annotation.Nullable;

public class BlockSilt extends BasicBlock {
	private static final AxisAlignedBB BOUNDING_BOX = Block.box(0, 0, 0, 1, 1 - 0.125F, 1);

	public BlockSilt(Properties properties) {
		super(properties);
		/*super(Material.SAND);
		setHardness(0.5F);
		setSoundType(SoundType.SAND);
		setHarvestLevel("shovel", 0);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
		return BOUNDING_BOX;
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		boolean canWalk = entityIn instanceof PlayerEntity && !((PlayerEntity)entityIn).inventory.armor.get(0).isEmpty()
				&& ((PlayerEntity)entityIn).inventory.armor.get(0).getItem() instanceof RubberBootsItem;
		if(!(entityIn instanceof IEntityBL) && !canWalk) {
			entityIn.xo *= 0.4D;
			entityIn.zo *= 0.4D;
		}
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, net.minecraftforge.common.IPlantable plantable) {
		if(super.canSustainPlant(state, world, pos, direction, plantable)) {
			return true;
		}

		PlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		switch(plantType) {
		case Beach:
			boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
			world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
			world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
			world.getBlockState(pos.south()).getMaterial() == Material.WATER);
			return hasWater;
		case Plains:
			return true;
		default:
			return false;
		}
	}
}
