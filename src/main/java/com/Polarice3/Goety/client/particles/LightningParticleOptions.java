package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;

/***
 * This class is based of BoltParticleOptions Code from Botania: "<a href="https://github.com/VazkiiMods/Botania/blob/1.19.x/Xplat/src/main/java/vazkii/botania/client/fx/BoltParticleOptions.java">...</a>">...</a>
 */
public class LightningParticleOptions {
    private final BoltRenderInfo renderInfo;

    private final Vec3 start;
    private final Vec3 end;

    private final int segments;

    private final int count;
    private final float size;

    private final int lifespan;

    private final SpawnFunction spawnFunction;
    private final FadeFunction fadeFunction;

    public LightningParticleOptions(Vec3 start, Vec3 end) {
        this(BoltRenderInfo.DEFAULT, start, end);
    }

    public LightningParticleOptions(Vec3 start, Vec3 end, int lifespan) {
        this(BoltRenderInfo.DEFAULT, start, end, lifespan, (int) (Math.sqrt(start.distanceTo(end) * 100)));
    }

    public LightningParticleOptions(BoltRenderInfo info, Vec3 start, Vec3 end) {
        this(info, start, end, 30);
    }

    public LightningParticleOptions(BoltRenderInfo info, Vec3 start, Vec3 end, int lifespan) {
        this(info, start, end, lifespan, (int) (Math.sqrt(start.distanceTo(end) * 100)));
    }

    public LightningParticleOptions(BoltRenderInfo info, Vec3 start, Vec3 end, int lifespan, int segments) {
        this(info, start, end, segments, 1, 0.1F, lifespan, SpawnFunction.delay(60), FadeFunction.fade(0.5F));
    }

    public LightningParticleOptions(BoltRenderInfo info, Vec3 start, Vec3 end, int segments,
                               int count,
                               float size,
                               int lifespan,
                               SpawnFunction spawnFunction,
                               FadeFunction fadeFunction) {
        this.renderInfo = info;
        this.start = start;
        this.end = end;
        this.segments = segments;
        this.count = count;
        this.size = size;
        this.lifespan = lifespan;
        this.spawnFunction = spawnFunction;
        this.fadeFunction = fadeFunction;
    }

    public LightningParticleOptions count(int count) {
        return new LightningParticleOptions(
                this.renderInfo, this.start, this.end, this.segments,
                count,
                this.size, this.lifespan, this.spawnFunction, this.fadeFunction);
    }

    public LightningParticleOptions size(float size) {
        return new LightningParticleOptions(
                this.renderInfo, this.start, this.end, this.segments, this.count,
                size,
                this.lifespan, this.spawnFunction, this.fadeFunction);
    }

    public LightningParticleOptions spawn(SpawnFunction spawnFunction) {
        return new LightningParticleOptions(
                this.renderInfo, this.start, this.end, this.segments, this.count,
                this.size, this.lifespan,
                spawnFunction,
                this.fadeFunction);
    }

    public LightningParticleOptions fade(FadeFunction fadeFunction) {
        return new LightningParticleOptions(
                this.renderInfo, this.start, this.end, this.segments, this.count,
                this.size, this.lifespan, this.spawnFunction,
                fadeFunction);
    }

    public LightningParticleOptions lifespan(int lifespan) {
        return new LightningParticleOptions(
                this.renderInfo, this.start, this.end, this.segments, this.count, this.size,
                lifespan,
                this.spawnFunction, this.fadeFunction);
    }

    public int getLifespan() {
        return lifespan;
    }

    public SpawnFunction getSpawnFunction() {
        return spawnFunction;
    }

    public FadeFunction getFadeFunction() {
        return fadeFunction;
    }

    public ColorUtil getColor() {
        return renderInfo.color;
    }

