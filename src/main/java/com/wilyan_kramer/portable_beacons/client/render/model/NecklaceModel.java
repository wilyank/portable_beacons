package com.wilyan_kramer.portable_beacons.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * PlayerModel - Either Mojang or a mod author (Taken From Memory)
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class NecklaceModel<T extends Entity> extends EntityModel<T> {
    public ModelRenderer BeaconNecklace;

    public NecklaceModel() {
        this.texWidth = 32;
        this.texHeight = 32;
        this.BeaconNecklace = new ModelRenderer(this, 0, 0);
        this.BeaconNecklace.setPos(-4.0F, 0.0F, -2.0F);
        this.BeaconNecklace.addBox(0.5F, -0.5F, -0.5F, 7.0F, 3.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.BeaconNecklace.texOffs(0, 8).addBox(2.5F, 1.0F, -1.1F, 3.0F, 3.0F, 0.99F, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.BeaconNecklace).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
