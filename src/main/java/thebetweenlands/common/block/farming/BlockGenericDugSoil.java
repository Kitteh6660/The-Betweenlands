package thebetweenlands.common.block.farming;

import java.util.Iterator;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.block.IDungeonFogBlock;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.IConnectedTextureBlock;
import thebetweenlands.common.item.ItemBlockMeta;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;
import thebetweenlands.common.tile.TileEntityDugSoil;
import thebetweenlands.util.AdvancedStateMap;

public abstract class BlockGenericDugSoil extends BasicBlock implements ITileEntityProvider, ISubtypeItemBlockModelDefinition, IStateMappedBlock, ICustomItemBlock, IConnectedTextureBlock {
	
    public static final BooleanProperty COMPOSTED = BooleanProperty.create("composted");
    public static final BooleanProperty DECAYED = BooleanProperty.create("decayed");
    public static final BooleanProperty FOGGED = BooleanProperty.create("fogged");
    
    private final boolean purified;

    public BlockGenericDugSoil(Material material) {
        this(material, false);
    }

    public BlockGenericDugSoil(boolean purified, Properties properties) {
    	super(properties);
        /*super(material);
        this.setTickRandomly(true);
        this.setSoundType(SoundType.GROUND);
        this.setHardness(0.5F);
        this.setHarvestLevel("shovel", 0);*/
        this.setDefaultState(this.getBlockState().getBaseState().setValue(COMPOSTED, false).setValue(DECAYED, false).setValue(FOGGED, false));
        this.purified = purified;
    }

