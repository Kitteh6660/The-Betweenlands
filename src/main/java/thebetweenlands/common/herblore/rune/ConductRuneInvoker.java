package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.ServerWorld;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.io.IGetter;
import thebetweenlands.api.runechain.io.InputSerializers;
import thebetweenlands.api.runechain.io.types.IBlockTarget;
import thebetweenlands.api.runechain.io.types.IRuneItemStackAccess;
import thebetweenlands.api.runechain.io.types.IVectorTarget;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;
import thebetweenlands.common.entity.EntityPlayerDelegate;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.util.InventoryUtil;

public final class ConductRuneInvoker extends AbstractRune<ConductRuneInvoker> {

	public static final class Blueprint extends AbstractRune.Blueprint<ConductRuneInvoker> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.FERGALAZ, 1)
					.duration(0.1f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;
		private static final IGetter<IRuneItemStackAccess> IN_ITEM_1;
		private static final IGetter<IBlockTarget> IN_POSITION_1;
		private static final IGetter<IVectorTarget> IN_DIRECTION_1;

		public static final RuneConfiguration CONFIGURATION_2;
		private static final IGetter<IRuneItemStackAccess> IN_ITEM_2;
		private static final IGetter<IVectorTarget> IN_POSITION_2;
		private static final IGetter<IVectorTarget> IN_DIRECTION_2;

		public static final RuneConfiguration CONFIGURATION_3;
		private static final IGetter<IRuneItemStackAccess> IN_ITEM_3;
		private static final IGetter<Entity> IN_ENTITY_3;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			IN_ITEM_1 = builder.in(RuneTokenDescriptors.ITEM).type(IRuneItemStackAccess.class).getter();
			IN_POSITION_1 = builder.in(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).serializer(InputSerializers.BLOCK).getter();
			IN_DIRECTION_1 = builder.in(RuneTokenDescriptors.DIRECTION).type(IVectorTarget.class).serializer(InputSerializers.VECTOR).getter();
			CONFIGURATION_1 = builder.build();

			IN_ITEM_2 = builder.in(RuneTokenDescriptors.ITEM).type(IRuneItemStackAccess.class).getter();
			IN_POSITION_2 = builder.in(RuneTokenDescriptors.POSITION).type(IVectorTarget.class).serializer(InputSerializers.VECTOR).getter();
			IN_DIRECTION_2 = builder.in(RuneTokenDescriptors.DIRECTION).type(IVectorTarget.class).serializer(InputSerializers.VECTOR).getter();
			CONFIGURATION_2 = builder.build();

			IN_ITEM_3 = builder.in(RuneTokenDescriptors.ITEM).type(IRuneItemStackAccess.class).getter();
			IN_ENTITY_3 = builder.in(RuneTokenDescriptors.ENTITY).type(Entity.class).serializer(InputSerializers.ENTITY).getter();
			CONFIGURATION_3 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3);
		}

		@Override
		public ConductRuneInvoker create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new ConductRuneInvoker(this, index, composition, (RuneConfiguration) configuration);
		}

		private void returnExcessItems(IRuneChainUser user, Vector3d pos, List<NonNullList<ItemStack>> excess, @Nullable IInventory accessInventory, int accessSlot) {
			Entity entity = user.getEntity();

			if(entity instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) entity;

				for(NonNullList<ItemStack> stacks : excess) {
					for(ItemStack stack : stacks) {
						if(!stack.isEmpty()) {
							InventoryUtil.addItemToInventory(player.inventory, stack, slot -> accessInventory != player.inventory || accessSlot != slot);

							if(!stack.isEmpty()) {	
								InventoryHelper.spawnItemStack(user.getWorld(), pos.x, pos.y, pos.z, stack);
							}
						}
					}

					stacks.clear();
				}
			} else {
				IInventory inv = user.getInventory();

				for(NonNullList<ItemStack> stacks : excess) {
					for(ItemStack stack : stacks) {
						if(!stack.isEmpty()) {
							if(inv != null) {
								InventoryUtil.addItemToInventory(inv, stack, slot -> accessInventory != inv || accessSlot != slot);
							}

							if(!stack.isEmpty()) {
								InventoryHelper.spawnItemStack(user.getWorld(), pos.x, pos.y, pos.z, stack);
							}
						}
					}

					stacks.clear();
				}
			}
		}

		public static Pair<Float, Float> getRotationsFromDir(Vector3d dir) {
			double magnitudeXZ = (double)MathHelper.sqrt(dir.x * dir.x + dir.z * dir.z);
			float yaw = (float)(MathHelper.atan2(dir.z, dir.x) * (180D / Math.PI)) - 90.0F;
			float pitch = (float)(-(MathHelper.atan2(dir.y, magnitudeXZ) * (180D / Math.PI)));
			return Pair.of(yaw, pitch);
		}

		private void invokeImmediateUse(INodeIO io, IRuneChainUser user, IRuneItemStackAccess access, EntityPlayerDelegate delegate, Vector3d pos, float yaw, float pitch, Consumer<EntityPlayerDelegate> action) {
			ItemStack inputStack = access.get();

			if(!inputStack.isEmpty() && access.set(ItemStack.EMPTY)) {
				delegate.moveTo(pos.x, pos.y - delegate.getEyeHeight(), pos.z, yaw, pitch);

				ItemStack prevHeldStack = delegate.getItemInHand(Hand.MAIN_HAND);

				delegate.setItemInHand(Hand.MAIN_HAND, inputStack);

				action.accept(delegate);

				ItemStack outputStack = delegate.getItemInHand(Hand.MAIN_HAND);

				if(!access.set(outputStack)) {
					this.returnExcessItems(user, pos, Arrays.asList(NonNullList.from(ItemStack.EMPTY, outputStack)), null, -1);
				}

				delegate.setItemInHand(Hand.MAIN_HAND, prevHeldStack);

				delegate.remove();

				Pair<IInventory, Integer> delegatedSlot = access.getDelegatedSlot();
				this.returnExcessItems(user, pos, delegate.getExcessInventories(), delegatedSlot != null ? delegatedSlot.getLeft() : null, delegatedSlot != null ? delegatedSlot.getRight() : -1);
			}
		}

		private void invokeContinuousUse(INodeIO io, IRuneChainUser user, IRuneItemStackAccess access, EntityPlayerDelegate delegate, Vector3d pos, float yaw, float pitch, BiFunction<EntityPlayerDelegate, Integer, Boolean> action) {
			ItemStack[] stack = new ItemStack[] { access.get() };

			if(!stack[0].isEmpty() && access.set(ItemStack.EMPTY)) {
				io.schedule(scheduler -> {
					boolean terminated = false;

					int i = scheduler.getUpdateCount();

					delegate.moveTo(pos.x, pos.y - delegate.getEyeHeight(), pos.z, yaw, pitch);

					ItemStack prevHeldStack = delegate.getItemInHand(Hand.MAIN_HAND);

					delegate.setItemInHand(Hand.MAIN_HAND, stack[0]);

					if(action.apply(delegate, i)) {
						delegate.remove();
						terminated = true;
						scheduler.terminate();
					}

					ItemStack outputStack = delegate.getItemInHand(Hand.MAIN_HAND);

					stack[0] = outputStack;

					if(terminated && !access.set(outputStack)) {
						this.returnExcessItems(user, pos, Arrays.asList(NonNullList.from(ItemStack.EMPTY, outputStack)), null, -1);
					}

					delegate.setItemInHand(Hand.MAIN_HAND, prevHeldStack);

					Pair<IInventory, Integer> delegatedSlot = access.getDelegatedSlot();
					this.returnExcessItems(user, pos, delegate.getExcessInventories(), delegatedSlot != null ? delegatedSlot.getLeft() : null, delegatedSlot != null ? delegatedSlot.getRight() : -1);

					scheduler.sleep(1);
				});
			}
		}

		@Override
		protected Subject activate(ConductRuneInvoker state, IRuneExecutionContext context, INodeIO io) {

			if(context.getUser().getWorld() instanceof ServerWorld) {
				ServerWorld world = (ServerWorld) context.getUser().getWorld();

				IRuneChainUser user = context.getUser();
				Entity userEntity = user.getEntity();

				List<NonNullList<ItemStack>> excess = new ArrayList<>();

				EntityPlayerDelegate.Builder delegateBuilder = EntityPlayerDelegate.from(world, new GameProfile(UUID.randomUUID(), "[RuneChain]"), excess);

				delegateBuilder.entity(userEntity);

				if(userEntity instanceof PlayerEntity) {
					delegateBuilder.playerInventory(((PlayerEntity) userEntity).inventory);
				} else {
					delegateBuilder.mainInventory(context.getUser().getInventory());
				}

				EntityPlayerDelegate delegate = delegateBuilder.build();

				if(state.getConfiguration() == CONFIGURATION_1) {
					IRuneItemStackAccess access = IN_ITEM_1.get(io);

					BlockPos block = IN_POSITION_1.get(io).block();
					Pair<Float, Float> rotations = getRotationsFromDir(IN_DIRECTION_1.get(io).vec());

					this.invokeImmediateUse(io, user, access, delegate, new Vector3d(block.getX() + 0.5f, block.getY() + 0.5f, block.getZ() + 0.5f), rotations.getLeft(), rotations.getRight(), d -> {
						ItemStack stack = delegate.getMainHandItem();

						if(stack.onItemUseFirst(delegate, delegate.world, block, Hand.MAIN_HAND, Direction.UP, 0.5f, 1.0f, 0.5f) == ActionResultType.PASS) {
							stack.onItemUse(delegate, delegate.world, block, Hand.MAIN_HAND, Direction.UP, 0.5f, 1.0f, 0.5f);
						}
					});

				} else if(state.getConfiguration() == CONFIGURATION_2) {
					IRuneItemStackAccess access = IN_ITEM_2.get(io);

					Vector3d pos = IN_POSITION_2.get(io).vec();
					Pair<Float, Float> rotations = getRotationsFromDir(IN_DIRECTION_2.get(io).vec());

					this.invokeContinuousUse(io, user, access, delegate, pos, rotations.getLeft(), rotations.getRight(), (d, i) -> {
						ItemStack stack = delegate.getMainHandItem();

						if(i == 0) {
							ItemStack resultStack = stack.useItemRightClick(delegate.world, delegate, Hand.MAIN_HAND).getResult();

							if(resultStack != stack || resultStack.getCount() != i) {
								delegate.setItemInHand(Hand.MAIN_HAND, resultStack);

								if(resultStack.isEmpty()) {
									net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(delegate, stack, Hand.MAIN_HAND);
								}

								stack = resultStack;
								delegate.setItemInHand(Hand.MAIN_HAND, stack);
							}
						}

						if(!delegate.isHandActive() || stack.getUseDuration() <= 0) {
							delegate.setItemInHand(Hand.MAIN_HAND, stack);

							return true;
						} else {
							stack.updateAnimation(delegate.world, delegate, 0 /*TODO Is this right? */, true);

							stack.getItem().onUsingTick(stack, delegate, i);

							if(i >= stack.getUseDuration() || i >= 20 * 5) {
								stack.onPlayerStoppedUsing(delegate.world, delegate, 0);

								ItemStack resultStack = stack.finishUsingItem(delegate.world, delegate);
								resultStack = net.minecraftforge.event.ForgeEventFactory.finishUsingItem(delegate, stack, i, resultStack);
								delegate.setItemInHand(Hand.MAIN_HAND, resultStack);

								stack = resultStack;
								delegate.setItemInHand(Hand.MAIN_HAND, stack);

								return true;
							}
						}

						return false;
					});

				} else {
					IRuneItemStackAccess access = IN_ITEM_3.get(io);

					Entity target = IN_ENTITY_3.get(io);

					if(target != null) {
						this.invokeImmediateUse(io, user, access, delegate, new Vector3d(target.getX(), target.getY(), target.getZ()), 0, 0, d -> {
							ActionResultType cancelResult = net.minecraftforge.common.ForgeHooks.onInteractEntity(delegate, target, Hand.MAIN_HAND);
							if(cancelResult != null) {
								return;
							}

							ItemStack stack = delegate.getMainHandItem();
							ItemStack copy = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();

							if(target.processInitialInteract(delegate, Hand.MAIN_HAND)) {
								if(stack.isEmpty()) {
									net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(delegate, copy, Hand.MAIN_HAND);
								}
							} else {
								if(!stack.isEmpty() && target instanceof LivingEntity) {
									if(stack.interactWithEntity(delegate, (LivingEntity) target, Hand.MAIN_HAND)) {
										if(stack.isEmpty()) {
											net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(delegate, copy, Hand.MAIN_HAND);
											delegate.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
										}
									}
								}
							}
						});
					}
				}
			}

			return null;
		}
		
		@Override
		protected boolean isDelegatingRuneEffectModifier(ConductRuneInvoker state, AbstractRune<?> target, AbstractRune<?> outputRune, int inputIndex) {
			if(state.getConfiguration() == CONFIGURATION_1) {
				return inputIndex == IN_ITEM_1.index();
			} else if(state.getConfiguration() == CONFIGURATION_2) {
				return inputIndex == IN_ITEM_2.index();
			} else {
				return inputIndex == IN_ITEM_3.index();
			}
		}
	}

	private ConductRuneInvoker(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
