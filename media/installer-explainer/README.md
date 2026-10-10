# Bring Puff home

An original 77-second installer explainer for the Puff website. Landscape 1920×1080, 30 fps; black surfaces, thin outlines, no shadows. SVG composition, English Kokoro narration (`af_heart`, speed 1.03), optional English WebVTT captions. No borrowed music or footage. Platform logos on the website identify their respective download targets and do not imply affiliation.

## Evidence and creative score

The claim ledger and complete script are in `project.json`, linked to the inspected installer implementation at commit `78551ea`. This is an **illustrated reconstruction**, not a screen recording. UI is simplified and a fictional “Living room TV” stands in for the chosen model. Progress is schematic, not measured download progress. The helper and the same TV retain their positions throughout.

| Shot | Before → action → consequence | Reading hold / proof |
| --- | --- | --- |
| Download | Choose a platform on the download page; the helper opens in the same left-hand position | Labels retain Mac architecture distinction |
| Prepare | Read license → checkbox → download button → toolkit ready | Readiness follows the illustrated download |
| Network | Computer and TV are separate → turn on supported TV debugging | Same-network connection; menus vary |
| Discovery | Find my TV → search → candidate appears → select it | A candidate is not yet an authorized TV |
| Approval | Desktop waits → remote approves on TV → device checks complete | TV permission precedes the install control |
| Installation | Confirm model → install → file check → installation → completion | No completion state before simulated work finishes |
| Setup | Puff is installed → open setup guide | Casting and optional Home/cleanup setup remain separate |

Shot boundaries and actual narration durations are in the exported timeline. Action timing is derived deterministically from each measured scene duration, not word-level forced alignment. Full scene holds allow reading. The footer visibly marks every scene as an illustrative beta walkthrough.

## Rebuild

The release attachment `Puff-installer-explainer-source.zip` contains the exact renderer, composition, measured audio, timeline, source hashes, and rebuild instructions. The repository keeps the editable original composition and script. The site uses `docs/media/puff-installer-walkthrough.mp4` and its selectable VTT sidecar. Narration was freshly synthesized with an existing local Kokoro installation; no software or weights were downloaded for this production.

Use research-explainer 0.5.0's `pipeline.py synth` with existing Kokoro model and voice files, followed by `render_studio.py build project.json --composition composition/composition.mjs --backend native-svg --out BUILD --size 1920x1080 --fps 30`. Runtime dependencies are documented in the exported source. This composition uses the system Arial font with a sans-serif fallback; font substitution can change appearance on another computer. Fonts and model weights are not redistributed.

## Verification scope

See `review.json` for artifact-bound visual/technical review and unresolved listening review. The illustrated scenario does not expand installer compatibility: Windows, Intel Mac, first-time tool provisioning, and wireless pairing still need runtime testing. Existing installer binaries are unchanged by this website/media update.

The exported package uses `bounded-render.py` for rebuilding. It retains full frame audits and full decode, while bounding the contact sheet to scene, beat, action-edge and three-second samples. Continuous phase metadata otherwise produces an oversized contact sheet. See `review.json` for this post-encode recovery.
