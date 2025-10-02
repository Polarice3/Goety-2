package com.Polarice3.Goety.common.blocks.entities.void_vault;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.ModLootTables;
import com.Polarice3.Goety.utils.PlayerDetector;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

//Took this config from @Soumeh's deobfuscated Minecraft codes:https://github.com/Soumeh/1.21.1-Deobfuscated/blob/51c9e6987e92432771e08dc6c9ee8f4f6c5a72f2/minecraft/src/net/minecraft/block/vault/VaultConfig.java
public record VoidVaultConfig (ResourceLocation lootTable, double activationRange, double deactivationRange, ItemStack keyItem, Optional<ResourceLocation> overrideLootTableToDisplay, PlayerDetector playerDetector, PlayerDetector.EntitySelector entitySelector) {
        static final String CONFIG_KEY = "config";
        public static VoidVaultConfig DEFAULT = new VoidVaultConfig();
        public static Codec<VoidVaultConfig> CODEC = RecordCodecBuilder.create(
                        instance -> instance.group(
                                        ResourceLocation.CODEC.optionalFieldOf("loot_table", DEFAULT.lootTable()).forGetter(VoidVaultConfig::lootTable),
                                        Codec.DOUBLE.optionalFieldOf("activation_range", DEFAULT.activationRange()).forGetter(VoidVaultConfig::activationRange),
                                        Codec.DOUBLE.optionalFieldOf("deactivation_range", DEFAULT.deactivationRange()).forGetter(VoidVaultConfig::deactivationRange),
                                        createOptionalCodec("key_item").forGetter(VoidVaultConfig::keyItem),
                                        ResourceLocation.CODEC.optionalFieldOf("override_loot_table_to_display").forGetter(VoidVaultConfig::overrideLootTableToDisplay)
                                )
                                .apply(instance, VoidVaultConfig::new)
                );

    public static MapCodec<ItemStack> createOptionalCodec(String fieldName) {
        return ItemStack.CODEC.optionalFieldOf(fieldName)
                .xmap(optional -> optional.orElse(ItemStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
    }

    public VoidVaultConfig() {
        this(ModLootTables.VOID_VAULT_REWARD, 4.0, 4.5, new ItemStack(ModItems.VOID_KEY.get()), Optional.empty(), PlayerDetector.INCLUDING_CREATIVE_PLAYERS, PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
    }

	public VoidVaultConfig(ResourceLocation lootTable, double activationRange, double deactivationRange, ItemStack keyItem, Optional<ResourceLocation> overrideLootTableToDisplay) {
        this(lootTable, activationRange, deactivationRange, keyItem, overrideLootTableToDisplay, DEFAULT.playerDetector(), DEFAULT.entitySelector());
    }

    private DataResult<VoidVaultConfig> validate() {
        return this.activationRange > this.deactivationRange
                ? DataResult.error(() -> "Activation range must (" + this.activationRange + ") be less or equal to deactivation range (" + this.deactivationRange + ")")
                : DataResult.success(this);
    }
}
