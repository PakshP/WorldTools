<p align="center">
  <img src="common/src/main/resources/assets/worldtools/WorldTools.png" alt="WorldTools icon" width="256" height="256">
</p>

# WorldTools (Fabric 26.3)

[![Minecraft](https://img.shields.io/badge/Minecraft-26.3-lime?style=for-the-badge)](https://www.minecraft.net/)
[![Loader](https://img.shields.io/badge/Loader-Fabric-ffbf00?style=for-the-badge)](https://fabricmc.net/)
[![License](https://img.shields.io/badge/License-GPL%20v3-blue?style=for-the-badge)](LICENSE.md)

This repository is a maintained **Fabric-only** build of WorldTools for **Minecraft 26.3**.

WorldTools captures a local world snapshot from a multiplayer session, including chunks, entities, players,
statistics, advancements, map data, and capture metadata.

> **Maintenance note:** Honestly, I mostly update this when I want to use it myself.
> If a new Minecraft version drops and you'd like it ported, open an issue. If a few
> other people, or I just want to, chime in that they want it too, I'll probably get around to it.

## Upstream Credit

This project is based on the original WorldTools project:

- Original project: `https://github.com/Avanatiker/WorldTools`
- Original authors: Constructor, P529, rfresh/rfresh2 (and contributors)

The intent of this release line is to keep the mod usable on modern Fabric versions while preserving upstream attribution.

## Current Scope

- Minecraft: **26.3**
- Mod version: **1.3.0**
- Loader: **Fabric**
- Java target: **25+**
- Module layout: `common` sources compiled into the `fabric` Gradle project (Forge removed)

## Features

- Capture toggle with default keybind `F12`
- Config/manager UI with default keybind `F10`
- Captures:
  - chunks (terrain/biomes/structures)
  - entities and players
  - container/block-entity data (when interacted with)
  - player stats and advancements
  - level/map metadata and export files
- Output appears as a playable singleplayer world save

## Install (Fabric)

### Required mods

- Fabric Loader
- Fabric API
- Fabric Language Kotlin
- Cloth Config
- Mod Menu (optional but recommended)

### Install steps

1. Install Fabric for Minecraft `26.3`.
2. Download the latest Fabric jar from the repository releases (or build locally).
3. Put the jar in your `.minecraft/mods` folder.

## Usage

- Start/stop capture: `F12` or `/worldtools capture`
- Open manager/config screen: `F10` or button in pause menu
- During capture, open containers you want included in the saved snapshot

## Build From Source

Requires JDK 25.

### Windows (PowerShell)

```powershell
./gradlew.bat :fabric:build --no-daemon
```

Output jars are in `fabric/build/libs`.

### Run dev client

```powershell
./gradlew.bat :fabric:runClient --no-daemon
```

## Project Notes

- This project intentionally removes old community links that are no longer maintained.
- Ported to Mojang's official names (26.x is unobfuscated, Yarn no longer exists) and the non-remapping Fabric Loom.
- Captured worlds use the 26.x save layout (`dimensions/<namespace>/<dimension>/`, `players/data`, saved-data files for world gen settings and weather).
- If you report issues, include logs/crash reports and reproduction steps.

## Contributing

PRs are welcome. Please keep changes focused and include:

- target behavior
- test/build result
- affected files/modules

## License

This project remains under **GNU GPL v3.0**, same as upstream.

See `LICENSE.md` for details.

---

**Disclaimer:** Not affiliated with Mojang Studios. Minecraft is a trademark of Mojang.
