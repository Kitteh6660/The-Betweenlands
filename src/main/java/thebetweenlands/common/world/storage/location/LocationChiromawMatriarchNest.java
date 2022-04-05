package thebetweenlands.common.world.storage.location;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawMatriarch;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

public class LocationChiromawMatriarchNest extends LocationGuarded {
	private static final int RESPAWN_TIME = 20 * 6; //20 * 6 * 10s = 20min.

	private BlockPos nest;

	private int respawnCounter = 0;

	public LocationChiromawMatriarchNest(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region, "chiromaw_matriarch_nest", EnumLocationType.CHIROMAW_MATRIARCH_NEST);
	}

	public LocationChiromawMatriarchNest(IWorldStorage worldStorage, StorageID id, LocalRegion region, BlockPos nest) {
		super(worldStorage, id, region, "chiromaw_matriarch_nest", EnumLocationType.CHIROMAW_MATRIARCH_NEST);
		this.setNestPosition(nest);
	}

	public void setNestPosition(BlockPos nest) {
		this.nest = nest;
	}

	public BlockPos getNestPosition() {
		return this.nest;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt = super.save(nbt);
		if(this.nest != null) {
			nbt.putInt("NestX", this.nest.getX());
			nbt.putInt("NestY", this.nest.getY());
			nbt.putInt("NestZ", this.nest.getZ());
		}
		nbt.putInt("RespawnCounter", this.respawnCounter);
		return nbt;
	}

	@Override
	public void load(CompoundNBT nbt) {
		super.readGuardNBT(nbt);
		this.nest = new BlockPos(nbt.getInt("NestX"), nbt.getInt("NestY"), nbt.getInt("NestZ"));
		this.respawnCounter = nbt.getInt("RespawnCounter");
	}

	@Override
	public void update() {
		super.update();

		World world = this.getWorldStorage().getWorld();
		if(!world.isClientSide() && this.nest != null && !this.getGuard().isClear(world)) {
			//Check for player claiming
			if(!world.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(this.nest), player -> !player.isCreative() && !player.isSpectator()).isEmpty()) {
				this.getGuard().clear(world);

				this.setVisible(false);

				for(ServerPlayerEntity player : world.getEntitiesOfClass(ServerPlayerEntity.class, this.getBoundingBox())) {
					player.sendMessage(new TranslationTextComponent("chat.chiromaw_matriarch_nest.tainted"), false);

					AdvancementCriterionRegistry.CHIROMAW_MATRIARCH_NEST_CLAIMED.trigger(player);
				}
			}

			//Check for respawn
			if(world.getGameTime() % 200 == 0 && world.getEntitiesOfClass(EntityChiromawMatriarch.class, this.getBoundingBox().inflate(160)).isEmpty()) {
				this.respawnCounter++;

				if(this.respawnCounter >= RESPAWN_TIME) {
					this.respawnCounter = 0;

					EntityChiromawMatriarch matriarch = new EntityChiromawMatriarch(world);
					matriarch.setPos(this.nest.getX() + 0.5D, this.nest.getY() + 0.01D, this.nest.getZ() + 0.5D);

					if(matriarch.isNotColliding()) {
						matriarch.onInitialSpawn(world.getCurrentDifficultyAt(this.nest), null);
						world.addFreshEntity(matriarch);
					} else {
						matriarch.remove();
					}

					if(world.getEntitiesOfClass(EntityChiromawHatchling.class, this.getBoundingBox()).isEmpty()) {
						for(Direction facing : Direction.Plane.HORIZONTAL) {
							if(world.random.nextBoolean()) {
								BlockPos pos = this.nest.relative(facing).below();

								EntityChiromawHatchling egg = new EntityChiromawHatchling(world);
								egg.setPos(pos.getX() + 0.5D, pos.getY() + 0.01D, pos.getZ() + 0.5D);

								if(egg.isNotColliding()) {
									world.addFreshEntity(egg);
								} else {
									egg.remove();
								}
							}
						}
					}
				}
			}
		}
	}
}
