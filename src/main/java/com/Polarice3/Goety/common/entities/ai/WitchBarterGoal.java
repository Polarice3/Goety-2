package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.common.entities.hostile.cultists.Warlock;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.ModLootTables;
import com.Polarice3.Goety.utils.WitchBarterHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class WitchBarterGoal extends Goal {
    private int progress = 100;
    public Raider witch;

    public WitchBarterGoal(Raider witch) {
        this.witch = witch;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
    }


    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void tick() {
        this.witch.setTarget(null);
        LivingEntity trader = WitchBarterHelper.getTrader(witch);
        if (--this.progress > 0) {
            this.witch.getNavigation().stop();
            if (trader != null && this.witch.distanceTo(trader) <= 16.0F) {
                this.witch.getLookControl().setLookAt(trader);
            }
        }
        if (this.progress <= 0) {
            Vec3 vec3 = trader != null ? trader.position() : this.witch.position();
            if (!this.witch.level.isClientSide) {
                if (this.witch.level.getServer() != null) {
                    LootTable loottable = this.witch.level.getServer().getLootTables().get(ModLootTables.WITCH_BARTER);
                    if (this.witch instanceof Warlock){
                        loottable = this.witch.level.getServer().getLootTables().get(ModLootTables.WARLOCK_BARTER);
                    }
                    List<ItemStack> list = loottable.getRandomItems((new LootContext.Builder((ServerLevel) this.witch.level)).withParameter(LootContextParams.THIS_ENTITY, this.witch).withRandom(this.witch.level.random).create(LootContextParamSets.PIGLIN_BARTER));
                    for(ItemStack itemstack : list) {
                        BehaviorUtils.throwItem(this.witch, itemstack, vec3.add(0.0D, 1.0D, 0.0D));
                    }
                }
            }
            this.clearTrade();
        }

        if (this.witch.hurtTime != 0){
            if (this.witch.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.Items.WITCH_CURRENCY)) {
                this.witch.spawnAtLocation(this.witch.getItemInHand(InteractionHand.MAIN_HAND));
                this.clearTrade();
            }
        }
    }

    protected void addParticlesAroundSelf(ParticleOptions p_35288_) {
        if (!this.witch.level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.witch.level;
            for (int i = 0; i < 5; ++i) {
                double d0 = this.witch.getRandom().nextGaussian() * 0.02D;
                double d1 = this.witch.getRandom().nextGaussian() * 0.02D;
                double d2 = this.witch.getRandom().nextGaussian() * 0.02D;
                serverLevel.sendParticles(p_35288_, this.witch.getRandomX(1.0D), this.witch.getRandomY() + 1.0D, this.witch.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
            }
        }

    }

    @Override
    public boolean canUse() {
        return this.witch.getMainHandItem().is(ModTags.Items.WITCH_CURRENCY);
    }

    @Override
    public void start(){
        super.start();
        this.progress = 100;
        WitchBarterHelper.setTimer(this.witch, 5);
        this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
    }

    public void clearTrade(){
        this.witch.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        WitchBarterHelper.setTimer(this.witch, 5);
        WitchBarterHelper.setTrader(this.witch, null);
    }
}
