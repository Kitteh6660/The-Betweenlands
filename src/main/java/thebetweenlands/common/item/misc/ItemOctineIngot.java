package thebetweenlands.common.item.misc;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLItemTags;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemOctineIngot extends Item 
{
	public ItemOctineIngot(Properties properties) {
		super(properties);
		//this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.get("tooltip.bl.octine.fire"));
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
		ItemStack itemStackIn = playerIn.getItemInHand(hand);
		RayTraceResult result = this.rayTrace(worldIn, playerIn, true);
		if(result != null && result.getType() == Type.BLOCK) {
			BlockPos offsetPos = result.getLocation().add(result.sideHit);
			boolean hasTinder = false;
			boolean isBlockTinder = false;
			BlockState blockState = worldIn.getBlockState(result.getBlockPos());
			if(((ItemOctineIngot)itemStackIn.getItem()).isTinder(itemStackIn, ItemStack.EMPTY, blockState)) {
				hasTinder = true;
				isBlockTinder = true;
			} else {
				List<ItemEntity> tinder = worldIn.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(offsetPos), entity -> !entity.getItem().isEmpty() && ((ItemOctineIngot)itemStackIn.getItem()).isTinder(itemStackIn, entity.getItem(), null));
				if(!tinder.isEmpty()) {
					hasTinder = true;
				}
			}
			if((hasTinder || isBlockTinder) && blockState.getBlock() != Blocks.FIRE) {
				playerIn.setActiveHand(hand);
				return new ActionResult<>(ActionResultType.SUCCESS, itemStackIn);
			}
		}
		return new ActionResult<>(ActionResultType.FAIL, itemStackIn);
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity playerIn, int count) {
		if(playerIn instanceof PlayerEntity) {
			World worldIn = playerIn.world;
			RayTraceResult result = this.rayTrace(worldIn, (PlayerEntity) playerIn, true);
			if(result != null && result.typeOfHit == Type.BLOCK) {
				BlockPos pos = result.getBlockPos();
				BlockPos offsetPos = pos.offset(result.sideHit);
				boolean hasTinder = false;
				boolean isBlockTinder = false;
				BlockState blockState = worldIn.getBlockState(pos);
				if(((ItemOctineIngot)stack.getItem()).isTinder(stack, ItemStack.EMPTY, blockState)) {
					hasTinder = true;
					isBlockTinder = true;
				} else {
					List<ItemEntity> tinder = worldIn.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(offsetPos), entity -> !entity.getItem().isEmpty() && ((ItemOctineIngot)stack.getItem()).isTinder(stack, entity.getItem(), null));
					if(!tinder.isEmpty()) {
						hasTinder = true;
					}
				}
				if(hasTinder) {
					if(worldIn.random.nextInt(count / 10 + 1) == 0) {
						worldIn.addParticle(ParticleTypes.SMOKE, 
								result.hitVec.x + worldIn.random.nextFloat()*0.2-0.1, 
								result.hitVec.y + worldIn.random.nextFloat()*0.2-0.1, 
								result.hitVec.z + worldIn.random.nextFloat()*0.2-0.1, 0, 0.1, 0);
						worldIn.addParticle(ParticleTypes.FLAME, 
								result.hitVec.x + worldIn.random.nextFloat()*0.2-0.1, 
								result.hitVec.y + worldIn.random.nextFloat()*0.2-0.1, 
								result.hitVec.z + worldIn.random.nextFloat()*0.2-0.1, 0, 0.1, 0);
					}
					if(!worldIn.isClientSide()) {
						if(count <= 1) {
							if(playerIn instanceof ServerPlayerEntity) {
								AdvancementCriterionRegistry.OCTINE_INGOT_FIRE.trigger((ServerPlayerEntity)playerIn);
								
								if(worldIn.getBlockState(isBlockTinder ? pos.below() : offsetPos.below()).getBlock() == BlockRegistry.PEAT) {
									AdvancementCriterionRegistry.PEAT_FIRE.trigger((ServerPlayerEntity)playerIn);
								}
							}
							
							if(isBlockTinder) {
								worldIn.setBlockState(pos, Blocks.FIRE.defaultBlockState());
							} else {
								if(worldIn.getBlockState(offsetPos).getMaterial().isReplaceable()) {
									worldIn.setBlockState(offsetPos, Blocks.FIRE.defaultBlockState());
								}
							}
							worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
						}
					}
				}
			}
		}
	}

	public boolean isTinder(ItemStack ingot, ItemStack stack, @Nullable BlockState blockState) {
		if(blockState != null) {
			Block block = blockState.getBlock();
			return block == BlockRegistry.CAVE_MOSS.get() || 
					block == BlockRegistry.MOSS.get() ||
					block == BlockRegistry.LICHEN.get() ||
					block == BlockRegistry.DEAD_MOSS.get() ||
					block == BlockRegistry.DEAD_LICHEN.get() ||
					block == BlockRegistry.THORNS.get();
		}
		if(!stack.isEmpty()) {
			if(stack.getItem() instanceof BlockItem) {
				BlockItem itemBlock = (BlockItem) stack.getItem();
				return isTinder(ingot, ItemStack.EMPTY, itemBlock.getBlock().defaultBlockState());
			}
			return stack.getItem().is(BLItemTags.TINDER);
		}
		return false;
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}
}
