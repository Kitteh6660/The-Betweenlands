package thebetweenlands.common.item.farming;

import net.minecraft.block.BlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityDugSoil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ItemPlantTonic extends Item implements ItemRegistry.IMultipleItemModelDefinition {

	public ItemPlantTonic() {
		this.setCreativeTab(BLCreativeTabs.GEARS);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this, 1, 0));
			items.add(new ItemStack(this, 1, 1));
		}
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return getUsages(stack) / 3F;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getUsages(stack) > 0;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		if (!stack.hasTag())
			stack.setTag(new CompoundNBT());
		return super.initCapabilities(stack, nbt);
	}

	@Override
	public ActionResultType onItemUse( PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = player.getItemInHand(hand);
		BlockState state = world.getBlockState(pos);

		if(state.getBlock() instanceof IPlantable || state.getBlock() instanceof IFarmablePlant) {
			while(world.getBlockState(pos).getBlock() instanceof IPlantable || world.getBlockState(pos).getBlock() instanceof IFarmablePlant) {
				pos = pos.below();
			}
		}

		state = world.getBlockState(pos);

		if(state.getBlock() instanceof BlockGenericDugSoil && BlockGenericDugSoil.getTile(world, pos) != null) {
			boolean cured = false;

			for(int xo = -2; xo <= 2; xo++) {
				for(int yo = -2; yo <= 2; yo++) {
					for(int zo = -2; zo <= 2; zo++) {
						BlockPos offsetPos = pos.offset(xo, yo, zo);
						TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, offsetPos);
						if(te != null && te.getDecay() > 0) {
							cured = true;
							if(!world.isClientSide()) {
								te.setDecay(0);
							} else {
								ItemDye.spawnBonemealParticles(world, offsetPos.above(), 6);
							}
						}
					}
				}
			}

			if(cured) {
				if(!world.isClientSide() && !player.isCreative()) {
					setUsages(stack, getUsages(stack) + 1);
					if(getUsages(stack) >= 3) {
						player.setItemInHand(hand, ItemRegistry.BL_BUCKET.getEmpty(new ItemStack(ItemRegistry.BL_BUCKET, 1, stack.getMetadata())));
					}
				}

				world.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1, 1);

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	private CompoundNBT getNBT(ItemStack stack) {
		CompoundNBT compound = stack.getTag();
		if (compound == null) {
			compound = new CompoundNBT();
			compound.putInt("usages", 0);
			stack.setTag(compound);
		}
		return compound;
	}

	private void setUsages(ItemStack stack, int usage) {
		getNBT(stack).putInt("usages", Math.max(usage, 0));
	}

	private int getUsages(ItemStack stack) {
		return getNBT(stack).getInt("usages");
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (stack.getMetadata() >= 2)
			return getTranslationKey() + ".unknown";
		return getTranslationKey() + (stack.getMetadata() == 0 ? "_weedwood": "_syrmorite");
	}

	@Override
	public Map<Integer, ResourceLocation> getModels() {
		Map<Integer, ResourceLocation> models = new HashMap<>();
		models.put(0, new ResourceLocation(getRegistryName().toString() + "_weedwood"));
		models.put(1, new ResourceLocation(getRegistryName().toString() + "_syrmorite"));
		return models;
	}
}
