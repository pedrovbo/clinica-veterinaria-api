package com.gft.clinica.services;

import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gft.clinica.dtos.auth.AutenticacaoDTO;
import com.gft.clinica.dtos.auth.TokenDTO;
import com.gft.clinica.entities.Usuario;

@Service
public class AutenticacaoService {

    @Autowired
    @Lazy
    private AuthenticationManager authManager;

    @Value("${clinica.jwt.expiration}")
    private String expiration;

    @Value("${clinica.jwt.secret}")
    private String secret;

    @Value("${clinica.jwt.issuer}")
    private String issuer;

    public TokenDTO autenticar(AutenticacaoDTO authForm) throws AuthenticationException {

        Authentication authenticate = authManager
                .authenticate(new UsernamePasswordAuthenticationToken(authForm.getEmail(), authForm.getSenha()));

        String token = gerarToken(authenticate);

        return new TokenDTO(token);

    }

    private Algorithm criarAlgoritmo() {
        return Algorithm.HMAC256(secret);
    }

    private String gerarToken(Authentication authenticate) {

        Usuario principal = (Usuario) authenticate.getPrincipal();

        String emailCodificado = Base64.getEncoder().encodeToString(principal.getEmail().getBytes());

        Date hoje = new Date();
        Date dataExpiracao = new Date(hoje.getTime() + Long.parseLong(expiration));

        return JWT.create()
                .withIssuer(issuer)
                .withExpiresAt(dataExpiracao)
                .withSubject(emailCodificado)
                .sign(this.criarAlgoritmo());

    }

    public boolean verificaToken(String token) {

        try {
            if (token == null)
                return false;

            JWT.require(this.criarAlgoritmo()).withIssuer(issuer).build().verify(token);

            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }

    }

    public String retornarEmailDoUsuario(String token) {

        return JWT.require(this.criarAlgoritmo()).withIssuer(issuer).build().verify(token).getSubject();
    }
}
