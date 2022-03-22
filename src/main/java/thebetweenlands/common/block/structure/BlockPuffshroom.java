package thebetweenlands.common.block.structure;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.structure.BlockMudTiles.EnumMudTileType;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityPuffshroom;

public class BlockPuffshroom extends Block implements ITileEntityProvider {
	
	public BlockPuffshroom(Properties properties) {
		super(properties);
		/*super(Material.ROCK);
		setSoundType(SoundType.STONE);
		setHardness(8);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
		return new TileEntityPuffshroom();
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		return this.tryHarvestWithShears(world, pos, state, player, player.getItemInHand(hand), true);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, World worldIn, BlockPos pos) {
		ItemStack stack = player.getMainHandItem();
		
		if(!stack.isEmpty() && stack.getItem() instanceof ItemShears) {
			return 1.0f;
		} else {
			return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
		}
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		this.tryHarvestWithShears(worldIn, pos, state, player, player.getMainHandItem(), false);
	}
	
	protected ActionResultType tryHarvestWithShears(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack stack, boolean effects) {
		if(!stack.isEmpty() && stack.getItem() instanceof ShearsItem) {
			TileEntity tile = world.getBlockEntity(pos);
			
			if(tile instanceof TileEntityPuffshroom) {
				TileEntityPuffshroom puffshroom = (TileEntityPuffshroom) tile;
				
				if(puffshroom.animation_1 >= 1) {
					if(!world.isClientSide() && world instanceof ServerWorld) {
						LootTable lootTable = ((ServerWorld) world).getLootTableManager().getLootTableFromLocation(LootTableRegistry.PUFFSHROOM);
						LootContext.Builder lootBuilder = new LootContext.Builder((ServerWorld) world);
						
						List<ItemStack> loot = lootTable.generateLootForPools(((ServerWorld) world).rand, lootBuilder.build());
						
						for(ItemStack lootStack : loot) {
							spawnAsEntity(world, pos.above(), lootStack);
						}
						
						world.setBlockState(pos, BlockRegistry.MUD_TILES.defaultBlockState().setValue(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_CRACKED), 3);
						
						if(effects) {
							world.playEvent(null, 2001, pos, Block.getIdFromBlock(this));
						}
						
						world.sendBlockUpdated(pos, state, state, 3);
						
						stack.hurtAndBreak(1, player, (entity) -> {
							entity.broadcastBreakEvent(player.getUsedItemHand());
						});
					}
					
					return ActionResultType.SUCCESS;
				}
			}
		}
		
		return ActionResultType.PASS;
	}
	
	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
        return world.setBlockState(pos, BlockRegistry.MUD_TILES.defaultBlockState().setValue(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_CRACKED), world.isClientSide() ? 11 : 3);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
	
	@Override
	public int quantityDropped(BlockState state, int fortune, Random random) {
		return 0;
	}
}