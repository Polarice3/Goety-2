package com.Polarice3.Goety.init;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.*;
import java.util.function.BiPredicate;

/**
 * Method and Registry based on TeamAbnormal codes: <a href="https://github.com/team-abnormals/blueprint/blob/1.19.x/src/main/java/com/teamabnormals/blueprint/core/util/DataUtil.java#L288">...</a>
 */
public class ModDispenserRegister {
    private static final Vector<AlternativeDispenseBehavior> ALTERNATIVE_DISPENSE_BEHAVIORS = new Vector<>();

    public static void registerAlternativeDispenseBehavior(AlternativeDispenseBehavior behavior) {
        ALTERNATIVE_DISPENSE_BEHAVIORS.add(behavior);
    }

    public static List<AlternativeDispenseBehavior> getSortedAlternativeDispenseBehaviors() {
        List<AlternativeDispenseBehavior> behaviors = new ArrayList<>(ALTERNATIVE_DISPENSE_BEHAVIORS);
        Collections.sort(behaviors);
        return behaviors;
    }

    public static BlockPos offsetPos(BlockSource source) {
        return source.getPos().relative(source.getBlockState().getValue(DirectionalBlock.FACING));
    }

    public static class AlternativeDispenseBehavior implements Comparable<AlternativeDispenseBehavior> {
        protected final String modId;
        protected final Item item;
        protected final BiPredicate<BlockSource, ItemStack> condition;
        protected final DispenseItemBehavior behavior;
        protected final Comparator<String> modIdComparator;

        public AlternativeDispenseBehavior(String modId, Item item, BiPredicate<BlockSource, ItemStack> condition, DispenseItemBehavior behavior) {
            this(modId, item, condition, behavior, (id1, id2) -> 0);
        }

        public AlternativeDispenseBehavior(String modId, Item item, BiPredicate<BlockSource, ItemStack> condition, DispenseItemBehavior behavior, Comparator<String> modIdComparator) {
            this.modId = modId;
            this.item = item;
            this.condition = condition;
            this.behavior = behavior;
            this.modIdComparator = modIdComparator;
        }

        @Override
        public int compareTo(AlternativeDispenseBehavior behavior) {
            return this.item == behavior.item ? this.modIdComparator.compare(this.modId, behavior.modId) : 0;
        }

        public void register() {
            DispenseItemBehavior oldBehavior = DispenserBlock.DISPENSER_REGISTRY.get(item);
            DispenserBlock.registerBehavior(item, (source, stack) -> condition.test(source, stack) ? behavior.dispense(source, stack) : oldBehavior.dispense(source, stack));
        }
    }
}
