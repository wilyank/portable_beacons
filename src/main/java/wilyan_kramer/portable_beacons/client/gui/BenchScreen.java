package wilyan_kramer.portable_beacons.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.common.container.BenchContainer;

public class BenchScreen extends ContainerScreen<BenchContainer> {
	@Override
	public int getXSize() {
		return this.imageWidth;
	}

	@Override
	public int getYSize() {
		return this.imageHeight;
	}

	private ResourceLocation GUI = new ResourceLocation(PortableBeaconsMod.MODID, "textures/gui/bench_gui.png");
	private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};
	
	// constructor
	public BenchScreen(BenchContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		this.titleLabelX = 24;
	    this.titleLabelY = 6;
	    this.imageWidth = 240;
	    this.imageHeight = 222;
	    this.inventoryLabelX = 24;
	    this.inventoryLabelY = this.imageHeight - 94;
	}

	// draw the GUI
	@Override
	public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
		// draw the background
		this.renderBackground(mStack);
		// draw the things we inherited
		super.render(mStack, mouseX, mouseY, partialTicks);
		// draw the tooltips in the inventory
		this.renderTooltip(mStack, mouseX, mouseY);
	}
	

	// I am assuming this gets called in the super.render() function. Here we can draw all the text and images.
	@Override
	protected void renderLabels(MatrixStack mStack, int mouseX, int mouseY) {
		// arguments: matrix stack, x coord, y coord, color
//		drawString(mStack, font, new StringTextComponent(Integer.toString(this.menu.getBrewTime())), this.leftPos + 135, this.topPos + 39, 255);
		super.renderLabels(mStack, mouseX, mouseY);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
		
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(GUI);
		int relX = (this.width - this.getXSize()) / 2;
		int relY = (this.height - this.getYSize()) / 2;
		// the main gui texture
		this.blit(mStack, relX+16, relY, 0, 0, this.getXSize(), this.getYSize());
		
		// the level bars
		int potioneerLevel = this.menu.getPotioneerLevel();
		int artificerLevel = this.menu.getArtificerLevel();
		int summonerLevel = this.menu.getSummonerLevel();
		this.blit(mStack, this.leftPos + 110, this.topPos + 74, 0, 222, potioneerLevel*75/5, 5);
		this.blit(mStack, this.leftPos + 110, this.topPos + 79, 0, 227, artificerLevel*75/5, 5);
		this.blit(mStack, this.leftPos + 110, this.topPos + 84, 0, 232, summonerLevel*75/5, 5);
	    
		
		// the progress bar for the brewing stand
		
		int brewTime = this.menu.getBrewTime();
	      if (brewTime > 0) {
	         int arrowSize = (int)(28.0F * (1.0F - (float)brewTime / 400.0F));
	         if (arrowSize > 0) {
	        	 this.blit(mStack, this.leftPos + 236, this.topPos + 8, 75, 222, 9, arrowSize);
	         }

	         arrowSize = BUBBLELENGTHS[brewTime / 2 % 7];
	         if (arrowSize > 0) {
	            this.blit(mStack, this.leftPos + 202, this.topPos + 5 + 29 - arrowSize, 84, 251 - arrowSize, 12, arrowSize);
	         }
	      }
	      // the cross through the progress bar if potioneer level is too low to use the brewing stand
	      if (potioneerLevel < 1) {
		      this.blit(mStack, this.leftPos + 235, this.topPos + 13, 96, 222, 11, 11);
	      }
	    //blit(matrixStack, x coord on gui, y coord on gui, x coord on texture, y coord on texture, width, height)
	}
}
