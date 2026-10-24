package com.starmaylight.arcana_industry.client;

import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public final class StaticCreeperRenderer extends CreeperRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("arcana_industry", "textures/entity/static_creeper.png");
    public StaticCreeperRenderer(EntityRendererProvider.Context context) {
        super(context);
        // CreeperRenderer adds the vanilla charged-armor texture; swap it for the arc lattice shell.
        layers.removeIf(layer -> layer instanceof CreeperPowerLayer);
        addLayer(new StaticCreeperChargeLayer(this, context.getModelSet()));
    }
    @Override public ResourceLocation getTextureLocation(Creeper creeper) { return TEXTURE; }
}
