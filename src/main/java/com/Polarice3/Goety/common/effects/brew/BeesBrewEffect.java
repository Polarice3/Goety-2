package com.Polarice3.Goety.common.effects.brew;

import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ConstantPaths;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;

import javax.annotation.Nullable;

public class BeesBrewEffect extends BrewEffect {
    public BeesBrewEffect(int soulCost, int capacityExtra) {
        super("bees", soulCost, capacityExtra, MobEffectCategory.HARMFUL, 0xfed668);
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
        if (!(pTarget instanceof Bee) && pTarget != null && pTarget != pIndirectSource && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(pTarget)) {
            for (int i = 0; i < 3 + pAmplifier; ++i) {
                Bee bee = new Bee(EntityType.BEE, pTarget.level);
                bee.moveTo(BlockFinder.SummonRadius(pTarget.blockPosition(), bee, pTarget.level), 0.0F, 0.0F);
                bee.addTag(ConstantPaths.conjuredBee());
                bee.startPersistentAngerTimer();
                bee.setPersistentAngerTarget(pTarget.getUUID());
                bee.setTarget(pTarget);
                pTarget.level.addFreshEntity(bee);
            }
        }
    }
}
