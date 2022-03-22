package thebetweenlands.client.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

@OnlyIn(Dist.CLIENT)
public class ThemHandler {
	private static final List<Particle> activeParticles = new ArrayList<Particle>();

	@SubscribeEvent
	public static void onTick(ClientTickEvent event) {
		if(event.phase == Phase.END && !Minecraft.getInstance().isGamePaused()) {
			World world = TheBetweenlands.proxy.getClientWorld();
			Entity viewer = Minecraft.getInstance().getRenderViewEntity();
			if(world != null && viewer != null && viewer.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && FogHandler.hasDenseFog(world) && (FogHandler.getCurrentFogEnd() + FogHandler.getCurrentFogStart()) / 2 < 65.0F) {
				Iterator<Particle> it = activeParticles.iterator();
				while(it.hasNext()) {
					Particle particle = it.next();
					if(!particle.isAlive()) {
						it.remove();
					}
				}
				if(activeParticles.size() < 4) {
					BlockPos worldHeight = world.getHeight(viewer.getPosition());
					if(viewer.getY() >= worldHeight.getY() - 3 && SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(world.getBlockState(worldHeight.below()))) {
						int probability = (int) (FogHandler.getCurrentFogEnd() + FogHandler.getCurrentFogStart()) / 2 * 10 + 60;
						if(world.rand.nextInt(probability) == 0) {
							double xOff = world.rand.nextInt(50) - 25;
							double zOff = world.rand.nextInt(50) - 25;
							double sx = viewer.getX() + xOff;
							double sz = viewer.getZ() + zOff;
							double sy = worldHeight.getY() + world.rand.nextFloat() * 0.75f;
							Particle particle = BLParticles.THEM.spawn(world, sx, sy, sz);
							activeParticles.add(particle);
						}
					}
				}
			}
		}
	}
}
