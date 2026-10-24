package com.starmaylight.arcana_industry.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public final class FluxArcherRenderer extends SkeletonRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("arcana_industry", "textures/entity/flux_archer.png");
    public FluxArcherRenderer(EntityRendererProvider.Context context) { super(context); }
    @Override public ResourceLocation getTextureLocation(AbstractSkeleton skeleton) { return TEXTURE; }
}
