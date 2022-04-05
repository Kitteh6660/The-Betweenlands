package thebetweenlands.client.render.particle.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.WorldShader;

public class ParticleLightningArc extends Particle {
	private class Arc {
		private @Nullable Arc parent;
		private float t;
		private Vector3d from, dir;
		private int splits;
		private int subdivs;
		private int depth;
		private float size;

		private Arc(@Nullable Arc parent, float t, Vector3d from, Vector3d to, int splits, int subdivs, int depth, float size) {
			this.parent = parent;
			this.t = t;
			this.from = from;
			this.dir = to;
			this.splits = splits;
			this.subdivs = subdivs;
			this.depth = depth;
			this.size = size;
		}
	}

	private final Vector3d target;

	private final List<Arc> arcs = new ArrayList<>();

	private float baseSize = 0.04f;
	private int baseSubdivs = 3;
	private int branchSubdivs = 3;
	private float baseOffsets = 0.3f;
	private float branchOffsets = 0.1f;
	private int splits = 5;
	private float minSplitSpeed = 1;
	private float maxSplitSpeed = 2;
	private float lengthDecay = 0.25f;
	private float sizeDecay = 0.8f;
	private boolean lighting = true;
	
	protected ParticleLightningArc(World worldIn, double posXIn, double posYIn, double posZIn, double mx, double my, double mz, Vector3d target) {
		super(worldIn, posXIn, posYIn, posZIn);
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.canCollide = false;
		this.target = target;
		this.lifetime = 5;
	}
	
	public ParticleLightningArc setBaseSize(float size) {
		this.baseSize = size;
		return this;
	}
	
	public ParticleLightningArc setSubdivs(int baseSubdivs, int branchSubdivs) {
		this.baseSubdivs = baseSubdivs;
		this.branchSubdivs = branchSubdivs;
		return this;
	}
	
	public ParticleLightningArc setOffsets(float baseOffsets, float branchOffsets) {
		this.baseOffsets = baseOffsets;
		this.branchOffsets = branchOffsets;
		return this;
	}
	
	public ParticleLightningArc setSplits(int splits) {
		this.splits = splits;
		return this;
	}

	public ParticleLightningArc setSplitSpeed(float minSplitSpeed, float maxSplitSpeed) {
		this.minSplitSpeed = minSplitSpeed;
		this.maxSplitSpeed = maxSplitSpeed;
		return this;
	}
	
	public ParticleLightningArc setLengthDecay(float multiplier) {
		this.lengthDecay = multiplier;
		return this;
	}
	
	public ParticleLightningArc setSizeDecay(float multiplier) {
		this.sizeDecay = multiplier;
		return this;
	}
	
	public ParticleLightningArc setLighting(boolean light) {
		this.lighting = light;
		return this;
	}
	
	private void addArc(List<Arc> arcs, Arc arc, float offsets, int subdivs) {
		Vector3d startpoint = arc.from;
		Arc prevArc = null;

		for(int i = 0; i < subdivs; i++) {
			float t2 = (i + 1) / (float)subdivs;

			float offsetScale = i == subdivs - 1 ? 0.0f : 1.0f;
			
			Vector3d endpoint = arc.from.add(arc.dir.scale(t2)).add((this.random.nextFloat() - 0.5f) * offsets * offsetScale, (this.random.nextFloat() - 0.5f) * offsets * offsetScale, (this.random.nextFloat() - 0.5f) * offsets * offsetScale);

			Arc newArc;
			if(prevArc == null) {
				newArc = new Arc(arc.parent, arc.t, startpoint, endpoint.subtract(startpoint), arc.splits, subdivs, arc.depth, arc.size);
			} else {
				newArc = new Arc(prevArc, 1, startpoint, endpoint.subtract(startpoint), arc.splits, subdivs, arc.depth, arc.size);
			}

			arcs.add(newArc);

			prevArc = newArc;
			startpoint = endpoint;
		}
	}
	
	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float rx = (float)(this.xOld + (this.getX() - this.xOld) * (double)partialTicks - interpPosX);
		float ry = (float)(this.yOld + (this.getY() - this.yOld) * (double)partialTicks - interpPosY);
		float rz = (float)(this.zOld + (this.getZ() - this.zOld) * (double)partialTicks - interpPosZ);

