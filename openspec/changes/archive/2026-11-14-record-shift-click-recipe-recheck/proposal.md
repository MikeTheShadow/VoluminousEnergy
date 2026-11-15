## Why

Commit 5a648f85 fixed machines not starting a recipe when a shift-click tops up a partial input stack,
but it landed without an OpenSpec change, so no spec states the behaviour it guarantees. This change
records it after the fact. It changes no code.

## What Changes

- **Machines recheck their recipe after any input change made in their menu.** Today a player who
  shift-clicks items onto a partial input stack, completing the amount a recipe needs, sees the
  machine start, as it does when the items are placed by hand. Before 5a648f85 the machine kept
  idling until something else changed its inventory, because the menu reported a stack grown in
  place to an empty container instead of the machine.

Compatibility:

- **Saves, network, registry ids, config, datapacks:** none changed.

Other branches: 5a648f85 is on `1.21.1-dev` and merged forward into `26.1-dev`, `26.2-dev` and
`26.3-dev`; this record merges forward the same way.

## Capabilities

### New Capabilities

- `machines/recipe-inputs`: when a machine notices that its input slots can run a recipe.

### Modified Capabilities

None.

## Impact

- **Code:** none in this change. The fix it records is in `blocks/containers/VEContainerFactory.java`
  (the slot's `setChanged`) and `blocks/tiles/inventory/VEItemStackHandler.java`
  (`onStackChangedInPlace`).
- **Gametests:** `recipe_shift_click_top_up_starts_recipe` in `RecipeTests`, added by the same commit.
