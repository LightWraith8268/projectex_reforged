# Porting Notes: 1.12.2 → 1.21.1 Migration Guide

This document contains technical notes and code migration patterns for porting ProjectEX from Forge 1.12.2 to NeoForge 1.21.1+.

## Major API Changes Overview

### 1. Block & Item Registration

#### Old (1.12.2 Forge)
```java
@Mod.EventBusSubscriber
public class ModBlocks {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
            new BlockCollector(),
            new BlockPowerFlower()
        );
    }
}
```

#### New (1.21.1 NeoForge)
```java
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(ProjectEX.MOD_ID);

    public static final DeferredBlock<Block> BASIC_COLLECTOR =
        BLOCKS.register("basic_collector", () -> new BlockCollector(properties));

    public static final DeferredBlock<Block> BASIC_POWER_FLOWER =
        BLOCKS.register("basic_power_flower", () -> new BlockPowerFlower(properties));
}
```

### 2. Tile Entities → Block Entities

#### Old (1.12.2)
```java
public class TileCollector extends TileEntity implements ITickable {
    @Override
    public void update() {
        // Tick logic
    }
}

// Registration
GameRegistry.registerTileEntity(TileCollector.class,
    new ResourceLocation(MOD_ID, "collector"));
```

#### New (1.21.1)
```java
public class CollectorBlockEntity extends BlockEntity {
    public CollectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COLLECTOR.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        // Tick logic
    }
}

// Registration with DeferredRegister
public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
    DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MOD_ID);

public static final Supplier<BlockEntityType<CollectorBlockEntity>> COLLECTOR =
    BLOCK_ENTITIES.register("collector", () ->
        BlockEntityType.Builder.of(CollectorBlockEntity::new,
            ModBlocks.BASIC_COLLECTOR.get()).build(null));
```

### 3. NBT Handling

#### Old (1.12.2)
```java
@Override
public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    compound.setLong("emc", storedEMC);
    return compound;
}

@Override
public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    storedEMC = compound.getLong("emc");
}
```

#### New (1.21.1)
```java
@Override
protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putLong("emc", storedEMC);
}

@Override
protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    storedEMC = tag.getLong("emc");
}
```

### 4. Capabilities System

#### Old (1.12.2 Forge)
```java
@Override
public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    if (capability == CapabilityEMC.EMC_HOLDER) {
        return true;
    }
    return super.hasCapability(capability, facing);
}

@Override
public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    if (capability == CapabilityEMC.EMC_HOLDER) {
        return CapabilityEMC.EMC_HOLDER.cast(emcStorage);
    }
    return super.getCapability(capability, facing);
}
```

#### New (1.21.1 NeoForge)
```java
// Attach capabilities via event
@SubscribeEvent
public static void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
    if (event.getObject() instanceof CollectorBlockEntity) {
        event.addCapability(
            new ResourceLocation(MOD_ID, "emc_storage"),
            new EMCStorageProvider()
        );
    }
}

// Or use Data Attachments (new NeoForge system)
public static final AttachmentType<EMCStorage> EMC_STORAGE =
    AttachmentType.builder(() -> new EMCStorage()).build();
```

### 5. Networking/Packets

#### Old (1.12.2)
```java
public class PacketHandler {
    private static int packetId = 0;
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        INSTANCE.registerMessage(PacketSyncEMC.Handler.class,
            PacketSyncEMC.class, packetId++, Side.CLIENT);
    }
}
```

#### New (1.21.1)
```java
public class ModPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel CHANNEL;

    public static void register() {
        int id = 0;
        CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
        );

        CHANNEL.messageBuilder(SyncEMCPacket.class, id++)
            .encoder(SyncEMCPacket::encode)
            .decoder(SyncEMCPacket::decode)
            .consumerMainThread(SyncEMCPacket::handle)
            .add();
    }
}
```

### 6. Recipes

#### Old (1.12.2)
- JSON files in `src/main/resources/assets/projectex/recipes/`
- Shaped/shapeless crafting
- Manual JSON creation

#### New (1.21.1)
- Data generation required (preferred)
- Recipe providers extend `RecipeProvider`
- Run data generation task to create JSONs

```java
public class ModRecipeProvider extends RecipeProvider {
    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BASIC_COLLECTOR.get())
            .pattern("MGM")
            .pattern("GCG")
            .pattern("MGM")
            .define('M', Tags.Items.DUSTS_GLOWSTONE)
            .define('G', Tags.Items.GLASS)
            .define('C', Items.DIAMOND)
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(output);
    }
}
```

### 7. Rendering

#### Old (1.12.2)
```java
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
            new ModelResourceLocation(item.getRegistryName(), id));
    }
}
```

#### New (1.21.1)
- Models registered via JSON files (data generation)
- Custom rendering via `BlockEntityRenderer`
- Item models through `ItemProperties`

