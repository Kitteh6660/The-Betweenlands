package thebetweenlands.common.block.terrain;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.render.particle.BLParticles;
import net.minecraft.block.CryingObsidianBlock;

public class BlockHearthgroveLog extends BlockLogBetweenlands {
	
	public static final BooleanProperty TARRED = BooleanProperty.create("tarred");

	public BlockHearthgroveLog(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(TARRED, false).setValue(AXIS, Direction.Axis.Y));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World player, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		List<String> strings = ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.hearthgrove_log"), 0);
		if (stack.getMetadata() == 5 || stack.getMetadata() == 7)
			strings.remove(strings.size() - 1);
		tooltip.addAll(strings);
	}

	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		BlockPos.Mutable checkPos = BlockPos.Mutable.retain();

		boolean hasWater = false;
		for(Direction offset : Direction.Plane.HORIZONTAL) {
			BlockState offsetState = worldIn.getBlockState(checkPos.set(pos.getX() + offset.getStepX(), pos.getY(), pos.getZ() + offset.getStepZ()));
			BlockState offsetStateDown = worldIn.getBlockState(checkPos.set(pos.getX() + offset.getStepX(), pos.getY() - 1, pos.getZ() + offset.getStepZ()));

			if(offsetStateDown.getMaterial() == Material.WATER && offsetState.getMaterial() != Material.WATER) {
				if(rand.nextInt(8) == 0) {
					for(int i = 0; i < 5; i++) {
						float x = pos.getX() + (offset.getStepX() > 0 ? 1.05F : offset.getStepX() == 0 ? rand.nextFloat() : -0.05F);
						float y = pos.getY() - 0.1F;
						float z = pos.getZ() + (offset.getStepZ() > 0 ? 1.05F : offset.getStepZ() == 0 ? rand.nextFloat() : -0.05F);

						BLParticles.PURIFIER_STEAM.spawn(worldIn, x, y, z);
					}
				}
			}

			if(offsetState.getMaterial() == Material.WATER) {
				if(rand.nextInt(8) == 0) {
					for(int i = 0; i < 5; i++) {
						float x = pos.getX() + (offset.getStepX() > 0 ? 1.1F : offset.getStepX() == 0 ? rand.nextFloat() : -0.1F);
						float y = pos.getY() + rand.nextFloat();
						float z = pos.getZ() + (offset.getStepZ() > 0 ? 1.1F : offset.getStepZ() == 0 ? rand.nextFloat() : -0.1F);

						worldIn.addParticle(ParticleTypes.BUBBLE, x, y, z, 0, 0, 0);
					}
				}
				hasWater = true;
			}
		}
		if(!hasWater) {
			for(Direction offset : Direction.Plane.HORIZONTAL) {
				if(rand.nextFloat() < 0.04F) {
					checkPos.set(pos.getX() + offset.getStepX(), pos.getY(), pos.getZ() + offset.getStepZ());
					BlockState offsetState = worldIn.getBlockState(checkPos);
					if(!offsetState.isFaceSturdy(worldIn, checkPos, offset.getOpposite())) {
						float x = pos.getX() + (offset.getStepX() > 0 ? 1.05F : offset.getStepX() == 0 ? rand.nextFloat() : -0.05F);
						float y = pos.getY() + rand.nextFloat();
						float z = pos.getZ() + (offset.getStepZ() > 0 ? 1.05F : offset.getStepZ() == 0 ? rand.nextFloat() : -0.05F);

						switch(rand.nextInt(3)) {
						default:
						case 0:
							BLParticles.EMBER_1.spawn(worldIn, x, y, z);
							break;
						case 1:
							BLParticles.EMBER_2.spawn(worldIn, x, y, z);
							break;
						case 2:
							BLParticles.EMBER_3.spawn(worldIn, x, y, z);
							break;
						}
					}
				}
			}
		}

		checkPos.release();
	}

}