		int light = this.getBrightnessForRender(partialTicks);
		int lightmapX = light >> 16 & 65535;
		int lightmapY = light & 65535;

		float scale = ((MathHelper.sin((this.age + partialTicks) * 0.8f) + 1) * 0.5f * 0.5f + 0.5f) * this.baseSize * (1 - (this.age - 1 + partialTicks) / this.lifetime);

		for(Arc arc : this.arcs) {

			ParticleBeam.buildBeam(rx + arc.from.x, ry + arc.from.y, rz + arc.from.z, arc.dir, scale * arc.size, 0, 0, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ, (x, y, z, u, v) -> {
				buffer.pos(x, y, z).tex(u, v).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();
			});

		}
		
		if(this.lighting && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			double distFromCam = entityIn.getDistance(this.target.x, this.target.y, this.target.z);
			if(distFromCam < 40) {
	        	ShaderHelper.INSTANCE.require();
	        	
	        	float strength = (1 - this.age / (float)this.lifetime) * this.alpha * 50.0f;
	        	
	            ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(this.target.x, this.target.y, this.target.z,
	                    1.0f + 5.0f * this.baseSize, this.particleRed * strength, this.particleGreen * strength, this.particleBlue * strength));
			}
        }
	}

	@Override
	public void tick() {
		super.tick();

		if(this.arcs.isEmpty()) {
			this.addArc(this.arcs, new Arc(null, 0, Vector3d.ZERO, target.subtract(this.getX(), this.getY(), this.getZ()), this.splits, 0, 0, 1.0f), this.baseOffsets, this.baseSubdivs);
		}
		
		for(Arc arc : this.arcs) {
			if(arc.parent != null) {
				arc.from = arc.parent.from.add(arc.parent.dir.scale(arc.t));
			}

			float jitter = (arc.depth + 1) * 0.02f;

			float length = (float)arc.dir.length() * (arc.depth > 0 ? 0.9f * arc.t : 1.0f);

			arc.dir = arc.dir.add((this.random.nextFloat() - 0.5f) * jitter, (this.random.nextFloat() - 0.5f) * jitter, (this.random.nextFloat() - 0.5f) * jitter).normalize().scale(length);
		}

		int iters = Math.round(this.minSplitSpeed + this.random.nextFloat() * (this.maxSplitSpeed - this.minSplitSpeed));

		for(int j = 0; j < iters; j++) {
			List<Arc> newArcs = new ArrayList<>();

			for(Arc arc : this.arcs) {
				if(arc.splits > 0) {
					int numSplits = arc.splits / 2 + this.random.nextInt(arc.splits / 2 + 1);
					for(int i = 0; i < numSplits; i++) {
						arc.splits--;

						float len = (float)arc.dir.length();

						Vector3d dir = arc.dir.add((this.random.nextFloat() - 0.5f) * len * 0.5f, (this.random.nextFloat() - 0.5f) * len * 0.5f, (this.random.nextFloat() - 0.5f) * len * 0.5f).normalize().scale(len * arc.subdivs * this.lengthDecay);

						float t = this.random.nextFloat();
						Vector3d from = arc.from.add(arc.dir.scale(t));

						this.addArc(newArcs, new Arc(arc, t, from, dir, arc.splits / 2, this.branchSubdivs, arc.depth + 1, arc.size * this.sizeDecay), len * this.branchOffsets /  3.0f, this.branchSubdivs);
					}
				}
			}

			this.arcs.addAll(newArcs);
		}
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleLightningArc> {
		public Factory() {
			super(ParticleLightningArc.class);
		}

		@Override
		public ParticleLightningArc createParticle(ImmutableParticleArgs args) {
			return new ParticleLightningArc(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getObject(Vector3d.class, 0));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(Vector3d.ZERO);
		}
	}
}
