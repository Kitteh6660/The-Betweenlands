package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockSulfurTorchExtinguished extends BlockSulfurTorch {
	public BlockSulfurTorchExtinguished() {
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.WOOD);
		this.setLightLevel(0.0f);
	}

	@Override
	public void fillWithRain(World worldIn, BlockPos pos) { }

	@Override
	public ActionResultType use(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack held = playerIn.getItemInHand(hand);
		if(!held.isEmpty() && held.getItem() == ItemRegistry.OCTINE_INGOT) {
			if(!worldIn.isClientSide()) {
				worldIn.setBlockState(pos, BlockRegistry.SULFUR_TORCH.defaultBlockState().setValue(FACING, state.getValue(FACING)));
				worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
			}
			return true;
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) { }
}
