# Excavator Pickaxe Plugin Plan

## Overview

A Paper 1.21.1 server-side plugin that introduces operator-created excavator pickaxes capable of mining cubic regions:

- 2x2x2
- 3x3x3
- 5x5x5

The plugin should:

- preserve vanilla drops
- support Fortune
- support Silk Touch
- support Mending
- apply Unbreaking logic correctly
- respect protections
- skip tile entities
- support creative mode properly
- avoid recursion and chunk-loading issues
- remain performant and extensible

---

# Technical Stack

## Platform

- Paper 1.21.1
- Java 21
- Gradle
- IntelliJ IDEA

## Recommended Setup

- paperweight-userdev
- Git
- Maven Central repositories

---

# Core Design Decisions

## Tool Type

Use custom pickaxe items identified via:

- `PersistentDataContainer`

NOT:
- lore
- display names

---

## Tool Variants

Separate tools:

- 2x2x2 Excavator Pickaxe
- 3x3x3 Excavator Pickaxe
- 5x5x5 Excavator Pickaxe

---

## Tool Creation

Players cannot craft tools.

Only operators can create them via command.

Example:
```text
/excavator give <player> 3x3
```

---

# Mining Rules

## 2x2x2 Behavior

Broken block acts as:

- bottom-left-front corner anchor

The region expands positively:
- +X
- +Y
- +Z

Example:
```text
[X][ ]
[ ][ ]

Depth: 2
```

---

## 3x3x3 and 5x5x5 Behavior

Broken block acts as the center of the cube.

Example:
```text
3x3x3:
radius = 1

5x5x5:
radius = 2
```

---

# Block Breaking Rules

## Valid Blocks

Only break blocks:
- mineable by the current tool
- not air
- not liquid
- not tile entities
- not protected
- inside loaded chunks

---

## Tile Entities

Must be skipped entirely.

Examples:
- chests
- barrels
- furnaces
- hoppers
- droppers
- dispensers
- shulker boxes
- brewing stands

Implementation:
```java
block.getState() instanceof TileState
```

Skipped blocks:
- do NOT break
- do NOT consume durability

---

## Protection Compatibility

Mining must respect:
- protection plugins
- future region systems

Initial implementation:
```java
ProtectionManager#canBreak(player, block)
```

Future integrations:
- WorldGuard
- GriefPrevention
- Lands

---

# Enchantment Support

## Fortune

Must apply to all mined blocks.

---

## Silk Touch

Must apply to all mined blocks.

---

## Mending

XP drops should behave naturally.

---

## Unbreaking

Each durability usage must independently roll vanilla Unbreaking chance.

Behavior should emulate vanilla logic:
- each broken block performs its own Unbreaking calculation

---

# Durability Rules

## Consumption

Durability consumed:
```text
= number of successfully mined blocks
```

Excluded:
- air
- skipped tile entities
- protected blocks
- invalid blocks

---

## Durability Precheck

Before area mining begins:

1. calculate valid breakable blocks
2. simulate required durability
3. verify tool has enough durability

If insufficient durability:
- ONLY mine original block normally
- NO area mining

---

## Creative Mode

Creative mode:
- ignores durability
- still performs area mining
- breaks instantly

---

# Drop Handling

## Required Behavior

Drops must behave exactly like vanilla.

DO NOT use:
```java
block.setType(Material.AIR)
```

Use:
```java
block.breakNaturally(tool)
```

Potential future improvement:
```java
player.breakBlock(block)
```

Testing required for:
- Mending
- XP consistency
- plugin compatibility

---

# Performance Design

## Important Constraint

Minecraft world operations are NOT thread-safe.

The following must remain synchronous:
- block access
- world mutation
- block breaking
- chunk access

---

## Optimization Strategy

Avoid:
- unnecessary allocations
- repeated chunk lookups
- recursive event handling
- forced chunk loads

---

## Chunk Safety

Never force-load chunks.

Before accessing:
```java
chunk.isLoaded()
```

If chunk is unloaded:
- skip block

---

# Recursion Prevention

## Problem

Breaking additional blocks may trigger:
```java
BlockBreakEvent
```

again.

---

## Solution

Maintain processing state.

Example:
```java
Set<Location> processingBlocks
```

or:
```java
Set<UUID> activePlayers
```

During internal mining:
- ignore recursive events

---

# Plugin Architecture

