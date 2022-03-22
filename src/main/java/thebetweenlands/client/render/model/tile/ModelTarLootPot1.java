package thebetweenlands.client.render.model.tile;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelTarLootPot1 extends Model {
	ModelRenderer foot;
	ModelRenderer tarBlock;
	ModelRenderer footChild;
	ModelRenderer footChild_1;
	ModelRenderer footChild_2;
	ModelRenderer footChild_3;
	ModelRenderer footChild_4;
	ModelRenderer footChildChild;

	public ModelTarLootPot1() {
		textureWidth = 128;
		textureHeight = 64;
		foot = new ModelRenderer(this, 0, 0);
		foot.setPos(0.0F, 20.0F, 0.0F);
		foot.addBox(-4.0F, -4.0F, -4.0F, 8, 4, 8, 0.0F);
		setRotateAngle(foot, 0.17453292519943295F, 0.0F, 0.0F);
		footChild_3 = new ModelRenderer(this, 33, 0);
		footChild_3.setPos(0.0F, -12.0F, 0.0F);
		footChild_3.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10, 0.0F);
		setRotateAngle(footChild_3, 0.0F, 0.04555309191346169F, 0.0F);
		footChild_1 = new ModelRenderer(this, 29, 26);
		footChild_1.setPos(0.0F, -2.0F, 4.0F);
		footChild_1.addBox(-4.0F, -10.0F, 0.0F, 8, 10, 2, 0.0F);
		footChildChild = new ModelRenderer(this, 64, 0);
		footChildChild.setPos(0.0F, -2.0F, 0.0F);
		footChildChild.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
		footChild_4 = new ModelRenderer(this, 0, 13);
		footChild_4.setPos(-4.0F, -2.0F, 0.0F);
		footChild_4.addBox(-2.0F, -10.0F, -6.0F, 2, 10, 12, 0.0F);
		footChild_2 = new ModelRenderer(this, 0, 36);
		footChild_2.setPos(4.0F, -2.0F, 0.0F);
		footChild_2.addBox(0.0F, -10.0F, -6.0F, 2, 10, 12, 0.0F);
		footChild = new ModelRenderer(this, 29, 13);
		footChild.setPos(0.0F, -2.0F, -4.0F);
		footChild.addBox(-4.0F, -10.0F, -2.0F, 8, 10, 2, 0.0F);
		tarBlock = new ModelRenderer(this, 52, 14);
		tarBlock.setPos(0.0F, 24.0F, 0.0F);
		tarBlock.addBox(-8.0F, -16.0F, -8.0F, 16, 16, 16, 0.0F);
		foot.addChild(footChild_3);
		foot.addChild(footChild_1);
		footChild_3.addChild(footChildChild);
		foot.addChild(footChild_4);
		foot.addChild(footChild_2);
		foot.addChild(footChild);
	}

	public void render() { 
		foot.render(0.0625F);
		tarBlock.render(0.0625F);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}
