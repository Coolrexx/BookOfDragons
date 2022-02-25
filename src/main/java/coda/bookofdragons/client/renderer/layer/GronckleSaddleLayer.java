package coda.bookofdragons.client.renderer.layer;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.common.entities.GronckleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class GronckleSaddleLayer extends GeoLayerRenderer<GronckleEntity> {
    private static final ResourceLocation SADDLE = new ResourceLocation(BookOfDragons.MOD_ID, "textures/entity/gronckle/saddle.png");

    public GronckleSaddleLayer(IGeoRenderer<GronckleEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GronckleEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderType saddle = RenderType.entityCutoutNoCull(SADDLE);
        if (entity.isSaddled()) {
            this.getRenderer().render(this.getEntityModel().getModel(this.getEntityModel().getModelLocation(entity)), entity, partialTicks, saddle, matrixStackIn, bufferIn, bufferIn.getBuffer(saddle), packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}