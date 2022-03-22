package thebetweenlands.common.capability.foodsickness;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.handler.FoodSicknessHandler;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class FoodSicknessEntityCapability extends EntityCapability<FoodSicknessEntityCapability, IFoodSicknessCapability, PlayerEntity> implements IFoodSicknessCapability, ISerializableCapability {
	
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "food_sickness");
	}

	@Override
	protected Capability<IFoodSicknessCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_FOOD_SICKNESS;
	}

	@Override
	protected Class<IFoodSicknessCapability> getCapabilityClass() {
		return IFoodSicknessCapability.class;
	}

	@Override
	protected FoodSicknessEntityCapability getDefaultCapabilityImplementation() {
		return new FoodSicknessEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof PlayerEntity;
	}

	@Override
	public boolean isPersistent(PlayerEntity oldPlayer, PlayerEntity newPlayer, boolean wasDead) {
		return true;
	}



	private Map<Item, Integer> hatredMap = Maps.newHashMap();
	private FoodSickness lastSickness = FoodSickness.FINE;

	@Override
	public FoodSickness getLastSickness() {
		return this.lastSickness;
	}

	@Override
	public void setLastSickness(FoodSickness sickness) {
		this.lastSickness = sickness;
	}

	@Override
	public FoodSickness getSickness(Item food) {
		return FoodSickness.getSicknessForHatred(this.getFoodHatred(food));
	}

	@Override
	public void decreaseHatredForAllExcept(Item food, int decrease) {
		if(decrease > 0) {
			Map<Item, Integer> newHatredMap = Maps.newHashMap();
			for (Item key : this.hatredMap.keySet()) {
				if (key != food) {
					newHatredMap.put(key, Math.max(this.hatredMap.get(key) - decrease, 0));
				}
			}
			if(!newHatredMap.isEmpty()) {
				this.hatredMap.putAll(newHatredMap);
				this.setChanged();
			}
		}
	}

	@Override
	public void increaseFoodHatred(Item food, int amount, int decreaseForOthers) {
		if (!FoodSicknessHandler.isFoodSicknessEnabled(this.getEntity().level))
			return;
		int finalMaxHatred = FoodSickness.VALUES[Math.max(FoodSickness.VALUES.length - 1, 0)].maxHatred;
		if (this.hatredMap.containsKey(food)) {
			int currentAmount = this.hatredMap.get(food);
			this.hatredMap.put(food, Math.max(Math.min(currentAmount + amount, finalMaxHatred), 0));
		} else {
			this.hatredMap.put(food, Math.max(Math.min(amount, finalMaxHatred), 0));
		}
		this.decreaseHatredForAllExcept(food, decreaseForOthers);
		this.setChanged();
	}

	@Override
	public int getFoodHatred(Item food) {
		if (this.hatredMap.containsKey(food)) {
			return this.hatredMap.get(food);
		}
		return 0;
	}

	@Override
	public void save(CompoundNBT nbt) {
		ListNBT list = new ListNBT();
		for (Map.Entry<Item, Integer> entry : this.hatredMap.entrySet()) {
			CompoundNBT listCompound = new CompoundNBT();
			listCompound.putString("Food", entry.getKey().getRegistryName().toString());
			listCompound.putInt("Level", entry.getValue());
			list.add(listCompound);
		}
		nbt.put("HatredMap", list);
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.hatredMap = Maps.newHashMap();
		ListNBT list = nbt.getList("HatredMap", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT listCompound = list.getCompound(i);
			Item food = Item.getByNameOrId(listCompound.getString("Food"));
			if(food != null) {
				int level = listCompound.getInt("Level");
				this.hatredMap.put(food, level);
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
