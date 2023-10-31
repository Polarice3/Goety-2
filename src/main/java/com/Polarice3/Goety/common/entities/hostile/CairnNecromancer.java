package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.SkeletonServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.ZombieServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.IceSpike;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.world.structures.ModStructures;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class CairnNecromancer extends AbstractNecromancer implements Enemy {

    public CairnNecromancer(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
        this.setHostile(true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(4, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public void projectileGoal(int priority) {
        this.goalSelector.addGoal(priority, new NecromancerRangedGoal(this, 1.0D, 20, 10.0F));
    }

    public void summonSpells(int priority){
        this.goalSelector.addGoal(priority, new SummonServantSpell());
    }

    @Override
    public float getVoicePitch() {
        return 0.75F;
    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
        IceSpike iceSpike = new IceSpike(this, this.level);
        double d0 = p_33317_.getX() - this.getX();
        double d1 = p_33317_.getY(0.3333333333333333D) - iceSpike.getY();
        double d2 = p_33317_.getZ() - this.getZ();
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        iceSpike.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, 1.0F);
        if (this.level.addFreshEntity(iceSpike)){
            this.playSound(ModSounds.CAST_SPELL.get());
            this.swing(InteractionHand.MAIN_HAND);
        }
    }

    public class SummonServantSpell extends UseSpellGoal {

        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof Owned owned && owned.getTrueOwner() instanceof AbstractNecromancer;
            int i = CairnNecromancer.this.level.getEntitiesOfClass(Owned.class, CairnNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D)
                    , predicate).size();
            return super.canUse() && i < 2;
        }

        protected void castSpell(){
            if (CairnNecromancer.this.level instanceof ServerLevel serverLevel) {
                for (int i1 = 0; i1 < 1 + serverLevel.random.nextInt(3); ++i1) {
                    Summoned summonedentity = new SkeletonServant(ModEntityType.SKELETON_SERVANT.get(), serverLevel);
                    if (CairnNecromancer.this.hasAlternateSummon()){
                        if (serverLevel.random.nextBoolean()){
                            summonedentity = new ZombieServant(ModEntityType.ZOMBIE_SERVANT.get(), serverLevel);
                        }
                    }
                    BlockPos blockPos = BlockFinder.SummonRadius(CairnNecromancer.this, serverLevel);
                    summonedentity.setOwnerId(CairnNecromancer.this.getUUID());
                    summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                    MobUtil.moveDownToGround(summonedentity);
                    summonedentity.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                    summonedentity.setPersistenceRequired();
                    summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(CairnNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    if (BlockFinder.findStructure(serverLevel, CairnNecromancer.this, ModStructures.CRYPT_KEY)) {
                        for(EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
                            if (equipmentslottype.getType() == EquipmentSlot.Type.ARMOR) {
                                ItemStack itemstack = summonedentity.getItemBySlot(equipmentslottype);
                                if (itemstack.isEmpty()) {
                                    Item item = cursedKnightArmor(equipmentslottype);
                                    if (item != null && serverLevel.random.nextFloat() <= 0.25F) {
                                        summonedentity.setItemSlot(equipmentslottype, new ItemStack(item));
                                    }
                                }
                            }
                            summonedentity.setDropChance(equipmentslottype, 0.0F);
                        }
                        summonedentity.setItemInHand(InteractionHand.MAIN_HAND, Items.IRON_SWORD.getDefaultInstance());
                        summonedentity.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
                    }
                    if (serverLevel.addFreshEntity(summonedentity)){
                        summonedentity.playSound(ModSounds.SUMMON_SPELL.get(), 1.0F, 1.0F);
                        for (int i = 0; i < serverLevel.random.nextInt(35) + 10; ++i) {
                            serverLevel.sendParticles(ParticleTypes.POOF, summonedentity.getX(), summonedentity.getEyeY(), summonedentity.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
                        }
                    }
                }
            }
        }

        @Nullable
        public Item cursedKnightArmor(EquipmentSlot pSlot) {
            return switch (pSlot) {
                case HEAD -> Items.CHAINMAIL_HELMET;
                case CHEST -> ModItems.CURSED_KNIGHT_CHESTPLATE.get();
                case LEGS -> Items.CHAINMAIL_LEGGINGS;
                case FEET -> ModItems.CURSED_KNIGHT_BOOTS.get();
                default -> null;
            };
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.PREPARE_SUMMON.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.ZOMBIE;
        }
    }

}
