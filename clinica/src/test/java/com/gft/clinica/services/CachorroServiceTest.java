package com.gft.clinica.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.gft.clinica.entities.Atendimento;
import com.gft.clinica.entities.Cachorro;
import com.gft.clinica.entities.Cliente;
import com.gft.clinica.entities.Perfil;
import com.gft.clinica.repositories.CachorroRepository;
import com.gft.clinica.repositories.ClienteRepository;
import com.gft.clinica.services.dogapi.DogApiService;

@SpringBootTest
class CachorroServiceTest {

	private static final long ID_INEXISTENTE = 10L;

	private static final String ENTITY_NOT_FOUND_EXCEPTION = "Verifique os parâmetros da requisição";

	private static final Long ID = 1L;

	private static final String SEXO = "macho";

	private static final Double PESO = 2D;

	private static final String PORTE = "miniatura";

	private static final Double TAMANHO = 10D;

	private static final Integer IDADE = 4;

	private static final String RACA = "Miniature Pinscher";

	private static final String NOME = "Rex";

	private static final List<Perfil> PERFIS = List.of(new Perfil(1L, "ADMIN"));

	private Page<Cachorro> pageCachorro;

	private Pageable pageable;

	@InjectMocks
	CachorroService cachorroService;

	@Mock
	private ClienteService mockClienteService;

	@Mock
	CachorroService mockCachorroService;

	@Autowired
	DogApiService dogApiService;

	@Mock
	private CachorroRepository cachorroRepository;

	@Mock
	private ClienteRepository clienteRepository;

	@Mock
	private DogApiService mockDogApiService;

	private Cachorro cachorro;

	private Optional<Cachorro> optionalCachorro;

	private Optional<Cliente> optionalCliente;

	private Cliente cliente;

	private List<Atendimento> atendimentos;

	@BeforeEach
	void setup() {

		MockitoAnnotations.openMocks(this);

		iniciarCachorro();

		iniciarObjetosAuxiliares();

	}

	@Test
	void quandoSalvarRetornaCachorroComSucesso() {

		when(cachorroRepository.save(any())).thenReturn(cachorro);
		when(mockClienteService.buscarCliente(anyLong())).thenReturn(optionalCliente.get());

		Cachorro resposta = cachorroService.salvarCachorro(cachorro, ID);

		assertAll(

				() -> assertNotNull(resposta),
				() -> assertEquals(Cachorro.class, resposta.getClass()),
				() -> assertEquals(ID, resposta.getId()),
				() -> assertEquals(SEXO, resposta.getSexo()),
				() -> assertEquals(TAMANHO, resposta.getTamanho()),
				() -> assertEquals(PESO, resposta.getPeso()),
				() -> assertEquals(NOME, resposta.getNome()),
				() -> assertEquals(PORTE, resposta.getPorte()),
				() -> assertEquals(RACA, resposta.getRaca()),
				() -> assertEquals(IDADE, resposta.getIdade()));
	}

	@Test
	void deveRetornarEntityNotFoundExceptionCasoCachorroSejaSalvoComClienteInexistente() {

		when(cachorroRepository.save(cachorro)).thenReturn(cachorro);
		when(mockClienteService.buscarCliente(ID)).thenReturn(optionalCliente.get());
		when(mockClienteService.buscarCliente(ID_INEXISTENTE))
				.thenThrow(new EntityNotFoundException("Verifique os parâmetros da requisição"));

		assertThrows(EntityNotFoundException.class, () -> cachorroService.salvarCachorro(cachorro, ID_INEXISTENTE));

	}

	@Test
	void quandoAtualizarRetornaCachorroAtualizadoComSucesso() {

		when(cachorroRepository.save(any())).thenReturn(cachorro);
		when(cachorroRepository.findById(anyLong())).thenReturn(optionalCachorro);

		when(mockClienteService.buscarCliente(anyLong())).thenReturn(optionalCliente.get());

		Cachorro resposta = cachorroService.atualizarCachorro(cachorro, ID);

		assertAll(

				() -> assertNotNull(resposta),
				() -> assertEquals(Cachorro.class, resposta.getClass()),
				() -> assertEquals(ID, resposta.getId()),
				() -> assertEquals(SEXO, resposta.getSexo()),
				() -> assertEquals(TAMANHO, resposta.getTamanho()),
				() -> assertEquals(PESO, resposta.getPeso()),
				() -> assertEquals(NOME, resposta.getNome()),
				() -> assertEquals(PORTE, resposta.getPorte()),
				() -> assertEquals(RACA, resposta.getRaca()),
				() -> assertEquals(IDADE, resposta.getIdade()));

	}

