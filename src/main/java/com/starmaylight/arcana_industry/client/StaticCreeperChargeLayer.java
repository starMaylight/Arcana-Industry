package com.starmaylight.arcana_industry.client;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

/** Charged shell drawn with this mod's arc lattice; reuses only the standard inflated creeper geometry. */
public final class StaticCreeperChargeLayer extends EnergySwirlLayer<Creeper, CreeperModel<Creeper>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("arcana_industry", "textures/entity/static_creeper_charge.png");
    private final CreeperModel<Creeper> model;
    public StaticCreeperChargeLayer(RenderLayerParent<Creeper, CreeperModel<Creeper>> parent, EntityModelSet models) {
        super(parent);
        this.model = new CreeperModel<>(models.bakeLayer(ModelLayers.CREEPER_ARMOR));
    }
    /** Slower drift than a vanilla charge so the zigzag arcs read as crawling current. */
    @Override protected float xOffset(float ageInTicks) { return ageInTicks * 0.006F; }
    @Override protected ResourceLocation getTextureLocation() { return TEXTURE; }
    @Override protected EntityModel<Creeper> model() { return model; }
}
