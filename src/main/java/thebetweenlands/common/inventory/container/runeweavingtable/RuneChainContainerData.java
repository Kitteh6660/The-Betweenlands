package thebetweenlands.common.inventory.container.runeweavingtable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.runechain.container.IRuneChainContainerData;
import thebetweenlands.api.runechain.container.IRuneLink;

public class RuneChainContainerData implements IRuneChainContainerData {
	
	public static final class Link implements IRuneLink {
		private int outputRune;
		private int output;

		private Link(int outputRune, int output) {
			this.outputRune = outputRune;
			this.output = output;
		}

		@Override
		public int getOutputRune() {
			return this.outputRune;
		}

		@Override
		public int getOutput() {
			return this.output;
		}
	}

	private final Map<Integer, Map<Integer, Link>> links = new HashMap<>();
	private final Map<Integer, CompoundNBT> containerNbt = new HashMap<>();
	private final Map<Integer, Integer> configurationIds = new HashMap<>();

	@Override
	public Collection<Integer> getLinkedInputs(int runeIndex) {
		Set<Integer> linkedSlots = new HashSet<>();
		Map<Integer, Link> links = this.links.get(runeIndex);
		if(links != null) {
			for(Entry<Integer, Link> link : links.entrySet()) {
				linkedSlots.add(link.getKey());
			}
		}
		return linkedSlots;
	}

	@Override
	@Nullable
	public Link getLink(int runeIndex, int input) {
		if(input >= 0) {
			Map<Integer, Link> links = this.links.get(runeIndex);
			if(links != null) {
				return links.get(input);
			}
		}
		return null;
	}

	@Override
	public boolean link(int runeIndex, int input, int outputRuneIndex, int output) {
		if(runeIndex <= outputRuneIndex) {
			return false;
		}

		Map<Integer, Link> links = this.links.get(runeIndex);

		if(links == null) {
			this.links.put(runeIndex, links = new HashMap<>());
		}

		links.put(input, new Link(outputRuneIndex, output));

		return true;
	}

	@Override
	@Nullable
	public Link unlink(int runeIndex, int input) {
		Map<Integer, Link> links = this.links.get(runeIndex);
		if(links != null) {
			Link removed = links.remove(input);
			if(links.isEmpty()) {
				this.links.remove(runeIndex);
			}
			return removed;
		}
		return null;
	}

	@Override
	public void unlinkAll(int runeIndex) {
		this.links.remove(runeIndex);
	}

	@Override
	public void unlinkAllIncoming(int runeIndex) {
		Iterator<Entry<Integer, Map<Integer, Link>>> entryIT = this.links.entrySet().iterator();
		while(entryIT.hasNext()) {
			Entry<Integer, Map<Integer, Link>> entry = entryIT.next();

			Iterator<Entry<Integer, Link>> linksIT = entry.getValue().entrySet().iterator();
			while(linksIT.hasNext()) {
				if(linksIT.next().getValue().outputRune == runeIndex) {
					linksIT.remove();
				}
			}

			if(entry.getValue().isEmpty()) {
				entryIT.remove();
			}
		}
	}

	@Override
	public void moveRuneData(int fromRune, int toRune) {
		//First adjust links that point towards the old position
		for(Entry<Integer, Map<Integer, Link>> entry : this.links.entrySet()) {
			Map<Integer, Link> links = entry.getValue();
			for(Link link : links.values()) {
				if(link.outputRune == fromRune) {
					link.outputRune = toRune;
				}
			}
		}

		Map<Integer, Link> links = this.links.get(fromRune);

		if(links != null) {
			Map<Integer, Link> newPosLinks = this.links.get(toRune);
			if(newPosLinks == null) {
				this.links.put(toRune, newPosLinks = new HashMap<>());
			} else {
				newPosLinks.clear();
			}

			newPosLinks.putAll(links);

			this.links.remove(fromRune);
		} else {
			if(this.links.containsKey(toRune)) {
				this.links.remove(toRune);
			}
		}
		
		if(this.containerNbt.containsKey(fromRune)) {
			this.containerNbt.put(toRune, this.containerNbt.get(fromRune));
		}
		
		if(this.configurationIds.containsKey(fromRune)) {
			this.configurationIds.put(toRune, this.configurationIds.get(fromRune));
		}
	}

	@Override
	public CompoundNBT getContainerNbt(int runeIndex) {
		return this.containerNbt.get(runeIndex);
	}

	@Override
	public void setContainerNbt(int runeIndex, CompoundNBT nbt) {
		this.containerNbt.put(runeIndex, nbt);
	}

	@Override
	public int getConfigurationId(int runeIndex) {
		Integer id = this.configurationIds.get(runeIndex);
		return id != null ? id : 0;
	}

