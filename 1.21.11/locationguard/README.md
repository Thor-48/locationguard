# Location Guard

Client-side privacy mod for Minecraft 1.21.11. Separate builds are available for
Fabric, Forge, and NeoForge.

## Settings

The mod creates `config/locationguard.json` the first time Minecraft starts.
Edit the file while the game is open; changes are checked about once per second.

F3 coordinates are shifted by a random offset for the current game session.
`F3+C` is blocked while coordinate protection is on. Set `scrambleCoordinates`
to `false` to hide the coordinate entries instead.

To turn protection off on a server, add its address to `serverRules`:

```json
"enabled": true,
"serverRules": {
  "play.example.net": false
}
```

Server names are case-insensitive. A rule for `play.example.net` also matches
that host with a port. The global `enabled` switch turns off all protection.

## Test overlay

Press **F8** in game to show or hide the debug overlay. Its counters help check
whether block seeds, offsets, chat messages, F3 entries, clouds, and items are
being handled. You can also set `"debugOverlay": true` in the config to show it
when the game starts. F8 resets the counters.

In VS Code, choose **Terminal > Run Task** and select a client task to test a
loader. The first run may take a while while Minecraft files are downloaded.

The game language selects the text: English (`en_us`) is the default, with
German (`de_de`) also included.

## Build

Open the project root in VS Code (the folder containing `settings.gradle` and
`gradlew.bat`). Use PowerShell from that folder:

```powershell
.\gradlew.bat :fabric:build
.\gradlew.bat :neoforge:build
.\forge\gradlew.bat build
```

To build all three:

```powershell
.\gradlew.bat buildAllMods
```

The JARs are written to `fabric/build/libs`, `neoforge/build/libs`, and
`forge/build/libs`. Use the JAR that matches the mod loader installed in
Minecraft; the three loaders cannot use one shared JAR.

Requires JDK 21 and an internet connection. The Gradle wrappers download their
Gradle versions automatically.
