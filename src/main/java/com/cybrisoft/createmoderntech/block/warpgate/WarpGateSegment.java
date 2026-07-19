package com.cybrisoft.createmoderntech.block.warpgate;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum WarpGateSegment implements StringRepresentable {
    VERTICAL("v"),
    HORIZONTAL_X("h_x"),
    HORIZONTAL_Z("h_z"),
    DIAGONAL_X_UP("d_x_up"),
    DIAGONAL_X_DOWN("d_x_down"),
    DIAGONAL_Z_UP("d_z_up"),
    DIAGONAL_Z_DOWN("d_z_down");

    final String name;

    WarpGateSegment(String name) {
        this.name = name;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }
}
