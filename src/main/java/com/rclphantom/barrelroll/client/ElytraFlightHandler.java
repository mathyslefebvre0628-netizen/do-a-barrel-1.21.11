package com.rclphantom.barrelroll.client;

import com.rclphantom.barrelroll.BarrelRollMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = BarrelRollMod.MOD_ID, value = Dist.CLIENT)
public class ElytraFlightHandler {

    // Roll visuel affiché à la caméra (interpolé)
    public static float currentRoll = 0f;
    // Roll cible (ce vers quoi on tend)
    private static float targetRoll = 0f;

    // Tuning
    private static final float YAW_SPEED             = 5.0f;
    private static final float BANK_FACTOR           = 2.5f;
    private static final float ROLL_LERP             = 0.18f;
    private static final float ROLL_DECAY            = 0.82f;
    private static final float MAX_ROLL              = 180f;
    private static final float MOUSE_ROLL_SENSITIVITY = 0.6f;

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (isFlying()) {
            event.setRoll(currentRoll);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != player) return;

        if (!player.isFallFlying()) {
            targetRoll  *= 0.6f;
            currentRoll  = Mth.lerp(ROLL_LERP * 2f, currentRoll, targetRoll);
            if (Math.abs(currentRoll) < 0.05f) { currentRoll = 0f; targetRoll = 0f; }
            return;
        }

        Options options = mc.options;
        boolean leftDown  = options.keyLeft.isDown();
        boolean rightDown = options.keyRight.isDown();

        if (leftDown && !rightDown) {
            player.setYRot(player.getYRot() - YAW_SPEED);
            targetRoll -= BANK_FACTOR;
        } else if (rightDown && !leftDown) {
            player.setYRot(player.getYRot() + YAW_SPEED);
            targetRoll += BANK_FACTOR;
        } else {
            targetRoll *= ROLL_DECAY;
            if (Math.abs(targetRoll) < 0.05f) targetRoll = 0f;
        }

        targetRoll  = Mth.clamp(targetRoll, -MAX_ROLL, MAX_ROLL);
        currentRoll = Mth.lerp(ROLL_LERP, currentRoll, targetRoll);
    }

    public static void handleMouseInput(LocalPlayer player, double rawYaw, double rawPitch) {
        targetRoll += (float)(rawYaw * MOUSE_ROLL_SENSITIVITY);
        targetRoll  = Mth.clamp(targetRoll, -MAX_ROLL, MAX_ROLL);

        float rollRad       = (float) Math.toRadians(currentRoll);
        double worldPitch   = rawPitch * Math.cos(rollRad);
        double yawFromPitch = rawPitch * Math.sin(rollRad);

        player.turn(yawFromPitch, worldPitch);
    }

    public static boolean isFlying() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.player.isFallFlying();
    }
}
