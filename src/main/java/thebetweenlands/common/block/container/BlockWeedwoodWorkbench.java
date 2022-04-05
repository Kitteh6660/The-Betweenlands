package thebetweenlands.common.block.container;

import net.minecraft.block.ContainerBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

public class BlockWeedwoodWorkbench extends ContainerBlock {
	
    public BlockWeedwoodWorkbench(Properties properties) {
    	super(properties);
        /*super(Material.WOOD);
        setSoundType(SoundType.WOOD);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        setTranslationKey("thebetweenlands.weedwoodCraftingTable");
        setHardness(2.5F);*/
    }

    @Override
    public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, BlockRayTraceResult hitResult){
        if (!level.isClientSide()) {
            player.openGui(TheBetweenlands.instance, CommonProxy.GUI_WEEDWOOD_CRAFT, world, pos.getX(), pos.getY(), pos.getZ());
        }
        ActionResultType.SUCCESS;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        ((TileEntityWeedwoodWorkbench) world.getBlockEntity(pos)).rotation = (byte) (((MathHelper.floor((double) (placer.yRot * 4.0F / 360.0F) + 0.5D) & 3) + 1) % 4);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new TileEntityWeedwoodWorkbench();
    }
}
