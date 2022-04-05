package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemBarkAmulet extends Item implements IEquippable {
	public ItemBarkAmulet() {
		this.setMaxDamage(14400);
		this.setMaxStackSize(1);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		IEquippable.addEquippedPropertyOverrides(this);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public EnumEquipmentInventory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentInventory.MISC;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, PlayerEntity player, Entity target) {
		return true;
	}

	@Override
	public boolean canEquip(ItemStack stack, PlayerEntity player, Entity target) {
		return player == target && EquipmentHelper.getEquipment(EnumEquipmentInventory.MISC, target, this).isEmpty();
	}

	@Override
	public boolean canUnequip(ItemStack stack, PlayerEntity player, Entity target, IInventory inventory) {
		return true;
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, IInventory inventory) {
		return true;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) { }

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { }

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(!entity.level.isClientSide() && entity instanceof LivingEntity && entity.tickCount % 20 == 0) {
			stack.damageItem(1, (LivingEntity) entity);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		if(!Minecraft.getInstance().isGamePaused()) {
			Entity view = Minecraft.getInstance().getCameraEntity();
			if(view != null) {
				if(!EquipmentHelper.getEquipment(EnumEquipmentInventory.MISC, view, ItemRegistry.BARK_AMULET).isEmpty() || (view instanceof LivingEntity && ((LivingEntity) view).isPotionActive(ElixirEffectRegistry.ENLIGHTENED))) {
					final float range = 12.0F;

					List<LivingEntity> entities = view.world.getEntitiesOfClass(LivingEntity.class, view.getBoundingBox().inflate(range), e -> e.getDistanceSq(view) <= range * range);

					for(LivingEntity entity : entities) {
						if(entity != view && entity.tickCount % 50 == 0) {
							BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.LIFE_ESSENCE.create(entity.world, 0, entity.height + 0.2D, 0, ParticleArgs.get().withScale(entity.width / 2.0F).withData(entity, entity.tickCount)));
						}
					}
				}
			}
		}
	}
}
