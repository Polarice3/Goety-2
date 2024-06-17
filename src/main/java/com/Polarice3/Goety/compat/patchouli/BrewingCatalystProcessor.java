package com.Polarice3.Goety.compat.patchouli;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffects;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class BrewingCatalystProcessor implements IComponentProcessor {
    protected BrewEffect brewEffect;
    private String extraText = "";

    @Override
    public void setup(IVariableProvider variables) {
        String effectId = variables.get("recipe").asString();
        this.brewEffect = new BrewEffects().getBrewEffect(effectId);
        if (variables.has("text")) {
            this.extraText = variables.get("text").asString();
        }
    }

    @Override
    public IVariable process(String key) {
        if (this.brewEffect == null)
            return IVariable.empty();

        if (key.startsWith("input")) {
            ItemStack itemStack = new BrewEffects().getCatalystFromEffect(this.brewEffect.getEffectID());
            return IVariable.from(itemStack);
        }

        if (key.startsWith("capacityExtra")) {
            if (this.brewEffect.getCapacityExtra() > 0) {
                return IVariable.wrap(I18n.get("jei.goety.capacityUse", this.brewEffect.getCapacityExtra()));
            }
        }

        if (key.startsWith("soulCost")) {
            return IVariable.wrap(I18n.get("jei.goety.single.soulcost", this.brewEffect.getSoulCost()));
        }

        if (key.startsWith("duration")) {
            if (this.brewEffect.getDuration() > 40) {
                return IVariable.wrap(I18n.get("jei.goety.single.duration", StringUtil.formatTickDuration(this.brewEffect.getDuration())));
            } else {
                return IVariable.wrap(I18n.get("jei.goety.instant.duration"));
            }
        }

        if (key.startsWith("linger")){
            if (!this.brewEffect.canLinger()){
                return IVariable.wrap(I18n.get("jei.goety.linger"));
            }
        }

        if (key.startsWith("output")) {
            return IVariable.wrap(this.brewEffect.getDescriptionId());
        }

        if (key.startsWith("text")){
            return IVariable.wrap(this.extraText);
        }

        return IVariable.empty();
    }
}
