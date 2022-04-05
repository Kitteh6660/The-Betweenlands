package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.block.structure.BlockBeamLensSupport;
import thebetweenlands.common.block.structure.BlockBeamOrigin;
import thebetweenlands.common.block.structure.BlockBeamRelay;
import thebetweenlands.common.block.structure.BlockBeamTube;
import thebetweenlands.common.block.structure.BlockDiagonalEnergyBarrier;
import thebetweenlands.common.block.structure.BlockEnergyBarrierMud;
import thebetweenlands.common.entity.mobs.EntityEmberlingShaman;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.registries.TileEntityRegistry;

public class BeamOriginTileEntity extends TileEntity implements ITickableTileEntity {
	public boolean active;

	public float prevVisibility = 0.0f;
	public float visibility = 0.0f;

	public float prevRotation = (float)Math.PI / 4;
	public float rotation = (float)Math.PI / 4;

	private int particleTimer = 0;

	public boolean beam_1_active = false;
	public boolean beam_2_active = false;
	public boolean beam_3_active = false;
	public boolean beam_4_active = false;

	public BeamOriginTileEntity() {
		super(TileEntityRegistry.BEAM_ORIGIN.get());
	}

	@Override
	public void tick() {
		int litBraziers = this.checkForLitBraziers();
		
		if (getLevel().getBlockState(getBlockPos()).getBlock() != null) {
			if (litBraziers == 4) {
				if (!active) {
					setActive(true);
					if (!level.isClientSide()) {
						spawnEmberling(getLevel(), getBlockPos().offset(3, -1, 3));
						spawnEmberling(getLevel(), getBlockPos().offset(3, -1, -3));
						spawnEmberling(getLevel(), getBlockPos().offset(-3, -1, 3));
						spawnEmberling(getLevel(), getBlockPos().offset(-3, -1, -3));
					}
				}
			} else {
				if (active)
					setActive(false);
			}
		}

		if (getLevel().getGameTime() % 10 == 0) {
			if (checkForLitBrazier(getBlockPos().offset(3, -1, 3))) {
				if (level.isClientSide())
					spawnBrazierParticles(new Vector3d(3, -1, 3));
				if (!level.isClientSide())
					if (!beam_1_active) {
						setBeam1Active(true);
						getLevel().playSound((PlayerEntity) null, getBlockPos().offset(3, -1, 3), SoundRegistry.PORTAL_ACTIVATE, SoundCategory.BLOCKS, 0.125F, 0.25F);
					}
			} else {
				if (!level.isClientSide())
					if (beam_1_active)
						setBeam1Active(false);
			}

			if (checkForLitBrazier(getBlockPos().offset(3, -1, -3))) {
				if (level.isClientSide())
					spawnBrazierParticles(new Vector3d(3, -1, -3));
				if (!level.isClientSide())
					if (!beam_2_active) {
						setBeam2Active(true);
						getLevel().playSound((PlayerEntity) null, getBlockPos().offset(3, -1, -3), SoundRegistry.PORTAL_ACTIVATE, SoundCategory.BLOCKS, 0.125F, 0.25F);
					}
			} else {
				if (!level.isClientSide())
					if (beam_2_active)
						setBeam2Active(false);
			}

			if (checkForLitBrazier(getBlockPos().offset(-3, -1, 3))) {
				if (level.isClientSide())
					spawnBrazierParticles(new Vector3d(-3, -1, 3));
				if (!level.isClientSide())
					if (!beam_3_active) {
						setBeam3Active(true);
						getLevel().playSound((PlayerEntity) null, getBlockPos().offset(-3, -1, 3), SoundRegistry.PORTAL_ACTIVATE, SoundCategory.BLOCKS, 0.125F, 0.25F);
					}
			} else {
				if (!level.isClientSide())
					if (beam_3_active)
						setBeam3Active(false);
			}

			if (checkForLitBrazier(getBlockPos().offset(-3, -1, -3))) {
				if (level.isClientSide())
					spawnBrazierParticles(new Vector3d(-3, -1, -3));
				if (!level.isClientSide())
					if (!beam_4_active) {
						setBeam4Active(true);
						getLevel().playSound((PlayerEntity) null, getBlockPos().offset(-3, -1, -3), SoundRegistry.PORTAL_ACTIVATE, SoundCategory.BLOCKS, 0.125F, 0.25F);
					}
			} else {
				if (!level.isClientSide())
					if (beam_4_active)
						setBeam4Active(false);
			}
		}

		this.prevVisibility = this.visibility;
		this.prevRotation = this.rotation;
		
		this.rotation += litBraziers * 0.0025f;
		
		float targetVisibility = 0.2f + 0.8f * litBraziers / 4.0f;
		
		if(this.visibility < targetVisibility) {
			this.visibility += 0.02f;
			if(this.visibility > targetVisibility) {
				this.visibility = targetVisibility;
			}
		} else if(this.visibility > targetVisibility) {
			this.visibility -= 0.02f;
			if(this.visibility < targetVisibility) {
				this.visibility = targetVisibility;
			}
		}
		
		if (active) {
			activateBlock();
		} else {
			deactivateBlock();
		}
	}

