package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelWoodSupportBeam2 extends Model {
	
    public ModelRenderer beampart1;
    public ModelRenderer beampart2;
    public ModelRenderer beampart3;
    public ModelRenderer beampart4;
    public ModelRenderer knot1;
    public ModelRenderer ropeboi;

    public ModelWoodSupportBeam2() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 64;
        this.beampart2 = new ModelRenderer(this, 0, 15);
        this.beampart2.setPos(0.0F, -8.0F, 6.0F);
        this.beampart2.addBox(-3.05F, -3.0F, -5.95F, 6, 3, 6, 0.0F);
        this.setRotateAngle(beampart2, 0.36425021489121656F, 0.0F, 0.0F);
        this.knot1 = new ModelRenderer(this, 28, 0);
        this.knot1.setPos(0.0F, -0.5F, -1.0F);
        this.knot1.addBox(0.0F, -3.05F, -3.0F, 1, 3, 3, 0.0F);
        this.setRotateAngle(knot1, 0.0F, 0.091106186954104F, -0.091106186954104F);
        this.beampart1 = new ModelRenderer(this, 0, 0);
        this.beampart1.setPos(0.0F, 24.0F, 8.0F);
        this.beampart1.addBox(-3.0F, -8.04F, 0.0F, 6, 8, 6, 0.0F);
        this.setRotateAngle(beampart1, 0.5462880558742251F, 0.0F, 0.091106186954104F);
        this.beampart3 = new ModelRenderer(this, 0, 25);
        this.beampart3.setPos(3.0F, -3.0F, 0.0F);
        this.beampart3.addBox(-6.025F, -6.95F, -6.0F, 6, 7, 6, 0.0F);
        this.setRotateAngle(beampart3, 0.0F, 0.0F, -0.18203784098300857F);
        this.beampart4 = new ModelRenderer(this, 0, 39);
        this.beampart4.setPos(-3.0F, -7.0F, 0.0F);
        this.beampart4.addBox(-2.95F, -8.07F, -6.0F, 6, 8, 6, 0.0F);
        this.setRotateAngle(beampart4, 0.36425021489121656F, 0.0F, 0.0F);
        this.ropeboi = new ModelRenderer(this, 25, 7);
        this.ropeboi.setPos(0.0F, -4.0F, 0.0F);
        this.ropeboi.addBox(-3.5F, -1.0F, -6.5F, 7, 2, 7, 0.0F);
        this.setRotateAngle(ropeboi, 0.091106186954104F, 0.045553093477052F, 0.136659280431156F);
        this.beampart1.addChild(this.beampart2);
        this.beampart3.addChild(this.knot1);
        this.beampart2.addChild(this.beampart3);
        this.beampart3.addChild(this.beampart4);
        this.beampart4.addChild(this.ropeboi);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {  
        this.beampart1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
