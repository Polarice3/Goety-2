package com.Polarice3.Goety.common.effects.brew;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.VampireBat;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ConstantPaths;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;

import javax.annotation.Nullable;

public class BatsBrewEffect extends BrewEffect {
    public BatsBrewEffect(int soulCost, int capacityExtra) {
        super("bats", soulCost, capacityExtra, MobEffectCategory.HARMFUL, 0x524020);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, @Nullable Entity pIndirectSource, int pAmplifier){
        if (!(pTarget instanceof Bat) && pTarget != pIndirectSource && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(pTarget)) {
            int amount = pTarget.level.random.nextInt(pAmplifier + 2) + 3;
            for (int i = 0; i < amount; ++i) {
                VampireBat bat = new VampireBat(ModEntityType.VAMPIRE_BAT.get(), pTarget.level);
                bat.moveTo(BlockFinder.SummonRadius(pTarget, pTarget.level), 0.0F, 0.0F);
                if (pIndirectSource instanceof LivingEntity livingEntity){
                    bat.setTrueOwner(livingEntity);
                }
                bat.setTarget(pTarget);
                bat.addTag(ConstantPaths.conjuredBat());
                pTarget.level.addFreshEntity(bat);
            }
        }
    }
}
