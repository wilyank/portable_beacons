package wilyan_kramer.portable_beacons.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.common.container.DiffuserContainer;

public class DiffuserScreen extends ContainerScreen<DiffuserContainer> {
	private ResourceLocation GUI = new ResourceLocation(PortableBeaconsMod.MODID, "textures/gui/diffuser_gui.png");

	public DiffuserScreen(DiffuserContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
	}

	@Override
	public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(mStack);
		super.render(mStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(mStack, mouseX, mouseY);
	}
	

	@Override
	protected void renderLabels(MatrixStack mStack, int mouseX, int mouseY) {
		if (menu.getDuration() <= 0) {
			drawTextComponent(mStack, new TranslationTextComponent("effect.none"), 0);
		}
		if (menu.getDuration() > 0) {
			drawTextComponent(mStack, new TranslationTextComponent("screen.portable_beacons.diffuser.time_left", StringUtils.formatTickDuration(menu.getDuration())) , 0);
		}
		super.renderLabels(mStack, mouseX, mouseY);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
		
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(GUI);
		int relX = (this.width - this.getXSize()) / 2;
		int relY = (this.height - this.getYSize()) / 2;
		this.blit(mStack, relX, relY, 0, 0, this.getXSize(), this.getYSize());		
		
	}
	private void drawTextComponent(MatrixStack mStack, ITextComponent text, int lineIndex) {
		drawString(mStack, font, text.getString(), 67, 23 + 11 * lineIndex, 0xffffff);
	}
	

}
