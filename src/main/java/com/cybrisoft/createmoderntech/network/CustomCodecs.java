package com.cybrisoft.createmoderntech.network;

import com.cybrisoft.createmoderntech.util.TriggerVariableEntry;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class CustomCodecs {
    public static final StreamCodec<ByteBuf, Vec3> VEC3_CODEC = new StreamCodec<ByteBuf, Vec3>() {
        @Override
        public Vec3 decode(ByteBuf buf) {
            return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        @Override
        public void encode(ByteBuf buf, Vec3 entry) {
            buf.writeDouble(entry.x);
            buf.writeDouble(entry.y);
            buf.writeDouble(entry.z);
        }
    };
}
