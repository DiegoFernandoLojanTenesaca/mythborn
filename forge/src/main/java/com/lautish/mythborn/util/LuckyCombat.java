package com.lautish.mythborn.util;

import com.lautish.mythborn.init.ModConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.lautish.mythborn.MythbornMod.MODID;

public class LuckyCombat {
    private static final double ANIMAL_DAMAGE = 4.0;

    public static final int RETALIATE_COOLDOWN = 200;

    public static void addGoals(Mob mob, @Nullable LuckyTier tier) {
        if (!passiveKind(mob) || (tier != LuckyTier.ALPHA && tier != LuckyTier.TITAN)) return;
        if (mob.getGoalSelector().getAvailableGoals().stream().anyMatch(w -> w.getGoal() instanceof PickGoal)) return;
        mob.removeAllGoals(g -> g instanceof PanicGoal);
        mob.getGoalSelector().addGoal(0, new PickGoal(mob));
        mob.getGoalSelector().addGoal(1, new KickGoal(mob));
        mob.getGoalSelector().addGoal(1, new StompGoal(mob));
        mob.getGoalSelector().addGoal(2, new ChargeGoal(mob));
    }

    public static boolean passiveKind(Mob mob) {
        return mob instanceof Animal && !(mob instanceof Enemy) && mob.getType().getCategory() != MobCategory.MONSTER;
    }

    public static boolean active(Mob mob) {
        LuckyTier tier = LuckyTiers.tierOf(mob);
        return passiveKind(mob) && ModConfig.aggressiveAnimals && (tier == LuckyTier.ALPHA || tier == LuckyTier.TITAN) && !mob.isBaby()
                && mob.level().getDifficulty() != Difficulty.PEACEFUL;
    }

    public static boolean canTarget(Mob mob, @Nullable LivingEntity target) {
        if (target == null || !target.isAlive() || target == mob || target == ownerOf(mob) || target.getVehicle() == mob) return false;
        return !(target instanceof Player player) || (!player.isCreative() && !player.isSpectator());
    }

    public static boolean canStomp(Mob mob, LivingEntity e) {
        if (!canTarget(mob, e) || !mob.hasLineOfSight(e)) return false;
        if (tamed(mob)) return e == mob.getTarget() || e instanceof Enemy;
        return e instanceof Player || e == mob.getTarget();
    }

    public static @Nullable LivingEntity ownerOf(Mob mob) {
        return mob instanceof OwnableEntity ownable ? ownable.getOwner() : null;
    }

    public static boolean tamed(Mob mob) {
        return mob instanceof OwnableEntity ownable && ownable.getOwnerReference() != null;
    }

    public static void hit(Mob attacker, LivingEntity target, float knockUp, float knockBack) {
        LuckyTier tier = LuckyTiers.tierOf(attacker);
        if (tier != null) strike(attacker, target, (float) (ANIMAL_DAMAGE * tier.hostileDamage()), knockUp, knockBack);
    }

    public static boolean strike(Mob attacker, LivingEntity target, float damage, float knockUp, float knockBack) {
        if (!(attacker.level() instanceof ServerLevel level) || damage <= 0.0F) return false;
        DamageSource source = level.damageSources().mobAttack(attacker);
        if (!target.hurtServer(level, source, damage)) return false;
        target.knockback(knockBack, attacker.getX() - target.getX(), attacker.getZ() - target.getZ(), source, damage);
        target.push(0.0, knockUp, 0.0);
        level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(0.5), target.getZ(), 8, 0.3, 0.3, 0.3, 0.1);
        attacker.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
        return true;
    }

    public static boolean retaliate(Mob mob, DamageSource source, ServerLevel level) {
        LuckyTier tier = LuckyTiers.tierOf(mob);
        LivingEntity owner = ownerOf(mob);
        if (!mob.isAlive() || owner == null || source.getEntity() != owner || !ModConfig.aggressiveAnimals) return false;
        if (tier != LuckyTier.ALPHA && tier != LuckyTier.TITAN) return false;
        float damage = Math.min((float) (ANIMAL_DAMAGE * tier.hostileDamage() / 2.0), (owner.getHealth() - 1.0F) / 1.5F);
        strike(mob, owner, damage, 0.2F, 0.4F);
        return true;
    }

    public static void armor(Mob mob, LuckyTier tier) {
        int chance = tier == LuckyTier.ALPHA ? 25 : tier == LuckyTier.TITAN ? 50 : 0;
        if (chance <= 0 || !mob.getBodyArmorItem().isEmpty() || mob.getRandom().nextInt(100) >= chance) return;
        String species = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getPath();
        TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MODID, "lucky_armor/" + species));
        List<Item> items = new ArrayList<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) items.add(holder.value());
        if (tier == LuckyTier.TITAN) {
            List<Item> high = items.stream().filter(i -> {
                String path = BuiltInRegistries.ITEM.getKey(i).getPath();
                return path.contains("diamond") || path.contains("netherite");
            }).toList();
            if (!high.isEmpty()) items = new ArrayList<>(high);
        }
        if (items.isEmpty()) return;
        mob.setItemSlot(EquipmentSlot.BODY, new ItemStack(items.get(mob.getRandom().nextInt(items.size()))));
        mob.setDropChance(EquipmentSlot.BODY, 0.1F);
    }

    private static class PickGoal extends Goal {
        private final Mob mob;
        private boolean mine;

        PickGoal(Mob mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            LivingEntity current = mob.getTarget();
            if (!active(mob)) {
                if (mine) mob.setTarget(null);
                mine = false;
                return false;
            }
            if (current != null && (!canTarget(mob, current) || mob.distanceToSqr(current) > 1024.0)) {
                mob.setTarget(null);
                current = null;
            }
            LivingEntity found = tamed(mob) ? defend() : attack();
            if (found != null && found != current) {
                mob.setTarget(found);
                mine = true;
            }
            return false;
        }

        private @Nullable LivingEntity defend() {
            LivingEntity owner = ownerOf(mob);
            if (owner == null || mob instanceof TamableAnimal tamable && tamable.isOrderedToSit()) return null;
            LivingEntity victim = owner.getLastHurtMob();
            LivingEntity attacker = owner.getLastHurtByMob();
            if (canTarget(mob, attacker) && owner.tickCount - owner.getLastHurtByMobTimestamp() < 100) return attacker;
            if (canTarget(mob, victim) && owner.tickCount - owner.getLastHurtMobTimestamp() < 100) return victim;
            return null;
        }

        private @Nullable LivingEntity attack() {
            LivingEntity hurtBy = mob.getLastHurtByMob();
            if (canTarget(mob, hurtBy) && mob.tickCount - mob.getLastHurtByMobTimestamp() < 200) return hurtBy;
            Player near = mob.level().getNearestPlayer(mob.getX(), mob.getY(), mob.getZ(), 12.0, true);
            return canTarget(mob, near) ? near : null;
        }
    }
}
