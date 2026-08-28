# Porting Voluminous Energy: 1.21.1 → 26.1

Status as of this audit: Gradle config is correct (NeoForge `26.1.1.11-beta`, Minecraft
`26.1.1`, Java 25 toolchain, Parchment `2025.12.20` — see `gradle.properties`/`build.gradle`).
`./gradlew compileJava` (with `-Xmaxerrs 10000` added to `build.gradle` so the full error list
prints instead of stopping at javac's 100-error cap) currently fails with **1,095 errors across
186 of the mod's 447 Java files**. This is not a small mechanical rename — three separate
subsystems were fundamentally rewritten between 1.21.1 and 26.1 (GUI rendering, block-entity
rendering, and recipes), plus NeoForge's entire capability/transfer API was replaced. This doc
is organized by **root cause** first (fix these first — each one clears dozens to hundreds of
errors), then by area, then an appendix of every file that currently fails to compile.

Sources: official NeoForged migration primers for 1.21.2, 1.21.4, 1.21.5, 1.21.6, 1.21.7,
1.21.8, 1.21.9, 1.21.10, 1.21.11, and 26.1 (github.com/neoforged/.github/tree/main/primers),
NeoForge's screens docs (docs.neoforged.net), NeoForge's Transfer API rework announcement
(neoforged.net/news/21.9-transfer-rework), and the actual `javac` output against this repo.

---

## How to reproduce the error list

```bash
JAVA_HOME=/opt/homebrew/opt/openjdk@25 ./gradlew compileJava --console=plain
```

Java 25 and 26 are both installed via Homebrew (`openjdk@25`, `openjdk@26`, plus a plain
`openjdk` symlinked to 26). The toolchain in `build.gradle` requests Java 25, matching what
Mojang ships to end users for 26.1+, so `openjdk@25` is the correct one to build with.
`gradlew` was not executable in a fresh checkout (`chmod +x gradlew` fixes it — this is normal,
not a repo problem, the bit just doesn't survive some git transports).

---

## Root causes (fix in this order — each clears a large, self-contained batch of errors)

### 1. `GuiGraphics` → `GuiGraphicsExtractor` + full GUI "extract" rewrite — **~360 errors, every screen file**

The GUI pipeline was rewritten three times between 1.21.1 and 26.1 (1.21.2, 1.21.6, 26.1). Only
the final, 26.1 shape matters for this port. The model: widgets/screens no longer render
directly — they **extract** a description of what to draw into a render-state tree, which is
then submitted and rendered separately. Concretely:

- `net.minecraft.client.gui.GuiGraphics` class **renamed to `GuiGraphicsExtractor`**.
- Method renames drop `draw*`/`render*`/`submit*` prefixes and `*RenderState` suffixes, and
  `*String*` → `*Text*`:
  - `drawString` → `text`, `drawCenteredString` → `centeredText`, `drawStringWithBackdrop` →
    `textWithBackdrop`
  - `hLine`/`vLine` → `horizontalLine`/`verticalLine`
  - `renderOutline` → `outline`, `renderItem` → `item`, `renderFakeItem` → `fakeItem`,
    `renderItemDecorations` → `itemDecorations`
  - `renderTooltip` → `tooltip`; deferred tooltips now use `setTooltipForNextFrame(...)`
    instead of an immediate `render*Tooltip` call
- `Renderable#render` → `extractRenderState`
- `AbstractWidget#renderWidget` → `extractWidgetRenderState`
- `AbstractContainerScreen`:
  - `renderLabels` → `extractLabels`
  - `renderBg` → **replaced by `Screen#extractBackground`** (not a rename — it moves up to `Screen`)
  - `renderTooltip` → `extractTooltip`
  - `renderContents`/`renderCarriedItem`/`renderSnapbackItem`/`renderSlots`/`renderSlot` →
    `extractContents`/`extractCarriedItem`/`extractSnapbackItem`/`extractSlots`/`extractSlot`
  - `render()` (the top-level entry point) now calls tooltip extraction automatically —
    **don't override the top-level render method**, override the specific hooks
  - Constructor can now take explicit `imageWidth`/`imageHeight` (default 176×166); these
    fields are now `final`
- `Screen`: `renderBackground`→`extractBackground`, `renderBlurredBackground`→
  `extractBlurredBackground`, `renderPanorama`→`extractPanorama`,
  `renderMenuBackground`→`extractMenuBackground`,
  `renderTransparentBackground`→`extractTransparentBackground`
- `ClickType` (used in `AbstractContainerMenu#clicked`, `AbstractContainerScreen#slotClicked`,
  `ServerboundContainerClickPacket`) **renamed → `ContainerInput`**

This explains essentially every error in every file under `blocks/screens/` (26 screen classes)
and `tools/buttons/**` (custom widgets — the "not abstract, does not override `extractContents`"
errors on `VEIOButton`/`ioMenuButton` are this).

Confirmed against NeoForge's own current docs (`docs.neoforged.net/docs/rendering/screens/`):
`extractBackground(GuiGraphicsExtractor, int, int, float)`,
`extractRenderState(GuiGraphicsExtractor, int, int, float)`,
`extractLabels(GuiGraphicsExtractor, int, int)` are the exact method signatures to implement now.

**Practical approach**: this is a mechanical, high-volume find/replace once the rename table
above is applied consistently. Do it file-by-file across `blocks/screens/**`, then
`tools/buttons/**`. Watch for the `drawString`/`text` overload taking a `Font` explicitly
(`Font#drawInBatch`-adjacent calls) vs. the `GuiGraphicsExtractor` instance methods — don't
conflate the two.

### 2. Block-entity NBT: `CompoundTag` → `ValueInput`/`ValueOutput` — **all tile entities**

`BlockEntity#saveAdditional`/`loadAdditional` no longer take a raw `CompoundTag`. They take
`ValueOutput`/`ValueInput` instead, and no longer separately take a `HolderLookup.Provider`
(it's folded into the Value{Input,Output} context):

```java
// 1.21.1
protected void saveAdditional(CompoundTag tag) { tag.putInt("value", this.value); }

// 26.1
@Override
protected void loadAdditional(ValueInput in) {
    super.loadAdditional(in);
    this.value = in.getIntOr("value", 0);
}

@Override
protected void saveAdditional(ValueOutput out) {
    super.saveAdditional(out);
    out.putInt("value", this.value);
}
```

- `ValueInput` has `get*`/`get*Or(key, default)` plus `read(key, Codec)`.
- `ValueOutput` has `put*` plus `store(key, Codec, value)`/`storeNullable`.
- If you need to bridge to/from a raw `CompoundTag` (e.g. for network sync or a legacy format),
  use `TagValueInput.create(problemReporter, registries, tag)` /
  `TagValueOutput.createWithContext(problemReporter, registries)`.
- `ContainerHelper#saveAllItems`/`loadAllItems` also now take `ValueOutput`/`ValueInput`.
- This directly explains the `VETileEntity.java:374` error
  (`applyImplicitComponents(DataComponentInput)` — see item 4 below, related but distinct) and
  is why `VETileEntity`, `VETileEntityFactory`, and every machine's save/load path need review —
  per `mem:blocks_structure`, tile-entity save/load logic was recently touched (commit
  `ad0825df` "Fixed all machines to store slot data and fluid data when mined") specifically
  targeting 1.21.1 `CompoundTag` APIs, so that work needs to be redone against `ValueInput`/
  `ValueOutput`.

### 3. Recipes are now a data-pack registry with a **record** `RecipeSerializer` — **~230 errors across recipe/serializer/JEI-category files**

`RecipeSerializer` is no longer a class you subclass — it's a **final record** constructed
directly with a `MapCodec` and `StreamCodec`:

```java
// 26.1
public static final RecipeSerializer<ExampleRecipe> EXAMPLE_RECIPE = new RecipeSerializer<>(
    MapCodec.unit(INSTANCE),      // or a real MapCodec<ExampleRecipe> if the recipe has fields
    StreamCodec.unit(INSTANCE)    // or a real StreamCodec<RegistryFriendlyByteBuf, ExampleRecipe>
);
```

This is exactly why the build reports `cannot inherit from final RecipeSerializer` and `cannot
infer type arguments for RecipeSerializer` for every one of this mod's ~14 custom recipe
serializers (Crusher, Compressor, Sawmill, DistillationUnit, ImplosionCompressor,
CentrifugalSeparator, CentrifugalAgitator, Electrolyzer, FluidMixer, FluidElectrolyzer,
DimensionalLaser, HydroponicIncubator, StirlingGenerator, PrimitiveBlastFurnace, ...). There is
no more inner `$Serializer` class or `fromNetwork`/`toNetwork`/`fromJson` overrides — everything
becomes codec definitions.

On top of that, every `Recipe` implementation now needs a `recipeBookCategory()` override
(new abstract method — this is the `X is not abstract and does not override abstract method
recipeBookCategory` errors) and a `placementInfo()` (ingredient placement for the recipe book —
built via `PlacementInfo.create(ingredient)`/`createFromOptionals(...)`).

Related recipe-system changes also in play:
- `getResultItem`/`getCategoryIconItem` are gone → replaced by a `RecipeDisplay` returned via
  `display()`.
- `canCraftInDimensions` is gone (folded into `matches`).
- `RecipeBuilder` no longer stores a plain `Item` result — builders now take `ItemStackTemplate`
  (an immutable sibling of `ItemStack`; see item 5).
- Datagen: `RecipeProvider` is **no longer a `DataProvider`** — register a
  `RecipeProvider.Runner` subclass instead (implement `createRecipeProvider` + `getName`), and
  `RecipeBuilder#save`/`RecipeOutput#accept` now take a `ResourceKey`, not a `ResourceLocation`.

**JEI categories** (`compat/jei/category/**`) fail for a parallel reason: the `X is not abstract
and does not override abstract method getHeight` errors are JEI's own API surface requiring a
`getHeight()` override on `IRecipeCategory` — this is a JEI 26.1 API change, not a vanilla one,
and needs to be checked against the JEI version pinned in `gradle.properties`
(`jei_version=29.4.0.23`) / JEI's own changelog if the primers don't explain a given error there.

### 4. Data components: `applyImplicitComponents(DataComponentInput)` → `(DataComponentGetter)` — small but foundational

`BlockEntity#applyImplicitComponents` (and the equivalent on `Entity`) used to take a
`DataComponentInput`; that type is gone, replaced by `DataComponentGetter` (the same interface
`BlockEntity`/`Entity` themselves now implement, for querying arbitrary component data). Fixes
`VETileEntity.java:374` directly. `TypedEntityData` also replaces `CustomData` for the
`ENTITY_DATA`/`BLOCK_ENTITY_DATA` components if the mod touches those.

### 5. `Level#isClientSide`, `Level#random`, `ChunkPos.x`/`.z` fields all made private/protected — ~50 errors

- `Level#isClientSide` field → private. Replace every bare `level.isClientSide` field read with
  `level.isClientSide()` method call.
- `Level#random` field → protected (was public). Use `level.getRandom()`.
- `ChunkPos` is now a **record** — `x`/`z` are no longer public fields accessible the old way in
  all contexts; use the accessor pattern the record provides, and update any
  `new ChunkPos(BlockPos)`/`new ChunkPos(long)` calls to `ChunkPos.containing(...)`/
  `ChunkPos.unpack(...)`, and `toLong`/`asLong` to `pack()`.

### 6. `DirectionProperty` removed — 2 files (`FaceableBlock`, `SawmillBlock`)

`DirectionProperty` the class is gone. All vanilla `Property` subclasses (`BooleanProperty`,
`EnumProperty`, `IntegerProperty`) are now `final` and must be built via their `create(...)`
factories. Since `BlockStateProperties.HORIZONTAL_FACING` still exists and already returns the
right type, the actual fix is almost certainly just changing the **declared type** of the
`FACING` field from `DirectionProperty` to `EnumProperty<Direction>` — check what
`BlockStateProperties.HORIZONTAL_FACING`'s type is in the 26.1 mappings and match it.

### 7. NeoForge capability/transfer API rewrite — **not in the vanilla primers, separate pass needed**

The vanilla Mojang primers (which is what all the above is sourced from) say nothing about
`IItemHandler`/`ItemStackHandler`/`IEnergyStorage`/`EnergyStorage`/`IFluidHandler`/`FluidTank`/
`InvWrapper`/`SlotItemHandler`/`INBTSerializable` — these are 100% NeoForge-specific. The
compiler currently reports these only as **deprecation warnings** ("deprecated and marked for
removal"), not hard errors, except for `INBTSerializable` which is a hard `cannot find symbol` —
that one interface has already been deleted from `net.neoforged.neoforge.common.util` (breaks
`VEEnergyStorage.java`).

NeoForge replaced the whole item/fluid/energy capability system in the "Transfer Rework"
(NeoForge 21.9, i.e. mid-1.21.x — carried forward unchanged into 26.1):

- New core interface **`ResourceHandler<T extends Resource>`** replaces both `IItemHandler` and
  `IFluidHandler`. Operations are `insert()`/`extract()`, working per-slot or across the whole
  handler.
- **`EnergyHandler`** replaces `IEnergyStorage` (same shape, no "resource" concept since energy
  has no variants).
- Resources are immutable, hashable descriptors with no amount attached — `ItemResource` (item +
  immutable component map), `FluidResource` (fluid + component map). Amounts are tracked
  separately via `ResourceStack` (resource + amount).
- Mutations happen inside a `Transaction` (open → checkpoint, commit → validate & discard
  checkpoint, abort → roll back) — replaces the old `FluidAction.EXECUTE`/`SIMULATE` boolean
  dance this mod currently uses throughout `VERelationalTank`/`VETileEntity`
  (`IFluidHandler.FluidAction.EXECUTE`/`SIMULATE` calls are exactly what's flagged as
  deprecated-for-removal in the build log).
- To implement custom handlers, extend NeoForge's provided base classes rather than the old
  ones: `ItemStacksResourceHandler`/`ItemAccessItemHandler` (item inventories),
  `FluidStacksResourceHandler`/`StacksResourceHandler<FluidResource>` (fluid tanks),
  `SimpleEnergyHandler` (energy storage) — these map fairly directly onto this mod's existing
  `VEItemStackHandler`, `VERelationalTank`, and `VEEnergyStorage` custom classes.
- **Migration bridges exist** for a staged port: `IItemHandler.of(handler)`,
  `IFluidHandler.of(handler)`, `IEnergyStorage.of(handler)` wrap a new-style handler as the old
  interface, so old call sites can keep compiling against a new-style handler underneath while
  you migrate call-by-call. Given this mod's energy system (`VEEnergyStorage`,
  `tools/energy/**`) and fluid tank system (`VERelationalTank`, `util/MultiSlotWrapper`,
  `util/MultiFluidSlotWrapper`) are used pervasively across ~30+ files, using the bridge
  adapters to get a compiling build first, then migrating to the native `ResourceHandler`/
  `EnergyHandler` APIs machine-by-machine, is very likely the lower-risk path.
- **Action item**: read NeoForge's own capabilities docs
  (docs.neoforged.net — the "Data Storage → Capabilities" and "Transfer" sections, correct URL
  needs to be re-verified since `docs.neoforged.net/docs/datastorage/capabilities/` currently
  404s; versioned paths like `docs.neoforged.net/docs/1.21.4/datastorage/capabilities/` exist,
  so the unversioned/current path may have moved) before touching `VEEnergyStorage`,
  `VERelationalTank`, `VEItemStackHandler`, or `VESlotManager`.

### 8. `BlockEntityRenderer` render → extract/submit split — custom animated machine textures

Directly relevant to this mod's recent animation work (commit `6606cb74` "Animate front of Air
Compressor..."). `BlockEntityRenderer#render(BlockEntity, float, PoseStack, MultiBufferSource,
int, int)` **is gone**. It's replaced by a three-piece pattern mirroring the entity-renderer
render-state pattern:

```java
public class ExampleRenderState extends BlockEntityRenderState {
    public float partialTick; // whatever custom animation-driving fields are needed
}

public class ExampleBlockEntityRenderer
        implements BlockEntityRenderer<ExampleBlockEntity, ExampleRenderState> {

    public ExampleBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) { /* materials from ctx */ }

    @Override
    public ExampleRenderState createRenderState() { return new ExampleRenderState(); }

    @Override
    public void extractRenderState(ExampleBlockEntity be, ExampleRenderState state, float partialTick,
                                    Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);
        state.partialTick = partialTick; // pull whatever the animation needs from the BE HERE
    }

    @Override
    public void submit(ExampleRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
                        CameraRenderState camera) {
        collector.submitModel(..., /* use only state fields, no BE access here */);
    }
}
```

The rule: **read from the live `BlockEntity` only inside `extractRenderState`**, and **push
geometry using only the render-state's captured fields inside `submit`** (no `BlockEntity`
access there at all). Any inline "compute animation frame index / UV offset during render()"
logic in `client/renderers/entity/LaserBlockEntityRenderer.java` (and any other custom BE
renderer) needs to be split across these two methods accordingly.

Related:
- `BlockEntityRendererProvider.Context` is now a record (`MaterialSet` + `PlayerSkinRenderCache`).
- `TextureSheetParticle` removed → merged into `SingleQuadParticle` (if the mod has custom
  particles, e.g. machine steam/smoke).
- `RegisterRenderers#registerBlockEntityRenderer` signature changed to match the new provider
  shape — this is the `method registerBlockEntityRenderer in class RegisterRenderers cannot be
  applied to given types` error in `VoluminousEnergy.java`/setup code.

