package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.Attributes.IAttribute;
import net.minecraft.entity.ai.attributes.Attributes.RangedAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityWallLivingRoot extends EntityMovingWallFace implements IMob, IEntityMultiPart {
	public static final byte EVENT_DEATH = 3;
	public static final byte EVENT_HURT_SOUND = 82;

	public static final IAttribute MAX_ARM_LENGTH = (new RangedAttribute(null, "bl.maxRootArmLength", 2.5D, 0.0D, 16.0D)).setDescription("Maximum length of root arm").setShouldWatch(true);

	public static class ArmSegment {
		public Vector3d motion = Vector3d.ZERO;

		public Vector3d prevPos, pos;

		public final float[] offsetX, offsetY, offsetZ;

		private final float[][] armCrossSection;

		public ArmSegment(EntityWallLivingRoot root) {
			this.armCrossSection = root.getArmCrossSection();
			this.offsetX = new float[this.armCrossSection.length];
			this.offsetY = new float[this.armCrossSection.length];
			this.offsetZ = new float[this.armCrossSection.length];
		}

		public void updatePrev() {
			if(this.pos == null) {
				this.prevPos = Vector3d.ZERO; 
			} else {
				this.prevPos = this.pos;
			}
		}

		public void update(Vector3d quadUp, Vector3d pos, Vector3d dir) {
			this.pos = pos;

			Vector3d right = dir.cross(quadUp).normalize();
			Vector3d up = right.cross(dir).normalize();

			int i = 0;
			for(float[] hullCrossSection : this.armCrossSection) {
				float hullX = hullCrossSection[0];
				float hullY = hullCrossSection[1];

				this.offsetX[i] = (float) (right.x * hullX + up.x * hullY);
				this.offsetY[i] = (float) (right.y * hullX + up.y * hullY);
				this.offsetZ[i] = (float) (right.z * hullX + up.z * hullY);

				i++;
			}
		}
	}

	private static final DataParameter<Integer> REL_TIP_X = EntityDataManager.defineId(EntityWallLivingRoot.class, DataSerializers.INT);
	private static final DataParameter<Integer> REL_TIP_Y = EntityDataManager.defineId(EntityWallLivingRoot.class, DataSerializers.INT);
	private static final DataParameter<Integer> REL_TIP_Z = EntityDataManager.defineId(EntityWallLivingRoot.class, DataSerializers.INT);

	private boolean rootTipPositionSet = false;

	public final MultiPartEntityPart rootTip;
	private MultiPartEntityPart[] parts;

	private Direction segmentsFacing = Direction.NORTH;

	public List<ArmSegment> armSegments = new ArrayList<>();

	@OnlyIn(Dist.CLIENT)
	private TextureAtlasSprite wallSprite;

	protected int armMovementTicks;

	public EntityWallLivingRoot(World world) {
		super(world);

		this.lookMoveSpeedMultiplier = 8.0F;
		this.experienceValue = 7;

		this.parts = new MultiPartEntityPart[this.getNumSegments() + 1];
		this.parts[0] = this.rootTip = new MultiPartEntityPart(this, "rootTip", this.getNodeSize(0), this.getNodeSize(0));
		for(int i = 0; i < this.getNumSegments(); i++) {
			this.parts[i + 1] = new MultiPartEntityPart(this, "rootNode" + i, this.getNodeSize(this.getNumSegments() - i + 1), this.getNodeSize(this.getNumSegments() - i + 1));
		}
	}

	protected float getNodeSize(int node) {
		return 0.3F;
	}

	protected float[][] getArmCrossSection() {
		float width = this.getFullArmWidth();
		return new float[][] {
			{-width, width},
			{-width, -width},
			{width, -width},
			{width, width},
		};
	}

	protected int getNumSegments() {
		return 8;
	}

	protected float getFullArmWidth() {
		return 0.2F;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.armMovementTicks = this.level.random.nextInt(10000);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(REL_TIP_X, 0);
		this.entityData.define(REL_TIP_Y, 0);
		this.entityData.define(REL_TIP_Z, 0);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();

		this.targetSelector.addGoal(0, new EntityAIHurtByTargetImproved(this, true) {
			@Override
			protected double getTargetDistance() {
				return 8.0D;
			}
		});
		this.targetSelector.addGoal(1, new EntityAINearestAttackableTarget<>(this, PlayerEntity.class, 0, true, false, null).setUnseenMemoryTicks(120));

		this.goalSelector.addGoal(0, new AITrackTarget<EntityWallLivingRoot>(this, true, 28.0D) {
			@Override
			protected boolean canMove() {
				return true;
			}
		});
		this.goalSelector.addGoal(1, new AIArmAttack(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.08D);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.5D);
		this.getAttributeMap().registerAttribute(MAX_ARM_LENGTH);
	}

	public Vector3d getTipPos() {
		return new Vector3d(this.getX() + this.entityData.get(REL_TIP_X) / 512.0f, this.getY() + this.entityData.get(REL_TIP_Y) / 512.0f, this.getZ() + this.entityData.get(REL_TIP_Z) / 512.0f);
	}

	public void setTipPos(Vector3d pos) {
		this.entityData.set(REL_TIP_X, (int)((pos.x - this.getX()) * 512));
		this.entityData.set(REL_TIP_Y, (int)((pos.y - this.getY()) * 512));
		this.entityData.set(REL_TIP_Z, (int)((pos.z - this.getZ()) * 512));
	}

	protected Vector3d updateTargetTipPos(Vector3d armStartWorld, float maxArmLength, Vector3d dirFwd, Vector3d dirUp) {
		float flailingStrength = this.isSwingInProgress ? (1 - this.swingProgress) : this.hurtTime > 0 ? (this.hurtTime / (float)this.maxHurtTime) * 0.5F : 0.0f;

		this.armMovementTicks += 1 + (int)(flailingStrength * 10);

		float idleX = MathHelper.cos(this.armMovementTicks / 9.0f) * 0.75F;
		float idleY = MathHelper.sin(this.armMovementTicks / 7.0f) * 0.75F;
		float idleZ = (MathHelper.cos(this.armMovementTicks / 15.0f) + 1) * 0.25f;

		Vector3d targetTipPos = armStartWorld.add(dirFwd.scale(maxArmLength));

		LivingEntity target = this.getAttackTarget();
		if(target != null) {
			targetTipPos = target.getDeltaMovement().add(0, target.height / 2, 0);
		}

		float forwardPos = (float) dirFwd.dotProduct(targetTipPos.subtract(armStartWorld));
		float offsetZ = 0.0f;
		if(forwardPos < 1.0F) {
			offsetZ = 1.0F - forwardPos;
		}

		//Idle movement
		targetTipPos = targetTipPos.add(dirUp.scale(idleY)).add(dirFwd.cross(dirUp).scale(idleX)).add(dirFwd.scale(offsetZ - idleZ));

		Vector3d tipPos = this.rootTip.getDeltaMovement();

		Vector3d tipDiff = targetTipPos.subtract(tipPos);
		targetTipPos = tipPos.add(tipDiff.normalize().scale(Math.min(tipDiff.length(), 0.1D + flailingStrength * 0.9D)));

		return targetTipPos;
	}

	protected float getArmLengthSlack() {
		return 0.0f;
	}

	@Override
	public void tick() {
		super.tick();

		if(!this.level.isClientSide() && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
			this.remove();
		}

		float maxArmLength = (float)this.getAttribute(MAX_ARM_LENGTH).getValue() * this.getArmSize(1);

		float segmentLength = maxArmLength / (float)(this.getNumSegments() - 2);

		Vector3d dirFwd = new Vector3d(this.getFacing().getStepX(), this.getFacing().getStepY(), this.getFacing().getStepZ());;
		Vector3d dirUp = new Vector3d(this.getFacingUp().getStepX(), this.getFacingUp().getStepY(), this.getFacingUp().getStepZ());

		Vector3d armStart = new Vector3d(0, this.height / 2, 0).add(-dirFwd.x * (this.width / 2 - 0.1f), -dirFwd.y * (this.height / 2 - 0.1f), -dirFwd.z * (this.width / 2 - 0.1f));
		Vector3d ikArmStart = new Vector3d(0, this.height / 2, 0).add(dirFwd.scale(0.1f));

		for(MultiPartEntityPart part : this.parts) {
			part.tick();
		}

		if(!this.rootTipPositionSet) {
			Vector3d tipPos = this.getDeltaMovement().add(armStart.add(dirFwd.scale(maxArmLength)).add(0, -this.rootTip.height / 2, 0));
			this.setTipPos(tipPos);
			this.rootTip.setPosition(tipPos.x, tipPos.y, tipPos.z);
			this.rootTipPositionSet = true;
		}

		Vector3d armEnd = this.rootTip.getDeltaMovement().add(0, this.rootTip.height / 2, 0).subtract(this.getDeltaMovement());

		if(!this.level.isClientSide()) {
			Vector3d armStartWorld = this.getDeltaMovement().add(ikArmStart);

			Vector3d tipPos = this.updateTargetTipPos(armStartWorld, maxArmLength, dirFwd, dirUp);

			//Clamp to max reach sphere
			tipPos = armStartWorld.add(tipPos.subtract(armStartWorld).normalize().scale(Math.min(tipPos.subtract(armStartWorld).length(), maxArmLength + this.getArmLengthSlack())));

			this.setTipPos(tipPos);
			this.rootTip.setPosition(tipPos.x, tipPos.y, tipPos.z);
		} else {
			Vector3d tipPos = this.getTipPos();
			this.rootTip.setPosition(tipPos.x, tipPos.y, tipPos.z);

			this.updateWallSprite();
		}

		if(this.armSegments.size() != this.getNumSegments() || this.getFacing() != this.segmentsFacing) {
			this.armSegments.clear();

			for(int i = 0; i < this.getNumSegments(); i++) {
				ArmSegment segment = new ArmSegment(this);
				float dist = maxArmLength / (float)(this.getNumSegments() - 1) * i;
				segment.update(dirUp, ikArmStart.add(dirFwd.x * dist, dirFwd.y * dist, dirFwd.z * dist), dirFwd);
				this.armSegments.add(segment);
			}

			this.segmentsFacing = this.getFacing();
		}

		for(ArmSegment segment : this.armSegments) {
			segment.updatePrev();

			segment.pos = segment.pos.offset(segment.motion);
		}

		for(int i = this.getNumSegments() - 2; i >= 2; i--) {
			ArmSegment segment = this.armSegments.get(i);

			Vector3d target;
			if(i == this.getNumSegments() - 2) {
				target = armEnd;
			} else {
				target = this.armSegments.get(i + 1).pos;
			}

			Vector3d dir = segment.pos.subtract(target).normalize();

			segment.update(dirUp, target.add(dir.scale(segmentLength)), dir.scale(-1));
		}

		for(int i = 2; i < this.getNumSegments(); i++) {
			ArmSegment segment = this.armSegments.get(i);

			Vector3d target;
			if(i == 0) {
				target = ikArmStart;
			} else {
				target = this.armSegments.get(i - 1).pos;
			}

			Vector3d dir = segment.pos.subtract(target).normalize();

			segment.update(dirUp, target.add(dir.scale(segmentLength)), dir.scale(-1));
		}

		ArmSegment startSegment = this.armSegments.get(0);
		startSegment.update(dirUp, armStart, new Vector3d(-dirFwd.x, -dirFwd.y, -dirFwd.z));

		ArmSegment startSegment2 = this.armSegments.get(1);
		startSegment2.update(dirUp, ikArmStart, new Vector3d(-dirFwd.x, -dirFwd.y, -dirFwd.z));

		ArmSegment endSegment = this.armSegments.get(this.armSegments.size() - 1);
		endSegment.update(dirUp, armEnd, this.armSegments.get(this.armSegments.size() - 2).pos.subtract(armEnd).normalize());

		for(int i = 0; i < this.getNumSegments(); i++) {
			ArmSegment segment = this.armSegments.get(i);
			Vector3d pos = segment.pos;
			this.parts[i + 1].setPosition(this.getX() + pos.x, this.getY() + pos.y - this.parts[i + 1].height / 2.0f, this.getZ() + pos.z);
		}
	}

	@Override
	protected void updateMovement() {
		if(!this.level.isClientSide() && this.isMoving() && this.getMoveReason() != MoveReason.LOOK) {
			boolean wasFirstHalf = this.getMovementProgress(1) < 0.5F;

			super.updateMovement();

			if(this.getMovementProgress(1) >= 0.5F && wasFirstHalf) {
				this.world.playLocalSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.WALL_LIVING_ROOT_EMERGE, SoundCategory.HOSTILE, 1, 1);
			}
		} else {
			super.updateMovement();
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.updateArmSwingProgress();
	}

	@OnlyIn(Dist.CLIENT)
	protected void updateWallSprite() {
		this.wallSprite = null;

		BlockPos pos = this.getPosition();

		BlockState state = this.world.getBlockState(pos);
		state = state.getActualState(this.world, pos);

		if(state.isFullCube()) {
			this.wallSprite = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
		}
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public TextureAtlasSprite getWallSprite() {
		return this.wallSprite;
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		this.armMovementTicks = nbt.getInt("armMovementTicks");
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putInt("armMovementTicks", this.armMovementTicks);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected void playHurtSound(DamageSource source) {
		this.world.setEntityState(this, EVENT_HURT_SOUND);
	}

	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_HURT_SOUND || id == EVENT_DEATH) {
			SoundType soundType = SoundType.WOOD;
			this.world.playLocalSound(this.getX(), this.getY(), this.getZ(), soundType.getBreakSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 1.3F, soundType.getPitch() * 0.8F, false);
			this.world.playLocalSound(this.getX(), this.getY(), this.getZ(), soundType.getHitSound(), SoundCategory.NEUTRAL, (soundType.getVolume() + 1.0F) / 4.0F, soundType.getPitch() * 0.5F, false);
		}
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.WALL_LIVING_ROOT;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos, Direction facing, Direction facingUp) {
		return this.isValidBlockForMovement(pos, this.world.getBlockState(pos));
	}

	@Override
	protected boolean isValidBlockForMovement(BlockPos pos, BlockState state) {
		return state.canOcclude() && state.isNormalCube() && state.isFullCube() && state.getBlockHardness(this.world, pos) > 0 && (state.getMaterial() == Material.ROCK || state.getMaterial() == Material.WOOD);
	}

	@Override
	public Vector3d getOffset(float movementProgress) {
		return super.getOffset(1.0F);
	}

	public float getArmSize(float partialTicks) {
		return this.getHalfMovementProgress(partialTicks);
	}

	public float getHoleDepthPercent(float partialTicks) {
		return this.getHalfMovementProgress(partialTicks);
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public Entity[] getParts() {
		return this.parts;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		LivingEntity attacker = source.getImmediateSource() instanceof LivingEntity ? (LivingEntity)source.getImmediateSource() : null;
		if(attacker != null && attacker.getActiveHand() != null) {
			ItemStack item = attacker.getItemInHand(attacker.getActiveHand());
			if(!item.isEmpty() && item.getItem().getToolClasses(item).contains("axe")) {
				amount *= 2.0F;
			}
		}
		return super.hurt(source, amount);
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
		return this.hurt(source, damage);
	}

	protected static class AIArmAttack extends EntityAIBase {
		protected final EntityWallLivingRoot entity;
		protected int attackTicks;

		public AIArmAttack(EntityWallLivingRoot entity) {
			this.entity = entity;
		}

		@Override
		public boolean canUse() {
			return this.entity.getAttackTarget() != null;
		}

		@Override
		public void updateTask() {
			Entity target = this.entity.getAttackTarget();

			if(this.attackTicks > 0) {
				this.attackTicks--;
			} else if(target != null && target.getBoundingBox().intersects(this.entity.rootTip.getBoundingBox())) {
				this.entity.attackEntityAsMob(target);
				this.entity.swing(Hand.MAIN_HAND);
				this.attackTicks = 20;
			}
		}
	}
}
