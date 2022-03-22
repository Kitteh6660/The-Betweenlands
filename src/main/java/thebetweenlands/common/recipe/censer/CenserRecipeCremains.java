package thebetweenlands.common.recipe.censer;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.block.ICenser;
import thebetweenlands.api.recipes.ICenserRecipe;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.GroundFog.GroundFogVolume;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.TileEntityCenser;

public class CenserRecipeCremains extends AbstractCenserRecipe<Void> {
	private static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "cremains");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return EnumItemMisc.CREMAINS.isItemOf(stack);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void render(Void context, ICenser censer, double x, double y, double z, float partialTicks) {
		float effectStrength = censer.getEffectStrength(partialTicks);

		if(effectStrength > 0.01F && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();

			float fogBrightness = 0.13F;
			float inScattering = 0.004F * effectStrength;
			float extinction = 0.15F;

			AxisAlignedBB fogArea = new AxisAlignedBB(censer.getCenserPos()).inflate(6, 0.1D, 6).expandTowards(0, 32, 0);

			ShaderHelper.INSTANCE.getWorldShader().addGroundFogVolume(new GroundFogVolume(new Vector3d(fogArea.minX, fogArea.minY, fogArea.minZ), new Vector3d(fogArea.maxX - fogArea.minX, fogArea.maxY - fogArea.minY, fogArea.maxZ - fogArea.minZ), inScattering, extinction, fogBrightness * 0.7F, fogBrightness * 0.2F, fogBrightness * 0.1F));
		}
	}

	private List<LivingEntity> getAffectedEntities(World world, BlockPos pos) {
		return world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos).inflate(6, 0.1D, 6).expandTowards(0, 12, 0));
	}

	@Override
	public int update(Void context, ICenser censer) {
		World world = censer.getCenserWorld();

		if(!world.isClientSide() && world.getGameTime() % 10 == 0) {
			BlockPos pos = censer.getCenserPos();

			List<LivingEntity> affected = this.getAffectedEntities(world, pos);
			for(LivingEntity living : affected) {
				if(!living.isInWater() && !living.isInvulnerableTo(DamageSource.IN_FIRE) && !living.fireImmune() && (living instanceof PlayerEntity == false || !((PlayerEntity) living).isCreative())) {
					living.setSecondsOnFire(1);
				}
			}
		}

		return 0;
	}

	@SubscribeEvent
	public static void onLivingDrops(LivingDropsEvent event) {
		LivingEntity entity = event.getEntityLiving();

		boolean isDeathFromCenser = false;

		if(entity != null && entity.isOnFire() && event.getSource().isFire()) {
			int sx = MathHelper.floor(entity.getX() - 6) >> 4;
			int sz = MathHelper.floor(entity.getZ() - 6) >> 4;
			int ex = MathHelper.floor(entity.getX() + 6) >> 4;
			int ez = MathHelper.floor(entity.getZ() + 6) >> 4;

			check : for(int cx = sx; cx <= ex; cx++) {
				for(int cz = sz; cz <= ez; cz++) {
					Chunk chunk = entity.level.getChunk(cx, cz);

					for(Entry<BlockPos, TileEntity> entry : chunk.getBlockEntities().entrySet()) {
						TileEntity tile = entry.getValue();

						if(tile instanceof TileEntityCenser) {
							TileEntityCenser censer = (TileEntityCenser) tile;

							if(censer.isRecipeRunning() && ((ICenserRecipe<?>) censer.getCurrentRecipe()) instanceof CenserRecipeCremains) {
								isDeathFromCenser = true;
								break check;
							}
						}
					}
				}
			}
		}

		if(isDeathFromCenser) {
			Iterator<ItemEntity> dropsIT = event.getDrops().iterator();

			while(dropsIT.hasNext()) {
				dropsIT.next();

				if(entity.level.random.nextBoolean()) {
					dropsIT.remove();
				}
			}

			int cremains = entity.level.random.nextInt(3);

			for(int i = 0; i < cremains; i++) {
				event.getDrops().add(new ItemEntity(entity.level, entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(), EnumItemMisc.CREMAINS.create(1)));
			}
		}
	}

	@Override
	public int getConsumptionDuration(Void context, ICenser censer) {
		//2.5 min. / item
		return 3;
	}

	@Override
	public int getEffectColor(Void context, ICenser censer, EffectColorType type) {
		return 0xFF500000;
	}
}
