package com.Polarice3.Goety.utils;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ModDamageSource extends DamageSource {

    public ModDamageSource(String pMessageId) {
        super(pMessageId);
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
        return new NoKnockBackDamageSource(source("iceBouquet"), pSource, pIndirectEntity).bypassArmor();
    }

    public static DamageSource windBlast(Entity pSource, @Nullable Entity pIndirectEntity){
        return (new IndirectEntityDamageSource("windBlast", pSource, pIndirectEntity)).bypassArmor().setMagic();
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

    public static DamageSource soulLeech(@Nullable Entity pSource, @Nullable Entity pIndirectEntity){
        return new NoKnockBackDamageSource(source("soulLeech"), pSource, pIndirectEntity).setMagic();
    }

    public static String source(String source){
        return "goety." + source;
    }

}
