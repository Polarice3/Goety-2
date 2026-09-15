package com.Polarice3.Goety.common.world.processors.ruletest;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModRuleTests {
    public static final DeferredRegister<RuleTestType<?>> RULE_TESTS = DeferredRegister.create(Registries.RULE_TEST, Goety.MOD_ID);

    public static final RegistryObject<RuleTestType<RandomTagMatchTest>> RANDOM_TAG_MATCH_TEST = RULE_TESTS.register("random_tag_match", () -> () -> RandomTagMatchTest.CODEC);

}
