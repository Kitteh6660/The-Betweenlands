package thebetweenlands.client.render.sprite;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import thebetweenlands.client.handler.gallery.GalleryEntry;

public class TextureGalleryEntry extends Texture {
	
	private static final Logger LOGGER = LogManager.getLogger();

	public final GalleryEntry galleryEntry;

	public TextureGalleryEntry(GalleryEntry entry) {
		this.galleryEntry = entry;
	}

	@Override
	public void load(IResourceManager resourceManager) throws IOException {
		this.close();

		try(FileInputStream fio = new FileInputStream(this.galleryEntry.getPictureFile())) {
			BufferedImage image = TextureUtil.readBufferedImage(fio);
			if(image != null && image.getWidth() > 0 && image.getHeight() > 0) {
				this.galleryEntry.setUploaded(image.getWidth(), image.getHeight());
				TextureUtil.uploadTextureImageAllocate(this.getId(), image, false, true);
			} else {
				throw new IOException("Gallery picture could not be loaded properly");
			}
		}
	}
}
