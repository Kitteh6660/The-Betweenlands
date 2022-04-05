package thebetweenlands.common.item.misc;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.UseAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.item.IRenamableItem;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.structure.BlockWaystone;
import thebetweenlands.common.handler.PlayerRespawnHandler;
import thebetweenlands.common.item.equipment.ItemRing;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.util.PlayerUtil;

public class ItemBoneWayfinder extends Item implements IRenamableItem, IAnimatorRepairable {
	public ItemBoneWayfinder() {
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		this.setMaxDamage(10);
		this.setMaxStackSize(1);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return this.getBoundWaystone(stack) != null;
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = player.getItemInHand(hand);
		if(this.getBoundWaystone(stack) == null && stack.getDamageValue() < stack.getMaxDamage()) {
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() == BlockRegistry.WAYSTONE && this.activateWaystone(world, pos, state, stack)) {
				if(!level.isClientSide()) {
					this.setBoundWaystone(stack, pos);
				}
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(player.isCrouching()) {
			if (!level.isClientSide()) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ITEM_RENAMING, world, hand == Hand.MAIN_HAND ? 0 : 1, 0, 0);
			}
		} else {
			if(stack.getDamageValue() < stack.getMaxDamage() && this.getBoundWaystone(stack) != null) {
				player.setActiveHand(hand);
				return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
			}
		}

