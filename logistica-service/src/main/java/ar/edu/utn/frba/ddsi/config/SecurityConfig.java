package ar.edu.utn.frba.ddsi.config;

import ar.edu.utn.frba.ddsi.common.seguridad.ApiTokensProperties;
import ar.edu.utn.frba.ddsi.common.seguridad.JwtUtil;
import ar.edu.utn.frba.ddsi.common.seguridad.SeguridadComunConfig;
import ar.edu.utn.frba.ddsi.common.seguridad.servlet.ApiTokenFilter;
import ar.edu.utn.frba.ddsi.common.seguridad.servlet.JwtAuthenticationFilter;
import ar.edu.utn.frba.ddsi.common.seguridad.servlet.ReglaDeDuenio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Las clases de seguridad (filtros, JwtUtil, regla de dueño) están en common-lib
// y las comparten todos los servicios. Acá solo van las reglas propias de logística.
@Configuration
@Import(SeguridadComunConfig.class)
public class SecurityConfig {

    private static final String P = "/logistica-service";

    private static final String ENTIDAD = "ENTIDAD_BENEFICIARIA";
    private static final String ADMIN = "ADMIN";
    private static final String SERVICIO = ApiTokenFilter.ROL_SERVICIO;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtil jwtUtil,
                                                   ApiTokensProperties apiTokens) throws Exception {
        http
                // Es una API, quién llama se sabe por el token de cada request.
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // ---------- Públicos ----------
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight CORS
                        // El planificador de rutas externo avisa acá cuando termina: no tiene nuestro token
                        .requestMatchers(HttpMethod.POST, P + "/callback-planificacion").permitAll()

                        // ---------- Seguimiento (mapas del donante y de la entidad): solo lo propio ----------
                        // Logística guarda el idDonante sin el tipo, por eso acepta humano o jurídico
                        .requestMatchers(HttpMethod.GET, P + "/camiones/monitoreo/ruta/donante/{idDonante}/recorrido")
                                .access(ReglaDeDuenio.donanteDeCualquierTipo())
                        .requestMatchers(HttpMethod.GET, P + "/camiones/monitoreo/ruta/entidad/{idEntidad}/recorrido")
                                .access(ReglaDeDuenio.entidad())
                        .requestMatchers(HttpMethod.GET, P + "/camiones/monitoreo/ruta/camion/*/recorrido").authenticated()
                        // La posición del camión la informa el GPS (u otro servicio), no un usuario
                        .requestMatchers(HttpMethod.POST, P + "/camiones/monitoreo/ruta/camion/*/posicionActual")
                                .hasAnyRole(SERVICIO, ADMIN)

                        // ---------- Entregas ----------
                        // La entidad ve sus entregas (el front filtra las suyas) y confirma la recepción
                        .requestMatchers(HttpMethod.GET, P + "/entregas", P + "/entrega/*").hasAnyRole(ENTIDAD, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, P + "/entrega/*/confirmarRecepcion", P + "/entrega/*/marcarNoRecibida")
                                .hasAnyRole(ENTIDAD, ADMIN)
                        // Crear entregas, iniciar traslado o volver a pendiente: administración o un servicio
                        .requestMatchers(P + "/entrega", P + "/entrega/**").hasAnyRole(ADMIN, SERVICIO)

                        // ---------- Camiones y planificación: solo administración ----------
                        .requestMatchers(P + "/camiones", P + "/camiones/*").hasRole(ADMIN)
                        .requestMatchers(P + "/admin/**").hasRole(ADMIN)
                        .requestMatchers("/api/v1/logistica/**").hasRole(ADMIN)

                        // Cualquier endpoint nuevo queda protegido hasta que se decida quién lo usa
                        .anyRequest().authenticated()
                )
                // Sin token (o vencido) -> 401, así el front sabe que tiene que refrescarlo.
                // Con token pero sin el rol -> 403.
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                // Servicios: X-Api-Token. Usuarios: Authorization: Bearer <JWT>
                .addFilterBefore(new ApiTokenFilter(apiTokens), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