    public List<BoltQuads> generate() {
        Random random = new Random();
        List<BoltQuads> quads = new ArrayList<>();
        Vec3 diff = end.subtract(start);
        float totalDistance = (float) diff.length();
        for (int i = 0; i < count; i++) {
            Queue<BoltInstructions> drawQueue = new ArrayDeque<>();
            drawQueue.add(new BoltInstructions(start, 0, Vec3.ZERO, null, false));
            while (!drawQueue.isEmpty()) {
                BoltInstructions data = drawQueue.poll();
                Vec3 perpendicularDist = data.perpendicularDist();
                float progress = data.progress() + (1F / segments) * (1 - renderInfo.parallelNoise + random.nextFloat() * renderInfo.parallelNoise * 2);
                Vec3 segmentEnd;
                float segmentDiffScale = renderInfo.spreadFunction.getMaxSpread(progress);
                if (progress >= 1 && segmentDiffScale <= 0) {
                    segmentEnd = end;
                } else {
                    float maxDiff = renderInfo.spreadFactor * segmentDiffScale * totalDistance;
                    Vec3 randVec = findRandomOrthogonalVector(diff, random);
                    double rand = renderInfo.randomFunction.getRandom(random);
                    perpendicularDist = renderInfo.segmentSpreader.getSegmentAdd(perpendicularDist, randVec, maxDiff, segmentDiffScale, progress, rand);
                    segmentEnd = start.add(diff.scale(progress)).add(perpendicularDist);
                }
                float boltSize = size * (0.5F + (1 - progress) * 0.5F);
                Pair<BoltQuads, QuadCache> quadData = createQuads(data.cache(), data.start(), segmentEnd, boltSize);
                quads.add(quadData.getLeft());

                if (progress >= 1) {
                    break;
                } else if (!data.isBranch()) {
                    drawQueue.add(new BoltInstructions(segmentEnd, progress, perpendicularDist, quadData.getRight(), false));
                } else if (random.nextFloat() < renderInfo.branchContinuationFactor) {
                    drawQueue.add(new BoltInstructions(segmentEnd, progress, perpendicularDist, quadData.getRight(), true));
                }

                while (random.nextFloat() < renderInfo.branchInitiationFactor * (1 - progress)) {
                    drawQueue.add(new BoltInstructions(segmentEnd, progress, perpendicularDist, quadData.getRight(), true));
                }
            }
        }
        return quads;
    }

    private static Vec3 findRandomOrthogonalVector(Vec3 vec, Random rand) {
        Vec3 newVec = new Vec3(-0.5 + rand.nextDouble(), -0.5 + rand.nextDouble(), -0.5 + rand.nextDouble());
        return vec.cross(newVec).normalize();
    }

    private Pair<BoltQuads, QuadCache> createQuads(@Nullable QuadCache cache, Vec3 startPos, Vec3 end, float size) {
        Vec3 diff = end.subtract(startPos);
        Vec3 rightAdd = diff.cross(new Vec3(0.5, 0.5, 0.5)).normalize().scale(size);
        Vec3 backAdd = diff.cross(rightAdd).normalize().scale(size);
        Vec3 rightAddSplit = rightAdd.scale(0.5F);

        Vec3 start = cache != null ? cache.prevEnd() : startPos;
        Vec3 startRight = cache != null ? cache.prevEndRight() : start.add(rightAdd);
        Vec3 startBack = cache != null ? cache.prevEndBack() : start.add(rightAddSplit).add(backAdd);
        Vec3 endRight = end.add(rightAdd);
        Vec3 endBack = end.add(rightAddSplit).add(backAdd);

        BoltQuads quads = new BoltQuads();
        quads.addQuad(start, end, endRight, startRight);
        quads.addQuad(startRight, endRight, end, start);

        quads.addQuad(startRight, endRight, endBack, startBack);
        quads.addQuad(startBack, endBack, endRight, startRight);

        return Pair.of(quads, new QuadCache(end, endRight, endBack));
    }

