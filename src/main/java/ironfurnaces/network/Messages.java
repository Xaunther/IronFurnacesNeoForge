package ironfurnaces.network;

import ironfurnaces.IronFurnaces;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;


public class Messages {


    @SubscribeEvent
    public static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(IronFurnaces.MOD_ID)
                .versioned("1.0")
                .optional();
        registrar.playToServer(PacketFurnaceSettings.TYPE, PacketFurnaceSettings.CODEC, PacketFurnaceSettings::handle);
        registrar.playToServer(PacketShowConfig.TYPE, PacketShowConfig.CODEC, PacketShowConfig::handle);

    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.sendToServer(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }


}
