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
public class BackpackModel<T extends Entity> extends EntityModel<T> {
    public ModelRenderer backpack_box;

    public BackpackModel() {
        this.texWidth = 128;
        this.texHeight = 64;
        this.backpack_box = new ModelRenderer(this, 0, 0);
        this.backpack_box.setPos(-4.0F, 1.0F, 2.0F);
        this.backpack_box.addBox(-4.0F, -4.5F, -4.0F, 16.0F, 16.0F, 16.0F, -4.5F, -4.5F, -4.5F);
        this.backpack_box.texOffs(64, 0).addBox(-3.0F, -3.5F, -3.0F, 14.0F, 14.0F, 14.0F, -4.0F, -4.0F, -4.0F);
        this.backpack_box.texOffs(0, 32).addBox(-4.0F, 7.0F, -4.0F, 16.0F, 1.0F, 16.0F, -4.0F, 0.0F, -4.0F);
        this.backpack_box.texOffs(64, 32).addBox(0.0F, -1.0F, -4.0F, 8.0F, 12.0F, 4.0F, 0.45F, 0.45F, 0.45F);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.backpack_box).forEach((modelRenderer) -> { 
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
