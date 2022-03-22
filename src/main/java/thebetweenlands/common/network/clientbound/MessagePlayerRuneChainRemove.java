package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import thebetweenlands.api.capability.IRuneChainUserCapability;
import thebetweenlands.common.capability.item.RuneChainItemCapability;
import thebetweenlands.common.herblore.rune.RuneChainData;
import thebetweenlands.common.network.MessageEntity;
import thebetweenlands.common.registries.CapabilityRegistry;

public class MessagePlayerRuneChainRemove extends MessageEntity {
	private PlayerEntity player;
	private int runeChainId;

	public MessagePlayerRuneChainRemove() { }

	public MessagePlayerRuneChainRemove(PlayerEntity player, int runeChainId) {
		this.addEntity(player);
		this.runeChainId = runeChainId;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
		buf.writeVarInt(this.runeChainId);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);
		this.runeChainId = buf.readVarInt();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.CLIENT) {
			Entity entity = this.getEntity(0);

			if(entity instanceof PlayerEntity) {
				IRuneChainUserCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN_USER, null);

				if(cap != null) {
					cap.removeRuneChain(this.runeChainId);
				}
			}
		}

		return null;
	}
}
