package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelWoodSupportBeam1 extends Model {
	
    public ModelRenderer beampart1;
    public ModelRenderer beampart2;
    public ModelRenderer beampart3;
    public ModelRenderer ropeboi;

    public ModelWoodSupportBeam1() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 64;
        this.ropeboi = new ModelRenderer(this, 25, 0);
        this.ropeboi.setPos(0.0F, -4.0F, 0.0F);
        this.ropeboi.addBox(-3.5F, -1.0F, -6.5F, 7, 2, 7, 0.0F);
        this.beampart1 = new ModelRenderer(this, 0, 0);
        this.beampart1.setPos(0.0F, 24.0F, 8.0F);
        this.beampart1.addBox(-3.0F, -8.0F, 0.0F, 6, 8, 6, 0.0F);
        this.setRotateAngle(beampart1, 0.5462880558742251F, 0.0F, 0.0F);
        this.beampart2 = new ModelRenderer(this, 0, 15);
        this.beampart2.setPos(0.0F, -8.0F, 6.0F);
        this.beampart2.addBox(-3.05F, -10.0F, -6.0F, 6, 10, 6, 0.0F);
        this.setRotateAngle(beampart2, 0.36425021489121656F, 0.0F, 0.0F);
        this.beampart3 = new ModelRenderer(this, 0, 32);
        this.beampart3.setPos(0.0F, -10.0F, 0.0F);
        this.beampart3.addBox(-3.0F, -8.0F, -6.0F, 6, 8, 6, 0.0F);
        this.setRotateAngle(beampart3, 0.36425021489121656F, 0.0F, 0.0F);
        this.beampart3.addChild(this.ropeboi);
        this.beampart1.addChild(this.beampart2);
        this.beampart2.addChild(this.beampart3);
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
