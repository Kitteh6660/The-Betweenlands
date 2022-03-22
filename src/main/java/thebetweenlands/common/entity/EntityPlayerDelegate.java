package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.Attributes.AttributeModifierMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class EntityPlayerDelegate extends FakePlayer {
	public static class InventoryDelegateList extends NonNullList<ItemStack> {
		private final IInventory inv;
		private final int invSize;
		private final int size;
		private final NonNullList<ItemStack> excess;

		public InventoryDelegateList(@Nullable IInventory inv, int requiredSize, List<NonNullList<ItemStack>> excess) {
			this.inv = inv;
			this.invSize = inv == null ? 0 : inv.getContainerSize();
			if(this.invSize < requiredSize) {
				excess.add(this.excess = NonNullList.withSize(requiredSize - this.invSize, ItemStack.EMPTY));
				this.size = requiredSize;
			} else {
				this.excess = NonNullList.withSize(0, ItemStack.EMPTY);
				this.size = this.invSize;
			}
		}

		@Override
		public ItemStack get(int index) {
			if(index >= this.invSize) {
				return this.excess.get(index - this.invSize);
			}
			return this.inv.getItem(index);
		}

		@Override
		public ItemStack set(int index, ItemStack stack) {
			Validate.notNull(stack);
			ItemStack old;
			if(index >= this.invSize) {
				old = this.excess.get(index - this.invSize);
				this.excess.set(index - this.invSize, stack);
			} else {
				old = this.inv.getItem(index);
				this.inv.setItem(index, stack);
			}
			return old;
		}

		@Override
		public void add(int index, ItemStack stack) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ItemStack remove(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return this.size;
		}

		@Override
		public void clear() {
			if(this.inv != null) {
				this.inv.clearContent();
			}
			this.excess.clear();
		}
	}

	public static class DelegatePlayerInventory extends PlayerInventory {
		public DelegatePlayerInventory(PlayerEntity playerIn, @Nullable IInventory main, @Nullable IInventory armor, @Nullable IInventory offhand, List<NonNullList<ItemStack>> excess) {
			super(playerIn);
			this.mainInventory = new InventoryDelegateList(main, 36, excess);
			this.armorInventory = new InventoryDelegateList(main, 4, excess);
			this.offHandInventory = new InventoryDelegateList(main, 1, excess);
			ObfuscationReflectionHelper.setPrivateValue(PlayerInventory.class, this, Arrays.asList(this.mainInventory, this.armorInventory, this.offHandInventory), "allInventories", "field_184440_g", "f");
		}
	}

	public static class Builder {
		private final Function<Builder, EntityPlayerDelegate> constructor;

		private final ServerWorld world;
		private final GameProfile profile;

		private Entity entity;
		private PlayerInventory playerInv;
		private IInventory mainInv, armorInv, offhandInv;

		protected Builder(Function<Builder, EntityPlayerDelegate> constructor, ServerWorld world, GameProfile profile) {
			this.constructor = constructor;
			this.world = world;
			this.profile = profile;
		}

		public ServerWorld world() {
			return this.world;
		}

		public GameProfile profile() {
			return this.profile;
		}

		public Builder entity(@Nullable Entity entity) {
			this.entity = entity;
			return this;
		}

		public Entity entity() {
			return this.entity;
		}

		public Builder playerInventory(@Nullable PlayerInventory inv) {
			this.playerInv = inv;
			return this;
		}

		public PlayerInventory playerInventory() {
			return this.playerInv;
		}

		public Builder mainInventory(@Nullable IInventory inv) {
			this.mainInv = inv;
			return this;
		}

		public IInventory mainInventory() {
			return this.mainInv;
		}

		public Builder armorInventory(@Nullable IInventory inv) {
			this.armorInv = inv;
			return this;
		}

		public IInventory armorInventory() {
			return this.armorInv;
		}

		public Builder offhandInventory(@Nullable IInventory inv) {
			this.offhandInv = inv;
			return this;
		}

		public IInventory offhandInventory() {
			return this.offhandInv;
		}

		public EntityPlayerDelegate build() {
			return this.constructor.apply(this);
		}
	}

	private final Entity entity;
	private final List<NonNullList<ItemStack>> excess;

	private EntityPlayerDelegate(ServerWorld world, GameProfile name, Entity entity) {
		super(world, name);
		this.entity = entity;
		this.excess = new ArrayList<>();
		this.connection = new NetHandlerPlayServer(world.getMinecraftServer(), new NetworkManager(EnumPacketDirection.SERVERBOUND), this);
	}

	private EntityPlayerDelegate(ServerWorld world, GameProfile name, @Nullable Entity parent, @Nullable IInventory main, @Nullable IInventory armor, @Nullable IInventory offhand) {
		this(world, name, parent);
		this.inventory = new DelegatePlayerInventory(parent instanceof PlayerEntity ? (PlayerEntity)parent : this, main, armor, offhand, this.excess);
	}

	private EntityPlayerDelegate(ServerWorld world, GameProfile name, @Nullable Entity entity, PlayerInventory inv) {
		this(world, name, entity);
		this.inventory = inv;
	}

	public static Builder from(ServerWorld world, GameProfile profile, List<NonNullList<ItemStack>> excess) {
		return new Builder((builder) -> {
			if(builder.playerInventory() != null) {
				return new EntityPlayerDelegate(builder.world(), builder.profile(), builder.entity(), builder.playerInventory());
			} else {
				return new EntityPlayerDelegate(builder.world(), builder.profile(), builder.entity(), builder.mainInventory(), builder.armorInventory(), builder.offhandInventory());
			}
		}, world, profile);
	}

	public List<NonNullList<ItemStack>> getExcessInventories() {
		return this.excess;
	}

	@Override
	public boolean isSilent() {
		return true;
	}

	@Override
	public void playSound(SoundEvent soundIn, float volume, float pitch) { }

	@Override
	public boolean startRiding(Entity entityIn) { return false; }

	@Override
	public boolean startRiding(Entity entityIn, boolean force) { return false; }

	@Override
	public AttributeModifierMap getAttributeMap() {
		if(this.entity instanceof LivingEntity) {
			return ((LivingEntity) this.entity).getAttributes();
		}
		return super.getAttributes();
	}

	@Override
	public void addEffect(PotionEffect potioneffectIn) {
		if(this.entity instanceof LivingEntity) {
			((LivingEntity) this.entity).addEffect(potioneffectIn);
		}
	}

	@Override
	public void curePotionEffects(ItemStack curativeItem) {
		if(this.entity instanceof LivingEntity) {
			((LivingEntity) this.entity).curePotionEffects(curativeItem);
		}
	}

	@Override
	public boolean canBeHitWithPotion() {
		if(this.entity instanceof LivingEntity) {
			return ((LivingEntity) this.entity).canBeHitWithPotion();
		}
		return super.canBeHitWithPotion();
	}

	@Override
	public void clearActivePotions() {
		if(this.entity instanceof LivingEntity) {
			((LivingEntity) this.entity).clearActivePotions();
		} else {
			super.clearActivePotions();
		}
	}

	@Override
	public PotionEffect getActivePotionEffect(Potion potionIn) {
		if(this.entity instanceof LivingEntity) {
			return ((LivingEntity) this.entity).getActivePotionEffect(potionIn);
		}
		return super.getActivePotionEffect(potionIn);
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects() {
		if(this.entity instanceof LivingEntity) {
			return ((LivingEntity) this.entity).getActivePotionEffects();
		}
		return super.getActivePotionEffects();
	}

	@Override
	public Map<Potion, PotionEffect> getActivePotionMap() {
		if(this.entity instanceof LivingEntity) {
			return ((LivingEntity) this.entity).getActivePotionMap();
		}
		return super.getActivePotionMap();
	}

	@Override
	public boolean isPotionActive(Potion potionIn) {
		if(this.entity instanceof LivingEntity) {
			return ((LivingEntity) this.entity).isPotionActive(potionIn);
		}
		return super.isPotionActive(potionIn);
	}

	@Override
	public boolean isPotionApplicable(PotionEffect potioneffectIn) {
		if(this.entity instanceof LivingEntity) {
			return ((LivingEntity) this.entity).isPotionApplicable(potioneffectIn);
		}
		return super.isPotionApplicable(potioneffectIn);
	}

	@Override
	public PotionEffect removeActivePotionEffect(Potion potioneffectin) {
		if(this.entity instanceof LivingEntity) {
			return ((LivingEntity) this.entity).removeActivePotionEffect(potioneffectin);
		}
		return super.removeActivePotionEffect(potioneffectin);
	}

	@Override
	public void removePotionEffect(Potion potionIn) {
		if(this.entity instanceof LivingEntity) {
			((LivingEntity) this.entity).removePotionEffect(potionIn);
		} else {
			super.removePotionEffect(potionIn);
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if(this.entity instanceof LivingEntity) {
			return ((LivingEntity) this.entity).attackEntityAsMob(entityIn);
		}
		return super.attackEntityAsMob(entityIn);
	}

	@Override
	public boolean attackable() {
		if(this.entity instanceof LivingEntity) {
			return ((LivingEntity) this.entity).attackable();
		}
		return super.attackable();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(this.entity != null) {
			return this.entity.attackEntityFrom(source, amount);
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean canBeAttackedWithItem() {
		if(this.entity != null) {
			return this.entity.canBeAttackedWithItem();
		}
		return super.canBeAttackedWithItem();
	}

	@Override
	public boolean canHarmPlayer(PlayerEntity player) {
		if(this.entity instanceof PlayerEntity) {
			return ((PlayerEntity) this.entity).canHarmPlayer(player);
		}
		return super.canHarmPlayer(player);
	}

	@Override
	public boolean isInvulnerable() {
		if(this.entity != null) {
			return this.entity.isInvulnerable();
		}
		return super.isInvulnerable();
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		if(this.entity != null) {
			return this.entity.isEntityInvulnerable(source);
		}
		return super.isEntityInvulnerable(source);
	}
}
