package thebetweenlands.client.handler;


import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ArrowPredictionRenderer {
    private static class EntityArrowSilent extends EntityArrow {
        public EntityArrowSilent(World world, LivingEntity entity) {
            super(world, entity);
        }

        @Override
        public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
            float f = MathHelper.sqrt(x * x + y * y + z * z);
            x = x / (double)f;
            y = y / (double)f;
            z = z / (double)f;
            x = x * (double)velocity;
            y = y * (double)velocity;
            z = z * (double)velocity;
            this.motionX = x;
            this.motionY = y;
            this.motionZ = z;
            float f1 = MathHelper.sqrt(x * x + z * z);
            this.yRot = (float)(MathHelper.atan2(x, z) * 180D / Math.PI);
            this.xRot = (float)(MathHelper.atan2(y, (double)f1) * 180D / Math.PI);
            this.prevRotationYaw = this.yRot;
            this.prevRotationPitch = this.xRot;
        }

        @Override
        protected ItemStack getArrowStack() {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean isInWater() {
            return false;
        }

        @Override
        public boolean handleWaterMovement() {
            if (this.world.handleMaterialAcceleration(this.getBoundingBox().expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.WATER, this)) {
                this.fallDistance = 0.0F;
                this.inWater = true;
            } else {
                this.inWater = false;
            }
            return this.inWater;
        }
    }

    public static void setRandomYawPitch() {
        randomYawPitchSet = false;
    }

    private static float randYaw = 0.0F;
    private static float randPitch = 0.0F;
    private static boolean randomYawPitchSet = false;
    private static float lastQuality = 0.0F;

    public static void render(float quality) {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player.getActiveHand() == null) return;
        ItemStack stack = player.getItemInHand(player.getActiveHand());
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemBow)) {
            randomYawPitchSet = false;
            return;
        }
        if(lastQuality != quality || !randomYawPitchSet) {
            randomYawPitchSet = true;
            lastQuality = quality;
            float maxOffset = 3.0F;
            randYaw = (maxOffset / 2.0F - Minecraft.getInstance().level.random.nextFloat() * maxOffset * 2.0F) * (1.0F - quality);
            randPitch = (maxOffset / 2.0F - Minecraft.getInstance().level.random.nextFloat() * maxOffset * 2.0F) * (1.0F - quality);
        }
        int maxDur = stack.getUseDuration() - player.getItemInUseCount();
        float strength = (float)maxDur / 20.0F;
        strength = (strength * strength + strength * 2.0F) / 3.0F;
        if(strength < 0.1f || strength > 1.0f) {
            strength = 1.0f;
        }
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();
        float pYaw = player.yRot;
        float pPitch = player.xRot;
        player.getX() = Minecraft.getInstance().getRenderManager().renderPosX;
        player.getY() = Minecraft.getInstance().getRenderManager().renderPosY;
        player.getZ() = Minecraft.getInstance().getRenderManager().renderPosZ;
        player.yRot += randYaw;
        player.xRot += randPitch;
        //TODO: Mabye find a better way to simulate an arrow
        EntityArrowSilent ea = new EntityArrowSilent(Minecraft.getInstance().world, player);
        ea.shoot(player, player.xRot, player.yRot, 0.0F, strength * 3.0f, 1.0f);
        player.getX() = px;
        player.getY() = py;
        player.getZ() = pz;
        player.yRot = pYaw;
        player.xRot = pPitch;
        double rx = Minecraft.getInstance().getRenderManager().renderPosX;
        double ry = Minecraft.getInstance().getRenderManager().renderPosY;
        double rz = Minecraft.getInstance().getRenderManager().renderPosZ;
        double startX = rx - (double)(MathHelper.cos(player.yRot / 180.0F * (float)Math.PI) * 0.46F);
        double startY = ry - 0.10000000149011612D + 1.5D;
        double startZ = rz - (double)(MathHelper.sin(player.yRot / 180.0F * (float)Math.PI) * 0.46F);
        double lastX = startX, lastY = startY, lastZ = startZ;

        float alpha = quality / 1.3F;

        GlStateManager.pushMatrix();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.glLineWidth(alpha * 3.5F);
        GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);

        boolean drawing = true;
        GlStateManager.glBegin(GL11.GL_LINES);
        for(int i = 0; i < 1000; i++) {
            ea.tick();

            GL11.glVertex3d(lastX - rx, lastY - ry, lastZ - rz);
            GL11.glVertex3d(ea.getX() - rx, ea.getY() - ry, ea.getZ() - rz);
            lastX = ea.getX();
            lastY = ea.getY();
            lastZ = ea.getZ();

            RayTraceResult collisionPoint = getCollision(ea);
            if(collisionPoint != null) {
                if(collisionPoint.typeOfHit == RayTraceResult.Type.BLOCK) {
                    drawing = false;
                    GL11.glVertex3d(ea.getX() - rx, ea.getY() - ry, ea.getZ() - rz);
                    GL11.glVertex3d(collisionPoint.hitVec.x-rx, collisionPoint.hitVec.y-ry, collisionPoint.hitVec.z-rz);
                    GL11.glEnd();
                    GL11.glLineWidth(2.0f);
                    GlStateManager.color(1.0f, 0.0f, 0.0f, quality);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glBegin(GL11.GL_LINES);
                    if(collisionPoint.sideHit == Direction.DOWN) {
                        GL11.glVertex3d(collisionPoint.hitVec.x-0.1-rx, collisionPoint.hitVec.y-0.001-ry, collisionPoint.hitVec.z-0.1-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x+0.1-rx, collisionPoint.hitVec.y-0.001-ry, collisionPoint.hitVec.z+0.1-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x+0.1-rx, collisionPoint.hitVec.y-0.001-ry, collisionPoint.hitVec.z-0.1-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x-0.1-rx, collisionPoint.hitVec.y-0.001-ry, collisionPoint.hitVec.z+0.1-rz);
                    } else if(collisionPoint.sideHit == Direction.UP) {
                        GL11.glVertex3d(collisionPoint.hitVec.x-0.1-rx, collisionPoint.hitVec.y+0.001-ry, collisionPoint.hitVec.z-0.1-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x+0.1-rx, collisionPoint.hitVec.y+0.001-ry, collisionPoint.hitVec.z+0.1-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x+0.1-rx, collisionPoint.hitVec.y+0.001-ry, collisionPoint.hitVec.z-0.1-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x-0.1-rx, collisionPoint.hitVec.y+0.001-ry, collisionPoint.hitVec.z+0.1-rz);
                    } else if(collisionPoint.sideHit == Direction.NORTH) {
                        GL11.glVertex3d(collisionPoint.hitVec.x-0.1-rx, collisionPoint.hitVec.y-0.1-ry, collisionPoint.hitVec.z-0.001-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x+0.1-rx, collisionPoint.hitVec.y+0.1-ry, collisionPoint.hitVec.z-0.001-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x+0.1-rx, collisionPoint.hitVec.y-0.1-ry, collisionPoint.hitVec.z-0.001-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x-0.1-rx, collisionPoint.hitVec.y+0.1-ry, collisionPoint.hitVec.z-0.001-rz);
                    } else if(collisionPoint.sideHit == Direction.SOUTH) {
                        GL11.glVertex3d(collisionPoint.hitVec.x-0.1-rx, collisionPoint.hitVec.y-0.1-ry, collisionPoint.hitVec.z+0.001-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x+0.1-rx, collisionPoint.hitVec.y+0.1-ry, collisionPoint.hitVec.z+0.001-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x+0.1-rx, collisionPoint.hitVec.y-0.1-ry, collisionPoint.hitVec.z+0.001-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x-0.1-rx, collisionPoint.hitVec.y+0.1-ry, collisionPoint.hitVec.z+0.001-rz);
                    } else if(collisionPoint.sideHit == Direction.WEST) {
                        GL11.glVertex3d(collisionPoint.hitVec.x-0.001-rx, collisionPoint.hitVec.y-0.1-ry, collisionPoint.hitVec.z-0.1-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x-0.001-rx, collisionPoint.hitVec.y+0.1-ry, collisionPoint.hitVec.z+0.1-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x-0.001-rx, collisionPoint.hitVec.y-0.1-ry, collisionPoint.hitVec.z+0.1-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x-0.001-rx, collisionPoint.hitVec.y+0.1-ry, collisionPoint.hitVec.z-0.1-rz);
                    } else if(collisionPoint.sideHit == Direction.EAST) {
                        GL11.glVertex3d(collisionPoint.hitVec.x+0.001-rx, collisionPoint.hitVec.y-0.1-ry, collisionPoint.hitVec.z-0.1-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x+0.001-rx, collisionPoint.hitVec.y+0.1-ry, collisionPoint.hitVec.z+0.1-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x+0.001-rx, collisionPoint.hitVec.y-0.1-ry, collisionPoint.hitVec.z+0.1-rz);
                        GL11.glVertex3d(collisionPoint.hitVec.x+0.001-rx, collisionPoint.hitVec.y+0.1-ry, collisionPoint.hitVec.z-0.1-rz);
                    }
                    GlStateManager.glEnd();
                } else if(collisionPoint.typeOfHit == RayTraceResult.Type.ENTITY) {
                    Entity hitEntity = collisionPoint.entityHit;
                    if(hitEntity != null) {

                    }
                }
                break;
            }
        }
        if(drawing) GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    private static RayTraceResult getCollision(EntityArrowSilent ea) {
        Vector3d start = new Vector3d(ea.getX(), ea.getY(), ea.getZ());
        Vector3d dest = new Vector3d(ea.getX() + ea.motionX, ea.getY() + ea.motionY, ea.getZ() + ea.motionZ);
        RayTraceResult hit = Minecraft.getInstance().world.rayTraceBlocks(start, dest, false, true, false);
        start = new Vector3d(ea.getX(), ea.getY(), ea.getZ());
        dest = new Vector3d(ea.getX() + ea.motionX, ea.getY() + ea.motionY, ea.getZ() + ea.motionZ);
        if (hit != null) {
            dest = new Vector3d(hit.hitVec.x, hit.hitVec.y, hit.hitVec.z);
        }
        Entity collidedEntity = null;
        List entityList = Minecraft.getInstance().world.getEntitiesWithinAABBExcludingEntity(ea, ea.getBoundingBox().expand(ea.motionX, ea.motionY, ea.motionZ).inflate(1.0D, 1.0D, 1.0D));
        double lastDistance = 0.0D;
        for (int c = 0; c < entityList.size(); ++c) {
            Entity currentEntity = (Entity)entityList.get(c);
            if (currentEntity.canBeCollidedWith() && (currentEntity != Minecraft.getInstance().player)) {
                AxisAlignedBB entityBoundingBox = currentEntity.getBoundingBox().inflate((double)0.3F, (double)0.3F, (double)0.3F);
                RayTraceResult collision = entityBoundingBox.calculateIntercept(start, dest);
                if (collision != null) {
                    double currentDistance = start.distanceTo(collision.hitVec);

                    if (currentDistance < lastDistance || lastDistance == 0.0D) {
                        collidedEntity = currentEntity;
                        lastDistance = currentDistance;
                    }
                }
            }
        }
        if (collidedEntity != null) {
            hit = new RayTraceResult(collidedEntity);
        }
        return hit;
    }
}
