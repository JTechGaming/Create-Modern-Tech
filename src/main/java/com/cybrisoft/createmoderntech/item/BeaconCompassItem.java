package com.cybrisoft.createmoderntech.item;

import com.cybrisoft.createmoderntech.client.ClientCompassHelper;
import com.cybrisoft.createmoderntech.registry.ModDataComponents;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

import java.util.List;

public class BeaconCompassItem extends Item {

    public BeaconCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        BeaconCompassData data = stack.get(ModDataComponents.BEACON_TARGET.get());
        if (data == null) return;

        lines.add(Component.literal(data.name())
                .withStyle(ChatFormatting.WHITE));

        lines.add(Component.literal("Color: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(data.colorName())
                        .withStyle(style -> style.withColor(data.packedRGB()))));

        lines.add(Component.literal(String.format("Target: %.0f, %.0f", data.x(), data.z()))
                .withStyle(ChatFormatting.GRAY));

        // client side only distance
        Player player = null;
        if (FMLEnvironment.dist.isClient()) {
            player = ClientCompassHelper.getClientPlayer();
        }
        if (player != null) {
            Vec3 playerPos = getProjectedPlayerPos(player);
            lines.add(Component.literal("Distance: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(data.formattedDistance(playerPos.x, playerPos.z))
                            .withStyle(ChatFormatting.AQUA)));
        }
    }

    /**
     * Compute the needle angle (0.0 - 1.0) for use as an item property.
     */
    public static float getNeedleAngle(ItemStack stack, Level level, Entity entity, int seed) {
        if (level == null || entity == null) return 0f;

        BeaconCompassData data = stack.get(ModDataComponents.BEACON_TARGET.get());
        if (data == null) return 0f;

        Vec3 playerPos = getProjectedPlayerPos((Player) (entity instanceof Player ? entity : null));
        if (playerPos == null) playerPos = entity.position();

        // World-space bearing to beacon
        double dx = data.x() - playerPos.x;
        double dz = data.z() - playerPos.z;
        double worldBearing = Math.atan2(dz, dx); // radians, east = 0

        // Player current yaw in radians (south = 0 in MC, convert to east = 0)
        float yawDeg = entity.getYRot();
        double playerYaw = Math.toRadians(yawDeg + 90); // MC yaw to standard angle

        // Sublevel rotation adjustment
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level,
                entity.blockPosition());
        if (subLevel != null) {
            Quaterniondc orientation = subLevel.logicalPose().orientation();
            // Extract yaw from quaternion
            double shipYaw = Math.atan2(
                    2.0 * (orientation.w() * orientation.y() + orientation.x() * orientation.z()),
                    1.0 - 2.0 * (orientation.y() * orientation.y() + orientation.z() * orientation.z())
            );
            playerYaw -= shipYaw;
        }

        double needleAngle = worldBearing - playerYaw;
        // Normalize to 0-1
        double normalized = (needleAngle / (2 * Math.PI)) % 1.0;
        if (normalized < 0) normalized += 1.0;
        return (float) normalized;
    }

    private static Vec3 getProjectedPlayerPos(Player player) {
        if (player == null) return null;
        Vec3 pos = player.position();
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(
                player.level(), player.blockPosition());
        if (subLevel != null) {
            pos = subLevel.logicalPose().transformPosition(pos);
        }
        return pos;
    }
}