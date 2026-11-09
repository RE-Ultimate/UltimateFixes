# Re:Ultimate Fixes

Fixes, convenience features, and backports for Minecraft 1.4.7, maintained for Re:Ultimate.

This repository continues the original project and keeps its history and MIT license.
See [the upstream notes](docs/UPSTREAM.md) for the original architecture and credits.

## Build

Install Java 17 and Python 3, then run:

```sh
./gradlew test build --console=plain
```

The bootstrap downloads only the required compile and test dependencies and verifies their SHA-256 hashes.
Maven artifacts come from [RE-Ultimate/maven](https://github.com/RE-Ultimate/maven).
To use an existing pack cache, set `REULTIMATE_PACK_DIR` to its Minecraft directory.
Builds target Java 8 for the game. Output JARs are in `build/libs`.

## Compatibility

Runtime mod IDs, Java package names, network channels, and existing configuration keys are retained so existing worlds and installations continue to use the same identifiers.
The visible project names and support links use Re:Ultimate.

Report problems in [this repository](https://github.com/RE-Ultimate/UltimateFixes/issues).
