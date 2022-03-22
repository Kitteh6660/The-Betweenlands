package thebetweenlands.common.item.misc;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemMob extends Item {
	private final Class<? extends Entity> defaultMob;
	private final Consumer<Entity> defaultMobSetter;

	/**
	 * @param maxStackSize Max stack size of the item. If this is > 1 then only the entity's ID and no additional NBT is stored.
	 * @param defaultMob Default mob type of this item
	 * @param defaultMobSetter Sets the properties of the default mob
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> ItemMob(int maxStackSize, @Nullable Class<T> defaultMob, @Nullable Consumer<T> defaultMobSetter) {
		this.maxStackSize = maxStackSize;
		this.defaultMob = defaultMob;
		this.defaultMobSetter = (Consumer<Entity>) defaultMobSetter;
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	public ItemStack capture(Class<? extends Entity> cls) {
		return this.capture(cls, null);
	}

	public ItemStack capture(Class<? extends Entity> cls, @Nullable CompoundNBT nbt) {
		ResourceLocation id = EntityList.getKey(cls);
		if(id != null) {
			if(nbt == null) {
				nbt = new CompoundNBT();
			}
			nbt.putString("id", id.toString());

			ItemStack stack = new ItemStack(this);

			stack.setTagInfo("Entity", nbt);

			return stack;
		}

		return ItemStack.EMPTY;
	}

	public ItemStack capture(Entity entity) {
		CompoundNBT nbt = new CompoundNBT();

		if(this.maxStackSize == 1) {
			entity.writeToNBTOptional(nbt);
		} else {
			CompoundNBT entityNbt = new CompoundNBT();
			if(entity.writeToNBTOptional(entityNbt) && entityNbt.contains("id", Constants.NBT.TAG_STRING)) {
				nbt.putString("id", entityNbt.getString("id"));
			}
		}

		if(!nbt.isEmpty()) {
			ItemStack stack = new ItemStack(this);

			stack.setTagInfo("Entity", nbt);

			if(entity.hasCustomName()) {
				stack.setStackDisplayName(entity.getCustomNameTag());
			}

			return stack;
		}

		return ItemStack.EMPTY;
	}

	public boolean isCapturedEntity(ItemStack stack, Class<? extends Entity> cls) {
		if(stack.getItem() != this) {
			return false;
		}

		CompoundNBT nbt = stack.getTag();

		if(nbt != null && nbt.contains("Entity", Constants.NBT.TAG_COMPOUND)) {
			CompoundNBT entityNbt = nbt.getCompoundTag("Entity");

			if(entityNbt.contains("id", Constants.NBT.TAG_STRING)) {
				Class<? extends Entity> entityCls = EntityList.getClass(new ResourceLocation(entityNbt.getString("id")));
				return entityCls != null && cls.isAssignableFrom(entityCls);
			}
		}

		return this.defaultMob != null && cls.isAssignableFrom(this.defaultMob);
	}

	@Nullable
	public ResourceLocation getCapturedEntityId(ItemStack stack) {
		if(stack.getItem() != this) {
			return null;
		}

		if(stack.getTag() != null && stack.getTag().contains("Entity", Constants.NBT.TAG_COMPOUND)) {
			CompoundNBT entityNbt = stack.getTag().getCompoundTag("Entity");

			if(entityNbt.contains("id", Constants.NBT.TAG_STRING)) {
				return new ResourceLocation(entityNbt.getString("id"));
			}
		}

		if(this.defaultMob != null) {
			return EntityList.getKey(this.defaultMob);
		}

		return null;
	}

	@Nullable
	public Entity createCapturedEntity(World world, double x, double y, double z, ItemStack stack) {
		if(stack.getTag() != null && stack.getTag().contains("Entity", Constants.NBT.TAG_COMPOUND)) {
			return this.createCapturedEntityFromNBT(world, x, y, z, stack.getTag().getCompoundTag("Entity"));
		}

		if(this.defaultMob != null) {
			ResourceLocation id = EntityList.getKey(this.defaultMob);
			if(id != null) {
				CompoundNBT nbt = new CompoundNBT();
				nbt.putString("id", id.toString());
				Entity entity = this.createCapturedEntityFromNBT(world, x, y, z, nbt);
				if(this.defaultMobSetter != null) {
					this.defaultMobSetter.accept(entity);
				}
				return entity;
			}
		}

		return null;
	}

	@Nullable
	protected Entity createCapturedEntityFromNBT(World world, double x, double y, double z, CompoundNBT nbt) {
		Entity entity = EntityList.createEntityFromNBT(nbt, world);

		if(entity != null) {
			entity.putUUID(UUID.randomUUID());
			entity.moveTo(x, y, z, world.rand.nextFloat() * 360.0f, 0);
			entity.motionX = entity.motionY = entity.motionZ = 0;
			return entity;
		}

		return null;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.defaultMob != null && this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this));
		}
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = player.getItemInHand(hand);
		if(!world.isClientSide()) {
			Entity entity = this.createCapturedEntity(world, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stack);
			if(entity != null) {
				if(facing.getStepX() != 0) {
					entity.setPosition(entity.getX() + facing.getStepX() * entity.width * 0.5f, entity.getY(), entity.getZ());
				}
				if(facing.getStepY() < 0) {
					entity.setPosition(entity.getX(), entity.getY() - entity.height, entity.getZ());
				}
				if(facing.getStepZ() != 0) {
					entity.setPosition(entity.getX(), entity.getY(), entity.getZ() + facing.getStepZ() * entity.width * 0.5f);
				}

				if(world.getCollisionBoxes(entity, entity.getBoundingBox()).isEmpty()) {
					this.spawnCapturedEntity(player, world, entity);
					stack.shrink(1);
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.SUCCESS;
	}

	protected void spawnCapturedEntity(PlayerEntity player, World world, Entity entity) {
		world.spawnEntity(entity);

		if(entity instanceof MobEntity) {
			((MobEntity) entity).playLivingSound();
		}
	}

	public void onCapturedByPlayer(PlayerEntity player, Hand hand, ItemStack captured) {
		
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		ResourceLocation id = this.getCapturedEntityId(stack);
		if(id != null) {
			return "entity." + id.getNamespace() + "." + id.getPath();
		}
		return super.getTranslationKey(stack);
	}

	@Override
	public boolean hasCustomProperties() {
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(worldIn != null) {
			Entity entity = this.createCapturedEntity(worldIn, 0, 0, 0, stack);
			if(entity instanceof LivingEntity) {
				LivingEntity living = (LivingEntity) entity;
				tooltip.add(I18n.get("tooltip.bl.item_mob.health", MathHelper.ceil(living.getHealth() / 2), MathHelper.ceil(living.getMaxHealth() / 2)));
			}
		}
	}
}
