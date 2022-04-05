package thebetweenlands.common.entity.rowboat;

import java.util.EnumMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.Hand;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.network.serverbound.MessageRow;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.CubicBezier;
import thebetweenlands.util.Mat4d;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.Matrix;
import thebetweenlands.util.OpenSimplexNoise;
import thebetweenlands.util.Quat;

import io.netty.buffer.PacketBuffer;

import javax.annotation.Nullable;

/*
 * Useful links:
 * https://en.wikipedia.org/wiki/Glossary_of_rowing_terms
 * https://en.wikipedia.org/wiki/Glossary_of_nautical_terms
 * https://en.wikipedia.org/wiki/List_of_ship_directions
 * https://en.wikipedia.org/wiki/Anatomy_of_a_rowing_stroke
 */
public class EntityWeedwoodRowboat extends BoatEntity implements IEntityAdditionalSpawnData {
	
    private static final CubicBezier DEVIATION_DRAG = new CubicBezier(0.9F, 0, 1, 0.6F);

    private static final CubicBezier SPEED_WAVE_POWER = new CubicBezier(0, 1, 0, 1);

    private static final EnumMap<ShipSide, DataParameter<Float>> ROW_PROGRESS = ShipSide.newEnumMap((Class<DataParameter<Float>>) (Class<?>) DataParameter.class, defineId(DataSerializers.FLOAT), defineId(DataSerializers.FLOAT));

    private static final DataParameter<Boolean> IS_TARRED = defineId(DataSerializers.BOOLEAN);

    private static final DataParameter<Boolean> HAS_LANTERN = defineId(DataSerializers.BOOLEAN);

    public static final float OAR_ROTATION_SCALE = -28;

    public static final float ROW_PROGRESS_PERIOD = MathUtils.TAU / Math.abs(OAR_ROTATION_SCALE);

    private static final float OAR_LENGTH = 40F / 16;

    private static final float BLADE_LENGTH = 12F / 16;

    private static final float LOOM_LENGTH = OAR_LENGTH - BLADE_LENGTH;

    private static final float RESTING_ROW_PROGRESS = ROW_PROGRESS_PERIOD * 0.05F;

    private static final int FORCE_SETTLE_DURATION = 10;

    private static final Quat UP = Quat.fromAxisAngle(0, 1, 0, 0);

    private static final OpenSimplexNoise WAVE_RNG = new OpenSimplexNoise(6354); // 1486858338

    private static final EnumMap<ShipSide, SoundEvent> SOUND_ROW = ShipSide.newEnumMap(SoundEvent.class, SoundRegistry.ROWBOAT_ROW_STARBOARD, SoundRegistry.ROWBOAT_ROW_PORT);

    private static final EnumMap<ShipSide, SoundEvent> SOUND_ROW_START = ShipSide.newEnumMap(SoundEvent.class, SoundRegistry.ROWBOAT_ROW_START_STARBOARD, SoundRegistry.ROWBOAT_ROW_START_PORT);

    private EnumMap<ShipSide, OarState> oars = ShipSide.newEnumMap(OarState.class, new OarState(), new OarState());

    public class OarState {
        float rowForce = 0.0F;
        int rowTime = FORCE_SETTLE_DURATION;
        float prevRowProgress = RESTING_ROW_PROGRESS;
        float rowProgress = RESTING_ROW_PROGRESS;
        boolean oarState = false;
        boolean oarInAir = false;
        float prevOarXWavePull;
        float prevOarZWavePull;
        float oarXWavePull;
        float oarZWavePull;
    }

    private float drag;

    private float submergeTicks;

    private int inWaterTicks;

    private float rotationalVelocity;

    private double serverX;

    private double serverY;

    private double serverZ;

    private float boatYaw;

    private float boatPitch;

    private int serverT;

    private boolean prevOarStrokeLeft;

    private boolean prevOarStrokeRight;

    private ShipSide synchronizer = ShipSide.STARBOARD;

    private Quat prevRotation = Quat.fromAxisAngle(0, 1, 0, 0);

    private Quat rotation = new Quat(prevRotation);

    private double prevWaveHeight;

    private double waveHeight;

    private float prevPilotPower;

    private float pilotPower;

    @Nullable
    private RowboatLantern lantern;

    public EntityWeedwoodRowboat(World world) {
        super(world);
        setSize(2, 0.9F);
    }

