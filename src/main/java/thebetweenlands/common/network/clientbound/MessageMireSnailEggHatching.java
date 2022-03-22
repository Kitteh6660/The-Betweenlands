package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntityMireSnailEgg;
import thebetweenlands.common.network.MessageEntity;
import thebetweenlands.common.registries.SoundRegistry;

public class MessageMireSnailEggHatching extends MessageEntity {
	public MessageMireSnailEggHatching() { }

	public MessageMireSnailEggHatching(EntityMireSnailEgg egg) {
		this.addEntity(egg);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.CLIENT) {
			this.spawnParticles();
		}

		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnParticles() {
		EntityMireSnailEgg entity = (EntityMireSnailEgg) this.getEntity(0);
		if (entity != null) {
			for (int count = 0; count <= 50; ++count) {
				entity.world.spawnParticle(EnumParticleTypes.SLIME, entity.getX() + (entity.world.rand.nextDouble() - 0.5D) * 0.35F, entity.getY() + entity.world.rand.nextDouble() * 0.175F, entity.getZ() + (entity.world.rand.nextDouble() - 0.5D) * 0.35F, 0, 0, 0);
			}
			entity.world.playSound(Minecraft.getInstance().player, entity.getX(), entity.getY(), entity.getZ(), SoundRegistry.SQUISH, SoundCategory.NEUTRAL, 1, 0.8F);
		}
	}
}
