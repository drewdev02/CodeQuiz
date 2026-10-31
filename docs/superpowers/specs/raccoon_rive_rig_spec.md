# Raccoon Rive Rig & State Machine Specification

> **Platform:** Rive 2D Vector & Bone Animation Engine (`rive.app`)  
> **Target Runtime:** Android Rive Runtime (`app.rive:rive-android:11.15.0`)  
> **Artboard Name:** `Raccoon`  
> **State Machine Name:** `MascotStateMachine`  
> **Output Binary:** `app/src/main/res/raw/raccoon_mascot.riv`

---

## 1. Character Layer Anatomy & Anchors (500x500 Canvas)

The raccoon character must be split into independent vector shapes/layers with the following pivot anchors:

```
┌────────────────────────────────────────────────────────┐
│                   BONE & LAYER HIERARCHY               │
├────────────────────────────────────────────────────────┤
│  root_bone (Position: 250, 480)                        │
│    ├── shadow_mesh (Dynamic ellipse with alpha blend) │
│    ├── bone_tail_base (Anchor: 210, 430)               │
│    │     └── bone_tail_tip (Anchor: 160, 360) [Spring] │
│    └── bone_torso (Anchor: 250, 450)                   │
│          ├── layer_feet_left / layer_feet_right        │
│          ├── layer_belly (White/Cream patch)           │
│          ├── bone_arm_left (Anchor: 170, 320)          │
│          │     └── layer_paw_left (Paw/Claws)          │
│          ├── bone_arm_right (Anchor: 330, 320)         │
│          │     └── layer_paw_right (Paw/Claws)         │
│          └── bone_neck (Anchor: 250, 240)              │
│                └── bone_head (Anchor: 250, 200)        │
│                      ├── layer_ears (Left & Right)     │
│                      ├── layer_mask (Bandit eye patch) │
│                      ├── layer_eyes_group              │
│                      │     ├── eye_sclera              │
│                      │     ├── eye_pupil               │
│                      │     └── eyelid_clip [Blink]     │
│                      ├── layer_snout & nose            │
│                      └── layer_mouth [Phonemes]        │
└────────────────────────────────────────────────────────┘
```

---

## 2. Bone Rigging & Procedural Springs

1. **Tail Physics (Secondary Motion):**
   - Base pivot at `(210, 430)`.
   - The tail tip bone uses Rive's procedural physics spring:
     - `Damping: 35%`
     - `Strength: 80%`
   - When the torso tilts or jumps, the tail naturally swings with realistic inertia and drag.
2. **Eye Blinking (Organic Life):**
   - Timeline animation `blink` (15 frames duration):
     - Frame 0: Eyes open (scale Y = 100%).
     - Frame 6: Eyelid shut (scale Y = 0%).
     - Frame 12: Eyes open (scale Y = 100%).
   - Driven cyclically inside the State Machine via a looping timer node every $3.0 - 4.5\text{ s}$ with random variance.
3. **Breathing Idle Loop (Squash & Stretch):**
   - Torso bone oscillates in scale Y between $98\%$ and $102\%$ over $2.0\text{ s}$.
   - Head bone has an offset phase of $+0.2\text{ s}$ to create natural lag.

---

## 3. State Machine Design (`MascotStateMachine`)

### Inputs Contract:
| Input Name | Type | Allowed Values | Description |
| :--- | :--- | :--- | :--- |
| **`stateIndex`** | Number | `0.0` .. `4.0` | Controls active behavioral loop: <br>• `0.0` = **IDLE** (Calm standing, tail sway, blinking)<br>• `1.0` = **THINKING** (Hand on chin, looking up, lightbulb)<br>• `2.0` = **SUCCESS** (Victory jump, arms up, cheerful)<br>• `3.0` = **ERROR** (Scratching head, empathetic, soft eyes)<br>• `4.0` = **CELEBRATION** (Party hat dance, confetti burst) |
| **`isBlinking`** | Boolean | `true` / `false` | Triggerable blink override |
| **`triggerCheer`** | Trigger | — | Instant one-shot victory burst |

### Transition Graph:
```
           ┌───────────┐
           │   Entry   │
           └─────┬─────┘
                 │
                 ▼
         ┌───────────────┐
    ┌───►│  State: IDLE  │◄───┐
    │    └───────┬───────┘    │
    │            │            │
    │  (state=1) │ (state=2)  │  (state=0)
    │            ▼            │
    │    ┌───────────────┐    │
    ├────┤State: THINKING│    │
    │    └───────┬───────┘    │
    │            │ (state=3)  │
    │            ▼            │
    │    ┌───────────────┐    │
    ├────┤ State: ERROR  │────┤
    │    └───────────────┘    │
    │            │ (state=4)  │
    │            ▼            │
    │    ┌───────────────┐    │
    └────┤State: CELEBRATE    │
         └───────────────┘
```

---

## 4. Export & Build Pipeline
1. In `rive.app`: **Export -> Download for Android (.riv binary)**.
2. Place the generated binary in:
   `app/src/main/res/raw/raccoon_mascot.riv`
3. The Android app automatically detects `R.raw.raccoon_mascot` and switches from Lottie to the native Rive 60 FPS skeletal state machine with zero code modification.
