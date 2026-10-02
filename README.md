# Cobbled Market

<div>
  <img src="https://mods.matthiesen.dev/badges/matthiesenCore.svg" alt="Matthiesen Core">
  <img src="https://mods.matthiesen.dev/badges/cobblemon.svg" alt="Cobblemon">
</div>

TODO

## Commands

| Command              | Description                                                                 | Default permission level |
|----------------------|-----------------------------------------------------------------------------|--------------------------|
| `/market`            | Opens the shop directory (or the shop directly if only one is configured)  | `NONE`                   |
| `/market <shopId>`   | Opens a specific shop (tab-completes configured shop IDs)                  | `NONE`                   |
| `/market reload`     | Reloads shop definitions from `server.toml`                                | `CHEAT_COMMANDS_AND_COMMAND_BLOCKS` |

Permission levels can be changed in `config/cobbled_market/permissions.toml` (permission nodes: `cobbled_market.command.market`,
`cobbled_market.command.market.shop`, `cobbled_market.command.market.reload`).

## Shops

Shops are defined in the `shops` list of the per-world `config/cobbled_market/server.toml`. Menus are generated
automatically from this list:

```toml
[[server.shops]]
shopId = "example_shop"     # Single word (letters, numbers, _ - . +); "reload" is reserved
shopName = "Example Shop"
shopIcon = "minecraft:chest" # Optional, defaults to minecraft:chest
[[server.shops.entries]]
    itemId = "minecraft:stone"
    quantity = 64
    price = 10
[[server.shops.entries]]
    itemId = "minecraft:dirt"
    quantity = 64
    price = 5
```

Clicking an item in a shop purchases it immediately using the configured currency provider.

## Requirements

- [Matthiesen Core](https://modrinth.com/mod/matthiesen-core)
- [Cobblemon](https://modrinth.com/mod/cobblemon)
- [Fabric API](https://modrinth.com/mod/fabric-api) (Fabric only)
- [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) (Fabric only)

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
