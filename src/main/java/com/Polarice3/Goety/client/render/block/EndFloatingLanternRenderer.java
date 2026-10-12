package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.EndFloatingLanternBlock;
import com.Polarice3.Goety.common.blocks.entities.EndLanternBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;

public class EndFloatingLanternRenderer implements BlockEntityRenderer<EndLanternBlockEntity> {
    private static final float LOOP_TICKS = 50.0F;
    private static final int KEYS = 60;

    public record Part(ResourceLocation model, float x, float y, float z, int track) {
    }

    public static final List<Part> PARTS = List.of(
            new Part(Goety.location("block/end_floating_lantern/mid"), 8.5F, 13.0F, 8.0F, 0),
            new Part(Goety.location("block/end_floating_lantern/top"), 8.0F, 29.0F, 8.0F, 1),
            new Part(Goety.location("block/end_floating_lantern/blocks1"), 11.9735F, 7.8889F, 11.5F, 2),
            new Part(Goety.location("block/end_floating_lantern/blocks2"), 4.553F, 7.0F, 12.0F, 3),
            new Part(Goety.location("block/end_floating_lantern/blocks3"), 4.553F, 2.8889F, 4.0F, 4),
            new Part(Goety.location("block/end_floating_lantern/blocks4"), 11.9733F, 3.389F, 4.4997F, 5),
            new Part(Goety.location("block/end_floating_lantern/blocks5"), 11.9733F, 10.4444F, 4.4997F, 6));

    private static final float[][] LAMP_IDLE = {
            {
                1.172F, 1.165F, 1.144F, 1.110F, 1.064F, 1.008F, 0.941F, 0.866F, 0.783F, 0.693F, 0.597F, 0.496F, 0.391F, 0.284F, 0.174F, 0.063F, -0.048F, -0.157F, -0.265F, -0.370F, -0.471F, -0.567F, -0.657F, -0.740F, -0.815F, -0.881F, -0.938F, -0.983F, -1.017F, -1.038F, -1.045F, -1.038F, -1.017F, -0.983F, -0.938F, -0.881F, -0.815F, -0.740F, -0.657F, -0.567F, -0.471F, -0.370F, -0.265F, -0.157F, -0.048F, 0.063F, 0.174F, 0.284F, 0.391F, 0.496F, 0.597F, 0.693F, 0.783F, 0.866F, 0.941F, 1.008F, 1.064F, 1.110F, 1.144F, 1.165F },
            {
                0.119F, 0.100F, 0.081F, 0.061F, 0.039F, 0.018F, -0.005F, -0.027F, -0.049F, -0.071F, -0.093F, -0.114F, -0.135F, -0.154F, -0.172F, -0.189F, -0.204F, -0.218F, -0.229F, -0.239F, -0.245F, -0.250F, -0.251F, -0.250F, -0.245F, -0.239F, -0.229F, -0.218F, -0.204F, -0.189F, -0.172F, -0.154F, -0.135F, -0.114F, -0.093F, -0.071F, -0.049F, -0.027F, -0.005F, 0.018F, 0.039F, 0.061F, 0.081F, 0.100F, 0.119F, 0.135F, 0.151F, 0.164F, 0.175F, 0.185F, 0.192F, 0.196F, 0.197F, 0.196F, 0.192F, 0.185F, 0.175F, 0.164F, 0.151F, 0.135F },
            {
                0.841F, 0.709F, 0.569F, 0.422F, 0.268F, 0.111F, -0.050F, -0.211F, -0.373F, -0.534F, -0.691F, -0.844F, -0.992F, -1.132F, -1.264F, -1.385F, -1.495F, -1.592F, -1.675F, -1.741F, -1.791F, -1.822F, -1.832F, -1.822F, -1.791F, -1.741F, -1.675F, -1.592F, -1.495F, -1.385F, -1.264F, -1.132F, -0.992F, -0.844F, -0.691F, -0.534F, -0.373F, -0.211F, -0.050F, 0.111F, 0.268F, 0.422F, 0.569F, 0.709F, 0.841F, 0.962F, 1.072F, 1.169F, 1.252F, 1.319F, 1.368F, 1.399F, 1.409F, 1.399F, 1.368F, 1.319F, 1.252F, 1.169F, 1.072F, 0.962F },
            {
                -0.493F, -0.586F, -0.675F, -0.762F, -0.844F, -0.921F, -0.992F, -1.057F, -1.114F, -1.162F, -1.201F, -1.230F, -1.248F, -1.255F, -1.248F, -1.230F, -1.201F, -1.162F, -1.114F, -1.057F, -0.992F, -0.921F, -0.844F, -0.762F, -0.675F, -0.586F, -0.493F, -0.399F, -0.304F, -0.209F, -0.115F, -0.023F, 0.067F, 0.153F, 0.235F, 0.313F, 0.384F, 0.448F, 0.505F, 0.554F, 0.593F, 0.622F, 0.640F, 0.646F, 0.640F, 0.622F, 0.593F, 0.554F, 0.505F, 0.448F, 0.384F, 0.313F, 0.235F, 0.153F, 0.067F, -0.023F, -0.115F, -0.209F, -0.304F, -0.399F },
            {
                -0.351F, -0.432F, -0.519F, -0.610F, -0.704F, -0.802F, -0.901F, -1.000F, -1.100F, -1.199F, -1.297F, -1.391F, -1.482F, -1.569F, -1.650F, -1.725F, -1.793F, -1.853F, -1.904F, -1.945F, -1.976F, -1.994F, -2.001F, -1.994F, -1.976F, -1.945F, -1.904F, -1.853F, -1.793F, -1.725F, -1.650F, -1.569F, -1.482F, -1.391F, -1.297F, -1.199F, -1.100F, -1.000F, -0.901F, -0.802F, -0.704F, -0.610F, -0.519F, -0.432F, -0.351F, -0.276F, -0.208F, -0.148F, -0.097F, -0.056F, -0.025F, -0.007F, 0.000F, -0.007F, -0.025F, -0.056F, -0.097F, -0.148F, -0.208F, -0.276F },
            {
                2.213F, 2.201F, 2.165F, 2.108F, 2.030F, 1.935F, 1.822F, 1.694F, 1.553F, 1.401F, 1.238F, 1.067F, 0.889F, 0.706F, 0.520F, 0.332F, 0.144F, -0.042F, -0.224F, -0.402F, -0.573F, -0.736F, -0.889F, -1.030F, -1.157F, -1.270F, -1.366F, -1.443F, -1.501F, -1.536F, -1.549F, -1.536F, -1.501F, -1.443F, -1.366F, -1.270F, -1.157F, -1.030F, -0.889F, -0.736F, -0.573F, -0.402F, -0.224F, -0.042F, 0.144F, 0.332F, 0.520F, 0.706F, 0.889F, 1.067F, 1.238F, 1.401F, 1.553F, 1.694F, 1.822F, 1.935F, 2.030F, 2.108F, 2.165F, 2.201F },
            {
                1.616F, 1.464F, 1.298F, 1.117F, 0.924F, 0.721F, 0.511F, 0.294F, 0.074F, -0.148F, -0.371F, -0.591F, -0.808F, -1.018F, -1.221F, -1.413F, -1.594F, -1.761F, -1.912F, -2.046F, -2.159F, -2.251F, -2.319F, -2.361F, -2.376F, -2.361F, -2.319F, -2.251F, -2.159F, -2.046F, -1.912F, -1.761F, -1.594F, -1.413F, -1.221F, -1.018F, -0.808F, -0.591F, -0.371F, -0.148F, 0.074F, 0.294F, 0.511F, 0.721F, 0.924F, 1.117F, 1.298F, 1.464F, 1.616F, 1.749F, 1.862F, 1.954F, 2.022F, 2.064F, 2.079F, 2.064F, 2.022F, 1.954F, 1.862F, 1.749F }
    };

