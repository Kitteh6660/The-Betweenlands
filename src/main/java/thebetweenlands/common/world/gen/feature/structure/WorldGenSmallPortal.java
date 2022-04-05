package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;
import java.util.UUID;

import net.minecraft.block.BlockLog;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.structure.BlockTreePortal;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationPortal;

public class WorldGenSmallPortal extends WorldGenerator {
	protected final Direction dir;

	public WorldGenSmallPortal(Direction dir) {
		super(true);
		this.dir = dir;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		for(BlockPos p : BlockPos.getAllInBox(pos.getX() - 2, pos.getY() + 1, pos.getZ() - 2, pos.getX() + 2, pos.getY() + 4, pos.getZ() + 2)) {
			world.setBlockToAir(p);
		}
		
		for(BlockPos p : BlockPos.getAllInBox(pos.getX() - 2, pos.getY(), pos.getZ() - 2, pos.getX() + 2, pos.getY(), pos.getZ() + 2)) {
			world.setBlockState(p, BlockRegistry.LOG_PORTAL.defaultBlockState().setValue(RotatedPillarBlock.AXIS, BlockLog.EnumAxis.NONE));
		}

		if(this.dir.getAxis() == Direction.Axis.X) BlockTreePortal.makePortalX(world, pos.above(2));
		if(this.dir.getAxis() == Direction.Axis.Z) BlockTreePortal.makePortalZ(world, pos.above(2));

		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		LocationPortal location = new LocationPortal(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), pos.offset(this.dir).below());
		location.addBounds(new AxisAlignedBB(pos).inflate(2, 1, 2).expand(0, -2, 0).offset(0, 3, 0).expand(0, -1, 0));
		location.setSeed(rand.nextLong());
		location.setDirty(true);
		location.setVisible(false);
		worldStorage.getLocalStorageHandler().addLocalStorage(location);

		return true;
	}
}
