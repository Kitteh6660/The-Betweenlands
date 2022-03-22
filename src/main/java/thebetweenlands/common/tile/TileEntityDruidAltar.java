package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.recipes.IDruidAltarRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.BlockDruidAltar;
import thebetweenlands.common.block.structure.BlockDruidStone;
import thebetweenlands.common.block.structure.BlockMobSpawnerBetweenlands;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;
import thebetweenlands.common.network.clientbound.MessageDruidAltarProgress;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationGuarded;

public class TileEntityDruidAltar extends TileEntityBasicInventory implements ITickable {
	public final static double FINAL_HEIGHT = 2.0D;
	// 14.25 seconds crafting time
	public static final int CRAFTING_TIME = 20 * 14 + 5;
	private static final float ROTATION_SPEED = 2.0F;
	public float rotation;
	public float prevRotation;
	public float renderYOffset;
	public float prevRenderYOffset;
	public int craftingProgress = 0;
	private boolean circleShouldRevert = true;

	public TileEntityDruidAltar() {
		super(5, "container.bl.druid_altar");
	}

	@Override
	public void update() {
		if (!this.level.isClientSide() && this.circleShouldRevert) {
			checkDruidCircleBlocks(this.world);
			this.circleShouldRevert = false;
		}
		if (this.level.isClientSide()) {
			this.prevRotation = this.rotation;
			this.rotation += ROTATION_SPEED;
			if (this.rotation >= 360.0F) {
				this.rotation -= 360.0F;
				this.prevRotation -= 360.0F;
			}
			if (this.craftingProgress != 0) {
				this.craftingProgress++;
			}
			this.prevRenderYOffset = this.renderYOffset;
			this.renderYOffset = (float) ((double) this.craftingProgress / (double) TileEntityDruidAltar.CRAFTING_TIME * (FINAL_HEIGHT - 0.2D) + 1.2D);
		} else {
			if (this.craftingProgress != 0) {
				IDruidAltarRecipe recipe = DruidAltarRecipe.getDruidAltarRecipe(this.inventory.get(1), this.inventory.get(2), this.inventory.get(3), this.inventory.get(4));
				// Sync clients every second
				if (this.craftingProgress % 20 == 0 || this.craftingProgress == 1) {
					sendCraftingProgressPacket();
				}
				this.craftingProgress++;
				if (recipe == null || !this.inventory.get(0).isEmpty()) {
					stopCraftingProcess();
				}
				if (recipe != null) {
					recipe.onCrafting(this.world, this.pos, new ItemStack[]{this.inventory.get(1), this.inventory.get(2), this.inventory.get(3), this.inventory.get(4)});
					if(this.craftingProgress >= CRAFTING_TIME) {
						ItemStack stack = recipe.getOutput(new ItemStack[]{this.inventory.get(1), this.inventory.get(2), this.inventory.get(3), this.inventory.get(4)}).copy();
						setItem(1, ItemStack.EMPTY);
						setItem(2, ItemStack.EMPTY);
						setItem(3, ItemStack.EMPTY);
						setItem(4, ItemStack.EMPTY);
						setItem(0, stack);
						stopCraftingProcess();
						recipe.onCrafted(this.world, this.pos, new ItemStack[]{this.inventory.get(1), this.inventory.get(2), this.inventory.get(3), this.inventory.get(4)}, stack);
					}
				}
			}
		}
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
			stack.setCount( getMaxStackSize());
		}
		IDruidAltarRecipe recipe = DruidAltarRecipe.getDruidAltarRecipe(this.inventory.get(1), this.inventory.get(2), this.inventory.get(3), this.inventory.get(4));
		if (!this.level.isClientSide() && recipe != null && !stack.isEmpty() && this.inventory.get(0).isEmpty() && this.craftingProgress == 0) {
			recipe.onStartCrafting(world, pos, new ItemStack[] {this.inventory.get(1), this.inventory.get(2), this.inventory.get(3), this.inventory.get(4)});
			startCraftingProcess();
		}
	}

	private void startCraftingProcess() {
		World world = this.world;
		int dim = world.provider.getDimension();
		this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).setValue(BlockDruidAltar.ACTIVE, true), 3);
		this.craftingProgress = 1;
		// Packet to start sound
		TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this, -1), new NetworkRegistry.TargetPoint(dim, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 64D));
		// Sets client crafting progress to 1
		TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this, 1), new NetworkRegistry.TargetPoint(dim, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 64D));
		// Does the metadata stuff for the circle animated textures
		checkDruidCircleBlocks(world);

		AxisAlignedBB aabb = new AxisAlignedBB(this.pos).grow(8, 6, 8);
		List<EntityDarkDruid> druids = this.world.getEntitiesOfClass(EntityDarkDruid.class, aabb);
		for(EntityDarkDruid druid : druids) {
			druid.attackEntityFrom(DamageSource.GENERIC, druid.getHealth());
		}

		MobSpawnerLogicBetweenlands logic = BlockMobSpawnerBetweenlands.getLogic(this.world, this.pos.below());
		if(logic != null) {
			//Don't spawn druids while crafting
			logic.setDelay(CRAFTING_TIME + 20);
		}
	}

	private void stopCraftingProcess() {
		World world = this.world;
		int dim = world.provider.getDimension();
		this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).setValue(BlockDruidAltar.ACTIVE, false), 3);
		this.craftingProgress = 0;
		// Packet to cancel sound
		TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this, -2), new NetworkRegistry.TargetPoint(dim, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 64D));
		// Sets client crafting progress to 0
		TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this, 0), new NetworkRegistry.TargetPoint(dim, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 64D));
		// Does the metadata stuff for the circle animated textures
		checkDruidCircleBlocks(world);
	}

	public void sendCraftingProgressPacket() {
		World world = this.world;
		int dim = world.provider.getDimension();
		TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this), new NetworkRegistry.TargetPoint(dim, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 64D));
	}

	private void checkDruidCircleBlocks(World world) {
		int baseRadius = 6;
		BlockPos.Mutable pos = new BlockPos.Mutable();
		int posX = this.pos.getX(), posY = this.pos.getY(), posZ = this.pos.getZ();
		for (int y = 0; y < 6; y++) {
			for (int x = -baseRadius; x <= baseRadius; x++) {
				for (int z = -baseRadius; z <= baseRadius; z++) {
					int dSq = x * x + z * z;
					if (Math.round(Math.sqrt(dSq)) <= baseRadius) {
						pos.setPos(posX + x, posY + y, posZ + z);
						BlockState state = world.getBlockState(pos);
						Block block = state.getBlock();
						if (block instanceof BlockDruidStone) {
							if ((this.craftingProgress == 0 || this.circleShouldRevert) && state.getValue(BlockDruidStone.ACTIVE)) {
								world.setBlockState(pos, state.setValue(BlockDruidStone.ACTIVE, false), 3);
							} else if (this.craftingProgress == 1 && !state.getValue(BlockDruidStone.ACTIVE)) {
								world.setBlockState(pos, state.setValue(BlockDruidStone.ACTIVE, true), 3);
							}
						}
					}
				}
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(this.pos.getX() - 1, this.pos.getY(), this.pos.getZ() - 1, this.pos.getX() + 2, this.pos.getY() + 3, this.pos.getZ() + 2);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("craftingProgress", this.craftingProgress);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.readFromNBT(nbt);
		this.craftingProgress = nbt.getInt("craftingProgress");
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT tag = new CompoundNBT();
		this.writeInventoryNBT(tag);
		return new SUpdateTileEntityPacket(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		this.readInventoryNBT(packet.getNbtCompound());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		this.writeInventoryNBT(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(CompoundNBT nbt) {
		super.handleUpdateTag(nbt);
		this.readInventoryNBT(nbt);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		switch (side){

			case DOWN:
				return new int[]{0};
			case UP:
			case NORTH:
			case SOUTH:
			case WEST:
			case EAST:
				return new int[]{1, 2, 3, 4};
		}
		return super.getSlotsForFace(side);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
		return super.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
		return super.removeStackFromSlot(index);
	}
}
