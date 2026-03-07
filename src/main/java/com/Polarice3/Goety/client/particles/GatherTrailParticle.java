package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.Easing;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public class GatherTrailParticle extends TextureSheetParticle {
    private final float red, green, blue;
    public final Vec3 fromPos;
    public final Vec3 toPos;

    protected GatherTrailParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, float red, float green, float blue, Vec3 end, SpriteSet spriteSet) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.fromPos = new Vec3(x, y, z);
        this.toPos = end;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.lifetime = Math.round(this.random.nextFloat() * 3.0F) + 5;
        this.pickSprite(spriteSet);
    }

    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        Vec3 camPos = camera.getPosition();
        PoseStack stack = new PoseStack();
        stack.pushPose();
        stack.translate(-camPos.x, -camPos.y, -camPos.z);
        double currentX = Mth.lerp(partialTick, this.xo, this.x);
        double currentY = Mth.lerp(partialTick, this.yo, this.y);
        double currentZ = Mth.lerp(partialTick, this.zo, this.z);
        Vec3 sight = camPos.subtract(currentX, currentY, currentZ).scale(-1);
        Vec3 direction = toPos.subtract(fromPos);
        Vec3 start = new Vec3(currentX, currentY, currentZ).add(direction.scale(Easing.IN_OUT_QUAD.interpolate(Math.min(age + partialTick, lifetime) / lifetime, 0, 1)));
        Vec3 end = start.add(direction.scale(Easing.IN_OUT_QUAD.interpolate(Mth.abs(Math.min(age + partialTick, lifetime) / lifetime - 0.5f) * 2, 0.5F, 0)));
        if (end.distanceTo(fromPos) > direction.length()) {
            end = toPos;
        }
        Vec3 offset = end.subtract(start);
        Vec3 sideOffset = offset.cross(sight).normalize().scale(0.03);
        PoseStack.Pose pose = stack.last();
        float u0 = this.getU0();
        float u1 = Easing.IN_OUT_QUAD.interpolate(Mth.abs(Math.min(age + partialTick, lifetime) / lifetime - 0.5f) * 2, this.getU1(), this.getU0());
        float v0 = this.getV0();
        float v1 = this.getV1();
        vertex(consumer, pose, start.add(sideOffset), u0, v0, LightTexture.FULL_BRIGHT);
        vertex(consumer, pose, start.add(sideOffset.scale(-1)), u0, v1, LightTexture.FULL_BRIGHT);
        vertex(consumer, pose, end.add(sideOffset.scale(-1)), u1, v1, LightTexture.FULL_BRIGHT);
        vertex(consumer, pose, end.add(sideOffset), u1, v0, LightTexture.FULL_BRIGHT);
        stack.popPose();
    }

    private void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 vec3, float u, float v, int light) {
        consumer.vertex(pose.pose(), (float) vec3.x(), (float) vec3.y(), (float) vec3.z()).uv(u, v).color(red, green, blue, alpha).uv2(light).endVertex();
    }

    public static class Provider implements ParticleProvider<Option> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(Option pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new GatherTrailParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType.red, pType.green, pType.blue, new Vec3(pType.endX, pType.endY, pType.endZ), sprites);
        }
    }

    public static class Option implements ParticleOptions {
        public static final Codec<Option> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
                Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
                Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
                Codec.FLOAT.fieldOf("endX").forGetter(d -> d.endX),
                Codec.FLOAT.fieldOf("endY").forGetter(d -> d.endY),
                Codec.FLOAT.fieldOf("endZ").forGetter(d -> d.endZ)
        ).apply(instance, Option::new));
        public static final Deserializer<Option> DESERIALIZER = new Deserializer<Option>() {
            public Option fromCommand(ParticleType<Option> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                float red = reader.readFloat();
                reader.expect(' ');
                float green = reader.readFloat();
                reader.expect(' ');
                float blue = reader.readFloat();
                reader.expect(' ');
                float endX = reader.readFloat();
                reader.expect(' ');
                float endY = reader.readFloat();
                reader.expect(' ');
                float endZ = reader.readFloat();
                return new Option(red, green, blue, endX, endY, endZ);
            }

            public Option fromNetwork(ParticleType<Option> particleTypeIn, FriendlyByteBuf buffer) {
                return new Option(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }
        };
        private final float red;
        private final float green;
        private final float blue;
        private final float endX;
        private final float endY;
        private final float endZ;

        public Option(ColorUtil color, Vec3 end) {
            this.red = color.red();
            this.green = color.green();
            this.blue = color.blue();
            this.endX = (float) end.x;
            this.endY = (float) end.y;
            this.endZ = (float) end.z;
        }

        public Option(float red, float green, float blue, Vec3 end) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.endX = (float) end.x;
            this.endY = (float) end.y;
            this.endZ = (float) end.z;
        }

        public Option(float red, float green, float blue, float endX, float endY, float endZ) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.endX = endX;
            this.endY = endY;
            this.endZ = endZ;
        }

        public void writeToNetwork(FriendlyByteBuf buffer) {
            buffer.writeFloat(this.red);
            buffer.writeFloat(this.green);
            buffer.writeFloat(this.blue);
            buffer.writeFloat(this.endX);
            buffer.writeFloat(this.endY);
            buffer.writeFloat(this.endZ);
        }

        public String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f",
                    BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.endX, this.endY, this.endZ);
        }

        public ParticleType<Option> getType() {
            return ModParticleTypes.GATHER_TRAIL.get();
        }

        public float getRed() {
            return this.red;
        }

        public float getGreen() {
            return this.green;
        }

        public float getBlue() {
            return this.blue;
        }

        public float getEndX() {
            return this.endX;
        }

        public float getEndY() {
            return this.endY;
        }

        public float getEndZ() {
            return this.endZ;
        }
    }
}
