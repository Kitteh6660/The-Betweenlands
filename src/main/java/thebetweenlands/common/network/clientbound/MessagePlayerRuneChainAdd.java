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
import thebetweenlands.api.runechain.chain.IRuneChainData;
import thebetweenlands.common.capability.item.RuneChainItemCapability;
import thebetweenlands.common.herblore.rune.RuneChainData;
import thebetweenlands.common.network.MessageEntity;
import thebetweenlands.common.registries.CapabilityRegistry;

public class MessagePlayerRuneChainAdd extends MessageEntity {
	private PlayerEntity player;
	private int runeChainId;
	private CompoundNBT runeChainData;

	public MessagePlayerRuneChainAdd() { }

	public MessagePlayerRuneChainAdd(PlayerEntity player, int runeChainId, IRuneChainData data) {
		this.addEntity(player);
		this.runeChainId = runeChainId;
		this.runeChainData = RuneChainData.save(data, new CompoundNBT());
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
		buf.writeVarInt(this.runeChainId);
		buf.writeCompoundTag(this.runeChainData);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);
		this.runeChainId = buf.readVarInt();
		this.runeChainData = buf.readCompoundTag();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.CLIENT) {
			Entity entity = this.getEntity(0);

			if(entity instanceof PlayerEntity) {
				IRuneChainUserCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN_USER, null);

				if(cap != null) {
					cap.addRuneChain(RuneChainItemCapability.createBlueprint(RuneChainData.readFromNBT(this.runeChainData)).create(), this.runeChainId);
				}
			}
		}

		return null;
	}
}
