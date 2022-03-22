package thebetweenlands.common.item.misc;

import net.minecraft.block.BlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.List;

public class ItemWeedwoodRowboat extends Item {
    private static final float REACH = 5;

    public ItemWeedwoodRowboat() {
        maxStackSize = 1;
        setCreativeTab(BLCreativeTabs.ITEMS);
        addPropertyOverride(new ResourceLocation("tarred"), (stack, world, entity) -> EntityWeedwoodRowboat.isTarred(stack) ? 1 : 0);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack stack) {
        String key = getTranslationKey(stack);
        if (EntityWeedwoodRowboat.isTarred(stack)) {
            return key + ".tarred";
        }
        return key;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tab)) {
            list.add(new ItemStack(this));
            list.add(getTarred());
        }
    }
    public static ItemStack getTarred() {
        ItemStack tarred = new ItemStack(ItemRegistry.WEEDWOOD_ROWBOAT);
        CompoundNBT attrs = new CompoundNBT();
        attrs.putBoolean("isTarred", true);
        tarred.setTagInfo("attributes", attrs);
        return tarred;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Vector3d pos = new Vector3d(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
        Vector3d look = player.getLookVec();
        Vector3d lookExtent = pos.offset(look.x * REACH, look.y * REACH, look.z * REACH);
        RayTraceResult hit = world.rayTraceBlocks(pos, lookExtent, true);
        if (hit == null) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, player.getBoundingBox().grow(look.x * REACH, look.y * REACH, look.z * REACH).grow(1, 1, 1));
        for (Entity entity : list) {
            if (entity.canBeCollidedWith()) {
                AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow(entity.getCollisionBorderSize(), entity.getCollisionBorderSize(), entity.getCollisionBorderSize());
                if (axisalignedbb.contains(pos)) {
                    return new ActionResult<>(ActionResultType.PASS, stack);
                }
            }
        }
        if (hit.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
        BlockState block = world.getBlockState(hit.getBlockPos());
        boolean liquid = block.getMaterial().isLiquid();
        EntityWeedwoodRowboat rowboat = new EntityWeedwoodRowboat(world, hit.hitVec.x, liquid ? hit.hitVec.y - 0.3 : hit.hitVec.y, hit.hitVec.z);
        rowboat.yRot = player.yRot;
        if (!world.getCollisionBoxes(rowboat, rowboat.getBoundingBox().grow(-0.1, -0.1, -0.1)).isEmpty()) {
            return new ActionResult<>(ActionResultType.FAIL, stack);
        }
        if (!world.isClientSide()) {
            CompoundNBT attrs = stack.getSubCompound("attributes");
            if (attrs != null) {
                rowboat.readEntityFromNBT(attrs);
            }
            world.spawnEntity(rowboat);
        }
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        player.awardStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}
