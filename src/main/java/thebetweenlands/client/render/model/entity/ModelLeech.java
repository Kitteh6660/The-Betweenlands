package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntityLeech;

@OnlyIn(Dist.CLIENT)
public class ModelLeech extends Model {
    ModelRenderer s1;
    ModelRenderer s2;
    ModelRenderer s3;
    ModelRenderer s4;
    ModelRenderer s5;
    ModelRenderer s6;

    public ModelLeech() {
        textureWidth = 64;
        textureHeight = 32;

        s1 = new ModelRenderer(this, 0, 11);
        s1.addBox(0F, 0F, 0F, 2, 2, 1);
        s1.setPos(-1F, 22F, -7F);
        setRotation(s1, 0F, 0F, 0F);

        s2 = new ModelRenderer(this, 0, 0);
        s2.addBox(0F, 0F, 0F, 3, 3, 2);
        s2.setPos(-1.5F, 21F, -6F);
        setRotation(s2, 0F, 0F, 0F);

        s3 = new ModelRenderer(this, 0, 0);
        s3.addBox(0F, 0F, 0F, 4, 4, 7);
        s3.setPos(-2F, 20F, -4F);
        setRotation(s3, 0F, 0F, 0F);

        s4 = new ModelRenderer(this, 0, 0);
        s4.addBox(0F, 0F, 0F, 3, 3, 2);
        s4.setPos(-1.5F, 21F, 3F);
        setRotation(s4, 0F, 0F, 0F);

        s5 = new ModelRenderer(this, 0, 0);
        s5.addBox(0F, 0F, 0F, 2, 2, 2);
        s5.setPos(-1F, 22F, 5F);
        setRotation(s5, 0F, 0F, 0F);

        s6 = new ModelRenderer(this, 6, 11);
        s6.addBox(0F, 0F, 0F, 1, 1, 2);
        s6.setPos(-0.5F, 23F, 7F);
        setRotation(s6, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float yRot, float xRot, float unitPixel) {
        super.render(entity, limbSwing, limbSwingAngle, entityTickTime, yRot, xRot, unitPixel);
        setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, yRot, xRot, unitPixel, entity);

        s1.render(unitPixel);
        s2.render(unitPixel);
        s3.render(unitPixel);
        s4.render(unitPixel);
        s5.render(unitPixel);
        s6.render(unitPixel);

    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float yRot, float xRot, float unitPixel, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, yRot, xRot, unitPixel, entity);
        EntityLeech leech = (EntityLeech) entity;
        if (!leech.isRiding())
            leech.moveProgress = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
    }
}
