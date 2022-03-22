package thebetweenlands.client.render.model.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.EnderDragonRenderer.EnderDragonModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntityAngler;

@OnlyIn(Dist.CLIENT)
public class ModelAngler<T extends Entity> extends EntityModel<EntityAngler> {
	
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

    public ModelAngler() {
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

    /*@Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float yRot, float xRot, float unitPixel) {
        super.render(entity, limbSwing, limbSwingAngle, entityTickTime, yRot, xRot, unitPixel);
        setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, yRot, xRot, unitPixel, entity);
        body.render(unitPixel);
        pectoralFinL.render(unitPixel);
        pectoralFinR.render(unitPixel);
        bottomTeeth.render(unitPixel);
        topTeeth.render(unitPixel);
        lure3.render(unitPixel);
        jaw.render(unitPixel);
        dorsalFin.render(unitPixel);
        midSection.render(unitPixel);
        lure1.render(unitPixel);
        lure2.render(unitPixel);
        head.render(unitPixel);
    }*/

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }

    @Override
    public void setLivingAnimations(LivingEntity entity, float swing, float speed, float partialRenderTicks) {
    	EntityAngler angler = (EntityAngler) entity;

            float flap = MathHelper.sin((angler.tickCount + partialRenderTicks) * 0.5F) * 0.6F;
            if (angler.isGrounded())
            	flap = MathHelper.sin((angler.tickCount + partialRenderTicks) * 1.5F) * 0.6F;
            jaw.xRot = 1.5F + flap*0.5F;
            bottomTeeth.xRot = 1.5F + flap*0.5F;
    		dorsalFin.yRot = midSection.yRot = -0.05F + flap * 0.2F;
    		pectoralFinR.yRot = 0.5F - flap;
    		pectoralFinL.yRot = -0.5F + flap;
    		tail.yRot = midSection.yRot * 1.2F;
    		tail.yRot = midSection.yRot * 1.4F;
    		tailFin.yRot = midSection.yRot * 1.6F;
    }

	@Override
	public Iterable<ModelRenderer> parts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setupAnim(EntityAngler p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderToBuffer(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
		// TODO Auto-generated method stub
		
	}
}
