package com.gft.clinica.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gft.clinica.entities.Atendimento;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long >{
    Page<Atendimento> findAllByClienteId(Long cliente_id, Pageable pageable);
    Page<Atendimento> findAllByVeterinarioId(Long veterinario_id, Pageable pageable);
    
}
