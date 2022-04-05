package thebetweenlands.common.block.terrain;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.RubberBootsItem;
import thebetweenlands.common.registries.ItemRegistry;


public class BlockMud extends Block {
	
	protected static final VoxelShape MUD_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);
	
	public BlockMud(Properties properties) {
		super(properties);
		/*super(BLMaterialRegistry.MUD);
		setHardness(0.5F);
		setSoundType(SoundType.GROUND);
		setHarvestLevel("shovel", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setLightOpacity(255);*/
	}

	public boolean canEntityWalkOnMud(Entity entity) {
		if (entity instanceof LivingEntity && ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.isActive((LivingEntity) entity))
			return false;
		boolean canWalk = entity instanceof PlayerEntity && RubberBootsItem.canEntityWalkOnMud(entity);
		boolean hasLurkerArmor = entity instanceof PlayerEntity && entity.isInWater() && !((PlayerEntity) entity).inventory.armor.isEmpty() && ((PlayerEntity) entity).inventory.armor.get(0).getItem() == ItemRegistry.LURKER_SKIN_BOOTS.get();
		return entity instanceof IEntityBL || entity instanceof ItemEntity || canWalk || hasLurkerArmor || (entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative() && ((PlayerEntity) entity).abilities.flying);
	}

	@Override
   public VoxelShape getBlockSupportShape(BlockState pState, IBlockReader pReader, BlockPos pPos) {
      return VoxelShapes.block();
   }

	@Override
	public VoxelShape getCollisionShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
		return MUD_AABB;
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
		AxisAlignedBB blockAABB = FULL_BLOCK_AABB.offset(pos);
		if (entityBox.intersects(blockAABB) && (entity == null || canEntityWalkOnMud(entity)))
			collidingBoxes.add(blockAABB);
		else if (world.isClientSide()) {
			blockAABB = MUD_AABB.offset(pos);
			if (entityBox.intersects(blockAABB)) {
				collidingBoxes.add(blockAABB);
			}
		}
	}

	@Override
	public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
		if (!canEntityWalkOnMud(entity)) {
			entity.xo *= 0.08D;
			if(!entity.isInWater() && entity.yo < 0 && entity.isOnGround()) entity.yo = -0.1D;
			entity.zo *= 0.08D;
			if(!entity.isInWater()) {
				entity.setInWeb();
			} else {
				entity.yo *= 0.02D;
			}
			entity.onGround = true;
			if(entity instanceof LivingEntity && entity.isInsideOfMaterial(BLMaterialRegistry.MUD)) {
				entity.hurt(DamageSource.IN_WALL, 2.0F);
			}
		}
	}

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState blockState) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		double d0 = (double) pos.getX();
		double d1 = (double) pos.getY();
		double d2 = (double) pos.getZ();

		if (rand.nextInt(10) == 0) {
			boolean stateBelow = world.isEmptyBlock(pos.below());
			if (stateBelow) {
				double d3 = d0 + (double) rand.nextFloat();
				double d5 = d1 - 0.05D;
				double d7 = d2 + (double) rand.nextFloat();
				BLParticles.CAVE_WATER_DRIP.spawn(world, d3, d5, d7).setColor(0.4118F, 0.2745F, 0.1568F);
			}
		}
	}
}