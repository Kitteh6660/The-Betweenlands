package thebetweenlands.common.block.container;

import javax.annotation.Nullable;

import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.block.terrain.BlockDentrothyst;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAspectVial;

public class BlockAspectVial extends ContainerBlock implements BlockRegistry.ICustomItemBlock {
	
    public static final EnumProperty<BlockDentrothyst.EnumDentrothyst> TYPE = EnumProperty.create("type", BlockDentrothyst.EnumDentrothyst.class);
    public static final BooleanProperty RANDOM_POSITION = BooleanProperty.create("random_position");
    
    public static final VoxelShape BOUNDING_BOX = Block.box(0.25F, 0.0F, 0.25F, 0.95F, 0.45F, 0.95F);

    public BlockAspectVial(Properties properties) {
    	super(properties);
        /*super(Material.GLASS);
        setSoundType(SoundType.GLASS);
        setHardness(0.4F);*/
        registerDefaultState(this.stateDefinition.any().setValue(TYPE, BlockDentrothyst.EnumDentrothyst.GREEN).setValue(RANDOM_POSITION, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
        return Block.box(0.15F, 0.0F, 0.15F, 0.85F, 0.45F, 0.85F);
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
    	BlockDentrothyst.EnumDentrothyst type;
    	switch(meta & 0x1) {
    	default:
    	case 0:
    		type = BlockDentrothyst.EnumDentrothyst.GREEN;
    		break;
    	case 1:
    		type = BlockDentrothyst.EnumDentrothyst.ORANGE;
    		break;
    	}
        return this.defaultBlockState().setValue(TYPE, type).setValue(RANDOM_POSITION, (meta & 0x2) == 0);
    }

    @Override
    public int getMetaFromState(BlockState state) {
    	int meta = 0;
        switch(state.getValue(TYPE)) {
        default:
        case GREEN:
        	break;
        case ORANGE:
        	meta |= 0x1;
        	break;
        }
        if(!state.getValue(RANDOM_POSITION)) {
        	meta |= 0x2;
        }
        return meta;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
        return new BlockStateContainer(this, TYPE, RANDOM_POSITION);
    }

    @Override
	public BlockItem getItemBlock() {
		return null;
	}

    @Override
    public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
        if (world.getBlockEntity(pos) instanceof TileEntityAspectVial) {
            TileEntityAspectVial tile = (TileEntityAspectVial) world.getBlockEntity(pos);

            if (!player.getItemInHand(hand).isEmpty()) {
                ItemStack heldItem = player.getItemInHand(hand);

                ItemAspectContainer container = null;
                if(heldItem.getItem() == ItemRegistry.ASPECT_VIAL && (container = ItemAspectContainer.fromItem(heldItem)).getAspects().size() == 1) {
                    Aspect itemAspect = container.getAspects().get(0);
                    if(!player.isCrouching()) {
                        if(tile.getAspect() == null || tile.getAspect().type == itemAspect.type) {
                            if(!world.isClientSide()) {
                                if(tile.getAspect() == null)
                                    tile.setAspect(new Aspect(itemAspect.type, 0));
                                int added = tile.addAmount(Math.min(itemAspect.amount, 100));
                                if(added > 0) {
                                    int leftAmount = itemAspect.amount - added;
                                    container.set(itemAspect.type, itemAspect.amount - added);
                                    if (leftAmount <= 0) {
                                        int type = heldItem.getDamageValue();
                                        switch(type) {
                                            default:
                                            case 0:
                                                player.setItemInHand(hand, ItemRegistry.DENTROTHYST_VIAL.createStack(0));
                                                break;
                                            case 1:
                                                player.setItemInHand(hand, ItemRegistry.DENTROTHYST_VIAL.createStack(2));
                                                break;
                                        }
                                    }
                                }
                            }
                            player.swing(hand);
                            return ActionResultType.SUCCESS;
                        }
                    } else {
                        if(tile.getAspect() != null && tile.getAspect().type == itemAspect.type) {
                            if(!level.isClientSide()) {
                                int toRemove = (int) Math.min(100, Amounts.VIAL - itemAspect.amount);
                                if(toRemove > 0) {
                                    int removedAmount = tile.removeAmount(toRemove);
                                    container.set(itemAspect.type, itemAspect.amount + removedAmount);
                                }
                            }
                            player.swing(hand);
                            return ActionResultType.SUCCESS;
                        }
                    }
                } else if(heldItem.getItem() == ItemRegistry.DENTROTHYST_VIAL && player.isCrouching() && tile.getAspect() != null && heldItem.getDamageValue() != 1) {
                    if(!level.isClientSide()) {
                        Aspect aspect = tile.getAspect();
                        int removedAmount = tile.removeAmount(100);
                        if(removedAmount > 0) {
                            ItemStack vial = new ItemStack(ItemRegistry.ASPECT_VIAL);
                            switch(heldItem.getDamageValue()) {
                                default:
                                case 0:
                                    vial.setDamageValue(0);
                                    break;
                                case 2:
                                    vial.setDamageValue(1);
                                    break;
                            }
                            container = ItemAspectContainer.fromItem(vial);
                            container.add(aspect.type, removedAmount);

                            heldItem.shrink(1);
                            if(heldItem.getCount() <= 0)
                                player.setItemInHand(hand, heldItem);

                            //Drop new aspect item
                            ItemEntity itemEntity = player.drop(vial, false);
                            if(itemEntity != null) itemEntity.setPickUpDelay(0);
                        }
                    }
                    player.swing(hand);
                    return ActionResultType.SUCCESS;
                }
            } else if(player.isCrouching()) {
            	if(!level.isClientSide()) {
            		world.setBlockState(pos, state.setValue(RANDOM_POSITION, !state.getValue(RANDOM_POSITION)));
            	}
            	player.swing(hand);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new TileEntityAspectVial();
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
        NonNullList<ItemStack> list = NonNullList.create();
        getDrops(list, world, pos, state, 0);
        return list.size() > 0 ? list.get(0): ItemStack.EMPTY;
    }

    @Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		TileEntityAspectVial tile = (TileEntityAspectVial) world.getBlockEntity(pos);
		if(tile != null) {
			if(tile.getAspect() != null) {
				if(tile.getAspect().amount > 0) {
					ItemStack vial = new ItemStack(ItemRegistry.ASPECT_VIAL);
					switch(state.getValue(TYPE)) {
					case ORANGE:
						vial.setDamageValue(1);
						break;
					default:
					case GREEN:
						vial.setDamageValue(0);
						break;
					}
					ItemAspectContainer.fromItem(vial).add(tile.getAspect().type, tile.getAspect().amount);
					drops.add(vial);
				}
			} else {
				ItemStack vial = new ItemStack(ItemRegistry.DENTROTHYST_VIAL);
				switch(state.getValue(TYPE)) {
				case ORANGE:
					vial.setDamageValue(2);
					break;
				default:
				case GREEN:
					vial.setDamageValue(0);
					break;
				}
				drops.add(vial);
			}
		}
	}

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(world.isClientSide()) return;

        player.awardStat(StatList.getBlockStats(this), 1);
        player.causeFoodExhaustion(0.025F);
        if (!world.isClientSide() && !world.restoringBlockSnapshots && !player.isCreative()) {
            NonNullList<ItemStack> drops = NonNullList.create();
            getDrops(drops, world, pos, state, 0);
            float chance = ForgeEventFactory.fireBlockHarvesting(drops, world, pos, world.getBlockState(pos), 0, 1, false, harvesters.get());
            for (ItemStack item : drops) {
                if (world.random.nextFloat() <= chance) {
                    float f = 0.7F;
                    double d0 = (double)(world.random.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d1 = (double)(world.random.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d2 = (double)(world.random.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    ItemEntity ItemEntity = new ItemEntity(world, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, item);
                    ItemEntity.setPickUpDelay(10);
                    world.addFreshEntity(ItemEntity);
                }
            }
        }

        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }
    
    @Override
    public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
    	return false;
    }
    
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
    
    @Override
    public boolean isBlockNormalCube(BlockState state) {
    	return false;
    }
    
	@Override
	public boolean isNormalCube(BlockState state) {
		return false;
	}
    
    @Override
    public boolean isFullBlock(BlockState state)    {
    	return false;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }
    
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
    	return super.canPlaceBlockAt(worldIn, pos) && worldIn.isSideSolid(pos.below(), Direction.UP);
    }
}
