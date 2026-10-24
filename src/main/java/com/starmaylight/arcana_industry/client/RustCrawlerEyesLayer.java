package com.starmaylight.arcana_industry.client;

import com.starmaylight.arcana_industry.mob.RustCrawler;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

/** Emissive sensor overlay drawn from this mod's own atlas through the standard eyes render type. */
public final class RustCrawlerEyesLayer extends EyesLayer<RustCrawler, SpiderModel<RustCrawler>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("arcana_industry", "textures/entity/rust_crawler_eyes.png");
    private static final RenderType EYES = RenderType.eyes(TEXTURE);
    public RustCrawlerEyesLayer(RenderLayerParent<RustCrawler, SpiderModel<RustCrawler>> parent) { super(parent); }
    @Override public RenderType renderType() { return EYES; }
}
