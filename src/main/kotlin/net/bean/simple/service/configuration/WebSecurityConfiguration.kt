package net.bean.simple.service.configuration

import net.bean.simple.service.misc.ApplicationRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver
import org.springframework.security.web.SecurityFilterChain
import java.util.stream.Collectors

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration {

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private val jwtIssuerUri: String? = null

    @Bean
    fun jwtIssuerAuthenticationManagerResolver(): JwtIssuerAuthenticationManagerResolver {
        return JwtIssuerAuthenticationManagerResolver.fromTrustedIssuers(jwtIssuerUri)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
        http.authorizeHttpRequests({ it.requestMatchers("/api/websocket").permitAll() })
        http.authorizeHttpRequests({ it.requestMatchers(HttpMethod.GET, "/api/v1/movies/**").hasAnyRole(ApplicationRole.ApplicationUser.name) })
        http.authorizeHttpRequests({ it.requestMatchers(HttpMethod.GET, "/api/greetings/notification/**").hasAnyRole(ApplicationRole.ApplicationAdmin.name) })
        http.authorizeHttpRequests({ it.anyRequest().authenticated() })

        http.oauth2ResourceServer({ it.jwt(Customizer.withDefaults()) })
        return http.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return JwtDecoders.fromIssuerLocation(jwtIssuerUri)
    }

    @Bean
    fun jwtAuthenticationConverterForKeycloak(): JwtAuthenticationConverter {
        val jwtGrantedAuthoritiesConverter =
            Converter<Jwt, Collection<GrantedAuthority>> { jwt: Jwt ->
                val realmAccess = jwt.getClaim<Map<String, Collection<String>>>("realm_access")
                val roles = realmAccess["roles"]!!
                roles.stream()
                    .map { role: String -> SimpleGrantedAuthority("ROLE_$role") }
                    .collect(Collectors.toList())
            }

        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter)

        return jwtAuthenticationConverter
    }

}