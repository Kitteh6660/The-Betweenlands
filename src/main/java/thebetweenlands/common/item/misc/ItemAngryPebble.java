package thebetweenlands.common.item.misc;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.UseAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.AngryPebbleEntity;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemAngryPebble extends Item {
	public ItemAngryPebble() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
		this.addPropertyOverride(new ResourceLocation("charge"), (stack, worldIn, entityIn) ->
				entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? (float)(stack.getUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F);
		this.addPropertyOverride(new ResourceLocation("charging"), (stack, worldIn, entityIn) ->
				entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		playerIn.setActiveHand(handIn);
		worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.PLAYERS, 1.0F, 0.5F);
		return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getItemInHand(handIn));
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
		if (player instanceof PlayerEntity) {
			Vector3d forward = player.getLookVec();
			float yaw = player.yRot;
			float pitch = player.xRot - 90;
			float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
			float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
			float f2 = -MathHelper.cos(-pitch * 0.017453292F);
			float f3 = MathHelper.sin(-pitch * 0.017453292F);
			Vector3d up = new Vector3d((double)(f1 * f2), (double)f3, (double)(f * f2));
			Vector3d right = forward.cross(up).normalize();
			Vector3d source = player.getDeltaMovement().add(0, player.getEyeHeight() - 0.2F, 0).add(forward.scale(0.4F)).add(right.scale(0.3F));

			for(int i = 0; i < 5; i++) {
				player.world.addParticle(ParticleTypes.SMOKE_NORMAL, source.x + player.level.random.nextFloat() * 0.5F - 0.25F, source.y + player.level.random.nextFloat() * 0.5F - 0.25F, source.z + player.level.random.nextFloat() * 0.5F - 0.25F, 0, 0, 0);
			}
		}

		super.onUsingTick(stack, player, count);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if (!worldIn.isClientSide() && entityLiving instanceof PlayerEntity) {
			int useTime = this.getUseDuration(stack) - timeLeft;

			if(useTime > 20) {
				worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundRegistry.SORRY, SoundCategory.PLAYERS, 0.7F, 0.8F);
				AngryPebbleEntity pebble = new AngryPebbleEntity(worldIn, entityLiving);
				pebble.shoot(entityLiving, entityLiving.xRot, entityLiving.yRot, -10, 1.2F, 3.5F);
				worldIn.addFreshEntity(pebble);

				if(!((PlayerEntity)entityLiving).isCreative()) {
					stack.shrink(1);
				}
			}
		}
	}
}