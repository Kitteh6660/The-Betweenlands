package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelWoodSupportBeam3 extends Model {
	
    public ModelRenderer beampart1;
    public ModelRenderer beampart2;
    public ModelRenderer beampart3;
    public ModelRenderer beampart4;
    public ModelRenderer beampart5;
    public ModelRenderer ropeboi;

    public ModelWoodSupportBeam3() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 64;
        this.beampart5 = new ModelRenderer(this, 0, 46);
        this.beampart5.setPos(0.0F, -4.0F, 6.0F);
        this.beampart5.addBox(-3.0F, -8.0F, -6.0F, 6, 8, 6, 0.0F);
        this.setRotateAngle(beampart5, 0.40980330836826856F, 0.0F, 0.0F);
        this.beampart4 = new ModelRenderer(this, 0, 35);
        this.beampart4.setPos(3.0F, -3.0F, -6.0F);
        this.beampart4.addBox(-3.05F, -3.95F, 0.0F, 6, 4, 6, 0.0F);
        this.setRotateAngle(beampart4, -0.136659280431156F, 0.0F, 0.0F);
        this.ropeboi = new ModelRenderer(this, 25, 0);
        this.ropeboi.setPos(0.0F, -5.0F, 0.0F);
        this.ropeboi.addBox(-3.5F, -1.0F, -6.5F, 7, 2, 7, 0.0F);
        this.setRotateAngle(ropeboi, 0.18203784098300857F, 0.0F, -0.091106186954104F);
        this.beampart2 = new ModelRenderer(this, 0, 15);
        this.beampart2.setPos(0.0F, -8.0F, 6.0F);
        this.beampart2.addBox(-3.1F, -3.0F, -6.05F, 6, 3, 6, 0.0F);
        this.setRotateAngle(beampart2, 0.40980330836826856F, 0.0F, 0.0F);
        this.beampart3 = new ModelRenderer(this, 0, 25);
        this.beampart3.setPos(-3.0F, -3.0F, 0.0F);
        this.beampart3.addBox(0.0F, -3.0F, -6.08F, 6, 3, 6, 0.0F);
        this.setRotateAngle(beampart3, 0.0F, 0.0F, 0.136659280431156F);
        this.beampart1 = new ModelRenderer(this, 0, 0);
        this.beampart1.setPos(0.0F, 24.0F, 8.5F);
        this.beampart1.addBox(-3.0F, -8.0F, 0.0F, 6, 8, 6, 0.0F);
        this.setRotateAngle(beampart1, 0.5462880558742251F, 0.136659280431156F, 0.0F);
        this.beampart4.addChild(this.beampart5);
        this.beampart3.addChild(this.beampart4);
        this.beampart5.addChild(this.ropeboi);
        this.beampart1.addChild(this.beampart2);
        this.beampart2.addChild(this.beampart3);
    }

    @Override
    public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) {  
        this.beampart1.render(matrix, vertex, in1, in2, f, f1, f2, f3);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
