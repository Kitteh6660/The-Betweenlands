package thebetweenlands.common.item.tools;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.container.BlockInfuser;
import thebetweenlands.common.block.misc.BlockRubberTap;
import thebetweenlands.common.block.terrain.BlockRubberLog;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBLBucket extends UniversalBucket implements ItemRegistry.IMultipleItemModelDefinition {

    private final ItemStack emptyWeedwood;
    private final ItemStack emptySyrmorite;

    public ItemBLBucket() {
        this(null);
    }

    public ItemBLBucket(Item bucket) {
        super(Fluid.BUCKET_VOLUME, ItemStack.EMPTY, true);
        this.emptyWeedwood = new ItemStack(bucket != null ? bucket: this, 1, 0);
        this.emptySyrmorite = new ItemStack(bucket != null ? bucket: this, 1, 1);
        CompoundNBT nbt = new CompoundNBT();
        nbt.setTag(FluidHandlerItemStackSimple.FLUID_NBT_KEY, new CompoundNBT());
        this.emptyWeedwood.setTag(nbt);
        this.emptySyrmorite.setTag(nbt.copy());
        this.setHasSubtypes(true);
        this.setMaxStackSize(16);
        this.setMaxDamage(0);
        this.setCreativeTab(BLCreativeTabs.GEARS);
    }
    
    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
    	CompoundNBT nbt = stack.getTag();
    	if(nbt == null) {
    		nbt = new CompoundNBT();
    	}
        nbt.setTag(FluidHandlerItemStackSimple.FLUID_NBT_KEY, new CompoundNBT());
        stack.setTag(nbt);
    }

    @Override
    public Map<Integer, ResourceLocation> getModels() {
        Map<Integer, ResourceLocation> models = new HashMap<>();
        models.put(0, new ResourceLocation(getRegistryName().toString() + "_weedwood"));
        models.put(1, new ResourceLocation(getRegistryName().toString() + "_syrmorite"));
        return models;
    }

    private static final class FluidBLBucketHandler extends FluidHandlerItemStackSimple {
        public FluidBLBucketHandler(final ItemStack container, final int capacity) {
            super(container, capacity);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            boolean canHold = container.getMetadata() == 1 || (container.getMetadata() == 0 && fluid.getFluid().getTemperature(fluid) <= 430);
            return canHold && (fluid.getFluid() == FluidRegistry.WATER || fluid.getFluid() == FluidRegistry.LAVA || fluid.getFluid().getName().equals("milk") || FluidRegistry.getBucketFluids().contains(fluid.getFluid()));
        }

        @Nullable
        @Override
        public FluidStack getFluid() {
            return ((ItemBLBucket) container.getItem()).getFluid(container);
        }

        @Override
        protected void setContainerToEmpty() {
            container = ((ItemBLBucket) container.getItem()).getEmpty(container).copy();
        }
        
        @Override
        protected void setFluid(FluidStack fluid) {
        	// Replace bucket with specific bucket if available, fix for #648
        	if(!container.isEmpty() && container.isItemEqualIgnoreDurability(((ItemBLBucket) container.getItem()).getEmpty(container))) {
        		ItemSpecificBucket specificBucket = ItemSpecificBucket.getSpecificBucket(fluid.getFluid());
        		if(specificBucket != null) {
	        		container = new ItemStack(specificBucket, 1, container.getMetadata());
	        	}
        	}
        	
        	super.setFluid(fluid);
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        FluidStack fluidStack = getFluid(stack);
        if (fluidStack != null)
            return 1;
        return super.getItemStackLimit(stack);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        if (!stack.hasTag())
            stack.setTag(new CompoundNBT());
        return new FluidBLBucketHandler(stack, getCapacity());
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (stack.getMetadata() >= 2)
            return getTranslationKey() + ".unknown";
        return getTranslationKey() + (stack.getMetadata() == 0 ? "_weedwood": "_syrmorite");
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        final FluidStack fluidStack = getFluid(stack);
        final String unlocName = getEmpty(stack).getTranslationKey();

        if (fluidStack == null)
            return I18n.get(unlocName + ".name").trim();

        String fluidUnlocKey = unlocName + "." + fluidStack.getUnlocalizedName() + ".name"; //Unlocalized full bucket name
        //Try to find localization for this specific fluid bucket, if not found use a generic name with the fluid passed in as %s
        if (I18n.canTranslate(fluidUnlocKey))
            return I18n.get(fluidUnlocKey).trim();

        return I18n.translateToLocalFormatted(getEmpty(stack).getTranslationKey() + ".filled.name", fluidStack.getFluid().getRarity(fluidStack).color + fluidStack.getLocalizedName() + TextFormatting.WHITE);
    }

    @Override
    public ActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack heldItem = player.getItemInHand(hand);
        final FluidStack fluidStack = getFluid(heldItem);

        if(world.isClientSide())
            return new ActionResult<>(ActionResultType.PASS, heldItem);

        if (fluidStack == null) {
            ActionResult<ItemStack> tapResult = tryPlaceTreeTap(world, player, heldItem);
            if (tapResult.getType() == ActionResultType.SUCCESS)
                return tapResult;
        }

        final RayTraceResult target = rayTrace(world, player, true);

        if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK)
            return new ActionResult<>(ActionResultType.PASS, heldItem);
        final BlockPos pos = target.getBlockPos();

        if (fluidStack != null) {
            ActionResult<ItemStack> result = super.onItemRightClick(world, player, hand);
            if (result.getType() == ActionResultType.SUCCESS)
                world.playSound(null, pos, fluidStack.getFluid().getEmptySound(fluidStack), SoundCategory.BLOCKS, 1.0F, 1.0F);
            return result;
        }

        ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(player, world, heldItem, target);
        if (ret != null) return ret;

        if (world.isBlockModifiable(player, pos) && player.mayUseItemAt(pos, target.sideHit, heldItem)) {
            final ItemStack singleBucket = heldItem.copy();
            singleBucket.setCount(1);

            final FluidActionResult filledResult = FluidUtil.tryPickUpFluid(singleBucket, player, world, pos, target.sideHit);
            if (filledResult.isSuccess()) {
                FluidStack fluidStack1 = getFluid(filledResult.getResult());
                world.playSound(null, pos, fluidStack1.getFluid().getFillSound(fluidStack1), SoundCategory.BLOCKS, 1.0F, 1.0F);
                final ItemStack filledBucket = filledResult.result;
                if (player.isCreative())
                    return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
                heldItem.shrink(1);
                if (heldItem.isEmpty())
                    return new ActionResult<>(ActionResultType.SUCCESS, filledBucket);
                ItemHandlerHelper.giveItemToPlayer(player, filledBucket);
                return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
            }
            return new ActionResult<>(ActionResultType.PASS, heldItem);
        } else {
            return new ActionResult<>(ActionResultType.FAIL, heldItem);
        }
    }

    private ActionResult<ItemStack> tryPlaceTreeTap(final World world, final PlayerEntity player, final ItemStack itemStack) {
        RayTraceResult result = rayTrace(world, player, true);

        if(result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && result.sideHit.getAxis() != Direction.Axis.Y) {
            BlockPos pos = result.getBlockPos();

            if(player.mayUseItemAt(pos, result.sideHit, itemStack)) {
                BlockState blockState = world.getBlockState(pos);
                Block rubberTap = itemStack.getMetadata() == 0 ? BlockRegistry.WEEDWOOD_RUBBER_TAP: BlockRegistry.SYRMORITE_RUBBER_TAP;

                if(blockState.getBlock() == BlockRegistry.LOG_RUBBER && blockState.getValue(BlockRubberLog.NATURAL)) {
                    BlockPos offset = pos.offset(result.sideHit);
                    if(world.getBlockState(offset).getBlock().isReplaceable(world, offset) && rubberTap.canPlaceBlockAt(world, offset)) {
                        world.setBlockState(offset, rubberTap.defaultBlockState().setValue(BlockRubberTap.FACING, result.sideHit));
                        itemStack.shrink(1);

                        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.PLAYERS, 1, 1);

                        return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
                    }
                }
            }
        }
        return new ActionResult<>(ActionResultType.FAIL, itemStack);
    }

    @Override
    public void getSubItems(@Nullable final CreativeTabs tab, final NonNullList<ItemStack> subItems) {
        if (!this.isInCreativeTab(tab)) return;

        subItems.add(emptyWeedwood.copy());
        subItems.add(emptySyrmorite.copy());

        for (int i = 0; i < 2; i++) {
            for (final Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
                if (fluid != FluidRegistry.WATER && fluid != FluidRegistry.LAVA && !fluid.getName().equals("milk") && !ItemSpecificBucket.hasSpecificBucket(fluid)) {
                    if (!BetweenlandsConfig.COMPATIBILITY.showNonBLFluids && !thebetweenlands.common.registries.FluidRegistry.REGISTERED_FLUIDS.contains(fluid))
                        continue;
                    if (i == 0 && fluid.getTemperature() > 430)
                        continue;
                    // Add all fluids that the bucket can be filled with
                    final FluidStack fs = new FluidStack(fluid, getCapacity());
                    final ItemStack stack = new ItemStack(this, 1, i);
                    final IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                    if (fluidHandler != null && fluidHandler.fill(fs, true) == fs.amount) {
                        final ItemStack filled = fluidHandler.getContainer();
                        subItems.add(filled);
                    }
                }
            }
        }
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return world.getBlockState(pos).getBlock() instanceof BlockInfuser;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
    	return !getEmpty(stack).isEmpty();
    }
    
    @Nonnull
    @Override
    public ItemStack getContainerItem(@Nonnull ItemStack itemStack) {
    	if (!getEmpty(itemStack).isEmpty()) {
            return getEmpty(itemStack).copy();
        }
        return super.getContainerItem(itemStack);
    }

    @Nullable
    @Override
    public FluidStack getFluid(final ItemStack container) {
        CompoundNBT tagCompound = container.getTag();
        if (tagCompound == null || !tagCompound.contains(FluidHandlerItemStackSimple.FLUID_NBT_KEY))
        {
            return null;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag(FluidHandlerItemStackSimple.FLUID_NBT_KEY));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        final FluidStack fluidStack = getFluid(stack);
        if (fluidStack == null)
            tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocalFormatted("tooltip.bl.bl_bucket"), 0));
    }

    public ItemStack getEmpty(ItemStack stack) {
        return stack.getMetadata() == 1 ? emptySyrmorite.copy() : emptyWeedwood.copy();
    }

    public ItemStack withFluid(int meta, Fluid fluid) {
        final FluidStack fs = new FluidStack(fluid, getCapacity());
        final ItemStack stack = new ItemStack(this, 1, meta);
        final IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        fluidHandler.fill(fs, true);
        return fluidHandler.getContainer();
    }

}
