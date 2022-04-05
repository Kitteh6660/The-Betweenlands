package thebetweenlands.common.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.event.ArmSwingSpeedEvent;
import thebetweenlands.api.item.IDecayFood;
import thebetweenlands.common.block.misc.DampTorchBlock;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.config.properties.ItemDecayFoodProperty.DecayFoodStats;
import thebetweenlands.common.item.tools.BLAxeItem;
import thebetweenlands.common.item.tools.BLPickaxeItem;
import thebetweenlands.common.item.tools.BLShovelItem;
import thebetweenlands.common.item.tools.BLSwordItem;
import thebetweenlands.common.item.tools.bow.ItemBLBow;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.GameruleRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class OverworldItemHandler {
	private OverworldItemHandler() { }

	public static interface ITorchPlaceHandler {
		/**
		 * Returns the ID of this handler
		 * @return
		 */
		public ResourceLocation getID();

		/**
		 * Returns whether the specified item is a torch that should be destroyed or replaced
		 * @param stack
		 * @return
		 */
		public boolean isTorchItem(ItemStack stack);

		/**
		 * Called when a torch item is placed
		 * @param world
		 * @param pos
		 * @param stack
		 * @param player
		 * @return Return true if the placing was handled
		 */
		public default boolean onTorchItemPlaced(World world, BlockPos pos, ItemStack stack, PlayerEntity player) {
			return false;
		}

		/**
		 * Called when a torch is placed, by default replaces the block with a damp torch
		 * @param world
		 * @param pos
		 * @param block
		 * @param player
		 */
		public default void onTorchBlockPlaced(World world, BlockPos pos, BlockState state, ItemStack stack, PlayerEntity player) {

		}
	}

	//TODO Add configs for all blacklists/whitelists and torch place handlers?

	public static final Map<ResourceLocation, Predicate<ItemStack>> ROTTING_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> ROTTING_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> TAINTING_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> TAINTING_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> FIRE_TOOL_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> FIRE_TOOL_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> FERTILIZER_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> FERTILIZER_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> TOOL_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> TOOL_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> TORCH_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> TORCH_BLACKLIST = new HashMap<>();
	public static final Map<ResourceLocation, ITorchPlaceHandler> TORCH_PLACE_HANDLERS = new HashMap<>();

	static {
		ROTTING_WHITELIST.put(new ResourceLocation(ModInfo.ID, "config_whitelist"), stack -> BetweenlandsConfig.GENERAL.rottenFoodWhitelist.isListed(stack));
		ROTTING_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "config_blacklist"), stack -> BetweenlandsConfig.GENERAL.rottenFoodBlacklist.isListed(stack));
		ROTTING_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "default_blacklist"), stack -> {
			if(stack.getItem() == Items.CAKE || Block.getBlockFromItem(stack.getItem()) == Blocks.CAKE) {
				return true;
			}
			if(stack.getItem() instanceof ItemFood && stack.getItem() != Items.ROTTEN_FLESH && !ItemRegistry.ITEMS.contains(stack.getItem())) {
				return true;
			}
			return false;
		});

		TAINTING_WHITELIST.put(new ResourceLocation(ModInfo.ID, "config_whitelist"), stack -> BetweenlandsConfig.GENERAL.taintingWhitelist.isListed(stack));
		TAINTING_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "config_blacklist"), stack -> BetweenlandsConfig.GENERAL.taintingBlacklist.isListed(stack));
		TAINTING_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "default_blacklist"), stack -> stack.getItem() instanceof ItemPotion);

		FIRE_TOOL_WHITELIST.put(new ResourceLocation(ModInfo.ID, "config_whitelist"), stack -> BetweenlandsConfig.GENERAL.fireToolWhitelist.isListed(stack));
		FIRE_TOOL_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "config_blacklist"), stack -> BetweenlandsConfig.GENERAL.fireToolBlacklist.isListed(stack));
		FIRE_TOOL_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "default_blacklist"), stack -> stack.getItem() instanceof ItemFlintAndSteel);

		FERTILIZER_WHITELIST.put(new ResourceLocation(ModInfo.ID, "config_whitelist"), stack -> BetweenlandsConfig.GENERAL.fertilizerWhitelist.isListed(stack));
		FERTILIZER_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "config_blacklist"), stack -> BetweenlandsConfig.GENERAL.fertilizerBlacklist.isListed(stack));
		FERTILIZER_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "default_blacklist"), stack -> stack.getItem() == Items.DYE);

		TOOL_WHITELIST.put(new ResourceLocation(ModInfo.ID, "config_whitelist"), stack -> BetweenlandsConfig.GENERAL.toolWeaknessWhitelist.isListed(stack));
		TOOL_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "config_blacklist"), stack -> BetweenlandsConfig.GENERAL.toolWeaknessBlacklist.isListed(stack));
		TOOL_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "default_blacklist"), stack -> {
			return (stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemBow) &&
					stack.getItem() instanceof BLSwordItem == false && 
					stack.getItem() instanceof BLAxeItem == false && 
					stack.getItem() instanceof BLPickaxeItem == false && 
					stack.getItem() instanceof BLShovelItem == false &&
					stack.getItem() instanceof ItemBLBow == false &&
					!ItemRegistry.ITEMS.contains(stack.getItem());
		});

		TORCH_WHITELIST.put(new ResourceLocation(ModInfo.ID, "config_whitelist"), stack -> BetweenlandsConfig.GENERAL.torchWhitelist.isListed(stack));
		TORCH_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "config_blacklist"), stack -> BetweenlandsConfig.GENERAL.torchBlacklist.isListed(stack));
		TORCH_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "default_blacklist"), stack -> {
			Block block = Block.getBlockFromItem(stack.getItem());
			return block instanceof BlockTorch && !BlockRegistry.BLOCKS.contains(block);
		});

		ITorchPlaceHandler vanillaTorchPlaceHandler = new ITorchPlaceHandler() {
			@Override
			public ResourceLocation getID() {
				return new ResourceLocation(ModInfo.ID, "vanilla_torch");
			}

			@Override
			public boolean isTorchItem(ItemStack stack) {
				return isTorchTurningDamp(stack);
			}

			@Override
			public void onTorchBlockPlaced(World world, BlockPos pos, BlockState state, ItemStack stack, PlayerEntity player) {
				Direction facing = null;
				try {
					facing = state.getValue(BlockTorch.FACING);
				} catch(Exception ex) {}
				if(facing == null) {
					List<Direction> dirs = new ArrayList<>();
					Collections.addAll(dirs, Direction.VALUES);
					Collections.shuffle(dirs, world.rand);
					for(Direction dir : dirs) {
						if(dir != Direction.DOWN) {
							BlockState offsetState = world.getBlockState(pos.offset(dir.getOpposite()));
							if((dir == Direction.UP && offsetState.getBlock().canPlaceTorchOnTop(offsetState, world, pos.offset(dir.getOpposite()))) || world.isSideSolid(pos.offset(dir.getOpposite()), dir)) {
								facing = dir;
								break;
							}
						}
					}
				}
				if(facing != null) {
					BlockState dampTorch = BlockRegistry.DAMP_TORCH.defaultBlockState().setValue(DampTorchBlock.FACING, facing);
					world.setBlockState(pos, dampTorch);
				} else {
					world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
					world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, new ItemStack(BlockRegistry.DAMP_TORCH)));
				}
				world.playLocalSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.2F, 1.0F);
				if(player instanceof ServerPlayerEntity) {
					AdvancementCriterionRegistry.DAMP_TORCH_PLACED.trigger((ServerPlayerEntity) player);
				}
			}
		};
		TORCH_PLACE_HANDLERS.put(vanillaTorchPlaceHandler.getID(), vanillaTorchPlaceHandler);
	}

	@SubscribeEvent
	public static void onPlayerTorchPlacement(PlaceEvent event) {
		if (event.getPlayer().dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			ItemStack held = event.getPlayer().getItemInHand(event.getHand());
			if(!held.isEmpty()) {
				for(ITorchPlaceHandler handler : TORCH_PLACE_HANDLERS.values()) {
					if(handler.isTorchItem(held)) {
						if(!handler.onTorchItemPlaced(event.getWorld(), event.getPos(), held, event.getPlayer())) {
							handler.onTorchBlockPlaced(event.getWorld(), event.getPos(), event.getState(), held, event.getPlayer());
						}
						break;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onUseItem(PlayerInteractEvent.RightClickBlock event) {
		ItemStack item = event.getItemStack();
		if(!item.isEmpty() && event.getEntityPlayer().dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			if(isFireToolBlocked(item)) {
				event.setUseItem(Result.DENY);
				event.setCanceled(true);
				if(event.getWorld().isClientSide()) {
					event.getEntityPlayer().displayClientMessage(new TranslationTextComponent("chat.flintandsteel", new TranslationTextComponent(item.getTranslationKey() + ".name")), true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBonemeal(BonemealEvent event) {
		if(event.getEntityPlayer().dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			ItemStack stack = event.getStack();
			if(!stack.isEmpty() && isFertilizerBlocked(stack)) {
				event.setResult(Result.DENY);
				event.setCanceled(true);
				if(event.getWorld().isClientSide()) {
					event.getEntityPlayer().displayClientMessage(new TranslationTextComponent("chat.fertilizer", new TranslationTextComponent(stack.getTranslationKey() + ".name")), true);
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onArmSwingSpeed(ArmSwingSpeedEvent event) {
		if(event.getEntityLiving().dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			ItemStack tool = event.getEntityLiving().getMainHandItem();
			if (!tool.isEmpty() && isToolWeakened(tool)) {
				event.setSpeed(event.getSpeed() * 0.3F);
			}
		}
	}

	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if(event.getEntityPlayer().dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			ItemStack tool = event.getEntityPlayer().getMainHandItem();
			if (!tool.isEmpty() && isToolWeakened(tool)) {
				event.setNewSpeed(event.getNewSpeed() * 0.3F);
			}
		}
	}

	@SubscribeEvent
	public static void onEntitySpawn(EntityJoinWorldEvent event) {
		if(event.getEntity() instanceof PlayerEntity && !((PlayerEntity)event.getEntity()).isCreative()) {
			updatePlayerInventory((PlayerEntity)event.getEntity());
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.END && !event.player.level.isClientSide() && event.player.tickCount % 5 == 0 && !event.player.isCreative()) {
			updatePlayerInventory(event.player);
		}
	}

	private static void updatePlayerInventory(PlayerEntity player) {
		int invCount = player.inventory.getContainerSize();
		if(player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			//Set to rotten food/tainted potion
			for(int i = 0; i < invCount; i++) {
				ItemStack stack = player.inventory.getItem(i);
				if(!stack.isEmpty()) {
					if(isRotting(stack)) {
						ItemStack rottenFoodStack = new ItemStack(ItemRegistry.ROTTEN_FOOD, stack.getCount());
						stack.setCount(1);
						ItemRegistry.ROTTEN_FOOD.setOriginalStack(rottenFoodStack, stack);
						player.inventory.setItem(i, rottenFoodStack);
					} else if(isTainting(stack)) {
						ItemStack taintedPotionStack = new ItemStack(ItemRegistry.TAINTED_POTION, stack.getCount());
						stack.setCount(1);
						ItemRegistry.TAINTED_POTION.setOriginalStack(taintedPotionStack, stack);
						player.inventory.setItem(i, taintedPotionStack);
					}
				}
			}
		} else {
			//Revert rotten food/tainted potion
			for(int i = 0; i < invCount; i++) {
				ItemStack stack = player.inventory.getItem(i);
				if(!stack.isEmpty()) {
					if(BetweenlandsConfig.GENERAL.reverseRottenFood && stack.getItem() == ItemRegistry.ROTTEN_FOOD) {
						ItemStack originalStack = ItemRegistry.ROTTEN_FOOD.getOriginalStack(stack);
						if(!originalStack.isEmpty()) {
							originalStack.setCount(stack.getCount());
							player.inventory.setItem(i, originalStack);
						} else {
							player.inventory.setItem(i, ItemStack.EMPTY);
						}
					} else if(stack.getItem() == ItemRegistry.TAINTED_POTION) {
						ItemStack originalStack = ItemRegistry.TAINTED_POTION.getOriginalStack(stack);
						if(!originalStack.isEmpty()) {
							originalStack.setCount(stack.getCount());
							player.inventory.setItem(i, originalStack);
						} else {
							player.inventory.setItem(i, ItemStack.EMPTY);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onItemPickup(EntityItemPickupEvent event) {
		PlayerEntity player = event.getEntityPlayer();
		if(player != null && !player.level.isClientSide() && !player.isCreative()) {
			ItemStack stack = event.getItem().getItem();
			if(!stack.isEmpty()) {
				if(player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
					if(isRotting(stack)) {
						ItemStack rottenFoodStack = new ItemStack(ItemRegistry.ROTTEN_FOOD, stack.getCount());
						ItemRegistry.ROTTEN_FOOD.setOriginalStack(rottenFoodStack, stack);
						event.getItem().setItem(rottenFoodStack);
					} else if(isTainting(stack)) {
						ItemStack taintedPotionStack = new ItemStack(ItemRegistry.TAINTED_POTION, stack.getCount());
						ItemRegistry.TAINTED_POTION.setOriginalStack(taintedPotionStack, stack);
						event.getItem().setItem(taintedPotionStack);
					}
				} else if(BetweenlandsConfig.GENERAL.reverseRottenFood && stack.getItem() == ItemRegistry.ROTTEN_FOOD) {
					ItemStack originalStack = ItemRegistry.ROTTEN_FOOD.getOriginalStack(stack);
					if(!originalStack.isEmpty()) {
						event.getItem().setItem(originalStack);
					} else {
						event.getItem().remove();
						event.setCanceled(true);
					}
				} else if(stack.getItem() == ItemRegistry.TAINTED_POTION) {
					ItemStack originalStack = ItemRegistry.TAINTED_POTION.getOriginalStack(stack);
					if(!originalStack.isEmpty()) {
						event.getItem().setItem(originalStack);
					} else {
						event.getItem().remove();
						event.setCanceled(true);
					}
				}
			}
		}
	}

	public static boolean isRotting(ItemStack stack) {
		if(!BetweenlandsConfig.GENERAL.useRottenFood || !GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_ROTTEN_FOOD)) {
			return false;
		}
		for(Predicate<ItemStack> whitelistPredicate : ROTTING_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : ROTTING_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTainting(ItemStack stack) {
		if(!BetweenlandsConfig.GENERAL.usePotionBlacklist || !GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_POTION_BLACKLIST)) {
			return false;
		}
		for(Predicate<ItemStack> whitelistPredicate : TAINTING_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : TAINTING_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isFireToolBlocked(ItemStack stack) {
		if(!BetweenlandsConfig.GENERAL.useFireToolBlacklist || !GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_FIRE_TOOL_BLACKLIST)) {
			return false;
		}
		for(Predicate<ItemStack> whitelistPredicate : FIRE_TOOL_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : FIRE_TOOL_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isFertilizerBlocked(ItemStack stack) {
		if(!BetweenlandsConfig.GENERAL.useFertilizerBlacklist || !GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_FERTILIZER_BLACKLIST)) {
			return false;
		}
		for(Predicate<ItemStack> whitelistPredicate : FERTILIZER_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : FERTILIZER_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isToolWeakened(ItemStack stack) {
		if(!BetweenlandsConfig.GENERAL.useToolWeakness || !GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_TOOL_WEAKNESS)) {
			return false;
		}
		for(Predicate<ItemStack> whitelistPredicate : TOOL_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : TOOL_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTorchTurningDamp(ItemStack stack) {
		if(!BetweenlandsConfig.GENERAL.useTorchBlacklist || !GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_TORCH_BLACKLIST)) {
			return false;
		}
		for(Predicate<ItemStack> whitelistPredicate : TORCH_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : TORCH_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}
	
	@Nullable
	public static DecayFoodStats getDecayFoodStats(ItemStack stack) {
		if(stack.getItem() instanceof IDecayFood) {
			return new DecayFoodStats(((IDecayFood) stack.getItem()).getDecayHealAmount(stack), ((IDecayFood) stack.getItem()).getDecayHealSaturation(stack));
		}
		return BetweenlandsConfig.GENERAL.decayFoodList.getStats(stack);
	}
}