package dev.willowv.girltimedata.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes Supplementaries (3.7.2 - 3.9.9) ZombieHorseMixin#readAdditionalSaveData, which calls
 * this.addAdditionalSaveData(tag) instead of super.readAdditionalSaveData(tag). As a result zombie
 * horses never load their LivingEntity/Mob/AbstractHorse data (Health, Tame, Owner, SaddleItem,
 * attributes, ...) from /summon NBT or from disk. Fixed upstream in Supplementaries 4.0.0.
 * <p>
 * Priority must be above Supplementaries' (default 1000) so this applies after its method is merged.
 * require = 0 so this is a no-op if Supplementaries is absent or fixed.
 */
@Mixin(value = ZombieHorse.class, priority = 1500)
public abstract class ZombieHorseLoadFixMixin extends AbstractHorse {

    protected ZombieHorseLoadFixMixin(EntityType<? extends AbstractHorse> type, Level level) {
        super(type, level);
    }

    // Minecraft Dev plugin is unaware of mixins from other mods, so suppressing warning.
    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Redirect(
            method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/horse/ZombieHorse;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
            ),
            require = 0
    )
    private void girltimedata$readSuperInsteadOfWrite(ZombieHorse self, CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }
}
