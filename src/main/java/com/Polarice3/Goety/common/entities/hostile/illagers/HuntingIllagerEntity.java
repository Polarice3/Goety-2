package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.entities.ai.StealTotemGoal;
import com.Polarice3.Goety.common.entities.neutral.ICustomAttributes;
import com.Polarice3.Goety.common.items.magic.TotemOfSouls;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public abstract class HuntingIllagerEntity extends SpellcasterIllager implements ICustomAttributes {
    private static final EntityDataAccessor<Boolean> RIDER = SynchedEntityData.defineId(HuntingIllagerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> FLAGS = SynchedEntityData.defineId(HuntingIllagerEntity.class, EntityDataSerializers.BYTE);
    public final Predicate<Entity> field_213690_b = Entity::isAlive;
    public final SimpleContainer inventory = new SimpleContainer(1);

    protected HuntingIllagerEntity(EntityType<? extends HuntingIllagerEntity> p_i48551_1_, Level p_i48551_2_) {
        super(p_i48551_1_, p_i48551_2_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new StealTotemGoal<>(this));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    public void setConfigurableAttributes(){
    }

    public void tick(){
        super.tick();
        EquipmentSlot equipmentslottype = EquipmentSlot.OFFHAND;
        ItemStack itemstack1 = this.getItemBySlot(equipmentslottype);
        if (itemstack1.isEmpty()) {
            if (!this.inventory.isEmpty()) {
                for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
                    ItemStack itemstack = this.inventory.getItem(i);
                    if (itemstack.getItem() == Items.TOTEM_OF_UNDYING) {
                        this.setItemSlot(equipmentslottype, itemstack);
                    }
                }
            }
        }
        for (LivingEntity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8.0D), field_213690_b)) {
            if (this.isRider()){
                if (entity instanceof Ravager ravagerEntity){
                    if (!ravagerEntity.isVehicle() && !this.isPassenger()){
                        this.startRiding(ravagerEntity, true);
                    }
                }
            }
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RIDER, false);
        this.entityData.define(FLAGS, (byte)0);
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(FLAGS);
        return (i & mask) != 0;
    }

    private void setFlag(int mask, boolean value) {
        int i = this.entityData.get(FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(FLAGS, (byte)(i & 255));
    }

    public void setCasting(boolean casting){
        this.setFlag(1, casting);
    }

    public boolean isCasting(){
        return this.getFlag(1);
    }

    public boolean isRider(){
        return this.entityData.get(RIDER);
    }

    public void setRider(boolean pIsRider){
        this.entityData.set(RIDER, pIsRider);
    }

    public boolean isCastingSpell() {
        return super.isCastingSpell() || this.isCasting();
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        ListTag listnbt = new ListTag();

        for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
                listnbt.add(itemstack.save(new CompoundTag()));
            }
        }

        pCompound.put("Inventory", listnbt);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        ListTag listnbt = pCompound.getList("Inventory", 10);

        for(int i = 0; i < listnbt.size(); ++i) {
            ItemStack itemstack = ItemStack.of(listnbt.getCompound(i));
            if (!itemstack.isEmpty()) {
                this.inventory.addItem(itemstack);
            }
        }

        this.setCanPickUpLoot(true);
        this.setConfigurableAttributes();
    }

    public void pickUpItem(ItemEntity pItemEntity) {
        ItemStack itemstack = pItemEntity.getItem();
        if (itemstack.getItem() instanceof TotemOfSouls) {
            if (this.inventory.canAddItem(itemstack)) {
                this.onItemPickup(pItemEntity);
                this.inventory.addItem(itemstack);
                this.take(pItemEntity, itemstack.getCount());
                pItemEntity.discard();
            } else {
                super.pickUpItem(pItemEntity);
            }
        } else if (itemstack.getItem() == Items.TOTEM_OF_UNDYING) {
            EquipmentSlot equipmentslottype = EquipmentSlot.OFFHAND;
            ItemStack itemstack1 = this.getItemBySlot(equipmentslottype);
            if (itemstack1.isEmpty()) {
                this.onItemPickup(pItemEntity);
                this.setItemSlot(equipmentslottype, itemstack);
                this.take(pItemEntity, itemstack.getCount());
                pItemEntity.discard();
            } else if (this.inventory.canAddItem(itemstack)) {
                this.onItemPickup(pItemEntity);
                this.inventory.addItem(itemstack);
                this.take(pItemEntity, itemstack.getCount());
                pItemEntity.discard();
            } else {
                super.pickUpItem(pItemEntity);
            }
        } else {
            super.pickUpItem(pItemEntity);
        }

    }

    public SlotAccess getSlot(int p_149743_) {
        int i = p_149743_ - 300;
        return i >= 0 && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(p_149743_);
    }

    public void die(DamageSource pCause) {
        if (!this.inventory.isEmpty()){
            for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);
                if (itemstack != ItemStack.EMPTY){
                    if (itemstack.getItem() instanceof TotemOfSouls){
                        TotemOfSouls.increaseSouls(itemstack, MainConfig.IllagerSouls.get());
                    }
                    ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), itemstack);
                    itemEntity.setDefaultPickUpDelay();
                    this.level.addFreshEntity(itemEntity);
                }
            }

        }
        super.die(pCause);
    }

}
