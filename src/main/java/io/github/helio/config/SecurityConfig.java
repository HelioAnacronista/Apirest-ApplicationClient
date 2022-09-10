package io.github.helio.config;

import io.github.helio.security.jwt.JwtAuthFilter;
import io.github.helio.security.jwt.JwtService;
import io.github.helio.service.impl.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UsuarioServiceImpl userService;

    @Autowired
    private JwtService jwtService;

    // Criptografar e descriptografar a senha do usuário
    @Bean
    public PasswordEncoder passwordEncoder() {
        // o Bcrypt é um algoritmo avançado de criptografia
        return new BCryptPasswordEncoder();
    }

    //implementando o método para interceptar as requisições
    @Bean
    public OncePerRequestFilter jwtFilter() {
        return new JwtAuthFilter(jwtService, userService);
    }

    //Http security
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                //definir quem acessa oque atrásves do parâmetro antMatchers
                //http://localhost:8080/cliente/*
                .antMatchers("/api/clientes/**")
                    .hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/pedidos/**")
                    .hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/produtos/**")
                    .hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/usuarios/**")
                    .permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore( jwtFilter(), UsernamePasswordAuthenticationFilter.class);

                /*
                o hasRole fala que tem que estar autenticado você também pode adicionar: .hasRole("USER")
                nisso só seriam permitido pessoas que tivessem a role "USER" podendo.
                ser estiver usado também .hasAuthority("MANTER USUARIO")
                também existe o permitAll() que não precisa estar autenticado
                */


                /*
                cria um formulário de login do spring security ou você pode criar
                o seu proprio formulário de login e colocar uma caminho para ele
                passando o path dentro do parâmetro em uma pasta que esteja dentro
                de resources > static ou templates ficaria algo como ("/meu-login.html")
                esse formulário deve ser submetido apenas com o método POST tendo dois
                campos de inputs por ex "user e password" com atributo names
                */
                        /*
                            <form method="post">
                                <input type="text" name="username">
                                <input type="secret" name="password">
                                <button type="submit"....
                            </form>
                        */

                /*
                .formLogin();
                Para utilizar via insominia
                */
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                // -- Swagger UI v2
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**");
    }
}
