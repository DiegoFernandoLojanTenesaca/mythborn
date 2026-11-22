package com.lautish.luckymobs.init.mixin;

import com.lautish.luckymobs.util.LuckyTier;
import com.lautish.luckymobs.util.LuckyTiers;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(Commands.class)
public abstract class CommandsMixin {

    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void registerLucky(Commands.CommandSelection selection, CommandBuildContext context, CallbackInfo ci) {
        var targets = Commands.argument("targets", EntityArgument.entities()).executes(c -> give(c, LuckyTier.LUCKY));
        for (LuckyTier tier : LuckyTier.values()) {
            targets.then(Commands.literal(tier.name().toLowerCase(Locale.ROOT)).executes(c -> give(c, tier)));
        }
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("lucky").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));
        dispatcher.register(root.then(targets));
    }

    @Unique
    private static int give(CommandContext<CommandSourceStack> c, LuckyTier tier) throws CommandSyntaxException {
        int changed = 0;
        for (Entity entity : EntityArgument.getEntities(c, "targets")) {
            if (entity instanceof Mob mob) {
                LuckyTiers.apply(mob, tier);
                changed++;
            }
        }
        int count = changed;
        c.getSource().sendSuccess(() -> Component.translatableWithFallback("commands.luckymobs.lucky.success", "Gave a level to %s mobs", count), true);
        return changed;
    }
}
