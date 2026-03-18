package com.rclphantom.barrelroll;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(BarrelRollMod.MOD_ID)
public class BarrelRollMod {

    public static final String MOD_ID = "barrelroll";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BarrelRollMod(IEventBus modEventBus) {
        LOGGER.info("Do a Barrel Roll mod chargé !");
    }
}
