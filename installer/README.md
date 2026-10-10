# Puff Installer (beta)

A graphical installer for macOS 13+ (Apple silicon / Intel) and Windows 10+ x64. No terminal commands are needed by end users. Puff itself runs on the TV.

## Use

1. Open Puff Installer. The initial builds have no publisher signature and the Mac build is not notarized; operating systems may require an explicit user override. Do not disable system security protections globally.
2. Read Google's Android SDK license and agree if acceptable. The app downloads Platform Tools directly from `dl.google.com`. Google tools are not bundled or relicensed as Puff code.
3. Connect the computer and TV to the same trusted network. Enable the TV's supported network debugging mode. USB debugging alone does not enable TCP ADB on every TV.
4. Choose **Find my TV**, select a candidate, and approve the debugging prompt on the TV. Puff checks Android TV features and Android 9+ before installation.
5. For newer wireless debugging, use the pairing address/code form, then connect with the TV's connection port (which may differ from the pairing port).
6. Choose **Install Puff on this TV**. The pinned APK is downloaded from the Puff GitHub release, SHA-256 verified, installed in place, and opened. Existing compatible installations keep their settings.

Discovery uses ADB mDNS plus a bounded port-5555 probe in each connected private IPv4 /24 (or narrower subnet). It skips common VPN/virtual interfaces. Large networks, guest isolation, blocked multicast, or firmware restrictions may prevent discovery; manual local IPv4 input is available. Open ports are candidates, not proof of a TV. No installation happens until the user selects and verifies a TV, then explicitly installs.

The installer cannot enable TV developer options or approve TV prompts for you. Casting and Home controls have separate setup on the TV. Automatic cleanup additionally requires local legacy ADB at `127.0.0.1:5555`; modern pairing support in this installer does not add that feature to the TV app.

## Build

Node 22.12+ (tested with 22.17), npm:

```sh
npm ci
npm test
npm start
npm run dist -- --mac zip --arm64 --x64
npm run dist -- --win zip --x64
```

The Windows download is a portable ZIP: extract the entire folder and open Puff Installer.exe. macOS downloads contain an app that can be moved to Applications. ZIP packages can be cross-built with electron-builder. Runtime Windows behavior must still be checked on Windows. macOS app signing and notarization require publisher credentials; they are not bypassed by this project.

## Implementation

- Electron renderer uses local content, CSP, sandboxing, context isolation, no Node integration, and a narrow validated IPC interface.
- Commands use argument arrays, never a shell. Targets must be literal IPv4 addresses in connected RFC1918 subnets. Pairing codes are not persisted by the UI.
- A dedicated ADB server on port 5039 and `ANDROID_USER_HOME` under app data isolate this helper from ordinary port-5037 workflows. The server is stopped on exit; authorization keys remain for later use.
- Google tools are downloaded via HTTPS with an allowlisted host; only required, explicitly named files are extracted. APK version and digest are pinned in `core.cjs`. There is no silent latest-release execution.
- No analytics or cloud account. Network communication is local discovery/ADB plus Google tools and the GitHub APK download.

Puff Installer is GPL-3.0-only. Electron is MIT (with Chromium/third-party notices included in packaged apps), adm-zip is MIT, and electron-builder is MIT. Google Platform Tools retain their own license and NOTICE files, stored with the downloaded tools.

## Verification (2026-10-10)

Six core tests pass (local endpoint validation, scan bounds, mDNS pairing parsing, integrity rejection, install ordering, license gating). The backend discovered the OnePlus Y Series on a local network, verified Android TV/API 30, downloaded and SHA-256 checked the published APK, updated Puff successfully, and opened it. This integration reused an already-authorized ADB server on port 5037; it does not validate first-time TV authorization. The packaged Apple-silicon app launched and its first-run UI was visually checked. Windows and Intel Mac packages are built but have not been run on those platforms. Pairing-code and first-run Google-tools provisioning still need a clean-machine/device check.
