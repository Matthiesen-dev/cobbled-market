# Cobbled Market

<div>
  <img src="https://mods.matthiesen.dev/badges/matthiesenCore.svg" alt="Matthiesen Core">
  <img src="https://mods.matthiesen.dev/badges/cobblemon.svg" alt="Cobblemon">
  <img src="https://mods.matthiesen.dev/badges/gooeylibs.svg" alt="GooeyLibs">
</div>

This mod provides a simple, configurable shop system for Cobblemon servers. It allows server owners to define shops in a configuration file, 
which players can access via Cobblemon NPC Molang functions or the `/market` command. The mod supports multiple shops, each with its own set 
of items and prices, and can be easily customized to fit the needs of any server.

## Commands

| Command            | Description                                                               | Default permission level            |
|--------------------|---------------------------------------------------------------------------|-------------------------------------|
| `/market`          | Opens the shop directory (or the shop directly if only one is configured) | `CHEAT_COMMANDS_AND_COMMAND_BLOCKS` |
| `/market <shopId>` | Opens a specific shop (tab-completes configured shop IDs)                 | `CHEAT_COMMANDS_AND_COMMAND_BLOCKS` |
| `/market reload`   | Reloads shop definitions from `server.toml`                               | `ALL_COMMANDS`                      |

Permission levels can be changed in `config/cobbled_market/permissions.toml`:

Available permission nodes: 
- `cobbled_market.command.market`
- `cobbled_market.command.market.shop`
- `cobbled_market.command.market.reload`

## Cobblemon NPCs

### Molang Functions

The following Molang functions are available for use with Cobblemon NPCs:

- `q.player.market.open()` - Opens the shop directory (or the shop directly if only one is configured). Returns 1.0 if successful, 0.0 otherwise.
- `q.player.market.shop(<shopId>)` - Opens a specific shop. Returns 1.0 if successful, 0.0 otherwise.

### NPC Behaviours

The `market_keeper` behaviour is provided for use with Cobblemon NPCs. It can be used to create a shopkeeper NPC that opens the shop directory or a specific shop when interacted with.

In your NPC's configuration, add the following:

```json
{
  ...other npc config options...,
  "ai": [
    {
      "type": "apply_behaviours",
      "behaviours": [
        ...other behaviours...,
        "cobbled_market:market_keeper"
      ]
    }
  ]
}
```

## Shops

Shops are defined in the `shops` list of the per-world `config/cobbled_market/server.toml`. Menus are generated
automatically from this list:

```toml
[[server.shops]]
    shopName = "Example Shop"
    shopIcon = "minecraft:chest"
    shopId = "example_shop"
    [[server.shops.entries]]
        itemId = "minecraft:stone"
        price = 10
        quantity = 64
    [[server.shops.entries]]
        itemId = "minecraft:dirt"
        price = 5
        quantity = 64
```

Clicking an item in a shop purchases it immediately using the configured currency provider.

### Purchase options

To offer different quantities of the same item at their own prices, replace an entry's `quantity` and `price`
with an `options` list:

```toml
[[server.shops.entries]]
    itemId = "minecraft:stone"
    [[server.shops.entries.options]]
        quantity = 1
        price = 2
    [[server.shops.entries.options]]
        quantity = 16
        price = 20
    [[server.shops.entries.options]]
        quantity = 64
        price = 60
```

Each `price` is the **total cost** for that option's quantity, not a per-item price. Quantities must be positive
integers and prices must be nonnegative integers, both no greater than 2,147,483,647. Zero-price options are supported.
The options list must contain at least one choice; do not combine it with top-level `quantity` or `price`.

Clicking an entry with options opens a paginated selection menu without charging the player. Clicking a choice
purchases that exact quantity at its listed price, and the menu stays open for repeat purchases. The back button
returns to the original shop page. Quantities exceeding an item's stack limit are delivered in separate stacks;
items that do not fit in the inventory are dropped for the player.

Legacy entries and option-based entries can coexist in the same shop. Entries without `options` retain their
instant-purchase behavior, and even a single explicitly configured option opens the selection menu.
Option-count lore, the browse prompt, the selection menu title, and back-to-shop text are configurable under
`server.menu` using `shopOptionsLoreLn1`, `shopOptionsLoreLn2`, `purchaseOptionsTitle`, and `navBackToShopText`.

## Requirements

- [Matthiesen Core](https://modrinth.com/mod/matthiesen-core)
- [Cobblemon](https://modrinth.com/mod/cobblemon)
- [GooeyLibs](https://modrinth.com/mod/gooeylibs)
- [Fabric API](https://modrinth.com/mod/fabric-api) (Fabric only)
- [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) (Fabric only)

### Supported Economy Providers

- Item (Built-in)
- [CobbleDollars](https://modrinth.com/mod/cobbledollars)
- [Impactor](https://modrinth.com/mod/impactor)

## Docs

Documentation for this mod can be found at [mods.matthiesen.dev](https://mods.matthiesen.dev/cobbled-market/)

## Version Compatibility

| Minecraft Version | Matthiesen Core Version | Cobblemon Version | Mod Version |
|-------------------|-------------------------|-------------------|-------------|
| 1.21.1            | 1.x.x                   | 1.8.0             | 1.x.x       |

## FastStats Metrics

This mod uses [FastStats](https://faststats.dev) to collect anonymous usage statistics. This helps the developer understand
how this mod is being used and improve it over time. You can learn more about the data collected and how it is used by visiting
[FastStats: Information](https://faststats.dev/info).

You can also view the data collected by this mod on the [FastStats: Cobbled Market](https://faststats.dev/project/cobbled-market) page.

To opt out of this data collection, set the `enabled` property to `false` in the `<game_directory>/config/matthiesen_core/metrics.properties` file.

## License

MIT - see `LICENSE`.
