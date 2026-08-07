package io.github.mc306798revert.mixin;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Reverts the MC-306798 fix (fixed in 26.1 Pre-Release 2).
 *
 * <p>Vanilla 26.1-pre-2+ removes an ender dragon once {@code dragonDeathTime >= 200}.
 * Before the fix, the check was {@code dragonDeathTime == 200}, so a dragon summoned
 * with {@code DragonDeathTime: 200} (or greater) never hit exactly 200 after the
 * per-tick increment and therefore never disappeared while continuing to drop XP.
 *
 * <p>This mixin makes the final "remove dragon" block in {@code tickDeath} run only
 * when {@code dragonDeathTime == 200}, restoring the pre-fix behaviour.
 */
@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin {
	/**
	 * {@code tickDeath()} contains three {@code level()} invocations; the last one
	 * (ordinal 2) is the {@code this.level()} call inside
	 * {@code if (this.dragonDeathTime >= 200 && this.level() instanceof ServerLevel)}.
	 *
	 * <p>Returning {@code null} makes the {@code instanceof} check fail, so the whole
	 * removal block is skipped unless {@code dragonDeathTime} is exactly 200.
	 */
	@Redirect(
		method = "tickDeath",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;level()Lnet/minecraft/world/level/Level;",
			ordinal = 2
		)
	)
	private static Level mc306798revert$revertDragonDeathFix(EnderDragon dragon) {
		return dragon.dragonDeathTime == 200 ? dragon.level() : null;
	}
}