# WinlatorMali Store Port — Build Log

Porting Bannerlator's game stores (Steam · Epic · GOG · Amazon) into WinlatorMali so users can log in,
download and launch store games. Mali GPU/graphics support remains the WinlatorMali developer's domain.

Fork: `The412Banner/WinlatorMali` (branch `feat/store-port`) · upstream `GunaCharanTeja/WinlatorMali` ← `brunodev85/winlator`.

## Decisions
- **Island package** — stores vendored under `com.winlator.star.*`, bridging to the app only at the container
  seam (`com.winlator.cmod.container.*`), so upstream WinlatorMali (Mali) updates never collide with store code.
- **Content host** — Goldberg/catalogs from `The412Banner/winlator-contents` for now (dev-migration notes at the end).
- **Order** — GOG → Epic → Amazon → Steam. Steam last (JavaSteam + Goldberg).

## Log
### Phase 0 — toolchain ✅
- Bumped Kotlin 1.9.0 → 2.0.21, enabled Jetpack Compose (BOM 2024.02.00) + coroutines 1.7.3, matched to Bannerlator.
- `ComposeSmokeActivity.kt` smoke test compiles green on CI (lean `compileDebugKotlin`, skips the multi-GB asset
  download + native build). Fixed a plugin-application quirk (legacy `apply plugin:` vs `plugins{}` DSL).

### Phase 1 — shared framework + GOG (in progress)
- Vendored the store subsystem as a `com.winlator.star.*` island: 82 store files + 24 support classes
  (Compose theme/components, `core` utils, `contents`, `util`).
- Bridged the container seam + `R` references to `com.winlator.cmod.*`; everything else stays `star`.
- Next: resolve the compile cascade, port ~7 resources + the Compose theme resources, wire the manifest.
- Trimmed over-copied UI (kept only CollapsibleRail + the Compose theme; dropped the custom font).
- Reshaped to GOG-first: deferred 50 Steam/Epic/Amazon/Goldberg files (`_deferred_stores/`), leaving GOG
  + the shared download framework (32 files) to reach a compiling state before layering the rest back.
- Resolution pass 2: the "shared" `DownloadManagerActivity`/`DepotSizeResolver`/`SaveSyncStore`/`QrLoginActivity`
  turned out to be cross-store (reach into Steam/Epic/Amazon), so deferred them too; restored the theme
  state files; added coil (Compose image loading). GOG set narrowing toward compile.
- **Status: partway through the GOG compile-resolution loop.** Remaining known work before a testable APK:
  reconcile star↔cmod Container/Shortcut API drift, port StoreStyle + ~7 resources, vendor a few more core
  helpers (ImageFs et al.), then re-layer Epic/Amazon/Steam (+JavaSteam+Goldberg), then the heavy native APK
  build (multi-GB Proton assets + box64/wine/virgl) — which is the WinlatorMali developer's local environment.
- Pivoted to full-set resolution (Option B: dev builds locally): restored all 82 store files, bridged
  `star.xenvironment` → `cmod.xenvironment` (cmod has ImageFs), and added the complete matched dependency
  set (JavaSteam 1.8.0, lifecycle-compose 2.7.0, navigation-compose, zxing 3.5.3, material-icons, coil 2.6.0).

### Phase 1 → handoff (Option B: developer builds the Mali APK)
- Kotlin bumped 2.0.21 → **2.2.20** (+ Compose-compiler 2.2.20) — JavaSteam 1.8.0 ships Kotlin-2.2 metadata a
  2.0 compiler can't read. This cleared all 251 metadata errors.
- Full dep set matched to Bannerlator; `resolutionStrategy` pins stdlib/okio.
- Bridged `star.{container,xenvironment,MainActivity,BuildConfig,R}` → `cmod.*`; ported `StoreAlertDialogDark`
  style + `icon_menu_container` drawable; vendored `FilePickerActivity`.
- **Compile reduced 250+ → 8 unresolved refs**, all documented in `INTEGRATION_GUIDE.md` (final star↔cmod API
  drift + FilePicker cascade — fastest resolved by the dev with cmod in front of them).
- **Handoff:** branch is build-ready pending (a) the 8 API-drift fixes, (b) manifest registration of the 24
  store Activities/services, (c) a "Stores" entry point. Full APK = the dev's local `assembleDebug`
  (multi-GB Proton assets + native tree). See INTEGRATION_GUIDE.md.
