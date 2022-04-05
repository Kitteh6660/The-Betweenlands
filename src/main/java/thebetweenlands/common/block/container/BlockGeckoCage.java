package thebetweenlands.common.block.container;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectItem;
import thebetweenlands.api.aspect.DiscoveryContainer;
import thebetweenlands.api.aspect.DiscoveryContainer.AspectDiscovery;
import thebetweenlands.api.aspect.DiscoveryContainer.AspectDiscovery.EnumDiscoveryResult;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityGeckoCage;
import thebetweenlands.util.TranslationHelper;

public class BlockGeckoCage extends ContainerBlock {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

	public BlockGeckoCage(Properties properties) {
		super(properties);
		/*super(Material.WOOD);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Override
	public boolean hasCustomBreakingProgress(BlockState state) {
		return true;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(FACING, Direction.byHorizontalIndex(meta));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[]{FACING});
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		int rotation = MathHelper.floor(placer.yRot * 4.0F / 360.0F + 0.5D) & 3;
		state = state.setValue(FACING, Direction.byHorizontalIndex(rotation).getOpposite());
		worldIn.setBlockState(pos, state, 3);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!level.isClientSide()) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof TileEntityGeckoCage) {
				TileEntityGeckoCage tile = (TileEntityGeckoCage) te;
				if (tile.hasGecko()) {
					EntityGecko gecko = new EntityGecko(world);
					gecko.setHealth(tile.getGeckoUsages());
					gecko.moveTo(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, 0.0F, 0.0F);
					if (tile.getGeckoName() != null && !tile.getGeckoName().isEmpty())
						gecko.setCustomNameTag(tile.getGeckoName());
					world.addFreshEntity(gecko);
					gecko.playLivingSound();
					if (player instanceof ServerPlayerEntity)
						AdvancementCriterionRegistry.GECKO_TRIGGER.trigger((ServerPlayerEntity) player, false, true);
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected void spawnHeartParticles(World world, BlockPos pos) {
		for (int i = 0; i < 7; ++i) {
			double d0 = world.rand.nextGaussian() * 0.02D;
			double d1 = world.rand.nextGaussian() * 0.02D;
			double d2 = world.rand.nextGaussian() * 0.02D;
			world.addParticle(ParticleTypes.HEART, pos.getX() + world.rand.nextFloat(), pos.getY() + world.rand.nextFloat(), pos.getZ() + world.rand.nextFloat(), d0, d1, d2, new int[0]);
		}
	}
	
	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand,  Direction side, BlockRayTraceResult hitResult) {
		ItemStack heldItemStack = player.getItemInHand(hand);
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof TileEntityGeckoCage) {
			TileEntityGeckoCage tile = (TileEntityGeckoCage) te;

			if(player.isCrouching())
				return false;

			if(!heldItemStack.isEmpty()) {

				Item heldItem = heldItemStack.getItem();
				if(ItemRegistry.CRITTER.isCapturedEntity(heldItemStack, EntityGecko.class)) {
					if(!tile.hasGecko()) {
						if(!level.isClientSide()) {
							Entity gecko = ItemRegistry.CRITTER.createCapturedEntity(world, pos.getX(), pos.getY(), pos.getZ(), heldItemStack);
							if(gecko instanceof EntityGecko) {
								tile.addGecko((int)((EntityGecko) gecko).getHealth(), gecko.hasCustomName() ? gecko.getCustomNameTag() : null);
								if(!player.isCreative())
									heldItemStack.shrink(1);
							}
						}
						return true;
					}
					return false;
				}
				if(heldItem == ItemRegistry.SAP_SPIT && tile.hasGecko() && tile.getGeckoUsages() < 12) {
					if(!level.isClientSide()) {
						tile.setGeckoUsages(12);
						if(!player.isCreative())
							heldItemStack.shrink(1);
					} else {
						this.spawnHeartParticles(world, pos);
					}
					return true;
				} else if(tile.getAspectType() == null) {
					if(tile.hasGecko()) {
						if(DiscoveryContainer.hasDiscoveryProvider(player)) {
							if(!level.isClientSide()) {
								AspectManager manager = AspectManager.get(world);
								AspectItem aspectItem = AspectManager.getAspectItem(heldItemStack);
								List<Aspect> aspects = manager.getStaticAspects(aspectItem);
								if(aspects.size() > 0) {
									DiscoveryContainer<?> mergedKnowledge = DiscoveryContainer.getMergedDiscoveryContainer(player);
									AspectDiscovery discovery = mergedKnowledge.discover(manager, aspectItem);
									switch(discovery.result) {
									case NEW:
									case LAST:
										DiscoveryContainer.addDiscoveryToContainers(player, aspectItem, discovery.discovered.type);
										tile.setAspectType(discovery.discovered.type, 600);
										if (player instanceof ServerPlayerEntity) {
											AdvancementCriterionRegistry.GECKO_TRIGGER.trigger((ServerPlayerEntity) player, true, false);
											if (discovery.result == EnumDiscoveryResult.LAST && DiscoveryContainer.getMergedDiscoveryContainer(player).haveDiscoveredAll(manager))
												AdvancementCriterionRegistry.HERBLORE_FIND_ALL.trigger((ServerPlayerEntity) player);
										}
										player.displayClientMessage(new TranslationTextComponent("chat.aspect.discovery." + discovery.discovered.type.getName().toLowerCase()), false);
										if(discovery.result == EnumDiscoveryResult.LAST) {
                                            player.displayClientMessage(new TranslationTextComponent("chat.aspect.discovery.last"), true);
                                            player.displayClientMessage(new TranslationTextComponent("chat.aspect.discovery.last"), false);
                                        } else {
                                            player.displayClientMessage(new TranslationTextComponent("chat.aspect.discovery.more"), true);
                                            player.displayClientMessage(new TranslationTextComponent("chat.aspect.discovery.more"), false);
                                        }
										if(!player.isCreative())
                                            heldItemStack.shrink(1);
										return true;
									case END:
										//already all discovered
										player.displayClientMessage(new TranslationTextComponent("chat.aspect.discovery.end"), true);
										return false;
									default:
										//no aspects
										player.displayClientMessage(new TranslationTextComponent("chat.aspect.discovery.none"), true);
										return false;
									}
								} else {
									player.displayClientMessage(new TranslationTextComponent("chat.aspect.discovery.none"), true);
									return true;
								}
							} else {
								//no aspects
								return false;
							}
						} else {
							//no herblore book
							if(!level.isClientSide()) 
								player.displayClientMessage(new TranslationTextComponent("chat.aspect.discovery.book.none"), true);
							return false;
						}
					} else {
						//no gecko
						if(!level.isClientSide()) 
							player.displayClientMessage(new TranslationTextComponent("chat.aspect.discovery.gecko.none"), true);
						return false;
					}
				} else {
					//recovering
					if(!level.isClientSide()) 
						player.displayClientMessage(new TranslationTextComponent("chat.aspect.discovery.gecko.recovering"), true);
					return false;
				}
			}
		}

		return false;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader level) {
		return new TileEntityGeckoCage();
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
