package com.gft.clinica.filter;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gft.clinica.entities.Cliente;
import com.gft.clinica.entities.Veterinario;
import com.gft.clinica.services.AutenticacaoService;
import com.gft.clinica.services.UsuarioService;

public class FiltroAutenticacao extends OncePerRequestFilter {

    private AutenticacaoService autenticacaoService;

    private UsuarioService usuarioService;

    public FiltroAutenticacao(AutenticacaoService autenticacaoService, UsuarioService usuarioService) {
        this.autenticacaoService = autenticacaoService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7, header.length());
        }

        if (autenticacaoService.verificaToken(token)) {

            byte[] emailDecodificado = Base64.getDecoder().decode(autenticacaoService.retornarEmailDoUsuario(token));
            
            String username = new String(emailDecodificado);
            
                if (usuarioService.buscarUsuarioPorEmail(username).getClass() == Cliente.class) {
                    Cliente cliente = (Cliente) usuarioService.buscarUsuarioPorEmail(username);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(cliente, null,
                            cliente.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else if (usuarioService.buscarUsuarioPorEmail(username).getClass() == Veterinario.class) {
                    Veterinario veterinario = (Veterinario) usuarioService.buscarUsuarioPorEmail(username);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(veterinario, null,
                            veterinario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
        }

        filterChain.doFilter(request, response);

    }

}