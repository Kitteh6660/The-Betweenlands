package thebetweenlands.common.item.armor;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemLurkerSkinArmor extends ItemBLArmor {
	
	public ItemLurkerSkinArmor(EquipmentSlotType equipmentSlotIn, Properties properties) {
		super(BLArmorMaterial.LURKER_SKIN, equipmentSlotIn, properties);
		//super(BLMaterialRegistry.ARMOR_LURKER_SKIN, 3, slot, "lurker_skin");
		this.setGemArmorTextureOverride(CircleGemType.AQUA, "lurker_skin_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "lurker_skin_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "lurker_skin_green");
	}

	@Override
	public void onArmorTick(ItemStack itemStack, World world, PlayerEntity player) {
		if(!player.isSpectator()) {
			NonNullList<ItemStack> armor = player.inventory.armor;
			int armorPieces = 0;
	
			for (ItemStack anArmor : armor) {
				if (anArmor != null && anArmor.getItem() instanceof ItemLurkerSkinArmor) {
					armorPieces += 1;
				}
			}
	
			if (itemStack.getItem() == ItemRegistry.LURKER_SKIN_BOOTS.get() && player.isInWater()) {
				BlockState blockState = player.level.getBlockState(new BlockPos(player.getX(), player.getBoundingBox().maxY + 0.1D, player.getZ()));
				boolean fullyInWater = blockState.getMaterial().isLiquid();
	
				if(fullyInWater) {
					if(!player.isCrouching() && player.zza == 0) {
						player.yo = Math.sin(player.tickCount / 5.0F) * 0.016D;
					}
	
					if(player.zza != 0) {
						if(player.zza > 0) {
							Vector3d lookVec = player.getLookAngle().normalize();
							double speed = 0.01D + 0.05D / 4.0D * armorPieces;
							player.xo += lookVec.x * player.getDeltaMovement().x * speed;
							player.zo += lookVec.z * player.getDeltaMovement().z * speed;
							player.yo += lookVec.y * player.getDeltaMovement().y * speed;
							player.getFoodData().causeFoodExhaustion(0.0024F);
						}
						player.yo += 0.02D;
					}
				}
	
				if(armorPieces >= 4) {
					player.addEffect(new EffectInstance(Effects.WATER_BREATHING, 10));
	
					if(player.tickCount % 3 == 0) {
						player.setAirSupply(player.getAirSupply() - 1);
					}
	
					if(player.getAirSupply() <= -20) {
						player.setAirSupply(0);
	
						for (int i = 0; i < 8; ++i) {
							Random rand = world.random;
							float rx = rand.nextFloat() - rand.nextFloat();
							float ry = rand.nextFloat() - rand.nextFloat();
							float rz = rand.nextFloat() - rand.nextFloat();
	
							player.level.addParticle(ParticleTypes.BUBBLE, player.getX() + (double)rx, player.getY() + (double)ry, player.getZ() + (double)rz, player.xo, player.yo, player.zo);
						}
	
						player.hurt(DamageSource.DROWN, 2.0F);
					}
				}
			}
		}
	}
}
