package thebetweenlands.client.render.model.baked;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonParser;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import thebetweenlands.util.QuadBuilder;

public class ModelRubberTapLiquid implements IModel {
	private final int height;
	private final ResourceLocation fluidTexture;

	public ModelRubberTapLiquid(ResourceLocation fluidTexture, int height) {
		this.fluidTexture = fluidTexture;
		this.height = height;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableSet.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		if(this.height > 0)
			return ImmutableSet.of(this.fluidTexture);
		return ImmutableSet.of();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		ImmutableMap<TransformType, TRSRTransformer> map = PerspectiveMapWrapper.getTransforms(state);
		return new BakedModelRubberTapLiquid(format, state.apply(Optional.empty()), map, this.fluidTexture != null ? bakedTextureGetter.apply(this.fluidTexture) : null, this.height);
	}

	@Override
	public IModelState defaultBlockState() {
		return TRSRTransformer.identity();
	}

	@Override
	public ModelRubberTapLiquid process(ImmutableMap<String, String> customData) {
		JsonParser parser = new JsonParser();

		ResourceLocation fluidTexture = this.fluidTexture;
		
		if(customData.containsKey("fluid_texture")) {
			fluidTexture = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("fluid_texture")), "fluid_texture"));
		}
		
		if(fluidTexture == null) {
			fluidTexture = TextureMap.LOCATION_MISSING_TEXTURE;
		}

		int height = 0;

		if(customData.containsKey("fluid_height")) {
			String fluidHeightJsonStr = customData.get("fluid_height");
			height = JsonUtils.getInt(parser.parse(fluidHeightJsonStr), "fluid_height");
		}

		return new ModelRubberTapLiquid(fluidTexture, height);
	}

	private static final class BakedModelRubberTapLiquid implements IBakedModel {
		protected final TRSRTransformer transformation;
		protected final ImmutableMap<TransformType, TRSRTransformer> transforms;
		private final TextureAtlasSprite fluidSprite;
		private final List<BakedQuad> quads;

		private BakedModelRubberTapLiquid(VertexFormat format, Optional<TRSRTransformer> transformation, ImmutableMap<TransformType, TRSRTransformer> transforms, TextureAtlasSprite fluidSprite, int height) {
			this.fluidSprite = fluidSprite;
			this.transformation = transformation.orElse(null);
			this.transforms = transforms;

			if(height > 0) {
				QuadBuilder builder = new QuadBuilder(format).setSprite(this.fluidSprite).setTransformation(this.transformation);

				double liquidHeight = 0.1D + 0.65D / 15.0D * height;

				builder.setCullFace(Direction.UP);
				builder.addVertexInferUV(0.225D, liquidHeight, 0.485D);
				builder.addVertexInferUV(0.225D, liquidHeight, 1.18D);
				builder.addVertexInferUV(0.775D, liquidHeight, 1.18D);
				builder.addVertexInferUV(0.775D, liquidHeight, 0.485D);

				this.quads = builder.build().culledQuads.get(Direction.UP);
			} else {
				this.quads = ImmutableList.of();
			}
		}

		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction side, long rand) {
			if(side == Direction.UP) {
				return this.quads;
			}
			return ImmutableList.of();
		}

		@Override
		public boolean isAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return this.fluidSprite;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return ItemOverrideList.NONE;
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type) {
			return PerspectiveMapWrapper.handlePerspective(this, this.transforms, type);
		}
	}
}
