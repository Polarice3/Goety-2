package com.Polarice3.Goety.utils;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ModDamageSource extends DamageSource {
    public static DamageSource SHOCK = new DamageSource(source("shock"));
    public static DamageSource BOILING = new DamageSource(source("boiling")).setIsFire();
    public static DamageSource PHOBIA = new DamageSource(source("phobia")).bypassArmor().setMagic();

    public ModDamageSource(String pMessageId) {
        super(pMessageId);
    }

    public static DamageSource directShock(LivingEntity pMob) {
        return new EntityDamageSource(source("directShock"), pMob);
    }

    public static DamageSource directFreeze(LivingEntity pMob) {
        return new EntityDamageSource(source("directFreeze"), pMob).bypassArmor();
    }

    public static DamageSource indirectFreeze(Entity pSource, @Nullable Entity pIndirectEntity) {
        return (new IndirectEntityDamageSource(source("indirectFreeze"), pSource, pIndirectEntity)).bypassArmor();
    }

    public static DamageSource modFireball(@Nullable Entity pIndirectEntity, Level world){
        LargeFireball fireball = new LargeFireball(EntityType.FIREBALL, world);
        return pIndirectEntity == null ? (new IndirectEntityDamageSource("onFire", fireball, fireball)).setIsFire().setProjectile() : (new IndirectEntityDamageSource("fireball", fireball, pIndirectEntity)).setIsFire().setProjectile();
    }

    public static DamageSource sword(Entity pSource, @Nullable Entity pIndirectEntity){
        return (new IndirectEntityDamageSource(source("sword"), pSource, pIndirectEntity)).setProjectile();
    }

    public static DamageSource iceBouquet(Entity pSource, @Nullable Entity pIndirectEntity){
        return new NoKnockBackDamageSource(source("iceBouquet"), pSource, pIndirectEntity).bypassArmor().setMagic();
    }

    public static DamageSource fireBreath(Entity pSource, @Nullable Entity pIndirectEntity){
        return new NoKnockBackDamageSource(source("fireBreath"), pSource, pIndirectEntity).setIsFire();
    }

    public static DamageSource magicBolt(Entity pSource, @Nullable Entity pIndirectEntity){
        return (new NoKnockBackDamageSource("indirectMagic", pSource, pIndirectEntity)).bypassArmor().setMagic();
    }

    public static DamageSource windBlast(Entity pSource, @Nullable Entity pIndirectEntity){
        return (new IndirectEntityDamageSource(source("windBlast"), pSource, pIndirectEntity)).bypassArmor().setMagic();
    }

    public static DamageSource modWitherSkull(Entity pSource, Entity pIndirectEntity) {
        return (new IndirectEntityDamageSource("witherSkull", pSource, pIndirectEntity)).setProjectile();
    }

    public static boolean shockAttacks(DamageSource source){
        return source.getMsgId().equals(source("directShock")) || source == ModDamageSource.SHOCK;
    }

    public static boolean freezeAttacks(DamageSource source){
        return source.getMsgId().equals(source("indirectFreeze")) || source.getMsgId().equals(source("directFreeze"))
        || source.getMsgId().equals(source("iceBouquet"));
    }

    public static boolean physicalAttacks(DamageSource source){
        return source.getDirectEntity() != null && source.getDirectEntity() instanceof LivingEntity
                && source instanceof EntityDamageSource
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

    public static DamageSource soulLeech(@Nullable Entity pSource, @Nullable Entity pIndirectEntity){
        return new NoKnockBackDamageSource(source("soulLeech"), pSource, pIndirectEntity).setMagic();
    }

    public static String source(String source){
        return "goety." + source;
    }

}
