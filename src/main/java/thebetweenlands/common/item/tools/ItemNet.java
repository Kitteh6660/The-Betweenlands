package thebetweenlands.common.item.tools;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityTinyWormEggSac;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemNet extends Item implements IAnimatorRepairable {
	public static final Multimap<Class<? extends Entity>, Pair<Supplier<? extends ItemMob>, BiPredicate<PlayerEntity, Entity>>> CATCHABLE_ENTITIES = MultimapBuilder.hashKeys().arrayListValues().build();

	@SuppressWarnings("unchecked")
	public static <T extends Entity> void register(Class<T> cls, Supplier<? extends ItemMob> item, BiPredicate<PlayerEntity, T> predicate) {
		CATCHABLE_ENTITIES.put(cls, Pair.of(item, (BiPredicate<PlayerEntity, Entity>) predicate));
	}

	//TODO: Rework this into JSON data.
	static {
		register(EntityFirefly.class, () -> ItemRegistry.CRITTER, (p, e) -> true);
		register(EntityGecko.class, () -> ItemRegistry.CRITTER, (p, e) -> true);
		register(EntityDragonFly.class, () -> ItemRegistry.CRITTER, (p, e) -> true);
		register(EntityTinyWormEggSac.class, () -> ItemRegistry.SLUDGE_WORM_EGG_SAC, (p, e) -> true);
		register(EntityChiromawHatchling.class, () -> ItemRegistry.CHIROMAW_EGG, (p, e) -> !e.getHasHatched() && !e.getElectricBoogaloo());
		register(EntityChiromawHatchling.class, () -> ItemRegistry.CHIROMAW_EGG_LIGHTNING, (p, e) -> !e.getHasHatched() && e.getElectricBoogaloo());
		register(EntityChiromawTame.class, () -> ItemRegistry.CHIROMAW_TAME, (p, e) -> e.getOwner() == p && !e.getElectricBoogaloo());
		register(EntityChiromawTame.class, () -> ItemRegistry.CHIROMAW_TAME_LIGHTNING, (p, e) -> e.getOwner() == p && e.getElectricBoogaloo());
	}

	public ItemNet() {
		this.maxStackSize = 1;
		this.setMaxDamage(32);
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
		Collection<Pair<Supplier<? extends ItemMob>, BiPredicate<PlayerEntity, Entity>>> entries = CATCHABLE_ENTITIES.get(target.getClass());

		if(entries != null) {
			if(!player.world.isClientSide()) {
				for(Pair<Supplier<? extends ItemMob>, BiPredicate<PlayerEntity, Entity>> entry : entries) {
					if(entry.getRight().test(player, target)) {
						ItemMob item = entry.getLeft().get();

						ItemStack mobItemStack = item.capture(target);

						if(!mobItemStack.isEmpty()) {
							target.setDropItemsWhenDead(false);
							target.remove();

							if(!player.inventory.addItemStackToInventory(mobItemStack))
								player.world.spawnEntity(new ItemEntity(player.world, player.getX(), player.getY(), player.getZ(), mobItemStack));

							stack.hurtAndBreak(1, player, (entity) -> {
								entity.broadcastBreakEvent(player.getUsedItemHand());
							});

							item.onCapturedByPlayer(player, hand, mobItemStack);
							
							break;
						}
					}
				}
			}

			player.swingArm(hand);
			return true;
		}
		return false;
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return 2;
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return 8;
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return 4;
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return 12;
	}
}
