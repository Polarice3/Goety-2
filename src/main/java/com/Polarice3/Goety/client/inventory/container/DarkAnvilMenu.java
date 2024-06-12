package com.Polarice3.Goety.client.inventory.container;

import com.Polarice3.Goety.common.blocks.DarkAnvilBlock;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class DarkAnvilMenu extends ItemCombinerMenu {
    public int repairItemCountCost;
    private String itemName;
    private final DataSlot cost = DataSlot.standalone();

    public DarkAnvilMenu(int p_39008_, Inventory p_39009_, ContainerLevelAccess p_39010_) {
        super(ModContainerType.DARK_ANVIL.get(), p_39008_, p_39009_, p_39010_);
        this.addDataSlot(this.cost);
    }

    public DarkAnvilMenu(int i, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        this(i, inventory, ContainerLevelAccess.NULL);
    }

    protected boolean isValidBlock(BlockState p_39019_) {
        return p_39019_.is(ModTags.Blocks.DARK_ANVILS);
    }

    protected boolean mayPickup(Player p_39023_, boolean p_39024_) {
        return (p_39023_.getAbilities().instabuild || p_39023_.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
    }

    protected void onTake(Player p_150474_, ItemStack p_150475_) {
        if (!p_150474_.getAbilities().instabuild) {
            if (MainConfig.DarkAnvilTakePoints.get()){
                p_150474_.giveExperiencePoints(-this.cost.get());
            } else {
                p_150474_.giveExperienceLevels(-this.cost.get());
            }
        }

        float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(p_150474_, p_150475_, DarkAnvilMenu.this.inputSlots.getItem(0), DarkAnvilMenu.this.inputSlots.getItem(1));

        if (breakChance > 0.05F) {
            breakChance = 0.05F;
        }
        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (this.repairItemCountCost > 0) {
            ItemStack itemstack = this.inputSlots.getItem(1);
            if (!itemstack.isEmpty() && itemstack.getCount() > this.repairItemCountCost) {
                itemstack.shrink(this.repairItemCountCost);
                this.inputSlots.setItem(1, itemstack);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }

        this.cost.set(0);
        float finalBreakChance = breakChance;
        this.access.execute((p_150479_, p_150480_) -> {
            BlockState blockstate = p_150479_.getBlockState(p_150480_);
            if (!p_150474_.getAbilities().instabuild && blockstate.is(ModTags.Blocks.DARK_ANVILS) && p_150474_.getRandom().nextFloat() < finalBreakChance) {
                BlockState blockstate1 = DarkAnvilBlock.damage(blockstate);
                if (blockstate1 == null) {
                    p_150479_.removeBlock(p_150480_, false);
                    for (int i = 0; i < 12; ++i) {
                        if (player.level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !player.level.restoringBlockSnapshots) {
                            ItemStack itemStack = new ItemStack(ModItems.DARK_METAL_INGOT.get());
                            double d0 = (double) (player.level.random.nextFloat() * 0.5F) + 0.25D;
                            double d1 = (double) (player.level.random.nextFloat() * 0.5F) + 0.25D;
                            double d2 = (double) (player.level.random.nextFloat() * 0.5F) + 0.25D;
                            ItemEntity itementity = new ItemEntity(player.level, (double) p_150480_.getX() + d0, (double) p_150480_.getY() + d1, (double) p_150480_.getZ() + d2, itemStack);
                            itementity.setDefaultPickUpDelay();
                            player.level.addFreshEntity(itementity);
                        }
                    }
                    p_150479_.levelEvent(1029, p_150480_, 0);
                } else {
                    p_150479_.setBlock(p_150480_, blockstate1, 2);
                    p_150479_.levelEvent(1030, p_150480_, 0);
                }
            } else {
                p_150479_.levelEvent(1030, p_150480_, 0);
            }

        });
    }

    public void createResult() {
        ItemStack itemstack = this.inputSlots.getItem(0);
        this.cost.set(1);
        int i = 0;
        int j = 0;
        int k = 0;
        if (itemstack.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
        } else {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.inputSlots.getItem(1);
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
            j += itemstack.getBaseRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getBaseRepairCost());
            this.repairItemCountCost = 0;
            boolean flag = false;

            if (!itemstack2.isEmpty()) {
                flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
                if (itemstack1.isDamageableItem() && itemstack1.getItem().isValidRepairItem(itemstack, itemstack2)) {
                    int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                    if (l2 <= 0) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    int i3;
                    for(i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
                        int j3 = itemstack1.getDamageValue() - l2;
                        itemstack1.setDamageValue(j3);
                        ++i;
                        l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                    }

                    this.repairItemCountCost = i3;
                } else {
                    if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    if (itemstack1.isDamageableItem() && !flag) {
                        int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
                        int i1 = itemstack2.getMaxDamage() - itemstack2.getDamageValue();
                        int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = itemstack1.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }

                        if (l1 < itemstack1.getDamageValue()) {
                            itemstack1.setDamageValue(l1);
                            i += 2;
                        }
                    }

                    Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
                    boolean flag2 = false;
                    boolean flag3 = false;

                    for(Enchantment enchantment1 : map1.keySet()) {
                        if (enchantment1 != null) {
                            int i2 = map.getOrDefault(enchantment1, 0);
                            int j2 = map1.get(enchantment1);
                            j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                            boolean flag1 = enchantment1.canEnchant(itemstack);
                            if (this.player.getAbilities().instabuild || itemstack.is(Items.ENCHANTED_BOOK)) {
                                flag1 = true;
                            }

                            for(Enchantment enchantment : map.keySet()) {
                                if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                                    flag1 = false;
                                    ++i;
                                }
                            }

                            if (!flag1) {
                                flag3 = true;
                            } else {
                                flag2 = true;
                                if (!MainConfig.DarkAnvilIgnoreMaxLevels.get() && j2 > enchantment1.getMaxLevel()) {
                                    j2 = enchantment1.getMaxLevel();
                                }

                                int maxLevel = Math.max(map.getOrDefault(enchantment1, 0), map1.get(enchantment1));
                                maxLevel = Math.max(maxLevel, j2);
                                if (maxLevel != j2) {
                                    j2 = maxLevel;
                                }

                                map.put(enchantment1, j2);
                                int k3 = switch (enchantment1.getRarity()) {
                                    case COMMON -> 1;
                                    case UNCOMMON -> 2;
                                    case RARE -> 4;
                                    case VERY_RARE -> 8;
                                };

                                if (flag) {
                                    k3 = Math.max(1, k3 / 2);
                                }

                                i += k3 * j2;
                                if (itemstack.getCount() > 1) {
                                    i = MainConfig.DarkAnvilRepairCost.get();
                                }
                            }
                        }
                    }

                    if (flag3 && !flag2) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }
                }
            }

            if (this.itemName != null && !StringUtils.isBlank(this.itemName)) {
                if (!this.itemName.equals(itemstack.getHoverName().getString())) {
                    k = 1;
                    i += k;
                    itemstack1.setHoverName(Component.literal(this.itemName));
                }
            } else if (itemstack.hasCustomHoverName()) {
                k = 1;
                i += k;
                itemstack1.resetHoverName();
            }
            if (flag && !itemstack1.isBookEnchantable(itemstack2)) {
                itemstack1 = ItemStack.EMPTY;
            }

            this.cost.set(j + i);
            if (i <= 0) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (this.cost.get() > MainConfig.DarkAnvilRepairCost.get()){
                this.cost.set(MainConfig.DarkAnvilRepairCost.get());
            }

            if (k == i && k > 0) {
                this.cost.set(1);
            }

            if (MainConfig.DarkAnvilCap.get() && this.cost.get() >= MainConfig.DarkAnvilRepairCost.get() && !this.player.getAbilities().instabuild) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (!itemstack1.isEmpty()) {
                int k2 = itemstack1.getBaseRepairCost();
                if (!itemstack2.isEmpty() && k2 < itemstack2.getBaseRepairCost()) {
                    k2 = itemstack2.getBaseRepairCost();
                }

                if (k != i || k == 0) {
                    k2 = calculateIncreasedRepairCost(k2);
                }

                itemstack1.setRepairCost(k2);
                EnchantmentHelper.setEnchantments(map, itemstack1);
            }

            this.resultSlots.setItem(0, itemstack1);
            this.broadcastChanges();
        }
    }

    public static int calculateIncreasedRepairCost(int repairCost) {
        return repairCost + 1;
    }

    public void setItemName(String p_39021_) {
        this.itemName = p_39021_;
        if (this.getSlot(2).hasItem()) {
            ItemStack itemstack = this.getSlot(2).getItem();
            if (StringUtils.isBlank(p_39021_)) {
                itemstack.resetHoverName();
            } else {
                itemstack.setHoverName(Component.literal(this.itemName));
            }
        }

        this.createResult();
    }

    public int getCost() {
        return this.cost.get();
    }

    public void setMaximumCost(int value) {
        this.cost.set(value);
    }

}
