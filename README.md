# ProjectEX - 1.21.1+ NeoForge Port

**Porting ProjectEX from Forge 1.12.2/1.16.5 to NeoForge 1.21.1+**

## Project Overview

This repository contains the source code and resources for porting **ProjectEX** to modern Minecraft versions (1.21.1+) using the NeoForge modding platform.

ProjectEX is an unofficial extension mod for ProjectE that provides:
- Single block, low lag EMC trees (Power Flowers & Collectors)
- An improved Transmutation tablet
- EMC linking systems for external mods (AE2, Refined Storage)
- EMC Relays
- Magnum Stars & Colossal Stars
- Knowledge Sharing Book
- Craftable Tome of Knowledge
- The Final Star
- And more EMC-based utilities

## Repository Structure

```
projectex/
├── 1.12.2 source/          # Original Forge 1.18.1 source code (reference)
├── 1.16.5/                 # ProjectEX 1.16.5 JAR (reference)
│   └── projectex-1605.2.0-build.3.jar
├── integrations/           # Integration mod JARs for 1.21.1+
│   ├── ProjectE-1.21.1-PE1.1.0.jar
│   └── refinedstorage-neoforge-2.0.0.jar
└── neoforge-1.21/          # 1.21.1+ NeoForge port (IN PROGRESS)
    ├── src/main/java/      # Ported source code
    ├── src/main/resources/ # Resources and metadata
    ├── build.gradle        # NeoForge build configuration
    └── LICENSE.txt         # LGPL-3.0 license
```

## Porting Goals

### Target Platform
- **Minecraft Version:** 1.21.1+
- **Mod Loader:** NeoForge (latest stable)
- **Java Version:** 21+

### Core Dependencies
- **ProjectE** 1.21.1-PE1.1.0 (EMC system integration)
- **Refined Storage** NeoForge 2.0.0 (storage integration)

## Porting Checklist

### Phase 1: Setup & Infrastructure ✅ COMPLETED
- [x] Set up NeoForge 1.21.1+ development environment
- [x] Create mod project structure
- [x] Configure build.gradle with dependencies
- [x] Set up mods.toml metadata
- [x] Configure IDE for NeoForge development

### Phase 2: Core Systems Migration 🔄 IN PROGRESS
- [x] Port Matter enum system (15 tiers)
- [x] Port Star enum system (6 variants)
- [x] Set up DeferredRegister pattern
- [ ] Port EMC calculation and storage systems
- [x] Migrate power flower mechanics (block class complete)
- [x] Port collector mechanics (block class complete)
- [x] Update relay systems (block class complete)
- [ ] Migrate link systems (Personal, Refined, Energy, Compressed)
- [ ] Implement block entities with capabilities

### Phase 3: Items & Blocks 🔄 IN PROGRESS
- [x] Port all matter items (colored matter system)
- [x] Port star items (Magnum, Colossal, Final)
- [x] Port collector blocks (all tiers)
- [x] Port power flower blocks (all tiers)
- [x] Port relay blocks (all tiers)
- [ ] Port link blocks (Energy, Personal, Refined, Compressed)
- [ ] Port transmutation tables (Stone, Alchemy)
- [ ] Port utility items (Arcane Tablet, Knowledge Sharing Book, Tome of Knowledge)

### Phase 4: Integration
- [ ] ProjectE integration (EMC hooks)
- [ ] Refined Storage integration (storage linking)
- [ ] Update to new NeoForge capabilities system
- [ ] Implement modern data-driven recipes

### Phase 5: Data Generation
- [ ] Migrate blockstates to new format
- [ ] Migrate item/block models
- [ ] Update recipes to 1.21.1 format
- [ ] Generate lang files
- [ ] Update advancements

### Phase 6: Testing & Polish
- [ ] Test all EMC calculations
- [ ] Verify power flower/collector rates
- [ ] Test integration with ProjectE
- [ ] Test Refined Storage linking
- [ ] Balance adjustments
- [ ] Bug fixes

## Key Migration Challenges

