package thebetweenlands.common.item.food;

import java.util.UUID;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityMireSnailEgg;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemMireSnailEgg extends Item 
{
	public ItemMireSnailEgg(Properties properties) {
		super(properties);
		//super(2, 0.2f, false);
		//this.maxStackSize = 1;
	}

	public static ItemStack fromEgg(EntityMireSnailEgg egg) {
		ItemStack stack = new ItemStack(ItemRegistry.MIRE_SNAIL_EGG.get());
		stack.addTagElement("egg", egg.writeEntityToNBT(new CompoundNBT()));
		return stack;
	}

	@Override
	public ActionResultType onUse(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (world.isClientSide()) {
			return ActionResultType.FAIL;
		}
		MobEntity entity = new EntityMireSnailEgg(world);
		CompoundNBT nbt = stack.getSubCompound("egg");
		if(nbt != null) {
			entity.load(nbt);
		}
		entity.setUUID(UUID.randomUUID());
		entity.moveTo(pos.getX() + hitX + facing.getStepX() * entity.width, pos.getY() + hitY + (facing.getStepY() < 0 ? -entity.height - 0.005F : 0.0F), pos.getZ() + hitZ + facing.getStepZ() * entity.width, 0.0F, 0.0F);
		if(entity.isNotColliding()) {
			world.addFreshEntity(entity);
			entity.playAmbientSound();
			stack.shrink(1);
		}
		return ActionResultType.SUCCESS;
	}
}
