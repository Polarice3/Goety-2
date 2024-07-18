package com.Polarice3.Goety.common.entities.ally.spider;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class CaveSpiderServant extends SpiderServant{
    public CaveSpiderServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return SpiderServant.setCustomAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CaveSpiderServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CaveSpiderServantDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.CaveSpiderServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.CaveSpiderServantDamage.get());
    }

    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            if (target instanceof LivingEntity livingEntity) {
                int i = this.getMasterOwner() instanceof Player ? 7 : 0;
                if (this.level.getDifficulty() == Difficulty.NORMAL) {
                    i = 7;
                } else if (this.level.getDifficulty() == Difficulty.HARD) {
                    i = 15;
                }

                if (i > 0) {
                    MobEffect effect = MobEffects.POISON;
                    if (CuriosFinder.hasWildRobe(this.getMasterOwner())){
                        effect = GoetyEffects.ACID_VENOM.get();
                    }
                    livingEntity.addEffect(new MobEffectInstance(effect, i * 20, 0), this);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_32259_, DifficultyInstance p_32260_, MobSpawnType p_32261_, @Nullable SpawnGroupData p_32262_, @Nullable CompoundTag p_32263_) {
        return p_32262_;
    }

    protected float getStandingEyeHeight(Pose p_32265_, EntityDimensions p_32266_) {
        return 0.45F;
    }
}
