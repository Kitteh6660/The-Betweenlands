package thebetweenlands.common.capability.item;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.capability.IRuneChainCapability;
import thebetweenlands.api.runechain.chain.IRuneChainBlueprint;
import thebetweenlands.api.runechain.chain.IRuneChainData;
import thebetweenlands.api.runechain.chain.RuneChainFactory;
import thebetweenlands.api.runechain.initiation.InitiationState;
import thebetweenlands.common.capability.base.ItemCapability;
import thebetweenlands.common.herblore.rune.RuneChainData;
import thebetweenlands.common.item.herblore.rune.ItemRuneChain;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class RuneChainItemCapability extends ItemCapability<RuneChainItemCapability, IRuneChainCapability> implements IRuneChainCapability {
	
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "rune_chain");

	@Override
	public boolean isApplicable(Item item) {
		return item instanceof ItemRuneChain;
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	protected RuneChainItemCapability getDefaultCapabilityImplementation() {
		return new RuneChainItemCapability();
	}

	@Override
	protected Capability<IRuneChainCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_RUNE_CHAIN;
	}

	@Override
	protected Class<IRuneChainCapability> getCapabilityClass() {
		return IRuneChainCapability.class;
	}

	private IRuneChainData data;
	private IRuneChainBlueprint blueprint;

	private InitiationState<?> initiationState;

	public static final String RUNE_CHAIN_BLUEPRINT_NBT_KEY = "thebetweenlands.runechain.blueprint";

	@Override
	protected void init() {

	}

	@Override
	public void setData(IRuneChainData data) {
		this.data = data;

		CompoundNBT itemNbt = this.getItemStack().getTag();

		if(data != null) {
			this.blueprint =createBlueprint(data);

			if(itemNbt == null) {
				itemNbt = new CompoundNBT();
			}

			itemNbt.put(RUNE_CHAIN_BLUEPRINT_NBT_KEY, RuneChainData.save(data, new CompoundNBT()));

			this.getItemStack().setTag(itemNbt);
		} else {
			this.blueprint = null;

			if(itemNbt != null) {
				itemNbt.remove(RUNE_CHAIN_BLUEPRINT_NBT_KEY);
			}
		}
	}

	public static IRuneChainBlueprint createBlueprint(IRuneChainData data) {
		return RuneChainFactory.INSTANCE.create(data);
	}

	protected void initFromNbt() {
		if(this.data == null) {
			this.blueprint = null;

			CompoundNBT itemNbt = this.getItemStack().getTag();

			if(itemNbt != null && itemNbt.contains(RUNE_CHAIN_BLUEPRINT_NBT_KEY, Constants.NBT.TAG_COMPOUND)) {
				this.data = RuneChainData.load(itemNbt.getCompound(RUNE_CHAIN_BLUEPRINT_NBT_KEY));
				this.blueprint = createBlueprint(this.data);
			}
		}
	}

	@Override
	public IRuneChainData getData() {
		this.initFromNbt();
		return this.data;
	}

	@Override
	public IRuneChainBlueprint getBlueprint() {
		this.initFromNbt();
		return this.blueprint;
	}

	@Override
	public void setInitiationState(InitiationState<?> state) {
		this.initiationState = state;
	}

	@Override
	public InitiationState<?> getInitiationState() {
		return this.initiationState;
	}
}
