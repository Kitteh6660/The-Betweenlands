package thebetweenlands.client.render.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MowzieModelRenderer extends ModelRenderer {
	
    public float initxRot;
    public float inityRot;
    public float initzRot;

    public float initx;
    public float inity;
    public float initz;

    //TODO: Find a replacement for this.
    /*public MowzieModelRenderer(Model modelBase, String name) {
        super(modelBase, name);
    }*/

    public MowzieModelRenderer(Model modelBase, int x, int y) {
        super(modelBase, x, y);
    }

    public MowzieModelRenderer(Model modelBase) {
        super(modelBase);
    }

    public void setInitValuesToCurrentPose() {
        initxRot = xRot;
        inityRot = yRot;
        initzRot = zRot;

        initx = x;
        inity = y;
        initz = z;
    }

    public void setCurrentPoseToInitValues() {
        xRot = initxRot;
        yRot = inityRot;
        zRot = initzRot;

        x = initx;
        y = inity;
        z = initz;
    }

    public void setRotationAngles(float x, float y, float z) {
        xRot = x;
        yRot = y;
        zRot = z;
    }

    /**
     * Resets all rotation points.
     */
    public void resetAllRotationPoints() {
        x = initx;
        y = inity;
        z = initz;
    }

    /**
     * Resets X rotation point.
     */
    public void resetXRotationPoints() {
        x = initx;
    }

    /**
     * Resets Y rotation point.
     */
    public void resetYRotationPoints() {
        y = inity;
    }

    /**
     * Resets Z rotation point.
     */
    public void resetZRotationPoints() {
        z = initz;
    }

    /**
     * Resets all rotations.
     */
    public void resetAllRotations() {
        xRot = initxRot;
        yRot = inityRot;
        zRot = initzRot;
    }

    /**
     * Resets X rotation.
     */
    public void resetXRotations() {
        xRot = initxRot;
    }

    /**
     * Resets Y rotation.
     */
    public void resetYRotations() {
        yRot = inityRot;
    }

    /**
     * Resets Z rotation.
     */
    public void resetZRotations() {
        zRot = initzRot;
    }

    /**
     * Copies the rotation point coordinates.
     */
    public void copyAllRotationPoints(MowzieModelRenderer target) {
        x = target.x;
        y = target.y;
        z = target.z;
    }

    /**
     * Copies X rotation point.
     */
    public void copyXRotationPoint(MowzieModelRenderer target) {
        x = target.x;
    }

    /**
     * Copies Y rotation point.
     */
    public void copyYRotationPoint(MowzieModelRenderer target) {
        y = target.y;
    }

    /**
     * Copies Z rotation point.
     */
    public void copyZRotationPoint(MowzieModelRenderer target) {
        z = target.z;
    }
}