```java
public class CollectorRenderer implements BlockEntityRenderer<CollectorBlockEntity> {
    @Override
    public void render(CollectorBlockEntity entity, float partialTick,
                      PoseStack poseStack, MultiBufferSource buffer,
                      int packedLight, int packedOverlay) {
        // Custom rendering logic
    }
}

// Register renderer
@SubscribeEvent
public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(ModBlockEntities.COLLECTOR.get(),
        CollectorRenderer::new);
}
```

### 8. Creative Tabs

#### Old (1.12.2)
```java
public static final CreativeTabs TAB = new CreativeTabs(MOD_ID) {
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.FINAL_STAR);
    }
};
```

#### New (1.21.1)
```java
public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
    DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

public static final Supplier<CreativeModeTab> TAB = CREATIVE_TABS.register("main",
    () -> CreativeModeTab.builder()
        .icon(() -> new ItemStack(ModItems.FINAL_STAR.get()))
        .title(Component.translatable("itemGroup." + MOD_ID))
        .displayItems((params, output) -> {
            output.accept(ModItems.FINAL_STAR.get());
            output.accept(ModBlocks.BASIC_COLLECTOR.get());
            // Add all items
        })
        .build());
```

## Critical Migration Tasks

### High Priority
1. ✅ Update mod metadata (`mods.toml` replaces `mcmod.info`)
2. ✅ Migrate to DeferredRegister for all registries
3. ✅ Convert TileEntities to BlockEntities
4. ✅ Update capability system to NeoForge style
5. ✅ Rewrite packet handling
6. ✅ Setup data generation

### Medium Priority
1. Update GUI/Container system to Menu/Screen
2. Migrate rendering to new pipeline
3. Update configuration system (TOML-based)
4. Convert to component-based item properties
5. Update advancements format

### Low Priority
1. Optimize performance with modern APIs
2. Add modern features (data components, etc.)
3. Improve code structure
4. Add comprehensive javadocs

## Common Pitfalls

### 1. Registry Names
- Must be lowercase with underscores only
- No uppercase or special characters

### 2. Side Annotations
- `@SideOnly` removed in modern versions
- Use `DistExecutor` for side-specific code

### 3. Null Safety
- Modern Minecraft uses `@Nullable` and `@NotNull` annotations
- IDE will warn about potential NPEs

### 4. Resource Locations
- Always use `new ResourceLocation(MOD_ID, "path")`
- Never hardcode namespace strings

### 5. Block Properties
- Block properties now use builder pattern
- Must copy from vanilla blocks or create new

## Testing Checklist

### Functionality Tests
- [ ] All blocks place and break correctly
- [ ] Block entities save/load NBT data
- [ ] Items stack and display properly
- [ ] Recipes craft correctly
- [ ] EMC generation works
- [ ] EMC storage persists
- [ ] EMC transfer functions
- [ ] GUIs open and close
- [ ] Multiplayer sync works
- [ ] Configuration loads

### Integration Tests
- [ ] ProjectE EMC values recognized
- [ ] Klein Star compatibility
- [ ] Knowledge system works
- [ ] Refined Storage links connect
- [ ] RS item extraction works
- [ ] Capabilities attach properly

### Visual Tests
- [ ] Textures load correctly
- [ ] Models render properly
- [ ] Particles display
- [ ] GUIs render correctly
- [ ] Tooltips show properly

## Reference Code Locations

### 1.12.2 Source Key Files
```
1.12.2 source/src/main/java/com/latmod/mods/projectex/
├── block/          # All block implementations
├── tile/           # Tile entity implementations
├── item/           # Item implementations
├── integration/    # Mod integration code
├── gui/            # GUI implementations
└── net/            # Packet handling
```

### Target 1.21.1 Structure (Recommended)
```
src/main/java/com/yourname/projectex/
├── block/
│   ├── entity/     # Block entities
│   └── custom/     # Custom block classes
├── item/
│   └── custom/     # Custom item classes
├── integration/
│   ├── projecte/   # ProjectE integration
│   └── refinedstorage/  # RS integration
├── client/
│   ├── gui/        # Screen classes
│   └── renderer/   # Custom renderers
├── network/
│   └── packet/     # Packet classes
├── data/
│   ├── recipe/     # Recipe providers
│   ├── loot/       # Loot table providers
│   └── tag/        # Tag providers
└── core/
    ├── registry/   # All DeferredRegisters
    └── config/     # Configuration
```

## Gradle Configuration Notes

### Essential Dependencies
```gradle
minecraft {
    mappings channel: 'official', version: '1.21.1'
}

dependencies {
    minecraft "net.neoforged:neoforge:${neoforge_version}"

    // ProjectE
    implementation fg.deobf("moze_intel.projecte:ProjectE:${projecte_version}")

    // Refined Storage
    implementation fg.deobf("com.refinedmods:refinedstorage:${rs_version}")
}
```

---

**Last Updated:** 2025-12-27
**Status:** Initial migration planning phase
