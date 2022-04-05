package thebetweenlands.common.block.terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.item.armor.RubberBootsItem;
import javax.annotation.Nullable;

public class BlockSilt extends Block {
	
	private static final VoxelShape BOUNDING_BOX = Block.box(0, 0, 0, 1, 1 - 0.125F, 1);

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
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return BOUNDING_BOX;
	}

	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		boolean canWalk = entityIn instanceof PlayerEntity && !((PlayerEntity)entityIn).inventory.armor.get(0).isEmpty() && ((PlayerEntity)entityIn).inventory.armor.get(0).getItem() instanceof RubberBootsItem;
		if(!(entityIn instanceof IEntityBL) && !canWalk) {
			entityIn.xo *= 0.4D;
			entityIn.zo *= 0.4D;
		}
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, IPlantable plantable) {
		if(super.canSustainPlant(state, world, pos, direction, plantable)) {
			return true;
		}

		PlantType plantType = plantable.getPlantType(world, pos.relative(direction));
		if (plantType == PlantType.BEACH) {
			boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER || world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
				world.getBlockState(pos.north()).getMaterial() == Material.WATER || world.getBlockState(pos.south()).getMaterial() == Material.WATER);
			return hasWater;
		}
		else if (plantType == PlantType.PLAINS) {
			return true;
		}
		else {
			return false;
		}
	}
}
