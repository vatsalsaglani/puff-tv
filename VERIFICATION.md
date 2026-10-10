# Beta verification

Local verification on 2026-10-10, OnePlus Y Series / Android 11 / ARM32, ~925 MiB usable RAM.

## Automated

- Release APK builds for ARM32, ARM64 and x86_64 with JDK 17 / SDK 35 / NDK 27.
- Seven JVM tests pass: CPU deltas, cache-aware RAM, bounded history, remote gesture cases, UTF-8 HTTP lengths, seven oversized/malformed request cases, allowed media schemes, and H.264/H.265 keyframe detection after leading metadata.
- Six shell cleanup scenarios pass: eligible Home cleans the 12-item allowlist; foreground streaming, playing audio/video, buffering, absent foreground and diagnostic timeout skip safely.
- Release signing is verified with Android SDK apksigner. Private signing and ADB authorization keys are excluded from source.

## Real TV checks

- Existing Puff installation updates in place; cleanup and overlay preferences remain.
- Both AirPlay mDNS and DLNA SSDP advertise **Puff TV** on the LAN when started.
- DLNA SOAP SetAVTransportURI / Play displays a PNG test card full screen.
- Switching to an H.264 MP4 plays the test clip, with visible frame/time progression.
- Puff's Home overlay is absent during these cast displays.
- The Android mirror receiver rendered a 15-second / 450-frame synthetic H.264 stream sent using the same framing protocol as the phone sender, with no decoder-drop warnings in the final run. This is a receiver test, not a physical-phone test.
- Oversized DLNA request-line and body-length declarations are rejected on the TV; subsequent valid requests still succeed.
- Home-monitor process measured about 17 MiB proportional-set memory; the active casting process was separate, about 62 MiB during these checks. These are snapshots, not a memory guarantee.
- Found and fixed two old-TV issues during testing: fullscreen insets queried before a window existed, and a decoder low-latency option requested on unsupported hardware.

## Remaining device coverage

- Native AirPlay phone pairing and live iPhone mirroring need a sender-device check. Discovery alone does not verify an end-to-end AirPlay session.
- Android phone MediaProjection consent, discovery and sender behavior need a physical Android phone check. Receiver protocol tests cannot prove every manufacturer's sender behavior.
- Newer Android versions, non-Google launchers, direct AirPlay from app share menus, DRM content, and arbitrary DLNA controller interoperability are not certified.
- A physical remote shortcut depends on the button/firmware. Injected ADB key events do not test the accessibility key path on this TV.

The original cleanup/overlay build was also checked through a real restart, Home → streaming-app visibility changes, and active audio playback. This beta keeps cleanup and casting startup preferences independent; casting defaults to off at boot. Do not interpret these checks as a universal performance or compatibility guarantee.
