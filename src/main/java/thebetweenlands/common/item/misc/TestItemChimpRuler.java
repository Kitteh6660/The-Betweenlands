package thebetweenlands.common.item.misc;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.gen.feature.structure.WorldGenSludgeWormDungeon;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationStorage;

//MINE!!
public class TestItemChimpRuler extends Item {
	public TestItemChimpRuler() {
		this.setMaxStackSize(1);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flag) {
		if (hasTag(stack) && stack.getTag().contains("homeX")) {
			list.add(TextFormatting.YELLOW + new TranslationTextComponent("tooltip.bl.chimp_ruler.homex", stack.getTag().getInt("homeX")).getFormattedText());
			list.add(TextFormatting.YELLOW + new TranslationTextComponent("tooltip.bl.chimp_ruler.homey", stack.getTag().getInt("homeY")).getFormattedText());
			list.add(TextFormatting.YELLOW + new TranslationTextComponent("tooltip.bl.chimp_ruler.homez", stack.getTag().getInt("homeZ")).getFormattedText());
		}
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = player.getItemInHand(hand);
		if (hasTag(stack) && player.isCrouching() && hand.equals(Hand.MAIN_HAND)) {
			Block block = world.getBlockState(pos).getBlock();
			if (!level.isClientSide() && block != null) {
				stack.getTag().putInt("homeX", pos.getX());
				stack.getTag().putInt("homeY", pos.getY());
				stack.getTag().putInt("homeZ", pos.getZ());
				return ActionResultType.SUCCESS;
			}
		}

		if (hasTag(stack) && stack.getTag().contains("homeX") && !player.isCrouching() && hand.equals(Hand.MAIN_HAND)) {
			BlockState state = world.getBlockState(pos);
			if (!level.isClientSide() && state.getBlock() != null) {
				int x = pos.getX() - stack.getTag().getInt("homeX");
				int y = pos.getY() - stack.getTag().getInt("homeY");
				int z = pos.getZ() - stack.getTag().getInt("homeZ");
				player.displayClientMessage(new TranslationTextComponent("chat.chimp_ruler_x", x), false);
				player.displayClientMessage(new TranslationTextComponent("chat.chimp_ruler_y", y), false);
				player.displayClientMessage(new TranslationTextComponent("chat.chimp_ruler_z", z), false);
			//	String[] name = state.getBlock().getRegistryName().toString().toUpperCase().split(":");
			//	CopytoClipboard("rotatedCubeVolume(world, rand, pos, " + x + ", "+ y + ", " + z + ", blockHelper." + name[1] + ", 1, 1, 1, facing);");
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.FAIL;
	}
	
	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if(!worldIn.isClientSide() && handIn == Hand.OFF_HAND) {
			ItemStack stack = playerIn.getItemInHand(handIn);
			int x = stack.getTag().getInt("homeX");
			int y = stack.getTag().getInt("homeY");
			int z = stack.getTag().getInt("homeZ");
			BlockPos home = new BlockPos(x, y, z);
			this.doUseAction(worldIn, playerIn, home);
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	protected void doUseAction(World world, PlayerEntity player, BlockPos home) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		ILocalStorageHandler handler = worldStorage.getLocalStorageHandler();
		handler.getLocalStorages(LocationStorage.class, new AxisAlignedBB(home.add(-80, -80, -80), home.add(80, 80, 80)), l -> true).forEach(l -> handler.removeLocalStorage(l));
		new WorldGenSludgeWormDungeon().generateLocations(world, world.rand, home);
	}

	public static boolean CopytoClipboard(String string) {
		try {
			StringSelection selection = new StringSelection(string);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
			return true;
		} catch (Exception e) {
			System.out.println("Poo-poo, pee-pee bum.");
			return false;
		}
	}

	private boolean hasTag(ItemStack stack) {
		if (!stack.hasTag()) {
			stack.setTag(new CompoundNBT());
			return false;
		}
		return true;
	}
	
	@Override
	public CreativeTabs getCreativeTab() {
		return BetweenlandsConfig.DEBUG.debug ? BLCreativeTabs.SPECIALS : null;
	}
}
