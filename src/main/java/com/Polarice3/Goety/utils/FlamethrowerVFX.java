package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.client.particles.ColoredSpellSquareParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public final class FlamethrowerVFX {
    private static final double CM = 0.01D;
    private static final double DT = 0.05D;

    public static final double DEFAULT_RANGE = 5.0D;

    public static final int SPRITE_LIFE = 12;
    public static final float SPRITE_FRICTION = 1.0F / (1.0F + 2.0F * (float) DT);
    public static final float SPRITE_GROW = 0.2F;
    private static final float SPRITE_RATE = 60.0F / 20.0F;
    private static final int SPRITE_BURST = 30;
    private static final double SPRITE_REACH_MIN = 700.0D / 800.0D;
    private static final double SPRITE_TRAVEL = (1.0D - SPRITE_FRICTION) / (1.0D - Math.pow(SPRITE_FRICTION, SPRITE_LIFE));
    private static final double SPRITE_CONE_COS = Math.cos(Math.toRadians(5.0D));
    private static final double SPRITE_SPHERE = 5.0D;

    private static final float SPARK_RATE = 60.0F / 20.0F;
    private static final int SPARK_LIFE_MIN = 20, SPARK_LIFE_RANGE = 20;
    private static final double SPARK_SPEED_MIN = 400.0D, SPARK_SPEED_RANGE = 400.0D;
    private static final double SPARK_CONE_COS = Math.cos(Math.toRadians(10.0D));
    private static final double SPARK_DROP = 15.0D;
    private static final float SPARK_SIZE_MIN = 0.05F, SPARK_SIZE_RANGE = 0.01F;
    private static final float SPARK_R = 1.0F, SPARK_G = 0.15F, SPARK_B = 0.0F;

    private static final float GROUND_RATE = 1.0f;
    private static final int GROUND_DELAY = 10;
    private static final double GROUND_WEDGE = Math.toRadians(5.0D);
    private static final int GROUND_SEARCH = 4;

    private FlamethrowerVFX() {
    }

    public static void flamethrower(LivingEntity breather, int breathTick) {
        flamethrower(breather, breather.getViewVector(1.0F), breathTick);
    }

    public static void flamethrower(LivingEntity breather, Vec3 direction, int breathTick) {
        flamethrower(breather, direction, breathTick, DEFAULT_RANGE);
    }

    public static void flamethrower(LivingEntity breather, Vec3 direction, int breathTick, double range) {
        if (breather.level() instanceof ServerLevel serverLevel) {
            double offset = breather.getBbWidth() * 0.5D;
            Vec3 origin = breather.position()
                    .add(0.0D, breather.getBbHeight() * 0.5D, 0.0D)
                    .add(direction.normalize().scale(offset));
            flamethrower(serverLevel, breather, origin, direction, breathTick, Math.max(range - offset, 0.0D), breather.getRandom());
        }
    }

    public static void flamethrower(ServerLevel level, @Nullable Entity source, Vec3 origin, Vec3 direction, int breathTick, RandomSource random) {
        flamethrower(level, source, origin, direction, breathTick, DEFAULT_RANGE, random);
    }

    public static void flamethrower(ServerLevel level, @Nullable Entity source, Vec3 origin, Vec3 direction, int breathTick, double range, RandomSource random) {
        Vec3 forward = direction.normalize();
        Vec3[] basis = basis(forward);

        double spriteReach = reach(level, source, origin, forward, range);
        int sprites = count(SPRITE_RATE, random) + (breathTick == 0 ? SPRITE_BURST : 0);
        for (int i = 0; i < sprites; i++) {
            Vec3 pos = origin.add(inSphere(random).scale(SPRITE_SPHERE * CM));
            double speed = spriteReach * (SPRITE_REACH_MIN + random.nextDouble() * (1.0D - SPRITE_REACH_MIN)) * SPRITE_TRAVEL;
            Vec3 vel = inCone(forward, basis, SPRITE_CONE_COS, random).scale(speed);
            level.sendParticles(ModParticleTypes.FLAMETHROWER.get(), pos.x, pos.y, pos.z, 0, vel.x, vel.y, vel.z, 1.0D);
        }

        int sparks = count(SPARK_RATE, random);
        for (int i = 0; i < sparks; i++) {
            Vec3 pos = origin.add(0.0D, -SPARK_DROP * CM, 0.0D);
            double speed = (SPARK_SPEED_MIN + random.nextDouble() * SPARK_SPEED_RANGE) * CM * DT;
            Vec3 vel = inCone(forward, basis, SPARK_CONE_COS, random).scale(speed);
            ColoredSpellSquareParticleOption ember = new ColoredSpellSquareParticleOption(
                    SPARK_SIZE_MIN + random.nextFloat() * SPARK_SIZE_RANGE, SPARK_R, SPARK_G, SPARK_B,
                    SPARK_LIFE_MIN + random.nextInt(SPARK_LIFE_RANGE + 1), true);
            level.sendParticles(ember, pos.x, pos.y, pos.z, 0, vel.x, vel.y, vel.z, 1.0D);
        }

        if (breathTick >= GROUND_DELAY) {
            int ground = count(GROUND_RATE, random);
            if (ground > 0) {
                Vec3 flat = new Vec3(forward.x, 0.0D, forward.z);
                if (flat.lengthSqr() < 1.0E-4D) {
                    return;
                }
                flat = flat.normalize();
                double reach = reach(level, source, origin, flat, range);
                for (int i = 0; i < ground; i++) {
                    groundFire(level, origin, flat, range, reach, random);
                }
            }
        }
    }

    private static void groundFire(ServerLevel level, Vec3 origin, Vec3 flat, double range, double reach, RandomSource random) {
        double angle = (random.nextDouble() * 2.0D - 1.0D) * GROUND_WEDGE;
        double distance = range * Math.sqrt(random.nextDouble());
        if (distance > reach) {
            return;
        }
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        Vec3 dir = new Vec3(flat.x * cos - flat.z * sin, 0.0D, flat.x * sin + flat.z * cos);
        Vec3 point = origin.add(dir.scale(distance));
        BlockPos.MutableBlockPos pos = BlockPos.containing(point).mutable();
        for (int i = 0; i <= GROUND_SEARCH; i++) {
            BlockState state = level.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(level, pos);
            if (!shape.isEmpty()) {
                double y = pos.getY() + shape.max(Direction.Axis.Y);
                if (y <= point.y + 0.01D) {
                    level.sendParticles(ModParticleTypes.SMALL_FIRE_GROUND.get(), point.x, y + 0.25D, point.z, 0, 0.0D, 0.0D, 0.0D, 1.0D);
                }
                return;
            }
            pos.move(0, -1, 0);
        }
    }

    private static double reach(ServerLevel level, @Nullable Entity source, Vec3 origin, Vec3 direction, double max) {
        Vec3 end = origin.add(direction.scale(max));
        BlockHitResult hit = level.clip(new ClipContext(origin, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source));
        return hit.getType() == HitResult.Type.MISS ? max : hit.getLocation().distanceTo(origin);
    }

    private static int count(float rate, RandomSource random) {
        int n = Mth.floor(rate);
        return random.nextFloat() < rate - n ? n + 1 : n;
    }

    private static Vec3[] basis(Vec3 forward) {
        Vec3 up = Math.abs(forward.y) > 0.99D ? new Vec3(1.0D, 0.0D, 0.0D) : new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 right = forward.cross(up).normalize();
        return new Vec3[]{right, right.cross(forward).normalize()};
    }

    private static Vec3 inCone(Vec3 forward, Vec3[] basis, double minCos, RandomSource random) {
        double cos = minCos + random.nextDouble() * (1.0D - minCos);
        double sin = Math.sqrt(Math.max(0.0D, 1.0D - cos * cos));
        double phi = random.nextDouble() * Math.PI * 2.0D;
        return forward.scale(cos).add(basis[0].scale(sin * Math.cos(phi))).add(basis[1].scale(sin * Math.sin(phi)));
    }

    private static Vec3 inSphere(RandomSource random) {
        double z = random.nextDouble() * 2.0D - 1.0D;
        double phi = random.nextDouble() * Math.PI * 2.0D;
        double r = Math.cbrt(random.nextDouble());
        double s = Math.sqrt(1.0D - z * z) * r;
        return new Vec3(s * Math.cos(phi), z * r, s * Math.sin(phi));
    }
}
