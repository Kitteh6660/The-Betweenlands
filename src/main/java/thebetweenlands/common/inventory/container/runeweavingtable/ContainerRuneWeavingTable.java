package thebetweenlands.common.inventory.container.runeweavingtable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import thebetweenlands.api.capability.IRuneCapability;
import thebetweenlands.api.capability.IRuneChainCapability;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INodeBlueprint;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.chain.IRuneChainData;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.container.IRuneChainContainerData;
import thebetweenlands.api.runechain.container.IRuneContainer;
import thebetweenlands.api.runechain.container.IRuneContainerContext;
import thebetweenlands.api.runechain.container.IRuneContainerFactory;
import thebetweenlands.api.runechain.container.IRuneLink;
import thebetweenlands.api.runechain.container.IRuneWeavingTableContainer;
import thebetweenlands.api.runechain.container.gui.IRuneWeavingTableGui;
import thebetweenlands.common.herblore.rune.RuneChainData;
import thebetweenlands.common.inventory.slot.SlotRuneWeavingTableInput;
import thebetweenlands.common.inventory.slot.SlotRuneWeavingTableOutput;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.tile.TileEntityRuneWeavingTable;


public class ContainerRuneWeavingTable extends Container implements IRuneWeavingTableContainer {
	protected final TileEntityRuneWeavingTable table;
	protected final PlayerEntity player;

	public static final int [][] SLOT_POSITIONS = new int[][] {
		{8, 56}, {32, 50}, {56, 48}, {80, 46}, {104, 48}, {128, 50}, {152, 56},
		{152, 96}, {128, 102}, {104, 104}, {80, 106}, {56, 104}, {32, 102}, {8, 96}
	};

	private static final int [] OUTPUT_SLOT_POSITION = new int[] { 80, 76 };

	public static final int SLOTS_PER_PAGE = SLOT_POSITIONS.length;

	public static class Page {
		private boolean isCurrent = false;
		private List<Slot> slots = new ArrayList<>();
		public final int index;
		public boolean interactable = true;

		private Page(int index) {
			this.index = index;
		}

		public boolean isCurrent() {
			return this.isCurrent;
		}

		public boolean isInteractable() {
			return this.isCurrent && this.interactable;
		}
	}

	private Page currentPage;
	private final List<Page> pages;

	private int selectedRune = -1;

	protected class RuneContainerEntry {
		protected IRuneContainer container;
		protected IRuneContainerContext context;
		protected final List<Slot> slots = new ArrayList<>();
		protected int runeIndex;
		protected INodeConfiguration configuration;
		protected INodeConfiguration provisionalConfiguration;

		protected RuneContainerEntry(int runeIndex) {
			this.runeIndex = runeIndex;
		}

		//TODO Implement adding/removing 
		protected void addSlotsToContainer() {
			for(Slot slot : this.slots) {
				ContainerRuneWeavingTable.this.this.addSlot(slot);
			}
		}

		protected void removeSlotsFromContainer() {
			for(Slot entrySlot : this.slots) {
				int index = ContainerRuneWeavingTable.this.slots.indexOf(entrySlot);
				if(index >= 0) {
					ContainerRuneWeavingTable.this.slots.remove(index);
					ContainerRuneWeavingTable.this.inventoryItemStacks.remove(index);
				}
			}
		}
	}

	private Map<Integer, RuneContainerEntry> runeContainers = new HashMap<>();

