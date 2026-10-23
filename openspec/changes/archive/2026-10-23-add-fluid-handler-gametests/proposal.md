## Why

How pipes fill and drain multi-tank machines has never been checked except by hand, and the 26.3
port showed how easily it regresses: a change there let one fluid overflow a Fluid Mixer's first
input tank into its second and stall the machine. Reading the 1.21.1 `MultiFluidSlotWrapper` also
turns up likely gaps that no one has confirmed in game: an empty machine accepting any fluid, a
disabled side still accepting fills, and an unnamed drain ignoring the side setting. 1.21.1 has no
automated tests, so each of these can only be found and re-checked in a client.

## What Changes

- No gameplay change. Players and modpacks see nothing new; nothing ships in the mod jar.
- `./gradlew runGameTestServer` runs Voluminous Energy gametests on 1.21.1 for the first time, from
  a separate `gametest` source set laid out like the one on 26.3-dev (same paths, same helper names)
  so forward merges can keep the 26.3 side.
- Required gametests pin the fluid behaviour that is correct today on real machines: fill routing
  and overflow on the Fluid Mixer, draining two output tanks on the Fluid Electrolyzer, side and
  `Allow Extraction from Input Tanks` rules on drains, capacity clamping, held-fluid refusal,
  simulated transfers, and empty or zero-amount requests.
- Optional gametests (vanilla's `required = false`) assert the intended behaviour for the gaps found
  by reading the code. They report as failures in the run summary without failing the run. Each
  follow-up fix makes its test required.
- `AGENTS.md`, `openspec/config.yaml`, and the Serena `suggested_commands` memory stop saying there
  are no gametests and name the new verification step.
- No save, data component, attachment, registry id, or config change. The startup config option
  `Allow Extraction from Input Tanks` is toggled only inside a test and restored afterwards.

## Capabilities

### New Capabilities

- `machines/storage-and-transfer`: how neighbouring blocks fill and drain a machine's tanks, as far
  as 1.21.1 does it correctly today. The requirement names match 26.3-dev's spec of the same path so
  forward merges resolve by keeping one side.

### Modified Capabilities

None.

## Impact

- `build.gradle`: a `gametest` source set and its configurations, added to the `gameTestServer` and
  `server` runs.
- New `src/gametest/java/com/veteam/voluminousenergy/gametest/`: `VEGameTests`,
  `GameTestDefinition`, `GameTestLog`, `MachineKit`, `StorageTests`.
- New `src/gametest/resources/data/voluminousenergy/structure/gametest/empty_5x5x5.nbt`.
- `AGENTS.md`, `openspec/config.yaml`, `.serena/memories/suggested_commands.md`.
- No change under `src/main`.

### Follow-ups (not in scope)

- Make a disabled tank side refuse fills and unnamed drains, as a disabled item slot already refuses
  hoppers, with a plan for existing pipe setups that fill through a disabled side.
- Stop empty tanks accepting any fluid: Lava into an empty Distillation Unit, Nitrogen piped first
  into the Fluid Mixer's Hydrogen tank, an oxidizer into an empty Combustion Generator's fuel tank.
- Return `false` instead of throwing for an out-of-range tank index in `isFluidValid`.
- Forward port: merging into 26.1-dev and 26.2-dev brings the gametest source set to branches whose
  gametests register through the test function registry, so `VEGameTests` needs 26.3's registration
  there. On 26.3-dev keep its harness, add the `required` flag to its `GameTestDefinition`, and port
  the new fluid tests to the transfer API. 26.3's `storage_fluid_fill_rules` Lava check passes only
  because the tank is already full of Crude Oil; the new empty-machine test closes that gap there too.
