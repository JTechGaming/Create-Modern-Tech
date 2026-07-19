package com.cybrisoft.createmoderntech.datagen;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.registry.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateModernTech.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}
