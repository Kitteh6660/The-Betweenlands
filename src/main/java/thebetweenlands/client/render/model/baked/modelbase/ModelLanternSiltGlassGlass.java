package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLLanternSiltGlass_inner - TripleHeadedSheep
 * Created using Tabula 7.0.1, updated for 1.16.5
 */
public class ModelLanternSiltGlassGlass extends Model {
	
    public ModelRenderer lamp_base;

    public ModelLanternSiltGlassGlass() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 32;
        this.lamp_base = new ModelRenderer(this, 0, 0);
        this.lamp_base.setPos(0.0F, 16.0F, 0.0F);
        this.lamp_base.addBox(-2.5F, 0.0F, -2.5F, 5, 6, 5, 0.0F);
    }

    @Override
    public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) {  
        this.lamp_base.render(matrix, vertex, in1, in2, f, f1, f2, f3);
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
