package com.Polarice3.Goety.common.effects;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.Tags;

public class HysteriaEffect extends GoetyBaseEffect{

    public HysteriaEffect() {
        super(MobEffectCategory.NEUTRAL, 0);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE,
                "08cdfeab-dca7-40a1-ad22-ae8909c2c7a3",
                0.75D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                "da84b2ce-6812-4d03-bae5-7052e80e439b",
                0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public void applyEffectTick(LivingEntity living, int amplify) {
        if (living.tickCount % 20 == 0) {
            float amp = (amplify + 1.0F) / 10.0F;
            float damage = living.getMaxHealth() * amp;
            if (living.getHealth() <= damage) {
                living.hurt(ModDamageSource.getDamageSource(living.level, ModDamageSource.RAGE), damage);
            } else {
                living.setHealth(living.getHealth() - damage);
            }
        }
        if (living.level instanceof ServerLevel serverLevel) {
            if (living.canChangeDimensions()
                    && !living.getType().is(Tags.EntityTypes.BOSSES)
                    && !living.isDeadOrDying()) {
                int i1 = living.isInvisible() ? 15 : 4;
                int j = 1;
                if (living.getRandom().nextInt(i1 * j) == 0) {
                    serverLevel.sendParticles(ModParticleTypes.HYSTERIA.get(), living.getRandomX(0.5D), living.getRandomY(), living.getRandomZ(0.5D), 1, 0.0D, 0.5D, 0.0D, 0);
                }
            }
        }
    }

    public boolean isDurationEffectTick(int tick, int amplify) {
        return true;
    }
}
