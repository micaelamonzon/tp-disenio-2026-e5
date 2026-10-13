package ar.edu.utn.frba.ddsi.donaciones.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableConfigurationProperties(ApiTokensProperties.class)
public class SecurityConfig {

    private static final String P = "/servicioDeDonaciones";

    private static final String HUMANO = "DONANTE_HanyUMANO";
    private static final String JURIDICO = "DONANTE_JURIDICO";
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
                // Usa la configuración de CorsConfig (admin.js llama desde el navegador)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // ---------- Públicos ----------
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight CORS
                        // Registro: el usuario todavía no existe, no puede tener token
                        .requestMatchers(HttpMethod.POST, P + "/donante/humano", P + "/donante/juridico", P + "/entidad").permitAll()
                        .requestMatchers(HttpMethod.GET, "/geocodificar", P + "/saludar").permitAll()
                        // Mapa y galería de la landing: donaciones entregadas, sin datos del donante
                        .requestMatchers(HttpMethod.GET, P + "/donaciones/entregadas").permitAll()

                        // ---------- TEMPORAL hasta que notificaciones mande su API token ----------
                        // notificaciones consulta el contacto del donante
                        .requestMatchers(HttpMethod.GET, P + "/donante/humano/*").permitAll()

                        // ---------- TEMPORAL hasta que admin.js pase por el front ----------
                        // admin.js llama desde el navegador y no tiene el token (está en la sesión del front).
                        // GET /donaciones también lo usa logística, que ya manda su API token.
                        .requestMatchers(HttpMethod.GET, P + "/donaciones").permitAll()
                        .requestMatchers(HttpMethod.GET, P + "/donantes", P + "/matcheos/*").permitAll()
                        .requestMatchers(HttpMethod.POST, P + "/humano/donacion/*", P + "/juridico/donacion/*",
                                P + "/matcheos/ejecutar", P + "/matcheos/*/seleccion").permitAll()

                        // ---------- Entre servicios (API token, ROLE_SERVICIO) ----------
                        // logística: cambios de estado de la donación durante la entrega
                        .requestMatchers(HttpMethod.PATCH, P + "/donacion/**").hasAnyRole(SERVICIO, ADMIN)

                        // ---------- Entidades y necesidades ----------
                        // Cualquier usuario logueado puede navegar las entidades (el donante las recorre).
                        // También logística (con su API token) la consulta para notificar la entrega.
                        .requestMatchers(HttpMethod.GET, P + "/entidades", P + "/entidad/*", P + "/entidad/*/necesidades").authenticated()
                        // Modificar: solo la propia entidad ({idEntidad} == claim idEntidad) o ADMIN
                        .requestMatchers(P + "/entidad/{idEntidad}/necesidad", P + "/entidad/{idEntidad}/necesidad/*").access(ReglaDeDuenio.entidad())
                        .requestMatchers(HttpMethod.PUT, P + "/entidad/{idEntidad}").access(ReglaDeDuenio.entidad())
                        .requestMatchers(HttpMethod.DELETE, P + "/entidad/{idEntidad}").access(ReglaDeDuenio.entidad())

                        // ---------- Donantes y sus donaciones: solo el propio donante o ADMIN ----------
                        .requestMatchers(HttpMethod.GET, P + "/donaciones/humano/{idDonante}").access(ReglaDeDuenio.donante(HUMANO))
                        .requestMatchers(HttpMethod.GET, P + "/donaciones/juridico/{idDonante}").access(ReglaDeDuenio.donante(JURIDICO))
                        .requestMatchers(P + "/donante/humano/{idDonante}", P + "/humano/donacion/{idDonante}/**").access(ReglaDeDuenio.donante(HUMANO))
                        .requestMatchers(P + "/donante/juridico/{idDonante}", P + "/juridico/donacion/{idDonante}/**").access(ReglaDeDuenio.donante(JURIDICO))
                        .requestMatchers(HttpMethod.GET, P + "/donacion/*/historial").authenticated()

                        // ---------- Solo administración ----------
                        .requestMatchers(HttpMethod.GET, P + "/donantes/humanos", P + "/donantes/juridicos").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, P + "/importadorCSV", P + "/donacion/registrar").hasRole(ADMIN)
                        .requestMatchers(P + "/matchmaking/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, P + "/admin/**").hasRole(ADMIN)

                        // Cualquier endpoint nuevo queda protegido hasta que se decida quién lo usa
                        .anyRequest().authenticated()
                )
                // Sin token (o vencido) -> 401, así el front sabe que tiene que refrescarlo.
                // Con token pero sin el rol -> 403 (lo que Spring hace por defecto).
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                // Servicios: X-Api-Token. Usuarios: Authorization: Bearer <JWT>
                .addFilterBefore(new ApiTokenFilter(apiTokens), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
