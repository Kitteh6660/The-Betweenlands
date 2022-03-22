package thebetweenlands.common.network.clientbound;

import java.io.IOException;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.capability.circlegem.CircleGem;
import thebetweenlands.common.capability.circlegem.CircleGem.CombatType;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.network.MessageEntity;

public class MessageGemProc extends MessageEntity {
	private CircleGem gem;

	public MessageGemProc() { }

	public MessageGemProc(Entity entity, boolean offensive, CircleGemType gem) {
		this.addEntity(entity);
		this.gem = new CircleGem(gem, offensive ? CombatType.OFFENSIVE : CombatType.DEFENSIVE);
	}

	public CircleGem getGem() {
		return this.gem;
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);
		try {
			this.gem = CircleGem.readFromNBT(buf.readCompoundTag());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
		buf.writeCompoundTag(this.gem.save(new CompoundNBT()));
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
		CircleGem gem = this.getGem();
		Entity entityHit = this.getEntity(0);
		if(entityHit != null) {
			Random rnd = entityHit.world.rand;
			for(int i = 0; i < 40; i++) {
				double x = entityHit.getX() + rnd.nextFloat() * entityHit.width * 2.0F - entityHit.width;
				double y = entityHit.getBoundingBox().minY + rnd.nextFloat() * entityHit.height;
				double z = entityHit.getZ() + rnd.nextFloat() * entityHit.width * 2.0F - entityHit.width;
				double dx = x - entityHit.getX();
				double dy = y - (entityHit.getY() + entityHit.height / 2.0F);
				double dz = z - entityHit.getZ();
				double len = Math.sqrt(dx*dx + dy*dy + dz*dz);
				ParticleArgs<?> args = ParticleArgs.get();

				switch(gem.getCombatType()) {
				case OFFENSIVE:
				default:
					args.withMotion(dx/len, dy/len, dz/len);
					break;
				case DEFENSIVE:
					args.withMotion(-dx/len, -dy/len, -dz/len);
					break;
				}

				switch(gem.getGemType()) {
				default:
				case AQUA:
					args.withColor(0.35F, 0.35F, 1, 1);
					break;
				case CRIMSON:
					args.withColor(1, 0, 0, 1);
					break;
				case GREEN:
					args.withColor(0.3F, 1.0F, 0.3F, 1.0F);
					break;
				}

				BLParticles.GEM_PROC.spawn(entityHit.world, x, y, z, args);
			}
		}
	}
}
