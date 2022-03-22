package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntitySmollSludge extends EntitySludge {
    public EntitySmollSludge(World worldIn) {
        super(worldIn);
        this.setSize(0.7F, 0.7F);
    }

    @Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().getAttributeInstance(Attributes.MAX_HEALTH).setBaseValue(14.0D);
		this.getAttributeMap().getAttributeInstance(Attributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		this.getAttributeMap().getAttributeInstance(SLUDGE_TRAIL).setBaseValue(0);
    }
    
    @Override
    protected float getSoundPitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
    }
    
    @Override
    protected void setNormalSize() {
    	this.setSize(0.7F, 0.7F);
    }
}