### 9. Fluid texture/tint moved out of the renderer entirely — custom fluid textures

`LiquidBlockRenderer` renamed → `FluidRenderer`. Still/flowing textures and tint are no longer
parameters to the renderer — they're defined once via a new `FluidModel` record (the fluid
equivalent of `BlockModel`):

```java
FluidModel.Unbaked exampleFluidModel = new FluidModel.Unbaked(
    new Material(Identifier.fromNamespaceAndPath("voluminousenergy", "block/example_fluid_still"), true),
    new Material(Identifier.fromNamespaceAndPath("voluminousenergy", "block/example_fluid_flowing")),
    null, // optional overlay material for occluded sides
    null  // optional BlockTintSource for tint
);
```

`ItemBlockRenderTypes` (the old manual block/fluid render-layer lookup map) is **removed
entirely** — render layer is now auto-computed from texture transparency at model-load time
(see item 10). This explains the `getStillTexture`/`getTintColor`/`ItemBlockRenderTypes` errors
in `fluids/VEFlowingGasFluid.java` and `util/extensions/VEFluidClientExtension.java`. Given this
mod has a large custom fluid chain (crude oil → gasoline/diesel/naphtha, acids, nitrogen chain —
per `mem:core`), every custom `Fluid`/`FluidType` client extension needs its texture/tint
definition moved to this new model.

