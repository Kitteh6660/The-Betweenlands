package thebetweenlands.common.item.herblore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.items.ItemHandlerHelper;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class ItemDentrothystVial extends Item implements ItemRegistry.IBlockStateItemModelDefinition {
	
	private static final class DentrothystVialFluidHandler extends FluidHandlerItemStackSimple {
		public DentrothystVialFluidHandler(final ItemStack container, final int capacity) {
			super(container, capacity);
		}

		@Override
		public boolean canFillFluidType(FluidStack fluid) {
			return ItemRegistry.DENTROTHYST_FLUID_VIAL.get().canFillWith(container, fluid);
		}

		@Nullable
		@Override
		public FluidStack getFluid() {
			return null;
		}

		@Override
		protected void setContainerToEmpty() {
			
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			if(this.container.getDamageValue() == 1) {
				//Dirty vial can't be filled
				return 0;
			}
			ItemStack fluidVial = new ItemStack(ItemRegistry.DENTROTHYST_FLUID_VIAL, 1, this.container.getDamageValue() == 2 ? 1 : 0);
			IFluidHandlerItem handler = FluidUtil.getFluidHandler(fluidVial);
			int filled = handler.fill(resource, doFill);
			if(filled > 0 && doFill) {
				this.container = fluidVial;
			}
			return filled;
		}
	}
	
    public ItemDentrothystVial() {
        this.setCreativeTab(BLCreativeTabs.HERBLORE);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (isInCreativeTab(tab)) {
            list.add(new ItemStack(this, 1, 0)); //green
            list.add(new ItemStack(this, 1, 1)); //green dirty
            list.add(new ItemStack(this, 1, 2)); //orange
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        String s = "item.thebetweenlands.elixir.dentrothyst_vial.";
        try {
            switch(stack.getDamageValue()) {
                case 0:
                    return s + "green";
                case 1:
                    return s + "dirty";
                case 2:
                    return s + "orange";
            }
        } catch (Exception e) { }
        return "item.thebetweenlands.unknown";
    }

    /**
     * Creates an item stack of the specified vial type.
     * Vial types: 0 = green, 1 = dirty, 2 = orange
     * @param vialType
     * @return
     */
    public ItemStack createStack(int vialType) {
        return new ItemStack(this, 1, vialType);
    }

    /**
     * Creates an item stack of the specified vial type.
     * Vial types: 0 = green, 1 = dirty, 2 = orange
     * @param vialType
     * @param size
     * @return
     */
    public ItemStack createStack(int vialType, int size) {
        return new ItemStack(this, size, vialType);
    }

    @Override
    public Map<Integer, String> getVariants() {
        Map<Integer, String> variants = new HashMap<>();
        variants.put(0, "green");
        variants.put(1, "dirty");
        variants.put(2, "orange");
        return variants;
    }
    
    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return world.getBlockState(pos).getBlock() == BlockRegistry.ASPECT_VIAL_BLOCK;
    }

    @Override
    public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if(player.isCrouching() && facing == Direction.UP && stack.getDamageValue() != 1) {
            if(world.isEmptyBlock(pos.above()) && BlockRegistry.ASPECT_VIAL_BLOCK.canPlaceBlockAt(world, pos.above())) {
                if(!level.isClientSide()) {
                    ItemAspectVial.placeAspectVial(world, pos.above(), stack.getDamageValue() == 2 ? 1 : 0, null);
                    stack.shrink(1);
                }
                return ActionResultType.SUCCESS;
            }
        } else if(!player.isCrouching() && stack.getDamageValue() != 1) {
        	List<LocationStorage> locations = LocationStorage.getLocations(world, new AxisAlignedBB(pos));
        	
        	for(LocationStorage location : locations) {
        		if(location instanceof LocationSludgeWormDungeon) {
        			LocationSludgeWormDungeon dungeon = (LocationSludgeWormDungeon) location;
        			
        			if(dungeon.hasGroundFog(pos)) {
	        			int floor = dungeon.getFloor(pos);
	        			
	        			if(floor == 5 || floor == 6) {
	        				if(!level.isClientSide()) {
	        					stack.shrink(1);
	        					world.playLocalSound(null, pos, FluidRegistry.FOG.getFillSound(new FluidStack(FluidRegistry.FOG, 1000)), SoundCategory.BLOCKS, 1.0F, 1.0F);
	        					ItemHandlerHelper.giveItemToPlayer(player, ItemRegistry.DENTROTHYST_FLUID_VIAL.withFluid(stack.getDamageValue() == 2 ? 1 : 0, FluidRegistry.FOG));
	        				}
	        				
	        				return ActionResultType.SUCCESS;
	        			} else if(!level.isClientSide()) {
	        				player.displayClientMessage(new TranslationTextComponent("chat.not_enough_fog_for_vial"), true);
	        			}
        			}
        		}
        	}
        }
        return ActionResultType.FAIL;
    }
    
    @Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new DentrothystVialFluidHandler(stack, ItemRegistry.DENTROTHYST_FLUID_VIAL.getCapacity());
	}
}
