package thebetweenlands.common.item.herblore.rune.properties;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.runechain.container.IRuneContainerFactory;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.herblore.rune.GatingRuneBlock;
import thebetweenlands.common.item.herblore.rune.DefaultRuneContainerFactory;
import thebetweenlands.common.item.herblore.rune.ItemRune.RuneItemProperties;
import thebetweenlands.util.NBTHelper;

public class BlockGatingRuneItemProperties extends RuneItemProperties {
	private static final String NBT_BLOCK_DATA = "thebetweenlands.block_rune.block_data";

	private final ResourceLocation regName;

	public BlockGatingRuneItemProperties(ResourceLocation regName) {
		this.regName = regName;
	}

	public Block getBlockType(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();

		if(nbt != null && nbt.contains(NBT_BLOCK_DATA, Constants.NBT.TAG_COMPOUND)) {
			CompoundNBT blockNbt = nbt.getCompoundTag(NBT_BLOCK_DATA);

			if(blockNbt.contains("id", Constants.NBT.TAG_STRING)) {
				return Block.REGISTRY.getObject(new ResourceLocation(blockNbt.getString("id")));
			}
		}

		return null;
	}

	@Override
	public IRuneContainerFactory getFactory(ItemStack stack) {
		return new DefaultRuneContainerFactory(this.regName, () -> new GatingRuneBlock.Blueprint(this.getBlockType(stack)));
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand,
			Direction facing, BlockRayTraceResult hitResult) {

		ItemStack stack = player.getItemInHand(hand);

		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);

		if(player.isCrouching()) {
			if(nbt.contains(NBT_BLOCK_DATA)) {
				nbt.removeTag(NBT_BLOCK_DATA);

				player.swing(hand);

				return ActionResultType.PASS;
			}
		} else {
			BlockState state = worldIn.getBlockState(pos);

			CompoundNBT blockNbt = new CompoundNBT();

			blockNbt.putString("id", state.getBlock().getRegistryName().toString());

			//TODO Also filter meta?

			nbt.put(NBT_BLOCK_DATA, blockNbt);

			player.swing(hand);

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		Block blockType = this.getBlockType(stack);

		if(blockType != null) {
			Item blockItem = Item.getItemFromBlock(blockType);

			String blockName;
			if(blockItem != Items.AIR) {
				blockName = I18n.get(blockItem.getUnlocalizedNameInefficiently(new ItemStack(blockItem)) + ".name").trim();
			} else {
				blockName = I18n.get(blockType.getTranslationKey() + ".name");
			}

			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocalFormatted("tooltip.thebetweenlands.rune.gating_block.bound", blockName), 0));
		} else {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.thebetweenlands.rune.gating_block.unbound"), 0));
		}
	}
}
