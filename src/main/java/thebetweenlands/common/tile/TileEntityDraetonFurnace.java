package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.util.NonNullDelegateList;

public class TileEntityDraetonFurnace extends TileEntityBLFurnace {
	private TileEntityDraetonFurnace(NonNullList<ItemStack> inventory) {
		super("container.bl.draeton_furnace", inventory);
	}

	public static TileEntityDraetonFurnace create(NonNullList<ItemStack> inventory, int index) {
		NonNullList<ItemStack> sublist = new NonNullDelegateList<ItemStack>(inventory.subList(index * 4, index * 4 + 4), ItemStack.EMPTY);
		return new TileEntityDraetonFurnace(sublist);
	}

	public CompoundNBT writeDreatonFurnaceData(CompoundNBT nbt) {
		return this.writeFurnaceData(nbt);
	}

	public void readDreatonFurnaceData(CompoundNBT nbt) {
		this.readFurnaceData(nbt);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		//no-op
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		//no-op
	}

	@Override
	protected void updateState(boolean active) {
		//TE is on draeton, don't set block
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		//Check if furnace is in draeton
		List<EntityDraeton> draetons = player.level.getEntitiesOfClass(EntityDraeton.class, player.getBoundingBox().inflate(6));
		for(EntityDraeton dreaton : draetons) {
			if(player.distanceToSqr(dreaton) <= 64.0D) {
				for(int i = 0; i < 4; i++) {
					if(this == dreaton.getFurnace(i)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
