# Credits and licenses

Puff is a derivative work distributed under GPL-3.0. Keep copyright notices and licenses when redistributing.

- **AirPlay + DLNA Receiver** by F-e-n-y-x, GPL-3.0: https://github.com/F-e-n-y-x/airplay-dlna-receiver, imported commit `c045344146c21c8834b0c02e76762d0db5dba584`. Puff modifies its defaults, UI integration, DLNA request limits, image playback, screen-sharing bounds, fullscreen initialization, and decoder capability/keyframe handling.
- **Android AirPlay Server** by jqssun, GPL-3.0: https://github.com/jqssun/android-airplay-server. The above fork supplies its Android receiver, Compose screens and JNI bridge. The original name/namespace is retained for JNI compatibility.
- **UxPlay**: GPL-3.0, vendored protocol sources and license under `app/src/main/cpp/third_party/UxPlay`.
- **libplist / libcnary**: LGPL-2.1-or-later (see their complete COPYING files), source included under `third_party/libplist` so users can rebuild and relink.
- **llhttp**: MIT, license at `third_party/UxPlay/lib/llhttp/LICENSE-MIT`.
- **playfair**: see `third_party/UxPlay/lib/playfair/LICENSE.md`.
- **Apple ALAC decoder**: Apache-2.0, licenses under `third_party/alac`.
- **OpenSSL 3.5.5**: Apache-2.0, downloaded at build time via **ViaDuck openssl-cmake** (MIT, source and license vendored). See BUILDING for source links.
- **AdbLib** by Anton Tananaev and contributors: BSD-3-Clause; full notice at `app/src/main/assets/ADBLIB-LICENSE.txt`. Puff includes a stream-opening race fix in the vendored Java sources.
- AndroidX, Compose, Media3, Hilt, Kotlin and coroutines: their upstream Apache-2.0 licenses apply.

Puff's cloud artwork is drawn in code; no Apple icons, fonts or proprietary UI assets are bundled. Apple/AirPlay, Android/Google Cast and other product names identify interoperability targets, not endorsement.
