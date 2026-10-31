#!/usr/bin/env python3
"""
Script to repartition C++ linear programming course levels in seed_data.json
so that each level contains a maximum of 5 exercises (microlearning standard).
Bumps catalog version from 7 to 8.
"""

import json
import os

SEED_DATA_PATH = "app/src/main/assets/data/seed_data.json"

LEVEL_METADATA = [
    # Topic 1: Introducción, Variables y Entrada/Salida (4 levels)
    {
        "title": "Fundamentos y Primer Programa en C++",
        "description": "Estructura del método main, directivas básicas, std::cout y concepto fundamental de variable."
    },
    {
        "title": "Tipos de Datos Primitivos",
        "description": "Números enteros, punto flotante (float/double), booleanos y cadenas de texto elementales."
    },
    {
        "title": "Entrada con std::cin e Identificadores",
        "description": "Lectura de datos desde teclado, reglas de nomenclatura y declaración de variables."
    },
    {
        "title": "Manipulación de Cadenas y Asignaciones",
        "description": "Declaraciones compuestas, operaciones con std::string y valores especiales."
    },
    # Topic 2: Operadores Aritméticos y Lógicos (6 levels)
    {
        "title": "Operadores Aritméticos Elementales",
        "description": "Suma, resta, multiplicación, división entera y operador residuo (% módulo)."
    },
    {
        "title": "Comparación y Precedencia Básica",
        "description": "Operadores relacionales (>, <, >=, ==) y diferencias entre asignación y comparación."
    },
    {
        "title": "Operadores Lógicos y Cortocircuito",
        "description": "Conjunción (&&), disyunción (||), negación (!) y evaluación de cortocircuito."
    },
    {
        "title": "Evaluación de Expresiones Complejas I",
        "description": "Trazas paso a paso de expresiones algebraicas y actualización de variables en memoria."
    },
    {
        "title": "Evaluación de Expresiones Complejas II",
        "description": "Interacción de operadores de asignación compuesta y jerarquía matemática estricta."
    },
    {
        "title": "Dominio de Operadores y Asignaciones",
        "description": "Casos desafiantes de acumulación, precedencia y trazado de variables."
    },
    # Topic 3: Estructuras de Control: Condicionales (5 levels)
    {
        "title": "Condicionales Simples y Switch",
        "description": "Estructuras de decisión, bifurcaciones de código y la sentencia switch con fall-through."
    },
    {
        "title": "Bifurcaciones if-else y Operador Ternario",
        "description": "Uso de if-else, operador condicional ternario (?:) y reglas de ámbito (scope) local."
    },
    {
        "title": "Análisis de Flujo Condicional I",
        "description": "Predicción de resultados en bloques if anidados y condiciones compuestas."
    },
    {
        "title": "Análisis de Flujo Condicional II",
        "description": "Evaluación de múltiples caminos de ejecución y validaciones lógicas."
    },
    {
        "title": "Estructuras de Decisión Complejas",
        "description": "Desafíos avanzados de lógica condicional anidada y depuración de ramas."
    },
    # Topic 4: Bucles e Iteración: while, for y do-while (7 levels)
    {
        "title": "Introducción a Bucles y Ciclo for",
        "description": "Sintaxis del bucle for: inicialización, condición de parada e incremento."
    },
    {
        "title": "Bucles while y do-while",
        "description": "Diferencias clave entre while (pre-condición) y do-while (post-condición garantizada)."
    },
    {
        "title": "Control de Bucles: break y continue",
        "description": "Interrupción anticipada con break y omisión de iteración con continue."
    },
    {
        "title": "Acumuladores y Contadores en Bucles",
        "description": "Técnicas de acumulación, actualización de sumadores y conteo en iteraciones."
    },
    {
        "title": "Iteración y Modificación de Estado",
        "description": "Análisis de cambios en variables durante ciclos y condiciones de salida."
    },
    {
        "title": "Traza de Bucles Complejos I",
        "description": "Cálculo de valores finales y seguimiento de programas con ciclos desafiantes."
    },
    {
        "title": "Traza de Bucles Complejos II",
        "description": "Bucles anidados, patrones de salida y maestría en flujo repetitivo."
    },
    # Topic 5: Funciones y Paso de Parámetros (4 levels)
    {
        "title": "Concepto de Funciones e Invocación",
        "description": "Modularidad, funciones void, parámetros formales y retorno de resultados."
    },
    {
        "title": "Retornos y Prototipos de Funciones",
        "description": "Tipos de retorno, declaración previa (prototipos) y paso por referencia básico."
    },
    {
        "title": "Parámetros por Referencia y const",
        "description": "Modificación de variables externas mediante &, referencias constantes (const &) y eficiencia."
    },
    {
        "title": "Análisis y Buenas Prácticas en Funciones",
        "description": "Efectos secundarios, scope de variables locales y diseño limpio de funciones."
    },
    # Topic 6: Arreglos Unidimensionales y Direcciones de Memoria (7 levels)
    {
        "title": "Concepto e Índices de Arreglos",
        "description": "Arreglos unidimensionales contiguos, sintaxis de declaración e indexación base 0."
    },
    {
        "title": "Límites de Arreglos y Punteros Básicos",
        "description": "Capacidad de vectores, desbordamiento de índice (out-of-bounds) y operador de dirección (&)."
    },
    {
        "title": "Recorrido e Iteración de Arreglos",
        "description": "Ciclos for para recorrer arreglos, acumuladores y detección de errores comunes."
    },
    {
        "title": "Operaciones con Arreglos y Matrices",
        "description": "Lectura de valores en arreglos, acumulación de datos y arreglos bidimensionales."
    },
    {
        "title": "Punteros y Modificación en Memoria",
        "description": "Modificación de variables vía punteros, desreferenciación (*) y condicionales con arreglos."
    },
    {
        "title": "Algoritmos sobre Arreglos y Punteros I",
        "description": "Trazado de algoritmos de búsqueda, intercambio y actualización posicional."
    },
    {
        "title": "Algoritmos sobre Arreglos y Punteros II",
        "description": "Desafíos avanzados de punteros, aritmética de direcciones y arreglos dinámicos."
    }
]

