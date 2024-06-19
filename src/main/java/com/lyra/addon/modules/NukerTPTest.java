package com.lyra.addon.modules;

import com.lyra.addon.Addon;
import com.lyra.addon.utils.Uwuify;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class TeleportNuker extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> areaSize = sgGeneral.add(new IntSetting.Builder()
        .name("area-size")
        .description("Size of the area to nuke.")
        .defaultValue(10)
        .min(1)
        .sliderMax(50)
        .build()
    );

    private final Setting<Integer> interval = sgGeneral.add(new IntSetting.Builder()
        .name("interval")
        .description("Interval between teleportations in ticks.")
        .defaultValue(20)
        .min(1)
        .sliderMax(100)
        .build()
    );

    private int tickCounter;

    public TeleportNuker() {
        super(Addon.CATEGORY, "teleport-nuker", "Teleports around and nukes large areas.");
    }

    @Override
    public void onActivate() {
        tickCounter = 0;
    }

    @Override
    public void onDeactivate() {
        // Cleanup or reset any necessary state here
    }

    @Override
    public void onTick() {
        if (++tickCounter >= interval.get()) {
            tickCounter = 0;
            teleportAndNuke();
        }
    }

    private void teleportAndNuke() {
        BlockPos playerPos = mc.player.getBlockPos();
        for (int x = -areaSize.get(); x <= areaSize.get(); x++) {
            for (int y = -areaSize.get(); y <= areaSize.get(); y++) {
                for (int z = -areaSize.get(); z <= areaSize.get(); z++) {
                    BlockPos targetPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(targetPos).getBlock() != Blocks.AIR) {
                        teleportTo(targetPos);
                        nuke(targetPos);
                    }
                }
            }
        }
    }

    private void teleportTo(BlockPos pos) {
        mc.player.setPosition(Vec3d.ofCenter(pos));
    }

    private void nuke(BlockPos pos) {
        BlockIterator.register(pos, pos.add(1, 1, 1), (blockPos, state) -> {
            if (state.getBlock() != Blocks.AIR) {
                BlockUtils.breakBlock(blockPos, true);
            }
        });
    }
}
