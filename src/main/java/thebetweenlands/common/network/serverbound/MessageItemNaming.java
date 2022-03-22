package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.api.item.IRenamableItem;
import thebetweenlands.common.network.MessageBase;

public class MessageItemNaming extends MessageBase {
	private String name;
	private Hand hand;

	public MessageItemNaming() { }

	public MessageItemNaming(String name, Hand hand) {
		this.name = name;
		this.hand = hand;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeString(this.name);
		buf.writeByte(this.hand == Hand.MAIN_HAND ? 0 : 1);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.name = buf.readString(128);
		this.hand = buf.readByte() == 0 ? Hand.MAIN_HAND : Hand.OFF_HAND;
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null) {
			PlayerEntity player = ctx.getServerHandler().player;
			ItemStack heldItem = player.getItemInHand(this.hand);
			if(this.name != null && !heldItem.isEmpty() && heldItem.getItem() instanceof IRenamableItem && ((IRenamableItem) heldItem.getItem()).canRename(player, this.hand, heldItem, this.name)) {
				if(this.name.length() == 0) {
					((IRenamableItem) heldItem.getItem()).clearRename(player, this.hand, heldItem, this.name);
				} else {
					((IRenamableItem) heldItem.getItem()).setRename(player, this.hand, heldItem, this.name);
				}
			}
		}
		return null;
	}
}
