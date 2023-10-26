package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ModDamageSource extends DamageSource {
    public static ResourceKey<DamageType> SHOCK = create("shock");
    public static ResourceKey<DamageType> DIRECT_SHOCK = create("direct_shock");
    public static ResourceKey<DamageType> DIRECT_FREEZE = create("direct_freeze");
    public static ResourceKey<DamageType> INDIRECT_FREEZE = create("indirect_freeze");
    public static ResourceKey<DamageType> ICE_SPIKE = create("ice_spike");
    public static ResourceKey<DamageType> SWORD = create("sword");
    public static ResourceKey<DamageType> WIND_BLAST = create("wind_blast");
    public static ResourceKey<DamageType> ICE_BOUQUET = create("ice_bouquet");
    public static ResourceKey<DamageType> FIRE_BREATH = create("fire_breath");
    public static ResourceKey<DamageType> MAGIC_BOLT = create("magic_bolt");
    public static ResourceKey<DamageType> SOUL_LEECH = create("soul_leech");
    public static ResourceKey<DamageType> SPIKE = create("spike");
    public static ResourceKey<DamageType> BOILING = create("boiling");
    public static ResourceKey<DamageType> PHOBIA = create("phobia");

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
        return toIgnore.length > 0 ? new EntityExcludedDamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), toIgnore) : new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), attacker, indirectAttacker);
    }

    public static DamageSource noKnockbackDamageSource(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker, @Nullable Entity indirectAttacker, EntityType<?>... toIgnore) {
        return new NoKnockBackDamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), attacker, indirectAttacker);
    }

    public static DamageSource directShock(LivingEntity pMob) {
        return ModDamageSource.entityDamageSource(pMob.level, DIRECT_SHOCK, pMob);
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

    public static DamageSource modFireball(@Nullable Entity pIndirectEntity, Level world){
        LargeFireball fireball = new LargeFireball(EntityType.FIREBALL, world);
        return pIndirectEntity == null ? indirectEntityDamageSource(world, DamageTypes.FIREBALL, fireball, fireball) : indirectEntityDamageSource(world, DamageTypes.FIREBALL, fireball, pIndirectEntity);
    }

    public static DamageSource sword(Entity pSource, @Nullable Entity pIndirectEntity){
        return indirectEntityDamageSource(pSource.level, SWORD, pSource, pIndirectEntity);
    }

    public static DamageSource iceBouquet(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, ICE_BOUQUET, pSource, pIndirectEntity);
    }

    public static DamageSource fireBreath(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, FIRE_BREATH, pSource, pIndirectEntity);
    }

    public static DamageSource magicBolt(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, MAGIC_BOLT, pSource, pIndirectEntity);
    }

    public static DamageSource spike(Entity pSource, @Nullable Entity pIndirectEntity){
        return noKnockbackDamageSource(pSource.level, SPIKE, pSource, pIndirectEntity);
    }

    public static DamageSource windBlast(Entity pSource, @Nullable Entity pIndirectEntity){
        return indirectEntityDamageSource(pSource.level, WIND_BLAST, pSource, pIndirectEntity);
    }

    public static boolean shockAttacks(DamageSource source){
        return source.getMsgId().equals(source("directShock")) || source.is(ModDamageSource.DIRECT_SHOCK);
    }

    public static boolean freezeAttacks(DamageSource source){
        return source.getMsgId().equals(source("indirectFreeze")) || source.getMsgId().equals(source("directFreeze"))
        || source.getMsgId().equals(source("iceBouquet"));
    }

    public static boolean physicalAttacks(DamageSource source){
        return source.getDirectEntity() != null && source.getDirectEntity() instanceof LivingEntity
                && (source.getMsgId().equals("mob") || source.getMsgId().equals("player"));
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
        context.register(SHOCK, new DamageType("goety.shock", 0.0F));
        context.register(DIRECT_SHOCK, new DamageType("goety.directShock", 0.0F));
        context.register(DIRECT_FREEZE, new DamageType("goety.directFreeze", 0.0F, DamageEffects.FREEZING));
        context.register(INDIRECT_FREEZE, new DamageType("goety.indirectFreeze", 0.0F, DamageEffects.FREEZING));
        context.register(ICE_SPIKE, new DamageType("goety.indirectFreeze", 0.0F, DamageEffects.FREEZING));
        context.register(SWORD, new DamageType("goety.sword", 0.0F));
        context.register(WIND_BLAST, new DamageType("goety.windBlast", 0.0F));
        context.register(ICE_BOUQUET, new DamageType("goety.iceBouquet", 0.0F, DamageEffects.FREEZING));
        context.register(FIRE_BREATH, new DamageType("goety.fireBreath", 0.0F, DamageEffects.BURNING));
        context.register(MAGIC_BOLT, new DamageType("indirectMagic", 0.0F));
        context.register(SOUL_LEECH, new DamageType("goety.soulLeech", 0.0F));
        context.register(SPIKE, new DamageType("goety.spike", 0.0F, DamageEffects.POKING));
        context.register(BOILING, new DamageType("goety.boiling", 0.0F, DamageEffects.BURNING));
        context.register(PHOBIA, new DamageType("goety.phobia", 0.0F));
    }

}
