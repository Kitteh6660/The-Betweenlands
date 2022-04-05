package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityMossBed;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockMossBed extends BedBlock {
	
	public BlockMossBed(DyeColor colour, Properties properties) { 
		super(colour, properties);
		/*this.setCreativeTab(null);
		this.setSoundType(SoundType.WOOD);
		this.setHardness(0.2F);*/
		//this.disableStats();
	}

	//TODO: Replace this code with loot_table json.
	/*
	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return state.getValue(PART) == BlockBed.EnumPartType.HEAD ? Items.AIR : ItemRegistry.MOSS_BED_ITEM;
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(ItemRegistry.MOSS_BED_ITEM);
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, BlockState state, float chance, int fortune) {
        if (state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
            spawnAsEntity(worldIn, pos, new ItemStack(ItemRegistry.MOSS_BED_ITEM));
        }
    }

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack) {
		if (state.getValue(PART) == BlockBed.EnumPartType.HEAD && te instanceof TileEntityMossBed) {
			spawnAsEntity(worldIn, pos, new ItemStack(ItemRegistry.MOSS_BED_ITEM));
		} else {
			super.harvestBlock(worldIn, player, pos, state, null, stack);
		}
	}*/

	@Override
	public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, Entity player) {
		return true;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hitResult) {
		if(player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			if(!player.isLocalPlayer()) {
				player.setSpawnPoint(pos, false);
				player.displayClientMessage(new TranslationTextComponent("chat.bed_spawn_set"), true);
			}
			return ActionResultType.SUCCESS;
		} else {
			return super.use(state, level, pos, player, hand, hitResult);
		}
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityMossBed();
	}
}