    private record QuadCache(Vec3 prevEnd, Vec3 prevEndRight, Vec3 prevEndBack) {
    }

    private record BoltInstructions(Vec3 start, float progress,
                                    Vec3 perpendicularDist,
                                    @Nullable QuadCache cache, boolean isBranch) {
    }

    public static class BoltQuads {

        private final List<Vec3> vecs = new ArrayList<>();

        protected void addQuad(Vec3... quadVecs) {
            vecs.addAll(Arrays.asList(quadVecs));
        }

        public List<Vec3> getVecs() {
            return vecs;
        }
    }

    public interface SpreadFunction {

        SpreadFunction LINEAR_ASCENT = progress -> progress;
        SpreadFunction LINEAR_ASCENT_DESCENT = progress -> (progress - Math.max(0, 2 * progress - 1)) / 0.5F;
        SpreadFunction SINE = progress -> (float) Math.sin(Math.PI * progress);

        float getMaxSpread(float progress);
    }

    public interface RandomFunction {
        RandomFunction UNIFORM = Random::nextFloat;
        RandomFunction GAUSSIAN = rand -> (float) rand.nextGaussian();

        float getRandom(Random rand);
    }

    public interface SegmentSpreader {

        SegmentSpreader NO_MEMORY = (perpendicularDist, randVec, maxDiff, scale, progress, rand) -> randVec.scale(maxDiff * rand);

        static SegmentSpreader memory(float memoryFactor) {
            return (perpendicularDist, randVec, maxDiff, spreadScale, progress, rand) -> {
                double nextDiff = maxDiff * (1 - memoryFactor) * rand;
                Vec3 cur = randVec.scale(nextDiff);
                perpendicularDist = perpendicularDist.add(cur);
                double length = perpendicularDist.length();
                if (length > maxDiff) {
                    perpendicularDist = perpendicularDist.scale(maxDiff / length);
                }
                return perpendicularDist.add(cur);
            };
        }

        Vec3 getSegmentAdd(Vec3 perpendicularDist, Vec3 randVec, float maxDiff, float scale, float progress, double rand);
    }

    public interface SpawnFunction {

        SpawnFunction NO_DELAY = rand -> Pair.of(0F, 0F);
        SpawnFunction CONSECUTIVE = new SpawnFunction() {
            @Override
            public Pair<Float, Float> getSpawnDelayBounds(Random rand) {
                return Pair.of(0F, 0F);
            }

            @Override
            public boolean isConsecutive() {
                return true;
            }
        };

        static SpawnFunction delay(float delay) {
            return rand -> Pair.of(delay, delay);
        }

        static SpawnFunction noise(float delay, float noise) {
            return rand -> Pair.of(delay - noise, delay + noise);
        }

        Pair<Float, Float> getSpawnDelayBounds(Random rand);

        default float getSpawnDelay(Random rand) {
            Pair<Float, Float> bounds = getSpawnDelayBounds(rand);
            return bounds.getLeft() + (bounds.getRight() - bounds.getLeft()) * rand.nextFloat();
        }

        default boolean isConsecutive() {
            return false;
        }
    }

    public interface FadeFunction {

        FadeFunction NONE = (totalBolts, lifeScale) -> Pair.of(0, totalBolts);

        static FadeFunction fade(float fade) {
            return (totalBolts, lifeScale) -> {
                int start = lifeScale > (1 - fade) ? (int) (totalBolts * (lifeScale - (1 - fade)) / fade) : 0;
                int end = lifeScale < fade ? (int) (totalBolts * (lifeScale / fade)) : totalBolts;
                return Pair.of(start, end);
            };
        }

        Pair<Integer, Integer> getRenderBounds(int totalBolts, float lifeScale);
    }

    public static class BoltRenderInfo {

        public static final BoltRenderInfo DEFAULT = defaultConfig();

        private final float parallelNoise;

        private final float spreadFactor;

