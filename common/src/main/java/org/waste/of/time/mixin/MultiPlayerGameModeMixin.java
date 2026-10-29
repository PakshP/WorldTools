package org.waste.of.time.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.waste.of.time.Events;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "useItemOn", at = @At("HEAD"))
    public void useItemOnHead(final LocalPlayer player, final InteractionHand hand, final BlockHitResult hitResult, final CallbackInfoReturnable<InteractionResult> cir) {
        if (minecraft.level == null) return;
        Events.INSTANCE.onInteractBlock(minecraft.level, hitResult);
    }

    @Inject(method = "interact", at = @At("HEAD"))
    public void interactHead(final Player player, final Entity entity, final EntityHitResult hitResult, final InteractionHand hand, final CallbackInfoReturnable<InteractionResult> cir) {
        if (minecraft.level == null) return;
        Events.INSTANCE.onInteractEntity(entity);
    }
}
