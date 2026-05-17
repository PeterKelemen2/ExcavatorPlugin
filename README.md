# Excavator

A Minecraft Paper 1.21.1 plugin that adds **area mining tools** — pickaxes, axes, and shovels that break **2×2×2**, **3×3×3**, or **5×5×5** cubes of blocks in a single swing.

## Features

### Area Mining
- Three tool families: **pickaxe**, **axe**, **shovel**, each in three sizes (2×2, 3×3, 5×5).
- The 2×2 area orients itself based on the player's facing direction.
- Full **Fortune**, **Silk Touch**, **Unbreaking**, and **Mending** support, enchantments behave just like vanilla.
- Vanilla-accurate XP drops (coal, copper, gold, redstone, lapis, diamond, emerald, quartz, nether gold), batched into a **single XP orb** per swing for performance.
- Plays nicely with other plugins: a `BlockBreakEvent` is fired per block so protection plugins can veto.
- Tile entities (chests, furnaces, etc.) and unbreakable blocks (bedrock, barriers) are automatically skipped.

### Player Controls
- **Sneak to disable** — hold Shift while breaking to mine a single block, ignoring the area effect.
- **`/excavator toggle`** — flip the area effect on/off for your account (in-memory; resets on server restart).

### Inventory Full Warning
- Non-intrusive **action bar** notification when your inventory starts filling up from area mining.
- **Stepped warnings** (configurable) — by default at **75%**, **90%**, and **98%** full, each with a distinct color (yellow → gold → red).
- Each step fires at most once per crossing; freeing slots below a step re-arms it for next time.
- Fully configurable in `config.yml` under `inventory-warning` (thresholds, messages, colors, or disable entirely).

### Crafting
- All excavator tools are **craftable**. Recipe (3×3 grid):

  ```
  T   T
    E
  T   T
  ```

  Four **base tool** items in the corners + one **emerald** in the centre. The base tool's material is read from `config.yml`, so the recipe automatically tracks any material changes.

### Configuration (`config.yml`)
- `debug: true|false` — verbose logging.
- `tools.<tool>-<size>` — override the base material per tool/size (e.g. swap `pickaxe-2x2` from `IRON_PICKAXE` to `STONE_PICKAXE`). Non-tool materials are rejected with a warning and fall back to defaults.
- `inventory-warning.enabled` and `inventory-warning.steps` — toggle and tune the stepped inventory-full action bar warnings.

### Statistics
- Per-player block counts are tracked and saved to `plugins/Excavator/stats.yml`.
- View totals with **`/excavator stats [player]`**.
- Saved automatically on plugin disable and after every reload.

### Commands
| Command | Description | Permission |
|---|---|---|
| `/excavator give <player> <pickaxe\|axe\|shovel> <2x2\|3x3\|5x5>` | Give an excavator tool to a player. | `excavator.give` (op) |
| `/excavator toggle` | Toggle the area effect for yourself. | `excavator.toggle` (default) |
| `/excavator reload` | Reload config, recipes, and stats from disk. | `excavator.reload` (op) |
| `/excavator stats [player]` | Show area-mined block totals. | `excavator.stats` (default) |

Alias: `/ex`.

### Permissions
| Node | Description | Default |
|---|---|---|
| `excavator.use` | Required to use area mining | `true` |
| `excavator.toggle` | Required for `/excavator toggle` | `true` |
| `excavator.stats` | Required for `/excavator stats` | `true` |
| `excavator.give` | Required for `/excavator give` | `op` |
| `excavator.reload` | Required for `/excavator reload` | `op` |
| `excavator.*` | Grants every node above | `op` |

## Building

```sh
./gradlew build
```

JARs are output to `build/libs/`.

## License

This project is licensed under the GNU GPLv3. See [LICENSE](LICENSE).

---

*This project was bootstrapped from a Paper plugin template. All original author branding and links have been removed for compliance and clarity.*