	@Override
	public boolean hasConfigurationId(int runeIndex) {
		return this.configurationIds.containsKey(runeIndex);
	}

	@Override
	public void setConfigurationId(int runeIndex, int configurationId) {
		this.configurationIds.put(runeIndex, configurationId);
	}

	@Override
	public void removeConfigurationId(int runeIndex) {
		this.configurationIds.remove(runeIndex);
	}

	@Override
	public void removeContainerNbt(int runeIndex) {
		this.containerNbt.remove(runeIndex);
	}

	public static CompoundNBT save(IRuneChainContainerData data, CompoundNBT nbt) {
		ListNBT linksNbt = new ListNBT();

		Map<Integer, Map<Integer, IRuneLink>> links = data.getLinks();

		for(Entry<Integer, Map<Integer, IRuneLink>> linkEntry : links.entrySet()) {
			CompoundNBT linkEntryNbt = new CompoundNBT();

			linkEntryNbt.putInt("inputRune", linkEntry.getKey());

			ListNBT runeLinksNbt = new ListNBT();

			for(Entry<Integer, IRuneLink> runeLinkEntry : linkEntry.getValue().entrySet()) {
				CompoundNBT linkNbt = new CompoundNBT();

				linkNbt.putInt("input", runeLinkEntry.getKey());
				linkNbt.putInt("outputRune", runeLinkEntry.getValue().getOutputRune());
				linkNbt.putInt("output", runeLinkEntry.getValue().getOutput());

				runeLinksNbt.add(linkNbt);
			}

			linkEntryNbt.put("links", runeLinksNbt);

			linksNbt.add(linkEntryNbt);
		}

		nbt.put("links", linksNbt);

		ListNBT dataNbt = new ListNBT();

		for(Entry<Integer, CompoundNBT> dataEntry : data.getContainerNbt().entrySet()) {
			CompoundNBT dataEntryNbt = new CompoundNBT();

			CompoundNBT containerNbt = dataEntry.getValue();

			if(containerNbt != null) {
				dataEntryNbt.putInt("rune", dataEntry.getKey());
				dataEntryNbt.put("nbt", containerNbt);

				dataNbt.add(dataEntryNbt);
			}
		}

		nbt.put("data", dataNbt);

		ListNBT configurationsNbt = new ListNBT();

		for(Entry<Integer, Integer> dataEntry : data.getConfigurationIds().entrySet()) {
			CompoundNBT configurationEntryNbt = new CompoundNBT();

			Integer id = dataEntry.getValue();

			if(id != null) {
				configurationEntryNbt.putInt("rune", dataEntry.getKey());
				configurationEntryNbt.putInt("configuration", id);

				configurationsNbt.add(configurationEntryNbt);
			}
		}

		nbt.put("configurations", configurationsNbt);

		return nbt;
	}

	public static RuneChainContainerData load(CompoundNBT nbt) {
		RuneChainContainerData containerData = new RuneChainContainerData();

		ListNBT linksNbt = nbt.getList("links", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < linksNbt.size(); i++) {
			CompoundNBT linkEntryNbt = linksNbt.getCompound(i);

			int inputRune = linkEntryNbt.getInt("inputRune");

			ListNBT runeLinksNbt = linkEntryNbt.getList("links", Constants.NBT.TAG_COMPOUND);
			for(int j = 0; j < runeLinksNbt.size(); j++) {
				CompoundNBT linkNbt = runeLinksNbt.getCompound(j);

				int input = linkNbt.getInt("input");
				int outputRune = linkNbt.getInt("outputRune");
				int output = linkNbt.getInt("output");

				containerData.link(inputRune, input, outputRune, output);
			}
		}

		ListNBT dataNbt = nbt.getList("data", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < dataNbt.size(); i++) {
			CompoundNBT dataEntryNbt = dataNbt.getCompound(i);
			containerData.setContainerNbt(dataEntryNbt.getInt("rune"), dataEntryNbt.getCompound("nbt"));
		}

		ListNBT configurationsNbt = nbt.getList("configurations", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < configurationsNbt.size(); i++) {
			CompoundNBT configurationEntryNbt = configurationsNbt.getCompound(i);
			containerData.setConfigurationId(configurationEntryNbt.getInt("rune"), configurationEntryNbt.getInt("configuration"));
		}
		
		return containerData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, Map<Integer, Link>> getLinks() {
		return this.links;
	}

	@Override
	public Map<Integer, Integer> getConfigurationIds() {
		return this.configurationIds;
	}

	@Override
	public Map<Integer, CompoundNBT> getContainerNbt() {
		return this.containerNbt;
	}
}
