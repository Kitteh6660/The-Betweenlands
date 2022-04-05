package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class LocationDebugItem extends Item {
	public LocationDebugItem() {
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResultType onItemUse( PlayerEntity playerIn, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		if (!level.isClientSide()) {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
			if(playerIn.isCrouching()) {
				List<LocationStorage> locations = worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, pos.getX(), pos.getZ(), location -> location.isInside(new Vector3d(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ)));
				if(locations.isEmpty()) {
					int rndID = world.rand.nextInt();
					LocationStorage location = new LocationGuarded(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), "Test Location ID: " + rndID, EnumLocationType.NONE);
					location.addBounds(new AxisAlignedBB(pos).inflate(16, 16, 16));
					location.setSeed(world.rand.nextLong());
					location.setDirty(true);
					worldStorage.getLocalStorageHandler().addLocalStorage(location);
					playerIn.sendMessage(new TextComponentString(String.format("Added new location: %s", location.getName())));
				} else {
					for(LocationStorage location : locations) {
						worldStorage.getLocalStorageHandler().removeLocalStorage(location);
					}
					playerIn.sendMessage(new TextComponentString(String.format("Removed %s locations:",  locations.size())));
					for(LocationStorage location : locations) {
						playerIn.sendMessage(new TextComponentString("  " + location.getName()));
					}
				}
			} else {
				List<LocationStorage> locations = worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, pos.getX(), pos.getZ(), location -> location.isInside(new Vector3d(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ)));
				List<ServerPlayerEntity> watchers = new ArrayList<ServerPlayerEntity>();
				boolean guard = false;
				for(LocationStorage location : locations) {
					if(hand == Hand.OFF_HAND && location.getGuard() != null) {
						boolean guarded = location.getGuard().isGuarded(world, playerIn, pos);
						location.getGuard().setGuarded(world, pos, !guarded);
						playerIn.sendMessage(new TextComponentString(String.format("Set block guard to %s at %s for location %s", !guarded, "X=" + pos.getX() + " Y=" + pos.getY() + " Z=" + pos.getZ(), location.getName())));
						location.setDirty(true);
						guard = true;
					}
				}
				if(!guard) {
					for(LocationStorage location : locations) {
						location.unlinkAllChunks();
						location.linkChunks();
						location.setDirty(true);
						for(ServerPlayerEntity watcher : location.getWatchers()) {
							if(!watchers.contains(watcher)) {
								watchers.add(watcher);
							}
						}
					}
					playerIn.sendMessage(new TextComponentString(String.format("Marked %s locations as dirty and queued update packets to %s watchers:", locations.size(), watchers.size())));
					playerIn.sendMessage(new TextComponentString("  Locations:"));
					for(LocationStorage location : locations) {
						playerIn.sendMessage(new TextComponentString("    " + location.getName() + " (" + location.getID().getStringID() + ")"));
						playerIn.sendMessage(new TranslationTextComponent("      Guarded at %s, %s: %s", new TranslationTextComponent(world.getBlockState(pos).getBlock().getTranslationKey() + ".name"), "X=" + pos.getX() + " Y=" + pos.getY() + " Z=" + pos.getZ(), (location.getGuard() == null ? String.valueOf(false) : location.getGuard().isGuarded(world, playerIn, pos))));
						playerIn.sendMessage(new TextComponentString("      Watchers:"));
						for(ServerPlayerEntity watcher : location.getWatchers()) {
							playerIn.sendMessage(new TextComponentString("        " + watcher.getName()));
						}
					}
				}
			}
		}

		return ActionResultType.SUCCESS;
	}
	
	@Override
	public CreativeTabs getCreativeTab() {
		return BetweenlandsConfig.DEBUG.debug ? BLCreativeTabs.SPECIALS : null;
	}
}
