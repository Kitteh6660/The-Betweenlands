package thebetweenlands.common.item.equipment;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;
import thebetweenlands.util.PlayerUtil;

public class ItemRingOfDispersion extends ItemRing {
	public static final String NBT_ACTIVE = "ring_of_dispersion.active";
	public static final String NBT_TIMER = "ring_of_dispersion.timer";
	public static final String NBT_LAST_VALID_POS = "ring_of_dispersion.last_valid_pos";

	public ItemRingOfDispersion(Properties properties) {
		super(properties);
		this.setMaxDamage(300);
	}

	public void setActive(ItemStack stack, boolean active) {
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.putBoolean(NBT_ACTIVE, active);
	}

	public boolean isActive(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean(NBT_ACTIVE);
	}

	public void setTimer(ItemStack stack, int ticks) {
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.putInt(NBT_TIMER, ticks);
	}

	public int getTimer(ItemStack stack) {
		return stack.hasTag() ? stack.getTag().getInt(NBT_TIMER) : 0;
	}

	public void setLastValidPos(ItemStack stack, @Nullable BlockPos pos) {
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
		if(pos != null) {
			nbt.putLong(NBT_LAST_VALID_POS, pos.asLong());
		} else {
			nbt.remove(NBT_LAST_VALID_POS);
		}
	}

	@Nullable
	public BlockPos getLastValidPos(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains(NBT_LAST_VALID_POS, Constants.NBT.TAG_LONG) ? BlockPos.of(stack.getTag().getLong(NBT_LAST_VALID_POS)) : null;
	}

	public int getMaxPhasingDuration(ItemStack stack) {
		return 300;
	}

	public boolean canPhase(PlayerEntity player, ItemStack stack) {
		return stack.getDamageValue() < stack.getMaxDamage() && !player.isSpectator() && player.isCrouching() && !player.getCooldowns().isOnCooldown(this);
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(!entity.level.isClientSide()) {
			if(this.isActive(stack) && entity.tickCount % 20 == 0) {
				this.drainPower(stack, entity);
			}

			BlockPos currentPos = new BlockPos(entity);
			BlockPos storedPos = this.getLastValidPos(stack);

			int storedTimer = this.getTimer(stack);

			boolean requiresTeleport = false;

			if(!this.isActive(stack)) {
				if(!currentPos.equals(storedPos)) {
					this.setLastValidPos(stack, currentPos);
				}

				if(storedTimer != 0) {
					if(entity instanceof PlayerEntity && !this.canPhase((PlayerEntity) entity, stack)) {
						requiresTeleport = true;
					}
					this.setTimer(stack, 0);
				}
			} else {
				int maxDuration = this.getMaxPhasingDuration(stack);

				if(storedTimer >= maxDuration) {
					requiresTeleport = true;

					//Add cooldown to prevent players from immediately phasing again if they
					//manage to cancel teleport somehow
					if(entity instanceof PlayerEntity) {
						((PlayerEntity) entity).getCooldowns().addCooldown(this, 300);
					}
				} else {
					this.setTimer(stack, storedTimer + 1);
				}
			}

			if(requiresTeleport && storedPos != null) {
				PlayerUtil.teleport(entity, storedPos.getX() + 0.5D, storedPos.getY(), storedPos.getZ() + 0.5D);

				this.setLastValidPos(stack, null);
				this.setTimer(stack, 0);

				entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundRegistry.RING_OF_DISPERSION_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.putBoolean(NBT_ACTIVE, false);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return this.isActive(stack);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
		list.add(new TranslationTextComponent("tooltip.bl.ring.dispersion.bonus"));
		if (Screen.hasShiftDown()) {
			String toolTip = I18n.get("tooltip.bl.ring.dispersion", KeyBindRegistry.RADIAL_MENU.getName(), Minecraft.getInstance().options.keyBindSneak.getDisplayName());
			list.add(new TranslationTextComponent(toolTip, 1));
		} else {
			list.add(new TranslationTextComponent("tooltip.bl.press.shift"));
		}
	}

	@Override
	protected float getXPConversionRate(ItemStack stack, PlayerEntity player) {
		//1 xp = 2 damage repaired
		return 2.0F;
	}
}
