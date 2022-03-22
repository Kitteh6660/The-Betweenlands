package thebetweenlands.common.item.food;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BLFoodItem extends Item
{
	private final boolean canGetSick;
	private final int decayHeal;
	private final float decayHealSaturation;
	
	public BLFoodItem(boolean canGetSick, int decayHeal, float decayHealSaturation, Properties properties) {
		super(properties);
		this.canGetSick = canGetSick;
		this.decayHeal = decayHeal;
		this.decayHealSaturation = decayHealSaturation;
	}
	
	public boolean canGetSickOf(PlayerEntity player, ItemStack stack) {
		return this.canGetSick;
	}
	
    public int getDecayHealAmount(ItemStack stack) {
        return this.decayHeal;
    }
    
	public float getDecayHealSaturation(ItemStack stack) {
		return decayHealSaturation; // Normally value is 0.2F.
	}
}
