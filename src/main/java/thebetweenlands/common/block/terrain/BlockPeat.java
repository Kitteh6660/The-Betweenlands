package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
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
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.armor.RubberBootsItem;

public class BlockPeat extends Block {
	private static final AxisAlignedBB PEAT_AABB = Block.box(0, 0, 0, 1, 1 - 0.125, 1);

	public BlockPeat() {
		super(Material.GROUND);
		setHardness(0.5F);
		setSoundType(SoundType.GROUND);
		setHarvestLevel("shovel", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
		return PEAT_AABB;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		boolean canWalk = entity instanceof PlayerEntity && ((PlayerEntity) entity).inventory.armor.get(0).getItem() instanceof RubberBootsItem;
		if(!canWalk) {
			entity.motionX *= 0.85D;
			entity.motionY *= 0.85D;
			entity.motionZ *= 0.85D;
		}
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 0;
	}
}
