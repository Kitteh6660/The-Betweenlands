package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationChiromawMatriarchNest;

public class ItemChiromawEgg extends ItemMob {
	private final boolean electric;

	public ItemChiromawEgg(boolean electric) {
		super(1, EntityChiromawHatchling.class, entity -> entity.setElectricBoogaloo(electric));
		this.electric = electric;
	}

	@Override
	public void onCapturedByPlayer(PlayerEntity player, Hand hand, ItemStack captured) {
		if(player instanceof ServerPlayerEntity) {
			AxisAlignedBB checkBox = player.getBoundingBox().inflate(8);

			if(player.world.getEntitiesOfClass(EntityChiromawHatchling.class, checkBox, entity -> entity.isEntityAlive()).isEmpty()) {
				List<LocationChiromawMatriarchNest> nests = BetweenlandsWorldStorage.forWorld(player.world).getLocalStorageHandler().getLocalStorages(LocationChiromawMatriarchNest.class, checkBox, location -> location.getBoundingBox().intersects(checkBox));
				
				if(nests.isEmpty()) {
					return;
				}
				
				for(LocationChiromawMatriarchNest nest : nests) {
					if(nest.getGuard() == null || nest.getGuard().isClear(player.world)) {
						return;
					}
				}
				
				AdvancementCriterionRegistry.CHIROMAW_MATRIARCH_NEST_RAIDED.trigger((ServerPlayerEntity) player);
			}
		}
	}

	@Override
	protected void spawnCapturedEntity(PlayerEntity player, World world, Entity entity) {
		if (entity instanceof EntityChiromawHatchling) {
			((EntityChiromawHatchling) entity).setOwnerId(player.getUUID());
			((EntityChiromawHatchling) entity).setFoodCraved(((EntityChiromawHatchling) entity).chooseNewFoodFromLootTable());
		}

		super.spawnCapturedEntity(player, world, entity);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return getTranslationKey();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isFoil(ItemStack stack) {
		return this.electric;
	}
}
