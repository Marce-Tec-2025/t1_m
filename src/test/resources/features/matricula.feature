Feature: Matrícula del alumno

  Background:
    Given que el alumno "i202610002" inicia sesión con clave "Cibertec2026"

  Scenario: Cotizar el curso
    When cotiza el curso "PI5620" para el alumno "i202610002"
    Then el código de respuesta es 200
    And el total de la cotización es 648.0
    And la respuesta cumple el esquema "cotizacion.json"

  Scenario: Matricularse y pagar el curso
    When se matricula en el curso "PI5620" para el alumno "i202610002"
    Then el código de respuesta es 201
    And el estado de la matrícula es "REGISTRADA"
    And el monto de la matrícula es 648.0
    And la respuesta cumple el esquema "matricula.json"
    And guarda el id de la matrícula
    When paga la matrícula sin enviar cuerpo
    Then el código de respuesta es 200
    And el estado de la matrícula es "PAGADA"
    And la fecha de pago no es nula
    When consulta la lista de pagos
    Then el código de respuesta es 200
    And existe el pago de la matrícula con método de pago por defecto y operación "OP-000000"

  Scenario: Pagar otra vez la misma matrícula
    When intenta pagar nuevamente la misma matrícula sin enviar cuerpo
    Then el código de respuesta es 409
    And la respuesta de error cumple el esquema "error.json"