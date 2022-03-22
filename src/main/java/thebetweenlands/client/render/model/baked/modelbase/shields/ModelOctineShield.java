package thebetweenlands.client.render.model.baked.modelbase.shields;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLShield_Octine - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelOctineShield extends Model {
	
    public ModelRenderer handle1;
    public ModelRenderer handle2;
    public ModelRenderer shield_main;
    public ModelRenderer plate1;
    public ModelRenderer plate2;
    public ModelRenderer rim3;
    public ModelRenderer rim4;
    public ModelRenderer bump;
    public ModelRenderer shieldpiece1;
    public ModelRenderer rim1;
    public ModelRenderer shieldpiece2;
    public ModelRenderer rim2;

    public ModelOctineShield() {
    	super(RenderType::entitySolid);
        this.texWidth = 128;
        this.texHeight = 64;
        this.rim3 = new ModelRenderer(this, 34, 22);
        this.rim3.setPos(5.0F, 0.0F, -2.0F);
        this.rim3.addBox(0.0F, -4.0F, -2.0F, 2, 8, 2, 0.0F);
        this.setRotateAngle(rim3, 0.0F, -0.18203784098300857F, 0.0F);
        this.handle2 = new ModelRenderer(this, 17, 0);
        this.handle2.setPos(6.0F, 0.0F, 0.0F);
        this.handle2.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 6, 0.0F);
        this.rim4 = new ModelRenderer(this, 43, 22);
        this.rim4.setPos(-5.0F, 0.0F, -2.0F);
        this.rim4.addBox(-2.0F, -4.0F, -2.0F, 2, 8, 2, 0.0F);
        this.setRotateAngle(rim4, 0.0F, 0.18203784098300857F, 0.0F);
        this.plate2 = new ModelRenderer(this, 0, 32);
        this.plate2.setPos(0.0F, 4.0F, -2.0F);
        this.plate2.addBox(-6.0F, 0.0F, 0.0F, 12, 5, 2, 0.0F);
        this.setRotateAngle(plate2, 0.091106186954104F, 0.0F, 0.0F);
        this.bump = new ModelRenderer(this, 52, 22);
        this.bump.setPos(0.0F, 0.0F, -2.0F);
        this.bump.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 1, 0.0F);
        this.rim1 = new ModelRenderer(this, 34, 12);
        this.rim1.setPos(0.0F, 0.0F, 0.0F);
        this.rim1.addBox(-7.0F, -2.0F, -2.0F, 14, 2, 2, 0.0F);
        this.setRotateAngle(rim1, -0.18203784098300857F, 0.0F, 0.0F);
        this.shield_main = new ModelRenderer(this, 0, 13);
        this.shield_main.setPos(3.0F, 0.0F, -1.0F);
        this.shield_main.addBox(-7.0F, -4.0F, -2.0F, 14, 8, 2, 0.0F);
        this.plate1 = new ModelRenderer(this, 0, 24);
        this.plate1.setPos(0.0F, -4.0F, -2.0F);
        this.plate1.addBox(-6.0F, -5.0F, 0.0F, 12, 5, 2, 0.0F);
        this.setRotateAngle(plate1, -0.091106186954104F, 0.0F, 0.0F);
        this.rim2 = new ModelRenderer(this, 34, 17);
        this.rim2.setPos(0.0F, 0.0F, 0.0F);
        this.rim2.addBox(-7.0F, 0.0F, -2.0F, 14, 2, 2, 0.0F);
        this.setRotateAngle(rim2, 0.18203784098300857F, 0.0F, 0.0F);
        this.handle1 = new ModelRenderer(this, 0, 0);
        this.handle1.setPos(-3.0F, 0.0F, 0.0F);
        this.handle1.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 6, 0.0F);
        this.shieldpiece1 = new ModelRenderer(this, 34, 0);
        this.shieldpiece1.setPos(0.0F, -5.0F, 0.0F);
        this.shieldpiece1.addBox(-7.0F, -2.0F, 0.0F, 14, 2, 3, 0.0F);
        this.shieldpiece2 = new ModelRenderer(this, 34, 6);
        this.shieldpiece2.setPos(0.0F, 5.0F, 0.0F);
        this.shieldpiece2.addBox(-7.0F, 0.0F, 0.0F, 14, 2, 3, 0.0F);
        this.shield_main.addChild(this.rim3);
        this.handle1.addChild(this.handle2);
        this.shield_main.addChild(this.rim4);
        this.shield_main.addChild(this.plate2);
        this.shield_main.addChild(this.bump);
        this.shieldpiece1.addChild(this.rim1);
        this.handle1.addChild(this.shield_main);
        this.shield_main.addChild(this.plate1);
        this.shieldpiece2.addChild(this.rim2);
        this.plate1.addChild(this.shieldpiece1);
        this.plate2.addChild(this.shieldpiece2);
    }

	@Override
	public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) { 
		this.handle1.render(matrix, vertex, in1, in2, f, f1, f2, f3);
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
