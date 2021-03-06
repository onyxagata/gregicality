package gregicadditions.machines.multi.override;

import gregicadditions.item.GAMetaBlocks;
import gregicadditions.machines.multi.simple.LargeSimpleRecipeMapMultiblockController;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.multiblock.PatternMatchContext;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.unification.material.Materials;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.metatileentities.multi.electric.MetaTileEntityElectricBlastFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

import static gregtech.api.unification.material.Materials.StainlessSteel;

public class MetaTileEntityCrackingUnit extends gregtech.common.metatileentities.multi.electric.MetaTileEntityCrackingUnit {

    private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {
            MultiblockAbility.IMPORT_FLUIDS, MultiblockAbility.EXPORT_FLUIDS,
            MultiblockAbility.INPUT_ENERGY
    };

    protected int heatingCoilLevel = 1;
    protected int heatingCoilDiscount = 1;

    public MetaTileEntityCrackingUnit(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.recipeMapWorkable = new CrackingUnitWorkable(this);
    }

    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new MetaTileEntityCrackingUnit(this.metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        BlockWireCoil.CoilType coilType = context.getOrDefault("CoilType", BlockWireCoil.CoilType.CUPRONICKEL);
        this.heatingCoilLevel = coilType.getLevel();
        this.heatingCoilDiscount = coilType.getEnergyDiscount();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.heatingCoilLevel = 1;
        this.heatingCoilDiscount = 1;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("gregtech.multiblock.multi_furnace.heating_coil_level", heatingCoilLevel));
            textList.add(new TextComponentTranslation("gregtech.multiblock.multi_furnace.heating_coil_discount", heatingCoilDiscount));
        }
        super.addDisplayText(textList);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("HCHCH", "HCHCH", "HCHCH")
                .aisle("HCHCH", "H###H", "HCHCH")
                .aisle("HCHCH", "HCOCH", "HCHCH")
                .setAmountAtLeast('L', 20)
                .where('O', selfPredicate())
                .where('L', statePredicate(getCasingState()))
                .where('H', statePredicate(getCasingState()).or(abilityPartPredicate(ALLOWED_ABILITIES)))
                .where('#', isAirPredicate())
                .where('C', MetaTileEntityElectricBlastFurnace.heatingCoilPredicate())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GAMetaBlocks.METAL_CASING.get(Materials.StainlessSteel);
    }

    @Override
    public IBlockState getCasingState() {
        return GAMetaBlocks.getMetalCasingBlockState(StainlessSteel);
    }

    protected class CrackingUnitWorkable extends LargeSimpleRecipeMapMultiblockController.LargeSimpleMultiblockRecipeLogic {

        public CrackingUnitWorkable(RecipeMapMultiblockController tileEntity) {
            super(tileEntity, 100 / heatingCoilDiscount, 100, 100, heatingCoilLevel);
        }

    }
}
