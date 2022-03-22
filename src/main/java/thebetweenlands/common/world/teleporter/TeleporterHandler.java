package thebetweenlands.common.world.teleporter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.IServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import thebetweenlands.common.config.BetweenlandsConfig;

public final class TeleporterHandler {
	private static final TeleporterHandler INSTANCE = new TeleporterHandler();

	private TeleporterHandler() {}
	
	public static void transferToDim(Entity entity, World world) {
		INSTANCE.transferEntity(entity, world.provider.getDimension(), true, true);
	}
	
	public static void transferToDim(Entity entity, World world, boolean makePortal, boolean setSpawn) {
		INSTANCE.transferEntity(entity, world.provider.getDimension(), makePortal, setSpawn);
	}

	private void transferEntity(Entity entity, int dimensionId, boolean makePortal, boolean setSpawn) {
		World world = entity.level;
		if (!world.isClientSide && entity.isAlive() && !(entity instanceof FakePlayer) && world instanceof IServerWorld) {
			if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(entity, dimensionId)) {
				return;
			}
			
			MinecraftServer server = world.getServer();
			IServerWorld toWorld = server.getLevel(dimensionId);
			AxisAlignedBB aabb = entity.getBoundingBox();
			aabb = new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) entity;
				player.invulnerableDimensionChange = true;
				player.server.getPlayerList().transferPlayerToDimension(player, dimensionId, new TeleporterBetweenlands(world.provider.getDimension(), aabb, toWorld, makePortal, setSpawn));
				player.timeUntilPortal = 0;
			} else {
				entity.setDropItemsWhenDead(false);
				world.removeEntityDangerously(entity);
				entity.dimension = dimensionId;
				entity.isDead = false;
				ServerWorld oldWorld = server.getWorld(entity.dimension);
				server.getPlayerList().transferEntityToWorld(entity, dimensionId, oldWorld, toWorld, new TeleporterBetweenlands(world.provider.getDimension(), aabb, toWorld, makePortal, setSpawn));
			}
		}
	}
}