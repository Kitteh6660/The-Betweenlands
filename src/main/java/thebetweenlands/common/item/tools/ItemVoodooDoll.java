package thebetweenlands.common.item.tools;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IBLBoss;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemVoodooDoll extends Item {
	
	public ItemVoodooDoll() {
		maxStackSize = 1;
		setMaxDamage(24);
		setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 40;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity user) {
		if(user instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) user;

			List<LivingEntity> living = world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(player.getX(), player.getY(), player.getZ(), player.getX(), player.getY(), player.getZ()).grow(5, 5, 5));
			living.remove(player);

			boolean attacked = false;
			for (LivingEntity entity : living) {
				if (entity.isEntityAlive() && !(entity instanceof IBLBoss) && entity instanceof PlayerEntity == false) {
					DamageSource source = new EntityDamageSource("magic", user).setDamageBypassesArmor().setMagicDamage();
					if (!world.isClientSide()) {
						attacked |= entity.attackEntityFrom(source, 20);
					} else if (!entity.isEntityInvulnerable(source)) {
						attacked = true;
						for (int i = 0; i < 20; i++) {
							BLParticles.SWAMP_SMOKE.spawn(world, entity.getX(), entity.getY() + entity.height / 2.0D, entity.getZ(), ParticleFactory.ParticleArgs.get().withMotion((world.rand.nextFloat() - 0.5F) * 0.5F, (world.rand.nextFloat() - 0.5F) * 0.5F, (world.rand.nextFloat() - 0.5F) * 0.5F).withColor(1, 1, 1, 1));
						}
					}
				}
			}

			if (!world.isClientSide() && attacked) {
				stack.hurtAndBreak(1, player, (entity) -> {
					entity.broadcastBreakEvent(player.getUsedItemHand());
				});
				world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.VOODOO_DOLL, SoundCategory.PLAYERS, 0.5F, 1.0F - world.rand.nextFloat() * 0.3F);
			}
		}

		return stack;
	}

	@Override
	public UseAction getItemUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getItemInHand(handIn));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}
}
