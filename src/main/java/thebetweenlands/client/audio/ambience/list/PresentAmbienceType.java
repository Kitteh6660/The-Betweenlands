package thebetweenlands.client.audio.ambience.list;

import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityPresent;
import thebetweenlands.common.world.event.EventWinter;

public class PresentAmbienceType extends AmbienceType {
	@Nullable
	protected TileEntityPresent getClosestPresent(PlayerEntity player, double range) {
		int sx = MathHelper.floor(player.getX() - range) >> 4;
		int sz = MathHelper.floor(player.getZ() - range) >> 4;
		int ex = MathHelper.floor(player.getX() + range) >> 4;
		int ez = MathHelper.floor(player.getZ() + range) >> 4;
		TileEntityPresent closest = null;
		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				Chunk chunk = player.world.getChunk(cx, cz);
				for(Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet()) {
					TileEntity tile = entry.getValue();
					if(tile instanceof TileEntityPresent) {
						double dstSq = entry.getKey().distanceSq(player.getX(), player.getY(), player.getZ());
						if(dstSq <= range*range && (closest == null || dstSq <= closest.getPos().distanceSq(player.getX(), player.getY(), player.getZ()))) {
							closest = (TileEntityPresent) tile;
						}
					}
				}
			}
		}
		return closest;
	}

	@Override
	public boolean isActive() {
		return EventWinter.isFroooosty(this.getPlayer().world) && this.getClosestPresent(this.getPlayer(), 32.0D) != null;
	}

	@Override
	public AmbienceLayer getAmbienceLayer() {
		return AmbienceRegistry.BASE_LAYER;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public SoundCategory getCategory() {
		return SoundCategory.RECORDS;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.CHRISTMAS_ON_THE_MARSH;
	}

	@Override
	public float getVolume() {
		TileEntityPresent present = this.getClosestPresent(this.getPlayer(), 32.0D);
		if(present != null) {
			float volume = (1 - MathHelper.clamp((float)Math.sqrt(present.getDistanceSq(this.getPlayer().getX(), this.getPlayer().getY(), this.getPlayer().getZ())) / 64.0F, 0, 1));
			return volume * volume * 0.3F;
		}
		return 0.0F;
	}

	@Override
	public float getLowerPriorityVolume() {
		return 1.0F;
	}
}
