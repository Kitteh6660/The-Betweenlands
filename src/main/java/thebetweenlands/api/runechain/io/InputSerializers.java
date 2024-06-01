package thebetweenlands.api.runechain.io;

import java.io.IOException;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.io.types.IBlockTarget;
import thebetweenlands.api.runechain.io.types.IVectorTarget;
import thebetweenlands.api.runechain.io.types.StaticBlockTarget;
import thebetweenlands.api.runechain.io.types.StaticVectorTarget;

public class InputSerializers {
	public static final IInputSerializer<IBlockTarget> BLOCK = new IInputSerializer<IBlockTarget>() {
		@Override
		public void write(IBlockTarget obj, FriendlyByteBuf buffer) {
			buffer.writeLong(obj.block().asLong());
		}

		@Override
		public IBlockTarget read(IRuneChainUser user, FriendlyByteBuf buffer) throws IOException {
			return new StaticBlockTarget(BlockPos.of(buffer.readLong()));
		}
	};

	public static final IInputSerializer<EntityType<?>> ENTITY = new IInputSerializer<EntityType<?>>() {
		@Override
		public void write(EntityType<?> obj, FriendlyByteBuf buffer) {
			buffer.writeResourceLocation(ForgeRegistries.ENTITY_TYPES.getKey(obj));
		}

		@Override
		public EntityType<?> read(IRuneChainUser user, FriendlyByteBuf buffer) throws IOException {
			return ForgeRegistries.ENTITY_TYPES.getValue(buffer.readResourceLocation());
		}	
	};

	//TODO Replace this with IRuneChainUser
	/*public static final IInputSerializer<Object> USER = new IInputSerializer<Object>() {
		@Override
		public void write(Object obj, FriendlyByteBuf buffer) {
			if(obj instanceof Entity) {
				buffer.writeBoolean(false);
				buffer.writeVarInt(((Entity)obj).getEntityId());
			} else {
				buffer.writeBoolean(true);
				buffer.writeVarInt(((IRuneChainUser)obj).getEntity().getEntityId());
			}
		}

		@Override
		public Object read(IRuneChainUser user, FriendlyByteBuf buffer) throws IOException {
			Entity entity = user.getLevel().getEntityByID(buffer.readVarInt());
			if(entity != null) {
				if(buffer.readBoolean()) {
					IRuneChainUserCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN_USER, null);
					if(cap != null) {
						return cap.getUser();
					}
				} else {
					return entity;
				}
			}
			return null;
		}		
	};*/

	public static final IInputSerializer<IVectorTarget> VECTOR = new IInputSerializer<IVectorTarget>() {
		@Override
		public void write(IVectorTarget obj, FriendlyByteBuf buffer) {
			buffer.writeDouble(obj.x());
			buffer.writeDouble(obj.y());
			buffer.writeDouble(obj.z());
		}

		@Override
		public IVectorTarget read(IRuneChainUser user, FriendlyByteBuf buffer) throws IOException {
			return new StaticVectorTarget(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		}		
	};
}
