package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import thebetweenlands.common.entity.draeton.EntityDraeton;

/**
 * BLDraetonAddonPulley - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelDraetonUpgradePulley extends Model {
    public ModelRenderer pulley_main;
    public ModelRenderer rotatingbeam;
    public ModelRenderer sideconnectionleft;
    public ModelRenderer sideconnection_right;
    public ModelRenderer guidingedge;
    public ModelRenderer handle1a;
    public ModelRenderer rope1;
    public ModelRenderer handle1b;
    public ModelRenderer handle1c;
    public ModelRenderer handle1d;
    public ModelRenderer handle1e;
    public ModelRenderer handle1f;
    public ModelRenderer handle1g;
    public ModelRenderer handle1h;
    public ModelRenderer handle1i;
    public ModelRenderer handle1j;
    public ModelRenderer handle1k;
    public ModelRenderer handle1l;

    public ModelDraetonUpgradePulley() {
        this.texWidth = 32;
        this.texHeight = 32;
        this.handle1b = new ModelRenderer(this, 26, 0);
        this.handle1b.setPos(-1.0F, 1.88F, -0.5F);
        this.handle1b.addBox(0.0F, -1.0F, -1.0F, 2, 1, 1, 0.0F);
        this.setRotateAngle(handle1b, -0.5235987755982988F, 0.0F, 0.0F);
        this.rope1 = new ModelRenderer(this, 0, 14);
        this.rope1.setPos(0.0F, 0.0F, 0.0F);
        this.rope1.addBox(-2.0F, -1.5F, -1.5F, 4, 3, 3, 0.0F);
        this.setRotateAngle(rope1, -0.091106186954104F, 0.0F, 0.0F);
        this.sideconnection_right = new ModelRenderer(this, 11, 24);
        this.sideconnection_right.setPos(-4.0F, -10.0F, 0.0F);
        this.sideconnection_right.addBox(-2.0F, -4.0F, -1.0F, 2, 4, 3, 0.0F);
        this.handle1e = new ModelRenderer(this, 19, 8);
        this.handle1e.setPos(0.0F, 0.0F, -1.0F);
        this.handle1e.addBox(0.0F, -1.0F, -1.0F, 2, 2, 1, 0.0F);
        this.setRotateAngle(handle1e, -0.5235987755982988F, 0.0F, 0.0F);
        this.handle1k = new ModelRenderer(this, 19, 20);
        this.handle1k.setPos(0.0F, 0.0F, -1.0F);
        this.handle1k.addBox(0.0F, -1.0F, -1.0F, 2, 2, 1, 0.0F);
        this.setRotateAngle(handle1k, -0.5235987755982988F, 0.0F, 0.0F);
        this.handle1j = new ModelRenderer(this, 26, 12);
        this.handle1j.setPos(0.0F, 0.0F, -1.0F);
        this.handle1j.addBox(0.0F, -1.0F, -1.0F, 2, 1, 1, 0.0F);
        this.setRotateAngle(handle1j, -0.5235987755982988F, 0.0F, 0.0F);
        this.handle1d = new ModelRenderer(this, 26, 3);
        this.handle1d.setPos(0.0F, 0.0F, -1.0F);
        this.handle1d.addBox(0.0F, -1.0F, -1.0F, 2, 1, 1, 0.0F);
        this.setRotateAngle(handle1d, -0.5235987755982988F, 0.0F, 0.0F);
        this.handle1c = new ModelRenderer(this, 19, 4);
        this.handle1c.setPos(0.0F, 0.0F, -1.0F);
        this.handle1c.addBox(0.0F, -1.0F, -1.0F, 2, 2, 1, 0.0F);
        this.setRotateAngle(handle1c, -0.5235987755982988F, 0.0F, 0.0F);
        this.handle1f = new ModelRenderer(this, 26, 6);
        this.handle1f.setPos(0.0F, 0.0F, -1.0F);
        this.handle1f.addBox(0.0F, -1.0F, -1.0F, 2, 1, 1, 0.0F);
        this.setRotateAngle(handle1f, -0.5235987755982988F, 0.0F, 0.0F);
        this.handle1h = new ModelRenderer(this, 26, 9);
        this.handle1h.setPos(0.0F, 0.0F, -1.0F);
        this.handle1h.addBox(0.0F, -1.0F, -1.0F, 2, 1, 1, 0.0F);
        this.setRotateAngle(handle1h, -0.5235987755982988F, 0.0F, 0.0F);
        this.rotatingbeam = new ModelRenderer(this, 0, 9);
        this.rotatingbeam.setPos(0.0F, -12.0F, 0.5F);
        this.rotatingbeam.addBox(-4.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.handle1g = new ModelRenderer(this, 19, 12);
        this.handle1g.setPos(0.0F, 0.0F, -1.0F);
        this.handle1g.addBox(0.0F, -1.0F, -1.0F, 2, 2, 1, 0.0F);
        this.setRotateAngle(handle1g, -0.5235987755982988F, 0.0F, 0.0F);
        this.guidingedge = new ModelRenderer(this, 0, 21);
        this.guidingedge.setPos(0.0F, -3.0F, 2.0F);
        this.guidingedge.addBox(-2.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(guidingedge, -0.136659280431156F, 0.0F, 0.0F);
        this.pulley_main = new ModelRenderer(this, 0, 0);
        this.pulley_main.setPos(0.0F, 0.0F, 0.0F);
        this.pulley_main.addBox(-2.0F, -3.0F, -2.0F, 4, 3, 5, 0.0F);
        this.setRotateAngle(pulley_main, -0.045553093477052F, 0.0F, 0.0F);
        this.sideconnectionleft = new ModelRenderer(this, 0, 24);
        this.sideconnectionleft.setPos(4.0F, -10.0F, -1.0F);
        this.sideconnectionleft.addBox(0.0F, -4.0F, 0.0F, 2, 4, 3, 0.0F);
        this.handle1i = new ModelRenderer(this, 19, 16);
        this.handle1i.setPos(0.0F, 0.0F, -1.0F);
        this.handle1i.addBox(0.0F, -1.0F, -1.0F, 2, 2, 1, 0.0F);
        this.setRotateAngle(handle1i, -0.5235987755982988F, 0.0F, 0.0F);
        this.handle1l = new ModelRenderer(this, 26, 15);
        this.handle1l.setPos(0.0F, 0.0F, -1.0F);
        this.handle1l.addBox(0.0F, -1.0F, -1.0F, 2, 1, 1, 0.0F);
        this.setRotateAngle(handle1l, -0.5235987755982988F, 0.0F, 0.0F);
        this.handle1a = new ModelRenderer(this, 19, 0);
        this.handle1a.setPos(3.0F, 0.0F, 0.0F);
        this.handle1a.addBox(-1.0F, 0.88F, -0.5F, 2, 2, 1, 0.0F);
        this.handle1a.addChild(this.handle1b);
        this.rotatingbeam.addChild(this.rope1);
        this.pulley_main.addChild(this.sideconnection_right);
        this.handle1d.addChild(this.handle1e);
        this.handle1j.addChild(this.handle1k);
        this.handle1i.addChild(this.handle1j);
        this.handle1c.addChild(this.handle1d);
        this.handle1b.addChild(this.handle1c);
        this.handle1e.addChild(this.handle1f);
        this.handle1g.addChild(this.handle1h);
        this.pulley_main.addChild(this.rotatingbeam);
        this.handle1f.addChild(this.handle1g);
        this.pulley_main.addChild(this.guidingedge);
        this.pulley_main.addChild(this.sideconnectionleft);
        this.handle1h.addChild(this.handle1i);
        this.handle1k.addChild(this.handle1l);
        this.rotatingbeam.addChild(this.handle1a);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.pulley_main.render(scale);
        
        if(entity instanceof EntityDraeton) {
        	EntityDraeton draeton = (EntityDraeton) entity;
        	this.rotatingbeam.xRot = (float) Math.toRadians(draeton.prevPulleyRotation + (draeton.pulleyRotation - draeton.prevPulleyRotation) * (ageInTicks - draeton.tickCount));
        } else {
        	this.rotatingbeam.xRot = 0;
        }
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
