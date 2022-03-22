package thebetweenlands.common.entity;

import javax.annotation.Nullable;

import io.netty.buffer.PacketBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.gui.GuiGalleryFrame;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityGalleryFrame extends EntityHanging implements IEntityAdditionalSpawnData {
	protected static final DataParameter<String> URL = EntityDataManager.createKey(EntityGalleryFrame.class, DataSerializers.STRING);

	public static enum Type {
		VERY_LARGE, LARGE, SMALL;
	}

	private Type type;

	public EntityGalleryFrame(World world) {
		super(world);
	}

	public EntityGalleryFrame(World world, BlockPos pos, Direction facing, Type type) {
		super(world, pos);
		this.type = type;
		this.updateFacingWithBoundingBox(facing);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(URL, "");
	}

	@Override
	public String getName() {
		String s;
		if(this.type == null) {
			s = "small";
		} else {
			switch (this.type) {
				default:
				case SMALL:
					s = "small";
					break;
				case LARGE:
					s = "large";
					break;
				case VERY_LARGE:
					s = "very_large";
			}
		}
		return I18n.get("entity.thebetweenlands.gallery_frame_" + s + ".name");
	}

	public void setUrl(String url) {
		this.dataManager.set(URL, url);
	}

	public String getUrl() {
		return this.dataManager.get(URL);
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putInt("type", this.type.ordinal());
		nbt.putString("url", this.getUrl());
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		this.type = Type.values()[nbt.getInt("type")];
		this.setUrl(nbt.getString("url"));
	}

	@Override
	public int getWidthPixels() {
		//Can be null during loading
		if(this.type == null) {
			return 16;
		}

		switch(this.type) {
		default:
		case SMALL:
			return 16;
		case LARGE:
			return 32;
		case VERY_LARGE:
			return 64;
		}
	}

	@Override
	public int getHeightPixels() {
		//Can be null during loading
		if(this.type == null) {
			return 16;
		}

		switch(this.type) {
		default:
		case SMALL:
			return 16;
		case LARGE:
			return 32;
		case VERY_LARGE:
			return 64;
		}
	}

	@Override
	public void onBroken(@Nullable Entity brokenEntity) {
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.playSound(SoundEvents.BLOCK_WOOD_BREAK, 1.0F, 1.0F);

			if (brokenEntity instanceof PlayerEntity) {
				PlayerEntity entityplayer = (PlayerEntity)brokenEntity;

				if (entityplayer.isCreative()) {
					return;
				}
			}

			this.entityDropItem(new ItemStack(this.type == Type.VERY_LARGE ? ItemRegistry.GALLERY_FRAME_VERY_LARGE : this.type == Type.LARGE ? ItemRegistry.GALLERY_FRAME_LARGE : ItemRegistry.GALLERY_FRAME_SMALL), 0.0F);
		}
	}

	@Override
	public void playPlaceSound() {
		this.playSound(SoundEvents.BLOCK_WOOD_PLACE, 1.0F, 1.0F);
	}

	@Override
	public void moveTo(double x, double y, double z, float yaw, float pitch)  {
		this.setPosition(x, y, z);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		this.setPosition((double)this.hangingPosition.getX(), (double)this.hangingPosition.getY(), (double)this.hangingPosition.getZ());
	}

	@Override
	public void writeSpawnData(PacketBuffer buf) {
		buf.writeInt(this.type.ordinal());
		buf.writeLong(this.hangingPosition.toLong());
		buf.writeBoolean(this.facingDirection != null);
		if(this.facingDirection != null) {
			buf.writeInt(this.facingDirection.getIndex());
		}
	}

	@Override
	public void readSpawnData(PacketBuffer buf) {
		this.type = Type.values()[buf.readInt()];
		this.hangingPosition = BlockPos.of(buf.readLong());
		if(buf.readBoolean()) {
			this.facingDirection = Direction.byIndex(buf.readInt());
			this.updateFacingWithBoundingBox(this.facingDirection);
		}
	}

	@Override
	public boolean processInitialInteract(PlayerEntity player, Hand hand) {
		if(this.level.isClientSide() && player == TheBetweenlands.proxy.getClientPlayer()) {
			this.showGalleryGui();
			return true;
		}
		return super.processInitialInteract(player, hand);
	}

	@OnlyIn(Dist.CLIENT)
	private void showGalleryGui() {
		Minecraft.getInstance().displayGuiScreen(new GuiGalleryFrame(this));
	}
}