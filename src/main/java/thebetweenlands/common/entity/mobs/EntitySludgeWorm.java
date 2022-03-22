package thebetweenlands.common.entity.mobs;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.entity.EntityTinyWormEggSac;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySludgeWorm extends EntityMob implements IEntityMultiPart, IMob, IEntityBL {

	public MultiPartEntityPart[] parts;

	public boolean debugHitboxes = false;

	Random rand = new Random();

	private AxisAlignedBB renderBoundingBox;
	
	private int wallInvulnerabilityTicks = 40;

	private boolean doSpawningAnimation = true;
	
	public EntitySludgeWorm(World world) {
		super(world);
		setSize(0.4375F, 0.3125F);
		setPathPriority(PathNodeType.WATER, -10.0F);
		fireImmune = true;
		parts = new MultiPartEntityPart[] {
				new MultiPartEntityPart(this, "part1", 0.4375F, 0.3125F),
				new MultiPartEntityPart(this, "part2", 0.3125F, 0.3125F),
				new MultiPartEntityPart(this, "part3", 0.3125F, 0.3125F),
				new MultiPartEntityPart(this, "part4", 0.3125F, 0.3125F),
				new MultiPartEntityPart(this, "part5", 0.3125F, 0.3125F),
				new MultiPartEntityPart(this, "part6", 0.3125F, 0.3125F),
				new MultiPartEntityPart(this, "part7", 0.3125F, 0.3125F),
				new MultiPartEntityPart(this, "part8", 0.3125F, 0.3125F),
				new MultiPartEntityPart(this, "part9", 0.3125F, 0.3125F) };
		this.renderBoundingBox = this.getBoundingBox();
	}
	
	public EntitySludgeWorm(World world, boolean doSpawningAnimation) {
		this(world);
		this.doSpawningAnimation = doSpawningAnimation;
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAIAttackMelee(this, 1, false));
		tasks.addTask(3, new EntityAIWander(this, 0.8D, 1));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, PlayerEntity.class, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, LivingEntity.class, 10, true, false, entity -> entity instanceof IMob == false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(10.0D);
		getEntityAttribute(Attributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.21D);
		getEntityAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.25D);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		setMoveForward(0.2F);
		setHitBoxes();
	}

	protected float getHeadMotionYMultiplier() {
		return this.doSpawningAnimation && this.tickCount < 20 ? 0.65F : 1.0F;
	}

	protected float getTailMotionYMultiplier() {
		return this.doSpawningAnimation && this.tickCount < 20 ? 0.0F : 1.0F;
	}

	@Override
	public void tick() {
		super.tick();
		
		if(this.level.isClientSide() && this.tickCount % 10 == 0) {
			this.spawnParticles(this.world, this.getX(), this.getY(), this.getZ(), this.rand);
		}

		if(this.wallInvulnerabilityTicks > 0) {
			this.wallInvulnerabilityTicks--;
		}
		
		motionY *= this.getHeadMotionYMultiplier();

		this.renderBoundingBox = this.getBoundingBox();
		for(MultiPartEntityPart part : this.parts) {
			this.renderBoundingBox = this.renderBoundingBox.union(part.getBoundingBox());
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void spawnParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 1 + world.rand.nextInt(4); ++count) {
			double a = Math.toRadians(renderYawOffset);
			double offSetX = -Math.sin(a) * 0D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			double offSetZ = Math.cos(a) * 0D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			BLParticles.TAR_BEAST_DRIP.spawn(world , x + offSetX, y, z + offSetZ).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
		}
	}

	// can be set to any part(s) - dunno if we want this either
	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float dmg) {
		damageWorm(source, dmg * 0.75F);
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source == DamageSource.OUT_OF_WORLD || (source instanceof EntityDamageSource && ((EntityDamageSource) source).getIsThornsDamage())) {
			damageWorm(source, amount);
		} else if(source == DamageSource.IN_WALL && this.wallInvulnerabilityTicks > 0) {
			return false;
		} else {
			damageWorm(source, amount);
		}
		return true;
	}

	@Override
	public boolean canAttackClass(Class<? extends LivingEntity> entity) {
		return !IEntityBL.class.isAssignableFrom(entity) && EntityTinyWormEggSac.class != entity;
	}

	protected boolean damageWorm(DamageSource source, float amount) {
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public Entity[] getParts() {
		return parts;
	}

	private void setHitBoxes() {
		if (tickCount == 1) {
			for(int i = 1; i < this.parts.length; i++) {
				this.parts[i].moveTo(posX, posY, posZ, yRot, 0F);
			}
		}

		this.parts[0].moveTo(posX, posY, posZ, yRot, 0);

		for(MultiPartEntityPart part : this.parts) {
			part.prevRotationYaw = part.yRot;
			part.prevRotationPitch = part.xRot;

			if(part != this.parts[0]) {
				part.xOld = part.lastTickPosX = part.getX();
				part.yOld = part.lastTickPosY = part.getY();
				part.zOld = part.lastTickPosZ = part.getZ();

				if(part.getY() < this.getY() && this.world.collidesWithAnyBlock(part.getBoundingBox())) {
					part.move(MoverType.SELF, 0, 0.1D, 0);
					part.motionY = 0.0D;
				}

				part.move(MoverType.SELF, 0, part.motionY, 0);

				part.motionY -= 0.08D;
				part.motionY *= 0.98D * this.getTailMotionYMultiplier();
			}
		}

		for(int i = 1; i < this.parts.length; i++) {
			this.movePiecePos(this.parts[i], this.parts[i - 1], 4.5F, 2F);
		}
	}

	protected double getMaxPieceDistance() {
		return 0.3D;
	}

	public void movePiecePos(MultiPartEntityPart targetPart, MultiPartEntityPart destinationPart, float speed, float yawSpeed) {
		//TODO make this better and use the parent entities motionY 
		if (destinationPart.getY() - targetPart.getY() < -0.5D)
			speed = 1.5F;

		double movementTolerance = 0.05D;
		double maxDist = this.getMaxPieceDistance();

		boolean correctY = false;

		for(int i = 0; i < 5; i++) {
			Vector3d diff = destinationPart.getPositionVector().subtract(targetPart.getPositionVector());
			double len = diff.length();

			if(len > maxDist) {
				Vector3d correction = diff.scale(1.0D / len * (len - maxDist));
				targetPart.getX() += correction.x;
				targetPart.getZ() += correction.z;

				targetPart.setPosition(targetPart.getX(), targetPart.getY(), targetPart.getZ());

				double cy = targetPart.getY();

				targetPart.move(MoverType.SELF, 0, correction.y, 0);

				if(Math.abs((targetPart.getY() - cy) - correction.y) <= movementTolerance) {
					correctY = true;
					break;
				}
			}
		}

		//Welp, failed to move smoothly along Y, just clip
		if(!correctY) {
			Vector3d diff = destinationPart.getPositionVector().subtract(targetPart.getPositionVector());
			double len = diff.lengthSqr();

			if(len > maxDist) {
				Vector3d correction = diff.scale(1.0D / len * (len - maxDist));

				targetPart.getX() += correction.x;
				targetPart.getY() += correction.y;
				targetPart.getZ() += correction.z;
			}
		}


		Vector3d diff = new Vector3d(destinationPart.getX(), 0, destinationPart.getZ()).subtract(new Vector3d(targetPart.getX(), 0, targetPart.getZ()));
		float destYaw = (float)Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90;

		double yawDiff = (destYaw - targetPart.yRot) % 360.0F;
		double yawInterpolant = 2 * yawDiff % 360.0F - yawDiff;

		targetPart.yRot += yawInterpolant / yawSpeed;

		targetPart.xRot = 0;

		targetPart.setPosition(targetPart.getX(), targetPart.getY(), targetPart.getZ());
	}

	@Override
	public World getWorld() {
		return level;
	}

	// temp Sounds until we have proper ones
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.WORM_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.WORM_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.WORM_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		playSound(SoundRegistry.WORM_LIVING, 0.5F, 1F);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return this.renderBoundingBox;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SMALL_SLUDGE_WORM;
	}
}
