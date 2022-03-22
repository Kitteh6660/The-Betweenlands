package thebetweenlands.common.block.container;

import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.tools.ItemBucketInfusion;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAlembic;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockAlembic extends ContainerBlock implements IWaterLoggable
{
    public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;

    public BlockAlembic(Properties properties) {
    	super(properties);
        /*super(Material.ROCK);
        setHardness(2.0F);
        setResistance(5.0F);
        setCreativeTab(BLCreativeTabs.BLOCKS);*/
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }


    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand){
        return this.defaultBlockState().setValue(FACING, placer.getDirection());
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        world.setBlock(pos, state.setValue(FACING, placer.getDirection()), 2);
    }

    @Override
    public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, BlockRayTraceResult hitResult){
        if (!world.isClientSide()) {
            if (world.getBlockEntity(pos) instanceof TileEntityAlembic) {
                TileEntityAlembic tile = (TileEntityAlembic) world.getBlockEntity(pos);

                if (player.isCrouching()) {
                    return ActionResultType.PASS;
                }
                if (!player.getItemInHand(hand).isEmpty()) {
                    ItemStack heldStack = player.getItemInHand(hand);
                    if (heldStack.getItem() == ItemRegistry.BL_BUCKET_INFUSION) {
                        if (!tile.isFull()) {
                            tile.addInfusion(heldStack);
                            if (!player.isCreative())
                                player.setItemInHand(hand, ItemBucketInfusion.getEmptyBucket(heldStack));
                        }
                    } else if (heldStack.getItem() == ItemRegistry.DENTROTHYST_VIAL && (heldStack.getItemDamage() == 0 || heldStack.getItemDamage() == 2)) {
                        if (tile.hasFinished()) {
                            ItemStack result = tile.getElixir(heldStack.getItemDamage() == 0 ? 0 : 1);
                            ItemEntity itemEntity = player.dropItem(result, false);
                            if (itemEntity != null) itemEntity.setPickupDelay(0);
                            if (!player.isCreative()) heldStack.shrink(1);
                        }
                    }
                }
            }
        }
        return true;
    }


    @Override
    public void randomDisplayTick(BlockState stateIn, World world, BlockPos pos, Random rand) {
        if (world.getBlockEntity(pos) instanceof TileEntityAlembic) {
            TileEntityAlembic alembic = (TileEntityAlembic) world.getBlockEntity(pos);
            if (alembic.isRunning()) {
                float xx = (float) pos.getX() + 0.5F;
                float yy = (float) (pos.getY() + 0.25F + rand.nextFloat() * 0.5F);
                float zz = (float) pos.getZ() + 0.5F;
                float fixedOffset = 0.25F;
                float randomOffset = rand.nextFloat() * 0.6F - 0.3F;
                BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx - fixedOffset), (double) yy + 0.250D, (double) (zz + randomOffset));
                BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + fixedOffset), (double) yy + 0.250D, (double) (zz + randomOffset));
                BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + randomOffset), (double) yy + 0.250D, (double) (zz - fixedOffset));
                BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + randomOffset), (double) yy + 0.250D, (double) (zz + fixedOffset));
                Direction facing = (Direction) stateIn.getProperties().get(FACING);
                switch (facing) {
                    case NORTH:
                        BLParticles.FLAME.spawn(world, pos.getX() + 0.65F + (rand.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + 0.6F + (rand.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((rand.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
                        break;
                    case SOUTH:
                        BLParticles.FLAME.spawn(world, pos.getX() + 0.375F + (rand.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + 0.375F + (rand.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((rand.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
                        break;
                    case EAST:
                        BLParticles.FLAME.spawn(world, pos.getX() + 0.375F + (rand.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + 0.6F + (rand.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((rand.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
                        break;
                    case WEST:
                        BLParticles.FLAME.spawn(world, pos.getX() + 0.6F + (rand.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + 0.375F + (rand.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((rand.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
                        break;
                }
            }
        }
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new TileEntityAlembic();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return defaultBlockState().setValue(FACING, Direction.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(FACING).getIndex();
    }
    
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
