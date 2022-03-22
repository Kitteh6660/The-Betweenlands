package thebetweenlands.common.herblore.book.widgets.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.herblore.book.widgets.ManualWidgetBase;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagColor;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagFont;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagNewLine;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagNewPage;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagPagelink;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagRainbow;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagScale;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagSimple;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagTooltip;
import thebetweenlands.util.TranslationHelper;

@OnlyIn(Dist.CLIENT)
public class TextWidget extends ManualWidgetBase {
    private TextContainer textContainer;
    private String text;
    private float scale = 1.0f;
    private int width;
    private int height;
    private int pageNumber = 0;

    @OnlyIn(Dist.CLIENT)
    public TextWidget(int xStart, int yStart, String unlocalizedText) {
        super(xStart, yStart);
        width = 130 - xStart;
        height = 144;
        this.textContainer = new TextContainer(width, height, I18n.get(unlocalizedText), Minecraft.getInstance().fontRenderer);
        this.text = I18n.get(unlocalizedText);
        if (!I18n.contains(unlocalizedText) || text.equals("")) {
        	TranslationHelper.addUnlocalizedString(unlocalizedText);
        }
        this.init();
    }

    @OnlyIn(Dist.CLIENT)
    public TextWidget(int xStart, int yStart, String unlocalizedText, int pageNumber) {
        super(xStart, yStart);
        width = 130 - xStart;
        height = 144;
        this.textContainer = new TextContainer(width, height, I18n.get(unlocalizedText), Minecraft.getInstance().fontRenderer);
        this.text = I18n.get(unlocalizedText);
        if (!I18n.contains(unlocalizedText) || text.equals("")) {
        	TranslationHelper.addUnlocalizedString(unlocalizedText);
        }
        this.init();
        this.pageNumber = pageNumber;
    }

    public TextWidget(int xStart, int yStart, String unlocalizedText, int pageNumber, int width, int height) {
        super(xStart, yStart);
        this.width = width;
        this.height = height;
        this.textContainer = new TextContainer(width, height, I18n.get(unlocalizedText), Minecraft.getInstance().fontRenderer);
        this.text = I18n.get(unlocalizedText);
        if (!I18n.contains(unlocalizedText) || text.equals("")) {
        	TranslationHelper.addUnlocalizedString(unlocalizedText);
        }
        this.init();
        this.pageNumber = pageNumber;
    }

    @OnlyIn(Dist.CLIENT)
    public TextWidget(int xStart, int yStart, String unlocalizedText, float scale) {
        super(xStart, yStart);
        width = 130 - xStart;
        height = 144;
        this.textContainer = new TextContainer(width, height, I18n.get(unlocalizedText), Minecraft.getInstance().fontRenderer);
        this.text = I18n.get(unlocalizedText);
        if (!I18n.contains(unlocalizedText) || text.equals("")) {
        	TranslationHelper.addUnlocalizedString(unlocalizedText);
        }
        this.scale = scale;
        this.init();
    }

    @OnlyIn(Dist.CLIENT)
    public TextWidget(int xStart, int yStart, String text, boolean isLocalized) {
        super(xStart, yStart);
        this.text = isLocalized ? text : I18n.get(text);
        width = 130 - xStart;
        height = 144;
        this.textContainer = new TextContainer(width, height, text, Minecraft.getInstance().fontRenderer);
        if (!isLocalized && (!I18n.contains(text) || text.equals(""))) {
        	TranslationHelper.addUnlocalizedString(text);
        }
        this.init();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setPageToRight() {
        super.setPageToRight();
        this.textContainer = new TextContainer(width, height, text, Minecraft.getInstance().fontRenderer);
        this.init();
    }

    @OnlyIn(Dist.CLIENT)
    public void init() {
        this.textContainer.setCurrentScale(scale).setCurrentColor(0x606060);
        this.textContainer.registerTag(new TagNewLine());
        this.textContainer.registerTag(new TagNewPage());
        this.textContainer.registerTag(new TagScale(1.0F));
        this.textContainer.registerTag(new TagColor(0x606060));
        this.textContainer.registerTag(new TagTooltip("N/A"));
        this.textContainer.registerTag(new TagSimple("bold", TextFormatting.BOLD));
        this.textContainer.registerTag(new TagSimple("obfuscated", TextFormatting.OBFUSCATED));
        this.textContainer.registerTag(new TagSimple("italic", TextFormatting.ITALIC));
        this.textContainer.registerTag(new TagSimple("strikethrough", TextFormatting.STRIKETHROUGH));
        this.textContainer.registerTag(new TagSimple("underline", TextFormatting.UNDERLINE));
        this.textContainer.registerTag(new TagPagelink());
        this.textContainer.registerTag(new TagRainbow());
        this.textContainer.registerTag(new TagFont());

        try {
            this.textContainer.parse();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawForeGround() {
        TextContainer.TextPage page = this.textContainer.getPages().get(pageNumber);
        page.render(this.xStart, this.yStart);
        page.renderTooltips(this.xStart, this.yStart, mouseX, mouseY);
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void resize() {
        super.resize();
        this.textContainer = new TextContainer(width, height, text, Minecraft.getInstance().fontRenderer);
        this.init();
    }


    public TextWidget setWidth(int width) {
        this.width = width;
        return this;
    }
}