    public static TileEntityDugSoil getTile(World world, BlockPos pos) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof TileEntityDugSoil) {
            return (TileEntityDugSoil) te;
        }
        return null;
    }
    
    public static boolean copy(World world, BlockPos pos, TileEntityDugSoil from) {
    	TileEntity te = world.getBlockEntity(pos);
        if (te instanceof TileEntityDugSoil) {
            ((TileEntityDugSoil) te).copy(from);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onBlockBreak(BreakEvent event) {
        //Consume compost if non-BL crop is broken
        if (!event.getWorld().isClientSide()) {
            BlockState stateDown = event.getWorld().getBlockState(event.getPos().below());
            if (stateDown.getBlock() instanceof BlockGenericDugSoil) {
                if (event.getState().getBlock() instanceof BlockGenericCrop == false && event.getState().getBlock() instanceof IPlantable) {
                    TileEntityDugSoil te = getTile(event.getWorld(), event.getPos().below());
                    if (te != null) {
                        te.setCompost(Math.max(te.getCompost() - 10, 0));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onHarvestBlock(HarvestDropsEvent event) {
        //Don't drop items except one seed if soil is decayed
        if (!event.getWorld().isClientSide()) {
            BlockState stateDown = event.getWorld().getBlockState(event.getPos().below());
            if (stateDown.getBlock() instanceof BlockGenericDugSoil) {
                if (event.getState().getBlock() instanceof BlockGenericCrop == false && event.getState().getBlock() instanceof IPlantable && stateDown.getValue(DECAYED)) {
                    Iterator<ItemStack> it = event.getDrops().iterator();
                    boolean removeSeeds = false;
                    while (it.hasNext()) {
                        ItemStack stack = it.next();
                        if (!stack.isEmpty()) {
                            if (!removeSeeds && stack.getItem() instanceof ItemSeeds) {
                                removeSeeds = true;
                                continue;
                            }
                            it.remove();
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCropGrow(CropGrowEvent.Pre event) {
        //Don't let crops grow further on decayed soil
        if (!event.getWorld().isClientSide()) {
            BlockState stateDown = event.getWorld().getBlockState(event.getPos().below());
            if (stateDown.getBlock() instanceof BlockGenericDugSoil) {
                if (event.getState().getBlock() instanceof BlockGenericCrop == false && event.getState().getBlock() instanceof IPlantable && stateDown.getValue(DECAYED)) {
                    event.setResult(Result.DENY);
                }
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[]{COMPOSTED, DECAYED, FOGGED}, new IUnlistedProperty[0]));
    }

    @Override
    public int getMetaFromState(BlockState state) {
    	int meta = 0;
    	if(!this.purified && state.getValue(DECAYED)) {
    		meta |= 0b010;
    	} else if(state.getValue(COMPOSTED)) {
    		meta |= 0b001;
    	}
    	if(state.getValue(FOGGED)) {
    		meta |= 0b100;
    	}
        return meta;
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
    	BlockState state = this.defaultBlockState();
    	if(!this.purified && (meta & 0b010) != 0) {
    		state = state.setValue(DECAYED, true);
    	} else if((meta & 0b001) != 0) {
    		state = state.setValue(COMPOSTED, true);
    	}
    	if((meta & 0b100) != 0) {
    		state = state.setValue(FOGGED, true);
    	}
    	return state;
    }

    @Override
    public BlockState getExtendedState(BlockState oldState, IBlockReader worldIn, BlockPos pos) {
        IExtendedBlockState state = (IExtendedBlockState) oldState;
        return this.getExtendedConnectedTextureState(state, worldIn, pos, p -> worldIn.getBlockState(p).getBlock() instanceof BlockGenericDugSoil /*TODO: Add canConnectTo similar to fence?*/, false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int getSubtypeNumber() {
        return this.purified ? 2 : 3;
    }

    @Override
    public String getSubtypeName(int meta) {
        switch (meta) {
            default:
            case 0:
                return "%s";
            case 1:
                return "%s_composted";
            case 2:
                return "%s_decayed";
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        if (!this.purified)
            list.add(new ItemStack(this, 1, 2));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setStateMapper(AdvancedStateMap.Builder builder) {
        builder.ignore(COMPOSTED).ignore(DECAYED).withPropertySuffix(COMPOSTED, null, "composted")
                .withPropertySuffixExclusions((map) -> {
                    //Exclude COMPOSTED && DECAYED because that will never be used
                    if (map != null && map.getValue(COMPOSTED) != null && map.getValue(DECAYED) != null && map.getValue(COMPOSTED) && map.getValue(DECAYED))
                        return ImmutableList.of(COMPOSTED, DECAYED);
                    return ImmutableList.of();
                });
        if (!this.purified) {
            builder.withPropertySuffixTrue(DECAYED, "decayed");
        }
    }

    @Override
    public BlockItem getItemBlock() {
        return new ItemBlockMeta(this);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(this, 1, this.getMetaFromState(state));
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileEntityDugSoil) {
            TileEntityDugSoil te = (TileEntityDugSoil) tile;
            if (state.getValue(COMPOSTED)) {
                te.setCompost(30);
            }
            if (state.getValue(DECAYED)) {
                te.setDecay(20);
                te.setCompost(30);
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDugSoil();
    }

    @Override
    public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
        if (!world.isClientSide()) {
        	state = this.updateFoggedState(world, pos, state);
        	
            TileEntityDugSoil te = getTile(world, pos);

            if (te != null) {
                if (!this.purified && te.isComposted() && !te.isFullyDecayed() && rand.nextFloat() <= this.getDecayChance(world, pos, state, rand)) {
                    te.setDecay(te.getDecay() + 1);
                }

                if (te.isComposted()) {
                    BlockState stateUp = world.getBlockState(pos.above());
                    
                    if (stateUp.getBlock() instanceof IFarmablePlant) {
                        IFarmablePlant plant = (IFarmablePlant) stateUp.getBlock();
                        
                        if (plant.isFarmable(world, pos.above(), stateUp)) {
                            BlockPos offsetPos = pos.above();
                            
                            switch (rand.nextInt(4)) {
                                case 0:
                                    offsetPos = offsetPos.north();
                                    break;
                                case 1:
                                    offsetPos = offsetPos.south();
                                    break;
                                case 2:
                                    offsetPos = offsetPos.east();
                                    break;
                                case 3:
                                    offsetPos = offsetPos.west();
                                    break;
                            }
                            
                            float spreadChance = plant.getSpreadChance(world, pos.above(), stateUp, offsetPos, rand);
                            
                            if(state.getValue(FOGGED)) {
                            	spreadChance *= 2;
                            }
                            
                            if (rand.nextFloat() <= spreadChance && plant.canSpreadTo(world, pos.above(), stateUp, offsetPos, rand)) {
                                plant.spreadTo(world, pos.above(), stateUp, offsetPos, rand);
                                te.setCompost(Math.max(te.getCompost() - plant.getCompostCost(world, pos.above(), stateUp, rand), 0));
                            }
                        }
                    }
                }

                if (te.isFullyDecayed()) {
                    for (int i = 0; i < 1 + rand.nextInt(6); i++) {
                        BlockPos offsetPos = pos.above();
                        switch (rand.nextInt(5)) {
                            case 0:
                                offsetPos = offsetPos.north();
                                break;
                            case 1:
                                offsetPos = offsetPos.south();
                                break;
                            case 2:
                                offsetPos = offsetPos.east();
                                break;
                            case 3:
                                offsetPos = offsetPos.west();
                                break;
                        }
                        BlockState stateOffset = world.getBlockState(offsetPos);
                        if (stateOffset.getBlock() instanceof IFarmablePlant) {
                            IFarmablePlant plant = (IFarmablePlant) stateOffset.getBlock();
                            if (plant.isFarmable(world, offsetPos, stateOffset)) {
                                plant.decayPlant(world, offsetPos, stateOffset, rand);
                            }
                        }
                    }

                    //Spread decay
                    for (int xo = -1; xo <= 1; xo++) {
                        for (int zo = -1; zo <= 1; zo++) {
                            if ((xo == 0 && zo == 0) || (zo != 0 && xo != 0) || rand.nextInt(3) != 0) {
                                continue;
                            }
                            BlockPos offset = pos.offset(xo, 0, zo);
                            BlockState offsetState = world.getBlockState(offset);
                            if (offsetState.getBlock() instanceof BlockGenericDugSoil) {
                                BlockGenericDugSoil dugDirt = (BlockGenericDugSoil) offsetState.getBlock();
                                if (!dugDirt.purified) {
                                    TileEntityDugSoil offsetTe = getTile(world, offset);
                                    if (offsetTe != null && !offsetTe.isFullyDecayed() && offsetTe.isComposted()) {
                                        offsetTe.setDecay(offsetTe.getDecay() + 5);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
	 * Updates the fogged state, i.e. whether a nearby censer is producing fog or not. Pretty much
	 * like farmland and water.
	 * @param world
	 * @param pos
	 * @param state
	 * @return
	 */
	protected BlockState updateFoggedState(World world, BlockPos pos, BlockState state) {
		boolean shouldBeFogged = false;

		for(BlockPos.Mutable checkPos : BlockPos.getAllInBoxMutable(pos.offset(-6, 0, -6), pos.offset(6, 1, 6))) {
			if(world.isLoaded(checkPos)) {
				BlockState offsetState = world.getBlockState(checkPos);
				Block offsetBlock = offsetState.getBlock();
				if(offsetBlock instanceof IDungeonFogBlock && ((IDungeonFogBlock) offsetBlock).isCreatingDungeonFog(world, checkPos, offsetState)) {
					shouldBeFogged = true;
					break;
				}
			}
		}

		if(shouldBeFogged != state.getValue(FOGGED)) {
			state = state.setValue(FOGGED, shouldBeFogged);
			world.setBlock(pos, state, 3);
		}

		return state;
	}
    
    /**
     * Returns the decay chance
     *
     * @param world
     * @param pos
     * @param state
     * @param rand
     * @return
     */
    protected float getDecayChance(World world, BlockPos pos, BlockState state, Random rand) {
        return 0.25F;
    }

    @Override
    public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
        ItemStack heldItem = playerIn.getItemInHand(hand);
        TileEntityDugSoil te = getTile(world, pos);
        if (te != null && te.getCompost() == 0 && !heldItem.isEmpty() && EnumItemMisc.COMPOST.isItemOf(heldItem)) {
            if (!world.isClientSide()) {
                world.playSound(null, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.PLAYERS, 1, 0.5f + world.rand.nextFloat() * 0.5f);
                te.setCompost(30);
                if (!playerIn.isCreative()) {
                    heldItem.shrink(1);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    	if(stateIn.getValue(FOGGED)) {
    		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(worldIn, pos.getX() + rand.nextFloat(), pos.getY() + 1, pos.getZ() + rand.nextFloat(), 
    				ParticleArgs.get()
    				.withMotion((rand.nextFloat() - 0.5f) * 0.05f, rand.nextFloat() * 0.02F + 0.005F, (rand.nextFloat() - 0.5f) * 0.05f)
    				.withScale(5.0f)
    				.withColor(1, 1, 1, 0.1f)
    				.withData(80, true, 0.0F, true)));
    	}
    	
        if (stateIn.getValue(DECAYED)) {
            BLParticles.DIRT_DECAY.spawn(worldIn, pos.getX() + rand.nextFloat(), pos.getY() + 1.0F, pos.getZ() + rand.nextFloat());

            for (int i = 0; i < 5; i++) {
                BlockPos offsetPos = pos.above();
                switch (i) {
                    case 0:
                        offsetPos = offsetPos.north();
                        break;
                    case 1:
                        offsetPos = offsetPos.south();
                        break;
                    case 2:
                        offsetPos = offsetPos.east();
                        break;
                    case 3:
                        offsetPos = offsetPos.west();
                        break;
                }
                BlockState stateOffset = worldIn.getBlockState(offsetPos);
                if (stateOffset.getBlock() instanceof IFarmablePlant && ((IFarmablePlant) stateOffset.getBlock()).isFarmable(worldIn, offsetPos, stateOffset)) {
                    BLParticles.DIRT_DECAY.spawn(worldIn, offsetPos.getX() + rand.nextFloat(), offsetPos.getY(), offsetPos.getZ() + rand.nextFloat());
                }
            }
        } else {
            TileEntityDugSoil te = getTile(worldIn, pos);
            if (te.getDecay() >= 11) {
                if (rand.nextInt(Math.max(120 - (te.getDecay() - 11) * 14, 2)) == 0) {
                    BLParticles.DIRT_DECAY.spawn(worldIn, pos.getX() + rand.nextFloat(), pos.getY() + 1.0F, pos.getZ() + rand.nextFloat());

                    for (int i = 0; i < 5; i++) {
                        BlockPos offsetPos = pos.above();
                        switch (i) {
                            case 0:
                                offsetPos = offsetPos.north();
                                break;
                            case 1:
                                offsetPos = offsetPos.south();
                                break;
                            case 2:
                                offsetPos = offsetPos.east();
                                break;
                            case 3:
                                offsetPos = offsetPos.west();
                                break;
                        }
                        BlockState stateOffset = worldIn.getBlockState(offsetPos);
                        if (stateOffset.getBlock() instanceof IFarmablePlant && ((IFarmablePlant) stateOffset.getBlock()).isFarmable(worldIn, offsetPos, stateOffset)) {
                            BLParticles.DIRT_DECAY.spawn(worldIn, offsetPos.getX() + rand.nextFloat(), offsetPos.getY(), offsetPos.getZ() + rand.nextFloat());
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, net.minecraftforge.common.IPlantable plantable) {
        if (super.canSustainPlant(state, world, pos, direction, plantable)) {
            return true;
        }

        PlantType plantType = plantable.getPlantType(world, pos.offset(direction));

        boolean isSoilSuitable = direction == Direction.UP && (state.getValue(DECAYED) || state.getValue(COMPOSTED));

        if (!isSoilSuitable) {
            return false;
        }

        switch (plantType) {
            case Crop:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isFertile(World world, BlockPos pos) {
        return true;
    }
    
    /**
     * Returns whether the soil is purified
     * @param world
     * @param pos
     * @param state
     * @return
     */
    public boolean isPurified(World world, BlockPos pos, BlockState state) {
    	return this.purified;
    }
    
    /**
     * Returns how often crops can be harvested before the purified soil turns into unpurified soil
     * @param world
     * @param pos
     * @param state
     * @return
     */
    public int getPurifiedHarvests(World world, BlockPos pos, BlockState state) {
    	return 3;
    }
    
    /**
     * Returns the unpurified variant of this soil.
     * This should also copy the {@link #COMPOSTED} and {@link #DECAYED}
     * properties.
     * @param world
     * @param pos
     * @param state
     * @return
     */
    public abstract BlockState getUnpurifiedDugSoil(World world, BlockPos pos, BlockState state);
}
