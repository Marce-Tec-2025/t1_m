Feature: Seguridad de la sesion

  Scenario: Rotacion del refresh token
    Given que el alumno "i202610002" inicia sesion
    When intenta renovar el token con su refresh token original
    Then el codigo de respuesta es 200
    When intenta reutilizar el refresh token original
    Then el codigo de respuesta es 401
    And el codigo de error es "REFRESH_REUTILIZADO"
    When consulta su perfil con el nuevo access token
    Then el codigo de respuesta es 401
    And el codigo de error es "TOKEN_INVALIDO"

  Scenario: Cierre de sesion
    Given que el alumno "i202610002" inicia sesion
    When cierra sesion
    Then el codigo de respuesta es 204
    When consulta su perfil con el mismo access token
    Then el codigo de respuesta es 401
    And el codigo de error es "TOKEN_INVALIDO"

  Scenario Outline: Bloqueo de cuenta por intentos fallidos
    Given que el usuario "i202610004" intenta iniciar sesion con clave incorrecta por <intento> vez
    Then el codigo de respuesta es <status>
    And el codigo de error es "<codigo>"
    Examples:
      | intento | status | codigo                |
      | 1       | 401    | CREDENCIALES_INVALIDAS|
      | 2       | 401    | CREDENCIALES_INVALIDAS|
      | 3       | 401    | CREDENCIALES_INVALIDAS|
      | 4       | 401    | CREDENCIALES_INVALIDAS|
      | 5       | 423    | CUENTA_BLOQUEADA      |

  Scenario: Intento de login con clave correcta estando bloqueado
    Given que el usuario "i202610004" intenta iniciar sesion con clave correcta
    Then el codigo de respuesta es 423
    And el codigo de error es "CUENTA_BLOQUEADA"

  Scenario: Alumno sin permiso de administrador
    Given que el alumno "i202610002" inicia sesion
    When intenta listar todos los alumnos
    Then el codigo de respuesta es 403
    And el codigo de error es "ACCESO_DENEGADO"