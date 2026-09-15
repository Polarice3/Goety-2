package com.Polarice3.Goety.common.world.structures;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;

public class ModStructurePieces {
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE = DeferredRegister.create(Registries.STRUCTURE_PIECE, Goety.MOD_ID);

    public static RegistryObject<StructurePieceType> WRECKED_MANSION_PIECE = setTemplatePieceId(WreckedMansionPieces.MansionPiece::new, "wrecked_mansion_piece");

    private static RegistryObject<StructurePieceType> setFullContextPieceId(StructurePieceType type, String name) {
        return STRUCTURE_PIECE.register(name.toLowerCase(Locale.ROOT), () -> type);
    }

    private static RegistryObject<StructurePieceType> setPieceId(StructurePieceType.ContextlessType piece, String name) {
        return setFullContextPieceId(piece, name);
    }

    private static RegistryObject<StructurePieceType> setTemplatePieceId(StructurePieceType.StructureTemplateType piece, String name) {
        return setFullContextPieceId(piece, name);
    }


}
