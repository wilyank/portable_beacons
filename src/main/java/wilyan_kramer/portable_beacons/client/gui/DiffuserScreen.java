package wilyan_kramer.portable_beacons.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.common.container.DiffuserContainer;

public class DiffuserScreen extends ContainerScreen<DiffuserContainer> {
	private ResourceLocation GUI = new ResourceLocation(PortableBeaconsMod.MODID, "textures/gui/diffuser_gui.png");

	// constructor
	public DiffuserScreen(DiffuserContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
	}

	// draw the GUI
	@Override
	public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(mStack);
		// draw all the things that the super class handles
		super.render(mStack, mouseX, mouseY, partialTicks);
		// draw the tooltips in the inventory
		this.renderTooltip(mStack, mouseX, mouseY);
	}
	

	// draw the texts
	@Override
	protected void renderLabels(MatrixStack mStack, int mouseX, int mouseY) {
		if (menu.getDuration() <= 0) {
			clearTextBox(mStack);
			drawTextComponent(mStack, new TranslationTextComponent("effect.none").withStyle(TextFormatting.GRAY), 0);
		}
		if (menu.getDuration() > 0) {
			clearTextBox(mStack);
			int[] effs = menu.getEffectIds();
			int duration = menu.getDuration();
			int[] amps = menu.getAmplifiers();
			int j = 0;
			for (int i = 0; i < 4; i++) {
				if (effs[i] > 0) {
					if (effs[i] == 6 || effs[i] == 7) {
						drawTextComponent(mStack, new StringTextComponent("secretmessage").withStyle(TextFormatting.OBFUSCATED).withStyle(TextFormatting.DARK_PURPLE), j);
					}
					else {
						EffectInstance effectinstance = new EffectInstance(Effect.byId(effs[i]), duration, amps[i]);
						IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(effectinstance.getDescriptionId());
						if (effectinstance.getAmplifier() > 0) {
							iformattabletextcomponent = new TranslationTextComponent("potion.withAmplifier", iformattabletextcomponent, new TranslationTextComponent("potion.potency." + effectinstance.getAmplifier()));
						}
						if (effectinstance.getDuration() > 0) {
							iformattabletextcomponent = new TranslationTextComponent("potion.withDuration", iformattabletextcomponent, EffectUtils.formatDuration(effectinstance, 1));
						}
						drawTextComponent(mStack, iformattabletextcomponent.withStyle(effectinstance.getEffect().getCategory().getTooltipFormatting()), j);
					}
					j++;
				}
			}
			if (j == 0) {
				drawTextComponent(mStack, new TranslationTextComponent("effect.none").withStyle(TextFormatting.GRAY), 0);
			}
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
		drawString(mStack, font, text, 49, 23 + 11 * lineIndex, text.getStyle().getColor().getValue());
	}
	
	private void clearTextBox(MatrixStack mStack) {
		for (int i = 0; i < 4; i++) {
			drawString(mStack, font, "", 49, 23 + 11 * i, 0x000000);
		}
	}
}
