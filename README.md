![AI Declaration](https://img.shields.io/badge/AI%20Declaration-DeepSeek%20%26%20Codex-blue)
[![English](https://img.shields.io/badge/English-README-blue)](README.md)
[![中文](https://img.shields.io/badge/中文-切换-red)](README.zh.md)
# Unfix MC-306798

A **Fabric mod** for Minecraft **Java Edition 26.2** that reverts the fix for [MC-306798](https://bugs.mojang.com/browse/MC-306798).

Author: jiajihuajihuafen
Repository: [https://github.com/jiajihuajihuafen/Unfix-MC-306798](https://github.com/jiajihuajihuafen/Unfix-MC-306798)

## What does this mod do?

- MC-306798: When the Ender Dragon is spawned with a `DragonDeathTime` of 200 or higher, it never disappears and drops experience orbs infinitely (behavior introduced in 1.17-pre1).
- Mojang fixed this in **26.1 Pre-Release 2** by changing `dragonDeathTime == 200` to `dragonDeathTime >= 200` in `EnderDragon.tickDeath()`, so dragons with a death time ≥ 200 now disappear properly.
- This mod uses a Mixin to change that check **back to `== 200`**, restoring the pre-fix behavior: dragons spawned with `summon ender_dragon ~ ~ ~ {DragonDeathTime: 200, Health: 0.0f}` will no longer disappear (and will drop experience orbs infinitely).

> Note: This deliberately restores the old "infinite XP orbs + dragon never disappears" bug. Only use it if you actually need this behavior (e.g., worlds/builds/server gameplay that rely on this mechanic).

## How it works

Uses `@Redirect` on `net.minecraft.world.entity.boss.enderdragon.EnderDragon#tickDeath()`:
It redirects the 3rd (`ordinal 2`) `level()` call in that method (inside the condition `if (this.dragonDeathTime >= 200 && this.level() instanceof ServerLevel)`), returning `null` when `dragonDeathTime != 200`, so the `instanceof` check fails and the entire removal block is skipped. As a result, only dragons with `dragonDeathTime == 200` disappear normally — matching vanilla behavior before 26.1-pre-2.

## Environment

|      | Version |
| --- | --- |
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3 |
| Fabric Loom | 1.17-SNAPSHOT |
| Java | 25 (required) |

Does not depend on Fabric API (Fabric Loader only).

## Building

MC 26.2's game classes are **Java 25 bytecode**, so you must build with **JDK 25**.
If you get (error: release version 25 not supported), your `JAVA_HOME` isn't JDK 25 (e.g., it's JDK 21).

```powershell
# Option 1: temporarily point to JDK 25 and build (local JDK 25 path is C:\java\java25)
$env:JAVA_HOME = "C:\java\java25"
.\gradlew.bat --stop
.\gradlew.bat build

# Option 2: this project's gradle.properties already pins org.gradle.java.home=C:/java/java25,
# so you can just build directly; on another machine, remove that line and make sure JAVA_HOME points to JDK 25
.\gradlew.bat build
```

The artifact is at `build/libs/unfix-mc-306798-1.0.0.jar`.

## Installation

1. Install [Fabric Loader 0.19.3+](https://fabricmc.net/use/installer/) (for Minecraft 26.2).
2. Put `unfix-mc-306798-1.0.0.jar` into `.minecraft/mods/` (works on both client and server).
3. Launch the game; seeing `[Unfix MC-306798] Loaded: ...` in the log means it's active.
4. Test: `summon ender_dragon ~ ~ ~ {DragonDeathTime: 200, Health: 0.0f}`.
