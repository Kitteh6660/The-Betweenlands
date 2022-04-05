package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class ModelBlank implements IBakedModel {
	
	private final ResourceLocation particleTexture;
	private final List<ResourceLocation> texturesToLoad = new ArrayList<ResourceLocation>();

	public ModelBlank() {
		this.particleTexture = TextureMap.LOCATION_MISSING_TEXTURE;
	}

	public ModelBlank(ResourceLocation texture) {
		if(texture == null) {
			this.particleTexture = TextureMap.LOCATION_MISSING_TEXTURE;
		} else {
			this.particleTexture = texture;
		}
	}

	public ModelBlank(List<ResourceLocation> texturesToLoad) {
		this.texturesToLoad.addAll(texturesToLoad);
		this.particleTexture = texturesToLoad.get(0);
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptySet();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		List<ResourceLocation> textures = new ArrayList<ResourceLocation>();
		if(this.particleTexture != null)
			textures.add(this.particleTexture);
		if(!this.texturesToLoad.isEmpty())
			textures.addAll(this.texturesToLoad);
		return textures;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new ModelBakedBlank(bakedTextureGetter.apply(this.particleTexture));
	}

	@Override
	public IModelState defaultBlockState() {
		return TRSRTransformer.identity();
	}

	public static class ModelBakedBlank implements IBakedModel {
		private final ImmutableList<BakedQuad> noQuads = ImmutableList.of();
		private final TextureAtlasSprite particleTexture;

		public ModelBakedBlank(TextureAtlasSprite particleTexture) {
			this.particleTexture = particleTexture;
		}

		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
			return this.noQuads;
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
			return this.particleTexture;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.NO_TRANSFORMS;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return ItemOverrideList.EMPTY;
		}
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		JsonParser parser = new JsonParser();

		ResourceLocation particleTextureLocation = this.particleTexture;

		if(customData.containsKey("particle_texture")) {
			particleTextureLocation = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("particle_texture")), "particle_texture"));
		}

		if(particleTextureLocation == null) {
			particleTextureLocation = TextureMap.LOCATION_MISSING_TEXTURE;
		}

		return new ModelBlank(particleTextureLocation);
	}
}
