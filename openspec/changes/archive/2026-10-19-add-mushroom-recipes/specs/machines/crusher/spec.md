## Purpose

Defines which plant items the Crusher accepts as organic input and what Shredded Biomass and
by-products it yields for them.

## ADDED Requirements

### Requirement: Crusher shreds mushrooms

The Crusher SHALL accept every item in the `c:mushrooms` tag, which includes Brown Mushroom and Red
Mushroom, each processed one item at a time in 200 ticks with no experience, and SHALL yield 1
Shredded Biomass.

#### Scenario: Crushing a Brown Mushroom

- **WHEN** a powered Crusher has 1 Brown Mushroom in its input slot and empty output slots
- **THEN** after 200 ticks the main output holds 1 Shredded Biomass, the RNG slot is empty, and the
  input is consumed
