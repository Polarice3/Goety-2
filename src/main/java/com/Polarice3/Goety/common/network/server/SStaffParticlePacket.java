package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.particles.GatherTrailParticle;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ModelUtil;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class SStaffParticlePacket {
    private final int playerId;
    private final float staffHeight;
    private final float range;
    private final int color;
    private final boolean offHand;

    public SStaffParticlePacket(int playerId, float staffHeight, float range, int color, boolean offHand) {
        this.playerId = playerId;
        this.staffHeight = staffHeight;
        this.range = range;
        this.color = color;
        this.offHand = offHand;
    }

    public static void encode(SStaffParticlePacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.playerId);
        buffer.writeFloat(packet.staffHeight);
        buffer.writeFloat(packet.range);
        buffer.writeInt(packet.color);
        buffer.writeBoolean(packet.offHand);
    }

    public static SStaffParticlePacket decode(FriendlyByteBuf buffer) {
        return new SStaffParticlePacket(
                buffer.readInt(),
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readInt(),
                buffer.readBoolean());
    }

    public static void consume(SStaffParticlePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Level level = Goety.PROXY.getLevel();
                if (level instanceof ClientLevel clientWorld && clientWorld.getEntity(packet.playerId) instanceof Player player) {
                    Optional<Vec3> staffEndPos;
                    if (player == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                        // first-person staff end position
                        int arm = (player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1) * (packet.offHand ? -1 : 1);
                        double fovFactor = 960.0 / (double) Minecraft.getInstance().options.fov().get();
                        float horizontalFactor = 0.125F * packet.staffHeight + 0.35F, verticalFactor = 1.5F * packet.staffHeight - 0.45F;
                        Vec3 vec3 = Minecraft.getInstance().gameRenderer.getMainCamera().getNearPlane().getPointOnPlane(horizontalFactor * arm, verticalFactor).scale(fovFactor);
                        staffEndPos = Optional.of(player.getEyePosition().add(vec3));
                    } else {
                        // third person staff end position
                        staffEndPos = ModelUtil.getThirdPersonPlayerHandPosition(
                                player,
                                Minecraft.getInstance().getEntityRenderDispatcher(),
                                player.yBodyRotO,
                                0,
                                packet.offHand ? player.getMainArm().getOpposite() : player.getMainArm(),
                                new Vec3(0, 0.55, -packet.staffHeight)
                        );
                    }
                    if (staffEndPos.isPresent()) {
                        Vec3 pos = staffEndPos.get().add(new Vec3(level.random.nextDouble() - 0.5, level.random.nextDouble() - 0.5, level.random.nextDouble() - 0.5).normalize().scale(packet.range));
                        level.addParticle(
                                new GatherTrailParticle.Option(new ColorUtil(packet.color), staffEndPos.get()),
                                pos.x, pos.y, pos.z,
                                0, 0, 0
                        );
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
