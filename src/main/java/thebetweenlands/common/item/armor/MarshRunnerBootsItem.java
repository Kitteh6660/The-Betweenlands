package thebetweenlands.common.item.armor;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import thebetweenlands.common.block.fluid.SwampWaterBlock;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.NBTHelper;

public class MarshRunnerBootsItem extends RubberBootsItem {
	
	private static final int MAX_WALK_TICKS = 30;

	public MarshRunnerBootsItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isValidRepairItem(ItemStack armour, ItemStack material) {
		return material == new ItemStack(ItemRegistry.RUBBER_BALL.get());
	}

	@Override
	public void onCraftedBy(ItemStack itemStack, World world, PlayerEntity player) {
		if(itemStack.getTag() == null) {
			itemStack.setTag(new CompoundNBT());
		}
	}

	@Override
	public void onArmorTick(ItemStack itemStack, World world, PlayerEntity player) {
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(itemStack);
		int walkTicksLeft = nbt.getInt("walkTicksLeft");
		BlockState blockBelowPlayer = world.getBlockState(player.blockPosition().below());

		if(player.isOnGround() && blockBelowPlayer.getBlock() instanceof SwampWaterBlock) {
			player.xo *= 1.0D / MAX_WALK_TICKS * walkTicksLeft;
			player.zo *= 1.0D / MAX_WALK_TICKS * walkTicksLeft;
		}
		if(!player.level.isClientSide()) {
			boolean playerOnGround = player.isOnGround() && !player.isInWater() && blockBelowPlayer.getBlock() instanceof SwampWaterBlock == false;
			if(walkTicksLeft == 0 || playerOnGround) {
				nbt.putInt("walkTicksLeft", MAX_WALK_TICKS);
			} else {
				if(walkTicksLeft > 1) {
					nbt.putInt("walkTicksLeft", --walkTicksLeft);
				}
			}
		}
	}

	public static boolean checkPlayerWalkOnWater(PlayerEntity player) {
		if(player.isCrouching() || ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.isActive(player)) return false;
		ItemStack boots = player.getItemBySlot(EquipmentSlotType.FEET);
		if(!boots.isEmpty() && boots.getItem() instanceof MarshRunnerBootsItem) {
			if(boots.getTag() != null && boots.getTag().getInt("walkTicksLeft") > 1) {
				return true;
			}
		}
		return false;
	}

	public static double getWalkPercentage(PlayerEntity player) {
		ItemStack boots = player.getItemBySlot(EquipmentSlotType.FEET);
		if(!boots.isEmpty() && boots.getItem() instanceof MarshRunnerBootsItem) {
			if(boots.getTag() != null) {
				return (double)boots.getTag().getInt("walkTicksLeft") / (double)MAX_WALK_TICKS;
			}
		}
		return 0.0D;
	}
}