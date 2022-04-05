package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.common.block.structure.BlockDungeonDoorRunes;
import thebetweenlands.common.entity.mobs.EntityBarrishee;
import thebetweenlands.common.entity.mobs.EntityCryptCrawler;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.gen.feature.structure.LightTowerBuildParts;

public class TileEntityDungeonDoorRunes extends TileEntity implements ITickableTileEntity, IEntityScreenShake {
	private LightTowerBuildParts lightTowerBuild = new LightTowerBuildParts(null);
	
	private boolean mimic; // true = trap
	private boolean barrishee; // true = Barrishee / false = Crypt Crawler Chief
	public int top_code = -1, mid_code = -1, bottom_code = -1; // set back to -1
	public int top_state = 0, mid_state = 0, bottom_state = 0;
	public int top_state_prev = 0, mid_state_prev = 0, bottom_state_prev = 0;
	public int top_rotate = 0, mid_rotate = 0, bottom_rotate = 0;
	public int lastTickTopRotate = 0, lastTickMidRotate = 0, lastTickBottomRotate = 0;
	public int renderTicks = 0;
	public boolean animate_open = false;
	public boolean animate_open_recess = false;
	public boolean animate_tile_recess = false;
	public boolean break_blocks = false;
	public int slate_1_rotate = 0, slate_2_rotate = 0, slate_3_rotate = 0;
	public int last_tick_slate_1_rotate = 0, last_tick_slate_2_rotate = 0, last_tick_slate_3_rotate = 0;
	public int recess_pos = 0;
	public int last_tick_recess_pos = 0;

	public int tile_1_recess_pos = 0;
	public int last_tick_recess_pos_tile_1 = 0;
	public int tile_2_recess_pos = 0;
	public int last_tick_recess_pos_tile_2 = 0;
	public int tile_3_recess_pos = 0;
	public int last_tick_recess_pos_tile_3 = 0;

	private int prev_shake_timer;
	private int shake_timer;
	private boolean shaking = false;
	private boolean falling_shake = false;

	public boolean hide_slate_1 = false;
	public boolean hide_slate_2 = false;
	public boolean hide_slate_3 = false;
	public boolean hide_lock = false;
	public boolean hide_back_wall = false;
	public boolean is_in_dungeon = false;

	public boolean is_gate_entrance = false;

	private final ItemStack renderStack = new ItemStack(BlockRegistry.MUD_TOWER_BEAM_RELAY.defaultBlockState().getBlock());

	private int shakingTimerMax = 240;

	public TileEntityDungeonDoorRunes(boolean mimic, boolean barishee) {
		super();
		this.mimic = mimic;
		this.barrishee = barishee;
	}

