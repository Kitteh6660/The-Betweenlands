package thebetweenlands.common.item.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;

public class ItemChiromawTame extends ItemMob {
	private final boolean electric;

	public ItemChiromawTame(boolean electric) {
		super(1, EntityChiromawTame.class, entity -> entity.setElectricBoogaloo(electric));
		this.electric = electric;
	}

	@Override
	protected void spawnCapturedEntity(PlayerEntity player, World world, Entity entity) {
		if (entity instanceof EntityChiromawTame) {
			((EntityChiromawTame) entity).setOwnerId(player.getUUID());
		}

		super.spawnCapturedEntity(player, world, entity);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return getTranslationKey();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return this.electric;
	}
}
