package com.Polarice3.Goety.common.entities.ally.undead.zombie;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumSet;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FrozenZombieServant extends ZombieServant implements RangedAttackMob {
    public int throwCooldown;

    public FrozenZombieServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new ThrowSnowballGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.FrozenZombieServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FrozenZombieServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.FrozenZombieServantArmor.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.FrozenZombieServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.FrozenZombieServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.FrozenZombieServantDamage.get());
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("throwCooldown", this.throwCooldown);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.throwCooldown = pCompound.getInt("throwCooldown");
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.FROZEN_ZOMBIE_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.FROZEN_ZOMBIE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.FROZEN_ZOMBIE_DEATH.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.throwCooldown > 0){
            --this.throwCooldown;
        }
    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
        Snowball snowball = new Snowball(this.level, this);
        double d0 = p_33317_.getEyeY() - (double)1.1F;
        double d1 = p_33317_.getX() - this.getX();
        double d2 = d0 - snowball.getY();
        double d3 = p_33317_.getZ() - this.getZ();
        double d4 = Math.sqrt(d1 * d1 + d3 * d3) * (double)0.2F;
        snowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
        this.playSound(SoundEvents.SNOWBALL_THROW, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(snowball);
        this.throwCooldown = MathHelper.secondsToTicks(2);
    }

    @SubscribeEvent
    public static void FrozenAttack(LivingAttackEvent event){
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof FrozenZombieServant){
            victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, MathHelper.secondsToTicks(3)));
        }
    }

    static class ThrowSnowballGoal extends Goal{
        public FrozenZombieServant zombie;
        public int start;

        public ThrowSnowballGoal(FrozenZombieServant zombie){
            this.zombie = zombie;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity living = this.zombie.getTarget();
            if (living != null){
                return living.distanceTo(this.zombie) >= 6.0F
                        && living.distanceTo(this.zombie) <= 16.0F
                        && this.zombie.throwCooldown <= 0;
            }
            return false;
        }

        @Override
        public void start() {
            this.start = 10;
            if (this.zombie.getMainHandItem().isEmpty()){
                this.zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.SNOWBALL));
            } else if (this.zombie.getOffhandItem().isEmpty()){
                this.zombie.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SNOWBALL));
            }
            super.start();
        }

        @Override
        public void stop() {
            this.start = 0;
            if (this.zombie.getMainHandItem().is(Items.SNOWBALL)){
                this.zombie.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else if (this.zombie.getOffhandItem().is(Items.SNOWBALL)){
                this.zombie.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
            super.stop();
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity living = this.zombie.getTarget();
            if (living != null){
                if (this.start <= 0){
                    this.zombie.performRangedAttack(living, 0);
                } else {
                    --this.start;
                    this.zombie.lookControl.setLookAt(living);
                    this.zombie.navigation.stop();
                }
            }
        }
    }
}
