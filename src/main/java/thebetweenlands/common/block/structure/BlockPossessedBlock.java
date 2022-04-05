package thebetweenlands.common.block.structure;

import java.util.Random;

import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityPossessedBlock;

public class BlockPossessedBlock extends ContainerBlock {
	
    public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;

    public BlockPossessedBlock(Properties properties) {
    	super(properties);
        //super(Material.ROCK);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        /*setSoundType(SoundType.STONE);
        setHardness(10F);
        setResistance(2000.0F);
        setCreativeTab(BLCreativeTabs.BLOCKS);*/
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new TileEntityPossessedBlock();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return defaultBlockState().setValue(FACING, Direction.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public BlockState withRotation(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState withMirror(BlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
        return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(BlockRegistry.BETWEENSTONE_BRICKS);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

}