package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;

public class ItemCavingRope extends Item {
	public ItemCavingRope() {
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	public ActionResultType onItemUse( PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = player.getItemInHand(hand);
		if(!level.isClientSide()) {
			EntityRopeNode connectedRopeNode = null;
			for(Entity e : (List<Entity>) world.loadedEntityList) {
				if(e instanceof EntityRopeNode) {
					EntityRopeNode ropeNode = (EntityRopeNode) e;
					if(ropeNode.getNextNodeByUUID() == player) {
						connectedRopeNode = ropeNode;
						break;
					}
				}
			}
			if(connectedRopeNode == null) {
				EntityRopeNode ropeNode = new EntityRopeNode(world);
				ropeNode.moveTo(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, 0, 0);
				ropeNode.setNextNode(player);
				world.addFreshEntity(ropeNode);
				if (player instanceof ServerPlayerEntity)
					AdvancementCriterionRegistry.CAVINGROPE_PLACED.trigger((ServerPlayerEntity) player);
				world.playLocalSound((PlayerEntity)null, ropeNode.getX(), ropeNode.getY(), ropeNode.getZ(), SoundEvents.BLOCK_METAL_STEP, SoundCategory.PLAYERS, 1, 1.5F);
				stack.shrink(1);
			} else {
				if(connectedRopeNode.getDistance(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ) > EntityRopeNode.ROPE_LENGTH) {
					player.displayClientMessage(new TranslationTextComponent("chat.rope.too_far"), true);
					
					return ActionResultType.FAIL;
				} else {
					EntityRopeNode ropeNode = connectedRopeNode.extendRope(player, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ);
					world.playLocalSound((PlayerEntity)null, ropeNode.getX(), ropeNode.getY(), ropeNode.getZ(), SoundEvents.BLOCK_METAL_STEP, SoundCategory.PLAYERS, 1, 1.5F);
					stack.shrink(1);
				}
			}
		}
		
		return ActionResultType.SUCCESS;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.caving_rope", KeyBindRegistry.CONNECT_CAVING_ROPE.getDisplayName(), StringUtils.ticksToElapsedTime(BetweenlandsConfig.GENERAL.cavingRopeDespawnTime * 20)), 0));
	}
}
