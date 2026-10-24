package com.starmaylight.arcana_industry.client;

import com.starmaylight.arcana_industry.mob.OreMite;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SilverfishRenderer;
import net.minecraft.resources.ResourceLocation;

public final class OreMiteRenderer extends SilverfishRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("arcana_industry", "textures/entity/ore_mite.png");
    public OreMiteRenderer(EntityRendererProvider.Context context) { super(context); }
    @Override public ResourceLocation getTextureLocation(net.minecraft.world.entity.monster.Silverfish mite) { return TEXTURE; }
}
