package org.example.absolutecinema.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.example.absolutecinema.entity.Role.ADMIN;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Value("${public.urls}")
    private String[] publicUrls;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(publicUrls).permitAll()
//                        .requestMatchers("/swagger-ui/").hasAuthority(ADMIN.getAuthority())
                        .anyRequest().authenticated()) // все остальные запросы блокируй, пока пользователь не зарегается
//                .formLogin(login -> login
//                        .loginPage("/login")
//                        .loginProcessingUrl("/api/v1/users/login")
//                        .defaultSuccessUrl("/swagger-ui/index.html")
//                        .permitAll())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
