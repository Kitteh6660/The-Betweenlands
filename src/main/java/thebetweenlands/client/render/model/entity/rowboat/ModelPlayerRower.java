package thebetweenlands.client.render.model.entity.rowboat;

public class PlayerModelRower extends ModelBipedRower {
    public PlayerModelRower(float expand, boolean slimArms, BipedTextureUVs limbUVs) {
        this(expand, false, slimArms, limbUVs);
    }
    public PlayerModelRower(float expand, boolean expandJointed, boolean slimArms, BipedTextureUVs limbUVs) {
        super(expand, expandJointed, slimArms,  64, 64, limbUVs);
    }
}