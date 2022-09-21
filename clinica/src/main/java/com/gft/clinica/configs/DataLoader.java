package com.gft.clinica.configs;

import com.gft.clinica.entities.*;
import com.gft.clinica.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

        @Autowired
        private ClienteRepository clienteRepository;

        @Autowired
        private VeterinarioRepository veterinarioRepository;

        @Autowired
        private CachorroRepository cachorroRepository;

        @Autowired
        private AtendimentoRepository atendimentoRepository;

        @Autowired
        private PerfilRepository perfilRepository;

        @Autowired(required = true)
        private BCryptPasswordEncoder encoder;

        @Override
        public void run(String... args) throws Exception {

                Perfil perfil1 = new Perfil(null, "ADMIN");
                Perfil perfil2 = new Perfil(null, "CLIENTE");
                Perfil perfil3 = new Perfil(null, "VETERINARIO");

                Endereco endereco1 = Endereco.builder()
                                .cep("12345-678")
                                .complemento("Apto. 101")
                                .logradouro("Rua dos Bobos")
                                .numero("123A")
                                .build();

                Endereco endereco2 = Endereco.builder()
                                .cep("12345-679")
                                .complemento("Casa 02")
                                .logradouro("Avenida Palmas")
                                .numero("123A")
                                .build();

                Endereco endereco3 = Endereco.builder()
                                .cep("12345-680")
                                .complemento("Casa 03")
                                .logradouro("Avenida Ouro")
                                .numero("123A")
                                .build();

                Endereco endereco4 = Endereco.builder()
                                .cep("12345-681")
                                .complemento("Casa 04")
                                .logradouro("Avenida Palmas")
                                .numero("123A")
                                .build();
                Cliente cliente1 = Cliente.builder()
                                .nome("João")
                                .email("joaograndao@email.com")
                                .senha(encoder.encode("123"))
                                .telefone("123456")
                                .endereco(endereco1)
                                .perfis(List.of(perfil2))
                                .build();

                Cliente cliente2 = Cliente.builder()
                                .nome("Maria")
                                .email("maria@email.com")
                                .senha(encoder.encode("123"))
                                .telefone("123456")
                                .endereco(endereco2)
                                .perfis(List.of(perfil2))
                                .build();

                Cliente cliente3 = Cliente.builder()
                                .nome("Victor")
                                .email("victor@email.com")
                                .senha(encoder.encode("123"))
                                .telefone("123456")
                                .endereco(endereco3)
                                .perfis(List.of(perfil2))
                                .build();

                Cachorro cachorro1 = Cachorro.builder()
                                .nome("Totó")
                                .idade(8)
                                .raca("Affenpinscher")
                                .tamanho(15d)
                                .porte("Miniatura")
                                .peso(2.0)
                                .sexo("Macho")
                                .cliente(cliente1)
                                .build();

                Cachorro cachorro2 = Cachorro.builder()
                                .nome("Albert")
                                .idade(8)
                                .raca("Golden Retriever")
                                .tamanho(45d)
                                .porte("Grande")
                                .peso(37.0)
                                .sexo("Macho")
                                .cliente(cliente2)
                                .build();

                Cachorro cachorro3 = Cachorro.builder()
                                .nome("Tobia")
                                .idade(8)
                                .raca("Labrador Retriever")
                                .tamanho(45d)
                                .porte("Grande")
                                .peso(37.0)
                                .sexo("Fêmea")
                                .cliente(cliente3)
                                .build();

                Veterinario veterinario1 = Veterinario.builder()
                                .crmv("BR123")
                                .nome("João")
                                .email("joao@email.com")
                                .senha(encoder.encode("123"))
                                .telefone("123456")
                                .endereco(endereco4)
                                .perfis(List.of(perfil3))
                                .build();

                Veterinario veterinarioAdmin = Veterinario.builder()
                                .crmv("BR123")
                                .nome("João")
                                .email("veterinarioAdmin@email.com")
                                .senha(encoder.encode("123"))
                                .telefone("123456")
                                .endereco(endereco4)
                                .perfis(List.of(perfil3, perfil1))
                                .build();

                DadosCachorroDia dadosCachorroDia = DadosCachorroDia.builder()
                                .nome("Totó")
                                .idade(8)
                                .tamanho(15d)
                                .peso(2.0)
                                .build();

                Atendimento atendimento = Atendimento.builder()
                                .cliente(cliente1)
                                .cachorro(cachorro1)
                                .veterinario(veterinario1)
                                .titulo("Consulta")
                                .dadosCachorroDia(dadosCachorroDia)
                                .descricaoAtendimento("Cachorro mancando")
                                .diagnostico("Fratura pata esquerda")
                                .comentarios("Muito nervoso")
                                .build();

                Atendimento atendimento1 = Atendimento.builder()
                                .cliente(cliente2)
                                .cachorro(cachorro2)
                                .veterinario(veterinario1)
                                .titulo("Consulta")
                                .dadosCachorroDia(dadosCachorroDia)
                                .descricaoAtendimento("Cachorro mancando")
                                .diagnostico("Fratura pata esquerda")
                                .comentarios("Muito nervoso")
                                .build();

                Atendimento atendimento3 = Atendimento.builder()
                                .cliente(cliente3)
                                .cachorro(cachorro3)
                                .veterinario(veterinario1)
                                .titulo("Consulta")
                                .dadosCachorroDia(dadosCachorroDia)
                                .descricaoAtendimento("Cachorro mancando")
                                .diagnostico("Fratura pata esquerda")
                                .comentarios("Muito nervoso")
                                .build();

                this.perfilRepository.saveAll(Arrays.asList(perfil1, perfil2, perfil3));
                clienteRepository.saveAll(List.of(cliente1, cliente2, cliente3));
                cachorroRepository.saveAll(List.of(cachorro1, cachorro2, cachorro3));
                veterinarioRepository.saveAll(List.of(veterinario1, veterinarioAdmin));
                atendimentoRepository.saveAll(List.of(atendimento, atendimento1, atendimento3));

        }

}
