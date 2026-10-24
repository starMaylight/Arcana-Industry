package com.starmaylight.arcana_industry.client;

import com.starmaylight.arcana_industry.mob.RustCrawler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.resources.ResourceLocation;

public final class RustCrawlerRenderer extends SpiderRenderer<RustCrawler> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("arcana_industry", "textures/entity/rust_crawler.png");
    public RustCrawlerRenderer(EntityRendererProvider.Context context) {
        super(context);
        // SpiderRenderer adds the vanilla eye texture; swap it for the crawler's own sensor overlay.
        layers.removeIf(layer -> layer instanceof SpiderEyesLayer);
        addLayer(new RustCrawlerEyesLayer(this));
    }
    @Override public ResourceLocation getTextureLocation(RustCrawler crawler) { return TEXTURE; }
}
