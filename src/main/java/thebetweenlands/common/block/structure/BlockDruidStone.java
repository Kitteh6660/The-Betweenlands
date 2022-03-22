package thebetweenlands.common.block.structure;

import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.Random;

public class BlockDruidStone extends BasicBlock implements BlockRegistry.ISubtypeItemBlockModelDefinition {
	public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;

	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public BlockDruidStone(Properties properties) {
		super(properties);
		/*super(blockMaterialIn);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(ACTIVE, false)
				);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);
		setLightLevel(0.8F);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, ACTIVE);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return defaultBlockState()
				.setValue(FACING, Direction.byHorizontalIndex(meta))
				.setValue(ACTIVE, (meta & 4) != 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getHorizontalIndex() | (state.getValue(ACTIVE) ? 4 : 0);
	}

	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
		double pixel = 0.625;
		if (!world.getBlockState(pos).getValue(ACTIVE) && rand.nextInt(80) == 0) {
			for (Direction facing : Direction.VALUES) {
				BlockPos side = pos.offset(facing);
				if (!world.getBlockState(side).isOpaqueCube()) {
					double dx = rand.nextFloat() - 0.5, dy = rand.nextFloat() - 0.5, dz = rand.nextFloat() - 0.5;
					int vx = facing.getStepX();
					int vy = facing.getStepY();
					int vz = facing.getStepZ();
					dx *= (1 - Math.abs(vx));
					dy *= (1 - Math.abs(vy));
					dz *= (1 - Math.abs(vz));
					double particleX = pos.getX() + 0.5 + dx + vx * pixel;
					double particleY = pos.getY() + 0.5 + dy + vy * pixel;
					double particleZ = pos.getZ() + 0.5 + dz + vz * pixel;
					BLParticles.DRUID_CASTING_BIG.spawn(world, particleX, particleY, particleZ, 
							ParticleArgs.get()
							.withMotion((rand.nextFloat() - rand.nextFloat()) * 0.1, 0, (rand.nextFloat() - rand.nextFloat()) * 0.1)
							.withScale(rand.nextFloat() * 0.5F + 0.5F)
							.withColor(0.8F + rand.nextFloat() * 0.2F, 0.8F + rand.nextFloat() * 0.2F, 0.8F, 1));
				}                    
			}
		}
	}

	@Override
	public int getSubtypeNumber() {
		return 2;
	}

	@Override
	public String getSubtypeName(int meta) {
		switch(meta) {
		default:
		case 0:
			return "%s";
		case 1:
			return "%s_active";
		}
	}
}
