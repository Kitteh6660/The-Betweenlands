package thebetweenlands.client.render.model.entity.rowboat;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.entity.rowboat.RowboatLantern;

public class ModelLantern extends Model {
	
	private ModelRenderer root;

	public ModelLantern() {
		super();
		texWidth = 256;
		texHeight = 128;
		root = new ModelRenderer(this, 218, 11);
		//root.setPos(0.0F, 4.0F, 1.0F);
		root.addBox(-2.5F, 0.0F, -2.5F, 5, 7, 5, 0.0F);
		root.texOffs(239, 13);
		root.addBox(-1.5F, 2.0F, -1.5F, 3, 4, 3, 0.0F);
		ModelRenderer lanternTop = new ModelRenderer(this, 218, 24);
		lanternTop.setPos(0.0F, 0.5F, 0.0F);
		lanternTop.xRot = 0.13F;
		lanternTop.addBox(-3.0F, -1.0F, -3.0F, 6, 2, 6, 0.0F);
		root.addChild(lanternTop);
	}

	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.xRot = lantern.getAngle(delta);
		GlStateManager.disableCull();
		root.render(scale);
		GlStateManager.enableCull();
	}
}
