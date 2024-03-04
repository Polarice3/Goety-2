package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

public class SurveyEye extends FlyingMob {
    private Player owner;
    public UUID playerId;

    public SurveyEye(EntityType<? extends FlyingMob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
        this.noPhysics = true;
        this.getNavigation().setCanFloat(true);
    }

    public void setOwner(Player owner) {
        this.owner = owner;
        if (owner != null) {
            this.playerId = owner.getUUID();
        }
    }

    @Nullable
    public Player getPlayer(){
        if (this.owner == null && this.playerId != null) {
            if (this.level instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(this.playerId);
                if (entity instanceof Player) {
                    this.owner = (Player) entity;
                }
            } else if (this.level.isClientSide) {
                this.owner = this.level.getPlayerByUUID(this.playerId);
            }
        }
        return this.owner;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    protected int decreaseAirSupply(int p_21303_) {
        return p_21303_;
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        return InteractionResult.PASS;
    }

    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    public void tick(){
        super.tick();
        if (this.getPlayer() == null){
            this.discard();
        } else {
            if (!SEHelper.hasCamera(this.getPlayer())){
                this.discard();
            } else {
                this.lookAt(this.getPlayer(), 90.0F, 90.0F);
            }
        }
    }
}
