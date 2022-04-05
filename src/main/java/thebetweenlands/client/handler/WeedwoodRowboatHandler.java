package thebetweenlands.client.handler;

import java.util.ArrayDeque;
import java.util.concurrent.FutureTask;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.Matrix;

public final class WeedwoodRowboatHandler {
    public static final WeedwoodRowboatHandler INSTANCE = new WeedwoodRowboatHandler();

    private static final Minecraft MC = Minecraft.getInstance();

    private boolean isPlayerInRowboat;

    private MouseHelper lastMouseHelper;

    private View view = View.ROWBOAT;
    private View changedTo = null;

    private WeedwoodRowboatHandler() {}

    public void init() {
        try {
            MC.scheduledTasks = new ArrayDeque<FutureTask<?>>() {
                @Override
                public boolean isEmpty() {
                    if (super.isEmpty()) {
                        onMacgyveredGameLoop();
                        return true;
                    }
                    return false;
                }
            };
        } catch (Exception e) {
            throw new RuntimeException("Any problem can be solved with a little ingenuity, he said...", e);
        }
    }

    public void onPilotEnterWeedwoodRowboat(Entity pilot) {
        if (pilot == MC.player) {
            if (MC.gameSettings.thirdPersonView > 0)
                changedTo = View.ROWBOAT;
            if ((changedTo != null && changedTo == View.ROWBOAT) || BetweenlandsConfig.GENERAL.rowboatView)
                enterRowboatPerspective();
            else
                leaveRowboatPerspective();
        }
    }