	private void spawnEmberling(World world, BlockPos pos) {
		EntityEmberlingShaman entity = new EntityEmberlingShaman(world);
		entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
		//entity.setBoundOrigin(pos); // may use this dunno yet...
		entity.onInitialSpawn(level.getCurrentDifficultyAt(pos), null);
		level.addFreshEntity(entity);
		
	}

	public int checkForLitBraziers() {
		int braziers = 0;
		if(checkForLitBrazier(getBlockPos().offset(3, -1, 3))) braziers++;
		if(checkForLitBrazier(getBlockPos().offset(3, -1, -3))) braziers++;
		if(checkForLitBrazier(getBlockPos().offset(-3, -1, 3))) braziers++;
		if(checkForLitBrazier(getBlockPos().offset(-3, -1, -3))) braziers++;
		return braziers;
	}

	public boolean checkForLitBrazier(BlockPos targetBlockPos) {
		BlockState flame = getLevel().getBlockState(targetBlockPos);
		if (flame.getBlock() instanceof FireBlock) {
			return true;
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnBrazierParticles(Vector3d target) {
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, BLParticles.PUZZLE_BEAM_2.create(level, this.worldPosition.getX() + 0.5 + target.x, this.worldPosition.getY() + 0.5 + target.y, this.worldPosition.getZ() + 0.5 + target.z, ParticleArgs.get().withMotion(0, 0, 0).withColor(255F, 102F, 0F, 1F).withScale(1.5F).withData(30, target.scale(-1))));
		for(int i = 0; i < 2; i++) {
			float offsetLen = this.level.random.nextFloat();
			Vector3d offset = new Vector3d(target.x * offsetLen + level.random.nextFloat() * 0.2f - 0.1f, target.y * offsetLen + level.random.nextFloat() * 0.2f - 0.1f, target.z * offsetLen + level.random.nextFloat() * 0.2f - 0.1f);
			float vx = (level.random.nextFloat() * 2f - 1) * 0.0025f;
			float vy = (level.random.nextFloat() * 2f - 1) * 0.0025f + 0.008f;
			float vz = (level.random.nextFloat() * 2f - 1) * 0.0025f;
			float scale = 0.5f + level.random.nextFloat();
			if(ShaderHelper.INSTANCE.canUseShaders() && level.random.nextBoolean()) {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS, BLParticles.SMOOTH_SMOKE.create(level, this.worldPosition.getX() + 0.5 + offset.x, this.worldPosition.getY() + 0.5 + offset.y, this.worldPosition.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(1, 1, 1, 0.2F).withScale(scale * 8).withData(80, true, 0.0F, true)));
			} else {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(level, this.worldPosition.getX() + 0.5 + offset.x, this.worldPosition.getY() + 0.5 + offset.y, this.worldPosition.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(255F, 102F, 0F, 1F).withScale(scale).withData(100)));
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnBeamParticles(Vector3d target) {
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, BLParticles.PUZZLE_BEAM_2.create(level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, ParticleArgs.get().withMotion(0, 0, 0).withColor(40F, 220F, 130F, 1F).withScale(2.5F).withData(30, target)));
		for(int i = 0; i < 3; i++) {
			float offsetLen = this.level.random.nextFloat();
			Vector3d offset = new Vector3d(target.x * offsetLen + level.random.nextFloat() * 0.2f - 0.1f, target.y * offsetLen + level.random.nextFloat() * 0.2f - 0.1f, target.z * offsetLen + level.random.nextFloat() * 0.2f - 0.1f);
			float vx = (level.random.nextFloat() * 2f - 1) * 0.0025f;
			float vy = (level.random.nextFloat() * 2f - 1) * 0.0025f + 0.008f;
			float vz = (level.random.nextFloat() * 2f - 1) * 0.0025f;
			float scale = 0.5f + level.random.nextFloat();
			if(ShaderHelper.INSTANCE.canUseShaders() && level.random.nextBoolean()) {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS, BLParticles.SMOOTH_SMOKE.create(level, this.worldPosition.getX() + 0.5 + offset.x, this.worldPosition.getY() + 0.5 + offset.y, this.worldPosition.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(1, 1, 1, 0.2F).withScale(scale * 8).withData(80, true, 0.0F, true)));
			} else {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(level, this.worldPosition.getX() + 0.5 + offset.x, this.worldPosition.getY() + 0.5 + offset.y, this.worldPosition.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(40F, 220F, 130F, 1F).withScale(scale).withData(100)));
			}
		}
	}

	public void activateBlock() {
		if(!level.isClientSide()) {
			BlockState state = getLevel().getBlockState(getBlockPos());
			if(!state.getValue(BlockBeamOrigin.POWERED)) {
				getLevel().setBlockAndUpdate(getBlockPos(), BlockRegistry.MUD_TOWER_BEAM_ORIGIN.get().defaultBlockState().setValue(BlockBeamOrigin.POWERED, true));
				getLevel().playSound((PlayerEntity)null, getBlockPos(), SoundRegistry.BEAM_ACTIVATE, SoundCategory.BLOCKS, 1F, 1F);
			}
		}

		Direction facing = Direction.DOWN;
		BlockPos targetBlockPos = getBlockPos().relative(facing, getDistanceToObstruction(facing));

		if(level.isClientSide()) {
			if(this.particleTimer++ >= 20) {
				this.particleTimer = 0;
				spawnBeamParticles(new Vector3d(targetBlockPos.getX() - worldPosition.getX(), targetBlockPos.getY() - worldPosition.getY(), targetBlockPos.getZ() - worldPosition.getZ()));
			}
		} else {
			BlockState stateofTarget = getLevel().getBlockState(targetBlockPos);

			if (stateofTarget.getBlock() instanceof BlockBeamRelay) {
				if (getLevel().getBlockEntity(targetBlockPos) instanceof BeamRelayTileEntity) {
					BeamRelayTileEntity targetTile = (BeamRelayTileEntity) getLevel().getBlockEntity(targetBlockPos);
					targetTile.setTargetIncomingBeam(facing.getOpposite(), true);
					if (!getLevel().getBlockState(targetBlockPos).getValue(BlockBeamRelay.POWERED)) {
						stateofTarget = stateofTarget.cycle(BlockBeamRelay.POWERED);
						getLevel().setBlock(targetBlockPos, stateofTarget, 3);
					}
				}
			}
		}
	}

	public void deactivateBlock() {
		BlockState state = getLevel().getBlockState(getBlockPos());
		if (state.getValue(BlockBeamOrigin.POWERED)) {
			getLevel().setBlockAndUpdate(getBlockPos(), BlockRegistry.MUD_TOWER_BEAM_ORIGIN.get().defaultBlockState().setValue(BlockBeamOrigin.POWERED, false));

			Direction facing = Direction.DOWN;
			BlockPos targetBlockPos = getBlockPos().relative(facing, getDistanceToObstruction(facing));
			BlockState stateofTarget = getLevel().getBlockState(targetBlockPos);

			if (stateofTarget.getBlock() instanceof BlockBeamRelay) {
				if (getLevel().getBlockEntity(targetBlockPos) instanceof BeamRelayTileEntity) {
					BeamRelayTileEntity targetTile = (BeamRelayTileEntity) getLevel().getBlockEntity(targetBlockPos);
					targetTile.setTargetIncomingBeam(facing.getOpposite(), false);
					if (!targetTile.isGettingBeamed())
						if (getLevel().getBlockState(targetBlockPos).getValue(BlockBeamRelay.POWERED)) {
							stateofTarget = stateofTarget.cycle(BlockBeamRelay.POWERED);
							getLevel().setBlock(targetBlockPos, stateofTarget, 3);
						}
				}
			}
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public void setActive(boolean isActive) {
		active = isActive;
		getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3);
	}

	public void setBeam1Active(boolean isActive) {
		beam_1_active = isActive;
		getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3);
	}

	public void setBeam2Active(boolean isActive) {
		beam_2_active = isActive;
		getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3);
	}

	public void setBeam3Active(boolean isActive) {
		beam_3_active = isActive;
		getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3);
	}

	public void setBeam4Active(boolean isActive) {
		beam_4_active = isActive;
		getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3);
	}

	public int getDistanceToObstruction(Direction facing) {
		int distance = 0;
		for (distance = 1; distance < 14; distance++) {
			BlockState state = getLevel().getBlockState(getBlockPos().relative(facing, distance));
			if (state != Blocks.AIR.defaultBlockState()
					&& !(state.getBlock() instanceof BlockDiagonalEnergyBarrier) 
					&& !(state.getBlock() instanceof BlockEnergyBarrierMud)
					&& !(state.getBlock() instanceof BlockBeamLensSupport)
					&& !isValidBeamTubeLens(state, facing))
				break;
		}
		return distance;
	}

	private boolean isValidBeamTubeLens(BlockState state, Direction facing) {
		if(!(state.getBlock() instanceof BlockBeamTube))
			return false;
		if(state.getValue(BlockBeamTube.FACING) == facing)
			return true;
		if(state.getValue(BlockBeamTube.FACING) == facing.getOpposite())
			return true;
		return false;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putBoolean("active", active);
		nbt.putBoolean("beam_1_active", beam_1_active);
		nbt.putBoolean("beam_2_active", beam_2_active);
		nbt.putBoolean("beam_3_active", beam_3_active);
		nbt.putBoolean("beam_4_active", beam_4_active);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		active = nbt.getBoolean("active");
		beam_1_active = nbt.getBoolean("beam_1_active");
		beam_2_active = nbt.getBoolean("beam_2_active");
		beam_3_active = nbt.getBoolean("beam_3_active");
		beam_4_active = nbt.getBoolean("beam_4_active");
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
		return save(nbt);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		save(nbt);
		return new SUpdateTileEntityPacket(getBlockPos(), 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		load(this.getBlockState(), packet.getTag());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox().inflate(10);
	}
}
