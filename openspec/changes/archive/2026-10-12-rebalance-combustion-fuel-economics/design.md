## Context

See proposal.md for the motivation and the player-visible numbers. The current implementation:

- `CombustionGeneratorProcessor.validateRecipe` checks both tanks against the constant
  `COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT` (250), drains a literal 250 mB from each, and sets the burn
  counter to `Config.COMBUSTION_GENERATOR_FIXED_TICK_TIME / 4` (or `COMBUSTION_GENERATOR_PROCESS_TIME / 4`
  in "Balanced Mode", which is the same 1600). "Fixed Process Time" therefore means ticks per bucket today,
  and a 250 mB burn lasts a quarter of it.
- Burn eligibility goes through recipe matching first. Every `fuel_combustion` recipe lists the fuel tag at
  250 mB and the `voluminousenergy:oxidizer` tag at 250 mB, and `BasicParser.isCompleteRecipe` rejects a
  tank holding less than an ingredient's amount. Any per-oxidizer amount below 250 is blocked there before
  the processor sees it.
- `OxidizerFluidsData` loads `fluid_data/oxidizers/*.json` through `VERecipeCodecs.REGISTRY_OXIDIZER_FLUID_CODEC`
  (tag or fluid plus `multiplier`) into a `HashMap<Fluid, Float>`. `VEServerSideListener` sends that map
  to clients in `ClientBoundFluidDataPacket`, where JEI reads it.
- `CombustionCategory` (JEI) computes volumetric energy as `rating * 1600` and FE/t as that value divided
  by "Fixed Process Time", and lists every oxidizer at a fixed 250 mB.
- The yield changes are hand-written recipe JSON in `src/main/resources`; none comes from datagen.

## Goals / Non-Goals

**Goals:**

- One source of truth for an oxidizer's per-burn amount (its fluid data), used by the processor on the
  server and by JEI on the client.
- Keep the Combustion Generator assembled as it is: same `VETileEntityFactory` entry, tanks, container,
  screen, and recipe type. Only the processor, data, and JEI category change.

**Non-Goals:**

- No change to how fuel is consumed (still 250 mB per burn) or to fuel FE/t ratings.
- No rework of "Balanced Mode" beyond keeping it consistent with the fixed path.

## Decisions

### Burn length: drop the divide-by-4, keep the 1600 default

The processor sets the counter to "Fixed Process Time" (and to `COMBUSTION_GENERATOR_PROCESS_TIME` in
Balanced Mode) without dividing by 4, and the config comment says the value is ticks per 250 mB burn.

Alternative considered: keep the divide and raise the default to 6400. Rejected because NeoForge keeps the
value already written in an existing config file, so most installs would never see the rebalance, and a
value that means "per bucket" while the machine burns per 250 mB is the confusion that started this.

### Oxidizer data carries `multiplier` and `amount`

Replace the oxidizer codec in `VERecipeCodecs` with a record whose `multiplier` and `amount` fields are
both required (`Codec.INT.fieldOf("amount")`), and resolve its tag or fluid the same way
`RegistryFluidValue.getAsValuePair()` does today (extract the shared lookup rather than copy it). An entry
missing `amount` fails to parse and is logged through the existing `resultOrPartial` path; there is no
default, by decision. `OxidizerFluidsData` caches a small record per fluid (multiplier and amount) instead
of a bare `Float`, keeps `getOxidizerMultiplier`, and gains an amount lookup beside it.

Alternative considered: a second `HashMap<Fluid, Integer>` beside the multiplier map. Rejected because two
maps filled and synced separately can drift, and the packet would grow a second parallel codec.

### Network packet sends the record

`ClientBoundFluidDataPacket` replaces its oxidizer `FLUID_FLOAT_HASH_MAP_STREAM_CODEC` with a stream codec
that writes the multiplier and amount per fluid, and `updateFromPacket` takes the new map. The payload id
stays the same; mixing client and server builds from before and after this change fails to decode, which
is acceptable because both sides must run the same 0.5 build anyway.

### Recipe gate lowered to 25 mB; the processor enforces each oxidizer's amount

The eleven built-in `fuel_combustion` recipes, Natural Gas's included, lower their `voluminousenergy:oxidizer` ingredient to 25 mB,
the smallest built-in amount. The tag ingredient stays, because it is also what lets only oxidizers into
the oxidizer tank and what JEI lays out. The processor then checks the oxidizer tank against
`OxidizerFluidsData`'s amount for the fluid in it and drains exactly that; the fuel side keeps the 250 mB
constant and replaces its literal `drain(250, ...)` with it.

Alternatives considered: special-casing the oxidizer slot in `BasicParser` (it is shared by every machine,
so a Combustion-only rule does not belong there), or removing the oxidizer ingredient from the recipes
(loses the tank filter and the JEI slot).

### Yields are recipe JSON edits only

Liquefied coal and coke (`aqueoulizing`), crude oil (`distilling`), and red fuming nitric acid
(`centrifugal_agitating/rfna.json`) change amounts only. Process times stay at 200 ticks, so the Agitator
and Aqueoulizer now move 500 mB per operation; both are within the 4000 mB default tank capacity.

### JEI derives its numbers from config and data

`CombustionCategory` reads the oxidizer its slot is displaying and computes FE/t as the fuel's rating times
that oxidizer's multiplier (the generator's own formula), volumetric energy as that FE/t × "Fixed Process
Time" × (1000 / 250), and builds the oxidizer slot from each oxidizer's amount. Before this change JEI
always showed the compressed-air rating, which disagreed with a running generator on oxygen or N₂O₄.

## Risks / Trade-offs

- [Packs that tuned "Fixed Process Time" get 4× their intended burn] → The proposal flags it as a config
  meaning change, and the release notes should say to divide a custom value by 4.
- [A datapack oxidizer with an amount below 25 mB cannot start a burn] → Its pack lowers the oxidizer amount
  in its `fuel_combustion` recipes; the built-in data never goes below 25.
- [Custom `fuel_combustion` recipes that still ask for 250 mB oxidizer gate oxygen and N₂O₄ at 250] →
  Burns still work once 250 mB is present; documented in the proposal's Impact.
- [FE inflation] → Diesel reaches about 1.0M FE per bucket on air and 4.0M on N₂O₄; FE/t and the
  generator's buffer and transfer limits are unchanged, so no single generator outputs more per tick than before.
- [Liquid routes now rival the Stirling for coal and coke] → Intended: on air, liquefied coal returns about
  what the coal gives in a Stirling, and oxidizers are what make liquefying worth the machines.

## Migration Plan

None, by decision: no transition or compatibility code for saves, configs, or datapacks.
