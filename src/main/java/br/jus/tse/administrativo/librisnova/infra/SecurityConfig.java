package br.jus.tse.administrativo.librisnova.infra;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    
    @Bean 
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement( session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        
                        .requestMatchers( "/login", "/register", "/css/**", "/js/**", "/img/**").permitAll()


                        .requestMatchers("/emprestimos/**", "/livros/**").access((authentication, context) -> {
                            String acceptHeader = context.getRequest().getHeader("Accept");

                            boolean isNotBrowserAddressBar = acceptHeader != null && !acceptHeader.contains("text/html");
                            return new AuthorizationDecision(isNotBrowserAddressBar);
                        })  


                        .requestMatchers("/").authenticated()
                        
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers("/admin/emprestimos-page").hasAnyRole("ADMIN", "APOIO")

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        
                     
                        .anyRequest().authenticated()
                )

                        .exceptionHandling(exceptions -> exceptions
                            .accessDeniedHandler((request, response, accessDeniedException) -> {
                                response.sendRedirect("/"); 
                            })
                )

                .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/", true)
                    .permitAll()
                )

                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
                )
                .build();   
                
    }

    
}