        private final float branchInitiationFactor;
        private final float branchContinuationFactor;

        private final ColorUtil color;

        private final RandomFunction randomFunction;
        private final SpreadFunction spreadFunction;
        private final SegmentSpreader segmentSpreader;

        private BoltRenderInfo(
                float parallelNoise,
                float spreadFactor,
                float branchInitiationFactor,
                float branchContinuationFactor,
                ColorUtil color,
                RandomFunction randomFunction,
                SpreadFunction spreadFunction,
                SegmentSpreader segmentSpreader) {
            this.parallelNoise = parallelNoise;
            this.spreadFactor = spreadFactor;
            this.branchInitiationFactor = branchInitiationFactor;
            this.branchContinuationFactor = branchContinuationFactor;
            this.color = color;
            this.randomFunction = randomFunction;
            this.spreadFunction = spreadFunction;
            this.segmentSpreader = segmentSpreader;
        }

        private static BoltRenderInfo defaultConfig() {
            return new BoltRenderInfo(
                    0.1F, 0.1F, 0.0F, 0.0F,
                    new ColorUtil(177,
                            171,
                            241,
                            0.8F),
                    RandomFunction.GAUSSIAN,
                    SpreadFunction.SINE,
                    SegmentSpreader.NO_MEMORY
            );
        }

        public static BoltRenderInfo thunderBolt(ColorUtil colorUtil){
            return new BoltRenderInfo(
                    0.1F, 0.1F, 0.0F, 0.0F,
                    colorUtil,
                    RandomFunction.GAUSSIAN,
                    SpreadFunction.LINEAR_ASCENT,
                    SegmentSpreader.NO_MEMORY
            );
        }

        public BoltRenderInfo noise(float parallelNoise, float spreadFactor) {
            return new BoltRenderInfo(
                    parallelNoise,
                    spreadFactor,
                    this.branchInitiationFactor,
                    this.branchContinuationFactor,
                    this.color,
                    this.randomFunction,
                    this.spreadFunction,
                    this.segmentSpreader
            );
        }

        public BoltRenderInfo branching(float branchInitiationFactor, float branchContinuationFactor) {
            return new BoltRenderInfo(
                    this.parallelNoise,
                    this.spreadFactor,
                    branchInitiationFactor,
                    branchContinuationFactor,
                    this.color,
                    this.randomFunction,
                    this.spreadFunction,
                    this.segmentSpreader
            );
        }

        public BoltRenderInfo spreader(SegmentSpreader segmentSpreader) {
            return new BoltRenderInfo(
                    this.parallelNoise,
                    this.spreadFactor,
                    this.branchInitiationFactor,
                    this.branchContinuationFactor,
                    this.color,
                    this.randomFunction,
                    this.spreadFunction,
                    segmentSpreader
            );
        }

        public BoltRenderInfo randomFunction(RandomFunction randomFunction) {
            return new BoltRenderInfo(
                    this.parallelNoise,
                    this.spreadFactor,
                    this.branchInitiationFactor,
                    this.branchContinuationFactor,
                    this.color,
                    randomFunction,
                    this.spreadFunction,
                    this.segmentSpreader
            );
        }

        public BoltRenderInfo spreadFunction(SpreadFunction spreadFunction) {
            return new BoltRenderInfo(
                    this.parallelNoise,
                    this.spreadFactor,
                    this.branchInitiationFactor,
                    this.branchContinuationFactor,
                    this.color,
                    this.randomFunction,
                    spreadFunction,
                    this.segmentSpreader
            );
        }

        public BoltRenderInfo color(ColorUtil color) {
            return new BoltRenderInfo(
                    this.parallelNoise,
                    this.spreadFactor,
                    this.branchInitiationFactor,
                    this.branchContinuationFactor,
                    color,
                    this.randomFunction,
                    this.spreadFunction,
                    this.segmentSpreader
            );
        }
    }
}
