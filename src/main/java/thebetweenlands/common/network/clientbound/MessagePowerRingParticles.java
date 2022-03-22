package thebetweenlands.common.network.clientbound;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.handler.AttackDamageHandler;
import thebetweenlands.common.network.MessageEntity;

public class MessagePowerRingParticles extends MessageEntity {
	public MessagePowerRingParticles() { }

	public MessagePowerRingParticles(Entity entity) {
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
		Entity entityHit = this.getEntity(0);
		if(entityHit != null) {
			AttackDamageHandler.spawnPowerRingParticles(entityHit);
		}
	}
}
