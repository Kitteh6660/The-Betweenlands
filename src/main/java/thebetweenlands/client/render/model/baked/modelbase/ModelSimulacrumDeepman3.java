package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLDeepmanStatuette3 - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelSimulacrumDeepman3 extends Model {
	
    public ModelRenderer body_base;
    public ModelRenderer scroll;
    public ModelRenderer arm1a;
    public ModelRenderer arm1b;
    public ModelRenderer arm1c;
    public ModelRenderer scrollstick;
    public ModelRenderer paperpiece1a;
    public ModelRenderer paperpiece1b;

    public ModelSimulacrumDeepman3() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 64;
        this.scrollstick = new ModelRenderer(this, 0, 42);
        this.scrollstick.setPos(0.0F, -1.0F, 0.0F);
        this.scrollstick.addBox(-0.5F, -0.5F, -4.0F, 1, 1, 8, 0.0F);
        this.setRotateAngle(scrollstick, 0.0F, 0.0F, 0.36425021489121656F);
        this.body_base = new ModelRenderer(this, 0, 0);
        this.body_base.setPos(-4.5F, 24.0F, -3.0F);
        this.body_base.addBox(0.0F, -10.0F, 0.0F, 6, 10, 6, 0.0F);
        this.setRotateAngle(body_base, -0.045553093477052F, -0.045553093477052F, 0.045553093477052F);
        this.arm1a = new ModelRenderer(this, 0, 17);
        this.arm1a.setPos(3.0F, -6.7F, 3.0F);
        this.arm1a.addBox(-3.5F, 0.0F, 0.0F, 7, 3, 3, 0.0F);
        this.setRotateAngle(arm1a, -0.8651597102135892F, 0.0F, 0.0F);
        this.scroll = new ModelRenderer(this, 0, 32);
        this.scroll.setPos(3.5F, 24.0F, 0.0F);
        this.scroll.addBox(-1.0F, -2.0F, -3.4F, 2, 2, 7, 0.0F);
        this.setRotateAngle(scroll, 0.0F, -0.18203784098300857F, -0.136659280431156F);
        this.arm1c = new ModelRenderer(this, 13, 24);
        this.arm1c.setPos(3.5F, 3.0F, 3.0F);
        this.arm1c.addBox(-2.99F, 0.0F, -3.0F, 3, 3, 3, 0.0F);
        this.setRotateAngle(arm1c, -0.5009094953223726F, 0.0F, 0.0F);
        this.arm1b = new ModelRenderer(this, 0, 24);
        this.arm1b.setPos(-0.5F, 3.0F, 3.0F);
        this.arm1b.addBox(-3.01F, 0.0F, -3.0F, 3, 3, 3, 0.0F);
        this.setRotateAngle(arm1b, -0.27314402793711257F, 0.0F, 0.0F);
        this.paperpiece1a = new ModelRenderer(this, 0, 52);
        this.paperpiece1a.setPos(1.0F, 0.0F, 0.0F);
        this.paperpiece1a.addBox(0.0F, 0.0F, -3.5F, 1, 0, 7, 0.0F);
        this.setRotateAngle(paperpiece1a, 0.0F, 0.0F, -0.22759093446006054F);
        this.paperpiece1b = new ModelRenderer(this, -5, 52);
        this.paperpiece1b.setPos(1.0F, 0.0F, 0.0F);
        this.paperpiece1b.addBox(0.0F, 0.0F, -3.5F, 2, 0, 7, 0.0F);
        this.setRotateAngle(paperpiece1b, 0.0F, 0.0F, 0.5462880558742251F);
        this.scroll.addChild(this.scrollstick);
        this.body_base.addChild(this.arm1a);
        this.arm1a.addChild(this.arm1c);
        this.arm1a.addChild(this.arm1b);
        this.scroll.addChild(this.paperpiece1a);
        this.paperpiece1a.addChild(this.paperpiece1b);
    }

    @Override
    public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) {  
        this.body_base.render(matrix, vertex, in1, in2, f, f1, f2, f3);
        this.scroll.render(matrix, vertex, in1, in2, f, f1, f2, f3);
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
