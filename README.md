# LeraWare

Client-side Fabric mod for Minecraft 1.21.x (Java 21).

LeraWare is the open-source source release of the **NevaWare 1.7** mod, originally created by **TrNeverMind**
and distributed as `NevaWare.jar`. The code in this repository is derived from that release; the internal
package names (`neva.ware.*`), the mod id and the in-game name are intentionally left unchanged so that all
original features keep working exactly as before.

Licensed under **GPL-3.0** (see `LICENSE`). Third-party components keep their own notices
(see `THIRD_PARTY_NOTICES.md`).

## Layout
```
src/main/java        decompiled sources (neva.ware.*, com.ezzenix.*, ru.donyka.*)
src/main/resources   fabric.mod.json, mixin configs, accesswidener, assets (fonts, shaders, lang, sounds), default.nw
stubs/baritone       compile-only stub of the Baritone API surface used by the mod (not shipped)
tools/               helper scripts (identity mappings generator, CI script, decompile helper)
```

## Building
Built by GitHub Actions on every push (see `.github/workflows/build.yml`); the jar is uploaded as the
`NevaWare-jar` artifact.

Locally: `python3 tools/make_identity_mappings.py 1.21.1 && gradle build`

## Notes
- The sources use Minecraft's **intermediary** names (`class_2561`, `method_3653`, ...), because that is what the
  original release jar contains. `tools/make_identity_mappings.py` generates an identity mapping so Loom can
  compile them unchanged. Remapping to readable Yarn names is a possible future improvement.
- `nevaware.accesswidener` uses the `named` namespace (identity mappings); Loom converts it when packaging.
- Netty SOCKS/proxy codecs and Orbit were shaded in the original jar; here they are Gradle dependencies.
- Baritone is optional at runtime; only a small compile-time stub is included.

## Status
Sources are decompiled from the original release jar. A few decompiler artefacts may still prevent a clean
build; contributions that fix them without changing behaviour are welcome.

## Credits
- NevaWare by TrNeverMind (GPL-3.0)
- ChatAnimation by Ezzenix (MIT), ChunkAnimator by Donyka (MIT)
