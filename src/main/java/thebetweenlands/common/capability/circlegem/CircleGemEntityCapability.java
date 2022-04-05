package thebetweenlands.common.capability.circlegem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.capability.IEntityCircleGemCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class CircleGemEntityCapability extends EntityCapability<CircleGemEntityCapability, IEntityCircleGemCapability, LivingEntity> implements IEntityCircleGemCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "entity_gems");
	}

	@Override
	protected CircleGemEntityCapability getDefaultCapabilityImplementation() {
		return new CircleGemEntityCapability();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Capability<IEntityCircleGemCapability> getCapability() {
		return (Capability<IEntityCircleGemCapability>) (Capability<?>) CapabilityRegistry.CAPABILITY_ENTITY_CIRCLE_GEM;
	}

	@Override
	protected Class<IEntityCircleGemCapability> getCapabilityClass() {
		return IEntityCircleGemCapability.class;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return CircleGemHelper.isApplicable(entity);
	}

	@Override
	public boolean isPersistent(PlayerEntity oldPlayer, PlayerEntity newPlayer, boolean wasDead) {
		return !wasDead || this.getEntity().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
	}





	private List<CircleGem> gems = new ArrayList<CircleGem>();

	@Override
	public boolean canAdd(CircleGem gem) {
		return true;
	}

	@Override
	public void addGem(CircleGem gem) {
		if(this.canAdd(gem)) {
			this.gems.add(gem);
			this.setChanged();
		}
	}

	@Override
	public boolean removeGem(CircleGem gem) {
		Iterator<CircleGem> gemIT = this.gems.iterator();
		while(gemIT.hasNext()) {
			CircleGem currentGem = gemIT.next();
			if(currentGem.getGemType() == gem.getGemType() && currentGem.getCombatType() == gem.getCombatType()) {
				gemIT.remove();
				this.setChanged();
				return true;
			}
		}
		return false;
	}

	@Override
	public List<CircleGem> getGems() {
		return Collections.unmodifiableList(this.gems);
	}

	@Override
	public boolean removeAll() {
		boolean hadGems = !this.gems.isEmpty();
		this.gems.clear();
		return hadGems;
	}

	@Override
	public void save(CompoundNBT nbt) {
		ListNBT gemList = new ListNBT();
		for(CircleGem gem : this.gems) {
			CompoundNBT gemCompound = new CompoundNBT();
			gem.save(gemCompound);
			gemList.add(gemCompound);
		}
		nbt.put("gems", gemList);
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.gems.clear();
		ListNBT gemList = nbt.getList("gems", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < gemList.size(); i++) {
			CompoundNBT gemCompound = gemList.getCompound(i);
			CircleGem gem = CircleGem.load(gemCompound);
			if(gem != null) {
				this.gems.add(gem);
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
		return 0;
	}
}
