package thebetweenlands.common.network.clientbound;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.network.MessageEntity;
import thebetweenlands.common.registries.SoundRegistry;

public class MessageShockArrowHit extends MessageEntity {
	public MessageShockArrowHit() {

	}

	public MessageShockArrowHit(List<Pair<Entity, Entity>> chain) {
		for(Pair<Entity, Entity> pair : chain) {
			this.addEntity(pair.getLeft());
			this.addEntity(pair.getRight());
		}
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.CLIENT) {
			List<Entity> entities = this.getEntities();

			for(int i = 0; i < entities.size() / 2; i++) {
				Entity from = entities.get(i * 2);
				Entity to = entities.get(i * 2 + 1);

				if(from != null && to != null) {
					Particle particle = BLParticles.LIGHTNING_ARC.create(from.world, from.getX(), from.getY() + from.height / 2, from.getZ(), 
							ParticleArgs.get().withColor(0.3f, 0.5f, 1.0f, 0.9f).withData(new Vector3d(to.getX(), to.getY() + to.height / 2, to.getZ())));
					
					BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, particle);
					
					from.world.playSound(from.getX(), from.getY(), from.getZ(), SoundRegistry.ZAP, SoundCategory.PLAYERS, 1, 1, false);
				}
			}
		}

		return null;
	}
}
