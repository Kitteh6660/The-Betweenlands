package thebetweenlands.common.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityPreventUnmount;

public class EntityUnmountHandler {
	@SubscribeEvent
	public static void onEntityMountEvent(EntityMountEvent event) {
		if(event.isDismounting()) {
			Entity rider = event.getEntityMounting();
			Entity mount = event.getEntityBeingMounted();

			if(mount instanceof IEntityPreventUnmount && rider instanceof PlayerEntity && mount.isEntityAlive() && rider.isEntityAlive() && rider.isCrouching() && ((IEntityPreventUnmount) mount).isUnmountBlocked((PlayerEntity) rider)) {
				CompoundNBT nbt = rider.getEntityData();

				//Allow blocking unmount just once per tick. If it tries unmounting the player multiple times per tick then
				//that means the player is (also) being unmounted by something else other than the player's controls.
				if(nbt.getLong("thebetweenlands.unmount.lastBlockedTime") != rider.world.getGameTime()) {
					nbt.setLong("thebetweenlands.unmount.lastBlockedTime", rider.world.getGameTime());
					((IEntityPreventUnmount) mount).onUnmountBlocked((PlayerEntity) rider);
					event.setCanceled(true);
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onRenderHUD(Pre event) {
		if(event.getType() == ElementType.HEALTHMOUNT || event.getType() == ElementType.ALL) {
			EntityPlayerSP player = Minecraft.getInstance().player;

			if(player != null && player.isRiding()) {
				Entity mount = player.getRidingEntity();

				if(mount instanceof IEntityPreventUnmount) {
					if(event.getType() == ElementType.HEALTHMOUNT) {
						event.setCanceled(true);
					} else if(event.getType() == ElementType.ALL && ((IEntityPreventUnmount) mount).shouldPreventStatusBarText(player)) {
						Minecraft.getInstance().ingameGUI.setOverlayMessage("", false);
					}
				}
			}
		}
	}
}
