package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ModDamageSource extends DamageSource {
    public static ResourceKey<DamageType> SUMMON = create("summon");
    public static ResourceKey<DamageType> SHOCK = create("shock");
    public static ResourceKey<DamageType> DIRECT_SHOCK = create("direct_shock");
    public static ResourceKey<DamageType> INDIRECT_SHOCK = create("indirect_shock");
    public static ResourceKey<DamageType> LIGHTNING = create("lightning");
    public static ResourceKey<DamageType> DIRECT_FREEZE = create("direct_freeze");
    public static ResourceKey<DamageType> INDIRECT_FREEZE = create("indirect_freeze");
    public static ResourceKey<DamageType> ICE_SPIKE = create("ice_spike");
    public static ResourceKey<DamageType> DRENCH = create("drench");
    public static ResourceKey<DamageType> DIRECT_DRENCH = create("direct_drench");
    public static ResourceKey<DamageType> INDIRECT_DRENCH = create("indirect_drench");
    public static ResourceKey<DamageType> SWORD = create("sword");
    public static ResourceKey<DamageType> WIND_BLAST = create("wind_blast");
    public static ResourceKey<DamageType> ICE_BOUQUET = create("ice_bouquet");
    public static ResourceKey<DamageType> HELLFIRE = create("hellfire");
    public static ResourceKey<DamageType> INDIRECT_HELLFIRE = create("indirect_hellfire");
    public static ResourceKey<DamageType> MAGIC_FIRE = create("magic_fire");
    public static ResourceKey<DamageType> MAGIC_FIREBALL = create("magic_fireball");
    public static ResourceKey<DamageType> NO_OWNER_MAGIC_FIREBALL = create("no_owner_magic_fireball");
    public static ResourceKey<DamageType> LOOT_EXPLODE = create("loot_explode");
    public static ResourceKey<DamageType> LOOT_EXPLODE_OWNED = create("loot_explode_owned");
    public static ResourceKey<DamageType> FIRE_BREATH = create("fire_breath");
    public static ResourceKey<DamageType> FROST_BREATH = create("frost_breath");
    public static ResourceKey<DamageType> BUBBLE_STREAM = create("bubble_stream");
    public static ResourceKey<DamageType> MAGIC_BOLT = create("magic_bolt");
    public static ResourceKey<DamageType> SOUL_LEECH = create("soul_leech");
    public static ResourceKey<DamageType> LIFE_LEECH = create("life_leech");
    public static ResourceKey<DamageType> ACID = create("acid");
    public static ResourceKey<DamageType> SPIKE = create("spike");
    public static ResourceKey<DamageType> BOILING = create("boiling");
    public static ResourceKey<DamageType> PHOBIA = create("phobia");
    public static ResourceKey<DamageType> CHOKE = create("choke");
    public static ResourceKey<DamageType> SWARM = create("swarm");
    public static ResourceKey<DamageType> VOIDED = create("voided");
    public static ResourceKey<DamageType> DISMISSED = create("dismissed");
    public static ResourceKey<DamageType> DOOM = create("doom");
    public static ResourceKey<DamageType> DEATH = create("death");

    public ModDamageSource(Holder<DamageType> p_270906_, @Nullable Entity p_270796_, @Nullable Entity p_270459_, @Nullable Vec3 p_270623_) {
        super(p_270906_, p_270796_, p_270459_, p_270623_);
    }

    public static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Goety.location(name));
    }

    public static DamageSource getDamageSource(Level level, ResourceKey<DamageType> type, EntityType<?>... toIgnore) {
        return getEntityDamageSource(level, type, null, toIgnore);
    }

    public static DamageSource entityDamageSource(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker, EntityType<?>... toIgnore) {
        return getEntityDamageSource(level, type, attacker);
    }

    public static DamageSource getEntityDamageSource(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker, EntityType<?>... toIgnore) {
        return getIndirectEntityDamageSource(level, type, attacker, attacker, toIgnore);
    }

    public static DamageSource indirectEntityDamageSource(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker, @Nullable Entity indirectAttacker){
        return getIndirectEntityDamageSource(level, type, attacker, indirectAttacker);
    }

    /**
     * Based on @TeamTwilight's codes: <a href="https://github.com/TeamTwilight/twilightforest/blob/1.20.x/src/main/java/twilightforest/init/TFDamageTypes.java#L14">...</a>
     */
    public static DamageSource getIndirectEntityDamageSource(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker, @Nullable Entity indirectAttacker, EntityType<?>... toIgnore) {
        return toIgnore.length > 0 ? new EntityExcludedDamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), toIgnore) : source(level, type, attacker, indirectAttacker);
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker, @Nullable Entity indirectAttacker){
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), attacker, indirectAttacker);
    }

    public static DamageSource noKnockbackDamageSource(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker, @Nullable Entity indirectAttacker, EntityType<?>... toIgnore) {
        return new NoKnockBackDamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), attacker, indirectAttacker);
    }

    public static DamageSource ownedDamageSource(Level level, ResourceKey<DamageType> type, Entity attacker, LivingEntity owned) {
        return new OwnedDamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), attacker, owned);
    }

    public static DamageSource summonAttack(LivingEntity owned, LivingEntity owner) {
        return ownedDamageSource(owned.level, SUMMON, owned, owner);
    }

    public static DamageSource directShock(LivingEntity pMob) {
        return ModDamageSource.entityDamageSource(pMob.level, DIRECT_SHOCK, pMob);
    }

    public static DamageSource indirectShock(Entity pSource, @Nullable Entity pIndirectEntity) {
        return ModDamageSource.indirectEntityDamageSource(pSource.level, INDIRECT_SHOCK, pSource, pIndirectEntity);
    }

    public static DamageSource lightning(Entity pSource, @Nullable Entity pIndirectEntity) {
        return ModDamageSource.indirectEntityDamageSource(pSource.level, LIGHTNING, pSource, pIndirectEntity);
    }

    public static DamageSource directFreeze(LivingEntity pMob) {
        return ModDamageSource.entityDamageSource(pMob.level, DIRECT_FREEZE, pMob);
    }

    public static DamageSource indirectFreeze(Entity pSource, @Nullable Entity pIndirectEntity) {
        return ModDamageSource.indirectEntityDamageSource(pSource.level, INDIRECT_FREEZE, pSource, pIndirectEntity);
    }

    public static DamageSource iceSpike(Entity pSource, @Nullable Entity pIndirectEntity) {
        return ModDamageSource.indirectEntityDamageSource(pSource.level, ICE_SPIKE, pSource, pIndirectEntity);
    }

    public static DamageSource directDrench(LivingEntity pMob) {
        return ModDamageSource.entityDamageSource(pMob.level, DIRECT_DRENCH, pMob);
    }

    public static DamageSource indirectDrench(Entity pSource, @Nullable Entity pIndirectEntity) {
        return ModDamageSource.indirectEntityDamageSource(pSource.level, INDIRECT_DRENCH, pSource, pIndirectEntity);
    }

    public static DamageSource modFireball(@Nullable Entity pIndirectEntity, Level world){
        LargeFireball fireball = new LargeFireball(EntityType.FIREBALL, world);
        return pIndirectEntity == null ? indirectEntityDamageSource(world, DamageTypes.FIREBALL, fireball, fireball) : indirectEntityDamageSource(world, DamageTypes.FIREBALL, fireball, pIndirectEntity);
    }

    public static DamageSource magicFireball(Fireball p_270147_, @Nullable Entity pIndirectEntity, Level world) {
        return pIndirectEntity == null ? indirectEntityDamageSource(world, NO_OWNER_MAGIC_FIREBALL, p_270147_, null) : indirectEntityDamageSource(world, MAGIC_FIREBALL, p_270147_, pIndirectEntity);
    }

    public static DamageSource lootExplosion(@Nullable Entity pSource, @Nullable Entity pIndirectEntity, Level world) {
        return source(world, pIndirectEntity != null && pSource != null ? LOOT_EXPLODE_OWNED : LOOT_EXPLODE, pSource, pIndirectEntity);
    }

    public static DamageSource sword(Entity pSource, @Nullable Entity pIndirectEntity){
        return indirectEntityDamageSource(pSource.level, SWORD, pSource, pIndirectEntity);
    }

    public static DamageSource iceBouquet(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, ICE_BOUQUET, pSource, pIndirectEntity);
    }

    public static DamageSource hellfire(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, HELLFIRE, pSource, pIndirectEntity);
    }

    public static DamageSource fireBreath(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, FIRE_BREATH, pSource, pIndirectEntity);
    }

    public static DamageSource magicFireBreath(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, MAGIC_FIRE, pSource, pIndirectEntity);
    }

    public static DamageSource frostBreath(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, FROST_BREATH, pSource, pIndirectEntity);
    }

    public static DamageSource bubbleStream(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, BUBBLE_STREAM, pSource, pIndirectEntity);
    }

    public static DamageSource magicBolt(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, MAGIC_BOLT, pSource, pIndirectEntity);
    }

    public static DamageSource acid(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, ACID, pSource, pIndirectEntity);
    }

    public static DamageSource spike(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, SPIKE, pSource, pIndirectEntity);
    }

    public static DamageSource windBlast(Entity pSource, @Nullable Entity pIndirectEntity){
        return indirectEntityDamageSource(pSource.level, WIND_BLAST, pSource, pIndirectEntity);
    }

    public static DamageSource deathCurse(Entity pSource){
        return noKnockbackDamageSource(pSource.level, DEATH, pSource, pSource);
    }

    public static boolean hellfireAttacks(DamageSource source){
        return source != null && source.is(ModTags.DamageTypes.HELLFIRE);
    }

    public static boolean isMagicFire(DamageSource source){
        return source != null && (source.is(ModTags.DamageTypes.MAGIC_FIRE));
    }

    public static boolean shockAttacks(DamageSource source){
        return source.getMsgId().equals(source("shock"))
                || source.getMsgId().equals(source("directShock"))
                || source.getMsgId().equals(source("indirectShock"))
                || source.is(ModTags.DamageTypes.SHOCK_ATTACKS);
    }

    public static boolean freezeAttacks(DamageSource source){
        return source.getMsgId().equals(source("indirectFreeze"))
                || source.getMsgId().equals(source("directFreeze"))
                || source.getMsgId().equals(source("iceBouquet"))
                || source.getMsgId().equals(source("frostBreath"))
                || source.is(ModTags.DamageTypes.FROST_ATTACKS);
    }

    public static boolean waterAttacks(DamageSource source){
        return source.getMsgId().equals(source("drench"))
                || source.getMsgId().equals(source("directDrench"))
                || source.getMsgId().equals(source("indirectDrench"))
                || source.getMsgId().equals(source("bubbleStream"))
                || source.is(ModTags.DamageTypes.WATER_ATTACKS);
    }

    public static boolean physicalAttacks(DamageSource source){
        return source.getDirectEntity() != null && source.getDirectEntity() instanceof LivingEntity
                && (source.getMsgId().equals("mob")
                || source.getMsgId().equals("sting")
                || source.getMsgId().equals("player")
                || source.getMsgId().equals(source("summon"))
                || source.is(ModTags.DamageTypes.PHYSICAL));
    }

    public static boolean toolAttack(DamageSource source, Predicate<Item> item){
        if (physicalAttacks(source)) {
            if (source.getDirectEntity() instanceof LivingEntity living) {
                return item.test(living.getMainHandItem().getItem());
            }
        }
        return false;
    }

    public static DamageSource soulLeech(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, SOUL_LEECH, pSource, pIndirectEntity);
    }

    public static DamageSource lifeLeech(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, LIFE_LEECH, pSource, pIndirectEntity);
    }

    public static DamageSource choke(Entity pSource, @Nullable Entity pIndirectEntity) {
        return noKnockbackDamageSource(pSource.level, CHOKE, pSource, pIndirectEntity);
    }

    public static DamageSource swarm(Entity pSource, @Nullable Entity pIndirectEntity) {
        return noKnockbackDamageSource(pSource.level, SWARM, pSource, pIndirectEntity);
    }

    public static boolean wantingAttacks(DamageSource source){
        return source.is(ModTags.DamageTypes.WANTING_DAMAGE);
    }

    public static String source(String source){
        return "goety." + source;
    }

    public static class EntityExcludedDamageSource extends DamageSource {

        protected final List<EntityType<?>> entities;

        public EntityExcludedDamageSource(Holder<DamageType> type, EntityType<?>... entities) {
            super(type);
            this.entities = Arrays.stream(entities).toList();
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity living) {
            LivingEntity livingentity = living.getKillCredit();
            String s = "death.attack." + this.type().msgId();
            String s1 = s + ".player";
            if (livingentity != null) {
                for (EntityType<?> entity : entities) {
                    if (livingentity.getType() == entity) {
                        return Component.translatable(s, living.getDisplayName());
                    }
                }
            }
            return livingentity != null ? Component.translatable(s1, living.getDisplayName(), livingentity.getDisplayName()) : Component.translatable(s, living.getDisplayName());
        }
    }

    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(SUMMON, new DamageType("goety.summon", 0.1F));
        context.register(SHOCK, new DamageType("goety.shock", 0.0F));
        context.register(DIRECT_SHOCK, new DamageType("goety.directShock", 0.0F));
        context.register(INDIRECT_SHOCK, new DamageType("goety.indirectShock", 0.0F));
        context.register(LIGHTNING, new DamageType("goety.lightning", 0.0F));
        context.register(DIRECT_FREEZE, new DamageType("goety.directFreeze", 0.0F, DamageEffects.FREEZING));
        context.register(INDIRECT_FREEZE, new DamageType("goety.indirectFreeze", 0.0F, DamageEffects.FREEZING));
        context.register(ICE_SPIKE, new DamageType("goety.indirectFreeze", 0.0F, DamageEffects.FREEZING));
        context.register(DRENCH, new DamageType("goety.drench", 0.0F, DamageEffects.DROWNING));
        context.register(DIRECT_DRENCH, new DamageType("goety.directDrench", 0.0F, DamageEffects.DROWNING));
        context.register(INDIRECT_DRENCH, new DamageType("goety.indirectDrench", 0.0F, DamageEffects.DROWNING));
        context.register(SWORD, new DamageType("goety.sword", 0.0F));
        context.register(WIND_BLAST, new DamageType("goety.windBlast", 0.0F));
        context.register(ICE_BOUQUET, new DamageType("goety.iceBouquet", 0.0F, DamageEffects.FREEZING));
        context.register(HELLFIRE, new DamageType("goety.hellfire", 0.0F, DamageEffects.BURNING));
        context.register(INDIRECT_HELLFIRE, new DamageType("goety.indirectHellfire", 0.0F, DamageEffects.BURNING));
        context.register(MAGIC_FIREBALL, new DamageType("fireball", 0.1F, DamageEffects.BURNING));
        context.register(NO_OWNER_MAGIC_FIREBALL, new DamageType("onFire", 0.1F, DamageEffects.BURNING));
        context.register(LOOT_EXPLODE, new DamageType("explosion", DamageScaling.ALWAYS, 0.1F));
        context.register(LOOT_EXPLODE_OWNED, new DamageType("explosion.player", DamageScaling.ALWAYS, 0.1F));
        context.register(FIRE_BREATH, new DamageType("goety.fireBreath", 0.0F, DamageEffects.BURNING));
        context.register(MAGIC_FIRE, new DamageType("goety.fireBreath", 0.0F, DamageEffects.BURNING));
        context.register(FROST_BREATH, new DamageType("goety.frostBreath", 0.0F, DamageEffects.FREEZING));
        context.register(BUBBLE_STREAM, new DamageType("goety.bubbleStream", 0.0F, DamageEffects.DROWNING));
        context.register(MAGIC_BOLT, new DamageType("indirectMagic", 0.0F));
        context.register(ACID, new DamageType("goety.acid", 0.0F));
        context.register(SOUL_LEECH, new DamageType("goety.soulLeech", 0.0F));
        context.register(LIFE_LEECH, new DamageType("goety.lifeLeech", 0.0F));
        context.register(SPIKE, new DamageType("goety.spike", 0.0F, DamageEffects.POKING));
        context.register(BOILING, new DamageType("goety.boiling", 0.0F, DamageEffects.BURNING));
        context.register(PHOBIA, new DamageType("goety.phobia", 0.0F));
        context.register(CHOKE, new DamageType("goety.choke", 0.0F));
        context.register(SWARM, new DamageType("goety.swarm", 0.0F));
        context.register(VOIDED, new DamageType("goety.voided", 0.0F));
        context.register(DISMISSED, new DamageType("goety.dismissed", 0.0F));
        context.register(DOOM, new DamageType("goety.doom", 0.0F));
        context.register(DEATH, new DamageType("goety.death", 0.0F));
    }

}
