package thebetweenlands.client.render.model.armor;

import java.util.function.Function;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ModelRendererItemAttachment<T extends LivingEntity> extends ModelRenderer {
	private T entity;
	private Function<T, ItemStack> stack;
	private Hand side;
	private float scale;

	public ModelRendererItemAttachment(Model model, Function<T, ItemStack> stack, Hand side, float scale) {
		super(model);
		this.stack = stack;
		this.side = side;
		this.scale = scale;
		this.visible = true;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

	@Override
	public void render(float scale) {
		if (!this.isHidden)
		{
			if (this.showModel)
			{
				GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);

				if (this.xRot == 0.0F && this.yRot == 0.0F && this.zRot == 0.0F)
				{
					if (this.xRot == 0.0F && this.yRot == 0.0F && this.zRot == 0.0F)
					{
						this.ItemRenderer(scale);

						if (this.childModels != null)
						{
							for (int k = 0; k < this.childModels.size(); ++k)
							{
								((ModelRenderer)this.childModels.get(k)).render(scale);
							}
						}
					}
					else
					{
						GlStateManager.translate(this.xRot * scale, this.yRot * scale, this.zRot * scale);

						this.ItemRenderer(scale);

						if (this.childModels != null)
						{
							for (int j = 0; j < this.childModels.size(); ++j)
							{
								((ModelRenderer)this.childModels.get(j)).render(scale);
							}
						}

						GlStateManager.translate(-this.xRot * scale, -this.yRot * scale, -this.zRot * scale);
					}
				}
				else
				{
					GlStateManager.pushMatrix();
					GlStateManager.translate(this.xRot * scale, this.yRot * scale, this.zRot * scale);

					if (this.zRot != 0.0F)
					{
						GlStateManager.rotate(this.zRot * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
					}

					if (this.yRot != 0.0F)
					{
						GlStateManager.rotate(this.yRot * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
					}

					if (this.xRot != 0.0F)
					{
						GlStateManager.rotate(this.xRot * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
					}

					this.ItemRenderer(scale);

					if (this.childModels != null)
					{
						for (int i = 0; i < this.childModels.size(); ++i)
						{
							((ModelRenderer)this.childModels.get(i)).render(scale);
						}
					}

					GlStateManager.popMatrix();
				}

				GlStateManager.translate(-this.offsetX, -this.offsetY, -this.offsetZ);
			}
		}
	}

	@Override
	public void renderWithRotation(float scale) {
		if (!this.isHidden)
		{
			if (this.showModel)
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(this.xRot * scale, this.yRot * scale, this.zRot * scale);

				if (this.yRot != 0.0F)
				{
					GlStateManager.rotate(this.yRot * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
				}

				if (this.xRot != 0.0F)
				{
					GlStateManager.rotate(this.xRot * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
				}

				if (this.zRot != 0.0F)
				{
					GlStateManager.rotate(this.zRot * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
				}

				this.ItemRenderer(scale);

				GlStateManager.popMatrix();
			}
		}
	}

	protected void ItemRenderer(float modelScale) {
		if(this.entity != null && this.stack != null && this.side != null) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(this.scale, this.scale, this.scale);

			Minecraft.getInstance().getItemRenderer().renderItemSide(this.entity, this.stack.apply(this.entity),
					this.side == EnumHandSide.LEFT ? ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
							this.side == EnumHandSide.LEFT);

			GlStateManager.popMatrix();
		}
	}
}
