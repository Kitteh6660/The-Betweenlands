package thebetweenlands.common.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityLurkerSkinRaft extends BoatEntity {
	private ItemStack shield = ItemStack.EMPTY;
	private boolean updating = false;

	public EntityLurkerSkinRaft(World worldIn) {
		super(worldIn);
		this.setSize(1.25F, 0.25F);
	}

	public EntityLurkerSkinRaft(World worldIn, double x, double y, double z, ItemStack shield) {
		super(worldIn, x, y,z);
		this.setSize(1.25F, 0.25F);
		this.shield = shield.copy();
	}

	@Override
	public Item getItemBoat() {
		return ItemRegistry.LURKER_SKIN_SHIELD;
	}

	protected ItemStack getBoatDrop() {
		if(!this.shield.isEmpty()) {
			return this.shield.copy();
		}
		return new ItemStack(this.getItemBoat());
	}

	@Override
	public ItemEntity entityDropItem(ItemStack stack, float offsetY) {
		if(stack.getItem() == this.getItemBoat()) {
			return super.entityDropItem(this.getBoatDrop(), offsetY);
		}
		return null;
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
		//Don't break raft
		this.fallDistance = 0;

		super.updateFallState(y, onGroundIn, state, pos);
	}

	@Override
	protected void removePassenger(Entity passenger) {
		if(!this.level.isClientSide() && this.isEntityAlive() && this.getPassengers().indexOf(passenger) == 0) {
			ItemStack drop = this.getBoatDrop();

			boolean itemReturned = false;

			if(passenger instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) passenger;

				if(player.getItemInHand(Hand.OFF_HAND).isEmpty()) {
					player.setItemInHand(Hand.OFF_HAND, drop);
					itemReturned = true;
				} else if(player.getItemInHand(Hand.MAIN_HAND).isEmpty()) {
					player.setItemInHand(Hand.MAIN_HAND, drop);
					itemReturned = true;
				} else {
					itemReturned = player.inventory.addItemStackToInventory(drop);
				}
			}

			if(itemReturned) {
				this.remove();
			}
		}

		super.removePassenger(passenger);
	}

	@Override
	public void tick() {
		this.updating = true;
		super.tick();
		this.updating = false;
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		if(this.updating) {
			x *= 0.4D;
			z *= 0.4D;
		}
		super.move(type, x, y, z);
	}

	@Override
	public void updatePassenger(Entity passenger) {
		super.updatePassenger(passenger);

		if(this.isPassenger(passenger)) {
			float offset = -0.2F;
			float yOffset = (float)((this.isDead ? 0.01D : this.getMountedYOffset()) + passenger.getStepY());

			if(this.getPassengers().size() > 1) {
				int i = this.getPassengers().indexOf(passenger);

				if(i == 0) {
					offset = 0.3F;
				} else {
					offset = -0.4F;
				}

				if (passenger instanceof EntityAnimal) {
					offset = (float)((double)offset + 0.2D);
				}
			}

			Vector3d offsetPos = (new Vector3d((double)offset, 0.0D, 0.0D)).rotateYaw(-this.yRot * 0.017453292F - ((float)Math.PI / 2F));
			passenger.setPosition(this.getX() + offsetPos.x, this.getY() + (double)yOffset, this.getZ() + offsetPos.z);
		}
	}

	@Override
	public void save(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);

		if(!this.shield.isEmpty()) {
			CompoundNBT shieldNbt = new CompoundNBT();
			this.shield.save(shieldNbt);
			nbt.setTag("shield", shieldNbt);
		}
	}

	@Override
	public void load(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);

		this.shield = ItemStack.EMPTY;
		if(nbt.contains("shield", Constants.NBT.TAG_COMPOUND)) {
			CompoundNBT shieldNbt = nbt.getCompoundTag("shield");
			this.shield = new ItemStack(shieldNbt);
		}
	}
}
