package com.iftm.client.tests.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.tests.factory.ClientFactory;

@DataJpaTest
public class ClientRepositoryTests {
	@Autowired
	private ClientRepository repository;
	private long existingId;
	private long nonExistingId;
	private long countTotalClients;
	private long countClientByIncome;
	private long countClientByname;
	private String existingName, nonExistingName, existingNameIgnoreCase, nameIsEmppty;
	private String existingBirthDate, nonexistingBirthDate;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = Long.MAX_VALUE;
		existingName = "Carolina";
		existingNameIgnoreCase = "cAroLina";
		nonExistingName = "Fail";
		countTotalClients = 12L;
		countClientByIncome = 5L;
		countClientByname = 1L;
		nameIsEmppty = "";
	}

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existingId);
		Optional<Client> result = repository.findById(1l);
		Assertions.assertFalse(result.isPresent());
	}

	@Test
	public void deleteShouldThrowExeptionWhenIdDoesNotExists() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});
	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		Client client = ClientFactory.createClient();
		client.setId(null);
		client = repository.save(client);
		Optional<Client> result = repository.findById(client.getId());
		Assertions.assertNotNull(client.getId());
		Assertions.assertEquals(countTotalClients + 1, client.getId());
		Assertions.assertTrue(result.isPresent());
		Assertions.assertSame(result.get(), client);
		repository.delete(client);
	}

	@Test
	public void findByIncomeShouldReturnClientsWhenClientIncomeIsGreaterThanOrEqualsToValue() {
		Double income = 4000.0;
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<Client> result = repository.findByIncome(income, pageRequest);
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientByIncome, result.getTotalElements());
	}
	
	/*************************************/
	/*Atividade: testes em JPA Repository*/
	/*************************************/
	
	@Test
	public void findByNameShouldReturnClientsWhenNameExists() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<Client> result = repository.findByName(existingName, pageRequest);
		Assertions.assertEquals(countClientByname, result.getTotalElements());
	}
	@Test
	public void findByNameShouldReturnAllClientsWhenNameIsEmpty() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<Client> result = repository.findByName(nameIsEmppty, pageRequest);
		Assertions.assertEquals(countTotalClients, result.getTotalElements());
		Assertions.assertFalse(result.isEmpty());
	}
	@Test
	public void findByNameShouldReturnAllClientsWhenExistingNameIgnoreCase() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<Client> result = repository.findByName(existingNameIgnoreCase, pageRequest);
		Assertions.assertEquals(countClientByname, result.getTotalElements());
		Assertions.assertFalse(result.isEmpty());
	}

	@Test
	public void findByNameShouldReturnEmptyWhenNameDoesNotExists() {
			PageRequest pageRequest = PageRequest.of(0, 10);
			Page<Client> result = repository.findByName(nonExistingName, pageRequest);
			Assertions.assertTrue(result.isEmpty());
	}

	@Test
	public void updateShouldChangeAndPersistRegister() {
		Optional<Client> result = repository.findById(existingId);
		result.get().setName("Carol");
		repository.save(result.get());
		List<Client> list = repository.findAll();
		Assertions.assertNotNull(result.get());
		Assertions.assertEquals(countTotalClients, list.size());
		Assertions.assertTrue(result.isPresent());
		Assertions.assertSame(result.get().getName(), "Carol");
	}
	
//	@Test
//	public void update

}
