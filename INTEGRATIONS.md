# Integration Dependencies

This document details the mod integrations required for ProjectEX 1.21.1+ and how they interact with the mod.

## Required Dependencies

### ProjectE (Core Dependency)
- **Version:** 1.21.1-PE1.1.0
- **Type:** Required
- **JAR Location:** `integrations/ProjectE-1.21.1-PE1.1.0.jar`
- **Purpose:** Core EMC (Energy-Matter Currency) system

#### ProjectE Integration Points
1. **EMC System Access**
   - Reading EMC values from items
   - Setting custom EMC values for ProjectEX items
   - EMC storage and transfer mechanics

2. **Knowledge System**
   - Player knowledge tracking
   - Knowledge Sharing Book functionality
   - Tome of Knowledge crafting integration

3. **Transmutation**
   - Enhanced transmutation tablet
   - Stone Table integration
   - Alchemy Table integration

4. **Star System**
   - Klein Star integration
   - Magnum Star mechanics (extended Klein Stars)
   - Colossal Star mechanics
   - Final Star functionality

#### API Usage Areas
```java
// Key ProjectE API classes to integrate with:
- moze_intel.projecte.api.capabilities.IKnowledgeProvider
- moze_intel.projecte.api.capabilities.IItemEmcHolder
- moze_intel.projecte.api.event.PlayerAttemptCondenserSetEvent
- moze_intel.projecte.api.ItemInfo
- moze_intel.projecte.api.ProjectEAPI
```

### Refined Storage (Optional Integration)
- **Version:** 2.0.0 (NeoForge)
- **Type:** Optional
- **JAR Location:** `integrations/refinedstorage-neoforge-2.0.0.jar`
- **Purpose:** Storage network integration for EMC items

#### Refined Storage Integration Points
1. **Link Blocks**
   - Refined Link: Connects RS network to EMC system
   - Compressed Refined Link: Higher-capacity variant
   - Energy Link: Power transfer between RS and EMC

2. **Storage Access**
   - Accessing items from RS networks
   - EMC-based item extraction
   - Network connectivity detection

3. **Capability Integration**
   - RS storage capability
   - EMC transfer capability
   - Network node registration

#### API Usage Areas
```java
// Key Refined Storage 2.0 API patterns:
- Network node registration
- Storage channel access
- Grid synchronization
- Capability attachments (NeoForge style)
```

## Legacy Integration Support (1.12.2 Reference)

### Applied Energistics 2 (AE2)
- **Status:** Present in 1.12.2/1.16.5, TBD for 1.21.1+
- **Integration:** Personal Link for AE2 network connectivity
- **Note:** May be implemented in future update after core port completion

## Integration Architecture

### EMC Provider System
ProjectEX blocks that interact with EMC:

1. **Collectors** (All Tiers)
   - Generate EMC from light
   - Store EMC internally
   - Transfer to adjacent EMC acceptors

2. **Power Flowers**
   - Compact collector arrays
   - Exponential EMC generation
   - Configurable output rates

3. **Relays** (All Tiers)
   - Transfer EMC between blocks
   - Configurable transfer rates
   - Support for different tier speeds

4. **Link Blocks**
   - Personal Link: Player inventory access
   - Refined Link: RS network access
   - Energy Link: Power conversion
   - Compressed Refined Link: High-capacity RS access

### Knowledge Sharing
- **Knowledge Sharing Book:** Transfer learned transmutations between players
- Integrates with ProjectE's `IKnowledgeProvider` capability
- Requires both players to have the capability

### Star System Hierarchy
```
Klein Star (ProjectE base)
    ↓
Magnum Star (ProjectEX tier 1)
    ├── Ein, Zwei, Drei, Vier (sub-tiers)
    ├── Sphere
    └── Omega
    ↓
Colossal Star (ProjectEX tier 2)
    ├── Ein, Zwei, Drei, Vier (sub-tiers)
    ├── Sphere
    └── Omega
    ↓
Final Star (ProjectEX ultimate)
    └── Crafted with Final Star Shard
```

## NeoForge-Specific Integration Considerations

### Capability System Changes
- **Old (Forge):** `ICapabilityProvider` interface
- **New (NeoForge):** `AttachCapabilitiesEvent` + Data Components
- **Impact:** All capability interactions need migration

### Network Communication
- **Old (Forge):** `SimpleNetworkWrapper`
- **New (NeoForge):** `SimpleChannel` with updated packet handling
- **Impact:** All client-server sync needs rewrite

### Registry System
- **Old (Forge):** `@ObjectHolder` and registry events
- **New (NeoForge):** `DeferredRegister` required
- **Impact:** All block/item registration patterns updated

## Testing Integration Points

### ProjectE Integration Tests
- [ ] EMC values properly assigned to all items
- [ ] Collectors generate EMC correctly
- [ ] Power Flowers calculate rates properly
- [ ] Stars store and transfer EMC
- [ ] Knowledge Book shares transmutations
- [ ] Transmutation tables access EMC

### Refined Storage Integration Tests
- [ ] Refined Link detects RS networks
- [ ] Items can be extracted via EMC
- [ ] Energy Link converts power correctly
- [ ] Compressed Refined Link handles high throughput
- [ ] Network disconnection handled gracefully

## Version Compatibility Matrix

| ProjectEX Version | Minecraft | Mod Loader | ProjectE Version | Refined Storage Version |
|-------------------|-----------|------------|------------------|-------------------------|
| 1.12.2 (Original) | 1.12.2    | Forge      | PE 1.4.1         | RS 1.6.x                |
| 1.16.5 (Build 3)  | 1.16.5    | Forge      | PE 1.0.2B        | RS 1.9.x                |
| **1.21.1+ (Port)**| **1.21.1+**| **NeoForge**| **PE 1.1.0**    | **RS 2.0.0**            |

## Future Integration Possibilities

### Potential Mod Integrations
1. **Applied Energistics 2** (when available for 1.21.1)
   - Personal Link restoration
   - ME network EMC access

2. **Mekanism** (energy conversion)
   - Power generation from EMC
   - EMC from power conversion

3. **Create** (mechanical integration)
   - Rotational power from EMC
   - EMC generation from kinetic systems

4. **Botania** (magical integration)
   - Mana-EMC conversion
   - Passive EMC generation

## Implementation Priority

1. **Phase 1:** ProjectE core integration (REQUIRED)
2. **Phase 2:** Refined Storage basic integration
3. **Phase 3:** Advanced RS features
4. **Phase 4:** Future mod integrations (post-release)

---

**Note:** Integration APIs may change as NeoForge and dependency mods update. This document will be updated accordingly.
