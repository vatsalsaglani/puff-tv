"""Run the exported Studio renderer with a bounded encoded contact sheet.

The composition exposes a continuous phase for audits. Preserve every-frame
geometry/semantic checks and full decode, but sample evidence images at meaningful
boundaries instead of treating each fractional phase as a distinct event.
"""
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parent / 'scripts'))
import render_studio as studio
_original = studio.extract_encoded_evidence

def bounded_evidence(out, timeline, frames):
    chosen = {frame: labels for frame, labels in frames.items()
              if any(not label.startswith('semantic-') for label in labels)}
    for frame in range(0, timeline['frames'], 3 * timeline['fps']):
        chosen.setdefault(frame, []).append('three-second-review')
    for scene in timeline['scenes']:
        for phase in [.18, .3, .35, .45, .5, .58, .63, .76, .84]:
            event = round((scene['start'] + scene['duration'] * phase) * timeline['fps'])
            for frame in [event - 1, event, event + 1]:
                if 0 <= frame < timeline['frames']:
                    chosen.setdefault(frame, []).append('action-edge')
    return _original(out, timeline, chosen)

studio.extract_encoded_evidence = bounded_evidence
if __name__ == '__main__':
    studio.main()
