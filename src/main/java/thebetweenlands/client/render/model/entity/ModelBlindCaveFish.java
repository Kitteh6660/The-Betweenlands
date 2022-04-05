package thebetweenlands.client.render.model.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;

@OnlyIn(Dist.CLIENT)
public class ModelBlindCaveFish extends Model {
	
    ModelRenderer lure1;
    ModelRenderer lure2;
    ModelRenderer lure3;
    ModelRenderer head;
    ModelRenderer jaw;
    ModelRenderer bottomTeeth;
    ModelRenderer topTeeth;
    ModelRenderer body;
    ModelRenderer tail;
    ModelRenderer midSection;
    ModelRenderer dorsalFin;
    ModelRenderer pectoralFinL;
    ModelRenderer pectoralFinR;
    ModelRenderer tailFin;

    public ModelBlindCaveFish() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 32;
        this.pectoralFinL = new ModelRenderer(this, 35, 0);
        this.pectoralFinL.setPos(2.0F, 11.0F, 2.0F);
        this.pectoralFinL.addBox(0.0F, 0.0F, -1.5F, 4, 0, 3, 0.0F);
        this.setRotation(pectoralFinL, -0.5585053563117981F, -0.6217309832572937F, -0.2617993950843811F);
        this.midSection = new ModelRenderer(this, 42, 21);
        this.midSection.setPos(0.0F, 16.0F, 0.0F);
        this.midSection.addBox(-1.0F, -7.0F, 8.0F, 2, 6, 5, 0.0F);
        this.setRotation(midSection, 0.0F, -0.024346200749278072F, 0.0F);
        this.lure1 = new ModelRenderer(this, 2, 0);
        this.lure1.setPos(0.0F, 9.0F, 0.0F);
        this.lure1.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotation(lure1, 0.8203047513961792F, -0.0F, 0.0F);
        this.lure3 = new ModelRenderer(this, 0, 9);
        this.lure3.setPos(0.0F, 9.0F, 0.0F);
        this.lure3.addBox(-1.5F, -5.0F, -4.5F, 3, 3, 3, 0.0F);
        this.setRotation(lure3, 0.8203047513961792F, -0.0F, 0.0F);
        this.head = new ModelRenderer(this, 0, 19);
        this.head.setPos(0.0F, 16.0F, 0.0F);
        this.head.addBox(-2.0F, -5.0F, 0.0F, 4, 6, 7, 0.0F);
        this.setRotation(head, 0.8203047513961792F, -0.0F, 0.0F);
        this.topTeeth = new ModelRenderer(this, 16, 0);
        this.topTeeth.setPos(0.0F, 16.0F, 0.0F);
        this.topTeeth.addBox(-2.0F, -4.5F, -2.0F, 4, 5, 2, 0.0F);
        this.setRotation(topTeeth, 0.8203047513961792F, -0.0F, 0.0F);
        this.lure2 = new ModelRenderer(this, 0, 4);
        this.lure2.setPos(0.0F, 9.0F, 0.0F);
        this.lure2.addBox(-0.5F, -4.0F, -3.5F, 1, 1, 4, 0.0F);
        this.setRotation(lure2, 0.8203047513961792F, -0.0F, 0.0F);
        this.dorsalFin = new ModelRenderer(this, 46, -6);
        this.dorsalFin.setPos(0.0F, 16.0F, 0.0F);
        this.dorsalFin.addBox(0.0F, -11.0F, 5.0F, 0, 3, 6, 0.0F);
        this.setRotation(dorsalFin, -0.13962633907794952F, -0.024346200749278072F, 0.0F);
        this.jaw = new ModelRenderer(this, 14, 13);
        this.jaw.setPos(0.0F, 16.0F, 0.0F);
        this.jaw.addBox(-1.5F, -4.0F, -1.0F, 3, 5, 1, 0.0F);
        this.setRotation(jaw, 1.3782689571380613F, -0.0F, 0.0F);
        this.body = new ModelRenderer(this, 22, 17);
        this.body.setPos(0.0F, 16.0F, 0.0F);
        this.body.addBox(-1.5F, -8.0F, 1.0F, 3, 8, 7, 0.0F);
        this.tail = new ModelRenderer(this, 56, 26);
        this.tail.setPos(0.0F, 12.0F, 13.0F);
        this.tail.addBox(-0.5F, -17.5F, 0.0F, 1, 3, 3, 0.0F);
        this.setRotation(tail, 0.0F, 0.07557275661135447F, 0.0F);
        this.pectoralFinR = new ModelRenderer(this, 35, 0);
        this.pectoralFinR.setPos(-2.0F, 11.0F, 2.0F);
        this.pectoralFinR.addBox(-4.0F, 0.0F, -1.5F, 4, 0, 3, 0.0F);
        this.setRotation(pectoralFinR, -0.5585053563117981F, 0.6217309832572937F, 0.2617993950843811F);
        this.bottomTeeth = new ModelRenderer(this, 8, 0);
        this.bottomTeeth.setPos(0.0F, 16.0F, 0.0F);
        this.bottomTeeth.addBox(-1.5F, -3.5F, 0.0F, 3, 5, 1, 0.0F);
        this.setRotation(bottomTeeth, 1.3782689571380613F, -0.0F, 0.0F);
        this.tailFin = new ModelRenderer(this, 58, -3);
        this.tailFin.setPos(0.0F, 12.0F, 13.0F);
        this.tailFin.addBox(0.0F, -18.0F, 3.0F, 0, 4, 3, 0.0F);
        this.setRotation(tailFin, 0.0F, 0.07557275661135447F, 0.0F);
        this.midSection.addChild(this.tail);
        this.midSection.addChild(this.tailFin);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        //super.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, yRot, xRot, unitPixel, entity);
        body.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        pectoralFinL.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        pectoralFinR.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        bottomTeeth.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        topTeeth.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        lure3.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        jaw.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        dorsalFin.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        midSection.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        lure1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        lure2.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        head.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }

    @Override
    public void setPose(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float yRot, float xRot, float unitPixel) {
        super.setPose(entity, limbSwing, limbSwingAngle, entityTickTime, yRot, xRot, unitPixel);
        EntityBlindCaveFish fish = (EntityBlindCaveFish) entity;
        jaw.xRot = 1.5F + fish.moveProgress;
        bottomTeeth.xRot = 1.5F + fish.moveProgress;
        pectoralFinL.yRot = -0.5F - fish.moveProgress;
        pectoralFinR.yRot = 0.5F + fish.moveProgress;
        dorsalFin.yRot = midSection.yRot = -0.05F + fish.moveProgress * 0.2F;
        tail.yRot = midSection.yRot * 1.2F;
        tailFin.yRot = midSection.yRot * 1.4F;
    }

}
