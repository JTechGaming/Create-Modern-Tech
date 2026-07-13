package com.cybrisoft.createmoderntech.util;

import com.cybrisoft.createmoderntech.ui.TriggerVariableListWidget;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TriggerVariableEntry {
    private Direction direction;
    private String    value;
    private String    blockId;

    public TriggerVariableEntry(Direction direction, String value, String blockId) {
        this.direction = direction;
        this.value = value;
        this.blockId = blockId;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public static final StreamCodec<ByteBuf, TriggerVariableEntry> STREAM_CODEC = new StreamCodec<ByteBuf, TriggerVariableEntry>() {
        @Override
        public TriggerVariableEntry decode(ByteBuf buf) {
            Direction dir = Direction.from3DDataValue(buf.readInt());
            String value = ByteBufCodecs.STRING_UTF8.decode(buf);
            String blockId = ByteBufCodecs.STRING_UTF8.decode(buf);

            return new TriggerVariableEntry(dir, value, blockId);
        }

        @Override
        public void encode(ByteBuf buf, TriggerVariableEntry entry) {
            buf.writeInt(entry.getDirection().get3DDataValue());
            ByteBufCodecs.STRING_UTF8.encode(buf, entry.value);
            ByteBufCodecs.STRING_UTF8.encode(buf, entry.blockId);
        }
    };

    public static final StreamCodec<ByteBuf, List<TriggerVariableEntry>> LIST_CODEC = new StreamCodec<ByteBuf, List<TriggerVariableEntry>>() {
        @Override
        public List<TriggerVariableEntry> decode(ByteBuf buf) {
            int size = buf.readInt();
            List<TriggerVariableEntry> entries = new ArrayList<>();
            for (int i=0; i<size; i++) {
                entries.add(STREAM_CODEC.decode(buf));
            }
            return entries;
        }

        @Override
        public void encode(ByteBuf buf, List<TriggerVariableEntry> entries) {
            buf.writeInt(entries.size());
            for (TriggerVariableEntry entry : entries) {
                STREAM_CODEC.encode(buf, entry);
            }
        }
    };
}
