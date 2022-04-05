package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMultipartDummy extends Entity {
	protected float sizePadding = 0.01F;

	//For sync with client.
	//Multiparts do not exist in the world so they need to be synced via the owner entity.
	public static final DataParameter<String> PARENT_PART_NAME = EntityDataManager.defineId(EntityMultipartDummy.class, DataSerializers.STRING);
	public static final DataParameter<Integer> PARENT_OWNER_ID = EntityDataManager.defineId(EntityMultipartDummy.class, DataSerializers.INT);

	private int cachedPartOwnerId = -1;
	private String cachedParentPartName = "";

	private MultiPartEntityPart parent = null;

	public EntityMultipartDummy(World world) {
		super(world);
		this.setSize(0, 0);
	}

	public EntityMultipartDummy(World world, MultiPartEntityPart parent) {
		this(world);
		this.parent = parent;
		this.setPosition(parent.getX(), parent.getY(), parent.getZ());
	}

	public void updatePositioning() {
		if(!this.level.isClientSide() && this.parent != null) {
			this.entityData.set(PARENT_OWNER_ID, this.parent.parent instanceof Entity ? ((Entity) this.parent.parent).getEntityId() : -1); 
			this.entityData.set(PARENT_PART_NAME, this.parent.partName);
		}

		if(this.level.isClientSide()) {
			int partOwnerId = this.entityData.get(PARENT_OWNER_ID);
			String parentPartName = this.entityData.get(PARENT_PART_NAME);

			if(partOwnerId >= 0 && parentPartName.length() > 0 && (this.cachedPartOwnerId != partOwnerId || !this.cachedParentPartName.equals(parentPartName))) {
				Entity parentOwner = this.world.getEntityByID(partOwnerId);

				if(parentOwner != null) {
					for(Entity part : parentOwner.getParts()) {
						if(part instanceof MultiPartEntityPart) {
							if(parentPartName.equals(((MultiPartEntityPart) part).partName)) {
								this.parent = (MultiPartEntityPart) part;
								this.cachedPartOwnerId = partOwnerId;
								this.cachedParentPartName = parentPartName;
							}
						}
					}
				}
			}
		}

		if(this.parent == null || this.parent.parent instanceof Entity == false || !this.parent.isEntityAlive() || !((Entity)this.parent.parent).isEntityAlive()) {
			if(!this.level.isClientSide()) {
				this.remove();
			}
		} else {
			this.setSize(this.parent.width + this.sizePadding, this.parent.height + this.sizePadding);
			this.setPositionAndUpdate(this.parent.getX(), this.parent.getY() - this.sizePadding / 2.0f, this.parent.getZ());
		}
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(PARENT_PART_NAME, "");
		this.entityData.define(PARENT_OWNER_ID, -1);
	}

	public MultiPartEntityPart getParent() {
		return this.parent;
	}

	@Override
	public boolean writeToNBTOptional(CompoundNBT compound) {
		return false; //Don't write to disk
	}

	@Override
	public void tick() {
		this.xOld = this.lastTickPosX = this.getX();
		this.yOld = this.lastTickPosY = this.getY();
		this.zOld = this.lastTickPosZ = this.getZ();

		this.updatePositioning();

		this.firstUpdate = false;
	}

	@Override
	public void onKillCommand() {
		this.remove();
	}


	@Override
	public boolean processInitialInteract(PlayerEntity player, Hand hand) {
		if(this.parent != null) {
			return this.parent.processInitialInteract(player, hand);
		}
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(this.parent != null) {
			return this.parent.hurt(source, amount);
		}
		return false;
	}

	@Override
	public boolean shouldDismountInWater(Entity rider) {
		return false;
	}

	@Override
	public boolean isSilent() {
		return true;
	}

	@Override
	public boolean isInvisible() {
		return true;
	}

	@Override
	public boolean getIsInvulnerable() {
		if(this.parent != null) {
			return this.parent.getIsInvulnerable();
		}
		return true;
	}

	@Override
	public boolean canBeAttackedWithItem() {
		if(this.parent != null) {
			return this.parent.canBeAttackedWithItem();
		}
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		if(this.parent != null) {
			return this.parent.canBeCollidedWith();
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if(this.parent != null) {
			return this.parent.getRenderBoundingBox();
		}
		return super.getRenderBoundingBox();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isInRangeToRender3d(double x, double y, double z) {
		if(this.parent != null) {
			return this.parent.isInRangeToRender3d(x, y, z);
		}
		return super.isInRangeToRender3d(x, y, z);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		if(this.parent != null) {
			return this.parent.isInRangeToRenderDist(distance);
		}
		return super.isInRangeToRenderDist(distance);
	}

	@Override
	protected boolean canBeRidden(Entity entityIn) {
		return false;
	}

	@Override
	public void load(CompoundNBT compound) {

	}

	@Override
	public void save(CompoundNBT compound) {

	}
}
