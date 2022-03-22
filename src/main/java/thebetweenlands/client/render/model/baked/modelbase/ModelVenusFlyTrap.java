package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLVenusFlytrap - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelVenusFlyTrap extends Model {
	
    public ModelRenderer leaf1;
    public ModelRenderer leaf2;
    public ModelRenderer leaf3;
    public ModelRenderer leaf4;
    public ModelRenderer leaf5;
    public ModelRenderer head6;
    public ModelRenderer head7;
    public ModelRenderer head8;
    public ModelRenderer stalk1;
    public ModelRenderer stalk2;
    public ModelRenderer leaf1b;
    public ModelRenderer head1a;
    public ModelRenderer head1b;
    public ModelRenderer leaf2b;
    public ModelRenderer head2a;
    public ModelRenderer head2b;
    public ModelRenderer leaf3b;
    public ModelRenderer head3a;
    public ModelRenderer head3b;
    public ModelRenderer leaf4b;
    public ModelRenderer head4a;
    public ModelRenderer head4b;
    public ModelRenderer leaf5b;
    public ModelRenderer head5a;
    public ModelRenderer head5b;
    public ModelRenderer head6a;
    public ModelRenderer head6b;
    public ModelRenderer head7a;
    public ModelRenderer head7b;
    public ModelRenderer head8a;
    public ModelRenderer head8b;
    public ModelRenderer flower1;
    public ModelRenderer flower2;

    public ModelVenusFlyTrap() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 32;
        this.head1b = new ModelRenderer(this, 19, 9);
        this.head1b.setPos(0.0F, 0.3F, -3.5F);
        this.head1b.addBox(0.0F, -2.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(head1b, -0.27314402793711257F, -0.136659280431156F, -0.5462880558742251F);
        this.head7a = new ModelRenderer(this, 30, 21);
        this.head7a.setPos(0.0F, 0.3F, 0.0F);
        this.head7a.addBox(-1.0F, -2.0F, -2.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(head7a, 0.0F, 0.0F, 0.5009094953223726F);
        this.head5b = new ModelRenderer(this, 37, 10);
        this.head5b.setPos(0.0F, 0.3F, -1.5F);
        this.head5b.addBox(0.0F, -2.0F, -2.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(head5b, 0.0F, 0.0F, -0.5009094953223726F);
        this.flower1 = new ModelRenderer(this, 40, 0);
        this.flower1.setPos(0.0F, 0.0F, 0.0F);
        this.flower1.addBox(-1.0F, -6.5F, -1.0F, 2, 4, 2, 0.0F);
        this.flower2 = new ModelRenderer(this, 49, 0);
        this.flower2.setPos(0.0F, 0.0F, 0.0F);
        this.flower2.addBox(-1.0F, -4.5F, -1.0F, 2, 4, 2, 0.0F);
        this.head8 = new ModelRenderer(this, 20, 0);
        this.head8.setPos(-1.0F, 24.5F, 1.5F);
        this.head8.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(head8, 0.091106186954104F, 2.5497515042385164F, 0.136659280431156F);
        this.leaf5b = new ModelRenderer(this, 10, 3);
        this.leaf5b.setPos(0.0F, 0.0F, -2.0F);
        this.leaf5b.addBox(-1.5F, 0.0F, -2.0F, 3, 0, 2, 0.0F);
        this.setRotateAngle(leaf5b, 0.5462880558742251F, 0.0F, 0.0F);
        this.head8a = new ModelRenderer(this, 30, 26);
        this.head8a.setPos(0.0F, 0.3F, 0.0F);
        this.head8a.addBox(-1.0F, -2.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(head8a, 0.0F, 0.0F, 0.8196066167365371F);
        this.leaf1 = new ModelRenderer(this, 0, 0);
        this.leaf1.setPos(0.5F, 24.0F, -0.5F);
        this.leaf1.addBox(-1.5F, 0.0F, -3.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf1, -0.40980330836826856F, -0.6373942428283291F, 0.0F);
        this.leaf4b = new ModelRenderer(this, 0, 29);
        this.leaf4b.setPos(0.0F, 0.0F, -4.0F);
        this.leaf4b.addBox(-1.5F, 0.0F, -3.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf4b, 0.6373942428283291F, 0.0F, 0.0F);
        this.stalk2 = new ModelRenderer(this, 35, 0);
        this.stalk2.setPos(0.0F, 24.0F, 1.0F);
        this.stalk2.addBox(-0.5F, -4.0F, -0.5F, 1, 5, 1, 0.0F);
        this.setRotateAngle(stalk2, -0.36425021489121656F, 0.0F, 0.0F);
        this.leaf4 = new ModelRenderer(this, -1, 24);
        this.leaf4.setPos(0.0F, 24.0F, 0.5F);
        this.leaf4.addBox(-1.5F, 0.0F, -4.0F, 3, 0, 4, 0.0F);
        this.setRotateAngle(leaf4, -0.4553564018453205F, -2.9595548126067843F, 0.0F);
        this.head4b = new ModelRenderer(this, 19, 26);
        this.head4b.setPos(0.0F, 0.3F, -2.5F);
        this.head4b.addBox(0.0F, -2.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(head4b, -0.27314402793711257F, -0.27314402793711257F, -0.8196066167365371F);
        this.leaf2 = new ModelRenderer(this, 0, 9);
        this.leaf2.setPos(-0.5F, 24.0F, 0.0F);
        this.leaf2.addBox(-1.5F, 0.0F, -3.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf2, -0.36425021489121656F, 0.31869712141416456F, 0.0F);
        this.leaf3 = new ModelRenderer(this, 1, 17);
        this.leaf3.setPos(-0.5F, 24.0F, 0.0F);
        this.leaf3.addBox(-1.5F, 0.0F, -2.0F, 3, 0, 2, 0.0F);
        this.setRotateAngle(leaf3, -0.27314402793711257F, 2.1399481958702475F, 0.0F);
        this.head2a = new ModelRenderer(this, 10, 15);
        this.head2a.setPos(0.0F, 0.3F, -2.5F);
        this.head2a.addBox(-1.0F, -2.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(head2a, -0.136659280431156F, 0.091106186954104F, 0.5462880558742251F);
        this.head6 = new ModelRenderer(this, 20, 0);
        this.head6.setPos(1.0F, 24.5F, 0.0F);
        this.head6.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(head6, 0.0F, -1.3203415791337103F, 0.0F);
        this.leaf3b = new ModelRenderer(this, 0, 20);
        this.leaf3b.setPos(0.0F, 0.0F, -2.0F);
        this.leaf3b.addBox(-1.5F, 0.0F, -3.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf3b, 0.31869712141416456F, 0.0F, 0.0F);
        this.head6a = new ModelRenderer(this, 30, 15);
        this.head6a.setPos(0.0F, 0.3F, 0.0F);
        this.head6a.addBox(-1.0F, -2.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(head6a, -0.091106186954104F, 0.045553093477052F, 0.6829473363053812F);
        this.head1a = new ModelRenderer(this, 10, 9);
        this.head1a.setPos(0.0F, 0.3F, -3.5F);
        this.head1a.addBox(-1.0F, -2.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(head1a, -0.27314402793711257F, 0.136659280431156F, 0.5462880558742251F);
        this.head2b = new ModelRenderer(this, 19, 15);
        this.head2b.setPos(0.0F, 0.3F, -2.5F);
        this.head2b.addBox(0.0F, -2.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(head2b, -0.136659280431156F, -0.091106186954104F, -0.5462880558742251F);
        this.head7b = new ModelRenderer(this, 37, 21);
        this.head7b.setPos(0.0F, 0.3F, 0.0F);
        this.head7b.addBox(0.0F, -2.0F, -2.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(head7b, 0.0F, 0.0F, -0.5009094953223726F);
        this.leaf1b = new ModelRenderer(this, -1, 4);
        this.leaf1b.setPos(0.0F, 0.0F, -3.0F);
        this.leaf1b.addBox(-1.5F, 0.0F, -4.0F, 3, 0, 4, 0.0F);
        this.setRotateAngle(leaf1b, 0.5009094953223726F, 0.0F, 0.0F);
        this.leaf5 = new ModelRenderer(this, 10, 0);
        this.leaf5.setPos(0.5F, 24.0F, 0.5F);
        this.leaf5.addBox(-1.5F, 0.0F, -2.0F, 3, 0, 2, 0.0F);
        this.setRotateAngle(leaf5, -0.5462880558742251F, -2.0943951023931953F, 0.0F);
        this.stalk1 = new ModelRenderer(this, 30, 0);
        this.stalk1.setPos(0.0F, 24.0F, 0.0F);
        this.stalk1.addBox(-0.5F, -6.0F, -0.5F, 1, 7, 1, 0.0F);
        this.setRotateAngle(stalk1, 0.091106186954104F, 0.0F, -0.18203784098300857F);
        this.head5a = new ModelRenderer(this, 30, 10);
        this.head5a.setPos(0.0F, 0.3F, -1.5F);
        this.head5a.addBox(-1.0F, -2.0F, -2.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(head5a, 0.0F, 0.0F, 0.5009094953223726F);
        this.head3b = new ModelRenderer(this, 17, 21);
        this.head3b.setPos(0.0F, 0.3F, -2.5F);
        this.head3b.addBox(0.0F, -2.0F, -2.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(head3b, 0.0F, 0.0F, -0.7740535232594852F);
        this.head8b = new ModelRenderer(this, 39, 26);
        this.head8b.setPos(0.0F, 0.3F, 0.0F);
        this.head8b.addBox(0.0F, -2.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(head8b, 0.0F, 0.0F, -0.8196066167365371F);
        this.head6b = new ModelRenderer(this, 39, 15);
        this.head6b.setPos(0.0F, 0.3F, 0.0F);
        this.head6b.addBox(0.0F, -2.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(head6b, -0.091106186954104F, -0.045553093477052F, -0.6829473363053812F);
        this.head4a = new ModelRenderer(this, 10, 26);
        this.head4a.setPos(0.0F, 0.3F, -2.5F);
        this.head4a.addBox(-1.0F, -2.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(head4a, -0.27314402793711257F, 0.27314402793711257F, 0.8196066167365371F);
        this.head7 = new ModelRenderer(this, 20, 0);
        this.head7.setPos(-2.0F, 24.5F, -0.5F);
        this.head7.addBox(-0.5F, 0.0F, -0.6F, 1, 1, 1, 0.0F);
        this.setRotateAngle(head7, 0.136659280431156F, 1.1383037381507017F, 0.0F);
        this.leaf2b = new ModelRenderer(this, 0, 13);
        this.leaf2b.setPos(0.0F, 0.0F, -3.0F);
        this.leaf2b.addBox(-1.5F, 0.0F, -3.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf2b, 0.31869712141416456F, 0.0F, 0.0F);
        this.head3a = new ModelRenderer(this, 10, 21);
        this.head3a.setPos(0.0F, 0.3F, -2.5F);
        this.head3a.addBox(-1.0F, -2.0F, -2.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(head3a, 0.0F, 0.0F, 0.7740535232594852F);
        this.leaf1b.addChild(this.head1b);
        this.head7.addChild(this.head7a);
        this.leaf5b.addChild(this.head5b);
        this.stalk1.addChild(this.flower1);
        this.stalk2.addChild(this.flower2);
        this.leaf5.addChild(this.leaf5b);
        this.head8.addChild(this.head8a);
        this.leaf4.addChild(this.leaf4b);
        this.leaf4b.addChild(this.head4b);
        this.leaf2b.addChild(this.head2a);
        this.leaf3.addChild(this.leaf3b);
        this.head6.addChild(this.head6a);
        this.leaf1b.addChild(this.head1a);
        this.leaf2b.addChild(this.head2b);
        this.head7.addChild(this.head7b);
        this.leaf1.addChild(this.leaf1b);
        this.leaf5b.addChild(this.head5a);
        this.leaf3b.addChild(this.head3b);
        this.head8.addChild(this.head8b);
        this.head6.addChild(this.head6b);
        this.leaf4b.addChild(this.head4a);
        this.leaf2.addChild(this.leaf2b);
        this.leaf3b.addChild(this.head3a);
    }

    @Override
    public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) {  
        this.head8.render(matrix, vertex, in1, in2, f, f1, f2, f3);
        this.leaf1.render(matrix, vertex, in1, in2, f, f1, f2, f3);
        this.stalk2.render(matrix, vertex, in1, in2, f, f1, f2, f3);
        this.leaf4.render(matrix, vertex, in1, in2, f, f1, f2, f3);
        this.leaf2.render(matrix, vertex, in1, in2, f, f1, f2, f3);
        this.leaf3.render(matrix, vertex, in1, in2, f, f1, f2, f3);
        this.head6.render(matrix, vertex, in1, in2, f, f1, f2, f3);
        this.leaf5.render(matrix, vertex, in1, in2, f, f1, f2, f3);
        this.stalk1.render(matrix, vertex, in1, in2, f, f1, f2, f3);
        this.head7.render(matrix, vertex, in1, in2, f, f1, f2, f3);
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
