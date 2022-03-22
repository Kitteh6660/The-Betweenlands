package thebetweenlands.common.registries;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.api.distmarker.Dist;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.network.bidirectional.MessageUpdateDraetonPhysicsPart;
import thebetweenlands.common.network.clientbound.MessageAddLocalStorage;
import thebetweenlands.common.network.clientbound.MessageAmateMap;
import thebetweenlands.common.network.clientbound.MessageBlockGuardData;
import thebetweenlands.common.network.clientbound.MessageBlockGuardSectionChange;
import thebetweenlands.common.network.clientbound.MessageClearBlockGuard;
import thebetweenlands.common.network.clientbound.MessageCureDecayParticles;
import thebetweenlands.common.network.clientbound.MessageDamageReductionParticle;
import thebetweenlands.common.network.clientbound.MessageDruidAltarProgress;
import thebetweenlands.common.network.clientbound.MessageDruidTeleportParticles;
import thebetweenlands.common.network.clientbound.MessageGemProc;
import thebetweenlands.common.network.clientbound.MessageLivingWeedwoodShieldSpit;
import thebetweenlands.common.network.clientbound.MessageMireSnailEggHatching;
import thebetweenlands.common.network.clientbound.MessagePlayEntityIdle;
import thebetweenlands.common.network.clientbound.MessagePlayerRuneChainAdd;
import thebetweenlands.common.network.clientbound.MessagePlayerRuneChainPacket;
import thebetweenlands.common.network.clientbound.MessagePlayerRuneChainRemove;
import thebetweenlands.common.network.clientbound.MessagePowerRingParticles;
import thebetweenlands.common.network.clientbound.MessageRemoveLocalStorage;
import thebetweenlands.common.network.clientbound.MessageRiftSound;
import thebetweenlands.common.network.clientbound.MessageShockArrowHit;
import thebetweenlands.common.network.clientbound.MessageShowFoodSicknessLine;
import thebetweenlands.common.network.clientbound.MessageSoundRipple;
import thebetweenlands.common.network.clientbound.MessageSummonPeatMummyParticles;
import thebetweenlands.common.network.clientbound.MessageSyncChunkStorage;
import thebetweenlands.common.network.clientbound.MessageSyncDraetonLeakages;
import thebetweenlands.common.network.clientbound.MessageSyncEntityCapabilities;
import thebetweenlands.common.network.clientbound.MessageSyncEnvironmentEventData;
import thebetweenlands.common.network.clientbound.MessageSyncGameRules;
import thebetweenlands.common.network.clientbound.MessageSyncLocalStorageData;
import thebetweenlands.common.network.clientbound.MessageSyncLocalStorageReferences;
import thebetweenlands.common.network.clientbound.MessageSyncStaticAspects;
import thebetweenlands.common.network.clientbound.MessageWeedwoodBushRustle;
import thebetweenlands.common.network.clientbound.MessageWightVolatileParticles;
import thebetweenlands.common.network.serverbound.MessageChiromawDoubleJump;
import thebetweenlands.common.network.serverbound.MessageConnectCavingRope;
import thebetweenlands.common.network.serverbound.MessageEquipItem;
import thebetweenlands.common.network.serverbound.MessageExtendedReach;
import thebetweenlands.common.network.serverbound.MessageFlightState;
import thebetweenlands.common.network.serverbound.MessageItemNaming;
import thebetweenlands.common.network.serverbound.MessageLinkRuneWeavingTableRune;
import thebetweenlands.common.network.serverbound.MessageOpenPouch;
import thebetweenlands.common.network.serverbound.MessagePurgeDraetonBurner;
import thebetweenlands.common.network.serverbound.MessageRow;
import thebetweenlands.common.network.serverbound.MessageSetDraetonAnchorPos;
import thebetweenlands.common.network.serverbound.MessageSetGalleryUrl;
import thebetweenlands.common.network.serverbound.MessageSetRuneWeavingTableConfiguration;
import thebetweenlands.common.network.serverbound.MessageSetRuneWeavingTablePage;
import thebetweenlands.common.network.serverbound.MessageShiftRuneWeavingTableSlot;
import thebetweenlands.common.network.serverbound.MessageUnlinkRuneWeavingTableRune;
import thebetweenlands.common.network.serverbound.MessageUpdatePuppeteerState;
import thebetweenlands.common.network.serverbound.MessageUpdateRingKeybindState;

public class MessageRegistry {
	private MessageRegistry() { }

	private static byte nextMessageId = 0;

