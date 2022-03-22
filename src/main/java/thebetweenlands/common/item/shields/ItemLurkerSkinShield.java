package thebetweenlands.common.item.shields;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.entity.EntityLurkerSkinRaft;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.tools.ItemBLShield;

public class ItemLurkerSkinShield extends ItemBLShield {
	
	public ItemLurkerSkinShield(Properties properties) {
		super(BLMaterialRegistry.TOOL_LURKER_SKIN);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.lurker_skin_shield"), 0));
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		float pitch = playerIn.xRot;
		float yaw = playerIn.yRot;
		double playerX = playerIn.getX();
		double playerY = playerIn.getY() + (double)playerIn.getEyeHeight();
		double playerZ = playerIn.getZ();
		Vector3d playerPos = new Vector3d(playerX, playerY, playerZ);
		float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
		float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
		float pitchCos = -MathHelper.cos(-pitch * 0.017453292F);
		float pitchSin = MathHelper.sin(-pitch * 0.017453292F);
		float dirX = yawSin * pitchCos;
		float dirZ = yawCos * pitchCos;
		Vector3d endPos = playerPos.add((double)dirX * 5.0D, (double)pitchSin * 5.0D, (double)dirZ * 5.0D);
		RayTraceResult rayTrace = worldIn.rayTraceBlocks(playerPos, endPos, true);

		if(rayTrace != null) {
			Vector3d lookVec = playerIn.getLook(1.0F);
			boolean entityColliding = false;
			List<Entity> entities = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.getBoundingBox().expand(lookVec.x * 5.0D, lookVec.y * 5.0D, lookVec.z * 5.0D).grow(1.0D));

			for(Entity entity : entities) {
				if(entity.canBeCollidedWith()) {
					AxisAlignedBB aabb = entity.getBoundingBox().grow((double)entity.getCollisionBorderSize());

					if(aabb.contains(playerPos)) {
						entityColliding = true;
						break;
					}
				}
			}

			if(!entityColliding && rayTrace.getType() == RayTraceResult.Type.BLOCK) {
				boolean isWater = worldIn.getBlockState(rayTrace.getBlockPos()).getMaterial() == Material.WATER;
				if(isWater) {
					BoatEntity boat = new EntityLurkerSkinRaft(worldIn, rayTrace.hitVec.x, rayTrace.hitVec.y - 0.12D, rayTrace.hitVec.z, stack);
					boat.yRot = playerIn.yRot;

					if(worldIn.getCollisionBoxes(boat, boat.getBoundingBox().inflate(-0.1D)).isEmpty()) {
						if(!worldIn.isClientSide()) {
							worldIn.spawnEntity(boat);

							if(!playerIn.isCrouching()) {
								playerIn.startRiding(boat);
							}
						}

						if(!playerIn.isCreative()) {
							stack.shrink(1);
						}

						playerIn.awardStat(Stats.getObjectUseStats(this));

						return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
					}
				}
			}
		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
}
