package com.lautish.alphamobs.util;

import com.lautish.alphamobs.init.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LuckyBossBars {
    public static void tick(ServerLevel level, Map<UUID, ServerBossEvent> bars) {
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
        bars.values().removeIf(bar -> {
            if (mobs.containsKey(bar.getId())) return false;
            bar.removeAllPlayers();
            return true;
        });
        mobs.forEach((id, mob) -> {
            ServerBossEvent event = bars.computeIfAbsent(id, k -> new ServerBossEvent(id,
                    Component.translatableWithFallback("alphamobs.name.titan", "Titan %s", mob.getType().getDescription()),
                    BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS));
            event.setProgress(Mth.clamp(mob.getHealth() / mob.getMaxHealth(), 0.0F, 1.0F));
            Set<ServerPlayer> in = near.get(id);
            for (ServerPlayer p : Set.copyOf(event.getPlayers())) if (!in.contains(p)) event.removePlayer(p);
            in.forEach(event::addPlayer);
        });
    }

    private static boolean qualifies(Mob mob) {
        if (!mob.isAlive() || LuckyTiers.tierOf(mob) != LuckyTier.TITAN) return false;
        if (mob.getType().getCategory() == MobCategory.MONSTER) return true;
        return LuckyCombat.active(mob) && !LuckyCombat.tamed(mob);
    }
}
