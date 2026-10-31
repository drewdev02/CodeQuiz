#!/usr/bin/env python3
import unittest
import os
import json
import re

from parse_apk_exercises import parse_apk_text

SAMPLE_TEXT = """
1.3.2. Variables y tipos de datos
Nivel 1. Fácil
Objetivo: reconocer las variables.
Ejercicio 1. Concepto de variable
Tipo: Selección simple.

¿Qué es una variable en programación?

A) Un espacio de memoria identificado por un nombre que almacena un valor.

B) Una instrucción que repite un bloque de código.

C) Un símbolo utilizado únicamente para realizar divisiones.

D) Una palabra reservada que siempre representa una constante.

Respuesta correcta: A.

Retroalimentación: Una variable permite almacenar un dato.

Nivel 2. Intermedio
Ejercicio 8. Identificadores válidos
Tipo: Selección múltiple. Selecciona todos los identificadores válidos en C++.

A) edad

B) nombre_usuario

C) 2nota

D) nombre usuario

E) total2

F) int

Respuesta correcta: A, B y E.

Retroalimentación: edad, nombre_usuario y total2 son válidos.

Nivel: Difícil
Ejercicio 15. Integración
Analiza el siguiente código:
int x = 10;
x += 5;

¿Qué imprime?

A) 10
B) 15
C) 5
D) 0

Respuesta correcta: B.

Retroalimentación: Imprime 15.
"""

class TestParseApkExercises(unittest.TestCase):

    def test_sample_parsing(self):
        exercises = parse_apk_text(SAMPLE_TEXT)
        self.assertEqual(len(exercises), 3)

        # Exercise 1
        ex1 = exercises[0]
        self.assertEqual(ex1['difficulty'], 'EASY')
        self.assertEqual(ex1['question'], '¿Qué es una variable en programación?')
        self.assertEqual(len(ex1['options']), 4)
        self.assertTrue(ex1['options'][0]['isCorrect'])
        self.assertFalse(ex1['options'][1]['isCorrect'])
        self.assertEqual(ex1['explanation'], 'Una variable permite almacenar un dato.')

        # Exercise 2 (Multi-select)
        ex2 = exercises[1]
        self.assertEqual(ex2['difficulty'], 'MEDIUM')
        self.assertEqual(len(ex2['options']), 6)
        # A, B, E correct
        correct_ids = [opt['id'] for opt in ex2['options'] if opt['isCorrect']]
        self.assertEqual(correct_ids, ['opt_A', 'opt_B', 'opt_E'])

        # Exercise 3 (Hard with snippet)
        ex3 = exercises[2]
        self.assertEqual(ex3['difficulty'], 'HARD')
        self.assertIn('int x = 10;', ex3['codeSnippet'])
        self.assertTrue(ex3['options'][1]['isCorrect'])

if __name__ == '__main__':
    unittest.main()
