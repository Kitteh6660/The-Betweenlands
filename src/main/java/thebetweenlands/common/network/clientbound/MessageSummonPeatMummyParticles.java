package thebetweenlands.common.network.clientbound;

import net.minecraft.block.Block;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.entity.Entity;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.network.MessageEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class MessageSummonPeatMummyParticles extends MessageEntity {
	public MessageSummonPeatMummyParticles() { }

	public MessageSummonPeatMummyParticles(Entity entity) {
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
			for (int i = 0; i < 250; i++) {
				double px = entity.getX() - 0.75F + entity.level.rand.nextFloat() * 1.5F;
				double py = entity.getY() - 2.0F + entity.level.rand.nextFloat() * 4.0F;
				double pz = entity.getZ() - 0.75F + entity.level.rand.nextFloat() * 1.5F;
				Vector3d vec = new Vector3d(px, py, pz).subtract(new Vector3d(entity.getX() + 0.35F, entity.getY() + 1.1F, entity.getZ() + 0.35F)).normalize();
				entity.level.addParticle(ParticleTypes.BLOCK_CRACK, px, py, pz, vec.x * 0.25F, vec.y * 0.25F, vec.z * 0.25F, Block.getStateId(BlockRegistry.MUD.defaultBlockState()));
			}
		}
	}
}
