package thebetweenlands.common.block.terrain;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.block.BlockStateContainerHelper;

public class BlockHearthgroveLog extends BlockLogBetweenlands {
	public static final BooleanProperty TARRED = BooleanProperty.create("tarred");

	public BlockHearthgroveLog() {
		this.setDefaultState(this.blockState.getBaseState().setValue(TARRED, false));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		List<String> strings = ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.hearthgrove_log"), 0);
		if (stack.getMetadata() == 5 || stack.getMetadata() == 7)
			strings.remove(strings.size() - 1);
		tooltip.addAll(strings);
	}

	@Override
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();

		boolean hasWater = false;
		for(Direction offset : Direction.HORIZONTALS) {
			BlockState offsetState = worldIn.getBlockState(checkPos.setPos(pos.getX() + offset.getStepX(), pos.getY(), pos.getZ() + offset.getStepZ()));
			BlockState offsetStateDown = worldIn.getBlockState(checkPos.setPos(pos.getX() + offset.getStepX(), pos.getY() - 1, pos.getZ() + offset.getStepZ()));

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

						worldIn.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x, y, z, 0, 0, 0);
					}
				}
				hasWater = true;
			}
		}
		if(!hasWater) {
			for(Direction offset : Direction.HORIZONTALS) {
				if(rand.nextFloat() < 0.04F) {
					checkPos.setPos(pos.getX() + offset.getStepX(), pos.getY(), pos.getZ() + offset.getStepZ());
					BlockState offsetState = worldIn.getBlockState(checkPos);
					if(!offsetState.isSideSolid(worldIn, checkPos, offset.getOpposite())) {
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

	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		BlockState state = this.getStateFromMeta(meta);
		if(state.getValue(LOG_AXIS) != BlockLog.EnumAxis.NONE) {
			return state.setValue(LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()));
		}
		return state;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		BlockLog.EnumAxis axis;

		int axisIndex = meta & 3;
		switch(axisIndex) {
		default:
			case 0:
				axis = BlockLog.EnumAxis.X;
				break;
			case 1:
				axis = BlockLog.EnumAxis.Y;
				break;
			case 2:
				axis = BlockLog.EnumAxis.Z;
				break;
			case 3:
				axis = BlockLog.EnumAxis.NONE;
				break;
		}

		boolean tarred = (meta >> 2) != 0;

		return this.defaultBlockState().setValue(LOG_AXIS, axis).setValue(TARRED, tarred);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int meta = 0;

		switch(state.getValue(LOG_AXIS)) {
			case X:
				meta = 0;
				break;
			case Y:
				meta = 1;
				break;
			case Z:
				meta = 2;
				break;
			case NONE:
				meta = 3;
				break;
		}

		meta |= state.getValue(TARRED) ? (1 << 2) : 0;

		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return BlockStateContainerHelper.extendBlockstateContainer(super.createBlockState(), new IProperty[] {TARRED});
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, this.getMetaFromState(this.defaultBlockState().setValue(LOG_AXIS, BlockLog.EnumAxis.Y).setValue(TARRED, false))));
		list.add(new ItemStack(this, 1, this.getMetaFromState(this.defaultBlockState().setValue(LOG_AXIS, BlockLog.EnumAxis.Y).setValue(TARRED, true))));
		list.add(new ItemStack(this, 1, this.getMetaFromState(this.defaultBlockState().setValue(LOG_AXIS, BlockLog.EnumAxis.NONE).setValue(TARRED, false))));
		list.add(new ItemStack(this, 1, this.getMetaFromState(this.defaultBlockState().setValue(LOG_AXIS, BlockLog.EnumAxis.NONE).setValue(TARRED, true))));
	}

	@Override
	public int damageDropped(BlockState state) {
		BlockLog.EnumAxis axis = state.getValue(LOG_AXIS);
		if(axis == BlockLog.EnumAxis.X || axis == BlockLog.EnumAxis.Z) {
			state = state.setValue(LOG_AXIS, BlockLog.EnumAxis.Y);
		}
		return this.getMetaFromState(state);
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, BlockState state) {
		return getSilkTouchDrop(state);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return getSilkTouchDrop(state);
	}

	@Override
	protected ItemStack getSilkTouchDrop(BlockState state) {
		return new ItemStack(Item.getItemFromBlock(this), 1, this.damageDropped(state));
	}

	@Override
	public int getSubtypeNumber() {
		return 4;
	}

	@Override
	public int getSubtypeMeta(int subtype) {
		switch(subtype) {
		default:
		case 0:
			return this.getMetaFromState(this.defaultBlockState().setValue(LOG_AXIS, BlockLog.EnumAxis.Y).setValue(TARRED, false));
		case 1:
			return this.getMetaFromState(this.defaultBlockState().setValue(LOG_AXIS, BlockLog.EnumAxis.Y).setValue(TARRED, true));
		case 2:
			return this.getMetaFromState(this.defaultBlockState().setValue(LOG_AXIS, BlockLog.EnumAxis.NONE).setValue(TARRED, false));
		case 3:
			return this.getMetaFromState(this.defaultBlockState().setValue(LOG_AXIS, BlockLog.EnumAxis.NONE).setValue(TARRED, true));
		}
	}

	@Override
	public String getSubtypeName(int meta) {
		BlockState state = this.getStateFromMeta(meta);
		String name = "%s";
		if(state.getValue(LOG_AXIS) == BlockLog.EnumAxis.NONE) {
			name = name + "_full";
		}
		if(state.getValue(TARRED)) {
			name = name + "_tarred";
		}
		return name;
	}

	@Override
	public BlockItem getItemBlock() {
		BlockItem item = new BlockItem(this) {
			@Override
			public String getTranslationKey(ItemStack stack) {
				BlockState state = this.block.getStateFromMeta(this.getMetadata(stack.getItemDamage()));
				return this.block.getTranslationKey() + (state.getValue(TARRED) ? "_tarred" : "");
			}

			@Override
			public int getMetadata(int damage) {
				return damage;
			}
		};
		item.setHasSubtypes(true);
		return item;
	}
}
