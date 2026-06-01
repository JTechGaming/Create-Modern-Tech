package com.cybrisoft.createmoderntech.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BeaconCompassData(float x, float z, int color, String name) {

    public static final Codec<BeaconCompassData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("x").forGetter(BeaconCompassData::x),
                    Codec.FLOAT.fieldOf("z").forGetter(BeaconCompassData::z),
                    Codec.INT.fieldOf("color").forGetter(BeaconCompassData::color),
                    Codec.STRING.optionalFieldOf("name", "Beacon").forGetter(BeaconCompassData::name)
            ).apply(instance, BeaconCompassData::new));

    public static final StreamCodec<io.netty.buffer.ByteBuf, BeaconCompassData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, BeaconCompassData::x,
                    ByteBufCodecs.FLOAT, BeaconCompassData::z,
                    ByteBufCodecs.INT, BeaconCompassData::color,
                    ByteBufCodecs.STRING_UTF8, BeaconCompassData::name,
                    BeaconCompassData::new);

    public float r() { return ((color >> 16) & 0xFF) / 255f; }
    public float g() { return ((color >> 8)  & 0xFF) / 255f; }
    public float b() { return  (color        & 0xFF) / 255f; }

    public int packedRGB() { return color & 0xFFFFFF; }

    /** Derive a display name from the packed color */
    public String colorName() {
        // Map common packed colors to names
        if (color == 0xFF4444) return "Red";
        if (color == 0x44FF44) return "Green";
        if (color == 0x4444FF) return "Blue";
        if (color == 0xFFFF44) return "Yellow";
        if (color == 0xFF44FF) return "Magenta";
        if (color == 0x44FFFF) return "Cyan";
        if (color == 0xFF8844) return "Orange";
        if (color == 0xFFFFFF) return "White";
        return String.format("#%06X", color & 0xFFFFFF);
    }

    /** Formatted distance string from a world position */
    public String formattedDistance(double worldX, double worldZ) {
        double dx = x - worldX;
        double dz = z - worldZ;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist >= 1000) {
            return String.format("%.1fkm", dist / 1000.0);
        }
        return String.format("%.0fm", dist);
    }
}