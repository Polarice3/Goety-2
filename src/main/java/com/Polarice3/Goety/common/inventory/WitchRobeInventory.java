package com.Polarice3.Goety.common.inventory;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import javax.annotation.Nullable;
import java.util.Arrays;

import static net.minecraftforge.common.brewing.BrewingRecipeRegistry.canBrew;

/**
 * Code based from @cnlimiter's Portable-Craft mod: <a href="https://github.com/Nova-Committee/Portable-Craft/blob/1.18.2-forge/src/main/java/committee/nova/portablecraft/common/containers/BrewingStandInventory.java">...</a>
 */
public class WitchRobeInventory extends SimpleContainer implements MenuProvider {
    /**
     * item.get(0 - 2) = Outputs
     * item.get(3) = Catalyst
     * item.get(4) = Fuel
     */
    private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 4};
    private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
    private int brewTime;
    private boolean[] lastPotionCount;
    private Item ingredient;
    private int fuel;
    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> brewTime;
                case 1 -> fuel;
                default -> 0;
            };
        }

        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> brewTime = pValue;
                case 1 -> fuel = pValue;
            }

        }

        public int getCount() {
            return 2;
        }
    };
    private int inventoryNum;
    private int increaseSpeed;
    private double rest;
    private LivingEntity livingEntity;


    public WitchRobeInventory(CompoundTag nbt) {
        this.load(nbt);
    }

    public WitchRobeInventory() {
    }

    public int getInventoryNum() {
        return this.inventoryNum;
    }

    public void setInventoryNum(int inventoryNum) {
        this.inventoryNum = inventoryNum;
        this.setChanged();
    }

    public int getIncreaseSpeed() {
        return this.increaseSpeed;
    }

    public void setIncreaseSpeed(int increaseSpeed) {
        this.increaseSpeed = increaseSpeed;
        this.setChanged();
    }

    public double getRest() {
        return this.rest;
    }

    public void setRest(double newrest) {
        this.rest = newrest;
        this.setChanged();
    }

    public LivingEntity getLivingEntity(){
        return this.livingEntity;
    }

    public void setLivingEntity(LivingEntity livingEntity){
        this.livingEntity = livingEntity;
        this.setChanged();
    }

    public void tick() {
        double x = (double) (this.increaseSpeed * 720) / 100.0D;
        int time = (int) Math.floor(1.0D + x + this.rest);
        this.setRest(1.0D + x + this.rest - (double) time);

        for (int a = 0; a < time; ++a) {
            ItemStack itemstack = this.items.get(4);
            if (this.fuel <= 0 && itemstack.isStackable()) {
                this.fuel = 20;
                itemstack.shrink(1);
                this.setChanged();
            }

            boolean flag = this.isBrewable();
            boolean flag1 = this.brewTime > 0;
            ItemStack itemstack1 = this.items.get(3);
            if (flag1) {
                --this.brewTime;
                boolean flag2 = this.brewTime == 0;
                if (flag2 && flag) {
                    this.doBrew();
                    this.setChanged();
                } else if (!flag) {
                    this.brewTime = 0;
                    this.setChanged();
                } else if (this.ingredient != itemstack1.getItem()) {
                    this.brewTime = 0;
                    this.setChanged();
                }
            } else if (flag && this.fuel > 0) {
                --this.fuel;
                this.brewTime = 400;
                this.ingredient = itemstack1.getItem();
                this.setChanged();
            }

            boolean[] aboolean = this.getPotionBits();
            if (!Arrays.equals(aboolean, this.lastPotionCount)) {
                this.lastPotionCount = aboolean;
            }
        }
    }

    public int getContainerSize() {
        return this.items.size();
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }


    public boolean[] getPotionBits() {
        boolean[] aboolean = new boolean[3];

        for (int i = 0; i < 3; ++i) {
            if (!this.items.get(i).isEmpty()) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    public boolean isBrewable() {
        ItemStack itemstack = this.items.get(3);
        if (!itemstack.isEmpty())
            return canBrew(items, itemstack, SLOTS_FOR_SIDES);
        if (itemstack.isEmpty()) {
            return false;
        } else if (!PotionBrewing.isIngredient(itemstack)) {
            return false;
        } else {
            for (int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = this.items.get(i);
                if (!itemstack1.isEmpty() && PotionBrewing.hasMix(itemstack1, itemstack)) {
                    return true;
                }
            }

            return false;
        }
    }

    private void doBrew() {
        if (net.minecraftforge.event.ForgeEventFactory.onPotionAttemptBrew(items)) {
            return;
        }
        ItemStack itemstack = this.items.get(3);

        net.minecraftforge.common.brewing.BrewingRecipeRegistry.brewPotions(items, itemstack, SLOTS_FOR_SIDES);
        net.minecraftforge.event.ForgeEventFactory.onPotionBrewed(items);
        if (itemstack.hasCraftingRemainingItem()) {
            ItemStack itemstack1 = itemstack.getCraftingRemainingItem();
            itemstack.shrink(1);
            if (itemstack.isEmpty()) {
                itemstack = itemstack1;
            }

        } else {
            itemstack.shrink(1);
        }

        this.playSound(SoundEvents.BREWING_STAND_BREW);

        this.items.set(3, itemstack);
    }

    public static boolean isAirOrEmpty(ItemStack itemStack){
        return itemStack.isEmpty() || itemStack.is(Items.AIR);
    }

    public boolean inputEmpty(){
        return isAirOrEmpty(this.items.get(0)) || isAirOrEmpty(this.items.get(1)) || isAirOrEmpty(this.items.get(2));
    }

    public boolean isWaterOrEmpty(){
        boolean[] flag = {false, false, false};
        if (!inputEmpty()) {
            flag[0] = PotionUtils.getPotion(this.items.get(0)) == Potions.WATER || isAirOrEmpty(this.items.get(0));
            flag[1] = PotionUtils.getPotion(this.items.get(1)) == Potions.WATER || isAirOrEmpty(this.items.get(1));
            flag[2] = PotionUtils.getPotion(this.items.get(2)) == Potions.WATER || isAirOrEmpty(this.items.get(2));
        }

        return flag[0] && flag[1] && flag[2];
    }

    public boolean needsFuel(){
        return this.fuel <= 5;
    }

    public void addFuel(ItemStack itemStack){
        if (canPlaceItem(4, itemStack)){
            if (this.items.get(4).getCount() != 64) {
                if (this.items.get(4).isEmpty()) {
                    ItemStack newItem = itemStack.copy();
                    newItem.setCount(1);
                    this.items.set(4, newItem);
                } else {
                    this.items.get(4).grow(1);
                }
                itemStack.shrink(1);
                this.playSound(SoundEvents.FIRECHARGE_USE);
                this.setChanged();
            }
        }
    }

    public void autoAddWaterBottles(ItemStack itemStack){
        if (itemStack.is(Items.GLASS_BOTTLE)){
            if (this.inputEmpty()){
                this.addBottles(itemStack, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
                this.playSound(SoundEvents.BOTTLE_FILL);
            }
        }
    }

    public void addBottlesOrCatalyst(ItemStack itemStack){
        if (canPlaceItem(3, itemStack) && (isAirOrEmpty(this.items.get(3)) || itemStack.sameItem(this.items.get(3)) && this.items.get(3).getCount() < 64)){
            this.items.set(3, itemStack.copy());
            itemStack.shrink(1);
            this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC);
            this.setChanged();
        } else if (canPlaceItem(0, itemStack) || canPlaceItem(1, itemStack) || canPlaceItem(2, itemStack)){
            this.addBottles(itemStack, itemStack.copy());
        }
    }

    public void addBottles(ItemStack input, ItemStack newStack){
        for (int i = 0; i < 3; i++){
            if (isAirOrEmpty(this.items.get(i)) && input.getCount() > 0){
                this.items.set(i, newStack);
                input.shrink(1);
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC);
                this.setChanged();
            }
        }
    }

    public void extractPotions(){
        for (int i = 0; i < 3; i++){
            if (!this.items.get(i).isEmpty()){
                if (this.getLivingEntity() instanceof Player player){
                    if (player.getInventory().add(this.items.get(i))){
                        this.playSound(SoundEvents.ITEM_PICKUP);
                    } else {
                        BlockPos blockPos = this.getLivingEntity().blockPosition();
                        ItemEntity itemEntity = new ItemEntity(this.getLivingEntity().level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.items.get(i));
                        this.getLivingEntity().level.addFreshEntity(itemEntity);
                        this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
                    }
                    this.items.get(i).shrink(1);
                }
            }
        }
        this.setChanged();
    }

    public void playSound(SoundEvent soundEvent){
        if (this.getLivingEntity() != null) {
            this.getLivingEntity().playSound(soundEvent);
            if (!this.getLivingEntity().level.isClientSide) {
                if (this.getLivingEntity() instanceof ServerPlayer serverPlayer) {
                    ModNetwork.sendTo(serverPlayer, new SPlayPlayerSoundPacket(soundEvent, 1.0F, 1.0F));
                }
            }
        }
    }

    public void load(CompoundTag p_230337_2_) {
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(p_230337_2_, this.items);
        this.brewTime = p_230337_2_.getShort("BrewTime");
        this.fuel = p_230337_2_.getByte("Fuel");
        this.inventoryNum = p_230337_2_.getInt("InventoryNum");
        this.increaseSpeed = p_230337_2_.getInt("increaseSpeed");
        this.rest = p_230337_2_.getDouble("rest");
    }

    public CompoundTag save(CompoundTag pCompound) {
        pCompound.putShort("BrewTime", (short) this.brewTime);
        ContainerHelper.saveAllItems(pCompound, this.items);
        pCompound.putByte("Fuel", (byte) this.fuel);
        pCompound.putInt("InventoryNum", this.inventoryNum);
        pCompound.putInt("increaseSpeed", this.increaseSpeed);
        pCompound.putDouble("rest", this.rest);
        return pCompound;
    }

    @Override
    public void setChanged() {
        ModSaveInventory.getInstance().setDirty();
    }

    @Override
    public ItemStack getItem(int pIndex) {
        return pIndex >= 0 && pIndex < this.items.size() ? this.items.get(pIndex) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        return ContainerHelper.removeItem(this.items, pIndex, pCount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pIndex) {
        return ContainerHelper.takeItem(this.items, pIndex);
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        if (pIndex >= 0 && pIndex < this.items.size()) {
            this.items.set(pIndex, pStack);
        }

    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        if (pIndex == 3) {
            return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidIngredient(pStack);
        } else {
            Item item = pStack.getItem();
            if (pIndex == 4) {
                return item == Items.BLAZE_POWDER;
            } else {
                return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidInput(pStack) && this.getItem(pIndex).isEmpty();
            }
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("item.goety.witch_robe.brew");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pId, Inventory pPlayer, Player p_createMenu_3_) {
        return new BrewingStandMenu(pId, pPlayer, this, this.dataAccess);
    }
}
