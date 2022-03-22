package thebetweenlands.common.item.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpiritTreeFaceMaskItem extends ItemBLArmor {
	
	public static interface EntityFactory {
		public HangingEntity create(World world, BlockPos pos, Direction clickedSide);
	}

	private final EntityFactory factory;

	@OnlyIn(Dist.CLIENT)
	private BipedModel<?> model;

	public SpiritTreeFaceMaskItem(EntityFactory factory, Properties properties) {
		super(BLArmorMaterial.SLIMY_BONE, EquipmentSlotType.HEAD, properties);
		/*super(BLMaterialRegistry.ARMOR_BONE, 2, EquipmentSlotType.HEAD, armorName);
		this.setMaxDamage(0);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);*/
		this.factory = factory;
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	@Override
	public boolean isBookEnchantable(ItemStack is, ItemStack book) {
		return false;
	}

	/*@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}*/

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		World level = context.getLevel();
		PlayerEntity player = context.getPlayer();
		
		ItemStack itemstack = player.getItemInHand(context.getHand());
		Direction dir = context.getClickedFace();
		BlockPos offsetPos = context.getClickedPos().relative(dir);

		if (dir != Direction.DOWN && dir != Direction.UP && player.mayUseItemAt(offsetPos, dir, itemstack)) {
			HangingEntity entity = this.factory.create(context.getLevel(), offsetPos, dir);

			if (entity != null && entity.survives()) {
				if (!level.isClientSide()) {
					entity.playPlacementSound();
					level.addFreshEntity(entity);
				}

				itemstack.shrink(1);
			}

			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.FAIL;
		}
	}
}
