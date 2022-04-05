package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLWhitePear2 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelWhitePearCrop2 extends Model {
	
    public ModelRenderer stem1;
    public ModelRenderer stem2;
    public ModelRenderer stem3;
    public ModelRenderer leaf1a;
    public ModelRenderer leaf2a;
    public ModelRenderer leaf3a;
    public ModelRenderer leaf3b;
    public ModelRenderer leaf1b;
    public ModelRenderer leaf1d;
    public ModelRenderer leaf1c;
    public ModelRenderer leaf2b;
    public ModelRenderer leaf2c;

    public ModelWhitePearCrop2() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 32;
        this.leaf1c = new ModelRenderer(this, 6, 8);
        this.leaf1c.setPos(0.0F, 0.0F, 3.0F);
        this.leaf1c.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf1c, -0.7740535232594852F, 0.0F, 0.0F);
        this.leaf1b = new ModelRenderer(this, 6, 4);
        this.leaf1b.setPos(0.0F, 0.0F, 3.0F);
        this.leaf1b.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf1b, -0.7285004297824331F, 0.0F, 0.0F);
        this.stem3 = new ModelRenderer(this, 0, 15);
        this.stem3.setPos(0.0F, -4.0F, 0.0F);
        this.stem3.addBox(-2.0F, -4.0F, -1.02F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stem3, 0.0F, 0.0F, -0.4553564018453205F);
        this.leaf3a = new ModelRenderer(this, 23, 0);
        this.leaf3a.setPos(-1.0F, -4.0F, 0.0F);
        this.leaf3a.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F);
        this.setRotateAngle(leaf3a, 2.6862362517444724F, -0.36425021489121656F, 0.0F);
        this.stem2 = new ModelRenderer(this, 0, 8);
        this.stem2.setPos(1.0F, -4.0F, 0.0F);
        this.stem2.addBox(-2.0F, -4.0F, -1.01F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stem2, 0.0F, 0.0F, -0.36425021489121656F);
        this.leaf1d = new ModelRenderer(this, 6, 12);
        this.leaf1d.setPos(0.0F, 0.3F, 0.0F);
        this.leaf1d.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf1d, -0.36425021489121656F, 0.4553564018453205F, -0.40980330836826856F);
        this.leaf3b = new ModelRenderer(this, 23, 3);
        this.leaf3b.setPos(0.0F, 0.0F, 2.0F);
        this.leaf3b.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F);
        this.setRotateAngle(leaf3b, 0.5009094953223726F, 0.0F, 0.0F);
        this.leaf2c = new ModelRenderer(this, 16, 8);
        this.leaf2c.setPos(0.0F, 0.0F, 0.0F);
        this.leaf2c.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F);
        this.setRotateAngle(leaf2c, -0.36425021489121656F, 0.40980330836826856F, 0.045553093477052F);
        this.stem1 = new ModelRenderer(this, 0, 0);
        this.stem1.setPos(0.0F, 24.0F, 0.0F);
        this.stem1.addBox(-1.0F, -4.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(stem1, 0.136659280431156F, 0.0F, 0.40980330836826856F);
        this.leaf2b = new ModelRenderer(this, 15, 4);
        this.leaf2b.setPos(0.0F, 0.0F, 3.0F);
        this.leaf2b.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf2b, -0.6373942428283291F, 0.0F, 0.0F);
        this.leaf1a = new ModelRenderer(this, 6, 0);
        this.leaf1a.setPos(-0.5F, -4.0F, 0.5F);
        this.leaf1a.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(leaf1a, 0.5462880558742251F, 0.5918411493512771F, 0.0F);
        this.leaf2a = new ModelRenderer(this, 15, 0);
        this.leaf2a.setPos(-1.5F, -3.5F, -0.5F);
        this.leaf2a.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf2a, 0.31869712141416456F, -2.1399481958702475F, 0.0F);
        this.leaf1b.addChild(this.leaf1c);
        this.leaf1a.addChild(this.leaf1b);
        this.stem2.addChild(this.stem3);
        this.stem3.addChild(this.leaf3a);
        this.stem1.addChild(this.stem2);
        this.leaf1a.addChild(this.leaf1d);
        this.leaf3a.addChild(this.leaf3b);
        this.leaf2a.addChild(this.leaf2c);
        this.leaf2a.addChild(this.leaf2b);
        this.stem2.addChild(this.leaf1a);
        this.stem2.addChild(this.leaf2a);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {  
        this.stem1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
