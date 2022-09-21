package com.gft.clinica.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Veterinario extends Usuario implements UserDetails {

    private static final long serialVersionUID = 1L;

    private String crmv;

    @OneToMany(mappedBy = "veterinario")
    private List<Atendimento> atendimentos;

    public Veterinario(Long id, String email, String senha, List<Perfil> perfis) {
        super(id, email, senha, perfis);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getPerfis();
    }

    @Override
    public String getPassword() {
        return getSenha();
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
