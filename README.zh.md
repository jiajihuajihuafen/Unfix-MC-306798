![AI Declaration](https://img.shields.io/badge/AI%20Declaration-DeepSeek%20%26%20Codex-blue)
[![English](https://img.shields.io/badge/English-switch-blue)](README.md)
[![中文](https://img.shields.io/badge/中文-切换-red)](README.zh.md)
# Unfix MC-306798

一个 **Fabric 模组**,用于 Minecraft **Java 版 26.2**,撤销 [MC-306798](https://bugs.mojang.com/browse/MC-306798) 的修复内容。

作者:jiajihuajihuafen
仓库:[https://github.com/jiajihuajihuafen/Unfix-MC-306798](https://github.com/jiajihuajihuafen/Unfix-MC-306798)

## 这个模组做什么?

- MC-306798:当末影龙被以 `DragonDeathTime`(龙死亡计时)为 200 或更高的值生成时,它永远不会消失,并无限掉落经验球(该行为自 1.17-pre1 引入)。
- 官方在 **26.1 Pre-Release 2** 修复了此问题:将 `EnderDragon.tickDeath()` 中的 `dragonDeathTime == 200` 改成了 `dragonDeathTime >= 200`,使死亡时间 ≥ 200 的龙也能正常消失。
- 本模组通过 Mixin 把该判断**改回 `== 200`**,恢复修复前的行为:用
  `summon ender_dragon ~ ~ ~ {DragonDeathTime: 200, Health: 0.0f}` 生成的龙将不再消失(会无限掉落经验球)。

> 注意:这是刻意恢复"无限经验球 + 龙不消失"的旧 bug,仅在你确实需要该行为时使用(例如依赖此机制的存档/建筑/服务器玩法)。

## 工作原理

对 `net.minecraft.world.entity.boss.enderdragon.EnderDragon#tickDeath()` 使用 `@Redirect`:
把该方法里第 3 次(ordinal 2)`level()` 调用(位于
`if (this.dragonDeathTime >= 200 && this.level() instanceof ServerLevel)` 条件中)重定向,
当 `dragonDeathTime != 200` 时返回 `null`,使 `instanceof` 失败、整个移除区块被跳过。
因此只有 `dragonDeathTime == 200` 的龙才会正常消失 —— 与 26.1-pre-2 之前的原版行为一致。

## 环境

| 项目 | 版本 |
| --- | --- |
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3 |
| Fabric Loom | 1.17-SNAPSHOT |
| Java | 25(必须) |

不依赖 Fabric API(仅 Fabric Loader)。

## 构建

MC 26.2 的游戏类为 **Java 25 字节码**,必须用 **JDK 25** 构建。
如果报错 `错误: 不支持发行版本 25`,说明当前 JAVA_HOME 不是 JDK 25(例如是 JDK 21)。

```powershell
# 方式一:临时指定 JDK 25 后构建(本机 JDK 25 路径为 C:\java\java25)
$env:JAVA_HOME = "C:\java\java25"
.\gradlew.bat --stop
.\gradlew.bat build

# 方式二:本项目 gradle.properties 已固定 org.gradle.java.home=C:/java/java25,
# 直接构建即可;换到其他机器时删除该行,并确保 JAVA_HOME 指向 JDK 25
.\gradlew.bat build
```

产物在 `build/libs/unfix-mc-306798-1.0.0.jar`。

## 安装

1. 安装 [Fabric Loader 0.19.3+](https://fabricmc.net/use/installer/)(对应 Minecraft 26.2)。
2. 把 `unfix-mc-306798-1.0.0.jar` 放入 `.minecraft/mods/`(客户端与服务器均可)。
3. 启动游戏,在日志中看到 `[Unfix MC-306798] Loaded: ...` 即表示生效。
4. 测试:`summon ender_dragon ~ ~ ~ {DragonDeathTime: 200, Health: 0.0f}`。
