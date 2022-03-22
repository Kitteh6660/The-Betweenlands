package thebetweenlands.common.item.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityDreadfulMummy;
import thebetweenlands.common.registries.BiomeRegistry;

public class ItemMummyBait extends Item {

    public ItemMummyBait() {
        this.setCreativeTab(BLCreativeTabs.SPECIALS);
    }

    @Override
    public boolean onEntityItemUpdate(ItemEntity ItemEntity) {
        if(ItemEntity.tickCount % 10 == 0) {
            if(ItemEntity.onGround) {
                int bx = MathHelper.floor(ItemEntity.getX());
                int by = MathHelper.floor(ItemEntity.getY());
                int bz = MathHelper.floor(ItemEntity.getZ());
                BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
                Biome biome = ItemEntity.world.getBiome(pos.setPos(bx, by, bz));
                if(biome == BiomeRegistry.SLUDGE_PLAINS || biome == BiomeRegistry.MARSH_0 || biome == BiomeRegistry.MARSH_1) {
                    boolean canSpawn = true;
                    for(int yo = -3; yo <= -1; yo++) {
                        for(int xo = -1; xo <= 1 && canSpawn; xo++) {
                            for(int zo = -1; zo <= 1 && canSpawn; zo++) {
                                BlockState state = ItemEntity.world.getBlockState(pos.setPos(bx+xo, by+yo, bz+zo));
                                if(!state.isNormalCube() && !state.isSideSolid(ItemEntity.world, pos, Direction.UP))
                                    canSpawn = false;
                            }
                        }
                    }
                    if(canSpawn) {
                        if(ItemEntity.world.isClientSide()) {
                            for(int xo = -1; xo <= 1 && canSpawn; xo++) {
                                for(int zo = -1; zo <= 1 && canSpawn; zo++) {
                                    BlockState state = ItemEntity.world.getBlockState(pos.setPos(bx+xo, by-1, bz+zo));
                                    int stateId = Block.getStateId(state);
                                    for (int i = 0, amount = 12 + ItemEntity.world.rand.nextInt(20); i < amount; i++) {
                                        double ox = ItemEntity.world.rand.nextDouble();
                                        double oy = ItemEntity.world.rand.nextDouble() * 3;
                                        double oz = ItemEntity.world.rand.nextDouble();
                                        double motionX = ItemEntity.world.rand.nextDouble() * 0.2 - 0.1;
                                        double motionY = ItemEntity.world.rand.nextDouble() * 0.1 + 0.1;
                                        double motionZ = ItemEntity.world.rand.nextDouble() * 0.2 - 0.1;
                                        ItemEntity.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, bx+xo + ox, by, bz+zo + oz, motionX, motionY, motionZ, stateId);
                                        BLParticles.SMOKE.spawn(ItemEntity.world, bx+xo + ox, by + oy, bz+zo + oz, ParticleFactory.ParticleArgs.get().withColor(-1, 0xDEAD, 0xC0DE, 1).withMotion(0, 0.25F, 0).withScale(1));
                                    }
                                }
                            }
                        } else {
                            EntityDreadfulMummy boss = new EntityDreadfulMummy(ItemEntity.world);
                            boss.moveTo(ItemEntity.getX(), ItemEntity.getY(), ItemEntity.getZ(), 0, 0);
                            if(boss.getCanSpawnHere()) {
                                ItemEntity.world.spawnEntity(boss);
                                ItemEntity.remove();
                                pos.release();
                                return true;
                            }
                        }
                    }
                }
                pos.release();
            }
        }
        return false;
    }

    @Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
}
