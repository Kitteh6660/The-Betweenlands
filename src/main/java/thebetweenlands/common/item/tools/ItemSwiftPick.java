package thebetweenlands.common.item.tools;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.common.ToolType;
import thebetweenlands.api.item.CorrosionHelper;

public class ItemSwiftPick extends BLPickaxeItem {
	
	public ItemSwiftPick(IItemTier tier, int damage, float speed, Properties properties) {
		super(tier, damage, speed, properties);
		//super(BLMaterialRegistry.TOOL_VALONITE);
		//this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		float digSpeed = this.speed;
		if (this.isEffective(stack, state)) {
			digSpeed = 100.0F;
		}
		return CorrosionHelper.getDestroySpeed(digSpeed, stack, state);
	}

	private boolean isEffective(ItemStack stack, BlockState state) {
		if(state.getMaterial() == Material.METAL || state.getMaterial() == Material.HEAVY_METAL || state.getMaterial() == Material.STONE) {
			return true;
		}
		for(ToolType type : stack.getItem().getToolTypes(stack)) {
			if(state.getBlock().isToolEffective(state, type)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isRepairableByAnimator(ItemStack stack) {
		return false;
	}
	
	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}
}
