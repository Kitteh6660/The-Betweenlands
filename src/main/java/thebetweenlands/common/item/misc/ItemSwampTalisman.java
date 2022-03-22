package thebetweenlands.common.item.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.block.structure.BlockTreePortal;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.item.IGenericItem;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.gen.feature.structure.WorldGenWeedwoodPortalTree;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationPortal;

public class ItemSwampTalisman extends Item {
	
	public ItemSwampTalisman(Properties properties) {
		super(properties);
		/*this.setMaxDamage(0);
		this.maxStackSize = 1;
		this.setHasSubtypes(true);*/
	}

	/*@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			for (EnumTalisman type : EnumTalisman.values()) {
				if(type != EnumTalisman.SWAMP_TALISMAN_5) {
					items.add(type.create(1));
				}
			}
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		try {
			return "item.thebetweenlands." + IGenericItem.getFromStack(EnumTalisman.class, stack).getTranslationKey();
		} catch (Exception e) {
			return "item.thebetweenlands.unknown_talisman";
		}
	}*/

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (this.getItem() == ItemRegistry.SWAMP_TALISMAN_5.get() && stack.hasTag() && stack.getTag().contains("link", Constants.NBT.TAG_LONG)) {
			BlockPos otherPortalPos = BlockPos.of(stack.getTag().getLong("link"));
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.swamp_talisman_linked", otherPortalPos.getX(), otherPortalPos.getY(), otherPortalPos.getZ()), 0));
		}
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = playerIn.getItemInHand(hand);
		if (!playerIn.mayUseItemAt(pos, facing, stack)) {
			return ActionResultType.FAIL;
		} else {
			BlockState state = worldIn.getBlockState(pos);

			if(this.isPortalWood(state)) {
				BlockPos offsetPos = pos.relative(facing);
				for(int yo = 3; yo > 0; yo--) {
					BlockPos portalPos = offsetPos.below(yo);
					Direction.Axis frameAxis = this.getPortalWoodFrameAxis(worldIn, portalPos);
					if(frameAxis != null) {
						if(!worldIn.isClientSide()) {
							Direction closestDir = null;
							for(Direction dir : Direction.values()) {
								if(dir.getAxis() == frameAxis) {
									if(closestDir == null || pos.relative(dir).distSqr(new Vector3i(playerIn.getX(), playerIn.getY(), playerIn.getZ())) <= pos.relative(closestDir).distSqr(new Vector3i(playerIn.getX(), playerIn.getY(), playerIn.getZ()))) {
										closestDir = dir;
									}
								}
							}
							if(frameAxis == Direction.Axis.X) {
								BlockTreePortal.makePortalX(worldIn, portalPos.above());
							} else if(frameAxis == Direction.Axis.Z) {
								BlockTreePortal.makePortalZ(worldIn, portalPos.above());
							}
							if(frameAxis == Direction.Axis.X && BlockTreePortal.isPatternValidX(worldIn, portalPos.above())
									|| frameAxis == Direction.Axis.Z && BlockTreePortal.isPatternValidZ(worldIn, portalPos.above())) {

								//Only create new location is none exists
								if(this.getPortalAt(worldIn, portalPos.above()) == null) {
									BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(worldIn);
									LocationPortal location = new LocationPortal(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), portalPos.relative(closestDir).below());
									location.addBounds(new AxisAlignedBB(portalPos.above()).inflate(1, 2, 1).expandTowards(0, -0.5D, 0));
									location.setSeed(worldIn.random.nextLong());
									location.setDirty(true);
									location.setVisible(false);
									worldStorage.getLocalStorageHandler().addLocalStorage(location);
								}

								worldIn.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.PORTAL_ACTIVATE, SoundCategory.PLAYERS, 0.5F, random.nextFloat() * 0.4F + 0.8F);
							}
						}
						return ActionResultType.SUCCESS;
					}
				}
			}

			boolean isCustomListed = BetweenlandsConfig.WORLD_AND_DIMENSION.portalDimensionTargetsList.isListed(state);
			boolean sapling = this.isBlockSapling(worldIn, playerIn, state, pos, facing, hitX, hitY, hitZ);
			if ((sapling || isCustomListed) && this.getItem() == ItemRegistry.SWAMP_TALISMAN_0.get() || this.getItem() == ItemRegistry.SWAMP_TALISMAN_5.get()) {
				if (!worldIn.isClientSide()) {
					if(!BetweenlandsConfig.WORLD_AND_DIMENSION.portalDimensionWhitelistSet.isListed(playerIn.level.provider.getDimension())) {
						playerIn.sendMessage(new TranslationTextComponent("chat.talisman.wrongdimension"), true);
					} else {
						WorldGenWeedwoodPortalTree gen;
						if(isCustomListed) {
							int targetDim = BetweenlandsConfig.WORLD_AND_DIMENSION.portalDimensionTargetsList.getDimension(state);
							if(targetDim == playerIn.level.provider.getDimension()) {
								gen = new WorldGenWeedwoodPortalTree();
							} else {
								gen = new WorldGenWeedwoodPortalTree(BetweenlandsConfig.WORLD_AND_DIMENSION.portalDimensionTargetsList.getDimension(state));
							}
						} else {
							gen = new WorldGenWeedwoodPortalTree();
						}
						if(gen.generate(worldIn, random, pos)) {
							worldIn.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.PORTAL_ACTIVATE, SoundCategory.PLAYERS, 0.5F, random.nextFloat() * 0.4F + 0.8F);
							playerIn.moveTo(pos.getX() + 0.5D, pos.getY() + 2D, pos.getZ() + 0.5D, playerIn.yRot, playerIn.xRot);
							if(playerIn instanceof ServerPlayerEntity) {
								((ServerPlayerEntity)playerIn).connection.setPlayerLocation(pos.getX() + 0.5D, pos.getY() + 2D, pos.getZ() + 0.5D, playerIn.yRot, playerIn.xRot);
							}
						} else {
							playerIn.sendMessage(new TranslationTextComponent("chat.talisman.noplace"), true);
						}
					}
				}
				return ActionResultType.SUCCESS;
			} else if(this.getItem() == ItemRegistry.SWAMP_TALISMAN_0.get() && playerIn instanceof FakePlayer == false) {
				LocationPortal portal = this.getPortalAt(worldIn, pos);
				if(portal != null) {
					if(!worldIn.isClientSide()) {
						stack = copyDataFromOtherTalisman(ItemRegistry.SWAMP_TALISMAN_5.get(), stack); // stack.copy();
						playerIn.setItemInHand(hand, stack);

						stack.addTagElement("link", new LongNBT(portal.getPortalPosition().asLong()));
						stack.addTagElement("linkDim", new IntNBT(worldIn.provider.getDimension()));

						playerIn.sendMessage(new TranslationTextComponent("chat.talisman.linked"), true);

						worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.8F, 0.7F);
					}
					return ActionResultType.SUCCESS;
				}
			} else if(this.getItem() == ItemRegistry.SWAMP_TALISMAN_5.get() && playerIn instanceof FakePlayer == false) {
				if(!worldIn.isClientSide()) {
					stack = copyDataFromOtherTalisman(ItemRegistry.SWAMP_TALISMAN_0.get(), stack); // stack.copy();
					playerIn.setItemInHand(hand, stack);

					worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.8F, 0.7F);
				}

				if(stack.hasTag() && stack.getTag().contains("link", Constants.NBT.TAG_LONG) && stack.getTag().contains("linkDim", Constants.NBT.TAG_INT)) {
					BlockPos otherPortalPos = BlockPos.of(stack.getTag().getLong("link"));
					LocationPortal portal = this.getPortalAt(worldIn, pos);
					if(portal != null) {
						if(worldIn instanceof ServerWorld) {
							int linkDim = stack.getTag().getInt("linkDim");
							if(linkDim != worldIn.provider.getDimension() && 
									(linkDim == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId || worldIn.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId)) {
								ServerWorld otherWorld = ((ServerWorld) worldIn).getServer().getWorld(linkDim);
								if(otherWorld != null) {
									double moveFactor = otherWorld.provider.getMovementFactor() / worldIn.provider.getMovementFactor();
									if(new Vector3d(portal.getPortalPosition()).distanceTo(new Vector3d(otherPortalPos.getX() * moveFactor, portal.getPortalPosition().getY(), otherPortalPos.getZ() * moveFactor)) <= BetweenlandsConfig.WORLD_AND_DIMENSION.portalMaxLinkDist) {
										LocationPortal linkPortal = this.getLinkPortal(otherWorld, otherPortalPos);
										if(linkPortal != null) {
											linkPortal.setOtherPortalPosition(worldIn.provider.getDimension(), portal.getPortalPosition());
											portal.setOtherPortalPosition(linkDim, linkPortal.getPortalPosition());
											playerIn.sendMessage(new TranslationTextComponent("chat.talisman.portal_linked"), true);
										} else {
											playerIn.sendMessage(new TranslationTextComponent("chat.talisman.cant_link"), true);
										}
									} else {
										playerIn.sendMessage(new TranslationTextComponent("chat.talisman.too_far", new TextComponent(String.valueOf(BetweenlandsConfig.WORLD_AND_DIMENSION.portalMaxLinkDist))), true);
									}
								}
							} else {
								playerIn.sendMessage(new TranslationTextComponent("chat.talisman.cant_link"), true);
							}
						}
					}
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.FAIL;
	}

	protected LocationPortal getPortalAt(World world, BlockPos pos) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		List<LocationPortal> portals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, pos.getX() + 0.5D, pos.getZ() + 0.5D, location -> location.isInside(new Vector3d(pos.getX(), pos.getY(), pos.getZ()).add(0.5D, 0.5D, 0.5D)));
		if(!portals.isEmpty()) {
			return portals.get(0);
		}
		return null;
	}

	protected LocationPortal getLinkPortal(ServerWorld world, BlockPos portal2Pos) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		List<LocationPortal> portals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, portal2Pos.getX() + 0.5D, portal2Pos.getZ() + 0.5D, location -> location.isInside(new Vector3d(portal2Pos.getX(), portal2Pos.getY(), portal2Pos.getZ()).add(0.5D, 0.5D, 0.5D)) && portal2Pos.equals(location.getPortalPosition()));
		if(!portals.isEmpty()) {
			return portals.get(0);
		}
		return null;
	}

	protected boolean isBlockSapling(World worldIn, PlayerEntity playerIn, BlockState state, BlockPos pos, Direction facing, BlockRayTraceResult hitResult) {
		if (state.is(BlockTags.SAPLINGS)) {
			return true;
		}
		return false;
	}

	@Nullable
	protected Direction.Axis getPortalWoodFrameAxis(World world, BlockPos pos) {
		Direction north = Direction.NORTH;
		Direction south = Direction.SOUTH;
		if(this.isPortalWood(world.getBlockState(pos)) && this.isPortalWood(world.getBlockState(pos.relative(north))) && this.isPortalWood(world.getBlockState(pos.relative(south)))
				&& this.isPortalWood(world.getBlockState(pos.above().relative(north))) && this.isPortalWood(world.getBlockState(pos.above().relative(south)))
				&& this.isPortalWood(world.getBlockState(pos.above(2).relative(north))) && this.isPortalWood(world.getBlockState(pos.above(2).relative(south)))
				&& this.isPortalWood(world.getBlockState(pos.above(3))) && this.isPortalWood(world.getBlockState(pos.above(3).relative(north))) && this.isPortalWood(world.getBlockState(pos.above(3).relative(south)))) {
			return Direction.Axis.X;
		}

		Direction east = Direction.EAST;
		Direction west = Direction.WEST;
		if(this.isPortalWood(world.getBlockState(pos)) && this.isPortalWood(world.getBlockState(pos.relative(east))) && this.isPortalWood(world.getBlockState(pos.relative(west)))
				&& this.isPortalWood(world.getBlockState(pos.above().relative(east))) && this.isPortalWood(world.getBlockState(pos.above().relative(west)))
				&& this.isPortalWood(world.getBlockState(pos.above(2).relative(east))) && this.isPortalWood(world.getBlockState(pos.above(2).relative(west)))
				&& this.isPortalWood(world.getBlockState(pos.above(3))) && this.isPortalWood(world.getBlockState(pos.above(3).relative(east))) && this.isPortalWood(world.getBlockState(pos.above(3).relative(west)))) {
			return Direction.Axis.Z;
		}

		return null;
	}

	protected boolean isPortalWood(BlockState state) {
		Block block = state.getBlock();
		return block == BlockRegistry.PORTAL_FRAME || block == BlockRegistry.LOG_PORTAL;
	}

	protected ItemStack copyDataFromOtherTalisman(Item newItem, ItemStack stack) {
		ItemStack newStack = new ItemStack(newItem);
		newStack.setTag(stack.getTag());
		return newStack;
	}
	
	/*@Override
	public Map<Integer, String> getVariants() {
		Map<Integer, String> variants = new HashMap<>();
		for (EnumTalisman type : EnumTalisman.values())
			variants.put(type.ordinal(), type.getTranslationKey());
		return variants;
	}

	public enum EnumTalisman implements IGenericItem {
		SWAMP_TALISMAN_0,
		SWAMP_TALISMAN_1,
		SWAMP_TALISMAN_2,
		SWAMP_TALISMAN_3,
		SWAMP_TALISMAN_4,
		SWAMP_TALISMAN_5;

		private final String unlocalizedName;
		private final String modelName;

		EnumTalisman() {
			this.modelName = this.name().toLowerCase(Locale.ENGLISH);
			this.unlocalizedName = this.modelName;
		}

		@Override
		public String getTranslationKey() {
			return this.unlocalizedName;
		}

		@Override
		public String getModelName() {
			return this.modelName;
		}

		@Override
		public int getID() {
			return this.ordinal();
		}

		@Override
		public Item getItem() {
			return ItemRegistry.SWAMP_TALISMAN;
		}
	}*/
}
