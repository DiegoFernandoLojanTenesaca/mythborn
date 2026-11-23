package com.lautish.luckymobs.util;

import com.lautish.luckymobs.init.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Animal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LuckyBossBars {
    private record Bar(ServerLevel level, ServerBossEvent event) {
    }

    private static final Map<UUID, Bar> BARS = new HashMap<>();

    public static void tick(ServerLevel level) {
        if (level.getGameTime() % 5 != 0) return;
        Map<UUID, Set<ServerPlayer>> near = new HashMap<>();
        Map<UUID, Mob> mobs = new HashMap<>();
        if (ModConfig.bossBar) {
            for (ServerPlayer player : level.players()) {
                for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(32.0), LuckyBossBars::qualifies)) {
                    if (mob.distanceToSqr(player) > 1024.0) continue;
                    mobs.put(mob.getUUID(), mob);
                    near.computeIfAbsent(mob.getUUID(), k -> new HashSet<>()).add(player);
                }
            }
        }
        BARS.values().removeIf(bar -> {
            if (bar.level != level || mobs.containsKey(bar.event.getId())) return false;
            bar.event.removeAllPlayers();
            return true;
        });
        mobs.forEach((id, mob) -> {
            ServerBossEvent event = BARS.computeIfAbsent(id, k -> new Bar(level, new ServerBossEvent(id,
                    Component.translatableWithFallback("luckymobs.name.titan", "Titan %s", mob.getType().getDescription()),
                    BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS))).event;
            event.setProgress(Mth.clamp(mob.getHealth() / mob.getMaxHealth(), 0.0F, 1.0F));
            Set<ServerPlayer> in = near.get(id);
            for (ServerPlayer p : Set.copyOf(event.getPlayers())) if (!in.contains(p)) event.removePlayer(p);
            in.forEach(event::addPlayer);
        });
    }

    private static boolean qualifies(Mob mob) {
        if (!mob.isAlive() || LuckyTiers.tierOf(mob) != LuckyTier.TITAN) return false;
        if (mob.getType().getCategory() == MobCategory.MONSTER) return true;
        return mob instanceof Animal && ModConfig.aggressiveAnimals && !mob.isBaby() && !LuckyCombat.tamed(mob);
    }
}
