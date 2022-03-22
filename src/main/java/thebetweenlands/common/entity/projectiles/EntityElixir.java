package thebetweenlands.common.entity.projectiles;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.projectile.EntityThrowable;

import net.minecraft.nbt.CompoundNBT;
import thebetweenlands.api.event.SplashPotionEvent;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityElixir extends EntityThrowable {
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityElixir.class, DataSerializers.ITEM_STACK);

    public EntityElixir(World world) {
        super(world);
    }

    public EntityElixir(World world, LivingEntity thrower, ItemStack elixir) {
        super(world, thrower);
        this.setItem(elixir.copy());
    }

    @Override
	protected void defineSynchedData() {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    public ItemStack getElixirStack() {
        return this.getDataManager().get(ITEM);
    }

    public void setItem(ItemStack stack) {
        this.getDataManager().set(ITEM, stack);
        this.getDataManager().setDirty(ITEM);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.05F;
    }
    
    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.level.isClientSide()) {
            AxisAlignedBB hitBB = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
            List<LivingEntity> hitEntities = this.world.getEntitiesOfClass(LivingEntity.class, hitBB);
            if (!hitEntities.isEmpty()) {
                Iterator<LivingEntity> hitEntitiesIT = hitEntities.iterator();
                while (hitEntitiesIT.hasNext()) {
                    LivingEntity affectedEntity = hitEntitiesIT.next();
                    double entityDst = this.getDistanceSq(affectedEntity);
                    if (entityDst < 16.0D) {
                        double modifier = 1.0D - Math.sqrt(entityDst) / 4.0D;
                        if (affectedEntity == result.entityHit) {
                            modifier = 1.0D;
                        }
                        PotionEffect effect = ItemRegistry.ELIXIR.createPotionEffect(getElixirStack(), modifier);
                        SplashPotionEvent event = new SplashPotionEvent(this, affectedEntity, effect, effect.getPotion().isInstant());
                        MinecraftForge.EVENT_BUS.post(event);
                        if(!event.isCanceled()) {
                        	affectedEntity.addEffect(effect);
                        }
                    }
                }
            }
            this.world.playEvent(2002, new BlockPos(this), ItemRegistry.ELIXIR.getColorMultiplier(getElixirStack(), 0));
            this.entityDropItem(new ItemStack(ItemRegistry.ELIXIR.getDentrothystType(getElixirStack()) == EnumDentrothyst.GREEN ? ItemRegistry.DENTROTHYST_SHARD_GREEN : ItemRegistry.DENTROTHYST_SHARD_ORANGE,
            		this.world.rand.nextInt(2) + 2), this.height / 2);
            if(this.world.rand.nextInt(2) == 0) this.entityDropItem(EnumItemMisc.RUBBER_BALL.create(1), this.height / 2);
            this.remove();
        }
    }

    @Override
    public void readEntityFromNBT(CompoundNBT nbt) {
        super.readEntityFromNBT(nbt);
    	ItemStack itemstack = new ItemStack(nbt.getCompoundTag("elixir"));

        if (itemstack.isEmpty()) {
            this.remove();
        } else {
            this.setItem(itemstack);
        }
    }

    @Override
    public void writeEntityToNBT(CompoundNBT nbt) {
        super.writeEntityToNBT(nbt);
        ItemStack stack = getElixirStack();
        if (!stack.isEmpty()) {
            nbt.setTag("elixir", stack.save(new CompoundNBT()));
        }
    }
}
