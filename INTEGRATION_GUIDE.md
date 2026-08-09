# WinlatorMali Store Port — Developer Integration Guide

This branch (`feat/store-port`) vendors Bannerlator's four game stores (Steam · Epic · GOG · Amazon —
login, library, download, and launch) into WinlatorMali as a self-contained island. This guide is for
**you (the WinlatorMali developer)** to finish the last mile and build the Mali APK.

## What was done
- **Toolchain:** Kotlin bumped `1.9.0 → 2.2.20` + Compose-compiler plugin `2.2.20`, Jetpack Compose enabled
  (`buildFeatures.compose`), full store dependency set added (Compose BOM 2024.02.00, coroutines,
  lifecycle-compose, navigation-compose, coil 2.6.0, zxing 3.5.3, **JavaSteam 1.8.0**). A `resolutionStrategy`
  pins `kotlin-stdlib`/`okio` so JavaSteam's Kotlin-2.2 metadata resolves.
- **Store island:** all 82 store files + ~24 support classes (Compose theme/components, `core` utils,
  `contents`, `util`) vendored under `com.winlator.star.*`. **Nothing under `com.winlator.cmod.*` was
  modified**, so your upstream/Mali updates never collide with the store code.
- **Bridges to your app** (the only points the island touches `cmod`):
  `com.winlator.star.container|xenvironment|MainActivity|BuildConfig|R` → `com.winlator.cmod.*`.
- **Resources:** `StoreAlertDialogDark` style + `icon_menu_container` drawable ported.
- Compile progress: from 250+ unresolved refs down to **8**, all listed below.

## The last 8 — final API reconciliation (why you, not CI)
These are places where Bannerlator's `star` classes differ from your `cmod` equivalents. You have the
`cmod` API in front of you, so these are quick:

| File | Symbol | What to do |
|---|---|---|
| `star/FilePickerActivity.kt:12,45,52` | `FileManagerScreen`, `EXTRA_OPEN_SCREEN`, `absolutePath` | Vendored file-picker reaches for a `star` UI screen + a `MainActivity` extra your `cmod.MainActivity` lacks. Simplest: replace `InAppFilePicker`'s picker with `ACTION_OPEN_DOCUMENT_TREE`, or point these at your `FileManagerFragment`. (Only used by the Steam **save manager** — non-core; you can also just drop `SteamSaveManagerActivity` + `InAppFilePicker` + `FilePickerActivity` if you don't want cloud saves yet.) |
| `store/compose/AddToShortcutsFlow.kt:163` | `path` | `cmod` `Shortcut`/`Container` exposes the path differently — map to your accessor. |
| `store/download/ContentDownloadController.kt:123,153` | `downloadToCache`, `delete` | `star.contents.Downloader` API drift — align to the vendored `Downloader`/your `ContentsManager`. |
| `store/SteamDepotDownloader.kt:6,285` | `BuildConfig` field | `cmod.BuildConfig` lacks a Steam flag `star.BuildConfig` had — add the field to `defaultConfig { buildConfigField(...) }` or inline the constant. |

## To finish + build
1. Resolve the 8 above (grep the branch; each is one line).
2. **Register the store Activities/services** in `AndroidManifest.xml` (24 of them — see Bannerlator's manifest
   for the list; all are `com.winlator.star.store.*`). Include `DownloadForegroundService` +
   `SteamForegroundService`.
3. **Add an entry point** — a "Stores" button/menu in `MainActivity`/a fragment that starts
   `GogMainActivity` (etc.).
4. **Build** normally: `./gradlew assembleDebug` (pulls the multi-GB Proton/imagefs assets via `downloadAssets`
   + the native tree — your usual local build). The store code compiles in the same pass.
5. Test on Mali hardware: log in → library → download → the game appears as a shortcut → launch.

## Content host (Goldberg / catalogs)
Steam's Goldberg emulation + catalogs currently point at `The412Banner/winlator-contents` releases. To make
WinlatorMali self-sufficient, mirror `goldberg.tzst` (and any store catalog json) to **your own** GitHub
release and update the URLs in `store/GoldbergComponent.kt` (+ any `*Catalog`/`goldberg.json` reference).

## Notes
- Package strategy is deliberately an **island** (`com.winlator.star.*`) so pulling upstream WinlatorMali is
  painless — keep it that way.
- CI here only runs a lean `compileDebugKotlin` (skips the heavy asset download + native build) for fast
  feedback; the full APK is your local build.
