package com.lyra.addon.modules;

import com.lyra.addon.Addon;
import com.lyra.addon.utils.CreativeSetItem;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.PlaySoundEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

public class NetherTravel extends Module {
    private final SettingGroup sgGeneral;

    public NetherTravel() {
        super(AutoplayAddon.autoplay, "NetherTravel", "Bypasses Plutoren's Fix for NetherTravel.");

        this.sgGeneral = this.settings.getDefaultGroup();

        this.pos = this.sgGeneral.add(new BlockPosSetting.Builder()
            .name("Destination")
            .description("The location of the waypoint.")
            .defaultValue(class_2338.field_10980)
            .build());

        this.blocks = this.sgGeneral.add(new IntSetting.Builder()
            .name("Distance between tps")
            .description("How much distance in each TP?")
            .defaultValue(120)
            .min(0)
            .sliderMax(300)
            .build());
    }

    public final Setting<class_2338> pos;
    private final Setting<Integer> blocks;

    @EventHandler
    public void onTick(TickEvent.Post event) {
        for (Waypoint waypoint : Waypoints.get()) {
            waypoint.getPos();
        }

        class_243 tickpos = ServerSideValues.tickpos;
        if (tickpos.field_1351 < 128.0D) {
            simpletp(tickpos, new class_243(tickpos.field_1352, 128.5D, tickpos.field_1350));
        }
        class_243 newpos = ServerSideValues.serversidedposition;
        class_243 temp1 = new class_243(this.pos.get().method_10263() - newpos.field_1352, 0.0D, this.pos.get().method_10260() - newpos.field_1350).method_1029().method_1021(this.blocks.get());
        double xpos = newpos.field_1352 + temp1.field_1352;
        double zpos = newpos.field_1350 + temp1.field_1350;
        class_243 temp2 = new class_243(xpos, 128.5D, zpos);
        simpletp(newpos, temp2);

        class_2338 destination = this.pos.get();
        class_243 currentPos = ServerSideValues.serversidedposition;
        double distance = Math.sqrt(Math.pow(destination.method_10263() - currentPos.field_1352, 2.0D) + Math.pow(destination.method_10260() - currentPos.field_1350, 2.0D));

        if (distance <= 5.0D) {
            this.pos.set(new class_2338(0, 0, 0));
            ChatUtils.info("You have arrived at your destination!", new Object[0]);
            Module flight = Modules.get().get(Flight.class);
            if (!flight.isActive()) {
                flight.toggle();
            }
            toggle();
        }
    }

    @EventHandler(priority = 201)
    private void onSendPacket(PacketEvent.Send event) {
        if (event.packet instanceof class_2828.class_2829 || event.packet instanceof class_2828.class_2830 || event.packet instanceof class_2828.class_2831 || event.packet instanceof class_2828.class_5911) {
            event.cancel();
        }
    }

    private void simpletp(class_243 currentpos, class_243 pos) {
        double distance = currentpos.method_1022(pos);
        int packetsRequired = (int) Math.ceil(Math.abs(distance / 10.0D)) - 1;
        PacketUtils.packetQueue.clear();
        for (int packetNumber = 0; packetNumber < packetsRequired; packetNumber++) {
            class_2828.class_5911 class_5911 = new class_2828.class_5911(true);
            PacketUtils.packetQueue.add(class_5911);
        }
        class_2828.class_2829 class_2829 = new class_2828.class_2829(pos.field_1352, pos.field_1351, pos.field_1350, true);
        PacketUtils.packetQueue.add(class_2829);
        PacketUtils.sendAllPacketsInQueue();
        ServerSideValues.serversidedposition = pos;
    }
}

