package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.common.blocks.*;
import com.Polarice3.Goety.common.blocks.entities.OwnedBlockEntity;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.spider.AbstractSpiderServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVillagerServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.SpellEntity;
import snownee.jade.addon.vanilla.MobBreedingProvider;
import snownee.jade.addon.vanilla.MobGrowthProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    public static IWailaClientRegistration CLIENT_REGISTRATION;

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(SummonOwnerProvider.INSTANCE, Owned.class);
        registration.registerEntityDataProvider(SummonOwnerProvider.INSTANCE, AbstractSpiderServant.class);
        registration.registerEntityDataProvider(SummonOwnerProvider.INSTANCE, SpellEntity.class);
        registration.registerEntityDataProvider(SummonLifespanProvider.INSTANCE, Owned.class);
        registration.registerEntityDataProvider(SummonLifespanProvider.INSTANCE, AbstractSpiderServant.class);
        registration.registerEntityDataProvider(SummonBreedProvider.INSTANCE, AnimalSummon.class);
        registration.registerEntityDataProvider(SummonGrowthProvider.INSTANCE, AnimalSummon.class);
        registration.registerEntityDataProvider(ServantModeProvider.INSTANCE, Summoned.class);
        registration.registerEntityDataProvider(ServantModeProvider.INSTANCE, AbstractSpiderServant.class);
        registration.registerEntityDataProvider(ZVServantProvider.INSTANCE, ZombieVillagerServant.class);

        registration.registerBlockDataProvider(BlockOwnerProvider.INSTANCE, OwnedBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        CLIENT_REGISTRATION = registration;

        registration.registerEntityComponent(SummonLifespanProvider.INSTANCE, Owned.class);
        registration.registerEntityComponent(SummonLifespanProvider.INSTANCE, AbstractSpiderServant.class);
        registration.registerEntityComponent(MobBreedingProvider.INSTANCE, AnimalSummon.class);
        registration.registerEntityComponent(MobGrowthProvider.INSTANCE, AnimalSummon.class);
        registration.registerEntityComponent(ServantModeProvider.INSTANCE, Summoned.class);
        registration.registerEntityComponent(ServantModeProvider.INSTANCE, AbstractSpiderServant.class);
        registration.registerEntityComponent(ModProfessionProvider.INSTANCE, ZombieVillagerServant.class);
        registration.registerEntityComponent(ZVServantProvider.INSTANCE, ZombieVillagerServant.class);

        registration.registerBlockComponent(SoulEnergyProvider.INSTANCE, ArcaBlock.class);
        registration.registerBlockComponent(SoulEnergyProvider.INSTANCE, CursedCageBlock.class);
        registration.registerBlockComponent(BlockOwnerProvider.INSTANCE, ArcaBlock.class);
        registration.registerBlockComponent(BlockOwnerProvider.INSTANCE, IceBouquetTrapBlock.class);
        registration.registerBlockComponent(BlockOwnerProvider.INSTANCE, SculkDevourerBlock.class);
        registration.registerBlockComponent(BlockOwnerProvider.INSTANCE, TrainingBlock.class);
    }
}
