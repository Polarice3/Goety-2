package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModLootModifier {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Goety.MOD_ID);

    public static final RegistryObject<Codec<InstantPickModifier>> INSTANT_PICK_MODIFIER = GLOBAL_LOOT_MODIFIER.register("instant_pick", InstantPickModifier.CODEC);

    public static class InstantPickModifier extends LootModifier {

        public static final Supplier<Codec<InstantPickModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, InstantPickModifier::new)));

        public InstantPickModifier(LootItemCondition[] conditions) {
            super(conditions);
        }

        @Override
        protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
            Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
            if (entity instanceof Player player) {
                if (!MobUtil.isShifting(player)) {
                    loot.removeIf(player::addItem);
                }
            }
            return loot;
        }

        @Override
        public Codec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }
}
