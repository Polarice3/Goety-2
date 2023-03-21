package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.utils.ModLootTables;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class WitchBarterGoal extends Goal {
    private int progress = 100;
    public Witch witch;
    @Nullable
    public static LivingEntity trader;

    public WitchBarterGoal(Witch witch) {
        this.witch = witch;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
    }

    public static void setTrader(@Nullable LivingEntity livingEntity){
        trader = livingEntity;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void tick() {
        this.witch.setTarget(null);
        if (--this.progress > 0) {
            this.witch.getNavigation().stop();
        }
        if (this.progress <= 0) {
            Vec3 vec3 = trader != null ? trader.position() : this.witch.position();
            if (!this.witch.level.isClientSide) {
                if (this.witch.level.getServer() != null) {
                    LootTable loottable = this.witch.level.getServer().getLootTables().get(ModLootTables.WITCH_BARTER);
                    List<ItemStack> list = loottable.getRandomItems((new LootContext.Builder((ServerLevel) this.witch.level)).withParameter(LootContextParams.THIS_ENTITY, this.witch).withRandom(this.witch.level.random).create(LootContextParamSets.PIGLIN_BARTER));
                    for(ItemStack itemstack : list) {
                        BehaviorUtils.throwItem(this.witch, itemstack, vec3.add(0.0D, 1.0D, 0.0D));
                    }
                }
            }
            this.witch.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
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
        return this.witch.getMainHandItem().getItem() == Items.EMERALD;
    }

    @Override
    public void start(){
        super.start();
        this.progress = 100;
        this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
    }
}
