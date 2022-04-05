package thebetweenlands.common.tile.spawner;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.registries.BlockRegistry;

public class TileEntityMobSpawnerBetweenlands extends TileEntity implements ITickableTileEntity {
	
	public TileEntityMobSpawnerBetweenlands(TileEntityType<?> te) {
		super(te);
	}

	public float counter = 0.0F;
	public float lastCounter = 0.0F;

	private final MobSpawnerLogicBetweenlands spawnerLogic = new MobSpawnerLogicBetweenlands() {
		@Override
		public void broadcastEvent(int eventID) {
			TileEntityMobSpawnerBetweenlands.this.level.blockEvent(TileEntityMobSpawnerBetweenlands.this.getBlockPos(), BlockRegistry.MOB_SPAWNER.get(), eventID, 0);
		}

		@Override
		public World getSpawnerWorld() {
			return TileEntityMobSpawnerBetweenlands.this.level;
		}

		@Override
		public int getSpawnerX() {
			return TileEntityMobSpawnerBetweenlands.this.getBlockPos().getX();
		}

		@Override
		public int getSpawnerY() {
			return TileEntityMobSpawnerBetweenlands.this.getBlockPos().getY();
		}

		@Override
		public int getSpawnerZ() {
			return TileEntityMobSpawnerBetweenlands.this.getBlockPos().getZ();
		}

		@Override
		protected void addParticles() {
			World world = this.getSpawnerWorld();
			if(level.random.nextInt(2) == 0) {
				double rx = (double) (level.random.nextFloat());
				double ry = (double) (level.random.nextFloat());
				double rz = (double) (level.random.nextFloat());

				double len = Math.sqrt(rx * rx + ry * ry + rz * rz);

				float counter = -TileEntityMobSpawnerBetweenlands.this.counter;

				BLParticles.SPAWNER.spawn(world,
						(float) this.getSpawnerX() + rx, (float) this.getSpawnerY() + ry, (float) this.getSpawnerZ() + rz,
						ParticleFactory.ParticleArgs.get()
						.withMotion((rx - 0.5D) / len * 0.05D, (ry - 0.5D) / len * 0.05D, (rz - 0.5D) / len * 0.05D)
						.withColor(1.0F, MathHelper.clamp(4 + (float) Math.sin(counter) * 3, 0, 1), MathHelper.clamp((float) Math.sin(counter) * 2, 0, 1), 0.65F));
			}
		}
		
		@Override
		public MobSpawnerLogicBetweenlands setNextEntityName(String name) {
			super.setNextEntityName(name);
			TileEntityMobSpawnerBetweenlands te = TileEntityMobSpawnerBetweenlands.this;
			if(te != null && te.level != null) {
				BlockState blockState = te.level.getBlockState(te.worldPosition);
				te.level.sendBlockUpdated(te.worldPosition, blockState, blockState, 3);
			}
			return this;
		}
		
		@Override
		public MobSpawnerLogicBetweenlands setNextEntity(String name) {
			super.setNextEntity(name);
			TileEntityMobSpawnerBetweenlands te = TileEntityMobSpawnerBetweenlands.this;
			if(te != null && te.level != null) {
				BlockState blockState = te.level.getBlockState(te.worldPosition);
				te.level.sendBlockUpdated(te.worldPosition, blockState, blockState, 3);
			}
			return this;
		}
		
		@Override
		public MobSpawnerLogicBetweenlands setNextEntity(WeightedSpawnerEntity entity) {
			super.setNextEntity(entity);
			TileEntityMobSpawnerBetweenlands te = TileEntityMobSpawnerBetweenlands.this;
			if(te != null && te.level != null) {
				BlockState blockState = te.level.getBlockState(te.worldPosition);
				te.level.sendBlockUpdated(te.worldPosition, blockState, blockState, 3);
			}
			return this;
		}
		
		@Override
		public MobSpawnerLogicBetweenlands setEntitySpawnList(List<WeightedSpawnerEntity> entitySpawnList) {
			super.setEntitySpawnList(entitySpawnList);
			TileEntityMobSpawnerBetweenlands te = TileEntityMobSpawnerBetweenlands.this;
			if(te != null && te.level != null) {
				BlockState blockState = te.level.getBlockState(te.worldPosition);
				te.level.sendBlockUpdated(te.worldPosition, blockState, blockState, 3);
			}
			return this;
		}
	};

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.spawnerLogic.load(state, nbt);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		this.spawnerLogic.save(nbt);
		return nbt;
	}

	@Override
	public void tick() {
		this.spawnerLogic.updateSpawner();
		this.lastCounter = this.counter;
		this.counter += 0.0085F;
	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("entityType", this.getSpawnerLogic().getEntityId().toString());
		return new SUpdateTileEntityPacket(worldPosition, 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		if (packet.getTag().contains("entityType")) {
			String entityType = packet.getTag().getString("entityType");
			this.getSpawnerLogic().setNextEntityName(entityType);
		}
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		this.getSpawnerLogic().save(nbt);
		return nbt;
	}

	@Override
	public boolean triggerEvent(int event, int parameter) {
		return this.spawnerLogic.setDelayToMin(event) || super.triggerEvent(event, parameter);
	}

	public MobSpawnerLogicBetweenlands getSpawnerLogic() {
		return this.spawnerLogic;
	}
}
