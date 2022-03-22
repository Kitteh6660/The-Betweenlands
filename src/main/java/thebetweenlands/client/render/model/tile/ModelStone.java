package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelStone extends Model {

    private final ModelRenderer stone4;
    private final ModelRenderer stone1;
    private final ModelRenderer stone2;
    private final ModelRenderer stone3;

    public ModelStone() {
        textureWidth = 256;
        textureHeight = 128;

        stone4 = new ModelRenderer(this, 160, 30);
        stone4.addBox(-1F, -1F, -2F, 3, 3, 3);
        stone4.setPos(-2F, -4F, 5F);
        setRotation(stone4, 0.7807508F, -0.7261189F, 0.3346075F);
        stone1 = new ModelRenderer(this, 160, 0);
        stone1.addBox(-1F, -1F, -1F, 4, 4, 4);
        stone1.setPos(0F, -12F, -2F);
        setRotation(stone1, 0.5711987F, 0.3046393F, -0.1903996F);
        stone2 = new ModelRenderer(this, 160, 10);
        stone2.addBox(-1F, -2F, -2F, 3, 3, 3);
        stone2.setPos(-4F, -4F, -3F);
        setRotation(stone2, -0.4089647F, 0F, 0.2602503F);
        stone3 = new ModelRenderer(this, 160, 18);
        stone3.addBox(-2F, -2F, -2F, 5, 5, 5);
        stone3.setPos(4F, 1F, -1F);
        setRotation(stone3, 0F, -0.4089647F, 0.3892394F);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }

    public void renderAll() {
        stone4.render(0.0625F);
        stone1.render(0.0625F);
        stone2.render(0.0625F);
        stone3.render(0.0625F);
    }
}
