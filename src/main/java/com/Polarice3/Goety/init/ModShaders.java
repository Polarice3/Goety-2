package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModShaders {

    private static ShaderInstance holeShader;
    private static ShaderInstance enchantBeamShader;
    private static ShaderInstance enchantSwirlShader;
    private static ShaderInstance chargeAuraShader;
    private static ShaderInstance soulParticleShader;
    private static ShaderInstance ditherSmokeShader;

    @SubscribeEvent
    static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        Goety.location("hole"),
                        DefaultVertexFormat.POSITION),
                shader -> holeShader = shader
        );
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        Goety.location("enchant_beam"),
                        DefaultVertexFormat.POSITION_COLOR_TEX),
                shader -> enchantBeamShader = shader
        );
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        Goety.location("enchant_swirl"),
                        DefaultVertexFormat.NEW_ENTITY),
                shader -> enchantSwirlShader = shader
        );
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        Goety.location("charge_aura"),
                        DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
                shader -> chargeAuraShader = shader);
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        Goety.location("soul_particle"),
                        DefaultVertexFormat.PARTICLE),
                shader -> soulParticleShader = shader
        );
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        Goety.location("dither_smoke"),
                        DefaultVertexFormat.PARTICLE),
                shader -> ditherSmokeShader = shader
        );
    }

    public static ShaderInstance getHoleShader() {
        return holeShader;
    }

    public static ShaderInstance getEnchantBeamShader() {
        return enchantBeamShader;
    }

    public static ShaderInstance getEnchantSwirlShader() {
        return enchantSwirlShader;
    }

    public static ShaderInstance getChargeAuraShader() {
        return chargeAuraShader;
    }

    public static ShaderInstance getSoulParticleShader() {
        return soulParticleShader;
    }

    public static ShaderInstance getDitherSmokeShader() {
        return ditherSmokeShader;
    }
}
