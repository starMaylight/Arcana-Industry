package com.starmaylight.arcana_industry.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.starmaylight.arcana_industry.Arcana_industry;
import com.starmaylight.arcana_industry.multiblock.BlueprintPart;
import com.starmaylight.arcana_industry.multiblock.Projection;
import com.starmaylight.arcana_industry.multiblock.StructureProjectorItem;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Draws the held Structure Projector's blueprint: missing parts as shrunken translucent block models with a
 * cyan frame, occupied-but-wrong positions as a red frame. Uses the standard block model renderer through an
 * alpha-scaling buffer, so no shader or chunk-pipeline changes are involved.
 */
@Mod.EventBusSubscriber(modid = Arcana_industry.MODID, value = Dist.CLIENT)
public final class StructureHologramRenderer {
    private static final double MAX_DISTANCE_SQR = 96 * 96;
    private static final float GHOST_ALPHA = 0.45F;
    private static final float GHOST_SCALE = 0.8F;

    private StructureHologramRenderer() {}

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;
        Optional<Projection> held = heldProjection(minecraft);
        if (held.isEmpty()) return;
        Projection projection = held.get();
        if (!projection.controllerPresent(minecraft.level)) return;
        Vec3 camera = event.getCamera().getPosition();
        if (Vec3.atCenterOf(projection.origin()).distanceToSqr(camera) > MAX_DISTANCE_SQR) return;

        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        MultiBufferSource ghostBuffers = type -> new AlphaVertexConsumer(buffers.getBuffer(type), GHOST_ALPHA);
        pose.pushPose();
        pose.translate(-camera.x, -camera.y, -camera.z);
        for (BlueprintPart part : projection.visibleParts()) {
            BlockPos pos = part.at(projection.origin());
            if (!minecraft.level.hasChunkAt(pos)) continue;
            if (part.matches(minecraft.level, pos)) continue;
            BlockState present = minecraft.level.getBlockState(pos);
            boolean free = present.canBeReplaced();
            if (free) drawGhost(minecraft, pose, ghostBuffers, part.display(), pos);
            AABB box = new AABB(pos).deflate(0.02);
            if (free) LevelRenderer.renderLineBox(pose, buffers.getBuffer(RenderType.lines()), box, 0.3F, 0.85F, 1.0F, 0.9F);
            else LevelRenderer.renderLineBox(pose, buffers.getBuffer(RenderType.lines()), box, 1.0F, 0.25F, 0.2F, 1.0F);
        }
        pose.popPose();
        buffers.endBatch(Sheets.translucentCullBlockSheet());
        buffers.endBatch(RenderType.lines());
    }

    private static Optional<Projection> heldProjection(Minecraft minecraft) {
        for (InteractionHand hand : InteractionHand.values()) {
            var stack = minecraft.player.getItemInHand(hand);
            if (stack.getItem() instanceof StructureProjectorItem) {
                Optional<Projection> projection = Projection.read(stack);
                if (projection.isPresent()) return projection;
            }
        }
        return Optional.empty();
    }

    private static void drawGhost(Minecraft minecraft, PoseStack pose, MultiBufferSource buffers, BlockState state, BlockPos pos) {
        pose.pushPose();
        float inset = (1.0F - GHOST_SCALE) / 2.0F;
        pose.translate(pos.getX() + inset, pos.getY() + inset, pos.getZ() + inset);
        pose.scale(GHOST_SCALE, GHOST_SCALE, GHOST_SCALE);
        minecraft.getBlockRenderer().renderSingleBlock(state, pose, buffers, LightTexture.FULL_BRIGHT,
            OverlayTexture.NO_OVERLAY, ModelData.EMPTY, Sheets.translucentCullBlockSheet());
        pose.popPose();
    }

    /** Scales vertex alpha so solid block models draw as a see-through hologram. */
    private record AlphaVertexConsumer(VertexConsumer delegate, float alpha) implements VertexConsumer {
        @Override public VertexConsumer vertex(double x, double y, double z) { delegate.vertex(x, y, z); return this; }
        @Override public VertexConsumer color(int r, int g, int b, int a) { delegate.color(r, g, b, (int) (a * alpha)); return this; }
        @Override public VertexConsumer uv(float u, float v) { delegate.uv(u, v); return this; }
        @Override public VertexConsumer overlayCoords(int u, int v) { delegate.overlayCoords(u, v); return this; }
        @Override public VertexConsumer uv2(int u, int v) { delegate.uv2(u, v); return this; }
        @Override public VertexConsumer normal(float x, float y, float z) { delegate.normal(x, y, z); return this; }
        @Override public void endVertex() { delegate.endVertex(); }
        @Override public void defaultColor(int r, int g, int b, int a) { delegate.defaultColor(r, g, b, (int) (a * alpha)); }
        @Override public void unsetDefaultColor() { delegate.unsetDefaultColor(); }
    }
}