```text
src/main/java/dev/peti/excavator/

├── ExcavatorPlugin.java
├── commands/
│   └── GiveToolCommand.java
├── listeners/
│   └── BlockBreakListener.java
├── tools/
│   ├── ExcavatorToolType.java
│   ├── ToolFactory.java
│   └── ToolManager.java
├── mining/
│   ├── AreaCalculator.java
│   ├── MiningProcessor.java
│   ├── DurabilityManager.java
│   ├── ProtectionManager.java
│   └── BlockFilter.java
├── util/
│   ├── PdcUtil.java
│   └── ItemUtil.java
```

---

# Component Responsibilities

## ExcavatorPlugin

Responsibilities:
- plugin bootstrap
- command registration
- listener registration
- NamespacedKey initialization

---

## GiveToolCommand

Responsibilities:
- operator-only command
- create excavator tools
- validate arguments

---

## BlockBreakListener

Responsibilities:
- detect excavator tools
- prevent recursion
- initiate mining process

---

## ExcavatorToolType

Represents:
- 2x2x2
- 3x3x3
- 5x5x5

Contains:
- dimensions
- radius
- display name

---

## ToolFactory

Creates custom pickaxe items.

Adds:
- PersistentDataContainer tags
- display names
- enchant glint if desired

---

## ToolManager

Responsibilities:
- detect excavator tools
- parse tool type
- read/write PDC values

---

## AreaCalculator

Responsibilities:
- generate target block list
- calculate cube coordinates
- handle anchor logic

---

## BlockFilter

Responsibilities:
- skip invalid blocks
- skip air
- skip tile entities
- validate tool compatibility
- validate loaded chunks

---

## ProtectionManager

Responsibilities:
- protection abstraction layer
- future compatibility hooks

Initial implementation:
```java
return true;
```

---

## DurabilityManager

Responsibilities:
- simulate Unbreaking
- calculate durability cost
- verify sufficient durability
- apply durability damage

---

## MiningProcessor

Responsibilities:
- orchestrate mining process
- process filtered blocks
- execute breaks
- handle creative mode behavior

---

# Event Flow

## Step 1

Player breaks block:
```java
BlockBreakEvent
```

---

## Step 2

Check:
- recursion guard
- excavator tool
- gamemode

---

## Step 3

Calculate mining region.

---

## Step 4

Filter invalid blocks.

---

## Step 5

Calculate required durability.

---

## Step 6

If insufficient durability:
- allow normal vanilla mining only
- exit

---

## Step 7

Break filtered blocks naturally.

---

## Step 8

Apply durability damage manually.

---

# Persistent Data Design

## Key

Example:
```text
excavator_size
```

## Values

```text
2
3
5
```

---

# Command Design

## Base Command

```text
/excavator
```

---

## Subcommands

### Give Tool

```text
/excavator give <player> 2x2
/excavator give <player> 3x3
/excavator give <player> 5x5
```

---

# Initial Milestones

# Phase 1 — Project Setup

Goals:
- setup Paper project
- plugin loads
- command registration works

---

# Phase 2 — Tool Creation

Goals:
- create custom pickaxes
- PersistentDataContainer support
- operator give command

---

# Phase 3 — Basic Mining

Goals:
- detect excavator tools
- implement 3x3x3 mining
- recursion prevention

---

# Phase 4 — Filtering

Goals:
- air skipping
- tile entity protection
- valid-tool checks
- chunk safety

---

# Phase 5 — Durability

Goals:
- durability precheck
- Unbreaking logic
- manual durability application

---

# Phase 6 — Vanilla Compatibility

Goals:
- Fortune support
- Silk Touch support
- Mending compatibility
- creative mode handling

---

# Phase 7 — Future Compatibility

Goals:
- protection abstraction
- plugin compatibility architecture

---

# Known Technical Risks

## Recursion

Risk:
- infinite event loops

Mitigation:
- processing guards

---

## Durability Accuracy

Risk:
- double durability application
- incorrect Unbreaking behavior

Mitigation:
- centralized durability management

---

## Mending

Risk:
- inconsistent XP behavior depending on break method

Mitigation:
- test both:
    - `breakNaturally`
    - `player.breakBlock`

---

## Protection Plugins

Risk:
- plugin incompatibility

Mitigation:
- abstraction layer

---

## Performance

Risk:
- lag spikes from 5x5x5 mining

Mitigation:
- efficient filtering
- avoid forced chunk loads
- minimize allocations

---

# MVP Scope

The MVP should include:

- custom excavator pickaxes
- 2x2x2
- 3x3x3
- 5x5x5
- vanilla drops
- enchantment compatibility
- durability logic
- recursion prevention
- tile entity protection
- creative mode support
- chunk safety

NOT included initially:
- crafting
- configs
- GUIs
- async logic
- permissions beyond operators
- visual effects
- protection plugin integrations
- custom enchantments