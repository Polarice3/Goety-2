package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.IMobCrafter;
import com.Polarice3.Goety.api.entities.ally.illager.ILooter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MobSingleCraftingGoal<T extends Mob & ILooter & IMobCrafter> extends MobCraftingGoal<T> {
    private List<LivingEntity> cachedAllies = List.of();
    private int allyCheckCooldown = 0;
    public Predicate<LivingEntity> entityPredicate;
    public Predicate<ItemStack> itemPredicate;

    public MobSingleCraftingGoal(T mob, int craftTime, float speedModifier, Predicate<LivingEntity> entityPredicate, Predicate<ItemStack> itemPredicate) {
        super(mob, craftTime, speedModifier);
        this.entityPredicate = entityPredicate;
        this.itemPredicate = itemPredicate;
    }

    public static boolean cannotSatisfy(CraftingRecipe recipe, SimpleContainer inv) {
        List<ItemStack> pool = new ArrayList<>();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            for (int c = 0; c < s.getCount(); c++) {
                pool.add(s);
            }
        }
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.isEmpty()) {
                continue;
            }
            boolean matched = false;
            for (int i = 0; i < pool.size(); i++) {
                if (ingredient.test(pool.get(i))) {
                    pool.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return true;
            }
        }
        return false;
    }

    private List<LivingEntity> getAllies() {
        if (--this.allyCheckCooldown <= 0) {
            this.cachedAllies = this.mob.level.getEntitiesOfClass(
                    LivingEntity.class,
                    this.mob.getBoundingBox().inflate(16),
                    this.entityPredicate);
            this.allyCheckCooldown = 60;
        }
        return this.cachedAllies;
    }

    @Override
    public boolean canStartCrafting() {
        if (this.mob.level instanceof ServerLevel serverLevel) {
            return this.findCraftableStack(serverLevel) != null;
        }
        return false;
    }

    @Nullable
    public ItemStack findCraftableStack(ServerLevel level) {
        if (this.getAllies().isEmpty()) {
            return null;
        }
        int itemHeld = this.mob.itemsInInv(this.itemPredicate).stream().mapToInt(ItemStack::getCount).sum();
        if (itemHeld >= this.getAllies().size()) {
            return null;
        }
        CraftingRecipe recipe = this.getRecipe(level);
        if (recipe == null || cannotSatisfy(recipe, this.mob.getInventory())) {
            return null;
        }
        return this.mob.itemsInInv(s -> !s.isEmpty()).stream().findFirst().orElse(null);
    }

    public CraftingRecipe getRecipe(ServerLevel serverLevel) {
        return null;
    }

    @Override
    public void onCraft(ServerLevel serverLevel) {
        SimpleContainer inv = this.mob.getInventory();
        if (this.getAllies().isEmpty()) {
            return;
        }

        CraftingRecipe recipe = this.getRecipe(serverLevel);
        if (recipe == null || cannotSatisfy(recipe, inv)) {
            return;
        }

        ItemStack result = recipe.getResultItem(serverLevel.registryAccess()).copy();
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.isEmpty()) {
                continue;
            }
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack slot = inv.getItem(i);
                if (!slot.isEmpty() && ingredient.test(slot)) {
                    ItemStack remainder = slot.getCraftingRemainingItem() != null
                            ? slot.getCraftingRemainingItem() : ItemStack.EMPTY;
                    slot.shrink(1);
                    if (slot.isEmpty()) {
                        inv.setItem(i, remainder);
                    } else if (!remainder.isEmpty() && inv.canAddItem(remainder)) {
                        inv.addItem(remainder);
                    }
                    break;
                }
            }
        }
        inv.setChanged();

        if (inv.canAddItem(result)) {
            inv.addItem(result);
            serverLevel.playSound(null, this.mob.blockPosition(), SoundEvents.ANVIL_USE, this.mob.getSoundSource(), 0.5F, 1.0F);
        }
    }
}
