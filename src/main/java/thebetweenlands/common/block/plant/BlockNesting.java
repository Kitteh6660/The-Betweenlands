package thebetweenlands.common.block.plant;

import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockNesting extends BlockWeedwoodBush {
	
	private ItemStack drop;

	public BlockNesting(ItemStack drop, Properties properties) {
		super(properties);
		this.drop = drop;
	}

	@Override
	@Nullable
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return this.drop.getItem();
	}

	@Override
	public int damageDropped(BlockState state) {
		return this.drop.getDamageValue();
	}

	@Override
	public boolean canConnectTo(IBlockReader worldIn, BlockPos pos, Direction dir) {
		if(dir == Direction.DOWN && worldIn.isSideSolid(pos, Direction.UP, false)) {
			return true;
		}
		return worldIn.getBlockState(pos).getBlock() instanceof BlockNesting;
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		if (!worldIn.isClientSide() && !(entityIn instanceof IEntityBL) && entityIn instanceof LivingEntity && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.isActive((LivingEntity)entityIn)) {
			entityIn.hurt(DamageSource.CACTUS, 1.0F);
		}
	}

	@Override
	public int getColorMultiplier(BlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		return 0xFFFFFFFF;
	}
	
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return EnumItemMisc.SLIMY_BONE.isItemOf(drop) ? new ItemStack(BlockRegistry.NESTING_BLOCK_BONES) : new ItemStack(BlockRegistry.NESTING_BLOCK_STICKS);
	}
}
