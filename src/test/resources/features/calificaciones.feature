Feature: Calificación de matrículas e historial académico
  Como docente de CIBERTEC
  Quiero registrar las calificaciones de las matrículas
  Para que se actualice el historial académico del alumno

  Scenario Outline: El docente registra la nota y el historial se actualiza
    Given que la alumna "i202610005" inicia sesión
    And que el docente "D0001" inicia sesión
    When se obtiene la matrícula pagada del curso "PI5620"
    And el docente registra la nota <nota>
    And se consulta el historial académico de la alumna
    Then el historial muestra la condición "<condicion>"
    And los créditos aprobados son <creditosAprobados>
    And el promedio ponderado es <promedioPonderado>
    And la respuesta cumple el esquema "historial.json"

    Examples:
      | nota | condicion   | creditosAprobados | promedioPonderado |
      | 15   | APROBADO    | 13                | 15.23             |
      | 12.5 | DESAPROBADO | 7                 | 14.08             |

  Scenario: Un docente que no dicta el curso intenta registrar la nota
    Given que la alumna "i202610005" inicia sesión
    And que el docente "D0002" inicia sesión
    When se obtiene la matrícula pagada del curso "PI5620"
    And el docente registra la nota 15
    Then la API rechaza la calificación

  Scenario: No se puede calificar una matrícula que no está pagada
    Given que la alumna "i202610005" inicia sesión
    And que el docente "D0001" inicia sesión
    When se obtiene la matrícula no pagada del curso "PS5511"
    And el docente registra la nota 15
    Then la API rechaza la calificación
