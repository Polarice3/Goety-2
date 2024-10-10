package com.Polarice3.Goety.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ModDamageSource extends DamageSource {
    public static DamageSource SHOCK = new DamageSource(source("shock"));
    public static DamageSource HELLFIRE = new DamageSource(source("hellfire")).bypassArmor();
    public static DamageSource BOILING = new DamageSource(source("boiling")).setIsFire();
    public static DamageSource PHOBIA = new DamageSource(source("phobia")).bypassArmor().setMagic();
    public static DamageSource DOOM = new DamageSource(source("doom")).bypassArmor().bypassEnchantments().setMagic();

    public ModDamageSource(String pMessageId) {
        super(pMessageId);
    }

    public static DamageSource summonAttack(LivingEntity owned, LivingEntity owner) {
        return new OwnedDamageSource(source("summon"), owned, owner);
    }

    public static DamageSource directShock(LivingEntity pMob) {
        return new EntityDamageSource(source("directShock"), pMob);
    }

    public static DamageSource indirectShock(Entity pSource, @Nullable Entity pIndirectEntity) {
        return (new IndirectEntityDamageSource(source("indirectShock"), pSource, pIndirectEntity));
    }

    public static DamageSource directFreeze(LivingEntity pMob) {
        return new EntityDamageSource(source("directFreeze"), pMob);
    }

    public static DamageSource indirectFreeze(Entity pSource, @Nullable Entity pIndirectEntity) {
        return (new IndirectEntityDamageSource(source("indirectFreeze"), pSource, pIndirectEntity));
    }

    public static DamageSource modFireball(@Nullable Entity pIndirectEntity, Level world){
        LargeFireball fireball = new LargeFireball(EntityType.FIREBALL, world);
        return pIndirectEntity == null ? (new IndirectEntityDamageSource("onFire", fireball, fireball)).setIsFire().setProjectile() : (new IndirectEntityDamageSource("fireball", fireball, pIndirectEntity)).setIsFire().setProjectile();
    }

    public static DamageSource magicFireball(Fireball fireball, @Nullable Entity pIndirectEntity){
        return pIndirectEntity == null ? (new MagicFireballDamageSource("goety.noOwnerMagicFireball", fireball, fireball)).setProjectile() : (new MagicFireballDamageSource("goety.magicFireball", fireball, pIndirectEntity)).setProjectile();
    }

    public static DamageSource sword(Entity pSource, @Nullable Entity pIndirectEntity){
        return (new IndirectEntityDamageSource(source("sword"), pSource, pIndirectEntity)).setProjectile();
    }

    public static DamageSource iceBouquet(Entity pSource, @Nullable Entity pIndirectEntity){
        return new NoKnockBackDamageSource(source("iceBouquet"), pSource, pIndirectEntity).setMagic();
    }

    public static DamageSource hellfire(Entity pSource, @Nullable Entity pIndirectEntity){
        return new NoKnockBackDamageSource(source("indirectHellfire"), pSource, pIndirectEntity).bypassArmor();
    }

    public static DamageSource fireBreath(Entity pSource, @Nullable Entity pIndirectEntity){
        return new NoKnockBackDamageSource(source("fireBreath"), pSource, pIndirectEntity).setIsFire();
    }

    public static DamageSource magicFireBreath(Entity pSource, @Nullable Entity pIndirectEntity){
        return new NoKnockBackDamageSource(source("magicFireBreath"), source("fireBreath"), pSource, pIndirectEntity);
    }

    public static DamageSource frostBreath(Entity pSource, @Nullable Entity pIndirectEntity){
        return new NoKnockBackDamageSource(source("frostBreath"), pSource, pIndirectEntity);
    }

    public static DamageSource magicBolt(Entity pSource, @Nullable Entity pIndirectEntity){
        return (new NoKnockBackDamageSource("indirectMagic", pSource, pIndirectEntity)).bypassArmor().setMagic();
    }

    public static DamageSource windBlast(Entity pSource, @Nullable Entity pIndirectEntity){
        return (new IndirectEntityDamageSource(source("windBlast"), pSource, pIndirectEntity)).setMagic();
    }

    public static DamageSource choke(Entity pSource, @Nullable Entity pIndirectEntity){
        return new NoKnockBackDamageSource(source("choke"), pSource, pIndirectEntity).bypassArmor();
    }

    public static DamageSource swarm(Entity pSource, @Nullable Entity pIndirectEntity){
        return new NoKnockBackDamageSource(source("swarm"), pSource, pIndirectEntity);
    }

    public static DamageSource spike(Entity pSource, @Nullable Entity pIndirectEntity){
        return new NoKnockBackDamageSource(source("spike"), pSource, pIndirectEntity).setMagic();
    }

    public static boolean hellfireAttacks(DamageSource source){
        return source != null && (source.getMsgId().equals(source("indirectHellfire"))
                || source == ModDamageSource.HELLFIRE);
    }

    public static boolean isMagicFire(DamageSource source){
        return source instanceof MagicFireballDamageSource
                || (source != null && source.getMsgId().equals(source("magicFireBreath")));
    }

    public static boolean shockAttacks(DamageSource source){
        return source.getMsgId().equals(source("directShock"))
                || source.getMsgId().equals(source("indirectShock"))
                || source == ModDamageSource.SHOCK;
    }

    public static boolean freezeAttacks(DamageSource source){
        return source.getMsgId().equals(source("indirectFreeze")) || source.getMsgId().equals(source("directFreeze"))
        || source.getMsgId().equals(source("iceBouquet")) || source.getMsgId().equals(source("frostBreath"));
    }

    public static boolean physicalAttacks(DamageSource source){
        return source.getDirectEntity() != null && source.getDirectEntity() instanceof LivingEntity
                && source instanceof EntityDamageSource
                && (source.getMsgId().equals("mob")
                || source.getMsgId().equals("sting")
                || source.getMsgId().equals("player")
                || source.getMsgId().equals(source("summon")));
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

    public static class MagicFireballDamageSource extends IndirectEntityDamageSource {
        @Nullable
        private final Entity owner;

        public MagicFireballDamageSource(String p_19406_, Entity p_19407_, @Nullable Entity p_19408_) {
            super(p_19406_, p_19407_, p_19408_);
            this.owner = p_19408_;
        }

        public @NotNull Component getLocalizedDeathMessage(@NotNull LivingEntity p_19410_) {
            Component component = this.owner == null ? this.entity.getDisplayName() : this.owner.getDisplayName();
            String s0 = this.owner == null ? "onFire" : "fireball";
            ItemStack itemstack = this.owner instanceof LivingEntity ? ((LivingEntity)this.owner).getMainHandItem() : ItemStack.EMPTY;
            String s = "death.attack." + s0;
            String s1 = s + ".item";
            return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? Component.translatable(s1, p_19410_.getDisplayName(), component, itemstack.getDisplayName()) : Component.translatable(s, p_19410_.getDisplayName(), component);
        }
    }

}
