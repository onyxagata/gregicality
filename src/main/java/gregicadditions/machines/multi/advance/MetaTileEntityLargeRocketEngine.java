package gregicadditions.machines.multi.advance;

import gregicadditions.GAMaterials;
import gregicadditions.item.GAMetaBlocks;
import gregicadditions.recipes.GARecipeMaps;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.FuelRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.unification.material.Materials;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.multi.electric.generator.FueledMultiblockController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

import static gregicadditions.GAMaterials.Nitinol60;
import static gregtech.api.multiblock.BlockPattern.RelativeDirection.*;

public class MetaTileEntityLargeRocketEngine extends FueledMultiblockController {


    public MetaTileEntityLargeRocketEngine(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GARecipeMaps.ROCKET_FUEL_RECIPES, GTValues.V[GTValues.EV]);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new MetaTileEntityLargeRocketEngine(metaTileEntityId);
    }

    @Override
    protected FuelRecipeLogic createWorkable(long maxVoltage) {
        return new RocketEngineWorkableHandler(this, recipeMap, () -> energyContainer, () -> importFluidHandler, maxVoltage);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        if (isStructureFormed()) {
            FluidStack carbonDioxide = importFluidHandler.drain(Materials.CarbonDioxide.getFluid(Integer.MAX_VALUE), false);
            FluidStack hydrogen = importFluidHandler.drain(GAMaterials.LiquidHydrogen.getFluid(Integer.MAX_VALUE), false);
            FluidStack air = importFluidHandler.drain(Materials.Air.getFluid(Integer.MAX_VALUE), false);
            FluidStack fuelStack = ((RocketEngineWorkableHandler) workableHandler).getFuelStack();
            int hydrogenNeededToBoost = ((RocketEngineWorkableHandler) workableHandler).getHydrogenNeededToBoost();
            boolean isBoosted = ((RocketEngineWorkableHandler) workableHandler).isUsingHydrogen();
            int carbonDioxideAmount = carbonDioxide == null ? 0 : carbonDioxide.amount;
            int hydrogenAmount = hydrogen == null ? 0 : hydrogen.amount;
            int airAmount = air == null ? 0 : air.amount;
            int fuelAmount = fuelStack == null ? 0 : fuelStack.amount;

            textList.add(new TextComponentTranslation("gregtech.multiblock.universal.carbon_dioxide_amount", carbonDioxideAmount));
            textList.add(new TextComponentString(fuelStack != null ? String.format("%dmb %s", fuelAmount, fuelStack.getLocalizedName()) : ""));
            textList.add(new TextComponentTranslation("gregtech.multiblock.universal.liquid_hydrogen_amount", hydrogenAmount));
            textList.add(new TextComponentTranslation("gregtech.multiblock.universal.air_amount", airAmount));
            textList.add(new TextComponentTranslation("gregtech.multiblock.large_rocket_engine.hydrogen_need", hydrogenNeededToBoost));
            textList.add(new TextComponentTranslation(isBoosted ? "gregtech.multiblock.large_rocket_engine.boost" : "").setStyle(new Style().setColor(TextFormatting.GREEN)));
        }
        super.addDisplayText(textList);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtadditions.multiblock.large_rocket_engine.tooltip.1"));
        tooltip.add(I18n.format("gtadditions.multiblock.large_rocket_engine.tooltip.2"));
        tooltip.add(I18n.format("gtadditions.multiblock.large_rocket_engine.tooltip.3"));
        tooltip.add(I18n.format("gtadditions.multiblock.large_rocket_engine.tooltip.4"));
        tooltip.add(I18n.format("gtadditions.multiblock.large_rocket_engine.tooltip.5"));
        tooltip.add(I18n.format("gtadditions.multiblock.large_rocket_engine.tooltip.6"));
        tooltip.add(I18n.format("gtadditions.multiblock.large_rocket_engine.tooltip.7"));
        tooltip.add(I18n.format("gtadditions.multiblock.large_rocket_engine.tooltip.8"));
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(LEFT, DOWN, BACK)
                .aisle("CCC", "CEC", "CCC")
                .aisle("CAC", "F#F", "CCC").setRepeatable(8)
                .aisle("CCC", "CSC", "CCC")
                .where('S', selfPredicate())
                .where('C', statePredicate(getCasingState()))
                .where('E', statePredicate(getCasingState()).or(abilityPartPredicate(MultiblockAbility.OUTPUT_ENERGY)))
                .where('F', statePredicate(getCasingState()).or(abilityPartPredicate(MultiblockAbility.IMPORT_FLUIDS)))
                .where('A', statePredicate(MetaBlocks.MUTLIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.ENGINE_INTAKE_CASING)))
                .where('#', isAirPredicate())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GAMetaBlocks.METAL_CASING.get(Nitinol60);
    }

    protected IBlockState getCasingState() {
        return GAMetaBlocks.getMetalCasingBlockState(Nitinol60);
    }

}
