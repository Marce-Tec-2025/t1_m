package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import java.util.Map;

public class IntentarLoginFallido implements Task {
    private final String usuario;
    private final String clave;

    public IntentarLoginFallido(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public static IntentarLoginFallido con(String usuario, String clave) {
        return new IntentarLoginFallido(usuario, clave);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to("/api/auth/login").with(
                        request -> request.body(Map.of("username", usuario, "password", clave))
                )
        );
    }
}
