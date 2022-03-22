package thebetweenlands.client.render.model.baked.modelbase.shields;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLShield_Bone - TripleHeadedSheep
 * Created using Tabula 4.1.1, updated for 1.16.5
 */
public class ModelBoneShield extends Model {
	
    public ModelRenderer handle;
    public ModelRenderer shield_main;
    public ModelRenderer bone1;
    public ModelRenderer bone4;
    public ModelRenderer midrope1;
    public ModelRenderer midrope4;
    public ModelRenderer shoulderblade1;
    public ModelRenderer shoulderblade3;
    public ModelRenderer bone2;
    public ModelRenderer bone3;
    public ModelRenderer bone5;
    public ModelRenderer bone6;
    public ModelRenderer midrope2;
    public ModelRenderer midrope3;
    public ModelRenderer midrope5;
    public ModelRenderer midrope6;
    public ModelRenderer shoulderblade2;
    public ModelRenderer shoulderblade4;

    public ModelBoneShield() {
    	super(RenderType::entitySolid);
        this.texWidth = 128;
        this.texHeight = 64;
        this.midrope3 = new ModelRenderer(this, 36, 0);
        this.midrope3.setPos(-3.0F, 0.0F, -1.5F);
        this.midrope3.addBox(-5.0F, 0.0F, 0.0F, 5, 2, 3, 0.0F);
        this.setRotateAngle(midrope3, 0.0F, 0.22759093446006054F, 0.0F);
        this.midrope1 = new ModelRenderer(this, 17, 0);
        this.midrope1.setPos(0.0F, 5.5F, -1.0F);
        this.midrope1.addBox(-3.0F, 0.0F, -1.5F, 6, 2, 3, 0.0F);
        this.midrope4 = new ModelRenderer(this, 53, 0);
        this.midrope4.setPos(0.0F, -1.5F, -1.0F);
        this.midrope4.addBox(-3.0F, 0.0F, -1.5F, 6, 2, 3, 0.0F);
        this.bone3 = new ModelRenderer(this, 27, 13);
        this.bone3.setPos(-2.0F, 0.0F, 0.0F);
        this.bone3.addBox(-2.0F, -14.0F, -2.0F, 2, 14, 2, 0.0F);
        this.setRotateAngle(bone3, 0.0F, 0.091106186954104F, 0.0F);
        this.midrope6 = new ModelRenderer(this, 72, 0);
        this.midrope6.setPos(-3.0F, 0.0F, -1.5F);
        this.midrope6.addBox(-5.0F, 0.0F, 0.0F, 5, 2, 3, 0.0F);
        this.setRotateAngle(midrope6, 0.0F, 0.22759093446006054F, 0.0F);
        this.shoulderblade2 = new ModelRenderer(this, 19, 30);
        this.shoulderblade2.setPos(0.0F, 8.0F, 0.0F);
        this.shoulderblade2.addBox(0.0F, 0.0F, -2.0F, 5, 2, 2, 0.0F);
        this.handle = new ModelRenderer(this, 0, 0);
        this.handle.setPos(0.0F, 0.0F, 0.0F);
        this.handle.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 6, 0.0F);
        this.bone1 = new ModelRenderer(this, 9, 13);
        this.bone1.setPos(-1.0F, 6.0F, 0.0F);
        this.bone1.addBox(-2.0F, -14.0F, -2.0F, 2, 14, 2, 0.0F);
        this.setRotateAngle(bone1, -0.045553093477052F, 0.091106186954104F, 0.0F);
        this.bone2 = new ModelRenderer(this, 18, 13);
        this.bone2.setPos(-2.0F, 0.0F, 0.0F);
        this.bone2.addBox(-2.0F, -12.0F, -2.0F, 2, 12, 2, 0.0F);
        this.setRotateAngle(bone2, 0.045727626402251434F, 0.091106186954104F, 0.0F);
        this.shield_main = new ModelRenderer(this, 0, 13);
        this.shield_main.setPos(0.0F, -3.0F, -1.0F);
        this.shield_main.addBox(-1.0F, -8.0F, -2.0F, 2, 14, 2, 0.0F);
        this.midrope2 = new ModelRenderer(this, 17, 6);
        this.midrope2.setPos(3.0F, 0.0F, -1.5F);
        this.midrope2.addBox(0.0F, 0.0F, 0.0F, 5, 2, 3, 0.0F);
        this.setRotateAngle(midrope2, 0.0F, -0.22759093446006054F, 0.0F);
        this.shoulderblade4 = new ModelRenderer(this, 19, 41);
        this.shoulderblade4.setPos(0.0F, 8.0F, 0.0F);
        this.shoulderblade4.addBox(-5.0F, 0.0F, -2.0F, 5, 2, 2, 0.0F);
        this.bone4 = new ModelRenderer(this, 36, 13);
        this.bone4.setPos(1.0F, 6.0F, 0.0F);
        this.bone4.addBox(0.0F, -13.0F, -2.0F, 2, 13, 2, 0.0F);
        this.setRotateAngle(bone4, 0.0F, -0.091106186954104F, 0.0F);
        this.shoulderblade1 = new ModelRenderer(this, 0, 30);
        this.shoulderblade1.setPos(0.0F, 7.0F, 0.0F);
        this.shoulderblade1.addBox(0.0F, 0.0F, -2.0F, 7, 8, 2, 0.0F);
        this.setRotateAngle(shoulderblade1, 0.045553093477052F, -0.136659280431156F, 0.0F);
        this.bone6 = new ModelRenderer(this, 54, 13);
        this.bone6.setPos(2.0F, 0.0F, 0.0F);
        this.bone6.addBox(0.0F, -14.0F, -2.0F, 2, 14, 2, 0.0F);
        this.setRotateAngle(bone6, 0.0F, -0.091106186954104F, 0.026354471705114374F);
        this.shoulderblade3 = new ModelRenderer(this, 0, 41);
        this.shoulderblade3.setPos(0.0F, 7.0F, 0.0F);
        this.shoulderblade3.addBox(-7.0F, 0.0F, -2.0F, 7, 8, 2, 0.0F);
        this.setRotateAngle(shoulderblade3, 0.045553093477052F, 0.136659280431156F, 0.0F);
        this.midrope5 = new ModelRenderer(this, 53, 6);
        this.midrope5.setPos(3.0F, 0.0F, -1.5F);
        this.midrope5.addBox(0.0F, 0.0F, 0.0F, 5, 2, 3, 0.0F);
        this.setRotateAngle(midrope5, 0.0F, -0.22759093446006054F, 0.0F);
        this.bone5 = new ModelRenderer(this, 45, 13);
        this.bone5.setPos(2.0F, 0.0F, 0.0F);
        this.bone5.addBox(0.0F, -14.0F, -2.0F, 2, 14, 2, 0.0F);
        this.setRotateAngle(bone5, 0.0F, -0.091106186954104F, 0.0F);
        this.midrope1.addChild(this.midrope3);
        this.shield_main.addChild(this.midrope1);
        this.shield_main.addChild(this.midrope4);
        this.bone2.addChild(this.bone3);
        this.midrope4.addChild(this.midrope6);
        this.shoulderblade1.addChild(this.shoulderblade2);
        this.shield_main.addChild(this.bone1);
        this.bone1.addChild(this.bone2);
        this.midrope1.addChild(this.midrope2);
        this.shoulderblade3.addChild(this.shoulderblade4);
        this.shield_main.addChild(this.bone4);
        this.shield_main.addChild(this.shoulderblade1);
        this.bone5.addChild(this.bone6);
        this.shield_main.addChild(this.shoulderblade3);
        this.midrope4.addChild(this.midrope5);
        this.bone4.addChild(this.bone5);
    }

	@Override
	public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) { 
		this.handle.render(matrix, vertex, in1, in2, f, f1, f2, f3);
		this.shield_main.render(matrix, vertex, in1, in2, f, f1, f2, f3);
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
