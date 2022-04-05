package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite.EnumLifeCrystalType;
import thebetweenlands.common.network.clientbound.MessageSoundRipple;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsChunkStorage;
import thebetweenlands.util.NBTHelper;

public class ItemGemSinger extends Item {
	public static enum GemSingerTarget implements Predicate<BlockState> {
		AQUA_MIDDLE_GEM(0, state -> state.getBlock() == BlockRegistry.AQUA_MIDDLE_GEM_ORE),
		CRIMSON_MIDDLE_GEM(1, state -> state.getBlock() == BlockRegistry.CRIMSON_MIDDLE_GEM_ORE),
		GREEN_MIDDLE_GEM(2, state -> state.getBlock() == BlockRegistry.GREEN_MIDDLE_GEM_ORE),
		LIFE_CRYSTAL(3, state -> state.getBlock() == BlockRegistry.LIFE_CRYSTAL_STALACTITE && state.getValue(BlockLifeCrystalStalactite.VARIANT) == EnumLifeCrystalType.ORE);

		private final int id;
		private final Predicate<BlockState> predicate;

		private GemSingerTarget(int id, Predicate<BlockState> predicate) {
			this.id = id;
			this.predicate = predicate;
		}

		public int getId() {
			return this.id;
		}

		@Nullable
		public static GemSingerTarget byId(int id) {
			for(GemSingerTarget target : values()) {
				if(target.id == id) {
					return target;
				}
			}
			return null;
		}

		@Override
		public boolean test(BlockState state) {
			return this.predicate.test(state);
		}
	}

	public ItemGemSinger() {
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		this.setMaxStackSize(1);
		this.setMaxDamage(28);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return this.getTargetPosition(stack) != null;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if(!worldIn.isClientSide()) {
			ItemStack stack = playerIn.getItemInHand(handIn);
			if(playerIn.isCrouching()) {
				this.setTarget(stack, null, null);
			} else {
				final int chunkRange = 6;
				final int maxDelay = 80;
				final int attempts = 64;

				BlockPos gem = this.getTargetPosition(stack);

				if(gem != null) {
					GemSingerTarget target = this.getTargetType(stack);

					boolean valid = false;

					if(target != null) {
						Chunk chunk = worldIn.getChunkProvider().getLoadedChunk(gem.getX() >> 4, gem.getZ() >> 4);
						if(chunk != null) {
							BetweenlandsChunkStorage storage = BetweenlandsChunkStorage.forChunk(worldIn, chunk);
							if(storage != null) {
								IntSet gems = storage.findGems(target);
								if(gems.contains(BetweenlandsChunkStorage.getGemSingerTargetIndex(gem.getX(), gem.getY(), gem.getZ()))) {
									valid = true;
								}
							}
						}
					}

					if(!valid) {
						this.setTarget(stack, null, null);
						gem = null;
					}
				}

				if(gem == null) {
					for(int i = 0; i < attempts; i++) {
						Chunk chunk = worldIn.getChunkProvider().getLoadedChunk((MathHelper.floor(playerIn.getX()) >> 4) + worldIn.rand.nextInt(chunkRange * 2 + 1) - chunkRange, (MathHelper.floor(playerIn.getZ()) >> 4) + worldIn.rand.nextInt(chunkRange * 2 + 1) - chunkRange);

						if(chunk != null) {
							BetweenlandsChunkStorage storage = BetweenlandsChunkStorage.forChunk(worldIn, chunk);

							if(storage != null) {
								EnumMap<GemSingerTarget, BlockPos> foundGems = new EnumMap<GemSingerTarget, BlockPos>(GemSingerTarget.class);

								for(GemSingerTarget target : GemSingerTarget.values()) {
									BlockPos foundGem = storage.findRandomGem(target, worldIn.rand, playerIn.getPosition(), chunkRange * 16);

									if(foundGem != null) {
										foundGems.put(target, foundGem);
									}
								}

								if(!foundGems.isEmpty()) {
									List<Entry<GemSingerTarget, BlockPos>> foundGemEntries = new ArrayList<>(foundGems.entrySet());
									Entry<GemSingerTarget, BlockPos> picked = foundGemEntries.get(worldIn.rand.nextInt(foundGemEntries.size()));

									gem = picked.getValue();
									this.setTarget(stack, gem, picked.getKey());
									this.spawnEffect(playerIn, gem, chunkRange * 16, maxDelay);

									stack.damageItem(1, playerIn);
									break;
								}
							}
						}
					}
				} else {
					this.spawnEffect(playerIn, gem, chunkRange * 16, maxDelay);
					stack.damageItem(1, playerIn);
				}

				playerIn.getCooldownTracker().setCooldown(stack.getItem(), 60);
			}
		}

		if(worldIn.isClientSide() && !playerIn.isCrouching()) {
			worldIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundRegistry.GEM_SINGER, SoundCategory.PLAYERS, 2, 1);
		}

		playerIn.swing(handIn);

		return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getItemInHand(handIn));
	}

	protected void spawnEffect(PlayerEntity player, BlockPos target, int maxRangeBlocks, int maxDelay) {
		if(player instanceof ServerPlayerEntity) {
			int delay = Math.min((int)(Math.sqrt(player.getDistanceSq(target)) / (float)maxRangeBlocks * maxDelay), maxDelay);
			TheBetweenlands.networkWrapper.sendTo(new MessageSoundRipple(target, delay), (ServerPlayerEntity) player);
		}
	}

	protected void setTarget(ItemStack stack, @Nullable BlockPos pos, @Nullable GemSingerTarget target) {
		if(pos != null && target != null) {
			CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
			nbt.setLong("targetPos", pos.toLong());
			nbt.putInt("targetType", target.getId());
		} else if(stack.getTag() != null) {
			stack.getTag().removeTag("targetPos");
			stack.getTag().removeTag("targetType");
		}
	}

	@Nullable
	protected BlockPos getTargetPosition(ItemStack stack) {
		if(stack.hasTag() && stack.getTag().contains("targetPos", Constants.NBT.TAG_LONG)) {
			return BlockPos.of(stack.getTag().getLong("targetPos"));
		}
		return null;
	}

	@Nullable
	protected GemSingerTarget getTargetType(ItemStack stack) {
		if(stack.hasTag() && stack.getTag().contains("targetType", Constants.NBT.TAG_INT)) {
			return GemSingerTarget.byId(stack.getTag().getInt("targetType"));
		}
		return null;
	}
}
