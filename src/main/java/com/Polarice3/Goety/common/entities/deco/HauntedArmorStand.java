package com.Polarice3.Goety.common.entities.deco;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.HauntedArmorServant;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.magic.AnimationCore;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class HauntedArmorStand extends ArmorStand {
    public HauntedArmorStand(EntityType<? extends ArmorStand> p_31553_, Level p_31554_) {
        super(p_31553_, p_31554_);
    }

    public HauntedArmorStand(Level p_31556_, double p_31557_, double p_31558_, double p_31559_) {
        super(p_31556_, p_31557_, p_31558_, p_31559_);
    }

    public boolean isShowArms() {
        return true;
    }

    public InteractionResult interactAt(Player playerIn, Vec3 p_31595_, InteractionHand p_31596_) {
        ItemStack itemStack = playerIn.getItemInHand(p_31596_);
        if (itemStack.getItem() instanceof AnimationCore){
            if (SEHelper.hasResearch(playerIn, ResearchList.HAUNTING)) {
                if (ItemHelper.isFullEquipped(this)) {
                    HauntedArmorServant hauntedArmorServant = new HauntedArmorServant(ModEntityType.HAUNTED_ARMOR_SERVANT.get(), playerIn.level);
                    for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                        hauntedArmorServant.setItemSlot(equipmentSlot, this.getItemBySlot(equipmentSlot));
                        hauntedArmorServant.setGuaranteedDrop(equipmentSlot);
                    }
                    hauntedArmorServant.setTrueOwner(playerIn);
                    hauntedArmorServant.moveTo(this.blockPosition(), this.getYRot(), this.getXRot());
                    hauntedArmorServant.setLeftHanded(playerIn.getMainArm() == HumanoidArm.LEFT);
                    if (playerIn.level.addFreshEntity(hauntedArmorServant)) {
                        this.playSound(ModSounds.SUMMON_SPELL.get());
                        this.showBreakingParticles();
                        itemStack.shrink(1);
                        this.discard();
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.interactAt(playerIn, p_31595_, p_31596_);
    }

    public boolean hurt(DamageSource p_31579_, float p_31580_) {
        if (!this.level.isClientSide && !this.isRemoved()) {
            if (DamageSource.OUT_OF_WORLD.equals(p_31579_)) {
                this.kill();
                return false;
            } else if (!this.isInvulnerableTo(p_31579_) && !this.isInvisible() && !this.isMarker()) {
                if (p_31579_.isExplosion()) {
                    this.brokenByAnything(p_31579_);
                    this.kill();
                    return false;
                } else {
                    boolean flag = p_31579_.getDirectEntity() instanceof AbstractArrow;
                    boolean flag1 = flag && ((AbstractArrow)p_31579_.getDirectEntity()).getPierceLevel() > 0;
                    boolean flag2 = "player".equals(p_31579_.getMsgId());
                    if (!flag2 && !flag) {
                        return false;
                    } else if (p_31579_.getEntity() instanceof Player && !((Player)p_31579_.getEntity()).getAbilities().mayBuild) {
                        return false;
                    } else if (p_31579_.isCreativePlayer()) {
                        this.playBrokenSound();
                        this.showBreakingParticles();
                        this.kill();
                        return flag1;
                    } else {
                        long i = this.level.getGameTime();
                        if (i - this.lastHit > 5L && !flag) {
                            this.level.broadcastEntityEvent(this, (byte)32);
                            this.gameEvent(GameEvent.ENTITY_DAMAGE, p_31579_.getEntity());
                            this.lastHit = i;
                        } else {
                            this.brokenByPlayer(p_31579_);
                            this.showBreakingParticles();
                            this.kill();
                        }

                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void showBreakingParticles() {
        if (this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ModBlocks.HAUNTED_PLANKS.get().defaultBlockState()), this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05D);
        }
    }

    private void causeDamage(DamageSource p_31649_, float p_31650_) {
        float f = this.getHealth();
        f -= p_31650_;
        if (f <= 0.5F) {
            this.brokenByAnything(p_31649_);
            this.kill();
        } else {
            this.setHealth(f);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, p_31649_.getEntity());
        }

    }

    private void brokenByPlayer(DamageSource p_31647_) {
        Block.popResource(this.level, this.blockPosition(), new ItemStack(ModItems.HAUNTED_ARMOR_STAND.get()));
        this.brokenByAnything(p_31647_);
    }

    private void brokenByAnything(DamageSource p_31654_) {
        this.playBrokenSound();
        this.dropAllDeathLoot(p_31654_);

        for (ItemStack itemStack : this.getHandSlots()){
            if (!itemStack.isEmpty()) {
                Block.popResource(this.level, this.blockPosition().above(), itemStack);
            }
        }

        for (ItemStack itemStack : this.getArmorSlots()){
            if (!itemStack.isEmpty()) {
                Block.popResource(this.level, this.blockPosition().above(), itemStack);
            }
        }

    }

    private void playBrokenSound() {
        this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
    }

    public ItemStack getPickResult() {
        return new ItemStack(ModItems.HAUNTED_ARMOR_STAND.get());
    }
}