### 10. Block/item render layer is now auto-detected from texture alpha — verify after port

`ItemBlockRenderTypes`/manual "this block is CUTOUT/TRANSLUCENT/SOLID" declarations are gone.
The loader now inspects each texture's alpha channel at load time: any translucent pixel (alpha
neither 0 nor 255) → `TRANSLUCENT` layer; any fully-transparent pixel → `CUTOUT`; otherwise
`SOLID`. A model JSON texture entry can force translucency explicitly:
```json5
"textures": { "pane": { "sprite": "minecraft:block/glass", "force_translucent": true } }
```
**Action item**: after the port compiles, visually re-check every semi-transparent custom
texture (tank glass overlays, energy/fluid indicator overlays, animated glow-frame textures) —
stray anti-aliased/resized pixels with partial alpha can silently flip a texture that should be
`CUTOUT` into `TRANSLUCENT` unless cleaned up or `force_translucent` is set deliberately.

---

## Other confirmed changes worth knowing about (lower volume, still real)

- **Item Instances / `ItemStackTemplate`**: a new immutable sibling of `ItemStack` used by recipe
  results, advancement icons, bundle contents, and most recipe builders now. `ItemStack` and
  `ItemStackTemplate` share a common `ItemInstance` interface. Recipe builders' constructors are
  now private — use static factory methods, passing `ItemStackTemplate` for the result.
