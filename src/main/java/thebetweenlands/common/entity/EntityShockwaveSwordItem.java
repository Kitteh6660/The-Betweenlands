package thebetweenlands.common.entity;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityShockwaveSwordItem extends ItemEntity {
	private static final DataParameter<Integer> WAVE_PROGRESS = EntityDataManager.defineId(EntityShockwaveSwordItem.class, DataSerializers.INT);

	private int waveProgress;
	private int lastWaveProgress;

	public EntityShockwaveSwordItem(World worldIn) {
		super(worldIn);
		this.setPickUpDelay(80);
		this.setNoDespawn();
		this.setSize(0.25F, 1.0F);
	}

	public EntityShockwaveSwordItem(World worldObj, double posX, double posY, double posZ, ItemStack itemStack) {
		super(worldObj, posX, posY, posZ, itemStack);
		this.setPickUpDelay(80);
		this.setNoDespawn();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().register(WAVE_PROGRESS, 0);
	}

	@Override
	public void tick() {
		super.tick();
		this.lastWaveProgress = this.waveProgress;
		this.waveProgress = this.getEntityData().get(WAVE_PROGRESS);
		if(this.waveProgress < 50)
			this.getEntityData().set(WAVE_PROGRESS, this.waveProgress + 1);
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putInt("WaveProgress", this.getEntityData().get(WAVE_PROGRESS));
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		this.getEntityData().set(WAVE_PROGRESS, nbt.getInt("WaveProgress"));
	}

	@OnlyIn(Dist.CLIENT)
	public float getWaveProgress(float partialTicks) {
		return this.lastWaveProgress + (this.waveProgress - this.lastWaveProgress) * partialTicks;
	}
}
