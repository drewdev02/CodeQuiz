#!/usr/bin/env python3
import sys
import os
import re
import json

def parse_apk_text(text):
    lines = text.split('\n')
    exercises = []

    current_topic_level = "cpp_lin_level_1" # Default to Level 1
    current_difficulty = "EASY"

    # Pre-scan sections or process line by line
    i = 0
    total_lines = len(lines)

    exercise_start_re = re.compile(r'^(Ejercicio\s+\d+|Pregunta\s+\d+|\d+\.\s+¿)', re.IGNORECASE)
    option_re = re.compile(r'^-?\s*([A-F])\)\s*(.*)', re.IGNORECASE)

    raw_blocks = []
    current_block_lines = []
    block_difficulty = "EASY"
    block_target_level = "cpp_lin_level_1"

    while i < total_lines:
        line = lines[i]
        stripped = line.strip()

        # Check for topic header updates
        lower = stripped.lower()
        is_topic_header = False
        if '1.3.2' in lower or 'variables y tipos' in lower:
            current_topic_level = "cpp_lin_level_1"
            is_topic_header = True
        elif '1.3.3' in lower or 'operadores y expresiones' in lower:
            current_topic_level = "cpp_lin_level_2"
            is_topic_header = True
        elif '1.3.4' in lower or 'estructuras de control' in lower:
            current_topic_level = "cpp_lin_level_3"
            is_topic_header = True
        elif 'bucle' in lower and ('do-while' in lower or 'for' in lower or 'while' in lower):
            current_topic_level = "cpp_lin_level_4"
            is_topic_header = True
        elif 'arreglos unidimensionales' in lower or 'arreglos bidimensionales' in lower or 'matriz' in lower:
            current_topic_level = "cpp_lin_level_6"
            is_topic_header = True
        elif 'función' in lower and ('¿qué es una función' in lower or 'nivel fácil' in lower and i > 2300):
            current_topic_level = "cpp_lin_level_5"
            is_topic_header = True

        if is_topic_header and not stripped.startswith("Ejercicio") and not stripped.startswith("Pregunta") and not stripped.startswith("1."):
            i += 1
            continue

        # Check for difficulty changes
        is_diff_header = False
        if re.search(r'nivel.*f[aá]cil', lower) and not stripped.startswith('Ejercicio') and not stripped.startswith('Pregunta'):
            current_difficulty = "EASY"
            is_diff_header = True
        elif re.search(r'nivel.*(intermedio|medio)', lower) and not stripped.startswith('Ejercicio') and not stripped.startswith('Pregunta'):
            current_difficulty = "MEDIUM"
            is_diff_header = True
        elif re.search(r'nivel.*dif[ií]cil', lower) and not stripped.startswith('Ejercicio') and not stripped.startswith('Pregunta'):
            current_difficulty = "HARD"
            is_diff_header = True

        if is_diff_header:
            i += 1
            continue

        # Check if line starts an exercise
        is_exercise_start = False
        if exercise_start_re.match(stripped):
            # Exclude lines that are inside explanations or objective summaries
            if not stripped.startswith('- ') and 'Objetivo:' not in stripped:
                is_exercise_start = True

        if is_exercise_start:
            if current_block_lines:
                raw_blocks.append((block_target_level, block_difficulty, current_block_lines))
                current_block_lines = []
            block_target_level = current_topic_level
            block_difficulty = current_difficulty
            current_block_lines.append(line)
        else:
            if current_block_lines:
                current_block_lines.append(line)

        i += 1

    if current_block_lines:
        raw_blocks.append((block_target_level, block_difficulty, current_block_lines))

    # Parse each raw block into structured exercise
    level_counts = {}
    for target_level, diff, b_lines in raw_blocks:
        ex = parse_single_block(b_lines, target_level, diff, level_counts)
        if ex:
            exercises.append(ex)

    return exercises

