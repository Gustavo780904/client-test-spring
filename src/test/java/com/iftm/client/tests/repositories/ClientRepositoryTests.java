package com.iftm.client.tests.repositories;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
	private long countClientByBirthDateYear;
	private long countClientByBirthDateAfterYear;
	private Integer birthDateYear;
	private Integer yearWithoutBirth;
	private String existingName, nonExistingName, existingNameIgnoreCase, nameIsEmppty;

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
		countClientByBirthDateYear = 1L;
		countClientByBirthDateAfterYear = 9;
		birthDateYear = 1949;
		yearWithoutBirth = 1946;
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
	
	/* Find nome existente*/
	
	@Test
	public void findByNameShouldReturnClientsWhenNameExists() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<Client> result = repository.findByName(existingName, pageRequest);
		Assertions.assertEquals(countClientByname, result.getTotalElements());
	}
	
	/* Find nome ignorando case*/
	@Test
	public void findByNameShouldReturnAllClientsWhenExistingNameIgnoreCase() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<Client> result = repository.findByName(existingNameIgnoreCase, pageRequest);
		Assertions.assertEquals(countClientByname, result.getTotalElements());
		Assertions.assertFalse(result.isEmpty());
	}
	
	/* Find nome vazio*/
	@Test
	public void findByNameShouldReturnAllClientsWhenNameIsEmpty() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<Client> result = repository.findByName(nameIsEmppty, pageRequest);
		Assertions.assertEquals(countTotalClients, result.getTotalElements());
		Assertions.assertFalse(result.isEmpty());
	}
	
	/* Find nome inexistente*/
	@Test
	public void findByNameShouldReturnEmptyWhenNameDoesNotExists() {
			PageRequest pageRequest = PageRequest.of(0, 10);
			Page<Client> result = repository.findByName(nonExistingName, pageRequest);
			Assertions.assertTrue(result.isEmpty());
	}
	
	/* Find data maior que a referência*/
	
//	@Test
//	public void findByBirthDateAfterYearShouldReturnClientsWhenClientBirthDateAfterToValue() {
//		Integer year = 1948;
//		PageRequest pageRequest = PageRequest.of(0, 10);
//		Page<Client> result = repository.findByBirthDateAfterYear(year, pageRequest);
//		Assertions.assertFalse(result.isEmpty());
//		Assertions.assertEquals(countClientByBirthDateAfterYear, result.getTotalElements());
//	}
	
	/* Find birthdate por ano*/
	
	@Test 
	public void findByBirthdateShouldReturnClientThatYearIsEquals() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<Client> result = repository.findByBirthDateYear(birthDateYear, pageRequest);
		Assertions.assertEquals(countClientByBirthDateYear, result.getTotalElements());
	}
	
	/* Find birthdate por ano sem correspondência*/
	
	@Test
	public void findByBirthdateShouldReturnEmptyWhenNobodyClientToYear() {
			PageRequest pageRequest = PageRequest.of(0, 10);
			Page<Client> result = repository.findByBirthDateYear(yearWithoutBirth, pageRequest);
			Assertions.assertTrue(result.isEmpty());
	}
	
	/* update com id existente*/

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
	
	/*update com id inexistente*/
	
	@Test
	public void updateShouldThrowExeptionWhenIdDoesNotExists() {
		Assertions.assertThrows(NoSuchElementException.class, () -> {
			Optional<Client> result = repository.findById(nonExistingId);
			result.get().setName("Carol");
			repository.save(result.get());
		});
	}

}