- **Tool/weapon/armor classes removed** (this happened earlier, at 1.21.5, so it's already
  "old" by 26.1 but still needs handling if not already done): `SwordItem`, `DiggerItem`
  (`PickaxeItem`/`AxeItem`/`HoeItem`/`ShovelItem`), `ArmorItem`, `TieredItem` are **all gone**.
  This mod's `items/tools/VESwordItem`, `VEPickaxeItem`, `VEAxeItem`, `VEHoeItem`,
  `VEShovelItem`, `VEItemToolTier` all show compile errors for exactly this reason — they need
  to become plain `Item` subclasses configured via `Item.Properties#tool()/pickaxe()/sword()/
  axe()/hoe()/shovel()` plus the `DataComponents#TOOL`/`WEAPON` components, and `Tier`/`Tiers` →
  `ToolMaterial` (now a record).
- **`RecipesUpdatedEvent`/`RenderHighlightEvent` package moves** — these show as "cannot find
  symbol"/"package does not exist"; both are vanilla event classes that were relocated as part
  of the broader package reshuffle alongside the `Identifier` rename (see next point). Find the
  new package path via the decompiled sources (see "Ground truth beyond the primers" below)
  rather than guessing.
- **`ResourceLocation` → `Identifier`, plus a large companion package reshuffle** — the repo has
  already started this (commit `be340a60`), but the 1.21.11 primer notes this rename came
  bundled with moving a large number of client model/entity classes into subpackages (e.g.
  `net.minecraft.world.entity.animal.Cow` → `.cow.Cow`, `net.minecraft.world.entity.monster.Zombie`
  → `.zombie.Zombie`) and `net.minecraft.util`/`net.minecraft.advancements.critereon` package
  consolidations. If any mod code extends/imports vanilla entity or model classes directly
  (check `client/renderers/entity/**`), those import paths need updating too, not just
  `ResourceLocation` itself.
