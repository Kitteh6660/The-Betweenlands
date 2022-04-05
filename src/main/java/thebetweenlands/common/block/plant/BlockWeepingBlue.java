package thebetweenlands.common.block.plant;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockWeepingBlue extends BlockDoublePlantBL {
	
	public BlockWeepingBlue(Properties properties) {
		super(properties);
	}

	@Override
	public ArrayList<ItemStack> getDrops(IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		Random rand = world instanceof World ? ((World)world).random : RANDOM;

		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(ItemRegistry.WEEPING_BLUE_PETAL, 1 + rand.nextInt(2 + fortune)));
		return drops;
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		if(!worldIn.isClientSide() && !stack.isEmpty() && stack.getItem() instanceof ShearsItem) {
			player.awardStat(StatList.getBlockStats(this));
			player.causeFoodExhaustion(0.025F);
		} else {
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(rand.nextInt(4) == 0 && worldIn.getBlockState(pos).getValue(HALF) == Half.UPPER) {
			worldIn.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.25D + rand.nextFloat() * 0.5D, pos.getY() + 0.6D, pos.getZ() + 0.25D + rand.nextFloat() * 0.5D, 0.0D, 0.0D, 0.0D);
		}
	}
}
