package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSwordEnergy extends Model {

	ModelRenderer jewel1;
	ModelRenderer jewel2;
	ModelRenderer jewel3;

	public ModelSwordEnergy() {
		textureWidth = 32;
		textureHeight = 64;

		jewel1 = new ModelRenderer(this, 0, 7);
		jewel1.addBox(-4F, -4F, -4F, 8, 8, 8);
		jewel1.setPos(0F, 16F, 0F);
		setRotation(jewel1, 0F, 0.7853982F, 0F);
		jewel2 = new ModelRenderer(this, 0, 7);
		jewel2.addBox(-4F, -4F, -4F, 8, 8, 8);
		jewel2.setPos(0F, 16F, 0F);
		setRotation(jewel2, 0.7853982F, 0F, 0F);
		jewel3 = new ModelRenderer(this, 0, 7);
		jewel3.addBox(-4F, -4F, -4F, 8, 8, 8);
		jewel3.setPos(0F, 16F, 0F);
		setRotation(jewel3, 0F, 0F, 0.7853982F);
	}

	public void render(float unitPixel) {
		jewel1.render(unitPixel);
		jewel2.render(unitPixel);
		jewel3.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}

}