	public TileEntityDungeonDoorRunes() {
		super();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getBlockPos()).inflate(1D);
	}

	public void sinkingParticles(float ySpikeVel) {
		BlockState state = getLevel().getBlockState(getBlockPos());
		Direction facing = state.getValue(BlockDungeonDoorRunes.FACING);
		if (facing == Direction.WEST || facing == Direction.EAST) {
			for (int z = -1; z <= 1; z++)
				if (getLevel().isClientSide())
					spawnSinkingParticles(getBlockPos().add(0, -1, z), 0F + ySpikeVel);
		}

		if (facing == Direction.NORTH || facing == Direction.SOUTH) {
			for (int x = -1; x <= 1; x++)
				if (getLevel().isClientSide())
					spawnSinkingParticles(getBlockPos().add(x, -1, 0), 0F + ySpikeVel);
		}	
	}

	public void crashingParticles(float ySpikeVel) { // used for damaging entities too atm
		BlockState state = getLevel().getBlockState(getBlockPos());
		Direction facing = state.getValue(BlockDungeonDoorRunes.FACING);
		AxisAlignedBB hitBox = new AxisAlignedBB(getBlockPos().offset(facing, 2)).inflate(1D);
		if (facing == Direction.EAST) {
			for (int x = 1; x <= 3; x++)
				for (int z = -1; z <= 1; z++)
					if (getLevel().isClientSide())
						spawnCrashingParticles(getBlockPos().add(x, -1, z), 0F + ySpikeVel);
		}

		if (facing == Direction.WEST) {
			for (int x = -1; x >= -3; x--)
				for (int z = -1; z <= 1; z++)
					if (getLevel().isClientSide())
						spawnCrashingParticles(getBlockPos().add(x, -1, z), 0F + ySpikeVel);
		}

		if (facing == Direction.SOUTH) {
			for (int x = -1; x <= 1; x++)
				for (int z = 1; z <= 3; z++)
					if (getLevel().isClientSide())
						spawnCrashingParticles(getBlockPos().add(x, -1, z), 0F + ySpikeVel);
		}

		if (facing == Direction.NORTH) {
			for (int x = -1; x <= 1; x++)
				for (int z = -1; z >= -3; z--)
					if (getLevel().isClientSide())
						spawnCrashingParticles(getBlockPos().add(x, -1, z), 0F + ySpikeVel);
		}

		List<LivingEntity> list = getLevel().getEntitiesOfClass(LivingEntity.class, hitBox);
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null)
				if (entity instanceof LivingEntity && !(entity instanceof EntityBarrishee))
					if (!getLevel().isClientSide())
						entity.hurt(DamageSource.FALLING_BLOCK, 10F); // dunno what damage to do yet...
		}
	}

	//TODO shrink all this in to 1 method and eventually in to BL particles
	private void spawnSinkingParticles(BlockPos pos, float ySpikeVel) {
		if (getLevel().isClientSide()) {
			double px = pos.getX() + 0.5D;
			double py = pos.getY() + 0.0625D;
			double pz = pos.getZ() + 0.5D;
			for (int i = 0, amount = 2 + getLevel().random.nextInt(2); i < amount; i++) {
				double ox = getLevel().random.nextDouble() * 0.1F - 0.05F;
				double oz = getLevel().random.nextDouble() * 0.1F - 0.05F;
				double motionX = getLevel().random.nextDouble() * 0.2F - 0.1F;
				double motionY = getLevel().random.nextDouble() * 0.1F + 0.075F + ySpikeVel;
				double motionZ = getLevel().random.nextDouble() * 0.2F - 0.1F;
				world.addParticle(ParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(BlockRegistry.MUD_TILES.defaultBlockState()));
			}
		}
	}

	private void spawnCrashingParticles(BlockPos pos, float ySpikeVel) {
		if (getLevel().isClientSide()) {
			double px = pos.getX() + 0.5D;
			double py = pos.getY() + 0.0625D;
			double pz = pos.getZ() + 0.5D;
			for (int i = 0, amount = 2 + getLevel().random.nextInt(2); i < amount; i++) {
				double ox = getLevel().random.nextDouble() * 0.1F - 0.05F;
				double oz = getLevel().random.nextDouble() * 0.1F - 0.05F;
				double motionX = getLevel().random.nextDouble() * 0.2F - 0.1F;
				double motionY = getLevel().random.nextDouble() * 0.025F + 0.075F;
				double motionZ = getLevel().random.nextDouble() * 0.2F - 0.1F;
				world.addParticle(ParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY + ySpikeVel, motionZ, Block.getStateId(BlockRegistry.MUD_BRICKS.defaultBlockState()));
				world.addParticle(ParticleTypes.SMOKE_NORMAL, px + ox, py, pz + oz, motionX, 0D, motionZ);
			}
		}
	}

	@Override
	public void tick() {
		renderTicks++;

		lastTickTopRotate = top_rotate;
		lastTickMidRotate = mid_rotate;
		lastTickBottomRotate = bottom_rotate;

		last_tick_slate_1_rotate = slate_1_rotate;
		last_tick_slate_2_rotate = slate_2_rotate;
		last_tick_slate_3_rotate = slate_3_rotate;

		last_tick_recess_pos = recess_pos;
		
		last_tick_recess_pos_tile_1 = tile_1_recess_pos;
		last_tick_recess_pos_tile_2 = tile_2_recess_pos;
		last_tick_recess_pos_tile_3 = tile_3_recess_pos;

		if (top_state_prev != top_state) {
			top_rotate += 4;
			if (top_rotate > 90) {
				lastTickTopRotate = top_rotate = 0;
				top_state_prev = top_state;
			}
		}

		if (mid_state_prev != mid_state) {
			mid_rotate += 4;
			if (mid_rotate > 90) {
				lastTickMidRotate = mid_rotate = 0;
				mid_state_prev = mid_state;
			}
		}

		if (bottom_state_prev != bottom_state) {
			bottom_rotate += 4;
			if (bottom_rotate > 90) {
				lastTickBottomRotate = bottom_rotate = 0;
				bottom_state_prev = bottom_state;
			}
		}

		if (animate_open_recess) {
			if (recess_pos <= 0)
				if (!getLevel().isClientSide())
					playOpenRecessSound(true);
			shake(240);
			recess_pos += 1;
			int limit = 30;
			if (recess_pos > limit) {
				last_tick_recess_pos = recess_pos = limit;
				if (!getLevel().isClientSide()) {
					animate_open_recess = false;
					if(!animate_open) {
						animate_open = true;
						getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3);
					}
				}
			}
		}

		if (animate_tile_recess) {
			if (!mimic) {
				if(tile_1_recess_pos <= 0)
					if (!getLevel().isClientSide())
						playOpenRecessSound(false);
				if(tile_2_recess_pos == 2)
					if (!getLevel().isClientSide())
						playOpenRecessSound(false);
				if(tile_3_recess_pos == 2)
					if (!getLevel().isClientSide())
						playOpenRecessSound(false);
				tile_1_recess_pos += 2;
				if(tile_1_recess_pos >= 20)
					tile_2_recess_pos += 2;
				if(tile_2_recess_pos >= 20)
					tile_3_recess_pos += 2;
			}
			int limit = 60;	
			if (tile_1_recess_pos >= limit)
				last_tick_recess_pos_tile_1 = tile_1_recess_pos = limit;

			if (tile_2_recess_pos >= limit)
				last_tick_recess_pos_tile_2 = tile_2_recess_pos = limit;

			if (tile_3_recess_pos >= limit) {
				last_tick_recess_pos_tile_3 = tile_3_recess_pos = limit;
				if (!getLevel().isClientSide()) {
					break_blocks = true;
					getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3);
				}
			}
		}

		if (animate_open) {
			if (mimic) {
				if (slate_1_rotate <= 0)
					if (!getLevel().isClientSide()) {
						playTrapFallingSound();

						BlockState state = getLevel().getBlockState(getBlockPos());
						Direction facing = state.getValue(BlockDungeonDoorRunes.FACING);

						//TODO make this shit work properly
						BlockPos offsetPos = getBlockPos().offset(facing.getOpposite());
						if (barrishee) {
							EntityBarrishee entity = new EntityBarrishee(getLevel());
							entity.moveTo(offsetPos.getX() + 0.5D, offsetPos.below().getY(), offsetPos.getZ() + 0.5D, 0F, 0.0F);
							entity.rotationYawHead = entity.yRot;
							entity.renderYawOffset = entity.yRot;
							entity.setIsAmbushSpawn(true);
							entity.setIsScreaming(true);
							entity.setScreamTimer(0);
							getLevel().addFreshEntity(entity);
						}
						else {
							EntityCryptCrawler entity = new EntityCryptCrawler(getLevel());
							entity.moveTo(offsetPos.getX() + 0.5D, offsetPos.below().getY(), offsetPos.getZ() + 0.5D, 0F, 0.0F);
							entity.rotationYawHead = entity.yRot;
							entity.renderYawOffset = entity.yRot;
							entity.setIsBiped(true);
							entity.setIsChief(true);
							entity.onInitialSpawn(getLevel().getCurrentDifficultyAt(getBlockPos()), null);
							getLevel().addFreshEntity(entity);
						}
						
					}
				slate_1_rotate += 4 + (last_tick_slate_1_rotate < 8 ? 0 : last_tick_slate_1_rotate / 8);
				slate_2_rotate += 2 + (last_tick_slate_2_rotate < 6 ? 0 : last_tick_slate_2_rotate / 6);
				slate_3_rotate += 2 + (last_tick_slate_3_rotate < 8 ? 0 : last_tick_slate_3_rotate / 8);
				hide_lock = true;
				hide_back_wall = true;
			}
			if (!mimic) {
				if (slate_1_rotate == 0)
					if (!getLevel().isClientSide()) {
						playOpenSinkingSound();
						if(is_gate_entrance) {
							lightTowerBuild.destroyGateBeamLenses(getLevel(), getBlockPos());
							lightTowerBuild.destroyTowerBeamLenses(getLevel(), getBlockPos().add(-15, -2, -14)); // centre stone of tower  bottom floor
						}
					}
				slate_1_rotate += 4;
				slate_2_rotate += 3;
				slate_3_rotate += 3;
			}
			int limit = mimic ? 90 : 360;
			if(!mimic)
				if (slate_3_rotate < limit - 6)
					sinkingParticles(0F);
				else
					sinkingParticles(0.25F);	
			if (slate_1_rotate >= limit) {
				if (mimic) {
					falling_shake = true;
					crashingParticles(0.125F);
					hide_slate_1 = true;
				}
				last_tick_slate_1_rotate = slate_1_rotate = limit;
			}
			if (slate_2_rotate >= limit) {
				if (mimic) {
					crashingParticles(0.125F);
					hide_slate_2 = true;
				}
				last_tick_slate_2_rotate = slate_2_rotate = limit;
			}
			if (slate_3_rotate >= limit) {
				if (mimic) {
					crashingParticles(0.125F);
					hide_slate_3 = true;
				}
				if(!mimic) {
					hide_slate_1 = true;
					hide_slate_2 = true;
					hide_slate_3 = true;
					hide_lock = true;
					hide_back_wall = true;
				}
				last_tick_slate_3_rotate = slate_3_rotate = limit;
				if (!getLevel().isClientSide()) {
					if(mimic)
						break_blocks = true;
					if(!mimic)
						if(is_in_dungeon)
							animate_tile_recess = true;
						else
							break_blocks = true;
					animate_open = false;
					getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3);
				}
			}
			if (falling_shake) {
				shake(10);
				falling_shake = shaking;
			}
		}

		if (!getLevel().isClientSide()) {
			BlockState state = getLevel().getBlockState(getBlockPos());
			Direction facing = state.getValue(BlockDungeonDoorRunes.FACING);
			if (top_state_prev == top_code && mid_state_prev == mid_code && bottom_state_prev == bottom_code) {
				if(!mimic) {
					if (!animate_open_recess) {
						animate_open_recess = true;
						getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()),getLevel().getBlockState(getBlockPos()), 3);
					}
				}
				else {
					if (!animate_open) {
						animate_open = true;
						getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()),getLevel().getBlockState(getBlockPos()), 3);
					}
				}
			}
			if (break_blocks) {
				if (!mimic)
					if(is_in_dungeon)
						breakAllDoorBlocks(state, facing, true, false);
					else
						breakAllDoorBlocks(state, facing, false, false);
				else {
					breakAllDoorBlocks(state, facing, false, false);
				}
			}

			if (getLevel().getGameTime() % 5 == 0)
				checkComplete(state, facing);
		}
	}

	public void shake(int shakeTimerMax) {
		shakingTimerMax = shakeTimerMax;
		prev_shake_timer = shake_timer;
		if(shake_timer == 0) {
			shaking = true;
			shake_timer = 1;
		}
		if(shake_timer > 0)
			shake_timer++;

		if(shake_timer >= shakingTimerMax)
			shaking = false;
		else
			shaking = true;
	}

	private void checkComplete(BlockState state, Direction facing) {
		if (facing == Direction.WEST || facing == Direction.EAST) {
			for (int z = -1; z <= 1; z++)
				for (int y = -1; y <= 1; y++)
					if (!(getLevel().getBlockState(getBlockPos().add(0, y, z)).getBlock() instanceof BlockDungeonDoorRunes))
						breakAllDoorBlocks(state, facing, false, true);
		}

		if (facing == Direction.NORTH || facing == Direction.SOUTH) {
			for (int x = -1; x <= 1; x++)
				for (int y = -1; y <= 1; y++)
					if (!(getLevel().getBlockState(getBlockPos().add(x, y, 0)).getBlock() instanceof BlockDungeonDoorRunes))
						breakAllDoorBlocks(state, facing, false, true);
		}
	}

	public void breakAllDoorBlocks(BlockState state, Direction facing,  boolean breakFloorBelow, boolean particles) {
		if (facing == Direction.WEST || facing == Direction.EAST) {
			for (int z = -1; z <= 1; z++)
				for (int y = breakFloorBelow ? -2 : -1; y <= 1; y++)
					if (particles) {
						getLevel().destroyBlock(getBlockPos().add(0, y, z), false);
						getLevel().removeTileEntity(getBlockPos());
					}
					else {
						getLevel().setBlockState(getBlockPos().add(0, y, z), Blocks.AIR.defaultBlockState(), 3);
						getLevel().removeTileEntity(getBlockPos());
					}
		}

		if (facing == Direction.NORTH || facing == Direction.SOUTH) {
			for (int x = -1; x <= 1; x++)
				for (int y = breakFloorBelow ? -2 : -1; y <= 1; y++)
					if (particles) {
						getLevel().destroyBlock(getBlockPos().add(x, y, 0), false);
						getLevel().removeTileEntity(getBlockPos());
					}
					else {
						getLevel().setBlockState(getBlockPos().add(x, y, 0), Blocks.AIR.defaultBlockState(), 3);
						getLevel().removeTileEntity(getBlockPos());
					}
		}
	}

	public void cycleTopState() {
		top_state_prev = top_state;
		top_state++;
		if (top_state > 7)
			top_state = 0;
		this.setChanged();
		playLockSound();
	}

	public void cycleMidState() {
		mid_state_prev = mid_state;
		mid_state++;
		if (mid_state > 7)
			mid_state = 0;
		this.setChanged();
		playLockSound();
	}

	public void cycleBottomState() {
		bottom_state_prev = bottom_state;
		bottom_state++;
		if (bottom_state > 7)
			bottom_state = 0;
		this.setChanged();
		playLockSound();
	}
	
	public void enterLockCode() {
		top_code = top_state;
		mid_code = mid_state;
		bottom_code = bottom_state;
		top_state_prev = top_state = 0;
		mid_state_prev = mid_state = 0;
		bottom_state_prev = bottom_state = 0;
		getLevel().playSound(null, getBlockPos(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1F, 1.0F);
		this.setChanged();
	}

	private void playLockSound() {
		getLevel().playSound(null, getBlockPos(), SoundRegistry.MUD_DOOR_LOCK, SoundCategory.BLOCKS, 1F, 1.0F);
	}

	private void playOpenRecessSound(boolean isBigDoor) {
		getLevel().playSound(null, getBlockPos(), SoundRegistry.MUD_DOOR_1, SoundCategory.BLOCKS, isBigDoor ? 1F : 0.5F, 1.0F);
	}

	private void playOpenSinkingSound() {
		getLevel().playSound(null, getBlockPos(), SoundRegistry.MUD_DOOR_2, SoundCategory.BLOCKS, 1F, 0.9F);
	}

	private void playTrapFallingSound() {
		getLevel().playSound(null, getBlockPos(), SoundRegistry.MUD_DOOR_TRAP, SoundCategory.BLOCKS, 1F, 1.25F);
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		top_code = nbt.getInt("top_code");
		mid_code = nbt.getInt("mid_code");
		bottom_code = nbt.getInt("bottom_code");
		top_state = nbt.getInt("top_state");
		mid_state = nbt.getInt("mid_state");
		bottom_state = nbt.getInt("bottom_state");
		top_state_prev = nbt.getInt("top_state_prev");
		mid_state_prev = nbt.getInt("mid_state_prev");
		bottom_state_prev = nbt.getInt("bottom_state_prev");
		mimic = nbt.getBoolean("mimic");
		barrishee = nbt.getBoolean("barrishee");
		animate_open = nbt.getBoolean("animate_open");
		animate_open_recess = nbt.getBoolean("animate_open_recess");
		animate_tile_recess = nbt.getBoolean("animate_tile_recess");
		break_blocks = nbt.getBoolean("break_blocks");
		hide_slate_1 = nbt.getBoolean("hide_slate_1");
		hide_slate_2 = nbt.getBoolean("hide_slate_2");
		hide_slate_3 = nbt.getBoolean("hide_slate_3");
		hide_lock = nbt.getBoolean("hide_lock");
		hide_back_wall = nbt.getBoolean("hide_back_wall");
		is_in_dungeon = nbt.getBoolean("is_in_dungeon");
		is_gate_entrance = nbt.getBoolean("is_gate_entrance");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("top_code", top_code);
		nbt.putInt("mid_code", mid_code);
		nbt.putInt("bottom_code", bottom_code);
		nbt.putInt("top_state", top_state);
		nbt.putInt("mid_state", mid_state);
		nbt.putInt("bottom_state", bottom_state);
		nbt.putInt("top_state_prev", top_state_prev);
		nbt.putInt("mid_state_prev", mid_state_prev);
		nbt.putInt("bottom_state_prev", bottom_state_prev);
		nbt.putBoolean("mimic", mimic);
		nbt.putBoolean("barrishee", barrishee);
		nbt.putBoolean("animate_open", animate_open);
		nbt.putBoolean("animate_open_recess", animate_open_recess);
		nbt.putBoolean("animate_tile_recess", animate_tile_recess);
		nbt.putBoolean("break_blocks", break_blocks);
		nbt.putBoolean("hide_slate_1", hide_slate_1);
		nbt.putBoolean("hide_slate_2", hide_slate_2);
		nbt.putBoolean("hide_slate_3", hide_slate_3);
		nbt.putBoolean("hide_lock", hide_lock);
		nbt.putBoolean("hide_back_wall", hide_back_wall);
		nbt.putBoolean("is_in_dungeon", is_in_dungeon);
		nbt.putBoolean("is_gate_entrance", is_gate_entrance);
		return nbt;
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
		readFromNBT(packet.getTag());
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(isShaking()) {
			double dist = getDistance(viewer);
			float shakeMult = (float) (1.0F - dist / 10.0F);
			if(dist >= 10.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(getShakingProgress(partialTicks) * Math.PI) + 0.1F) * 0.075F * shakeMult);
		} else {
			return 0.0F;
		}
	}

    public float getDistance(Entity entity) {
        float distX = (float)(getBlockPos().getX() - entity.getBlockPosition().getX());
        float distY = (float)(getBlockPos().getY() - entity.getBlockPosition().getY());
        float distZ = (float)(getBlockPos().getZ() - entity.getBlockPosition().getZ());
        return MathHelper.sqrt(distX  * distX  + distY * distY + distZ * distZ);
    }

	public boolean isShaking() {
		return shaking;
	}

	public float getShakingProgress(float delta) {
		return 1.0F / shakingTimerMax * (prev_shake_timer + (shake_timer - prev_shake_timer) * delta);
	}

	public boolean isMimic() {
		return this.mimic;
	}

	public ItemStack cachedStack() {
		return renderStack;
	}
}
