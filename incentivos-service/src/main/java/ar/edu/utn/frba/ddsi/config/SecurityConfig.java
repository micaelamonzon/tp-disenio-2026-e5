package ar.edu.utn.frba.ddsi.config;

import ar.edu.utn.frba.ddsi.common.seguridad.ApiTokensProperties;
import ar.edu.utn.frba.ddsi.common.seguridad.JwtUtil;
import ar.edu.utn.frba.ddsi.common.seguridad.SeguridadComunConfig;
import ar.edu.utn.frba.ddsi.common.seguridad.reactivo.ApiTokenWebFilter;
import ar.edu.utn.frba.ddsi.common.seguridad.reactivo.JwtWebFilter;
import ar.edu.utn.frba.ddsi.common.seguridad.reactivo.ReglaDeDuenioReactiva;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

// Incentivos es WebFlux (reactivo): usa las versiones reactivas de los filtros de common-lib,
// con la misma lógica que donaciones y logística. Acá solo van las reglas propias de incentivos.
@Configuration
@EnableWebFluxSecurity
@Import(SeguridadComunConfig.class)
public class SecurityConfig {

    private static final String P = "/incentivos-service";

    private static final String ADMIN = "ADMIN";
    private static final String SERVICIO = ApiTokenWebFilter.ROL_SERVICIO;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtUtil jwtUtil,
                                                         ApiTokensProperties apiTokens) {
        return http
                // Es una API, quién llama se sabe por el token de cada request.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                // Sin sesiones: el usuario se arma de nuevo con el token en cada request
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(auth -> auth
                        .pathMatchers("/error").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight CORS

                        // ---------- Entre servicios (API token, ROLE_SERVICIO) ----------
                        // donaciones avisa de donantes y donaciones nuevas
                        .pathMatchers("/incentivos/webhooks/**").hasAnyRole(SERVICIO, ADMIN)

                        // ---------- Del donante: solo lo propio (el id es el del donante en donaciones) ----------
                        .pathMatchers(P + "/misiones/todas/{idDonante}",
                                P + "/perfil/{idDonante}",
                                P + "/donante/{idDonante}/mision/*",
                                P + "/donante/misionesCompletadas/{idDonante}",
                                P + "/donante/insignias/{idDonante}/",
                                P + "/donante/misionActual/{idDonante}",
                                P + "/verificarMision/{idDonante}",
                                P + "/ranking/posicion/{idDonante}",
                                P + "/{idDonante}/metricas")
                                .access(ReglaDeDuenioReactiva.donanteDeCualquierTipo())

                        // ---------- Ranking: lo ven los usuarios logueados ----------
                        .pathMatchers(HttpMethod.GET, P + "/ranking", P + "/ranking/ultimo",
                                P + "/ranking/buscar", P + "/ranking/historial").authenticated()

                        // ---------- Solo administración ----------
                        .pathMatchers(P + "/ranking/calcular", P + "/categoriaMision", P + "/mision",
                                P + "/admin/**").hasRole(ADMIN)

                        // Cualquier endpoint nuevo queda protegido hasta que se decida quién lo usa
                        .anyExchange().authenticated()
                )
                // Sin token (o vencido) -> 401, así el front sabe que tiene que refrescarlo.
                // Con token pero sin el rol -> 403.
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
                // Servicios: X-Api-Token. Usuarios: Authorization: Bearer <JWT>
                .addFilterAt(new ApiTokenWebFilter(apiTokens), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(new JwtWebFilter(jwtUtil), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
