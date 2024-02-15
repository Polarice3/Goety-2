package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.ModLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class CryptSlime extends Slime {
    public CryptSlime(EntityType<? extends Slime> p_33588_, Level p_33589_) {
        super(p_33588_, p_33589_);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected ParticleOptions getParticleType() {
        return new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.BONE));
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return EntityType.SLIME.getDefaultLootTable();
    }

    protected void dropCustomDeathLoot(DamageSource p_33574_, int p_33575_, boolean p_33576_) {
        super.dropCustomDeathLoot(p_33574_, p_33575_, p_33576_);
        if (this.level.getServer() != null) {
            LootTable loottable = this.level.getServer().getLootData().getLootTable(ModLootTables.CRYPT_SLIME);
            LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) this.level())).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.DAMAGE_SOURCE, p_33574_).withOptionalParameter(LootContextParams.KILLER_ENTITY, p_33574_.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, p_33574_.getDirectEntity());
            if (this.lastHurtByPlayerTime > 0 && this.lastHurtByPlayer != null) {
                lootparams$builder = lootparams$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
            }

            LootParams lootparams = lootparams$builder.create(LootContextParamSets.ENTITY);
            loottable.getRandomItems(lootparams, this.getLootTableSeed(), this::spawnAtLocation);
        }
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean causeFallDamage(float p_149717_, float p_149718_, DamageSource p_149719_) {
        return false;
    }

    protected boolean isDealsDamage() {
        return this.isEffectiveAi();
    }

    protected void dealDamage(LivingEntity livingEntity) {
        if (this.isAlive()) {
            int i = this.getSize();
            if (this.distanceToSqr(livingEntity) < 0.6D * (double)i * 0.6D * (double)i && this.hasLineOfSight(livingEntity) && livingEntity.hurt(this.damageSources().mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.doEnchantDamageEffects(this, livingEntity);
                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 60, this.getSize()));
            }
        }
    }

    public static boolean checkMonsterSpawnRules(EntityType<? extends Slime> p_219014_, ServerLevelAccessor p_219015_, MobSpawnType p_219016_, BlockPos p_219017_, RandomSource p_219018_) {
        return p_219015_.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(p_219015_, p_219017_, p_219018_) && checkMobSpawnRules(p_219014_, p_219015_, p_219016_, p_219017_, p_219018_);
    }
}
