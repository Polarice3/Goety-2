package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.common.blocks.*;
import com.Polarice3.Goety.common.blocks.entities.OwnedBlockEntity;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.Neollager;
import com.Polarice3.Goety.common.entities.ally.illager.raider.AnimalRaiderServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.ally.spider.AbstractSpiderServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieRavager;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVillagerServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractZombieVindicator;
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
        registration.registerEntityDataProvider(HostileIndicatorProvider.INSTANCE, Owned.class);
        registration.registerEntityDataProvider(HostileIndicatorProvider.INSTANCE, AbstractSpiderServant.class);
        registration.registerEntityDataProvider(SummonLifespanProvider.INSTANCE, Owned.class);
        registration.registerEntityDataProvider(SummonLifespanProvider.INSTANCE, AbstractSpiderServant.class);
        registration.registerEntityDataProvider(SummonBreedProvider.INSTANCE, AnimalSummon.class);
        registration.registerEntityDataProvider(SummonBreedProvider.INSTANCE, AnimalRaiderServant.class);
        registration.registerEntityDataProvider(SummonBreedProvider.INSTANCE, AbstractIllagerServant.class);
        registration.registerEntityDataProvider(SummonGrowthProvider.INSTANCE, AnimalSummon.class);
        registration.registerEntityDataProvider(SummonGrowthProvider.INSTANCE, AnimalRaiderServant.class);
        registration.registerEntityDataProvider(SummonGrowthProvider.INSTANCE, Neollager.class);
        registration.registerEntityDataProvider(RaiderLeaderProvider.INSTANCE, RaiderServant.class);
        registration.registerEntityDataProvider(RaiderCaptureProvider.INSTANCE, RaiderServant.class);
        registration.registerEntityDataProvider(ServantModeProvider.INSTANCE, Summoned.class);
        registration.registerEntityDataProvider(ServantModeProvider.INSTANCE, AbstractSpiderServant.class);
        registration.registerEntityDataProvider(TrainableProvider.INSTANCE, Owned.class);
        registration.registerEntityDataProvider(ZConvertServantProvider.INSTANCE, ZombieVillagerServant.class);
        registration.registerEntityDataProvider(ZConvertServantProvider.INSTANCE, AbstractZombieVindicator.class);
        registration.registerEntityDataProvider(ZConvertServantProvider.INSTANCE, ZombieRavager.class);
        registration.registerEntityDataProvider(TrainToProvider.INSTANCE, Neollager.class);

        registration.registerBlockDataProvider(BlockOwnerProvider.INSTANCE, OwnedBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        CLIENT_REGISTRATION = registration;

        registration.registerEntityComponent(HostileIndicatorProvider.INSTANCE, Owned.class);
        registration.registerEntityComponent(HostileIndicatorProvider.INSTANCE, AbstractSpiderServant.class);
        registration.registerEntityComponent(SummonLifespanProvider.INSTANCE, Owned.class);
        registration.registerEntityComponent(SummonLifespanProvider.INSTANCE, AbstractSpiderServant.class);
        registration.registerEntityComponent(MobBreedingProvider.INSTANCE, AnimalSummon.class);
        registration.registerEntityComponent(MobBreedingProvider.INSTANCE, AnimalRaiderServant.class);
        registration.registerEntityComponent(MobBreedingProvider.INSTANCE, AbstractIllagerServant.class);
        registration.registerEntityComponent(MobGrowthProvider.INSTANCE, AnimalSummon.class);
        registration.registerEntityComponent(MobGrowthProvider.INSTANCE, AnimalRaiderServant.class);
        registration.registerEntityComponent(MobGrowthProvider.INSTANCE, Neollager.class);
        registration.registerEntityComponent(RaiderLeaderProvider.INSTANCE, RaiderServant.class);
        registration.registerEntityComponent(RaiderCaptureProvider.INSTANCE, RaiderServant.class);
        registration.registerEntityComponent(ServantModeProvider.INSTANCE, Summoned.class);
        registration.registerEntityComponent(ServantModeProvider.INSTANCE, AbstractSpiderServant.class);
        registration.registerEntityComponent(TrainableProvider.INSTANCE, Owned.class);
        registration.registerEntityComponent(ModProfessionProvider.INSTANCE, ZombieVillagerServant.class);
        registration.registerEntityComponent(ZConvertServantProvider.INSTANCE, ZombieVillagerServant.class);
        registration.registerEntityComponent(ZConvertServantProvider.INSTANCE, AbstractZombieVindicator.class);
        registration.registerEntityComponent(ZConvertServantProvider.INSTANCE, ZombieRavager.class);
        registration.registerEntityComponent(TrainToProvider.INSTANCE, Neollager.class);

        registration.registerBlockComponent(SoulEnergyProvider.INSTANCE, ArcaBlock.class);
        registration.registerBlockComponent(SoulEnergyProvider.INSTANCE, CursedCageBlock.class);
        registration.registerBlockComponent(BlockOwnerProvider.INSTANCE, ArcaBlock.class);
        registration.registerBlockComponent(BlockOwnerProvider.INSTANCE, IceBouquetTrapBlock.class);
        registration.registerBlockComponent(BlockOwnerProvider.INSTANCE, SculkDevourerBlock.class);
        registration.registerBlockComponent(BlockOwnerProvider.INSTANCE, TrainingBlock.class);
        registration.registerBlockComponent(BlockOwnerProvider.INSTANCE, BarracksBlock.class);
    }
}