    public void onPilotExitWeedwoodRowboat(EntityWeedwoodRowboat rowboat, Entity pilot) {
        if (pilot == MC.player) {
            double dx = rowboat.getX() - pilot.getX();
            double dy = rowboat.getY() + rowboat.height - (pilot.getY() + pilot.getEyeHeight());
            double dz = rowboat.getZ() - pilot.getZ();
            double h = MathHelper.sqrt(dx * dx + dz * dz);
            pilot.xRot = (float) -Math.toDegrees(MathHelper.atan2(dy, h));
            float yaw = (float) Math.toDegrees(MathHelper.atan2(dz, dx)) - 90;
            pilot.yRot = yaw;
            pilot.setRotationYawHead(yaw);
            pilot.setRenderYawOffset(yaw);
            leaveRowboatPerspective();
            if (changedTo != null && changedTo == View.ROWBOAT)
                MC.gameSettings.thirdPersonView = 1;
            changedTo = null;
        }
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Entity entity = MC.getCameraEntity();
            if (entity instanceof RowboatCam && isPlayerInRowboat && (!MC.inGameHasFocus || !Display.isActive())) {
                ((RowboatCam) entity).update(MC.player.getRidingEntity(), event.renderTickTime);
            }
        }
    }

    private Entity prevRenderViewEntity = null;
    
    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent event) {
    	if(isPlayerInRowboat) {
	        if (event instanceof RenderGameOverlayEvent.Pre && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
	        	//Set render view entity to player during GUI overlay rendering so that HUD renders
	            this.prevRenderViewEntity = MC.getCameraEntity();
	            MC.setRenderViewEntity(MC.player);
	            GuiIngameForge.renderFood = true;
	        } else if (event instanceof RenderGameOverlayEvent.Post && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
	        	if(MC.getCameraEntity() == MC.player || this.prevRenderViewEntity instanceof RowboatCam) {
	        		MC.setRenderViewEntity(this.prevRenderViewEntity);
	        	}
	        }
    	}
    }

    @SubscribeEvent
    public void onMouseInput(MouseEvent event) {
        Entity entity = MC.getCameraEntity();
        if (entity instanceof RowboatCam) {
            RowboatCam cam = (RowboatCam) entity;
            cam.dolly = MathHelper.clamp(cam.dolly - Math.signum(event.getDwheel()) * (cam.dolly - 1) * 0.1, 1, 10);
            event.setCanceled(true);
            int key = event.getButton() - 100;
            KeyBinding.setKeyBindState(key, Mouse.getEventButtonState());
            if (Mouse.getEventButtonState()) {
                KeyBinding.onTick(key);
            }
        }
    }

    @SubscribeEvent
    public void onTickAfterKeyboard(InputEvent.KeyInputEvent event) {
        if (isPlayerInRowboat && Keyboard.getEventKeyState()) {
            int press = MC.gameSettings.keyBindTogglePerspective.pressTime;
            if (press > 0) {
                if (view == View.FIRST_PERSON) {
                    enterRowboatPerspective();
                    view = View.ROWBOAT;
                    BetweenlandsConfig.GENERAL.rowboatView = true;
                } else {
                    leaveRowboatPerspective();
                    view = View.FIRST_PERSON;
                    BetweenlandsConfig.GENERAL.rowboatView = false;
                }
                MC.gameSettings.thirdPersonView = 2;
                changedTo = view;
            }
        }
    }

    @SubscribeEvent
    public void onHandRender(RenderHandEvent event) {
        if (MC.getCameraEntity() instanceof RowboatCam) {
            event.setCanceled(true);
        }
    }

    private void enterRowboatPerspective() {
        Entity entity = MC.player.getRidingEntity();
        MC.setRenderViewEntity(new RowboatCam(MC.world, entity == null ? 0 : entity.yRot, 30));
        lastMouseHelper = MC.mouseHelper;
        MC.mouseHelper = new RowboatCamUpdater();
        view = View.ROWBOAT;
        MC.gameSettings.thirdPersonView = 0;
    }

    private void leaveRowboatPerspective() {
        MC.setRenderViewEntity(null);
        if (MC.mouseHelper instanceof RowboatCamUpdater) {
            if (lastMouseHelper == null) {
                MC.mouseHelper = new MouseHelper();
            } else {
                MC.mouseHelper = lastMouseHelper;
            }
        }
        lastMouseHelper = null;
        GuiIngameForge.renderCrosshairs = true;
        view = View.FIRST_PERSON;
        MC.gameSettings.thirdPersonView = 0;
    }

    // Do this so that when the player enters the rowboat they are rotated correctly after a SetPassengers packet is recieved
    private void onMacgyveredGameLoop() {
        EntityPlayerSP player = MC.player;
        if (player == null) {
            isPlayerInRowboat = false;
            return;
        }
        Entity riding = player.getRidingEntity();
        if (riding instanceof EntityWeedwoodRowboat && riding.getControllingPassenger() == player) {
            if (!isPlayerInRowboat) {
                player.prevRotationPitch = player.xRot = 0;
                player.prevRotationYawHead = player.rotationYawHead = player.prevRotationYaw = player.yRot = MathHelper.wrapDegrees(riding.yRot - 180);
                riding.updatePassenger(player);
                player.prevRenderYawOffset = player.renderYawOffset;
                player.xOld = player.lastTickPosX = player.getX();
                player.yOld = player.lastTickPosY = player.getY();
                player.zOld = player.lastTickPosZ = player.getZ();
                isPlayerInRowboat = true;
            }
        } else {
            isPlayerInRowboat = false;
        }
    }

    private enum View {
        FIRST_PERSON,
        ROWBOAT;
    }

    private class RowboatCamUpdater extends MouseHelper {
        @Override
        public void mouseXYChange() {
            boolean reset = true;
            Entity entity = MC.getCameraEntity();
            if (entity instanceof RowboatCam) {
                RowboatCam cam = (RowboatCam) entity;
                PlayerEntity player = MC.player;
                Entity riding = player.getRidingEntity();
                if (riding instanceof EntityWeedwoodRowboat) {
                    int deltaX = Mouse.getDX();
                    int deltaY = Mouse.getDY();
                    cam.prevRotationYaw = cam.yRot = MathHelper.wrapDegrees(cam.yRot + deltaX * 0.15F);
                    cam.prevRotationPitch = cam.xRot = MathHelper.clamp(cam.xRot - deltaY * 0.15F, 0, 90);
                    cam.update(riding, MC.getRenderPartialTicks());
                    GuiIngameForge.renderCrosshairs = false;
                    reset = false;
                }
            }
            if (reset) {
                leaveRowboatPerspective();
            }
        }
    }

    private class RowboatCam extends Entity {
        private final Matrix mat = new Matrix();

        public double dolly = 5;

        public RowboatCam(World world, float yaw, float pitch) {
            super(world);
            this.prevRotationYaw = yRot = yaw;
            this.prevRotationPitch = xRot = pitch;
        }

        @Override
        public float getEyeHeight() {
            return 0;
        }

        @Override
        protected void defineSynchedData() {}

        public void update(Entity rowboat, float delta) {
            double x = rowboat.lastTickPosX + (rowboat.getX() - rowboat.lastTickPosX) * delta;
            double y = rowboat.lastTickPosY + (rowboat.getY() - rowboat.lastTickPosY) * delta;
            double z = rowboat.lastTickPosZ + (rowboat.getZ() - rowboat.lastTickPosZ) * delta;
            final double offsetY = 1.12;
            mat.setIdentity();
            mat.translate(x, y + offsetY, z);
            mat.rotate(-yRot * MathUtils.DEG_TO_RAD, 0, 1, 0);
            mat.rotate(xRot * MathUtils.DEG_TO_RAD, 1, 0, 0);
            mat.translate(0, 0, -getDistance(world, x, y + offsetY, z, yRot, xRot));
            Vector3d point = mat.transform(Vector3d.ZERO);
            lastTickPosX = xOld = posX = point.x;
            lastTickPosY = yOld = posY = point.y;
            lastTickPosZ = zOld = posZ = point.z;
        }

        private double getDistance(World world, double x, double y, double z, float yaw, float pitch) {
            double extent = dolly;
            float cosPitch = MathHelper.cos(pitch * MathUtils.DEG_TO_RAD);
            double extentX = -MathHelper.sin(yaw * MathUtils.DEG_TO_RAD) * cosPitch * extent;
            double extentZ = MathHelper.cos(yaw * MathUtils.DEG_TO_RAD) * cosPitch * extent;
            double extentY = -MathHelper.sin(pitch * MathUtils.DEG_TO_RAD) * extent;
            for (int zyx = 0; zyx < 8; zyx++) {
                float dx = ((zyx & 1) * 2 - 1) * 0.1F;
                float dy = ((zyx >> 1 & 1) * 2 - 1) * 0.1F;
                float dz = ((zyx >> 2 & 1) * 2 - 1) * 0.1F;
                RayTraceResult vector = world.rayTraceBlocks(new Vector3d(x + dx, y + dy, z + dz), new Vector3d(x - extentX + dx, y - extentY + dy, z - extentZ + dz), false, true, false);
                if (vector != null) {
                    double distance = vector.hitVec.distanceTo(new Vector3d(x, y, z));
                    if (distance < extent) {
                        extent = distance;
                    }
                }
            }
            return extent;
        }

        @Override
        public void load(CompoundNBT compound) {}

        @Override
        public void save(CompoundNBT compound) {}
    }
}
