package thebetweenlands.client.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.api.capability.ISummoningCapability;
import thebetweenlands.api.entity.IEntityCameraOffset;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.common.item.equipment.ItemRingOfSummoning;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationCragrockTower;

public class CameraPositionHandler {
	
	public static CameraPositionHandler INSTANCE = new CameraPositionHandler();

	private float getShakeStrength(Entity renderViewEntity) {
		float screenShake = 0.0F;
		World world = renderViewEntity.level;
		
		if(renderViewEntity != null) {
			for(Entity entity : (List<Entity>) world.loadedEntityList) {
				if(entity instanceof IEntityScreenShake) {
					IEntityScreenShake shake = (IEntityScreenShake) entity;
					screenShake += shake.getShakeIntensity(renderViewEntity);
				}
			}

			for(TileEntity tile : (List<TileEntity>) world.loadedBlockEntityList) {
				if(tile instanceof IEntityScreenShake) {
					IEntityScreenShake shake = (IEntityScreenShake) tile;
					screenShake += shake.getShakeIntensity(renderViewEntity);
				}
			}
			
			//Crumbling cragrock tower
			BetweenlandsWorldStorage worldData = BetweenlandsWorldStorage.forWorld(world);
			List<LocationCragrockTower> towers = worldData.getLocalStorageHandler().getLocalStorages(LocationCragrockTower.class, renderViewEntity.getX(), renderViewEntity.getZ(), location -> location.getInnerBoundingBox().grow(4, 4, 4).contains(renderViewEntity.getPositionVector()));
			for(LocationCragrockTower tower : towers) {
				if(tower.isCrumbling()) {
					screenShake += Math.min(Math.pow(tower.getCrumblingTicks() / 400.0f, 4) * 0.08f, 0.08f);
				}
			}

			//Ring of Summoning
			List<PlayerEntity> nearbyPlayers = renderViewEntity.level.getEntitiesOfClass(PlayerEntity.class, renderViewEntity.getBoundingBox().inflate(32, 32, 32), entity -> entity.distanceTo(renderViewEntity) <= 32.0D);

			for(PlayerEntity player : nearbyPlayers) {
				ISummoningCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_SUMMON, null);
				if (cap != null) {
					if(cap.isActive()) {
						screenShake += (ItemRingOfSummoning.MAX_USE_TIME - cap.getActiveTicks()) / (float)ItemRingOfSummoning.MAX_USE_TIME * 0.1F + 0.01F;
					}
				}
			}
		}
		
		return MathHelper.clamp(screenShake, 0.0F, 0.15F);
	}

	private double xOld;
	private double yOld;
	private double zOld;
	private boolean didChange = false;

	private List<IEntityCameraOffset> offsetEntities = new ArrayList<>();
	private float shakeStrength = 0.0f;
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		Entity renderViewEntity = Minecraft.getInstance().getCameraEntity();

		if(renderViewEntity != null) {
			this.shakeStrength = this.getShakeStrength(renderViewEntity);
			
			this.offsetEntities.clear();
			
			List<IEntityCameraOffset> offsetEntities = new ArrayList<IEntityCameraOffset>();
			for(Entity entity : (List<Entity>) renderViewEntity.level.loadedEntityList) {
				if(entity instanceof IEntityCameraOffset)
					offsetEntities.add((IEntityCameraOffset)entity);
			}
		} else {
			this.shakeStrength = 0.0f;
			this.offsetEntities.clear();
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onRenderTickStart(RenderTickEvent event) {
		Entity renderViewEntity = Minecraft.getInstance().getCameraEntity();

		if(renderViewEntity != null) {
			boolean shouldChange = this.shakeStrength > 0.0F || !this.offsetEntities.isEmpty();

			if((shouldChange && !Minecraft.getInstance().isPaused()) || this.didChange) {
				if(event.phase == Phase.START) {
					this.xOld = renderViewEntity.getX();
					this.yOld = renderViewEntity.getY();
					this.zOld = renderViewEntity.getZ();
					
					Random rnd = renderViewEntity.level.random;
					
					renderViewEntity.setPos(renderViewEntity.getX() + rnd.nextFloat() * this.shakeStrength, renderViewEntity.getY() + rnd.nextFloat() * this.shakeStrength, renderViewEntity.getZ() + rnd.nextFloat() * this.shakeStrength);
					
					if(!this.offsetEntities.isEmpty()) {
						for(IEntityCameraOffset offset : offsetEntities)
							if(((Entity) offset).isAlive() && offset.applyOffset(renderViewEntity, event.renderTickTime))
								break;
					}
					
					this.didChange = true;
				} else {
					renderViewEntity.setPos(renderViewEntity.xOld, renderViewEntity.yOld, renderViewEntity.zOld);
					
					this.didChange = false;
				}
			}
		}
	}
}
