package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;

public class Rattled extends AbstractSkeleton {
    public Rattled(EntityType<? extends AbstractSkeleton> p_32133_, Level p_32134_) {
        super(p_32133_, p_32134_);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.RattledServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.RattledServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.RattledServantDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.RattledServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.RattledServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.RattledServantDamage.get());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setConfigurableAttributes();
    }

    public double getBaseRangeDamage(){
        return AttributesConfig.RattledServantRangeDamage.get();
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.RATTLED_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.RATTLED_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.RATTLED_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return ModSounds.RATTLED_STEP.get();
    }

    protected AbstractArrow getArrow(ItemStack pArrowStack, float pDistanceFactor) {
        AbstractArrow abstractarrowentity = super.getArrow(pArrowStack, pDistanceFactor);
        abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + this.getBaseRangeDamage());
        if (abstractarrowentity instanceof Arrow) {
            ((Arrow)abstractarrowentity).addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(3), 0));
        }

        return abstractarrowentity;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level instanceof ServerLevel serverLevel){
            if (this.tickCount % 5 == 0 && this.level.getRandom().nextBoolean()) {
                ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ModParticleTypes.BIG_ELECTRIC.get(), this);
            }
        }
    }

    protected float getDamageAfterMagicAbsorb(DamageSource p_34149_, float p_34150_) {
        p_34150_ = super.getDamageAfterMagicAbsorb(p_34149_, p_34150_);

        if (ModDamageSource.shockAttacks(p_34149_) || p_34149_.is(DamageTypeTags.IS_LIGHTNING)) {
            p_34150_ *= 0.5F;
        }

        return p_34150_;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return super.canBeAffected(pPotioneffect)
                && pPotioneffect.getEffect() != GoetyEffects.SPASMS.get();
    }

    public static boolean checkRattledSpawnRules(EntityType<Rattled> p_219113_, LevelAccessor p_219114_, MobSpawnType p_219115_, BlockPos p_219116_, RandomSource p_219117_) {
        if (p_219114_.getDifficulty() != Difficulty.PEACEFUL) {
            if (p_219114_ instanceof WorldGenLevel genLevel) {
                if (genLevel.canSeeSky(p_219116_)) {
                    if (genLevel.getLevel().isThundering()) {
                        return checkMonsterSpawnRules(p_219113_, genLevel, p_219115_, p_219116_, p_219117_);
                    }
                }
            }
        }

        return false;
    }
}
