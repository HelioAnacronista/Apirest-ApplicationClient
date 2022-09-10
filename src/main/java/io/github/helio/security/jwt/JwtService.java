package io.github.helio.security.jwt;

import io.github.helio.VendasApplication;
import io.github.helio.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.expiracao}")
    private String expiracao;

    @Value("${security.jwt.chave-assinatura}")
    private String chaveAssinatura;

    public String gerarToken(Usuario usuario) {
        long expString = Long.valueOf(expiracao);
        LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(expString);
        Instant instant = dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant();
        Date data = Date.from(instant);

        /*
         Cria um hashmap com as informações que você deseja passar no token
         HashMap<String, Object> claims = new HashMap<>();
         claims.put("Email Do Usuario ", "usuario@email.com");
         claims.put("roles ", "ADMIN");
         claims.put("login ", usuario.getLogin());
         claims.put("Expiration Date ", data);
        */

        return Jwts
                .builder()
                .setSubject(usuario.getLogin())
                .setExpiration(data)
                /*
                Para passar propriedades customizadas no token
                Geralmente o padrão é o Subject e data
                .setClaims(claims)
                */

                .signWith( SignatureAlgorithm.HS512, chaveAssinatura)
                .compact();


    }

    //Decode de código JWT
    private Claims obterClaims (String token) throws ExpiredJwtException {
        return Jwts
                .parser()
                .setSigningKey(chaveAssinatura)
                .parseClaimsJws(token)
                .getBody();
    }

    //verificar se o token ainda é valido de acordo com a data e hora de expiração
    public boolean tokenValido (String token ) {
        try {
            Claims claims = obterClaims(token);
            Date dataExpiracao = claims.getExpiration();
            LocalDateTime data =
                    dataExpiracao.toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();
            return !LocalDateTime.now().isAfter(data);
        } catch (Exception e) {
            return false;
        }
    }

    //Serve para saber quem é o usuário que vai estar logado
    public String obterLoginUsuario (String token) throws ExpiredJwtException {
        return (String) obterClaims(token).getSubject();
    }

    // método para -> Testar e ver se está gerando token corretamente
    public static void main(String[] args) {
        ConfigurableApplicationContext contexto = SpringApplication.run(VendasApplication.class);
        JwtService service =  contexto.getBean(JwtService.class);
        Usuario usuario = Usuario.builder().login("fulano").build();
        String token = service.gerarToken(usuario);
        System.out.println(token);

        boolean isTokenValido = service.tokenValido(token);
        System.out.println("O token esta valido? :" + isTokenValido);

        System.out.println(service.obterLoginUsuario(token));
    }
}
