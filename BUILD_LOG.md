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
