package net.arathain.bookofdragons.client.render.layer;

import net.arathain.bookofdragons.BookOfDragons;
import net.arathain.bookofdragons.common.entity.GronckleEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class GronckleSaddleLayer extends GeoLayerRenderer<GronckleEntity> {
    private static final Identifier SADDLE = new Identifier(BookOfDragons.MOD_ID, "textures/entity/gronckle/saddle.png");
    private static final Identifier CHEST = new Identifier(BookOfDragons.MOD_ID, "textures/entity/gronckle/chest.png");

    public GronckleSaddleLayer(IGeoRenderer<GronckleEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, GronckleEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderLayer saddle = RenderLayer.getArmorCutoutNoCull(SADDLE);
        RenderLayer chest = RenderLayer.getArmorCutoutNoCull(CHEST);
        if (entity.hasChest()) {
            this.getRenderer().render(this.getEntityModel().getModel(this.getEntityModel().getModelLocation(entity)), entity, partialTicks, chest, matrixStackIn, bufferIn, bufferIn.getBuffer(chest), packedLightIn, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (entity.isSaddled()) {
            this.getRenderer().render(this.getEntityModel().getModel(this.getEntityModel().getModelLocation(entity)), entity, partialTicks, saddle, matrixStackIn, bufferIn, bufferIn.getBuffer(saddle), packedLightIn, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

}