    public EndFloatingLanternRenderer(BlockEntityRendererProvider.Context context) {
    }

    private static float sample(int track, float key) {
        float[] values = LAMP_IDLE[track];
        int i = Mth.floor(key);
        float f = key - i;
        return Mth.lerp(f, values[Math.floorMod(i, KEYS)], values[Math.floorMod(i + 1, KEYS)]);
    }

    @Override
    public void render(EndLanternBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof EndFloatingLanternBlock) || blockEntity.getLevel() == null) {
            return;
        }
        float offset = Math.floorMod(blockEntity.getBlockPos().asLong(), (long) LOOP_TICKS);
        float time = blockEntity.getLevel().getGameTime() + partialTick + offset;
        float key = (time % LOOP_TICKS) / LOOP_TICKS * KEYS;

        Minecraft minecraft = Minecraft.getInstance();
        ModelManager models = minecraft.getModelManager();
        ModelBlockRenderer renderer = minecraft.getBlockRenderer().getModelRenderer();
        VertexConsumer consumer = buffer.getBuffer(RenderType.solid());
        VertexConsumer consumer2 = buffer.getBuffer(RenderType.cutout());

        for (Part part : PARTS) {
            BakedModel model = models.getModel(part.model());
            poseStack.pushPose();
            poseStack.translate(part.x() / 16.0F, (part.y() + sample(part.track(), key)) / 16.0F, part.z() / 16.0F);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            renderer.renderModel(poseStack.last(), consumer, state, model, 1.0F, 1.0F, 1.0F, packedLight, packedOverlay, ModelData.EMPTY, RenderType.solid());
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.translate(part.x() / 16.0F, (part.y() + sample(part.track(), key)) / 16.0F, part.z() / 16.0F);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            renderer.renderModel(poseStack.last(), consumer2, state, model, 1.0F, 1.0F, 1.0F, packedLight, packedOverlay, ModelData.EMPTY, RenderType.cutout());
            poseStack.popPose();
        }
    }
}
