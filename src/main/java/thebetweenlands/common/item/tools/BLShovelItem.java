package thebetweenlands.common.item.tools;

import java.util.List;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.Item.Properties;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;

public class BLShovelItem extends ShovelItem implements ICorrodible, IAnimatorRepairable {
	
	public BLShovelItem(IItemTier itemTier, float damage, float speed, Properties properties) {
		super(itemTier, damage, speed, properties);
		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return CorrosionHelper.shouldCauseBlockBreakReset(oldStack, newStack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return CorrosionHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return CorrosionHelper.getDestroySpeed(super.getDestroySpeed(stack, state), stack, state);
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		CorrosionHelper.updateCorrosion(itemStack, world, holder, slot, isHeldItem);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		return CorrosionHelper.getAttributeModifiers(super.getAttributeModifiers(slot, stack), slot, stack, BASE_ATTACK_DAMAGE_UUID, this.attackDamage);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		CorrosionHelper.addCorrosionTooltips(stack, tooltip, flagIn.isAdvanced());
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		if(facing == Direction.UP) {
			boolean dug = false;
			BlockState blockState = world.getBlockState(pos);

			if(blockState.getBlock() == BlockRegistry.COARSE_SWAMP_DIRT.get()) {
				world.setBlockAndUpdate(pos, BlockRegistry.DUG_SWAMP_DIRT.get().defaultBlockState());
				dug = true;
			}

			if(blockState.getBlock() == BlockRegistry.SWAMP_DIRT.get()) {
				world.setBlockAndUpdate(pos, BlockRegistry.DUG_SWAMP_DIRT.get().defaultBlockState());
				dug = true;
			}

			if(blockState.getBlock() == BlockRegistry.SWAMP_GRASS.get()) {
				world.setBlockAndUpdate(pos, BlockRegistry.DUG_SWAMP_GRASS.get().defaultBlockState());
				dug = true;
			}

			if(blockState.getBlock() == BlockRegistry.PURIFIED_SWAMP_DIRT.get()) {
				world.setBlockAndUpdate(pos, BlockRegistry.DUG_PURIFIED_SWAMP_DIRT.get().defaultBlockState());
				dug = true;
			}

			if(dug) {
				if(world.isClientSide()) {
					for(int i = 0; i < 80; i++) {
						world.addParticle(ParticleTypes.BLOCK, pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F, (world.random.nextFloat() - 0.5F) * 0.1F, world.random.nextFloat() * 0.3f, (world.random.nextFloat() - 0.5F) * 0.1F, Block.getId(blockState));
					}
				}

				SoundType sound = blockState.getBlock().getSoundType(blockState, world, pos, player);
				for(int i = 0; i < 3; i++) {
					world.playLocalSound(null, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, sound.getBreakSound(), SoundCategory.PLAYERS, 1, 0.5f + world.random.nextFloat() * 0.5f);
				}

				player.getItemInHand(hand).damageItem(1, player);

				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}
	
	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(this.getTier());
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(this.getTier());
	}
 
	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(this.getTier());
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(this.getTier());
	}
}
