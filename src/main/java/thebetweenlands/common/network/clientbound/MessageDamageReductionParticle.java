package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.handler.AttackDamageHandler;
import thebetweenlands.common.network.MessageEntity;

public class MessageDamageReductionParticle extends MessageEntity {
	private Vector3d offset, dir;

	public MessageDamageReductionParticle() { }

	public MessageDamageReductionParticle(Entity entity, Vector3d offset, Vector3d dir) {
		this.addEntity(entity);
		this.offset = offset;
		this.dir = dir;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);

		buf.writeDouble(this.offset.x);
		buf.writeDouble(this.offset.y);
		buf.writeDouble(this.offset.z);

		buf.writeDouble(this.dir.x);
		buf.writeDouble(this.dir.y);
		buf.writeDouble(this.dir.z);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);

		this.offset = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		this.dir = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
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
			AttackDamageHandler.spawnDamageReductionParticle(entityHit, this.offset, this.dir);
		}
	}
}
