package thebetweenlands.common.network.clientbound;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.network.MessageEntity;

public class MessageWightVolatileParticles extends MessageEntity {
	public MessageWightVolatileParticles() { }

	public MessageWightVolatileParticles(Entity entity) {
		this.addEntity(entity);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.CLIENT) {
			this.handle();
		}

		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void handle() {
		Entity entity = this.getEntity(0);
		if(entity != null) {
			for (int i = 0; i < 80; i++) {
				double px = entity.getX() + entity.level.rand.nextFloat() * 0.7F;
				double py = entity.getY() + entity.level.rand.nextFloat() * 2.2F;
				double pz = entity.getZ() + entity.level.rand.nextFloat() * 0.7F;
				Vector3d vec = new Vector3d(px, py, pz).subtract(new Vector3d(entity.getX() + 0.35F, entity.getY() + 1.1F, entity.getZ() + 0.35F)).normalize();
				BLParticles.SWAMP_SMOKE.spawn(entity.world, px, py, pz, ParticleFactory.ParticleArgs.get().withMotion(vec.x * 0.25F, vec.y * 0.25F, vec.z * 0.25F));
			}
		}
	}
}
