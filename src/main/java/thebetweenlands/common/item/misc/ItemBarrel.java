package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.tile.TileEntityBarrel;

public class ItemBarrel extends BlockItem {
	private static final String NBT_FLUID_STACK = "bl.fluidStack";

	public ItemBarrel(Block block) {
		super(block);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return stack.getTag() != null ? 1 : super.getItemStackLimit(stack);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		FluidStack fluidStack = this.getFluidStack(stack);
		if(fluidStack != null) {
			tooltip.add(fluidStack.getLocalizedName() + " (" + fluidStack.amount + "mb)");
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side, BlockRayTraceResult hitResult, BlockState newState) {
		if(super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
			FluidStack fluidStack = this.getFluidStack(stack);
			if(fluidStack != null) {
				TileEntity te = world.getBlockEntity(pos);

				if(te instanceof TileEntityBarrel) {
					((TileEntityBarrel) te).fill(fluidStack, true);
				}
			}
			return true;
		}
		return false;
	}

	public FluidStack getFluidStack(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		if(nbt != null && nbt.contains(NBT_FLUID_STACK, Constants.NBT.TAG_COMPOUND)) {
			return FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(NBT_FLUID_STACK));
		}
		return null;
	}

	public ItemStack fromBarrel(TileEntityBarrel te) {
		ItemStack stack = new ItemStack(this);

		IFluidTankProperties props = te.getTankProperties()[0];
		FluidStack fluidStack = props.getContents();

		if(fluidStack != null && fluidStack.amount > 0) {
			stack.setTagInfo(NBT_FLUID_STACK, fluidStack.save(new CompoundNBT()));
		}

		return stack;
	}
}