- **`RegisterRenderers`, `RecipesUpdatedEvent`, `ExistingFileHelper`, `DimensionDataStorage`
  (→ `SavedDataStorage`)** — all show up as missing symbols; each is a real rename/relocation,
  not a fluke — cross-reference each against the decompiled 26.1 sources (see below) since
  they're NeoForge/datagen-adjacent and not always covered in the vanilla-only primers.
- **World generation**: `RandomPatchFeature` (and `Feature#RANDOM_PATCH`/`FLOWER`/
  `NO_BONEMEAL_FLOWER`) completely removed — any ore-patch/vegetation-patch-style placed feature
  (this mod's ore deposit/world features under `world/feature/**`, `world/modifiers/**`) needs
  rewriting as an explicit placement chain (`CountPlacement` + `RandomOffsetPlacement` +
  `BlockPredicateFilter`) around a `minecraft:simple_block` configured feature.
- **`Entity#interactAt` removed**, merged into `Entity#interact(Player, InteractionHand, Vec3)` —
  relevant if any custom entity in the mod overrides the old precise-hit-location method.

---

## Ground truth beyond the primers

For anything the vanilla primers don't cover (NeoForge-specific renames, or a symbol whose new
name isn't obvious from the primer text), the actual decompiled 26.1 + NeoForge sources are
already sitting in the local Gradle cache from this session's build attempts, and are the most
reliable source of truth — faster and more precise than guessing from changelogs:

```bash
# Compiled (not source, but javap-able) joined Minecraft classes live under:
find ~/.gradle/caches/ng_execute -iname "*.class" -path "*classes*"
# e.g. already confirmed to contain net/minecraft/client/gui/GuiGraphics.class,
# net/minecraft/world/level/Level.class, etc. — use `javap` against these for
# exact current method signatures when a primer is ambiguous.
```

Serena's Java language server is active for this project but **its classpath is not currently
synced to the Gradle 26.1 model** (it reports even `java.lang.Object`/`String` as unresolvable,
meaning it hasn't picked up the toolchain/dependencies at all) — don't trust
`get_diagnostics_for_file` output until that's fixed; `javac` via `./gradlew compileJava` is the
reliable signal right now.

---

## Suggested port order

1. Finish the in-progress `ResourceLocation` → `Identifier` rename (root cause list item omitted
   above only because it's already underway) and the companion package-path updates for any
   directly-imported vanilla entity/model classes.
2. Block-entity NBT: `CompoundTag` → `ValueInput`/`ValueOutput` (root cause 2) — foundational,
   touches every tile entity's save/load.
3. `DataComponentInput` → `DataComponentGetter` (root cause 4) — small, unblocks `VETileEntity`.
4. `DirectionProperty` removal (root cause 6) — trivial, 2 files.
5. `Level#isClientSide`/`#random`, `ChunkPos` (root cause 5) — mechanical, spread across many
   files but low-risk find/replace.
6. GUI extractor rewrite (root cause 1) — largest single mechanical batch; do all 26 screens +
   `tools/buttons/**` together since they share the same rename table.
7. `BlockEntityRenderer` extract/submit split (root cause 8) — do alongside/after screens since
   both are rendering-pipeline work, and this mod's animation logic (Air Compressor, Pump,
   Aqueoulizer per recent commits) lives here.
8. Fluid rendering/`FluidModel` (root cause 9) — do together with BE renderers.
9. Recipes as registry format + record `RecipeSerializer` + JEI category `getHeight()` (root
   cause 3) — large, but self-contained; can be done independently of 6–8.
10. Capabilities/transfer API (root cause 7) — use the `IItemHandler.of()`/`IFluidHandler.of()`/
    `IEnergyStorage.of()` bridge adapters first to get a compiling, runnable build, then migrate
    `VEEnergyStorage`/`VERelationalTank`/`VEItemStackHandler` to the native `EnergyHandler`/
    `ResourceHandler` APIs as a follow-up pass (these are currently only warnings, not compile
    errors, except `INBTSerializable` which is a hard error in `VEEnergyStorage.java`).
11. Remaining long-tail items (tool/weapon class removal, world-gen `RandomPatchFeature`,
    `RecipesUpdatedEvent`/misc package moves, item-instance/`ItemStackTemplate` in recipe
    builders) — pick these up as `compileJava` re-runs surface them; most are one- or two-file
    fixes once the big buckets above are cleared.
12. After a clean `compileJava`: `runClientData` to confirm datagen (recipe/tag providers changed
    shape — root cause 3 / datagen notes above), then `runClient` to visually verify every
    screen and animated machine texture, paying particular attention to the auto-detected
    translucent/cutout render layers (root cause 10).

---

## Appendix: every file currently failing to compile, by error count

(From `./gradlew compileJava` with `-Xmaxerrs 10000`, 1,095 errors / 186 files. Counts are a
rough proxy for how much a file is affected, not a precise fix-time estimate — a file hit by one
root cause across many lines will have a high count for a mechanical, fast fix.)

```
40  items/tools/VETools.java
30  blocks/tiles/VETileEntity.java
22  blocks/containers/VEContainerFactory.java
17  items/tools/FluidScanner.java
16  tools/VERender.java
16  datagen/LootSpawns.java
16  client/renderers/entity/LaserBlockEntityRenderer.java
16  blocks/screens/DimensionalLaserScreen.java
15  util/MultiSlotWrapper.java
15  items/tools/multitool/bits/VEMultitoolBitData.java
15  blocks/screens/BlastFurnaceScreen.java
14  items/AmmoniumNitrateBucket.java
14  blocks/screens/tank/TankScreen.java
14  blocks/screens/StirlingGeneratorScreen.java
14  blocks/screens/SawmillScreen.java
14  blocks/screens/PrimitiveStirlingGeneratorScreen.java
14  blocks/screens/PrimitiveBlastFurnaceScreen.java
14  blocks/screens/ImplosionCompressorScreen.java
14  blocks/screens/HydroponicIncubatorScreen.java
14  blocks/screens/GasFiredFurnaceScreen.java
14  blocks/screens/FluidMixerScreen.java
14  blocks/screens/FluidElectrolyzerScreen.java
14  blocks/screens/ElectrolyzerScreen.java
14  blocks/screens/ElectricFurnaceScreen.java
14  blocks/screens/DistillationUnitScreen.java
14  blocks/screens/CrusherScreen.java
14  blocks/screens/CompressorScreen.java
14  blocks/screens/CombustionGeneratorScreen.java
14  blocks/screens/CentrifugalSeparatorScreen.java
14  blocks/screens/CentrifugalAgitatorScreen.java
14  blocks/screens/AqueoulizerScreen.java
13  VoluminousEnergy.java
13  recipe/VERecipe.java
13  blocks/screens/SolarPanelScreen.java
13  blocks/screens/PumpScreen.java
13  blocks/screens/PrimitiveSolarPanelScreen.java
13  blocks/screens/BatteryBoxScreen.java
13  blocks/screens/AirCompressorScreen.java
12  items/tools/VESwordItem.java
12  items/tools/VEPickaxeItem.java
12  fluids/VEFlowingGasFluid.java
12  compat/jei/category/IndustrialBlastingCategory.java
12  blocks/screens/ToolingStationScreen.java
11  items/tools/CreativeFluidScanner.java
10  util/VERelationalTank.java
10  util/recipe/VERecipeCodecs.java
10  items/tools/multitool/Multitool.java
10  compat/jei/category/DistillingCategory.java
10  compat/jei/category/CrushingCategory.java
9   compat/jei/category/CompressingCategory.java
8   world/feature/GeyserFeature.java
8   util/VEAttachments.java
8   util/MultiFluidSlotWrapper.java
8   items/tools/VEItemToolTier.java
8   events/VEGenericListener.java
8   compat/jei/category/SawmillCategory.java
8   compat/jei/category/DimensionalLasingCategory.java
7   tools/energy/VEEnergyStorage.java
7   recipe/SawmillRecipe.java
7   persistence/ChunkFluids.java
7   compat/jei/category/ElectrolyzingCategory.java
7   blocks/containers/VEContainer.java
7   blocks/blocks/crops/VEWaterCrop.java
6   tools/buttons/tanks/TankDirectionButton.java
6   tools/buttons/tanks/TankBoolButton.java
6   tools/buttons/slots/SlotDirectionButton.java
6   tools/buttons/slots/SlotBoolButton.java
6   tools/buttons/ioMenuButton.java
6   tools/buttons/batteryBox/BatteryBoxSlotPairButton.java
6   tools/buttons/batteryBox/BatteryBoxSendOutPowerButton.java
6   recipe/StirlingGeneratorRecipe.java
6   recipe/PrimitiveBlastFurnaceRecipe.java
6   recipe/ImplosionCompressorRecipe.java
6   recipe/ElectrolyzerRecipe.java
6   recipe/CrusherRecipe.java
6   recipe/CompressorRecipe.java
6   recipe/CentrifugalSeparatorRecipe.java
6   items/crops/WaterCropItem.java
6   compat/jei/category/PrimitiveBlastingCategory.java
6   compat/jei/category/CombustionCategory.java
6   compat/jei/category/CentrifugalSeparationCategory.java
5   util/tiles/CapabilityMap.java
5   recipe/IndustrialBlastingRecipe.java
5   recipe/HydroponicIncubatorRecipe.java
5   recipe/FluidMixerRecipe.java
5   recipe/FluidElectrolyzerRecipe.java
5   recipe/DistillationRecipe.java
5   recipe/DimensionalLaserRecipe.java
5   recipe/CombustionGeneratorRecipe.java
5   recipe/CentrifugalAgitatorRecipe.java
5   recipe/AqueoulizerRecipe.java
5   loot/modifiers/MysteriousMultiplierModifier.java
5   items/tools/VEShovelItem.java
5   items/tools/VEHoeItem.java
5   items/tools/VEAxeItem.java
5   items/tools/multitool/bits/BitItemData.java
5   compat/jei/category/StirlingCategory.java
5   compat/jei/category/ImplosionCompressionCategory.java
5   compat/jei/category/HydroponicIncubatorCategory.java
5   compat/jei/category/AqueoulizingCategory.java
5   blocks/containers/iolisteners/ToolingStationSlotWithIOListening.java
5   blocks/blocks/machines/SawmillBlock.java
4   util/extensions/VEFluidClientExtension.java
4   tools/sidemanager/VESlotManager.java
4   recipe/processor/GasFiredFurnaceProcessor.java
4   recipe/processor/ElectricFurnaceProcessor.java
4   persistence/ChunkFluid.java
4   events/VEClientSideListener.java
4   compat/jei/category/FluidMixingCategory.java
4   compat/jei/category/FluidElectrolyzingCategory.java
4   compat/jei/category/CentrifugalAgitationCategory.java
4   blocks/containers/iolisteners/SlotWithIOListener.java
4   blocks/containers/iolisteners/ExperienceListener.java
4   blocks/blocks/crops/VELandCrop.java
3   world/feature/VEOreDepositFeature.java
3   util/WorldUtil.java
3   util/ExperienceHelper.java
3   items/upgrades/MysteriousMultiplier.java
3   items/batteries/VEEnergyItem.java
3   blocks/tiles/VETileEntityFactory.java
3   blocks/blocks/ores/VEOreBlock.java
2   util/recipe/FluidIngredient.java
2   tools/buttons/batteryBox/VEBatterySwitchManager.java
2   loot/modifiers/AnimalFatLootModifier.java
2   items/tools/RFIDChip.java
2   fluids/WhiteFumingNitricAcid.java
2   fluids/SulfuricAcid.java
2   fluids/RedFumingNitricAcid.java
2   datagen/VETagDataGenerator.java
2   blocks/tiles/inventory/GasFiredFurnaceInventoryValidator.java
2   blocks/tiles/inventory/FurnaceInventoryValidator.java
2   blocks/screens/tank/TitaniumTankScreen.java
2   blocks/screens/tank/SolariumTankScreen.java
2   blocks/screens/tank/NighaliteTankScreen.java
2   blocks/screens/tank/NetheriteTankScreen.java
2   blocks/screens/tank/EighzoTankScreen.java
2   blocks/screens/tank/AluminumTankScreen.java
2   blocks/blocks/util/FaceableBlock.java
2   blocks/blocks/machines/VEFaceableMachineBlock.java
2   blocks/blocks/machines/tanks/TankBlock.java
2   blocks/blocks/machines/PressureLadder.java
2   blocks/blocks/DimensionalLaserBlock.java
1   (36 more files with 1 error each — mostly fluids/**, items/solid_fuels/**, single-symbol fixes)
```

Re-run `JAVA_HOME=/opt/homebrew/opt/openjdk@25 ./gradlew compileJava --console=plain` after each
root-cause batch to get a fresh, shrinking list — with `-Xmaxerrs 10000` now in `build.gradle`
you'll always see the complete picture in one pass instead of javac's default 100-error cutoff.
