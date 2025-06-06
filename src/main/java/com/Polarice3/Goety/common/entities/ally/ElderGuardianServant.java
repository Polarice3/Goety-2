package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ElderGuardianServant extends GuardianServant{

    public ElderGuardianServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPersistenceRequired();
        if (this.randomStrollGoal != null) {
            this.randomStrollGoal.setInterval(400);
        }
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return GuardianServant.setCustomAttributes()
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MAX_HEALTH, 80.0D);
    }

    public int getAttackDuration() {
        return 60;
    }

    protected SoundEvent getAmbientSound() {
        return this.isInWaterOrBubble() ? SoundEvents.ELDER_GUARDIAN_AMBIENT : SoundEvents.ELDER_GUARDIAN_AMBIENT_LAND;
    }

    protected SoundEvent getHurtSound(DamageSource p_32468_) {
        return this.isInWaterOrBubble() ? SoundEvents.ELDER_GUARDIAN_HURT : SoundEvents.ELDER_GUARDIAN_HURT_LAND;
    }

    protected SoundEvent getDeathSound() {
        return this.isInWaterOrBubble() ? SoundEvents.ELDER_GUARDIAN_DEATH : SoundEvents.ELDER_GUARDIAN_DEATH_LAND;
    }

    protected SoundEvent getFlopSound() {
        return SoundEvents.ELDER_GUARDIAN_FLOP;
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if ((this.tickCount + this.getId()) % 1200 == 0) {
            MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 6000, 2);
            List<ServerPlayer> list = this.addEffectToPlayersAround((ServerLevel)this.level(), this.position(), 50.0D, mobeffectinstance, 1200);
            list.forEach((serverPlayer) -> serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, this.isSilent() ? 0.0F : 1.0F)));
        }
    }

    public List<ServerPlayer> addEffectToPlayersAround(ServerLevel p_216947_, Vec3 p_216949_, double p_216950_, MobEffectInstance p_216951_, int p_216952_) {
        MobEffect mobeffect = p_216951_.getEffect();
        List<ServerPlayer> list = p_216947_.getPlayers((serverPlayer) -> {
            return serverPlayer.gameMode.isSurvival() && !MobUtil.areAllies(this, serverPlayer) && p_216949_.closerThan(serverPlayer.position(), p_216950_) && (!serverPlayer.hasEffect(mobeffect) || serverPlayer.getEffect(mobeffect).getAmplifier() < p_216951_.getAmplifier() || serverPlayer.getEffect(mobeffect).endsWithin(p_216952_ - 1));
        });
        list.forEach((serverPlayer) -> serverPlayer.addEffect(new MobEffectInstance(p_216951_), this));
        return list;
    }
}
