package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class Hellhound extends BlackWolf{

    public Hellhound(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            if (!entityIn.fireImmune()) {
                float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
                entityIn.setSecondsOnFire(5 * (int) f);
            } else if (entityIn instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, MathHelper.secondsToTicks(5), 0), this);
            }
        }
        return flag;
    }

    @Override
    public void curseTarget(Entity entity) {
    }
}
