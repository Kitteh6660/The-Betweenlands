package thebetweenlands.client.gui;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.entity.ParticleSwarm;
import thebetweenlands.client.render.particle.entity.ParticleSwarm.ResourceLocationWithScale;
import thebetweenlands.client.render.sprite.TextureAnimation;

public class GuiCrawler extends Gui {
	public float updateCounter = 0;
	private float xOld = 0;
	private float yOld = 0;
	private float posX = 0;
	private float posY = 0;
	private float motionY = 0;
	private float motionX = 0;
	private Random rand;
	private float rotateBias;
	private TextureAnimation animation;
	private float rotation;
	private float prevHurtRotation;
	private float hurtRotation;
	private float hurtTint;
	private float alpha = 1;
	private int hurtDir;
	private float scale;

	public boolean dead;
	public boolean dropping;

	public GuiCrawler(float x, float y, float mX, float mY) {
		this.getX() = this.xOld = x;
		this.getY() = this.yOld = y;
		this.motionX = mX;
		this.motionY = mY;
		this.rand = new Random();
		this.hurtDir = this.random.nextBoolean() ? 1 : -1;
		this.animation = new TextureAnimation();
		Frame[][] sprites = ParticleSwarm.SPRITES.getSprites();
		Frame[] frames = sprites[this.random.nextInt(sprites.length)];
		this.animation.setFrames(frames);
		ResourceLocation location = frames[0].getLocation();
		if(location instanceof ResourceLocationWithScale) {
			this.scale = ((ResourceLocationWithScale) location).scale;
		}
	}

	public void update(int hurtTicks) {
		this.prevHurtRotation = this.hurtRotation;
		if(hurtTicks > 0) {
			this.hurtRotation = (float)Math.pow((hurtTicks / 10.0f) - 0.5f, 5) * 16 * (float) Math.PI;
			this.hurtTint = hurtTicks / 10.0f;
		} else {
			this.hurtRotation = 0;
			this.hurtTint = 0;
		}

		this.animation.update();

		this.updateCounter++;

		if(this.updateCounter > 80) {
			this.dead = true;
		} else if(this.updateCounter > 70) {
			this.alpha = (80 - this.updateCounter) / 10.0f;
		}

		this.xOld = this.getX();
		this.yOld = this.getY();

		this.getY() += this.motionY;
		this.getX() += this.motionX;

		double speed = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY);

		Vector3d normal = new Vector3d(0, 0, 1);
		Vector3d motion = new Vector3d(this.motionX, this.motionY, 0);
		Vector3d side = motion.normalize().cross(normal);

		if(this.random.nextInt(10) == 0) {
			this.rotateBias = ((float)this.random.nextFloat() - 0.5f) * 0.1f;
		}

		Vector3d newMotion = motion.add(side.scale(speed * ((this.random.nextFloat() - 0.5f) * 0.5f + this.rotateBias))).normalize().scale(speed);
		this.motionX = (float) newMotion.x;
		this.motionY = (float) newMotion.y;

		this.rotation = (float) MathHelper.atan2(this.motionY, this.motionX) + (float) Math.PI * 0.5f;

		if(this.dropping) {
			this.motionY += 8;
			this.motionX *= 0.5f;
		}
	}

	public float getPosX() {
		return this.getX();
	}

	public float getPosY() {
		return this.getY();
	}

	public void drawCrawler(Minecraft minecraft, BufferBuilder vertexbuffer, float partialTicks) {
		float interpX = this.xOld + (this.getX() - this.xOld) * partialTicks;
		float interpY = this.yOld + (this.getY() - this.yOld) * partialTicks;

		TextureAtlasSprite sprite = this.animation.getCurrentSprite();
		this.drawTexturedModalRectWithColor(vertexbuffer, interpX, interpY, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), 16 * this.scale, 16 * this.scale, this.rotation + this.hurtDir * (this.prevHurtRotation + (this.hurtRotation - this.prevHurtRotation) * partialTicks), this.alpha);
	}

	public void drawTexturedModalRectWithColor(BufferBuilder vertexbuffer, float x, float y, float minU, float minV, float maxU, float maxV, float width, float height, float rot, float alpha) {
		float minX = x;
		float maxX = x + width;
		float minY = y;
		float maxY = y + height;

		float cos = (float) Math.cos(rot);
		float sin = (float) Math.sin(rot);

		float cx = x + width * 0.5f;
		float cy = y + height * 0.5f;

		float nnx = (minX - cx) * cos - (minY - cy) * sin + cx;
		float nny = (minX - cx) * sin + (minY - cy) * cos + cy;
		float ppx = (maxX - cx) * cos - (maxY - cy) * sin + cx;
		float ppy = (maxX - cx) * sin + (maxY - cy) * cos + cy;
		float mpx = (minX - cx) * cos - (maxY - cy) * sin + cx;
		float mpy = (minX - cx) * sin + (maxY - cy) * cos + cy;
		float pmx = (maxX - cx) * cos - (minY - cy) * sin + cx;
		float pmy = (maxX - cx) * sin + (minY - cy) * cos + cy;

		float gb = 1 - this.hurtTint;

		vertexbuffer.pos(mpx, mpy, (double)this.zLevel).tex(minU, maxV).color(1.0f, gb, gb, alpha).endVertex();
		vertexbuffer.pos(ppx, ppy, (double)this.zLevel).tex(maxU, maxV).color(1.0f, gb, gb, alpha).endVertex();
		vertexbuffer.pos(pmx, pmy, (double)this.zLevel).tex(maxU, minV).color(1.0f, gb, gb, alpha).endVertex();
		vertexbuffer.pos(nnx, nny, (double)this.zLevel).tex(minU, minV).color(1.0f, gb, gb, alpha).endVertex();
	}
}