    public EntityWeedwoodRowboat(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ROW_PROGRESS.get(ShipSide.STARBOARD), RESTING_ROW_PROGRESS);
        this.entityData.define(ROW_PROGRESS.get(ShipSide.PORT), RESTING_ROW_PROGRESS);
        this.entityData.define(IS_TARRED, false);
        this.entityData.define(HAS_LANTERN, false);
    }

    @Override
    public double getMountedYOffset() {
        return getWaveHeight(1);
    }

    @Override
    public Item getItemBoat() {
        return ItemRegistry.WEEDWOOD_ROWBOAT;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return getItem();
    }

    public ItemStack getItem() {
        ItemStack stack = new ItemStack(getItemBoat());
        CompoundNBT attrs = new CompoundNBT();
        writeEntityToNBT(attrs);
        if (attrs.getSize() > 0) {
            stack.setTagInfo("attributes", attrs);   
        }
        return stack;
    }

    public void setIsTarred(boolean isTarred) {
        entityData.set(IS_TARRED, isTarred);
    }

    public boolean isTarred() {
        return entityData.get(IS_TARRED);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void updateInputs(boolean starboard, boolean port, boolean forward, boolean backward) {
        oars.get(ShipSide.STARBOARD).oarState = starboard;
        oars.get(ShipSide.PORT).oarState = port;
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int t, boolean teleport) {
        serverX = x;
        serverY = y;
        serverZ = z;
        boatYaw = yaw;
        boatPitch = pitch;
        serverT = 10;
    }

    @Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        posX = MathHelper.clamp(x, -3E7, 3E7);
        posY = y;
        posZ = MathHelper.clamp(z, -3E7, 3E7);
        // Keep prev for serverside onUpdate
        //if (level.isClientSide()) {
        xOld = posX;
        yOld = posY;
        zOld = posZ;   
        //}
        pitch = MathHelper.clamp(pitch, -90, 90);
        yRot = yaw;
        xRot = pitch;
        prevRotationYaw = yRot;
        prevRotationPitch = xRot;
        double delta = prevRotationYaw - yaw;
        if (delta < -180) {
            prevRotationYaw += 360;
        }
        if (delta >= 180) {
            prevRotationYaw -= 360;
        }
        setPosition(posX, posY, posZ);
        setRotation(yaw, pitch);
    }

    public void setOarStates(boolean starboard, boolean port, float progressStarboard, float progressPort) {
        setPaddleState(port, starboard);
        setRowProgress(ShipSide.STARBOARD, progressStarboard);
        setRowProgress(ShipSide.PORT, progressPort);
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return getPassengers().isEmpty();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (isEntityInvulnerable(source)) {
            return false;
        }
        if (!level.isClientSide() && !isDead) {
            if (source instanceof EntityDamageSourceIndirect && source.getTrueSource() != null && isPassenger(source.getTrueSource())) {
                return false;
            }
            setForwardDirection(-getForwardDirection());
            setTimeSinceHit(10);
            setDamageTaken(getDamageTaken() + amount * 10);
            markVelocityChanged();
            boolean creative = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity) source.getTrueSource()).isCreative();
            if (creative || getDamageTaken() > 20) {
                if (!creative && world.getGameRules().getBoolean("doEntityDrops")) {
                    entityDropItem(getItem(), 0);
                }
                remove();
            }
        }
        return true;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (HAS_LANTERN.equals(key)) {
            lantern = hasLantern() ? new RowboatLantern(1.2F, 0.2F) : null;
        }
        super.notifyDataManagerChange(key);
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (EnumItemMisc.TAR_DRIP.isItemOf(stack) && !isTarred()) {
            if (!level.isClientSide()) {
                setIsTarred(true);
                stack.shrink(1);
                playSound(SoundRegistry.TAR_BEAST_STEP, 0.9F + rand.nextFloat() * 0.1F, 0.6F + rand.nextFloat() * 0.15F);
            }
            player.swing(hand);
        } else if (!stack.isEmpty() && stack.getItem() == ItemRegistry.WEEDWOOD_ROWBOAT_UPGRADE_LANTERN) {
            if (!level.isClientSide()) {
                stack.shrink(1);
                setHasLantern(true);
            }
            player.swing(hand);
        } else if (!level.isClientSide() && !player.isCrouching()) {
            player.startRiding(this);
        }
        return true;
    }

    private void setHasLantern(boolean lantern) {
        entityData.set(HAS_LANTERN, lantern);
    }

    private boolean hasLantern() {
        return entityData.get(HAS_LANTERN);
    }

    @Override
    protected void updateFallState(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (onGround) {
            if (fallDistance > 0) {
                state.getBlock().onFallenUpon(world, pos, this, fallDistance);
            }
            fallDistance = 0;
        } else if (y < 0) {
            fallDistance -= y;
        }
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (level.isClientSide() && getControllingPassenger() == passenger) {
            TheBetweenlands.proxy.onPilotEnterWeedwoodRowboat(passenger);
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (level.isClientSide() && getControllingPassenger() == passenger) {
            TheBetweenlands.proxy.onPilotExitWeedwoodRowboat(this, passenger);
        }
        super.removePassenger(passenger);
    }

    @Override
    public void tick() {
        double pow = 1 - SPEED_WAVE_POWER.eval(MathHelper.sqrt((posX - xOld) * (posX - xOld) + (posZ - zOld) * (posZ - zOld)));
        if (!level.isClientSide()) {
            setFlag(6, isGlowing());
        }
        onEntityUpdate();
        if (getTimeSinceHit() > 0) {
            setTimeSinceHit(getTimeSinceHit() - 1);
        }
        if (getDamageTaken() > 0) {
            setDamageTaken(getDamageTaken() - 1);
        }
        tickLerp();
        if (level.isClientSide()) {
            updateClientOarProgress(ShipSide.STARBOARD);
            updateClientOarProgress(ShipSide.PORT);
        }
        if (canPassengerSteer()) {
            if (getPassengers().size() == 0 || !(getPassengers().get(0) instanceof PlayerEntity)) {
                setPaddleState(false, false);
            }
            applyForces();
        }
        boolean left = getAppropriateOarState(ShipSide.STARBOARD);
        boolean right = getAppropriateOarState(ShipSide.PORT);
        updateRowForce(ShipSide.STARBOARD, left, prevOarStrokeLeft);
        updateRowForce(ShipSide.PORT, right, prevOarStrokeRight);
        updatePilotPull();
        prevOarStrokeLeft = left;
        prevOarStrokeRight = right;
        prevRotation = new Quat(rotation);
        prevWaveHeight = waveHeight;
        OarState oarStarboard = oars.get(ShipSide.STARBOARD);
        OarState oarPort = oars.get(ShipSide.PORT);
        oarStarboard.prevOarXWavePull = oarStarboard.oarXWavePull;
        oarPort.prevOarXWavePull = oarPort.oarXWavePull;
        oarStarboard.prevOarZWavePull = oarStarboard.oarZWavePull;
        oarPort.prevOarZWavePull = oarPort.oarZWavePull;
        if (inWater) {
            hitWaves(pow);
            inWaterTicks++;
        } else {
            rotation.interpolate(UP, 0.175);
            waveHeight -= waveHeight * 0.6F;
            if (waveHeight < 1e-3F) {
                waveHeight = 0;
            }
            inWaterTicks = 0;
        }
        if (canPassengerSteer()) {
            Vector3d motion = null;
            if (level.isClientSide()) {
                motion = applyRowForce();
            }
            yRot += rotationalVelocity;
            if (level.isClientSide()) {
                if (motion != null) {
                    updateMotion(motion);
                }
                TheBetweenlands.networkWrapper.sendToServer(new MessageRow(oarStarboard.oarState, oarPort.oarState, oarStarboard.rowProgress, oarPort.rowProgress));
            }
            float rotationLeft = getAppropriateRowProgress(ShipSide.STARBOARD);
            float rotationRight = getAppropriateRowProgress(ShipSide.PORT);
            returnOarToResting(ShipSide.STARBOARD, rotationLeft);
            returnOarToResting(ShipSide.PORT, rotationRight);
            synchronizeOars();
            move(MoverType.SELF, motionX, motionY, motionZ);
        } else {
            motionX = motionY = motionZ = 0;
        }
        doBlockCollisions();
        if (inWater) {
            if (level.isClientSide()) {
                animateHullWaterInteraction();
                animateOars();   
            } else {
                createSoundFX();
            }
        }
        if (lantern != null && level.isClientSide()) {
            lantern.tick(getLanternPosition(), yRot);
        }
        if (!level.isClientSide()) {
            world.getEntitiesWithinAABBExcludingEntity(this, getBoundingBox().inflate(0.2, 0.05, 0.2)).forEach(this::applyEntityCollision);
        }
        yRot = MathHelper.wrapDegrees(yRot);
        prevRotationYaw = MathUtils.adjustAngleForInterpolation(yRot, prevRotationYaw);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }

    @Nullable
    public RowboatLantern getLantern() {
        return this.lantern;
    }

    private void hitWaves(double pow) {
        // TODO: custom smooth sync total world time
        double t = world.getGameTime() * 0.03;
        double roughness = 0.15 * pow * (inWaterTicks < 20 ? inWaterTicks / 20D : 1), scale = 0.5;
        double x = posX, z = posZ;
        Matrix mat = new Matrix();
        mat.rotate(rotation);
        mat.rotate(-yRot * MathUtils.DEG_TO_RAD, 0, 1, 0);
        Vector3d posFront = mat.transform(new Vector3d(0, 0, 0.75));
        Vector3d posStarboard = mat.transform(new Vector3d(0.435, 0, -0.5));
        Vector3d posPort = mat.transform(new Vector3d(-0.435, 0, 0.5));
        double sx0 = posFront.x;
        double sz0 = posFront.z;
        double sx1 = posStarboard.x;
        double sz1 = posStarboard.z;
        double sx2 = posPort.x;
        double sz2 = posPort.z;
        double sy0 = WAVE_RNG.eval((x + sx0) * scale, t, (z + sz0) * scale) * roughness;
        double sy1 = WAVE_RNG.eval((x + sx1) * scale, t, (z + sz1) * scale) * roughness;
        double sy2 = WAVE_RNG.eval((x + sx2) * scale, t, (z + sz2) * scale) * roughness;
        Vector3d s0 = new Vector3d(sx0 * scale, sy0, sz0 * scale);
        Vector3d s1 = new Vector3d(sx1 * scale, sy1, sz1 * scale);
        Vector3d s2 = new Vector3d(sx2 * scale, sy2, sz2 * scale);
        Vector3d normal = s2.subtract(s1).cross(s0.subtract(s1)).normalize();
        Vector3d yAxis = new Vector3d(0, 1, 0);
        double angle = Math.acos(Math.max(Math.min(normal.dotProduct(yAxis), 1), -1));
        Vector3d axis = normal.cross(yAxis).normalize();
        Quat wave = Quat.fromAxisAngle(axis.x, axis.y, axis.z, -angle * 0.4);
        rotation.interpolate(wave, 0.2);
        Vector3d point = new Vector3d(0, roughness, 0);
        waveHeight = point.subtract(normal.scale(point.subtract(s0).dotProduct(normal))).y; 
        pullOarByWave(ShipSide.STARBOARD, normal);
        pullOarByWave(ShipSide.PORT, normal);
        if (!isTarred() && canPassengerSteer()) {
            double wx = normal.x;
            double wz = normal.z;
            double mag = Math.sqrt(wx * wx + wz * wz);
            double strength = mag / Math.min(((1 - normal.y) * 1.8 * roughness), 0.00125);
            if (strength > 0) {
                motionX += wx / strength;
                motionZ += wz / strength;
                double dir = Math.atan2(wz, wx) * MathUtils.RAD_TO_DEG;
                rotationalVelocity += Math.signum(MathUtils.modularDelta(yRot, dir - 90, 360)) * Math.min((1 - normal.y) * 60 * roughness, roughness);   
            }
        }
    }

    private void updatePilotPull() {
        prevPilotPower = pilotPower;
        OarState oarStarboard = oars.get(ShipSide.STARBOARD);
        OarState oarPort = oars.get(ShipSide.PORT);
        int timeStarboard = oarStarboard.rowTime;
        int timePort = oarPort.rowTime;
        if (timeStarboard > 20 && timePort > 20 && getAppropriateOarState(ShipSide.STARBOARD) && getAppropriateOarState(ShipSide.PORT) && getAppropriateRowProgress(ShipSide.STARBOARD) == getAppropriateRowProgress(ShipSide.PORT)) {
            if (pilotPower < 1) {
                pilotPower += 0.2F;
                if (pilotPower > 1) {
                    pilotPower = 1;
                }
            }
        } else if (pilotPower > 0) {
            pilotPower -= 0.16F;
            if (pilotPower < 0) {
                pilotPower = 0;
            }
        }
    }

    private void pullOarByWave(ShipSide side, Vector3d normal) {
        Vector3d oar = getOarVector(side);
        Vector3d of = new Vector3d(oar.x, 0, oar.z);
        Vector3d nf = new Vector3d(normal.x, 0, normal.z);
        float angle = nf.length() < 1e-12 ? 0 : (float) Math.acos(Math.max(Math.min(nf.dotProduct(of) / (nf.length() * of.length()), 1), -1));
        float align = MathUtils.linearTransformf(angle, 0, MathUtils.PI, 1, 0);
        float yaw = (float) Math.atan2(-normal.z, -normal.x) - (yRot - 90) * MathUtils.DEG_TO_RAD;
        float pitch = (float) Math.acos(Math.max(Math.min(normal.dotProduct(of), 1), -1));
        OarState oarSide = oars.get(side);
        float x = oarSide.oarXWavePull;
        oarSide.oarXWavePull = x + (MathHelper.clamp(yaw * align * (float) nf.length() * 2, -0.3F, 0.3F) - x) * 0.7F * (float) nf.length();
        float z = oarSide.oarZWavePull;
        oarSide.oarZWavePull = z + ((pitch - MathUtils.PI / 2) * (1 - align) * (getOarElevation(side) + 1) / 2 - z) * 0.4F;
    }

    private void updateClientOarProgress(ShipSide side) {
        OarState oarSide = oars.get(side);
        oarSide.prevRowProgress = oarSide.rowProgress;
        if (!isUserSteering()) {
            oarSide.rowProgress = getServerRowProgress(side);
        }
    }

    private void returnOarToResting(ShipSide side, float preApplyValue) {
        if (getRowForce(side) == 0) {
            float value = getAppropriateRowProgress(side);
            if (value != RESTING_ROW_PROGRESS) {
                float dist = RESTING_ROW_PROGRESS - value;
                if (dist < 0) {
                    dist += ROW_PROGRESS_PERIOD;
                }
                if (dist < 1e-4 && preApplyValue < RESTING_ROW_PROGRESS) {
                    value = RESTING_ROW_PROGRESS;
                } else {
                    float increment = dist * 0.085F;
                    if (increment > 0.005F) {
                        increment = 0.005F;
                    }
                    value += increment;
                }
            }
            setRowProgress(side, value);
        }
    }

    private void synchronizeOars() {
        if (getRowForce(synchronizer) == 0) {
            return;
        }
        ShipSide desynced = synchronizer.getOpposite();
        if (getRowForce(desynced) == 0) {
            return;
        }
        float target = getAppropriateRowProgress(synchronizer);
        float value = getAppropriateRowProgress(desynced);
        if (Math.abs(target - value) < 1e-6F) {
            return;
        }
        if (target < value) {
            synchronizer = desynced;
            return;
        }
        value += 0.0045F;
        if (value > target) {
            value = target;
        }
        setRowProgress(desynced, value);
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (isPassenger(passenger)) {
            Matrix mat = new Matrix();
            double pelvis = 0.75;
            mat.translate(0, pelvis - 1.5, 0);
            mat.rotate(rotation);
            mat.translate(0, 1.5, 0);
            mat.rotate(-yRot * MathUtils.DEG_TO_RAD, 0, 1, 0);
            mat.translate(0, getMountedYOffset() - pelvis, 0.2625);
            Vector3d point = mat.transform(Vector3d.ZERO);
            passenger.setPosition(posX + point.x, posY + point.y, posZ + point.z);
            passenger.yRot += rotationalVelocity;
            passenger.setRotationYawHead(passenger.getRotationYawHead() + rotationalVelocity);
            applyYawToEntity(passenger);
        }
    }

    @Override
    protected void applyYawToEntity(Entity entity) {
        entity.setRenderYawOffset(MathHelper.wrapDegrees(yRot - 180));
        float delta = MathHelper.wrapDegrees(entity.yRot - yRot - 180);
        float clamped = MathHelper.clamp(delta, -135, 135);
        entity.prevRotationYaw += clamped - delta;
        entity.yRot = entity.yRot + clamped - delta;
        entity.setRotationYawHead(entity.yRot);
    }

    private void tickLerp() {
        if (serverT <= 0 || isUserSteering()) {
            return;
        }
        double dx = posX - serverX, dy = posY - serverY, dz = posZ - serverZ;
        if (dx * dx + dy * dy + dz * dz > 100) {
            setPosition(serverX, serverY, serverZ);
            setRotation(boatYaw, boatPitch);
            serverT = 0;
            xOld = posX;
            yOld = posY;
            zOld = posZ;
            prevRotationYaw = yRot;
        } else {
            double x = posX + (serverX - posX) / serverT;
            double y = posY + (serverY - posY) / serverT;
            double z = posZ + (serverZ - posZ) / serverT;
            yRot = yRot + MathHelper.wrapDegrees(boatYaw - yRot) / serverT;
            xRot = xRot + (boatPitch - xRot) / serverT;
            serverT--;
            setPosition(x, y, z);
            setRotation(yRot, xRot);
        }
    }

    private void applyForces() {
        float buoyancy = 0;
        BlockPos pos = new BlockPos(this);
        BlockState blockAt = world.getBlockState(pos);
        BlockState blockAbove = world.getBlockState(pos.above());
        if (isWater(blockAt) && !isWater(blockAbove)) {
            float y = (float) pos.getY() + getLiquidHeight(blockAt, world, pos) + height;
            buoyancy = (y - (float) getBoundingBox().minY - 0.55F) / height;
            drag = 0.9875F;
            submergeTicks = 0;
        } else if (isWater(blockAt) && isWater(blockAbove)) {
            buoyancy = 1.25F;
            drag = 0.975F;
            submergeTicks++;
        } else if (blockAt.getMaterial() == Material.AIR) {
            BlockState blockBellow = world.getBlockState(pos.below());
            if (isWater(blockBellow)) {
                drag = 0.95F;
            } else if (blockBellow.getMaterial().blocksMovement()) {
                drag = 0.35F;
            } else {
                drag = 1;
            }
        }
        float motionRawAngle = (float) Math.atan2(motionZ, motionX);
        float motionAngle = MathHelper.wrapDegrees(motionRawAngle * MathUtils.RAD_TO_DEG + 180);
        float deviation = Math.abs(MathHelper.wrapDegrees(yRot - 90 - motionAngle)) / 180;
        drag *= MathUtils.linearTransformf(DEVIATION_DRAG.eval(deviation), 0, 1, 1, 0.25F);
        motionY -= 0.04;
        motionX *= drag;
        motionZ *= drag;
        rotationalVelocity *= drag * 0.95F;
        if (buoyancy > 0) {
            motionY += buoyancy * 0.06;
            motionY *= 0.75;
        }
    }

    private Vector3d applyRowForce() {
        if (getControllingPassenger() == null || submergeTicks >= 25) {
            return null;
        }
        Vector3d rowForce = new Vector3d(1, 0, 0);
        Vector3d motion = new Vector3d(0, 0, 0);
        Vector3d rotation = new Vector3d(0, 0, 0);
        float leftOarForce = getRowForce(ShipSide.STARBOARD);
        float rightOarForce = getRowForce(ShipSide.PORT);
        float forceFactor = 0.35F;
        if (leftOarForce > 0) {
            updateRowProgress(ShipSide.STARBOARD, leftOarForce * getOarWaterResistance(ShipSide.STARBOARD));
            if (canOarsApplyForce()) {
                leftOarForce *= getOarPeriodicForceApplyment(ShipSide.STARBOARD);
                Vector3d leftLever = new Vector3d(0, 0, leftOarForce);
                motion = motion.add(0, 0, leftOarForce * forceFactor);
                Vector3d cross = rowForce.cross(leftLever);
                rotation = rotation.add(cross.x, cross.y, cross.z);
            }
        }
        if (rightOarForce > 0) {
            updateRowProgress(ShipSide.PORT, rightOarForce * getOarWaterResistance(ShipSide.PORT));
            if (canOarsApplyForce()) {
                rightOarForce *= getOarPeriodicForceApplyment(ShipSide.PORT);
                Vector3d rightLever = new Vector3d(0, 0, rightOarForce);
                motion = motion.add(0, 0, rightOarForce * forceFactor);
                Vector3d cross = new Vector3d(-rowForce.x, -rowForce.y, -rowForce.z).cross(rightLever);
                rotation = rotation.add(cross.x, cross.y, cross.z);
            }
        }
        Vector3d currentMotion = new Vector3d(motionX, 0, motionZ);
        if (currentMotion.length() < 0.1 && rotation.x * rotation.x + rotation.y * rotation.y + rotation.z + rotation.z > 0) {
            motion = motion.scale(0.35);
            rotation = rotation.scale(1.6);
        }
        rotationalVelocity += rotation.y * 10;
        return motion;
    }

    private void updateMotion(Vector3d motion) {
        motion = motion.rotateYaw(-yRot * MathUtils.DEG_TO_RAD);
        motionX += motion.x;
        motionY += motion.y;
        motionZ += motion.z;
        setPaddleState(oars.get(ShipSide.STARBOARD).oarState, oars.get(ShipSide.PORT).oarState);
    }

    private float getOarPeriodicForceApplyment(ShipSide side) {
        return MathUtils.linearTransformf(getOarElevation(side), -1, 1, 0, 2);
    }

    private float getOarWaterResistance(ShipSide side) {
        float weight = MathUtils.linearTransformf(getOarElevation(side), -1, 1, 1, 0.25F);
        float velocity = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
        final float max = 0.5F;
        if (velocity > max) {
            velocity = max;
        }
        float t = velocity / max;
        return weight + (1 - weight) * t;
    }

    private float getOarElevation(ShipSide side) {
        return MathHelper.cos(getAppropriateRowProgress(side) * OAR_ROTATION_SCALE);
    }

    public boolean canOarsApplyForce() {
        return drag <= 1;
    }

    public float getRowForce(ShipSide side) {
        return 0.017F * oars.get(side).rowForce;
    }

    public void updateRowProgress(ShipSide side, float value) {
        setRowProgress(side, getAppropriateRowProgress(side) + value);
    }

    public float getPilotPower(float delta) {
        return prevPilotPower + (pilotPower - prevPilotPower) * delta;
    }

    public void updateRowForce(ShipSide side, boolean oarStroke, boolean prevOarStroke) {
        OarState oarSide = oars.get(side);
        float force = oarSide.rowForce;
        int time = oarSide.rowTime + 1;
        if (oarStroke || time < FORCE_SETTLE_DURATION) {
            if (!prevOarStroke && oarStroke && time >= FORCE_SETTLE_DURATION) {
                force = 1;
                time = 0;
            } else {
                force = Math.max(force - 0.05F, 0.55F);
            }
        } else {
            force = Math.max(force - 0.1F, 0);
        }
        oarSide.rowTime = time;
        oarSide.rowForce = force;
    }

    private void animateHullWaterInteraction() {
        double motionX = posX - xOld;
        double motionY = posY - yOld;
        double motionZ = posZ - zOld;
        double velocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
        if (velocity > 0.2625) {
            double vecX = Math.cos((yRot - 90) * MathUtils.DEG_TO_RAD);
            double vecZ = Math.sin((yRot - 90) * MathUtils.DEG_TO_RAD);
            for (int p = 0; p < 1 + velocity * 60; p++) {
                double near = rand.nextFloat() * 2 - 1;
                double far = (rand.nextInt(2) * 2 - 1) * 0.7;
                double splashX, splashZ;
                if (rand.nextBoolean()) {
                    splashX = posX - vecX * near * 0.8 + vecZ * far;
                    splashZ = posZ - vecZ * near * 0.8 - vecX * far;
                } else {
                    splashX = posX + vecX + vecZ * near * 0.7;
                    splashZ = posZ + vecZ - vecX * near * 0.7;
                }
                world.addParticle(ParticleTypes.WATER_SPLASH, splashX, Math.ceil(posY) - 0.125, splashZ, motionX, 0.01, motionZ);
            }
        }
    }

    private void animateOars() {
        double motionX = posX - xOld;
        double motionZ = posZ - zOld;
        double motion = Math.sqrt(motionX * motionX + motionZ * motionZ);
        animateOar(ShipSide.STARBOARD, motion);
        animateOar(ShipSide.PORT, motion);
    }

    private void animateOar(ShipSide side, double motion) {
        Vector3d oarlock = getOarlockPosition(side);
        Vector3d oarVector = getOarVector(side);
        Vector3d blade = oarlock.add(oarVector.x * OAR_LENGTH, oarVector.y * OAR_LENGTH, oarVector.z * OAR_LENGTH);
        RayTraceResult raytrace = world.rayTraceBlocks(new Vector3d(oarlock.x, oarlock.y, oarlock.z), blade, true);
        boolean bladeInAir = true;
        float amountOfBladeInAir = BLADE_LENGTH;
        if (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (motion > 0.175) {
                for (int p = 0; p < motion; p++) {
                    float x = MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.2F, 0.2F);
                    float y = MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.2F, 0.2F);
                    float z = MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.2F, 0.2F);
                    world.addParticle(ParticleTypes.WATER_SPLASH, raytrace.hitVec.x + x, raytrace.hitVec.y + y, raytrace.hitVec.z + z, motionX, 0.01, motionZ);
                }
            }
            float amountInAir = (float) oarlock.distanceTo(raytrace.hitVec);
            if (amountInAir < LOOM_LENGTH) {
                bladeInAir = false;
            } else {
                amountOfBladeInAir = OAR_LENGTH - amountInAir;
            }
        }
        if (bladeInAir && rand.nextFloat() < 0.4F) {
            for (int p = 0, count = (int) (1 + motion * 3); p < count; p++) {
                float point = LOOM_LENGTH + rand.nextFloat() * amountOfBladeInAir;
                float x = (float) (oarVector.x * point + MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.1F, 0.1F));
                float y = (float) (oarVector.y * point + MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.4F, -0.2F));
                float z = (float) (oarVector.z * point + MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.1F, 0.1F));
                world.addParticle(ParticleTypes.WATER_SPLASH, oarlock.x + x, oarlock.y + y, oarlock.z + z, 0, 1e-8, 0);
            }
        }
    }

    public Vector3d getLanternPosition() {
        Matrix mat = new Matrix();
        mat.translate(posX, posY + waveHeight, posZ);
        return this.getLocalLanternPosition(mat, 1);
    }

    public Vector3d getLocalLanternPosition(float t) {
        return getLocalLanternPosition(new Matrix(), t);
    }

    private Vector3d getLocalLanternPosition(Matrix mat, float t) {
        mat.rotate(getRotation(t));
        mat.rotate(-(prevRotationYaw + (yRot - prevRotationYaw) * t) * MathUtils.DEG_TO_RAD, 0, 1, 0);
        float roll = getRoll(t);
        if (roll != 0) {
            mat.rotate(roll * MathUtils.DEG_TO_RAD, 0, 0, 1);
        }
        mat.scale(-1, -1, 1);
        return mat.transform(new Vector3d(0.0, -0.9922452370881644, 1.6755452654813303));
    }

    private Vector3d getOarlockPosition(ShipSide side) {
        float dir = side == ShipSide.PORT ? 1 : -1;
        Matrix mat = new Matrix();
        mat.translate(posX, posY + waveHeight, posZ);
        mat.rotate(rotation);
        mat.rotate(-yRot * MathUtils.DEG_TO_RAD, 0, 1, 0);
        mat.translate(0.6 * dir, 1.15, -0.2);
        return mat.transform(Vector3d.ZERO);
    }

    private Vector3d getOarVector(ShipSide side) {
        float dir = side == ShipSide.PORT ? -1 : 1;
        float progress = getAppropriateRowProgress(side);
        float yaw = getOarRotationX(side, progress, 1) * dir - (yRot - 90) * MathUtils.DEG_TO_RAD;
        float pitch = getOarRotationZ(side, progress, 1) - MathUtils.PI / 2;
        float cosYaw = MathHelper.cos(-yaw);
        float sinYaw = MathHelper.sin(-yaw);
        float cosPitch = MathHelper.cos(-pitch);
        Mat4d mat = new Mat4d();
        mat.asQuaternion(rotation);
        return mat.transform(new Vector3d(-sinYaw * cosPitch, MathHelper.sin(pitch), cosYaw * cosPitch));
    }

    private void createSoundFX() {
        createOarSoundFX(ShipSide.STARBOARD);
        createOarSoundFX(ShipSide.PORT);
    }

    private void createOarSoundFX(ShipSide side) {
        OarState oarSide = oars.get(side);
        Vector3d oarlock = getOarlockPosition(side);
        Vector3d oarVector = getOarVector(side);
        Vector3d blade = oarlock.add(oarVector.x * OAR_LENGTH, oarVector.y * OAR_LENGTH, oarVector.z * OAR_LENGTH);
        RayTraceResult raytrace = world.rayTraceBlocks(new Vector3d(oarlock.x, oarlock.y, oarlock.z), blade, true);
        boolean bladeInAir = true;
        if (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK) {
            float amountInAir = (float) oarlock.distanceTo(raytrace.hitVec);
            if (amountInAir < LOOM_LENGTH) {
                bladeInAir = false;
                float force = oarSide.rowForce;
                boolean start = force == 1;
                if (oarSide.oarInAir || start) {
                    float volume = force * 0.8F + 0.2F;
                    SoundEvent sound = (start ? SOUND_ROW_START : SOUND_ROW).get(side);
                    world.playLocalSound(null, raytrace.hitVec.x, raytrace.hitVec.y, raytrace.hitVec.z, sound, SoundCategory.NEUTRAL, volume, 0.8F + rand.nextFloat() * 0.3F);
                }
            }
        }
        oarSide.oarInAir = bladeInAir;
    }

    @Override
    public boolean isPushedByWater() {
        return canPassengerSteer();
    }

    @Override
    public boolean handleWaterMovement() {
        double mX = motionX, mZ = motionZ;
        if (world.handleMaterialAcceleration(getBoundingBox(), Material.WATER, this)) {
            if (mX != motionX && mZ != motionZ && canPassengerSteer()) {
                double aX = motionX - mX, aZ = motionZ - mZ;
                double dir = Math.atan2(aZ, aX) * MathUtils.RAD_TO_DEG;
                double speed = Math.sqrt(motionX * motionX + motionZ * motionZ);
                rotationalVelocity += (MathHelper.clamp(MathUtils.modularDelta(yRot, dir - 90, 360) * Math.min(speed * 1.1, 0.3), -12, 12) - rotationalVelocity) * 0.75;   
            }
            if (!inWater) {
                float volume = MathHelper.sqrt(motionX * motionX * 0.2 + motionY * motionY + motionZ * motionZ * 0.2) * 0.2F;
                if (volume > 0.15) {
                    if (volume > 1) {
                        volume = 1;
                    }
                    playSound(getSplashSound(), volume, 1 + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
                    float min = MathHelper.floor(getBoundingBox().minY);
                    for (int i = 0; i < 1 + width * 20; i++) {
                        float x = (rand.nextFloat() * 2 - 1) * width;
                        float z = (rand.nextFloat() * 2 - 1) * width;
                        world.addParticle(ParticleTypes.WATER_BUBBLE, posX + x, min + 1, posZ + z, motionX, motionY - rand.nextFloat() * 0.2F, motionZ);
                    }
                    for (int i = 0; i < 1 + width * 20; i++) {
                        float x = (rand.nextFloat() * 2 - 1) * width;
                        float z = (rand.nextFloat() * 2 - 1) * width;
                        world.addParticle(ParticleTypes.WATER_SPLASH, posX + x, min + 1, posZ + z, motionX, motionY, motionZ);
                    }
                }
            }
            fallDistance = 0;
            inWater = true;
            clearFire();
        } else {
            inWater = false;
        }
        return inWater;
    }

    public void setRowProgress(ShipSide side, float progress) {
        while (progress > ROW_PROGRESS_PERIOD) {
            progress -= ROW_PROGRESS_PERIOD;
        }
        while (progress < 0) {
            progress += ROW_PROGRESS_PERIOD;
        }
        if (isUserSteering()) {
             oars.get(side).rowProgress = progress;
        } else {
            entityData.set(ROW_PROGRESS.get(side), progress);
        }
    }

    public float getRowProgress(ShipSide side, float delta) {
        OarState oarSide = oars.get(side);
        float prevProgress = oarSide.prevRowProgress;
        float progress = oarSide.rowProgress;
        return delta * (MathUtils.mod(progress - prevProgress + ROW_PROGRESS_PERIOD / 2, ROW_PROGRESS_PERIOD) - ROW_PROGRESS_PERIOD / 2) + prevProgress;
    }

    public float getServerRowProgress(ShipSide side) {
        return entityData.get(ROW_PROGRESS.get(side));
    }

    public float getAppropriateRowProgress(ShipSide side) {
        return isUserSteering() ? oars.get(side).rowProgress : getServerRowProgress(side);
    }

    public boolean getAppropriateOarState(ShipSide side) {
        return isUserSteering() ? oars.get(side).oarState : getPaddleState(side.ordinal());
    }

    private boolean isUserSteering() {
        Entity entity = getControllingPassenger();
        return entity instanceof PlayerEntity && ((PlayerEntity) entity).isUser();
    }

    public float getOarRotationX(ShipSide side, float theta, float delta) {
        OarState oarSide = oars.get(side);
        return MathHelper.sin(theta * EntityWeedwoodRowboat.OAR_ROTATION_SCALE) * 0.6F + oarSide.prevOarXWavePull + (oarSide.oarXWavePull - oarSide.prevOarXWavePull) * delta;
    }

    public float getOarRotationY(ShipSide side, float theta) {
        float angle = MathUtils.linearTransformf(MathHelper.sin(theta * EntityWeedwoodRowboat.OAR_ROTATION_SCALE + MathUtils.PI / 2), -1, 1, MathUtils.PI / 2, 0);
        if (side == ShipSide.PORT) {
            angle = MathUtils.PI - angle;
        }
        return angle;
    }

    public float getOarRotationZ(ShipSide side, float theta, float delta) {
        OarState oarSide = oars.get(side);
        float angle = MathHelper.cos(theta * EntityWeedwoodRowboat.OAR_ROTATION_SCALE) * 0.45F - MathUtils.PI / 2.5F + oarSide.prevOarZWavePull + (oarSide.oarZWavePull - oarSide.prevOarZWavePull) * delta;
        if (side == ShipSide.PORT) {
            angle = -angle;
        }
        return angle;
    }

    public Quat getRotation(float delta) {
        Quat rot = new Quat(prevRotation);
        rot.interpolate(rotation, delta);
        return rot;
    }

    public float getRoll(float delta) {
        float timeSinceHit = getTimeSinceHit() - delta;
        float damageTaken = Math.max(getDamageTaken() - delta, 0.0F);
        if (timeSinceHit > 0) {
            return MathHelper.sin(timeSinceHit) * timeSinceHit * damageTaken / 10 * getForwardDirection();
        }
        return 0;
    }

    public double getWaveHeight(float delta) {
        return delta == 1 ? waveHeight : prevWaveHeight + (waveHeight - prevWaveHeight) * delta;
    }

    @Override
    public void save(CompoundNBT compound) {
        compound.putBoolean("isTarred", isTarred());
        compound.putBoolean("hasLantern", hasLantern());
    }

    @Override
    public void readEntityFromNBT(CompoundNBT compound) {
        setIsTarred(compound.getBoolean("isTarred"));
        setHasLantern(compound.getBoolean("hasLantern"));
    }

    @Override
    public void writeSpawnData(PacketBuffer buf) {}

    @Override
    public void readSpawnData(PacketBuffer buf) {
        prevRotationYaw = yRot;
    }

    public static boolean isTarred(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            return tag.getCompoundTag("attributes").getBoolean("isTarred");
        }
        return false;
    }

    private static <T> DataParameter<T> defineId(DataSerializer<T> serializer) {
        return EntityDataManager.defineId(EntityWeedwoodRowboat.class, serializer);
    }

    private static boolean isWater(BlockState state) {
        return state.getMaterial() == Material.WATER;
    }

    private static float getLiquidHeight(BlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        if (block instanceof IFluidBlock) {
            return ((IFluidBlock) block).getFilledPercentage(world, pos);
        }
        if (block instanceof BlockLiquid) {
            return BlockLiquid.getBlockLiquidHeight(state, world, pos);
        }
        return 1;
    }

    // Inherited methods not needed

    @Override
    public float getWaterLevelAbove() {
        return 0.0F;
    }

    @Override
    public float getBoatGlide() {
        return 0.0F;
    }

    @Override
    public void setBoatType(Type boatType) {
    }

    @Override
    public Type getBoatType() {
        return Type.OAK;
    }

    @Override
    public float getRowingTime(int oar, float limbSwing) {
        return 0.0F;
    }

    @SubscribeEvent
    public static void onLivingAttacked(LivingAttackEvent event) {
        Entity ridingEntity = event.getEntityLiving().getRidingEntity();
    	if (ridingEntity instanceof EntityWeedwoodRowboat && (event.getSource() instanceof EntityDamageSource == false || event.getSource().getTrueSource() != null)) {
            Vector3d location = event.getSource().getDamageLocation();
            Entity attacker = event.getSource().getImmediateSource();
            if (location != null && location.y + (attacker != null ? attacker.getEyeHeight() : 0) < ridingEntity.getY() + ridingEntity.height / 2) {
                //Cancel any damage dealt from below the boat
                event.setCanceled(true);
            }
        }
    }
}