TOPIC_CHUNKS = [4, 6, 5, 7, 4, 7]  # Total = 33 levels

ARTICLE_LEVEL_MAP = {
    "art_cpp_intro_lineal": "cpp_lin_level_1",
    "art_cpp_cin_cout_variables": "cpp_lin_level_2",
    "art_cpp_operadores_aritmeticos": "cpp_lin_level_5",
    "art_cpp_estructuras_condicionales": "cpp_lin_level_11",
    "art_cpp_bucles_repeticion": "cpp_lin_level_16",
    "art_cpp_funciones_referencias": "cpp_lin_level_23",
    "art_cpp_arreglos_memoria": "cpp_lin_level_27",
}

def split_balanced(items, num_chunks):
    k, m = divmod(len(items), num_chunks)
    res = []
    for i in range(num_chunks):
        start = i * k + min(i, m)
        end = (i + 1) * k + min(i + 1, m)
        res.append(items[start:end])
    return res

def main():
    with open(SEED_DATA_PATH, "r", encoding="utf-8") as f:
        data = json.load(f)

    # 1. Bump version
    data["version"] = 8

    # 2. Extract and repartition C++ course
    cpp_course = data["courses"][0]
    assert cpp_course["id"] == "cpp_linear_programming"
    original_levels = cpp_course["levels"]

    new_cpp_levels = []
    meta_idx = 0
    overall_level_num = 1

    for t_idx, orig_lvl in enumerate(original_levels):
        num_chunks = TOPIC_CHUNKS[t_idx]
        chunks = split_balanced(orig_lvl["exercises"], num_chunks)
        for chunk in chunks:
            assert len(chunk) <= 5, f"Chunk too large: {len(chunk)}"
            meta = LEVEL_METADATA[meta_idx]
            level_id = f"cpp_lin_level_{overall_level_num}"

            new_cpp_levels.append({
                "id": level_id,
                "levelNumber": overall_level_num,
                "title": meta["title"],
                "description": meta["description"],
                "status": "ACTIVE" if overall_level_num == 1 else "LOCKED",
                "totalExercises": len(chunk),
                "xpReward": 35 if len(chunk) == 5 else 30,
                "exercises": chunk
            })

            meta_idx += 1
            overall_level_num += 1

    assert len(new_cpp_levels) == 33, f"Expected 33 levels, got {len(new_cpp_levels)}"
    total_cpp_ex = sum(len(l["exercises"]) for l in new_cpp_levels)
    assert total_cpp_ex == 148, f"Expected 148 C++ exercises, got {total_cpp_ex}"

    cpp_course["levels"] = new_cpp_levels

    # 3. Update library articles linkedLevelId
    for article in data.get("library", []):
        art_id = article.get("id")
        if art_id in ARTICLE_LEVEL_MAP:
            article["linkedLevelId"] = ARTICLE_LEVEL_MAP[art_id]

    # 4. Save
    with open(SEED_DATA_PATH, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)

    print(f"Successfully repartitioned C++ course into {len(new_cpp_levels)} micro-levels.")
    print(f"Total exercises in C++: {total_cpp_ex}")
    total_all_levels = len(new_cpp_levels) + len(data['courses'][1]['levels'])
    total_all_ex = total_cpp_ex + sum(len(l['exercises']) for l in data['courses'][1]['levels'])
    print(f"Total levels in app: {total_all_levels}")
    print(f"Total exercises in app: {total_all_ex}")
    print(f"Catalog version: {data['version']}")

if __name__ == "__main__":
    main()
