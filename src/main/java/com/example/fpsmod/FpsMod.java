package com.example.fpsmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FpsMod implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("fps")
                        .executes(ctx -> run(ctx.getSource()))));
    }

    private static int run(ServerCommandSource source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayerEntity self = source.getPlayerOrThrow();
        int count = 0;

        for (ServerPlayerEntity other : source.getServer().getPlayerManager().getPlayerList()) {
            if (other == self) continue;
            count++;

            MutableText msg = Text.literal(other.getName().getString() + " -> ").formatted(Formatting.WHITE);

            // Coordinates (Yellow)
            String coords = (int) other.getX() + ", " + (int) other.getY() + ", " + (int) other.getZ();
            msg.append(Text.literal(coords).formatted(Formatting.YELLOW));
            msg.append(Text.literal(" | ").formatted(Formatting.WHITE));

            if (other.getEntityWorld() != self.getEntityWorld()) {
                msg.append(Text.literal("Dusre dimension mein").formatted(Formatting.GRAY));
            } else {
                // Distance (Red)
                int dist = (int) self.distanceTo(other);
                msg.append(Text.literal(dist + "M").formatted(Formatting.RED));
                msg.append(Text.literal(" | ").formatted(Formatting.WHITE));

                // Direction (Black)
                msg.append(Text.literal(direction(self, other)).formatted(Formatting.BLACK));
            }

            self.sendMessage(msg, false);
        }

        if (count == 0) {
            self.sendMessage(Text.literal("Server mein koi aur player nahi hai.").formatted(Formatting.GRAY), false);
        }
        return count;
    }

    // East = +X, South = +Z, West = -X, North = -Z
    private static String direction(ServerPlayerEntity self, ServerPlayerEntity other) {
        double dx = other.getX() - self.getX();
        double dz = other.getZ() - self.getZ();
        if (Math.abs(dx) > Math.abs(dz)) {
            return dx > 0 ? "East" : "West";
        }
        return dz > 0 ? "South" : "North";
    }
}
