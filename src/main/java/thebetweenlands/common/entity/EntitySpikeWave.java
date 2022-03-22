package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.netty.buffer.PacketBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.SpikeRenderer;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleRootSpike;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySpikeWave extends Entity implements IEntityAdditionalSpawnData {
	protected List<BlockPos> positions = new ArrayList<>();

	private AxisAlignedBB blockEnclosingBounds;
	private AxisAlignedBB renderingBounds;

	public BlockPos origin;
	public int delay;

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public Map<BlockPos, List<SpikeRenderer>> modelParts;

	protected float attackDamage = 10.0F;

	public EntitySpikeWave(World world) {
		super(world);
		this.setSize(1, 1);
		this.noClip = true;
	}

	public void setAttackDamage(float damage) {
		this.attackDamage = damage;
	}

	public void addPosition(BlockPos pos) {
		if(this.origin == null) {
			this.origin = pos;
			this.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			this.lastTickPosX = this.xOld = this.getX();
			this.lastTickPosY = this.yOld = this.getY();
			this.lastTickPosZ = this.zOld = this.getZ();
		}

		this.positions.add(pos);

		AxisAlignedBB aabb = new AxisAlignedBB(pos).expand(0, 1, 0);
		if(this.blockEnclosingBounds == null) {
			this.blockEnclosingBounds = aabb;
		} else {
			this.blockEnclosingBounds = this.blockEnclosingBounds.union(aabb);
		}
		this.renderingBounds = this.blockEnclosingBounds.offset(this.getX() - (this.origin.getX() + 0.5D), this.getY() - this.origin.getY(), this.getZ() - (this.origin.getZ() + 0.5D));
	}

	@OnlyIn(Dist.CLIENT)
	public void initRootModels() {
		if(this.modelParts == null) {
			this.modelParts = new HashMap<>();

			for(BlockPos pos : this.positions) {
				int models = 1 + this.random.nextInt(2);
				List<SpikeRenderer> renderers = new ArrayList<>();
				for(int i = 0; i < models; i++) {
					Vector3d offset = new Vector3d(
							pos.getX() + this.random.nextDouble() * 0.6D - 0.3D - this.getX(),
							pos.getY() + 0.25D - this.getY(),
							pos.getZ() + this.random.nextDouble() * 0.6D - 0.3D - this.getZ()
							);
					float scale = 0.4F + this.random.nextFloat() * 0.2F;
					SpikeRenderer renderer = new SpikeRenderer(2, scale * 0.5F, scale, 1, this.random.nextLong(), offset.x, offset.y, offset.z).build(DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL, Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(ParticleRootSpike.SPRITE.toString()));
					renderers.add(renderer);
				}

				this.modelParts.put(pos, renderers);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getBrightnessForRender() {
		BlockPos.Mutable pos = new BlockPos.Mutable(MathHelper.floor(this.getX()), 0, MathHelper.floor(this.getZ()));

		if (this.world.isBlockLoaded(pos)) {
			pos.setY(MathHelper.floor(this.getY() + (double)this.getEyeHeight()) + 1);
			return this.world.getCombinedLight(pos, 0);
		} else {
			return 0;
		}
	}

	@Override
	public void tick() {
		this.world.profiler.startSection("entityBaseTick");

		this.xOld = this.getX();
		this.yOld = this.getY();
		this.zOld = this.getZ();
		this.motionX = 0;
		this.motionZ = 0;
		this.lastTickPosX = this.getX();
		this.lastTickPosY = this.getY();
		this.lastTickPosZ = this.getZ();

		if(this.tickCount >= this.delay) {
			if(this.level.isClientSide() && this.tickCount == this.delay) {
				this.spawnEmergeParticles();
				this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundRegistry.SPIRIT_TREE_SPIKES, SoundCategory.HOSTILE, 0.7F, 0.9F + this.random.nextFloat() * 0.2F, false);
			}
			if(this.tickCount == this.delay && this.motionY <= 0.0D) {
				this.motionY += 0.25D;
			} else {
				this.motionY -= 0.05D;

				if(!this.level.isClientSide() && (this.getY() <= this.origin.getY() || this.onGround)) {
					this.remove();
				}
			}
		} else {
			this.motionY = 0.0D;
		}

		if(this.getY() < -64.0D) {
			this.remove();
		}

		if(this.getY() + this.motionY <= this.origin.getY()) {
			this.motionY = 0.0D;
			this.moveTo(this.getX(), this.origin.getY(), this.getZ(), 0, 0);
		} else {
			this.move(MoverType.SELF, 0, this.motionY, 0);
		}

		if(this.motionY > 0.1D && !this.level.isClientSide()) {
			DamageSource damageSource = new EntityDamageSource("bl.spikewave", this);
			for(BlockPos pos : this.positions) {
				AxisAlignedBB aabb = new AxisAlignedBB(pos).offset(this.getX() - (this.origin.getX() + 0.5D), this.getY() - this.origin.getY(), this.getZ() - (this.origin.getZ() + 0.5D)).shrink(0.1D).offset(0, 0.2D, 0);
				List<LivingEntity> entities = this.world.getEntitiesOfClass(LivingEntity.class, aabb);
				for(LivingEntity entity : entities) {      
					if (entity instanceof LivingEntity) {    
						entity.attackEntityFrom(damageSource, this.attackDamage);
					}
				}
			}
		}

		this.renderingBounds = this.blockEnclosingBounds.offset(this.getX() - (this.origin.getX() + 0.5D), this.getY() - this.origin.getY(), this.getZ() - (this.origin.getZ() + 0.5D));

		this.firstUpdate = false;
		this.world.profiler.endSection();
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnEmergeParticles() {
		if(!this.positions.isEmpty()) {
			int particles = 8 + this.random.nextInt(8);
			for(int i = 0; i < particles; i++) {
				BlockPos pos = this.positions.get(this.random.nextInt(this.positions.size()));

				double x = pos.getX() + this.random.nextDouble();
				double y = pos.getY() + 1;
				double z = pos.getZ() + this.random.nextDouble();
				double mx = (this.random.nextDouble() - 0.5D) * 0.8F;
				double my = 0.1D + this.random.nextDouble() * 0.4F;
				double mz = (this.random.nextDouble() - 0.5D) * 0.8F;

				ParticleRootSpike particle = (ParticleRootSpike) BLParticles.ROOT_SPIKE.spawn(this.world, x, y, z, ParticleArgs.get().withMotion(mx, my, mz));
				particle.setUseSound(this.random.nextInt(40) == 0);
			}

			for(BlockPos pos : this.positions) {
				BlockState state = this.world.getBlockState(pos);

				if(!state.getBlock().isAir(state, this.world, pos)) {
					int dustParticles = 1 + this.random.nextInt(3);

					for(int i = 0; i < dustParticles; i++) {
						double x = pos.getX() + this.random.nextDouble();
						double y = pos.getY() + 1;
						double z = pos.getZ() + this.random.nextDouble();
						double mx = (this.random.nextDouble() - 0.5D) * 0.3F;
						double my = 0.1D + this.random.nextDouble() * 0.2F;
						double mz = (this.random.nextDouble() - 0.5D) * 0.3F;

						this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, x, y, z, mx, my, mz, Block.getStateId(state));
					}
				}
			}
		}
	}

	@Override
	public boolean handleWaterMovement() {
		return false;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void writeSpawnData(PacketBuffer data) {
		data.writeLong(this.origin.toLong());

		data.writeInt(this.positions.size());
		for(BlockPos pos : this.positions) {
			data.writeLong(pos.toLong());
		}

		data.writeInt(this.delay);
	}

	@Override
	public void readSpawnData(PacketBuffer data) {
		this.origin = BlockPos.of(data.readLong());

		this.blockEnclosingBounds = null;
		this.positions.clear();
		int size = data.readInt();
		for(int i = 0; i < size; i++) {
			this.addPosition(BlockPos.of(data.readLong()));
		}

		this.delay = data.readInt();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return this.renderingBounds;
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.delay = nbt.getInt("delay");
		this.origin = BlockPos.of(nbt.getLong("origin"));

		this.positions.clear();
		ListNBT blocks = nbt.getList("positions", Constants.NBT.TAG_LONG);
		for(int i = 0; i < blocks.size(); i++) {
			this.addPosition(BlockPos.of(((LongNBT)blocks.get(i)).getLong()));
		}
		if(this.positions.isEmpty()) {
			this.addPosition(this.origin);
		}

		this.attackDamage = nbt.getFloat("attackDamage");
	}

	@Override
	public void save(CompoundNBT nbt) {
		nbt.putInt("delay", this.delay);
		nbt.setLong("origin", this.origin.toLong());

		ListNBT blocks = new ListNBT();
		for(BlockPos pos : this.positions) {
			blocks.appendTag(new LongNBT(pos.toLong()));
		}
		nbt.setTag("positions", blocks);

		nbt.putFloat("attackDamage", this.attackDamage);
	}

	@Override
	protected void defineSynchedData() { }
}