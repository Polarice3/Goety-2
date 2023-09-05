package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.AttributesConfig;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class VanguardServant extends AbstractSkeletonServant {
    public VanguardServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 25.0F)
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SkeletonServantDamage.get())
                .add(Attributes.ARMOR, 2.0D);
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
    }

    @Override
    protected SoundEvent getStepSound() {
        return null;
    }
}
