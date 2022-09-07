package io.github.helio.security.jwt;

import io.github.helio.service.impl.UsuarioServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtService jwtService;

    private UsuarioServiceImpl usuarioService;

    public JwtAuthFilter(JwtService jwtService, UsuarioServiceImpl usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {

         String authorizantion = httpServletRequest.getHeader("Authorizantion");

         if ( authorizantion != null && authorizantion.startsWith("Bearer")) {
             String token = authorizantion.split(" ")[1];
             boolean isValid = jwtService.tokenValido(token);

             if (isValid) {
                 String loginUsuario = jwtService.obterLoginUsuario(token);
                 UserDetails usuario = usuarioService.loadUserByUsername(loginUsuario);
                 UsernamePasswordAuthenticationToken user = new
                         UsernamePasswordAuthenticationToken(usuario, null,
                         usuario.getAuthorities());
                 user.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                 SecurityContextHolder.getContext().setAuthentication(user);
             }
         }

         filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
