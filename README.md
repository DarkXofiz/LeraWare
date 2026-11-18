# NevaWare

Client-side Fabric mod for Minecraft 1.21.x (Java 21), originally distributed as `NevaWare.jar` (v1.7).
Author: TrNeverMind. Licensed under **GPL-3.0** (see `LICENSE`).

## Layout
```
src/main/java        decompiled sources (neva.ware.*, com.ezzenix.*, ru.donyka.*)
src/main/resources   fabric.mod.json, mixin configs, accesswidener, assets (fonts, shaders, lang, sounds), default.nw
tools/decompile.sh   reproducible decompile step
```

## Building
1. Put the original jar somewhere outside the repo and get [Vineflower](https://github.com/Vineflower/vineflower/releases).
2. `tools/decompile.sh /path/NevaWare.jar /path/vineflower.jar`
3. `./gradlew build` → `build/libs/NevaWare-1.7.jar`
4. Fix only compile errors from decompilation; do not change behaviour.

## Notes
- The original jar is built against **intermediary** names (`class_846`, `method_3653`). Decompiled code must be
  remapped to Yarn (default here) — see "Remapping" below.
- `nevaware.accesswidener` is in the `intermediary` namespace; if Loom rejects it, remap it to `named`.
- Refmaps are intentionally not checked in; Loom regenerates them.
- Dependencies shaded in the original jar: Orbit (Gradle dependency now), Netty (provided by Minecraft).
- Suggested (optional) mods: Sodium, Exordium, Starlight, IAS, ViaFabricPlus.

## Remapping
Easiest path: run the original jar through Loom's `remapJar`-inverse, e.g. with
[Tiny Remapper](https://github.com/FabricMC/tiny-remapper) using Yarn `intermediary -> named` before decompiling,
or decompile via Loom's `genSources` workflow and replace names manually.

## License
GPL-3.0-or-later for NevaWare; bundled/derived MIT components keep their notices (`THIRD_PARTY_NOTICES.md`).

## Status
Sources are decompiled from the original release jar and are **not yet buildable as-is** (about 100 decompiler artefacts remain, mostly generics and mixin casts). Contributions that fix them without changing behaviour are welcome.
