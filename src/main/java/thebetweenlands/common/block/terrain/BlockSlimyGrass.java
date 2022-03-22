package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.Random;

public class BlockSlimyGrass extends Block
{

    public BlockSlimyGrass() {
        super(Material.GRASS);
        setHardness(0.5F);
        setSoundType(SoundType.PLANT);
        setHarvestLevel("shovel", 0);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        //setBlockName("thebetweenlands.slimyGrass");
        setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
        if( !world.isClientSide() ) {
            if( world.getLight(pos.above()) < 4 && world.getBlockLightOpacity(pos.above()) > 2 ) {
                world.setBlockState(pos, BlockRegistry.SLIMY_DIRT.defaultBlockState());
            } else if( world.getLight(pos.above()) >= 9 ) {
                for( int l = 0; l < 4; ++l ) {
                    BlockPos target = pos.offset(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
                    Block block = world.getBlockState(target.above()).getBlock();

                    if( world.getBlockState(target).getBlock() == Blocks.DIRT
                            && world.getBlockState(target).getBlock().getMetaFromState(world.getBlockState(target)) == 0
                            && world.getLight(target.above()) >= 4
                            && world.getBlockLightOpacity(target.above()) <= 2 )
                    {
                        world.setBlockState(target, BlockRegistry.SLIMY_GRASS.defaultBlockState());
                    }
                }
            }
        }
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return BlockRegistry.SLIMY_DIRT.getItemDropped(state, rand, fortune);
    }
}
