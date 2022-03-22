package thebetweenlands.client.render.model.entity;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntitySporeling;

@OnlyIn(Dist.CLIENT)
public class ModelSporeling extends Model {
	
    ModelRenderer torso;
    ModelRenderer legleft1;
    ModelRenderer legright1;
    ModelRenderer legleft2;
    ModelRenderer legright2;
    ModelRenderer roots;
    ModelRenderer armleft;
    ModelRenderer armright;
    ModelRenderer head1;
    ModelRenderer head2;
    ModelRenderer eyeleft;
    ModelRenderer eyeright;

    public ModelSporeling() {
    	
        this.texWidth = 64;
        this.texHeight = 32;

        torso = new ModelRenderer(this, 0, 0);
        torso.addBox(-1.5F, 0F, -1.5F, 3, 3, 3);
        torso.setPos(0F, 18F, 0F);
        setRotation(torso, 0.1115358F, 0F, 0F);
        legleft1 = new ModelRenderer(this, 0, 7);
        legleft1.addBox(0.04F, 2.2F, -1F, 1, 2, 2);
        legleft1.setPos(0F, 18F, 0F);
        setRotation(legleft1, -0.0371705F, 0F, -0.1487144F);
        legright1 = new ModelRenderer(this, 7, 7);
        legright1.addBox(-1F, 2.2F, -1F, 1, 2, 2);
        legright1.setPos(0F, 18F, 0F);
        setRotation(legright1, -0.0371705F, 0F, 0.1487195F);
        legleft2 = new ModelRenderer(this, 0, 12);
        legleft2.addBox(-0.35F, -0.5F, -1F, 1, 3, 1);
        legleft2.setPos(1F, 22F, 0F);
        setRotation(legleft2, 0.2602503F, 0F, 0F);
        legright2 = new ModelRenderer(this, 5, 12);
        legright2.addBox(-0.65F, -0.5F, -1F, 1, 3, 1);
        legright2.setPos(-1F, 22F, 0F);
        setRotation(legright2, 0.2602503F, 0F, 0F);
        roots = new ModelRenderer(this, 0, 17);
        roots.addBox(-1.5F, 3F, -0.5F, 3, 2, 2);
        roots.setPos(0F, 18F, 0F);
        setRotation(roots, 0.0743654F, 0F, 0F);
        armleft = new ModelRenderer(this, 0, 22);
        armleft.addBox(0F, -0.5F, -0.5F, 1, 3, 1);
        armleft.setPos(1F, 20F, 0F);
        setRotation(armleft, -0.1487144F, -0.0743572F, -0.5948578F);
        armright = new ModelRenderer(this, 5, 22);
        armright.addBox(-1F, -0.5F, -0.5F, 1, 3, 1);
        armright.setPos(-1F, 20F, 0F);
        setRotation(armright, -0.1487144F, 0.074351F, 0.5948606F);
        head1 = new ModelRenderer(this, 20, 0);
        head1.addBox(-2.5F, -2F, -2.5F, 5, 2, 5);
        head1.setPos(0F, 18F, 0F);
        setRotation(head1, -0.5204929F, 0F, 0F);
        head2 = new ModelRenderer(this, 20, 8);
        head2.addBox(-2F, -3F, -2F, 4, 1, 4);
        head2.setPos(0F, 18F, 0F);
        setRotation(head2, -0.5204921F, 0F, 0F);
        eyeleft = new ModelRenderer(this, 20, 14);
        eyeleft.addBox(0.5F, -2F, -2.2F, 1, 1, 1);
        eyeleft.setPos(0F, 18F, 0F);
        setRotation(eyeleft, -0.1115358F, 0F, 0F);
        eyeright = new ModelRenderer(this, 25, 14);
        eyeright.addBox(-1.5F, -2F, -2.2F, 1, 1, 1);
        eyeright.setPos(0F, 18F, 0F);
        setRotation(eyeright, -0.1115358F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float prevLimbSwing, float entityTickTime, float yRot, float xRot, float unitPixel) {
        super.render(entity, limbSwing, prevLimbSwing, entityTickTime, yRot, xRot, unitPixel);
        setRotationAngles(limbSwing, prevLimbSwing, entityTickTime, yRot, xRot, unitPixel, entity);
        EntitySporeling sporeling = (EntitySporeling) entity;
        
        GlStateManager.pushMatrix();
        if(sporeling.isRiding()) {
        	GlStateManager.translate(0, 0.15F, 0);
        }
        
        torso.render(unitPixel);
        legleft1.render(unitPixel);
        legright1.render(unitPixel);
        legleft2.render(unitPixel);
        legright2.render(unitPixel);
        roots.render(unitPixel);
        armleft.render(unitPixel);
        armright.render(unitPixel);
        if (sporeling.getIsFalling()) {
            GlStateManager.translate(0F, 0.125F, -1.125F);
            GlStateManager.scale(2F, 1F, 2F);
            GlStateManager.rotate(29.822046437F, 1, 0, 0);
            head1.render(unitPixel);
            head2.render(unitPixel);
            eyeleft.render(unitPixel);
            eyeright.render(unitPixel);
        } else {
            head1.render(unitPixel);
            head2.render(unitPixel);
            eyeleft.render(unitPixel);
            eyeright.render(unitPixel);
        }
        GlStateManager.popMatrix();
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float prevLimbSwing, float entityTickTime, float yRot, float xRot, float unitPixel, Entity entity) {
        super.setRotationAngles(limbSwing, prevLimbSwing, entityTickTime, yRot, xRot, unitPixel, entity);
        armleft.xRot = MathHelper.cos(limbSwing * 1.5F + (float) Math.PI) * 2.0F * prevLimbSwing * 0.5F - (MathHelper.cos(entityTickTime / 10.0F) + 0.7F) / 2.0F * 0.3F;
        armright.xRot = MathHelper.cos(limbSwing * 1.5F) * 2.0F * prevLimbSwing * 0.5F - (MathHelper.cos(entityTickTime / 11.0F) + 0.7F) / 2.0F * 0.3F;

        if(!entity.isRiding()) {
	        legleft2.xRot = MathHelper.cos(limbSwing * 1.5F) * 1.4F * prevLimbSwing;
	        legright2.xRot = MathHelper.cos(limbSwing * 1.5F + (float) Math.PI) * 1.4F * prevLimbSwing;
        } else {
        	legleft2.xRot = -80 / 360.0F * (float)Math.PI * 2;
        	legright2.xRot = -80 / 360.0F * (float)Math.PI * 2;
        }
    }
}
