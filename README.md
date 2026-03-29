<p align="center">
  <img src="common/src/main/resources/assets/worldtools/WorldTools.png" alt="WorldTools icon" width="256" height="256">
</p>

# WorldTools (Fabric 1.21.11)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-lime?style=for-the-badge)](https://www.minecraft.net/)
[![Loader](https://img.shields.io/badge/Loader-Fabric-ffbf00?style=for-the-badge)](https://fabricmc.net/)
[![License](https://img.shields.io/badge/License-GPL%20v3-blue?style=for-the-badge)](LICENSE.md)

This repository is a maintained **Fabric-only** build of WorldTools for **Minecraft 1.21.11**.

WorldTools captures a local world snapshot from a multiplayer session, including chunks, entities, players,
statistics, advancements, map data, and capture metadata.

## Upstream Credit

This project is based on the original WorldTools project:

- Original project: `https://github.com/Avanatiker/WorldTools`
- Original authors: Constructor, P529, rfresh/rfresh2 (and contributors)

The intent of this release line is to keep the mod usable on modern Fabric versions while preserving upstream attribution.

## Current Scope

- Minecraft: **1.21.11**
- Mod version: **1.2.9**
- Loader: **Fabric**
- Java target: **21+**
- Module layout: `common` + `fabric` (Forge removed)

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

1. Install Fabric for Minecraft `1.21.11`.
2. Download the latest Fabric jar from the repository releases (or build locally).
3. Put the jar in your `.minecraft/mods` folder.

## Usage

- Start/stop capture: `F12` or `/worldtools capture`
- Open manager/config screen: `F10` or button in pause menu
- During capture, open containers you want included in the saved snapshot

## Build From Source

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
- Some internal systems were adapted for 1.21.11 API changes (mixin targets, NBT/storage, render hooks).
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