		return new ActionResult<ItemStack>(ActionResultType.PASS, stack);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entity) {
		if(!worldIn.isClientSide() && stack.getDamageValue() < stack.getMaxDamage()) {
			BlockPos waystone = this.getBoundWaystone(stack);
			if(waystone != null) {
				BlockPos spawnPoint = PlayerRespawnHandler.getSpawnPointNearPos(worldIn, waystone, 8, false, 4, 0);

				if(spawnPoint != null) {
					if(entity.getDistanceSq(spawnPoint) > 24) {
						this.playThunderSounds(worldIn, entity.getX(), entity.getY(), entity.getZ());
					}

					PlayerUtil.teleport(entity, spawnPoint.getX() + 0.5D, spawnPoint.getY(), spawnPoint.getZ() + 0.5D);

					this.playThunderSounds(worldIn, entity.getX(), entity.getY(), entity.getZ());

					entity.addEffect(new EffectInstance(Effects.BLINDNESS, 60, 1));

					stack.damageItem(1, entity);
				} else if(entity instanceof ServerPlayerEntity) {
					((ServerPlayerEntity) entity).displayClientMessage(new TranslationTextComponent("chat.waystone.obstructed"), true);
				}
			}
		}
		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 100;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		int maxDamage = stack.getMaxDamage();
		if(damage > maxDamage) {
			//Don't let the wayfinder break
			damage = maxDamage;
		}
		super.setDamage(stack, damage);
	}

	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(stack.hasTag() && stack.getTag().contains("link", Constants.NBT.TAG_LONG)) {
			BlockPos waystone = BlockPos.of(stack.getTag().getLong("link"));
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocalFormatted("tooltip.bl.bone_wayfinder_linked", waystone.getX(), waystone.getY(), waystone.getZ()), 0));
		} else {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocalFormatted("tooltip.bl.bone_wayfinder"), 0));
		}
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
		if(!entity.level.isClientSide()) {
			if(entity.hurtTime > 0) {
				entity.stopActiveHand();
				entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
			}

			if(entity instanceof PlayerEntity && !((PlayerEntity) entity).isCreative() && count < 60 && entity.tickCount % 3 == 0) {
				int removed = ItemRing.removeXp((PlayerEntity) entity, 1);
				if(removed == 0) {
					entity.stopActiveHand();
					entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
				}
			}

			if(count < 90 && count % 20 == 0) {
				entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundRegistry.PORTAL_TRAVEL, SoundCategory.PLAYERS, 0.05F + 0.4F * (float)MathHelper.clamp(80 - count, 1, 80) / 80.0F, 0.9F + entity.level.rand.nextFloat() * 0.2F);
			}
		} else {
			Random rand = entity.level.rand;
			for(int i = 0; i < MathHelper.clamp(60 - count, 1, 60); i++) {
				entity.level.addParticle(ParticleTypes.SUSPENDED_DEPTH, entity.getX() + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 6, entity.getY() + rand.nextFloat() * 4 - 2, entity.getZ() + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 6, 0, 0.2D, 0);
			}
		}
	}

	protected void playThunderSounds(World world, double x, double y, double z) {
		world.playLocalSound(null, x, y, z, SoundRegistry.RIFT_CREAK, SoundCategory.PLAYERS, 2, 1);
		world.playLocalSound(null, x, y, z, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.75F, 0.75F);
	}

	protected boolean activateWaystone(World world, BlockPos pos, BlockState state, ItemStack stack) {
		BlockWaystone block = (BlockWaystone) state.getBlock();
		if(block.isValidWaystone(world, pos, state)) {
			BlockWaystone.Part part = state.getValue(BlockWaystone.PART);

			if(!level.isClientSide()) {
				int startY = part == BlockWaystone.Part.BOTTOM ? 0 : (part == BlockWaystone.Part.MIDDLE ? -1 : -2);
				for(int yo = startY; yo < startY + 3; yo++) {
					BlockState newState = world.getBlockState(pos.above(yo)).setValue(BlockWaystone.ACTIVE, true);
					world.setBlockState(pos.above(yo), newState);
					world.sendBlockUpdated(pos.above(yo), newState, newState, 2); //why tf is this necessary
				}

				this.playThunderSounds(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

				List<LocationStorage> waystoneLocations = BetweenlandsWorldStorage.forWorld(world).getLocalStorageHandler()
						.getLocalStorages(LocationStorage.class, new AxisAlignedBB(pos.getX(), pos.getY() + startY, pos.getZ(), pos.getX() + 1, pos.getY() + startY + 3, pos.getZ() + 1), storage -> storage.getType() == EnumLocationType.WAYSTONE);
				if(!waystoneLocations.isEmpty()) {
					LocationStorage location = waystoneLocations.get(0);

					if(stack.hasDisplayName()) {
						location.setName(stack.getDisplayName());
						location.setVisible(true);
						location.setChanged();
					} else {
						location.setName("waystone");
						location.setVisible(false);
						location.setChanged();
					}
				}
			} else {
				this.spawnWaystoneParticles(world, pos, part);
			}

			return true;
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	protected void spawnWaystoneParticles(World world, BlockPos pos, BlockWaystone.Part part) {
		int startY = part == BlockWaystone.Part.BOTTOM ? 0 : (part == BlockWaystone.Part.MIDDLE ? -1 : -2);
		for(int yo = startY; yo < startY + 3; yo++) {
			for(int i = 0; i < 4; i++) {
				Vector3d dir = new Vector3d(world.rand.nextFloat() - 0.5F, world.rand.nextFloat() - 0.5F + 0.25F, world.rand.nextFloat() - 0.5F);
				dir = dir.normalize().scale(2);
				BLParticles.CORRUPTED.spawn(world, pos.getX() + 0.5D + world.rand.nextFloat() / 2.0F - 0.25F, pos.getY() + yo + 0.5D + world.rand.nextFloat() / 2.0F - 0.25F, pos.getZ() + 0.5D + world.rand.nextFloat() / 2.0F - 0.25F, ParticleArgs.get().withMotion(dir.x, dir.y, dir.z));
			}
		}
	}

	@Nullable
	public BlockPos getBoundWaystone(ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
			if(nbt.contains("link", Constants.NBT.TAG_LONG)) {
				return BlockPos.of(nbt.getLong("link"));
			}
		}
		return null;
	}

	public void setBoundWaystone(ItemStack stack, @Nullable BlockPos pos) {
		CompoundNBT nbt = stack.getTag();
		if(pos == null) {
			if(nbt != null) {
				nbt.removeTag("link");
				stack.setTag(nbt);
			}
		} else {
			if(nbt == null) {
				nbt = new CompoundNBT();
			}
			nbt.setLong("link", pos.toLong());
			stack.setTag(nbt);
		}
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return 8;
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return 32;
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return 16;
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return 38;
	}
}