def parse_single_block(lines, target_level, difficulty, level_counts):
    # Find options, answer, feedback, question, code snippet
    header = lines[0].strip()
    idx = 1
    total = len(lines)

    # Process lines
    content_lines = []
    for l in lines[1:]:
        s = l.strip()
        if s.startswith("Objetivo:"):
            continue
        if s.startswith("Tipo:"):
            # Strip "Tipo: Selección simple/múltiple." and preserve remaining prompt if any
            cleaned = re.sub(r'^Tipo:\s*(Selecci[oó]n\s+(simple|m[uú]ltiple))\.?\s*', '', s, flags=re.IGNORECASE).strip()
            if cleaned:
                content_lines.append(cleaned)
            continue
        content_lines.append(l)

    # Separate question, code snippet, options, answer, feedback
    question_parts = []
    snippet_lines = []
    options = []
    answer_letters = []
    explanation_parts = []

    state = "QUESTION" # QUESTION, SNIPPET, OPTIONS, ANSWER, FEEDBACK

    i = 0
    option_re = re.compile(r'^-?\s*([A-F])\)\s*(.*)', re.IGNORECASE)
    ans_re = re.compile(r'^Respuesta correcta:\s*(.*)', re.IGNORECASE)
    feedback_re = re.compile(r'^(Retroalimentación|Explicación):\s*(.*)', re.IGNORECASE)

    while i < len(content_lines):
        line = content_lines[i]
        s = line.strip()

        if ans_re.match(s):
            state = "ANSWER"
            ans_match = ans_re.match(s)
            ans_text = ans_match.group(1).strip()
            # Extract letters: e.g. "A, B y E" or "C." or "C) int..."
            found = re.findall(r'\b([A-F])\b', ans_text)
            if not found:
                first_char = ans_text[:1].upper()
                if first_char in 'ABCDEF':
                    found = [first_char]
            answer_letters = [letter.upper() for letter in found]
            i += 1
            continue

        if feedback_re.match(s):
            state = "FEEDBACK"
            fb_match = feedback_re.match(s)
            first_fb = fb_match.group(2).strip()
            if first_fb:
                explanation_parts.append(first_fb)
            i += 1
            continue

        opt_match = option_re.match(s)
        if opt_match and state in ("QUESTION", "SNIPPET", "OPTIONS"):
            state = "OPTIONS"
            letter = opt_match.group(1).upper()
            text = opt_match.group(2).strip()
            options.append((letter, text))
            i += 1
            continue

        if state == "OPTIONS":
            # If line is continuation of an option
            if s and not s.startswith("Respuesta"):
                if options:
                    last_letter, last_text = options[-1]
                    options[-1] = (last_letter, (last_text + " " + s).strip())
            i += 1
            continue

        if state == "FEEDBACK":
            if s:
                # ignore footer dividers
                if not s.startswith("____") and not s.startswith("CodeQuiz:"):
                    explanation_parts.append(s)
            i += 1
            continue

        if state in ("QUESTION", "SNIPPET"):
            # Detect if line looks like C++ code
            if is_cpp_code_line(s):
                snippet_lines.append(line)
            else:
                if s:
                    # If snippet already started, and this is a question like "¿Qué valor...?"
                    if snippet_lines and (s.startswith("¿") or s.startswith("Cual") or s.startswith("Cuál") or s.startswith("Que") or s.startswith("Qué")):
                        question_parts.append(s)
                    elif not snippet_lines:
                        question_parts.append(s)
                    else:
                        snippet_lines.append(line)
            i += 1
            continue

        i += 1

    # Format question
    full_question = " ".join(question_parts).strip()
    if not full_question:
        # Use header if question is empty
        full_question = header
        if "." in full_question:
            full_question = full_question.split(".", 1)[1].strip()

    # Format explanation
    full_explanation = " ".join(explanation_parts).strip()
    if not full_explanation:
        full_explanation = "Opción correcta identificada según los estándares de C++."

    # Format snippet
    snippet_code = "\n".join(snippet_lines).strip()
    if not snippet_code:
        snippet_code = None

    # Format options
    if not options:
        return None # Incomplete block

    count = level_counts.get(target_level, 0) + 1
    level_counts[target_level] = count
    ex_id = f"{target_level}_txt_ex{count}"

    dto_options = []
    for letter, text in options:
        is_corr = letter in answer_letters
        dto_options.append({
            "id": f"opt_{letter}",
            "text": text,
            "isCorrect": is_corr
        })

    # If no option was marked correct, mark the first one as fallback
    if not any(o["isCorrect"] for o in dto_options) and answer_letters:
        for o in dto_options:
            if o["id"] == f"opt_{answer_letters[0]}":
                o["isCorrect"] = True

    return {
        "id": ex_id,
        "targetLevel": target_level,
        "question": full_question,
        "codeSnippet": snippet_code,
        "language": "C++",
        "options": dto_options,
        "explanation": full_explanation,
        "difficulty": difficulty
    }

def is_cpp_code_line(line):
    s = line.strip()
    if not s:
        return False
    indicators = [
        ";", "int ", "float ", "double ", "char ", "bool ", "string ", "void ",
        "cout ", "cin ", "cout<<", "cin>>", "cout <<", "cin >>",
        "#include", "return ", "{", "}", "for (", "for(", "while (", "while(",
        "do {", "if (", "if(", "else {", "else if", "std::", "endl",
        "int main()", "v[", "m[", "edad", "suma = ", "resultado = ",
        "contador = ", "total = ", "i++", "i--", "++i", "--i", "+=", "-="
    ]
    # If line starts with a non-code Spanish word, usually not code
    if s.startswith("¿") or s.startswith("Analiza") or s.startswith("Selecciona") or s.startswith("Cuál") or s.startswith("Qué") or s.startswith("Se necesita") or s.startswith("El tipo") or s.startswith("En C++"):
        return False
    return any(ind in s for ind in indicators)

def main():
    import argparse
    parser = argparse.ArgumentParser(description="Parse apk.txt into structured JSON")
    parser.add_argument("--input", default="/Users/Andrew/Downloads/apk.txt")
    parser.add_argument("--output", default="app/build/extracted_cpp_exercises.json")
    args = parser.parse_args()

    with open(args.input, "r", encoding="utf-8") as f:
        content = f.read()

    exercises = parse_apk_text(content)
    print(f"Parsed {len(exercises)} exercises from {args.input}")

    by_level = {}
    by_diff = {}
    for ex in exercises:
        lvl = ex["targetLevel"]
        by_level[lvl] = by_level.get(lvl, 0) + 1
        d = ex["difficulty"]
        by_diff[d] = by_diff.get(d, 0) + 1

    print("Distribution by Level:")
    for lvl, cnt in sorted(by_level.items()):
        print(f"  {lvl}: {cnt}")

    print("Distribution by Difficulty:")
    for d, cnt in sorted(by_diff.items()):
        print(f"  {d}: {cnt}")

    os.makedirs(os.path.dirname(args.output), exist_ok=True)
    with open(args.output, "w", encoding="utf-8") as f:
        json.dump(exercises, f, indent=2, ensure_ascii=False)

    print(f"Saved to {args.output}")

if __name__ == "__main__":
    main()
