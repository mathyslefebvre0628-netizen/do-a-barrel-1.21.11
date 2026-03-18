package com.rclphantom.barrelroll.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.rclphantom.barrelroll.client.ElytraFlightHandler;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @WrapOperation(
        method = "turnPlayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;turn(DD)V"
        )
    )
    private void wrapTurnPlayer(Entity entity, double yaw, double pitch, Operation<Void> original) {
        if (entity instanceof LocalPlayer player && ElytraFlightHandler.isFlying()) {
            ElytraFlightHandler.handleMouseInput(player, yaw, pitch);
        } else {
            original.call(entity, yaw, pitch);
        }
    }
}