	@Test
	void quandoAtualizarEOCachorroNaoExistirRetornarEntityNotFoundException() {

		when(cachorroRepository.save(any())).thenReturn(cachorro);
		when(cachorroRepository.findById(ID)).thenReturn(optionalCachorro);

		when(mockClienteService.buscarCliente(anyLong())).thenReturn(optionalCliente.get());

		assertThrows(EntityNotFoundException.class, () -> cachorroService.atualizarCachorro(cachorro, ID_INEXISTENTE));

	}

	@Test
	void quandoBuscarCachorroRetornarUmaInstanciaDoCachorro() {

		when(cachorroRepository.findById(anyLong())).thenReturn(optionalCachorro);

		Cachorro resposta = cachorroService.buscarCachorro(ID);

		assertAll(

				() -> assertNotNull(resposta),
				() -> assertEquals(Cachorro.class, resposta.getClass()),
				() -> assertEquals(ID, resposta.getId()),
				() -> assertEquals(SEXO, resposta.getSexo()),
				() -> assertEquals(TAMANHO, resposta.getTamanho()),
				() -> assertEquals(PESO, resposta.getPeso()),
				() -> assertEquals(NOME, resposta.getNome()),
				() -> assertEquals(PORTE, resposta.getPorte()),
				() -> assertEquals(RACA, resposta.getRaca()),
				() -> assertEquals(IDADE, resposta.getIdade())

		);

	}

	@Test
	void quandoBuscarCachorroENaoEncontrarDeveRetornarEntityNotFoundException() {

		when(cachorroRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> cachorroService.buscarCachorro(ID));

		try {
			cachorroService.buscarCachorro(ID);
		} catch (Exception ex) {
			assertEquals(EntityNotFoundException.class, ex.getClass());
			assertEquals("Cachorro não encontrado", ex.getMessage());
		}

	}

	@Test
	void quandoListarTodosCachorrosRetornarUmaPageDeCachorros() {

		when(cachorroRepository.findAll(pageable)).thenReturn(pageCachorro);

		Page<Cachorro> resposta = cachorroService.listarTodosCachorros(pageable);

		assertEquals(Cachorro.class, resposta.iterator().next().getClass());

		assertAll(

				() -> assertEquals(1, resposta.getSize()),
				() -> assertNotNull(resposta),
				() -> assertEquals(1, resposta.getSize()),
				() -> assertEquals(Cachorro.class, resposta.iterator().next().getClass()),
				() -> assertEquals(ID, resposta.iterator().next().getId()),
				() -> assertEquals(SEXO, resposta.iterator().next().getSexo()),
				() -> assertEquals(TAMANHO, resposta.iterator().next().getTamanho()),
				() -> assertEquals(PESO, resposta.iterator().next().getPeso()),
				() -> assertEquals(NOME, resposta.iterator().next().getNome()),
				() -> assertEquals(PORTE, resposta.iterator().next().getPorte()),
				() -> assertEquals(RACA, resposta.iterator().next().getRaca()),
				() -> assertEquals(IDADE, resposta.iterator().next().getIdade())

		);

	}

	@Test
	void excluindoCachorroComSucesso() {

		when(cachorroRepository.findById(anyLong())).thenReturn(optionalCachorro);
		doNothing().when(cachorroRepository).delete(any());

		cachorroService.excluirCachorro(ID);

		verify(cachorroRepository, times(1)).delete(any());

	}

	@Test
	void quandoNaoEncontrarCachorroParaExcluirDeveRetornarException() {

		when(cachorroRepository.findById(anyLong())).thenThrow(new EntityNotFoundException(ENTITY_NOT_FOUND_EXCEPTION));
		try {
			cachorroService.excluirCachorro(ID);
		} catch (Exception ex) {
			assertEquals(EntityNotFoundException.class, ex.getClass());
			assertEquals(ENTITY_NOT_FOUND_EXCEPTION, ex.getMessage());
		}
	}

	void iniciarCachorro() {

		cachorro = Cachorro.builder().nome(NOME).raca(RACA).idade(IDADE).tamanho(TAMANHO).porte(PORTE).id(ID).peso(PESO)
				.sexo(SEXO).cliente(cliente).atendimentos(atendimentos).build();

		optionalCachorro = Optional.of(Cachorro.builder().nome(NOME).id(ID).raca(RACA).idade(IDADE).tamanho(TAMANHO)
				.porte(PORTE).peso(PESO).sexo(SEXO).cliente(cliente).atendimentos(atendimentos).build());

		pageCachorro = new PageImpl<>(List.of(cachorro));
	}

	void iniciarObjetosAuxiliares() {

		cliente = Cliente.builder().id(ID).nome("Godofredo").perfis(PERFIS).build();
		optionalCliente = Optional.of(Cliente.builder().id(ID).nome("Godofredo").build());
	}

}