	public static void preInit() {
		registerMessage(MessageDruidAltarProgress.class, LogicalSide.CLIENT);
		registerMessage(MessageSyncEnvironmentEventData.class, LogicalSide.CLIENT);
		registerMessage(MessageWeedwoodBushRustle.class, LogicalSide.CLIENT);
		registerMessage(MessageSyncEntityCapabilities.class, LogicalSide.CLIENT);
		registerMessage(MessageSyncStaticAspects.class, LogicalSide.CLIENT);
		registerMessage(MessageDruidTeleportParticles.class, LogicalSide.CLIENT);
		registerMessage(MessageWightVolatileParticles.class, LogicalSide.CLIENT);
		registerMessage(MessageGemProc.class, LogicalSide.CLIENT);
		registerMessage(MessageMireSnailEggHatching.class, LogicalSide.CLIENT);
		registerMessage(MessageBlockGuardSectionChange.class, LogicalSide.CLIENT);
		registerMessage(MessageBlockGuardData.class, LogicalSide.CLIENT);
		registerMessage(MessageClearBlockGuard.class, LogicalSide.CLIENT);
		registerMessage(MessagePlayEntityIdle.class, LogicalSide.CLIENT);
		registerMessage(MessagePowerRingParticles.class, LogicalSide.CLIENT);
		registerMessage(MessageRemoveLocalStorage.class, LogicalSide.CLIENT);
		registerMessage(MessageAddLocalStorage.class, LogicalSide.CLIENT);
		registerMessage(MessageSyncLocalStorageData.class, LogicalSide.CLIENT);
		registerMessage(MessageSyncChunkStorage.class, LogicalSide.CLIENT);
		registerMessage(MessageSyncLocalStorageReferences.class, LogicalSide.CLIENT);
		registerMessage(MessageSummonPeatMummyParticles.class, LogicalSide.CLIENT);
		registerMessage(MessageShowFoodSicknessLine.class, LogicalSide.CLIENT);
		registerMessage(MessageDamageReductionParticle.class, LogicalSide.CLIENT);
		registerMessage(MessageRiftSound.class, LogicalSide.CLIENT);
		registerMessage(MessageLivingWeedwoodShieldSpit.class, LogicalSide.CLIENT);
		registerMessage(MessageAmateMap.class, LogicalSide.CLIENT);
		registerMessage(MessageSoundRipple.class, LogicalSide.CLIENT);
		registerMessage(MessageSyncGameRules.class, LogicalSide.CLIENT);
		registerMessage(MessageCureDecayParticles.class, LogicalSide.CLIENT);
		registerMessage(MessageUpdateDraetonPhysicsPart.class, LogicalSide.CLIENT);
		registerMessage(MessageSyncDraetonLeakages.class, LogicalSide.CLIENT);
		registerMessage(MessageShockArrowHit.class, LogicalSide.CLIENT);
		registerMessage(MessagePlayerRuneChainPacket.class, LogicalSide.CLIENT);
		registerMessage(MessagePlayerRuneChainAdd.class, LogicalSide.CLIENT);
		registerMessage(MessagePlayerRuneChainRemove.class, LogicalSide.CLIENT);
		
		registerMessage(MessageEquipItem.class, LogicalSide.SERVER);
		registerMessage(MessageOpenPouch.class, LogicalSide.SERVER);
		registerMessage(MessageItemNaming.class, LogicalSide.SERVER);
		registerMessage(MessageFlightState.class, LogicalSide.SERVER);
		registerMessage(MessageUpdatePuppeteerState.class, LogicalSide.SERVER);
		registerMessage(MessageUpdateRingKeybindState.class, LogicalSide.SERVER);
		registerMessage(MessageRow.class, LogicalSide.SERVER);
		registerMessage(MessageConnectCavingRope.class, LogicalSide.SERVER);
		registerMessage(MessageExtendedReach.class, LogicalSide.SERVER);
		registerMessage(MessageSetGalleryUrl.class, LogicalSide.SERVER);
		registerMessage(MessageSetRuneWeavingTablePage.class, LogicalSide.SERVER);
		registerMessage(MessageShiftRuneWeavingTableSlot.class, LogicalSide.SERVER);
		registerMessage(MessageLinkRuneWeavingTableRune.class, LogicalSide.SERVER);
		registerMessage(MessageUnlinkRuneWeavingTableRune.class, LogicalSide.SERVER);
		registerMessage(MessageSetRuneWeavingTableConfiguration.class, LogicalSide.SERVER);
		registerMessage(MessageUpdateDraetonPhysicsPart.class, LogicalSide.SERVER);
		registerMessage(MessageSetDraetonAnchorPos.class, LogicalSide.SERVER);
		registerMessage(MessagePurgeDraetonBurner.class, LogicalSide.SERVER);
		registerMessage(MessageChiromawDoubleJump.class, LogicalSide.SERVER);
	}

	private static void registerMessage(Class<? extends MessageBase> messageType, LogicalSide toLogicalSide) {
		TheBetweenlands.networkWrapper.registerMessage(getHandler(messageType, toLogicalSide), messageType, MessageRegistry.nextMessageId++, toLogicalSide);
	}

	private static IMessageHandler<MessageBase, IMessage> getHandler(Class<? extends MessageBase> messageType, LogicalSide toLogicalSide) {
		if (toLogicalSide == LogicalSide.CLIENT) {
			return new ClientboundHandler();
		}
		return new ServerboundHandler();
	}

	private static class ServerboundHandler implements IMessageHandler<MessageBase, IMessage> {
		@Override
		public IMessage onMessage(MessageBase message, MessageContext ctx) {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			try {
				server.callFromMainThread(() -> message.process(ctx));
			} catch (Exception e) {
				e.printStackTrace(); // TODO: proper logging
			}
			return null;
		}
	}

	private static class ClientboundHandler implements IMessageHandler<MessageBase, IMessage> {
		@Override
		public IMessage onMessage(MessageBase message, MessageContext ctx) {
			Minecraft mc = FMLClientHandler.instance().getClient();
			try {
				mc.addScheduledTask(() -> message.process(ctx));
			} catch (Exception e) {
				e.printStackTrace(); // TODO: proper logging
			}
			return null;
		}
	}
}
