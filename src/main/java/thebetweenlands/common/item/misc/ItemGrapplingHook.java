package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityGrapplingHookNode;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemGrapplingHook extends Item {
	public static final int MIN_GRAPPLING_HOOK_LENGTH = 16;
	public static final int MAX_GRAPPLING_HOOK_LENGTH = 32;

	public ItemGrapplingHook() {
		this.setCreativeTab(BLCreativeTabs.GEARS);

		this.setMaxStackSize(1);
		this.setMaxDamage(MAX_GRAPPLING_HOOK_LENGTH - MIN_GRAPPLING_HOOK_LENGTH);
		this.setNoRepair();
		
		this.addPropertyOverride(new ResourceLocation("grappling_hook_length"), (stack, worldIn, entityIn) -> MIN_GRAPPLING_HOOK_LENGTH + stack.getDamageValue());
		this.addPropertyOverride(new ResourceLocation("extended"), (stack, worldIn, entityIn) -> {
			if(entityIn != null && entityIn.getRidingEntity() instanceof EntityGrapplingHookNode) {
				boolean isMainHand = stack == entityIn.getItemInHand(Hand.MAIN_HAND);
				boolean isOffHand = stack == entityIn.getItemInHand(Hand.OFF_HAND);
				boolean hasOffHand = !entityIn.getItemInHand(Hand.OFF_HAND).isEmpty() && entityIn.getItemInHand(Hand.OFF_HAND).getItem() instanceof ItemGrapplingHook;
				return (isMainHand || isOffHand) && ((isMainHand && !hasOffHand) || isOffHand) ? 1 : 0;
			}
			return 0;
		});
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if(!level.isClientSide()) {
			if(!player.isRiding()) {
				ItemStack stack = player.getItemInHand(hand);

				Vector3d dir = player.getLookVec();

				int maxNodes = MIN_GRAPPLING_HOOK_LENGTH + stack.getDamageValue();
				int thrownNodes = maxNodes / 2;

				EntityGrapplingHookNode mountNode = new EntityGrapplingHookNode(world, thrownNodes + 1, maxNodes);
				mountNode.moveTo(player.getX() - player.width / 2, player.getY(), player.getZ() - player.width / 2, 0, 0);
				mountNode.motionX = player.motionX;
				mountNode.motionY = player.motionY;
				mountNode.motionZ = player.motionZ;

				player.startRiding(mountNode);

				EntityGrapplingHookNode prevNode = null;

				for(int i = 0; i < thrownNodes; i++) {
					EntityGrapplingHookNode node = new EntityGrapplingHookNode(world);
					node.moveTo(player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), 0, 0);

					float velocity = 1.5F * (0.4F + 1F * i / (float)thrownNodes) / (float)MAX_GRAPPLING_HOOK_LENGTH * maxNodes;
					float upwardsVelocity = 1.0F * (0.4F + 0.6F * (float) Math.sin(Math.PI / 2 / thrownNodes * i)) / (float)MAX_GRAPPLING_HOOK_LENGTH * maxNodes;

					node.motionX = player.motionX + dir.x * velocity;
					node.motionY = player.motionY + dir.y * velocity + upwardsVelocity + 0.5D;
					node.motionZ = player.motionZ + dir.z * velocity;

					if(prevNode == null) {
						node.setNextNode(mountNode);
						mountNode.setPreviousNode(node);
					} else {
						prevNode.setPreviousNode(node);
						node.setNextNode(prevNode);
					}

					world.addFreshEntity(node);

					prevNode = node;
				}

				mountNode.setNextNode(player);

				world.addFreshEntity(mountNode);

				world.playLocalSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.ROPE_THROW, SoundCategory.PLAYERS, 1.5F, 0.8F + world.rand.nextFloat() * 0.3F);
			}
		}

		return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		int maxNodes = MIN_GRAPPLING_HOOK_LENGTH + stack.getDamageValue();
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.grappling_hook", maxNodes, maxNodes * 2, maxNodes / 2, maxNodes), 0));
		if (stack.getDamageValue() < stack.getMaxDamage()) {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.grappling_hook.upgrade"), 0));
		}
		if (GuiScreen.hasShiftDown()) {
			String toolTip = I18n.get("tooltip.bl.grappling_hook.more_info",
					Minecraft.getInstance().gameSettings.keyBindJump.getDisplayName(),
					Minecraft.getInstance().gameSettings.keyBindForward.getDisplayName(),
					Minecraft.getInstance().gameSettings.keyBindBack.getDisplayName(),
					Minecraft.getInstance().gameSettings.keyBindLeft.getDisplayName(),
					Minecraft.getInstance().gameSettings.keyBindRight.getDisplayName());
			tooltip.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			tooltip.add(I18n.get("tooltip.bl.press.shift"));
		}
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}
	
    @Override
    public boolean isValidRepairItem() {
    	return false;
    }
    
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
    	return false;
    }
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)) {
			ItemStack baseHook = new ItemStack(this);
            items.add(baseHook);
            items.add(new ItemStack(this, 1, baseHook.getMaxDamage()));
        }
	}

	public void onGrapplingHookRipped(ItemStack stack, Entity user) {
		if(stack.getDamageValue() > 0) {
			stack.setDamageValue(stack.getDamageValue() - 1);
		}
	}

	public boolean canRideGrapplingHook(ItemStack stack, Entity user) {
		return true;
	}
}
