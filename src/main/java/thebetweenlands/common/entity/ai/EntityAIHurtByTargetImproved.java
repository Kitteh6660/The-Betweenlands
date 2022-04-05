package thebetweenlands.common.entity.ai;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIHurtByTargetImproved extends EntityAITarget {
    boolean entityCallsForHelp;
    private int revengeTimer;

    public EntityAIHurtByTargetImproved(EntityCreature entity, boolean callsHelp) {
        super(entity, false);
        this.entityCallsForHelp = callsHelp;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
	public boolean canUse() {
        int i = this.taskOwner.getRevengeTimer();
        LivingEntity entitylivingbase = this.taskOwner.getRevengeTarget();
        return i != this.revengeTimer && entitylivingbase != null && this.isSuitableTarget(entitylivingbase, false);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
	public void start() {
    	this.revengeTimer = this.taskOwner.getRevengeTimer();
    	
        this.taskOwner.setAttackTarget(this.taskOwner.getRevengeTarget());
        
        if(this.taskOwner.getAttackTarget() != null && this.entityCallsForHelp) {
            double dist = this.getTargetDistance();
            
            List<EntityCreature> list = this.taskOwner.world.getEntitiesOfClass(this.taskOwner.getClass(), new AxisAlignedBB(this.taskOwner.getX(), this.taskOwner.getY(), this.taskOwner.getZ(), this.taskOwner.getX() + 1.0D, this.taskOwner.getY() + 1.0D, this.taskOwner.getZ() + 1.0D).inflate(dist, 10.0D, dist));
            for (EntityCreature creature : list) {
                if (this.taskOwner != creature && creature.getAttackTarget() == null && !creature.isOnSameTeam(this.taskOwner.getAttackTarget()) && creature != this.taskOwner.getAttackTarget()) {
                    creature.setAttackTarget(this.taskOwner.getRevengeTarget());
                }
            }
        }
        
        super.start();
    }
    
    @Override
    public void stop() {
    	this.target = null;
    }
}