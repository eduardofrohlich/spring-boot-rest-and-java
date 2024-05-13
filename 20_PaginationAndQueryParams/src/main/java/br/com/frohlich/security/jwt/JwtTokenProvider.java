package br.com.frohlich.security.jwt;

import br.com.frohlich.data.vo.v1.security.TokenVO;
import br.com.frohlich.exceptions.InvalidJwtAuthenticationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000; //1 hora

    @Autowired
    private UserDetailsService userDetailsService;

    Algorithm algorithm = null;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
    }

    public TokenVO createAccessToken(String username, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds); //momento daqui 1 hora
        var accessToken = getAccessToken(username, roles, now, validity);
        var refreshToken = getRefreshToken(username, roles, now);
        return new TokenVO(username, true, now, validity, accessToken, refreshToken);
    }

    public TokenVO refresh(String refreshToken) {
        if (refreshToken.contains("Bearer")) {
            refreshToken = refreshToken.substring("Bearer ".length());
        }
        JWTVerifier verifier = JWT.require(algorithm).build(); //se eu nao passar o algoritmo correto, ele nao vai conseguir abrir o token e nao vai valida-lo
        DecodedJWT decodedJwt = verifier.verify(refreshToken);
        /*
        https://jwt.io/
        encoded refreshToken
        "roles": [
            "ADMIN",
            "MANAGER"
            ],
            "iss": "http://localhost:8080",
            "exp": 1709257530,
            "iat": 1709253930
            }
         */
        String username = decodedJwt.getSubject();
        List<String> roles = decodedJwt.getClaim("roles").asList(String.class);
        return createAccessToken(username, roles);
    }

    private String getAccessToken(String username, List<String> roles, Date now, Date validity) {
        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(); //pega a url do servidor
        return JWT.create()
                .withClaim("roles", roles)
                .withSubject(username)
                .withIssuer(issuerUrl)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(algorithm)
                .strip();

    }

    private String getRefreshToken(String username, List<String> roles, Date now) {

        Date validityRefreshToken = new Date(now.getTime() + (validityInMilliseconds * 3)); //momento daqui 3 horas

        return JWT.create()
                .withClaim("roles", roles)
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(validityRefreshToken)
                .sign(algorithm)
                .strip();
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJwt = decodedToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(decodedJwt.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodedToken(String token) {
        Algorithm alg = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier verifier = JWT.require(alg).build(); //se eu nao passar o algoritmo correto, ele nao vai conseguir abrir o token e nao vai valida-lo
        DecodedJWT decodedJwt = verifier.verify(token);
        return decodedJwt;
    }

    //verificar se o token é valido
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");

        // Bearrer token23qdw
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        try {
            //se ele for antes de agora ele ta expirado
            return !decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token!");
        }
    }

}
