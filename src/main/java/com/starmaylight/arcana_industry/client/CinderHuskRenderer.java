package com.starmaylight.arcana_industry.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public final class CinderHuskRenderer extends ZombieRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("arcana_industry", "textures/entity/cinder_husk.png");
    public CinderHuskRenderer(EntityRendererProvider.Context context) { super(context); }
    @Override public ResourceLocation getTextureLocation(Zombie zombie) { return TEXTURE; }
}
