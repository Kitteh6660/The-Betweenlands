package thebetweenlands.common.block.plant;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockBasicHanger extends BlockBush {

    public BlockBasicHanger() {
        super(Material.PLANTS);
        setHardness(0.0F);
        setCreativeTab(BLCreativeTabs.PLANTS);
        setSoundType(SoundType.PLANT);
        setTickRandomly(true);

    }

    @Override
    public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
        if (world.isEmptyBlock(pos.below()) && canBlockStay(world, pos.below(), state) && rand.nextInt(8) == 0)
            world.setBlockState(pos.below(), this.defaultBlockState());
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Nullable
    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return isValidBlock(worldIn.getBlockState(pos.below())) && canBlockStay(worldIn, pos, worldIn.getBlockState(pos));
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, BlockState state) {
        return isValidBlock(worldIn.getBlockState(pos.above()));
    }

    protected boolean isValidBlock(BlockState block) {
        return block.getMaterial().blocksMovement() || block.getBlock() == BlockRegistry.LEAVES_WEEDWOOD_TREE || block.getBlock() instanceof BlockBasicHanger;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
        return Block.box(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
    }
}
