package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.WraithServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.CairnNecromancerServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.common.entities.projectiles.IceSpike;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class AbstractCairnNecromancer extends AbstractNecromancer{

    public AbstractCairnNecromancer(EntityType<? extends AbstractSkeletonServant> type, Level level) {
        super(type, level);
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
    public void performRangedAttack(@NotNull LivingEntity p_33317_, float p_33318_) {
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

    public Summoned getDefaultSummon(){
        return new SkeletonServant(ModEntityType.STRAY_SERVANT.get(), this.level);
    }

    public Summoned getSummon(){
        Summoned summoned = getDefaultSummon();
        if (this.getSummonList().contains(ModEntityType.FROZEN_ZOMBIE_SERVANT.get())) {
            if (this.level.random.nextBoolean()) {
                summoned = new ZombieServant(ModEntityType.FROZEN_ZOMBIE_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.STRAY_SERVANT.get())) {
            if (this.level.random.nextBoolean()) {
                summoned = new SkeletonServant(ModEntityType.STRAY_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.WRAITH_SERVANT.get())) {
            if (this.level.random.nextFloat() <= 0.05F) {
                summoned = new WraithServant(ModEntityType.WRAITH_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.VANGUARD_SERVANT.get())){
            if (this.level.random.nextFloat() <= 0.15F) {
                summoned = new VanguardServant(ModEntityType.VANGUARD_SERVANT.get(), this.level);
            }
        }
        return summoned;
    }

    public class SummonServantSpell extends SummoningSpellGoal {

        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof IOwned owned && owned.getTrueOwner() instanceof AbstractNecromancer;
            int i = AbstractCairnNecromancer.this.level.getEntitiesOfClass(LivingEntity.class, AbstractCairnNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D)
                    , predicate).size();
            return super.canUse() && i < 6;
        }

        protected void castSpell(){
            if (AbstractCairnNecromancer.this.level instanceof ServerLevel serverLevel) {
                for (int i1 = 0; i1 < 2; ++i1) {
                    Summoned summonedentity = AbstractCairnNecromancer.this.getSummon();
                    BlockPos blockPos = BlockFinder.SummonRadius(AbstractCairnNecromancer.this.blockPosition(), summonedentity, serverLevel);
                    summonedentity.setTrueOwner(AbstractCairnNecromancer.this);
                    summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                    MobUtil.moveDownToGround(summonedentity);
                    summonedentity.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                    summonedentity.setPersistenceRequired();
                    summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(AbstractCairnNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    if (BlockFinder.findStructure(serverLevel, AbstractCairnNecromancer.this, ModTags.Structures.CRYPT)) {
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
        protected NecromancerSpellType getNecromancerSpellType() {
            return NecromancerSpellType.ZOMBIE;
        }
    }
}
