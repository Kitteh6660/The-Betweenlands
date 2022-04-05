package thebetweenlands.common.capability.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.inventory.InventoryEquipment;
import thebetweenlands.common.inventory.InventoryEquipmentAmulets;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

import java.util.EnumMap;
import java.util.Map;

public class EquipmentEntityCapability extends EntityCapability<EquipmentEntityCapability, IEquipmentCapability, PlayerEntity> implements IEquipmentCapability, ISerializableCapability {
	private Map<EnumEquipmentInventory, NonNullList<ItemStack>> allInventoryStacks = new EnumMap<>(EnumEquipmentInventory.class);
	private Map<EnumEquipmentInventory, IInventory> inventories = new EnumMap<>(EnumEquipmentInventory.class);
	private int amuletSlots = 1;

	public EquipmentEntityCapability() {
		for (EnumEquipmentInventory inventory : EnumEquipmentInventory.VALUES) {
			this.allInventoryStacks.put(inventory, NonNullList.withSize(inventory.maxSize, ItemStack.EMPTY));
		}
	}

	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "equipment");
	}

	@Override
	protected Capability<IEquipmentCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_EQUIPMENT;
	}

	@Override
	protected Class<IEquipmentCapability> getCapabilityClass() {
		return IEquipmentCapability.class;
	}

	@Override
	protected EquipmentEntityCapability getDefaultCapabilityImplementation() {
		return new EquipmentEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof LivingEntity;
	}

	@Override
	public boolean isPersistent(PlayerEntity oldPlayer, PlayerEntity newPlayer, boolean wasDead) {
		return !wasDead || this.getEntity().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
	}

	@Override
	public IInventory getInventory(EnumEquipmentInventory inventoryType) {
		IInventory inventory = this.inventories.get(inventoryType);
		if(inventory == null) {
			switch (inventoryType) {
			case AMULET:
				inventory = new InventoryEquipmentAmulets(this, this.allInventoryStacks.get(inventoryType));
				break;
			default:
				inventory = new InventoryEquipment(this, this.allInventoryStacks.get(inventoryType));
				break;
			}
			this.inventories.put(inventoryType, inventory);
		}
		return inventory;
	}

	@Override
	public void save(CompoundNBT nbt) {
		nbt.putInt("amuletSlots", this.amuletSlots);
		ListNBT inventoryList = new ListNBT();
		for (EnumEquipmentInventory inventoryType : EnumEquipmentInventory.VALUES) {
			NonNullList<ItemStack> inventoryStacks = this.allInventoryStacks.get(inventoryType);
			CompoundNBT inventoryNbt = new CompoundNBT();
			ListNBT slotList = new ListNBT();
			for (int c = 0; c < inventoryStacks.size(); c++) {
				ItemStack stack = inventoryStacks.get(c);
				if (!stack.isEmpty()) {
					CompoundNBT slotNbt = new CompoundNBT();
					slotNbt.putInt("slot", c);
					slotNbt.put("stack", stack.save(new CompoundNBT()));
					slotList.add(slotNbt);
				}
			}
			if (slotList.size() > 0) {
				inventoryNbt.putInt("id", inventoryType.id);
				inventoryNbt.put("items", slotList);
				inventoryList.add(inventoryNbt);
			}
		}
		if (inventoryList.size() > 0)
			nbt.put("inventories", inventoryList);
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.inventories.clear();
		for (EnumEquipmentInventory inventory : EnumEquipmentInventory.VALUES) {
			this.allInventoryStacks.put(inventory, NonNullList.withSize(inventory.maxSize, ItemStack.EMPTY));
		}
		if (nbt.contains("amuletSlots")) {
			this.amuletSlots = nbt.getInt("amuletSlots");
		}
		if (nbt.contains("inventories")) {
			ListNBT inventoryList = nbt.getList("inventories", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < inventoryList.size(); i++) {
				CompoundNBT inventoryNbt = inventoryList.getCompound(i);
				if (inventoryNbt.contains("items")) {
					int id = inventoryNbt.getInt("id");
					EnumEquipmentInventory inventoryType = EnumEquipmentInventory.fromID(id);
					if (inventoryType != null) {
						NonNullList<ItemStack> inventoryStacks = this.allInventoryStacks.get(inventoryType);
						ListNBT slotList = inventoryNbt.getList("items", Constants.NBT.TAG_COMPOUND);
						for (int c = 0; c < slotList.size(); c++) {
							CompoundNBT slotNbt = slotList.getCompound(c);
							int slot = slotNbt.getInt("slot");
							if (slot < inventoryStacks.size()) {
								inventoryStacks.set(slot, new ItemStack(slotNbt.getCompound("stack")));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void writeTrackingDataToNBT(CompoundNBT nbt) {
		this.save(nbt);
	}

	@Override
	public void readTrackingDataFromNBT(CompoundNBT nbt) {
		this.load(nbt);
	}

	@Override
	public int getTrackingTime() {
		return 10;
	}

	@Override
	public int getAmuletSlots() {
		return this.amuletSlots;
	}

	@Override
	public void setAmuletSlots(int slots) {
		this.amuletSlots = slots;
	}
}
