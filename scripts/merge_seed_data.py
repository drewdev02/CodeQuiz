#!/usr/bin/env python3
import json
import os

SEED_DATA_PATH = "app/src/main/assets/data/seed_data.json"
EXTRACTED_PATH = "app/build/extracted_cpp_exercises.json"

DIFFICULTY_ORDER = {
    "EASY": 1,
    "MEDIUM": 2,
    "HARD": 3
}

def merge_seed_data():
    if not os.path.exists(SEED_DATA_PATH):
        raise FileNotFoundError(f"{SEED_DATA_PATH} not found")
    if not os.path.exists(EXTRACTED_PATH):
        raise FileNotFoundError(f"{EXTRACTED_PATH} not found. Run parse_apk_exercises.py first.")

    with open(SEED_DATA_PATH, "r", encoding="utf-8") as f:
        catalog = json.load(f)

    with open(EXTRACTED_PATH, "r", encoding="utf-8") as f:
        extracted = json.load(f)

    # Group extracted exercises by target level
    extracted_by_level = {}
    for ex in extracted:
        lvl = ex["targetLevel"]
        extracted_by_level.setdefault(lvl, []).append(ex)

    # Bump version to 7
    catalog["version"] = 7
    print(f"Bumping catalog version to {catalog['version']}")

    total_exercises_before = sum(len(l.get("exercises", [])) for c in catalog["courses"] for l in c["levels"])

    for course in catalog["courses"]:
        course_id = course["id"]
        for level in course["levels"]:
            level_id = level["id"]

            # 1. Update existing exercises with difficulty if missing
            existing_exercises = level.get("exercises", [])
            for idx, ex in enumerate(existing_exercises):
                if "difficulty" not in ex or not ex["difficulty"]:
                    if idx < 2:
                        ex["difficulty"] = "EASY"
                    elif idx < 4:
                        ex["difficulty"] = "MEDIUM"
                    else:
                        ex["difficulty"] = "HARD"

            # 2. If this is a C++ course level, merge extracted exercises
            if course_id == "cpp_linear_programming" and level_id in extracted_by_level:
                new_list = []
                for ex in extracted_by_level[level_id]:
                    # Format options
                    formatted_opts = []
                    for o_idx, opt in enumerate(ex["options"]):
                        formatted_opts.append({
                            "id": f"opt{o_idx+1}",
                            "text": opt["text"],
                            "isCorrect": opt["isCorrect"]
                        })

                    formatted_ex = {
                        "id": ex["id"],
                        "question": ex["question"],
                        "codeSnippet": ex["codeSnippet"],
                        "language": ex.get("language", "C++"),
                        "options": formatted_opts,
                        "explanation": ex["explanation"],
                        "difficulty": ex["difficulty"]
                    }
                    new_list.append(formatted_ex)

                # Combine existing + new
                combined = existing_exercises + new_list

                # Sort progressively: EASY -> MEDIUM -> HARD
                combined.sort(key=lambda x: DIFFICULTY_ORDER.get(x.get("difficulty", "EASY"), 1))

                level["exercises"] = combined
                level["totalExercises"] = len(combined)
                level["xpReward"] = max(level.get("xpReward", 30), 25 + len(combined) * 5)
                print(f"Level {level_id}: {len(existing_exercises)} existing + {len(new_list)} new = {len(combined)} exercises (xp={level['xpReward']})")
            else:
                level["totalExercises"] = len(existing_exercises)

    total_exercises_after = sum(len(l.get("exercises", [])) for c in catalog["courses"] for l in c["levels"])
    print(f"Total exercises: {total_exercises_before} -> {total_exercises_after}")

    # Write back with nice formatting
    with open(SEED_DATA_PATH, "w", encoding="utf-8") as f:
        json.dump(catalog, f, indent=2, ensure_ascii=False)

    print(f"Updated {SEED_DATA_PATH} successfully!")

if __name__ == "__main__":
    merge_seed_data()
