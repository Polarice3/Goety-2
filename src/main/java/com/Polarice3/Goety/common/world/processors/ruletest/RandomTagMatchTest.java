package com.Polarice3.Goety.common.world.processors.ruletest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class RandomTagMatchTest extends RuleTest {
    public static final Codec<RandomTagMatchTest> CODEC = RecordCodecBuilder.create((p_259017_) -> {
        return p_259017_.group(TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter((p_163766_) -> {
            return p_163766_.tag;
        }), Codec.FLOAT.fieldOf("probability").forGetter((p_163764_) -> {
            return p_163764_.probability;
        })).apply(p_259017_, RandomTagMatchTest::new);
    });
    private final TagKey<Block> tag;
    private final float probability;

    public RandomTagMatchTest(TagKey<Block> p_74280_, float p_74281_) {
        this.tag = p_74280_;
        this.probability = p_74281_;
    }

    public boolean test(BlockState p_230320_, RandomSource p_230321_) {
        return p_230320_.is(this.tag) && p_230321_.nextFloat() < this.probability;
    }

    protected RuleTestType<?> getType() {
        return ModRuleTests.RANDOM_TAG_MATCH_TEST.get();
    }
}
