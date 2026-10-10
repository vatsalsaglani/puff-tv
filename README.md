# Puff ☁️

A little space to breathe — a free, open-source Android TV companion with optional startup cleanup, a small Home-screen monitor, and local casting.

**Beta.** The TV features are being tested on a OnePlus Y Series running Android 11. Other firmware can restrict debugging, accessibility shortcuts, background launches or codecs. Android 9+; ARM32, ARM64 and x86_64 builds. No subscription or ads.

[Explore Puff](https://vatsalsaglani.github.io/puff-tv/) — the product website and interactive preview.

## Get Puff

**Desktop helper:** [Puff Installer for Mac and Windows](https://github.com/vatsalsaglani/puff-tv/releases/tag/installer-v0.1.0) discovers network-debugging TVs and installs the verified APK. Beta builds have no publisher signature; Windows runtime verification is pending.

[Step-by-step computer installation guide](https://vatsalsaglani.github.io/puff-tv/#install): USB-drive installation, macOS/Linux/Windows ADB commands, pairing, and optional feature setup. Puff runs on Android devices; the separate [Puff Installer](installer/README.md) desktop helper installs it on a TV without terminal commands.

Download the signed APK from [Releases](https://github.com/vatsalsaglani/puff-tv/releases). Install on your TV; install the same APK on an Android phone if you want Puff screen sharing. On phones, Puff opens the casting screen directly.

You can sideload with a USB drive/file manager or `adb install -r Puff.apk`. Keep the same signing certificate when updating; uninstalling clears settings and local pairing keys.

## Cast to your TV

Open **Puff → Cast · iPhone & Android → Start** on the TV. Use the same trusted local network. Casting is **off after reboot by default**, independently of cleanup. Stop it with **Stop** when finished.

| Sender | How | Limitations |
| --- | --- | --- |
| iPhone / iPad | Control Center → Screen Mirroring → **Puff TV**, then enter the TV's AirPlay PIN | Experimental open-source AirPlay receiver. Show Photos while mirroring. Direct AirPlay from every app/share sheet is not promised. |
| Android media app | Select **Puff TV** in an app with a **DLNA / UPnP renderer** selector | HTTP(S) video/audio URLs and JPEG/PNG photos. A Chromecast-only button will not find Puff. |
| Android screen | Install Puff on phone → **Mirror my screen to a TV** → Puff TV → approve Android's screen-capture prompt | Screen only, no phone audio. 720p/30 fps target; actual display aspect ratio preserved. |

AirPlay defaults to H.264, 1280×720 at 30 fps and PIN pairing. Codec support depends on the TV. DRM-protected streaming is not supported; use the installed streaming apps. Puff is not a Google Cast or Miracast implementation. Your TV's existing Chromecast function remains separate.

The AirPlay PIN protects AirPlay pairing, **not DLNA or Puff's Android mirroring protocol**. Those are unencrypted, unauthenticated LAN protocols: start the receiver on a trusted network only. Do not expose its ports to the internet. Clients on that network can control playback while casting is enabled. The receiver currently permits one screen-sharing stream.

Grant **Display over other apps** if Android prevents an incoming cast from opening its player. Notifications may be required on newer Android. Battery optimization exemption is optional, useful only if the receiver stops unexpectedly.

## Home companion

Enable **Puff Home controls** in Android's Accessibility settings. A smiling cloud appears only on supported Home launchers, with cyan CPU and lavender RAM arcs. Select it or your chosen remote shortcut for a 90-second graph, **Clean apps**, **Cast**, and settings. It hides in other apps and during active audio detected by Android.

Choose a spare remote key and either long-press or double-press in Puff. Firmware can reserve some keys; Home, Power, Back, volume and navigation cannot be assigned. Accessibility is used for foreground package names and that key; Puff does not store screen text or keystroke history.

CPU is the change in aggregate kernel CPU counters. RAM is `(MemTotal − MemAvailable) / MemTotal`, so reclaimable cache is not counted as used. Readings are every five seconds, with history only in memory. Sampling stops when the overlay hides. This requires locally authorized ADB, like cleanup; otherwise readings show unavailable, not invented numbers. Currently the overlay's Home detection targets Google's Android TV launcher (`com.google.android.tvlauncher`).

## Optional startup cleanup

Cleanup requires a TV that exposes ADB on **127.0.0.1:5555** and allows its authorization to persist. Android's newer wireless-debugging pairing flow is not implemented. Enabling USB debugging alone does not enable TCP ADB on every TV. Do not assume this feature works on all Android devices.

1. Enable the TV's supported debugging/network-debugging option.
2. In Puff select **Authorize this TV** and approve the TV's debugging prompt for Puff's generated key.
3. Enable **Clean automatically after restart**. No Mac is required after successful local authorization while the TV keeps ADB available.

Puff waits until about 50 seconds after boot, checks foreground activity and playback, and force-stops a fixed list once. It stops its cleanup service afterward. It preserves installation, account logins and app data. Opening an app starts it normally again. This does **not** permanently block its future background starts and is not a guarantee of faster startup on every TV.

Current list: Spotify, Sony LIV, Hotstar, ZEE5, FanCode, Shemaroo, Apple TV, Netflix, Prime Video, YouTube, YouTube Music and Google TV. Missing packages are skipped. The allowlist is in `app/src/main/assets/cleanup.sh`. There is no Spotify-only button, cache deletion, root requirement or app uninstaller. Automatic cleanup is off on a fresh installation.

## Build / contribute

See [BUILDING.md](BUILDING.md) and [VERIFICATION.md](VERIFICATION.md). Please report your TV model, Android version, sender and a reproducible issue; remove device names, IP addresses and media URLs from logs before sharing.

Puff is GPL-3.0 licensed, based on [Android AirPlay Server](https://github.com/jqssun/android-airplay-server) and [AirPlay + DLNA Receiver](https://github.com/F-e-n-y-x/airplay-dlna-receiver), with substantial Home/cleanup integration and receiver changes. See [THIRD_PARTY.md](THIRD_PARTY.md). Puff is not affiliated with Apple, Google or OnePlus.

## Website

The static GitHub Pages site lives in `docs/` and deploys from `main` / `docs`. Preview with `python3 -m http.server 8766 --directory docs`. No build step is required. Motion respects the system reduced-motion setting. All product illustrations are original SVG/CSS.
