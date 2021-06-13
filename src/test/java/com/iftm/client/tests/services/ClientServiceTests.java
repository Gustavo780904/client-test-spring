package com.iftm.client.tests.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.DatabaseException;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import com.iftm.client.tests.factory.ClientFactory;

@ExtendWith(SpringExtension.class)
public class ClientServiceTests {

	@InjectMocks
	private ClientService service;

	// quando não precisar carregar todo o contexto da aplicação
	@Mock
	private ClientRepository repository;

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private Client client;
	private PageRequest pageRequest;
	private PageImpl<Client> page;
	private Double income;
	private ClientDTO dto;

	// carrega o contexto da aplicação, usar para testes camada web resourse,
	// controller
//	@MockBean 
//	private ClientRepository repository;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		client = ClientFactory.createClient();
		pageRequest = PageRequest.of(0, 6);
		page = new PageImpl<>(List.of(client));
		income = 4000.0;
		dto = ClientFactory.createClientDTO();

		// config do mock

		// se retornar void, usar doNothing
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(client));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingId);
		Mockito.when(repository.findAll(pageRequest)).thenReturn(page);
		Mockito.when(repository.findByIncome(ArgumentMatchers.anyDouble(), ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.getOne(existingId)).thenReturn(client);
		Mockito.when(repository.save(repository.getOne(existingId))).thenReturn(dto.toEntity());
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).getOne(nonExistingId);

	}
	/*delete deveria retornar vazio quando o id existir */
	
	@Test
	public void deleteShouldDoNothingWhenIdExistis() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});

//		Mockito.verify(repository).deleteById(existingId);
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);

	}

	/*delete deveria lançar uma EmptyResultDataAccessException quando o id não existir */
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExistis() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	/*delete deveria lançar DataIntegrityViolationException quando a deleção implicar em uma
	restrição de integridade. */

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdHasDependencyIntegrity() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}

	/* findAllPaged deveria retornar uma página */
	
	@Test
	public void findAllPagedShouldReturnPage() {
		Page<ClientDTO> result = service.findAllPaged(pageRequest);
		Assertions.assertNotNull(result);
		Assertions.assertFalse(result.isEmpty());

		Mockito.verify(repository, Mockito.times(1)).findAll(pageRequest);
	}

	/*findByIncome deveria retornar uma página*/
	
	@Test
	public void findByIncomePagedShouldReturnPage() {
		Page<ClientDTO> result = service.findByIncome(income, pageRequest);
		Assertions.assertNotNull(result);

		Mockito.verify(repository, Mockito.times(1)).findByIncome(income, pageRequest);
	}
	
	/*findById deveria retornar um ClientDTO quando o id existir*/
	
	@Test
	public void findByIdShouldReturnClientDTOWhenIdExistis() {
		ClientDTO result = service.findById(existingId);
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);
	}
	
	/*findById deveria lançar ResourceNotFoundException quando o id não existir*/
	
	@Test
	public void findByIdShouldResourceNotFoundExceptionWhenIdNonExistis() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
				
		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
	}
	
	/*update deveria retornar ResourceNotFoundException quando o id não existir*/
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExistis() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			
			service.update(nonExistingId, dto);
		});
		Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
	}
	
	/*update deveria retornar um ClientDTO quando o id existir*/
	
	@Test
	public void updateShouldReturnClientDTOWhenIdExistis() {
		
		ClientDTO result = service.update(existingId, dto);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.getId());
		Mockito.verify(repository, Mockito.times(1)).save(dto.toEntity());
	}
	
	/*insert deveria retornar um ClientDTO ao inserir um novo cliente*/
	
	@Test
	public void insertShouldReturnClientDTOWhenInsertNewClient() {
		
		ClientDTO result = service.insert(dto);
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).save(dto.toEntity());
	}

}
