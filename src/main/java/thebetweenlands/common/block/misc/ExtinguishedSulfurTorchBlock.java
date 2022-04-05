package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ExtinguishedSulfurTorchBlock extends SulfurTorchBlock {
	
	public ExtinguishedSulfurTorchBlock(Properties properties, IParticleData flameParticle) {
		super(properties, flameParticle);
		/*this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.WOOD);
		this.setLightLevel(0.0f);*/
	}

	@Override
	public void fillWithRain(World worldIn, BlockPos pos) { }

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hitResult) {
		ItemStack held = player.getItemInHand(hand);
		if(!held.isEmpty() && held.getItem() == ItemRegistry.OCTINE_INGOT.get()) {
			if(!level.isClientSide()) {
				level.setBlockAndUpdate(pos, BlockRegistry.SULFUR_TORCH.get().defaultBlockState());
				level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) { }
}
