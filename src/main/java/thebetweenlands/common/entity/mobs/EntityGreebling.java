package thebetweenlands.common.entity.mobs;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.audio.IEntitySound;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityMusic;
import thebetweenlands.client.audio.EntityMusicLayers;
import thebetweenlands.client.audio.GreeblingMusicSound;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.sound.BLSoundEvent;

/**
 * Created by Josh on 8/19/2018.
 */
public class EntityGreebling extends EntityCreature implements IEntityBL, IEntityMusic {
	protected static final byte EVENT_START_DISAPPEARING = 40;
	protected static final byte EVENT_DISAPPEAR = 41;
	
	private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityGreebling.class, DataSerializers.VARINT);
	private static final DataParameter<Direction> FACING = EntityDataManager.createKey(EntityGreebling.class, DataSerializers.FACING);

	public int disappearTimer = 0;

	public EntityGreebling(World worldIn) {
		super(worldIn);
		this.setSize(1, 0.75f);
	}

	private static final class GreeblingGroup implements IEntityLivingData {
		public boolean hasType1;
		public boolean hasType2;
		public int count;
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		super.onInitialSpawn(difficulty, livingdata);

		if(livingdata == null) {
			livingdata = new GreeblingGroup();
		}

		if(livingdata instanceof GreeblingGroup) {
			GreeblingGroup group = (GreeblingGroup) livingdata;

			if(group.count > 0 && (!group.hasType1 || !group.hasType2)) {
				if(!group.hasType1) {
					this.setType(0);
				} else {
					this.setType(1);
				}
			} else {
				this.setType(rand.nextInt(2));
				if(this.getType() == 0) {
					group.hasType1 = true;
				} else {
					group.hasType2 = true;
				}
			}

			group.count++;
		} else {
			this.setType(rand.nextInt(2));
		}

		this.dataManager.set(FACING, Direction.HORIZONTALS[rand.nextInt(Direction.HORIZONTALS.length)]);

		return livingdata;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(TYPE, 0);
		this.entityData.define(FACING, Direction.NORTH);
	}

	public void setType(int type) {
		this.dataManager.set(TYPE, type);
	}

	public int getType() {
		return this.dataManager.get(TYPE);
	}

	@Override
	public void tick() {
		super.tick();

		this.prevRotationPitch = this.xRot = 0;
		this.prevRotationYaw = this.yRot = this.dataManager.get(FACING).getHorizontalAngle();
		this.prevRenderYawOffset = this.renderYawOffset = this.dataManager.get(FACING).getHorizontalAngle();

		if (disappearTimer > 0 && disappearTimer < 8) disappearTimer++;
		
		if(!this.level.isClientSide()) {
			if (disappearTimer == 5) this.world.setEntityState(this, EVENT_DISAPPEAR);
			if (disappearTimer >= 8) remove();
			
			List<PlayerEntity> nearPlayers = world.getEntitiesOfClass(PlayerEntity.class, getBoundingBox().grow(4.5, 5, 4.5), e -> !e.isCreative() && !e.isInvisible());
			if (disappearTimer == 0 && !nearPlayers.isEmpty()) {
				disappearTimer++;
				this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.GREEBLING_VANISH, SoundCategory.NEUTRAL, 1, 1);
				this.world.setEntityState(this, EVENT_START_DISAPPEARING);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		
		if(id == EVENT_START_DISAPPEARING) {
			disappearTimer = 1;
		} else if(id == EVENT_DISAPPEAR) {
			doLeafEffects();
		}
	}
	
	private void doLeafEffects() {
		if(world.isClientSide()) {
			int leafCount = 40;
			float x = (float) (posX);
			float y = (float) (posY + 1.3F);
			float z = (float) (posZ);
			while (leafCount-- > 0) {
				float dx = world.rand.nextFloat() * 1 - 0.5f;
				float dy = world.rand.nextFloat() * 1f - 0.1F;
				float dz = world.rand.nextFloat() * 1 - 0.5f;
				float mag = 0.08F + world.rand.nextFloat() * 0.07F;
				BLParticles.WEEDWOOD_LEAF.spawn(world, x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

	@Override
	public void writeEntityToNBT(CompoundNBT compound) {
		super.writeEntityToNBT(compound);
		compound.putInt("type", getType());
		compound.putInt("facing", this.dataManager.get(FACING).getHorizontalIndex());
	}

	@Override
	public void readEntityFromNBT(CompoundNBT compound) {
		super.readEntityFromNBT(compound);
		setType(compound.getInt("type"));
		this.dataManager.set(FACING, Direction.byHorizontalIndex(compound.getInt("facing")));
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) { }

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	protected void collideWithNearbyEntities() { }

	@Override
	public void applyEntityCollision(Entity entityIn) { }

	@Override
	public BLSoundEvent getMusicFile(PlayerEntity listener) {
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public IEntitySound getMusicSound(PlayerEntity listener) {
		return new GreeblingMusicSound(this.getType(), this, 0.75f);
	}

	@Override
	public double getMusicRange(PlayerEntity listener) {
		return 40.0D;
	}

	@Override
	public boolean isMusicActive(PlayerEntity listener) {
		return this.isEntityAlive();
	}

	@Override
	public int getMusicLayer(PlayerEntity listener) {
		return this.getType() == 0 ? EntityMusicLayers.GREEBLING_1 : EntityMusicLayers.GREEBLING_2;
	}

	@Override
	public boolean canInterruptOtherEntityMusic(PlayerEntity listener) {
		return false;
	}

	public void setFacing(Direction facing) {
		this.dataManager.set(FACING, facing);
	}
}
