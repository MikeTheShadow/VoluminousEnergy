## ADDED Requirements

### Requirement: Machines recheck their recipe when a menu changes an input slot

A machine SHALL recheck whether its inputs can run a recipe whenever a player changes one of its input
slots through its menu, whether by placing a stack by hand or by shift-clicking from the player
inventory, including a shift-click that grows a stack already in the slot. A machine whose inputs
become enough for a recipe this way SHALL start it without any further inventory change.

#### Scenario: Shift-click completes a partial input stack

- **WHEN** an Electrolyzer's input slot holds 10 Bauxite Dust, fewer than its recipe needs, a player
  with its menu open holds 2 Bauxite Dust in their inventory, and shift-clicks them
- **THEN** the input slot holds 12 Bauxite Dust and the Electrolyzer starts the recipe

#### Scenario: A partial stack alone does not start the recipe

- **WHEN** an Electrolyzer's input slot holds 10 Bauxite Dust and nothing else changes
- **THEN** the Electrolyzer stays idle