	public ContainerRuneWeavingTable(PlayerEntity player, TileEntityRuneWeavingTable tile) {
		this.table = tile;
		this.player = player;

		this.pages = new ArrayList<>();

		Page page = new Page(0);
		page.isCurrent = true;
		this.currentPage = page;
		this.pages.add(page);

		//Output slot
		this.this.addSlot(new SlotRuneWeavingTableOutput(this.table, 0, OUTPUT_SLOT_POSITION[0], OUTPUT_SLOT_POSITION[1], this));

		//Rune slots
		for(int i = 0; i < tile.getMaxChainLength(); i++) {
			int pageSlot = i % SLOTS_PER_PAGE;
			if(i != 0 && pageSlot == 0) {
				page = new Page(this.pages.size());
				this.pages.add(page);
			}
			Slot slot = new SlotRuneWeavingTableInput(this.table, i + 1, SLOT_POSITIONS[pageSlot][0], SLOT_POSITIONS[pageSlot][1], page);
			this.this.addSlot(slot);
		}

		//Player inventory
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.this.addSlot(new Slot(this.player.inventory, x + y * 9 + 9, 8 + x * 18, 155 + y * 18));
			}
		}

		//Player hotbar
		for (int x = 0; x < 9; ++x) {
			this.this.addSlot(new Slot(this.player.inventory, x, 8 + x * 18, 213));
		}

		//Init rune containers
		for(int i = 0; i < tile.getChainLength(); i++) {
			this.updateRuneContainer(i);
		}

		//TODO Need a better way than doing this in ctor
		this.table.openContainer(this);
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);

		this.table.closeContainer();
	}

	public PlayerEntity getPlayer() {
		return this.player;
	}

	public Page getCurrentPage() {
		return this.currentPage;
	}

	public void setCurrentPage(int page) {
		this.currentPage.isCurrent = false;
		this.currentPage = this.pages.get(page);
		this.currentPage.isCurrent = true;
	}

	public int getPages() {
		return this.pages.size();
	}

	public int getShiftHoleSlot(int slotIndex, boolean back) {
		int shiftHole = this.getRuneShiftHoleIndex(slotIndex - this.table.getChainStart(), back);
		return shiftHole >= 0 ? shiftHole + this.table.getChainStart() : -1;
	}

	public void shiftSlot(int slotIndex, boolean back) {
		this.shiftRune(slotIndex - this.table.getChainStart(), back);
	}

	@Nullable
	public INodeBlueprint<?, IRuneExecutionContext> getRuneBlueprint(int slotIndex) {
		IRuneContainer container = this.getRuneContainer(slotIndex - this.table.getChainStart());
		return container != null ? container.getBlueprint() : null;
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		ItemStack prevStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);

		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getStack();
			prevStack = slotStack.copy();

			if(slotIndex < this.table.getContainerSize()) {
				if (!this.moveItemStackTo(slotStack, this.table.getContainerSize(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(slotStack, 0, this.table.getContainerSize(), false)) {
				return ItemStack.EMPTY;
			}

			if(slotStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if(prevStack.getCount() != slotStack.getCount() && slotIndex < this.table.getContainerSize()) {
				slot.onTake(player, slotStack);
			}
		}

		return prevStack;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
		if(slotId > 0 && slotId < this.table.getMaxChainLength() + 1 && clickType == ClickType.PICKUP && dragType == 1) {
			if(this.getSelectedSlot() == slotId) {
				this.setSelectedSlot(-1);
			} else {
				this.setSelectedSlot(slotId);
			}
			return ItemStack.EMPTY;
		}
		return super.slotClick(slotId, dragType, clickType, player);
	}

	public void setSelectedSlot(int slot) {
		this.setSelectedRune(slot - this.table.getChainStart());
	}

	public int getSelectedSlot() {
		int selected = this.getSelectedRuneIndex();
		return selected >= 0 ? selected + this.table.getChainStart() : -1;
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return this.table.stillValid(player);
	}

	public void setChanged(int slotIndex) {
		if(slotIndex >= this.table.getChainStart() && !this.updateRuneContainer(slotIndex - this.table.getChainStart())) {
			//Makes sure this is called when items are added/removed so they can't be duped from rune chain item
			this.onRunesChanged();
		}

		if(slotIndex == 0) {
			this.updateInputsFromRuneChain();
		}
	}

	@Override
	public int getRuneInventorySize() {
		return this.table.getMaxChainLength();
	}

	@Override
	public int getSelectedRuneIndex() {
		return this.selectedRune >= 0 ? this.selectedRune : -1;
	}

	@Override
	public void setSelectedRune(int runeIndex) {
		if(runeIndex >= 0 && runeIndex < this.table.getChainLength()) {
			this.selectedRune = runeIndex;
		} else {
			this.selectedRune = -1;
		}
	}

	@Override
	public ItemStack getRuneItemStack(int runeIndex) {
		return this.table.getItem(runeIndex + this.table.getChainStart());
	}

	@Override
	public void setRuneItemStack(int runeIndex, ItemStack stack) {
		this.table.setItem(runeIndex + this.table.getChainStart(), stack);
	}

	@Override
	public void shiftRune(int runeIndex, boolean back) {
		int chainStart = this.table.getChainStart();
		int slotIndex = runeIndex + chainStart;

		int hole = this.getShiftHoleSlot(runeIndex + chainStart, back);

		if(hole >= 0) {
			if(back) {
				for(int i = hole; i <= slotIndex - 1; ++i) {
					RuneContainerEntry containerEntry = this.runeContainers.get(i - chainStart + 1);
					if(containerEntry != null) {
						this.runeContainers.put(i - chainStart, containerEntry);
						this.runeContainers.remove(i - chainStart + 1);
						containerEntry.runeIndex = i - chainStart;
					}

					Slot slot = this.getSlot(i);
					Slot nextSlot = this.getSlot(i + 1);
					slot.putStack(nextSlot.getStack());
					nextSlot.putStack(ItemStack.EMPTY);

					if(i + 1 == this.getSelectedSlot()) {
						this.setSelectedSlot(this.getSelectedSlot() - 1);
					}

					for(RuneContainerEntry entry : this.runeContainers.values()) {
						entry.container.onRuneMoved(i - chainStart + 1, i - chainStart);
					}

					this.table.getContainerData().moveRuneData(i - chainStart + 1, i - chainStart);

					if(slot instanceof SlotRuneWeavingTableInput && nextSlot instanceof SlotRuneWeavingTableInput) {
						((SlotRuneWeavingTableInput) slot).prevHoverTicks = ((SlotRuneWeavingTableInput) slot).hoverTicks = ((SlotRuneWeavingTableInput) nextSlot).hoverTicks;
						((SlotRuneWeavingTableInput) nextSlot).prevHoverTicks = ((SlotRuneWeavingTableInput) nextSlot).hoverTicks = 0;
					}
				}
			} else {
				for(int i = hole; i >= slotIndex + 1; --i) {
					RuneContainerEntry containerEntry = this.runeContainers.get(i - chainStart - 1);
					if(containerEntry != null) {
						this.runeContainers.put(i - chainStart, containerEntry);
						this.runeContainers.remove(i - chainStart - 1);
						containerEntry.runeIndex = i - chainStart;
					}

					Slot slot = this.getSlot(i);
					Slot prevSlot = this.getSlot(i - 1);
					slot.putStack(prevSlot.getStack());
					prevSlot.putStack(ItemStack.EMPTY);

					if(i - 1 == this.getSelectedSlot()) {
						this.setSelectedSlot(this.getSelectedSlot() + 1);
					}

					for(RuneContainerEntry entry : this.runeContainers.values()) {
						entry.container.onRuneMoved(i - chainStart - 1, i - chainStart);
					}

					this.table.getContainerData().moveRuneData(i - chainStart - 1, i - chainStart);

					if(slot instanceof SlotRuneWeavingTableInput && prevSlot instanceof SlotRuneWeavingTableInput) {
						((SlotRuneWeavingTableInput) slot).prevHoverTicks = ((SlotRuneWeavingTableInput) slot).hoverTicks = ((SlotRuneWeavingTableInput) prevSlot).hoverTicks;
						((SlotRuneWeavingTableInput) prevSlot).prevHoverTicks = ((SlotRuneWeavingTableInput) prevSlot).hoverTicks = 0;
					}
				}
			}
		}

		this.onRunesChanged();

		this.table.setChanged();
	}

	@Override
	public int getRuneShiftHoleIndex(int runeIndex, boolean back) {
		int chainStart = this.table.getChainStart();
		int slotIndex = runeIndex + chainStart;

		if(back) {
			for(int i = slotIndex; i > TileEntityRuneWeavingTable.NON_INPUT_SLOTS - 1; --i) {
				if(i != slotIndex && !this.getSlot(i).hasItem()) {
					return i - chainStart;
				}
			}
		} else {
			for(int i = slotIndex; i < this.table.getMaxChainLength() + TileEntityRuneWeavingTable.NON_INPUT_SLOTS; ++i) {
				if(i != slotIndex && !this.getSlot(i).hasItem()) {
					return i - chainStart;
				}
			}
		}

		return -1;
	}

	@Override
	public Collection<Integer> getLinkedInputs(int runeIndex) {
		return this.table.getContainerData().getLinkedInputs(runeIndex);
	}

	@Override
	public IRuneLink getLink(int runeIndex, int input) {
		return this.table.getContainerData().getLink(runeIndex, input);
	}

	@Override
	public boolean link(int runeIndex, int input, int outputRuneIndex, int output) {
		//TODO Validate that link is possible, especially on server side

		if(this.table.getContainerData().link(runeIndex, input, outputRuneIndex, output)) {
			for(RuneContainerEntry entry : this.runeContainers.values()) {
				entry.container.onTokenLinked(input, this.table.getContainerData().getLink(runeIndex, input));
			}

			for(int i = 0; i < this.table.getMaxChainLength(); i++) {
				this.updateConfiguration(i);
			}

			this.onRunesChanged();

			this.table.setChanged();

			return true;
		}
		return false;
	}

	@Override
	public IRuneLink unlink(int runeIndex, int input) {
		IRuneLink unlinked = this.table.getContainerData().unlink(runeIndex, input);

		if(unlinked != null) {
			for(RuneContainerEntry entry : this.runeContainers.values()) {
				entry.container.onTokenUnlinked(input, unlinked);
			}

			for(int i = 0; i < this.table.getMaxChainLength(); i++) {
				this.updateConfiguration(i);
			}

			this.onRunesChanged();

			this.table.setChanged();
		}

		return unlinked;
	}

	@Override
	public void unlinkAll(int runeIndex) {
		Map<Integer, IRuneLink> links = new HashMap<>();

		for(int linkedInput : this.table.getContainerData().getLinkedInputs(runeIndex)) {
			IRuneLink link = this.table.getContainerData().getLink(runeIndex, linkedInput);
			links.put(linkedInput, link);
		}

		this.table.getContainerData().unlinkAll(runeIndex);

		for(Entry<Integer, IRuneLink> unlinked : links.entrySet()) {
			for(RuneContainerEntry entry : this.runeContainers.values()) {
				entry.container.onTokenUnlinked(unlinked.getKey(), unlinked.getValue());
			}
		}

		for(int i = 0; i < this.table.getMaxChainLength(); i++) {
			this.updateConfiguration(i);
		}

		this.onRunesChanged();

		this.table.setChanged();
	}

	/**
	 * Gets all new configurations of the rune at the specified index and then compares it to its current configuration.
	 * If any of the new configurations' ID matches with the current configuration then the current configuration is replaced
	 * with the matching new configuration.
	 * @param runeIndex
	 */
	protected void updateConfiguration(int runeIndex) {
		RuneContainerEntry entry = this.runeContainers.get(runeIndex);

		if(entry != null) {
			INodeConfiguration currentConfiguration = entry.container.getContext().getConfiguration();

			if(currentConfiguration != null) {
				for(INodeConfiguration newConfiguration : entry.container.getBlueprint().getConfigurations(this.createLinkAccess(runeIndex), false)) {
					if(currentConfiguration != newConfiguration && currentConfiguration.getId() == newConfiguration.getId()) {
						Pair<INodeConfiguration, INodeConfiguration> newConfigurations = ContainerRuneWeavingTable.this.getConfigurations(this.createLinkAccess(runeIndex), entry.container.getBlueprint(), newConfiguration);
						
						entry.configuration = newConfigurations.getKey();
						entry.provisionalConfiguration = newConfigurations.getRight();

						this.table.getContainerData().setConfigurationId(entry.runeIndex, newConfiguration.getId());

						entry.container.onConfigurationChanged(currentConfiguration, newConfiguration);

						this.onRunesChanged();

						this.table.setChanged();

						//TODO Check links for validity

						break;
					}
				}
			}
		}
	}

	@Override
	public void moveRuneData(int fromRuneIndex, int toRuneIndex) {
		this.table.getContainerData().moveRuneData(fromRuneIndex, toRuneIndex);

		for(RuneContainerEntry entry : this.runeContainers.values()) {
			entry.container.onRuneDataMoved(fromRuneIndex, toRuneIndex);
		}

		this.onRunesChanged();

		this.table.setChanged();
	}

	@Override
	public IRuneContainer getRuneContainer(int runeIndex) {
		RuneContainerEntry entry = this.runeContainers.get(runeIndex);
		return entry != null ? entry.container : null;
	}

	public IRuneContainerContext getRuneContainerContext(int runeIndex) {
		RuneContainerEntry entry = this.runeContainers.get(runeIndex);
		return entry != null ? entry.context : null;
	}

	protected boolean updateRuneContainer(int runeIndex) {
		ItemStack stack = this.getRuneItemStack(runeIndex);

		IRuneCapability runeCap = null;
		IRuneContainerFactory factory = null;

		if(!stack.isEmpty() && (runeCap = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE, null)) != null && (factory = runeCap.getRuneContainerFactory()) != null) {
			RuneContainerEntry currentContainerEntry = this.runeContainers.get(runeIndex);

			boolean isDifferentContainer = false;
			IRuneContainer newContainer;

			if(currentContainerEntry == null) {
				//No container currently exists, can be replaced directly
				newContainer = factory.createContainer();
				isDifferentContainer = true;
			} else {
				//Container already exists, need to check if the container needs to be replaced
				newContainer = currentContainerEntry.container.updateRuneContainer(stack, factory);
				isDifferentContainer = !currentContainerEntry.container.equals(newContainer);
			}

			if(isDifferentContainer) {
				//Remove current container if one already exists
				if(currentContainerEntry != null) {
					this.removeContainerEntry(runeIndex);
				}

				RuneContainerEntry entry = new RuneContainerEntry(runeIndex);

				entry.container = newContainer;

				IRuneContainerContext newContainerContext = this.createRuneContainerContext(entry);

				entry.context = newContainerContext;

				newContainer.setContext(newContainerContext);

				IRuneChainContainerData containerData = ContainerRuneWeavingTable.this.table.getContainerData();

				if(containerData.hasConfigurationId(entry.runeIndex)) {
					int savedConfigurationId = containerData.getConfigurationId(entry.runeIndex);

					for(INodeConfiguration configuration : entry.container.getBlueprint().getConfigurations(this.createLinkAccess(runeIndex), false)) {
						if(configuration.getId() == savedConfigurationId) {
							Pair<INodeConfiguration, INodeConfiguration> newConfigurations = ContainerRuneWeavingTable.this.getConfigurations(this.createLinkAccess(runeIndex), entry.container.getBlueprint(), configuration);
							
							entry.configuration = newConfigurations.getLeft();
							entry.provisionalConfiguration = newConfigurations.getRight();
							
							break;
						}
					}
				}

				if(entry.configuration == null || entry.provisionalConfiguration == null) {
					entry.configuration = entry.container.getBlueprint().getConfigurations(this.createLinkAccess(runeIndex), false).get(0);
					entry.provisionalConfiguration = entry.container.getBlueprint().getConfigurations(this.createLinkAccess(runeIndex), true).get(0);
				}

				newContainer.init();

				this.runeContainers.put(runeIndex, entry);

				this.onRunesChanged();

				return true;
			}
		} else {
			return this.removeContainerEntry(runeIndex);
		}

		return false;
	}

	protected boolean removeContainerEntry(int runeIndex) {
		RuneContainerEntry entry = this.runeContainers.get(runeIndex);

		if(entry != null) {
			//Remove all links and data
			IRuneChainContainerData containerData = this.table.getContainerData();

			containerData.unlinkAll(runeIndex);
			containerData.unlinkAllIncoming(runeIndex);
			containerData.removeConfigurationId(runeIndex);
			containerData.removeContainerNbt(runeIndex);

			//Remove slots from current container
			entry.removeSlotsFromContainer();

			this.runeContainers.remove(runeIndex);

			this.onRunesChanged();

			return true;
		}

		return false;
	}

	protected IRuneContainerContext createRuneContainerContext(RuneContainerEntry entry) {
		return new IRuneContainerContext() {
			@Override
			public IRuneWeavingTableContainer getRuneWeavingTableContainer() {
				return ContainerRuneWeavingTable.this;
			}

			@Override
			public IRuneWeavingTableGui getRuneWeavingTableGui() {
				return ContainerRuneWeavingTable.this.getRuneWeavingTableGui();
			}

			@Override
			public int getRuneIndex() {
				return entry.runeIndex;
			}

			@Override
			public ItemStack getRuneItemStack() {
				return ContainerRuneWeavingTable.this.getRuneItemStack(entry.runeIndex);
			}

			@Override
			public CompoundNBT getData() {
				IRuneChainContainerData info = ContainerRuneWeavingTable.this.table.getContainerData();
				CompoundNBT nbt = info.getContainerNbt(entry.runeIndex);
				if(nbt == null) {
					info.setContainerNbt(entry.runeIndex, nbt = new CompoundNBT());
					ContainerRuneWeavingTable.this.table.setChanged();
				}
				return nbt;
			}

			@Override
			public void setData(CompoundNBT nbt) {
				ContainerRuneWeavingTable.this.table.getContainerData().setContainerNbt(entry.runeIndex, nbt);
				ContainerRuneWeavingTable.this.table.setChanged();
			}

			@Override
			public void addSlot(Slot slot) {
				entry.slots.add(slot);
			}

			@Override
			public INodeConfiguration getConfiguration() {
				return entry.configuration;
			}
			
			@Override
			public INodeConfiguration getProvisionalConfiguration() {
				return entry.provisionalConfiguration;
			}

			@Override
			public void setConfiguration(INodeConfiguration configuration) {
				if(entry.configuration != configuration && entry.provisionalConfiguration != configuration) {
					Pair<INodeConfiguration, INodeConfiguration> newConfigurations = ContainerRuneWeavingTable.this.getConfigurations(ContainerRuneWeavingTable.this.createLinkAccess(entry.runeIndex), entry.container.getBlueprint(), configuration);
					
					entry.configuration = newConfigurations.getLeft();
					entry.provisionalConfiguration = newConfigurations.getRight();

					ContainerRuneWeavingTable.this.table.getContainerData().setConfigurationId(entry.runeIndex, configuration.getId());

					//TODO Preferably store links per configuration
					ContainerRuneWeavingTable.this.table.getContainerData().unlinkAll(entry.runeIndex);
					ContainerRuneWeavingTable.this.table.getContainerData().unlinkAllIncoming(entry.runeIndex);

					entry.container.onConfigurationChanged(entry.configuration, configuration);

					ContainerRuneWeavingTable.this.onRunesChanged();

					ContainerRuneWeavingTable.this.table.setChanged();
				}
			}

			@Override
			public IConfigurationLinkAccess getLinkAccess() {
				return ContainerRuneWeavingTable.this.createLinkAccess(entry.runeIndex);
			}
		};
	}

	protected Pair<INodeConfiguration, INodeConfiguration> getConfigurations(@Nullable IConfigurationLinkAccess access, INodeBlueprint<?, IRuneExecutionContext> blueprint, INodeConfiguration configuration) {
		INodeConfiguration finalConfiguration = null;
		for(INodeConfiguration config : blueprint.getConfigurations(access, false)) {
			if(config.getId() == configuration.getId()) {
				finalConfiguration = config;
			}
		}

		INodeConfiguration provisionalConfiguration = null;
		for(INodeConfiguration config : blueprint.getConfigurations(access, true)) {
			if(config.getId() == configuration.getId()) {
				provisionalConfiguration = config;
			}
		}

		return Pair.of(finalConfiguration, provisionalConfiguration);
	}

	protected IRuneWeavingTableGui getRuneWeavingTableGui() {
		return null;
	}

	@Override
	public Slot getRuneSlot(int runeIndex) {
		return this.getSlot(runeIndex + this.table.getChainStart());
	}

	private boolean updateOutput = true;
	private boolean updateInputs = true;

	@Override
	public void onRunesChanged() {
		//TODO This needs tp be moved to and called from the tileentity so that the output also updates when a rune is inserted without container

		if(this.updateOutput) {
			this.updateOutput = false;
			this.updateInputs = false; //Don't update inputs since the rune chain will contain the exact same input data

			try {
				ItemStack stack = this.table.getItem(0);

				if(!stack.isEmpty()) {
					IRuneChainCapability cap = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null);

					if(cap != null) {
						int runeCount = this.getRuneInventorySize();

						NonNullList<ItemStack> runes = NonNullList.withSize(runeCount, ItemStack.EMPTY);
						for(int i = 0; i < runeCount; i++) {
							runes.set(i, this.getRuneItemStack(i));
						}

						RuneChainData data = new RuneChainData(runes, this.table.getContainerData());

						cap.setData(data);
					}
				}

				this.table.setItem(0, stack);
			} finally {
				this.updateOutput = true;
				this.updateInputs = true;
			}
		}
	}

	//TODO This too needs to be moved to and called from the tileentity, see above
	protected void updateInputsFromRuneChain() {
		ItemStack stack = this.table.getItem(0);

		if(this.updateOutput && this.updateInputs) {
			this.updateOutput = false;

			try {
				boolean hasData = false;

				if(!stack.isEmpty()) {
					IRuneChainCapability cap = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null);

					if(cap != null) {
						IRuneChainData data = cap.getData();

						if(data != null) {
							hasData = true;

							NonNullList<ItemStack> runes = data.getRuneItems();

							//Set data *before* setting runes so that the rune containers are initialized correctly!
							this.table.setContainerData(data.getContainerData());

							for(int i = 0; i < this.getRuneInventorySize(); i++) {
								if(i < runes.size()) {
									this.setRuneItemStack(i, runes.get(i));
								} else {
									this.setRuneItemStack(i, ItemStack.EMPTY);
								}
							}
						}
					}
				}

				if(!hasData) {
					for(int i = 0; i < this.getRuneInventorySize(); i++) {
						this.setRuneItemStack(i, ItemStack.EMPTY);
					}
				}
			} finally {
				this.updateOutput = true;
			}
		}
	}
}