### API Changes (1.12.2 → 1.21.1)
1. **Registry System:** Complete overhaul to DeferredRegister
2. **Capabilities:** Migrated to NeoForge's AttachCapabilitiesEvent
3. **NBT Handling:** CompoundTag API changes
4. **Networking:** New packet system using SimpleChannel
5. **Rendering:** Client-side rendering completely rewritten
6. **Data Generation:** Now required for most JSON files
7. **Tile Entities → Block Entities:** Naming and API changes
8. **Containers → Menus:** Inventory system refactor

### ProjectE Integration Updates
- EMC API access patterns changed
- Knowledge system integration updates
- Transmutation interface updates

### Refined Storage Integration
- Storage API completely new in 2.0.0
- Network linking mechanics updated
- Capability system changes

## Development Setup

### Prerequisites
- Java Development Kit (JDK) 21+
- IntelliJ IDEA or Eclipse
- Git

### Initial Setup
```bash
# Clone the repository
git clone https://github.com/LightWraith8268/projectex.git
cd projectex

# [Future] Setup workspace
./gradlew genIntellijRuns  # For IntelliJ
# or
./gradlew genEclipseRuns   # For Eclipse
```

## Building

```bash
# [Future] Build the mod
./gradlew build

# Output will be in build/libs/
```

## Reference Materials

### Original Mod Info
- **1.12.2 Source:** Based on FTB ProjectEX (Forge)
- **Original Repository:** https://github.com/FTBTeam/ProjectEX
- **CurseForge:** https://www.curseforge.com/minecraft/mc-mods/projectex

### Documentation
- [NeoForge Documentation](https://docs.neoforged.net/)
- [ProjectE Wiki](https://github.com/sinkillerj/ProjectE/wiki)
- [Refined Storage Docs](https://refinedmods.com/refined-storage/)

## Contributing

This is a port project. Major features should remain consistent with the original ProjectEX design philosophy while adapting to modern Minecraft/NeoForge standards.

## Current Port Status

### ✅ Completed Components

**Core Infrastructure:**
- NeoForge 7.0.145 build system configured
- Mod metadata (neoforge.mods.toml) with ProjectE/Refined Storage dependencies
- LGPL-3.0 licensing with proper attribution to LatvianModder
- DeferredRegister pattern for all registries

**Enums & Constants:**
- Matter.java (15 tiers: BASIC → FINAL) with EMC values
- Star.java (6 variants: Ein, Zwei, Drei, Vier, Sphere, Omega)
- ProjectEX.java main class with modern CreativeModeTab

**Block Classes (3/5 core types):**
- CollectorBlock.java (all 15 tiers via Matter enum)
- RelayBlock.java (all 15 tiers)
- PowerFlowerBlock.java (all 15 tiers with VoxelShape, owner tracking)

**Item Registration:**
- All Matter-based items registered
- All Star items registered
- BlockItem wrappers for all blocks

### 🔄 In Progress

**Block Classes:**
- Link blocks (Energy, Personal, Refined, Compressed) - pending
- Table blocks (Stone, Alchemy) - pending

**Block Entities:**
- All block entity implementations with NeoForge capabilities - pending
- EMC storage capability integration - pending

### ⏳ Not Started

- Data generation (models, recipes, blockstates, lang files)
- Utility items (Arcane Tablet, Knowledge Sharing Book)
- ProjectE/Refined Storage capability integration
- Testing and builds

### Next Steps

1. Implement remaining block classes (Link + Table blocks)
2. Implement all block entities with ProjectE EMC capabilities
3. Set up data generation for models and recipes
4. Add gradle wrapper
5. First test build

---

## License

This project is licensed under **LGPL-3.0** (GNU Lesser General Public License v3.0).

**Original Author:** LatvianModder (ProjectEX 1.12.2/1.16.5)
**Port Author:** LightWraith8268 (1.21.1+ NeoForge port)

See [LICENSE.txt](neoforge-1.21/LICENSE.txt) and [CREDITS.md](neoforge-1.21/CREDITS.md) for full licensing information.

## Disclaimer

This mod is an **unofficial** extension of ProjectE. This port is not affiliated with the original FTB ProjectEX team or ProjectE developers.

For issues specific to this 1.21.1+ port, please use this repository's issue tracker.
For general ProjectEX features/bugs, refer to the original repository.

---

**Port Status:** Core Infrastructure Complete - Block Implementation Phase
**Last Updated:** 2025-12-27
