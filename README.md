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
├── 1.12.2 source/          # Original Forge 1.12.2 source code
├── 1.16.5/                 # ProjectEX 1.16.5 JAR (reference)
│   └── projectex-1605.2.0-build.3.jar
├── integrations/           # Integration mod JARs for 1.21.1+
│   ├── ProjectE-1.21.1-PE1.1.0.jar
│   └── refinedstorage-neoforge-2.0.0.jar
└── [Future: 1.21.1+ source directory]
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

### Phase 1: Setup & Infrastructure
- [ ] Set up NeoForge 1.21.1+ development environment
- [ ] Create mod project structure
- [ ] Configure build.gradle with dependencies
- [ ] Set up mods.toml metadata
- [ ] Configure IDE for NeoForge development

### Phase 2: Core Systems Migration
- [ ] Port EMC calculation and storage systems
- [ ] Migrate power flower mechanics
- [ ] Port collector mechanics
- [ ] Update relay systems
- [ ] Migrate link systems (Personal, Refined, Energy, Compressed)

### Phase 3: Items & Blocks
- [ ] Port all matter items (colored matter system)
- [ ] Port star items (Magnum, Colossal, Final)
- [ ] Port collector blocks (all tiers)
- [ ] Port power flower blocks (all tiers)
- [ ] Port relay blocks (all tiers)
- [ ] Port transmutation tables (Stone, Alchemy)
- [ ] Port utility items (Arcane Tablet, Knowledge Sharing Book)

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

## License

Check the original 1.12.2 source LICENSE.txt for licensing information.

## Disclaimer

This mod is an **unofficial** extension of ProjectE. This port is not affiliated with the original FTB ProjectEX team or ProjectE developers.

For issues specific to this 1.21.1+ port, please use this repository's issue tracker.
For general ProjectEX features/bugs, refer to the original repository.

---

**Port Status:** Planning & Setup Phase
**Last Updated:** 2025-12-27
