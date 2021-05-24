package com.iftm.client.tests.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.tests.factory.ClientFactory;

@DataJpaTest
public class ClientRepositoryTests {
	@Autowired
	private ClientRepository repository;
	private long existingId;
	private Long nonExistingId;
	private Long countTotalClients;
	private String existingName, nonExistingName;
	private String existingBirthDate, nonexistingBirthDate;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = Long.MAX_VALUE;
		existingName = "Jimmy";
		nonExistingName = "Fail";
		countTotalClients = 12L;
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
	}
	
//	@Test
//	public void 
	@Test
	public void findByNameShouldFindWhenNameExists() {
		repository.findByName(existingName);
		Optional<Client> list = Optional.empty();

		Assertions.assertFalse(list.isPresent());

	}

	@Test
	public void findByNameShouldThrowExceptionWhenNameDoesNotExists() {

		repository.findByName(nonExistingName);
	}

	@Test
	public void findByBirthDateShouldFindWhenBirthDateExists() {
		repository.findByBirthDate(existingBirthDate);
		Optional<Client> list = Optional.empty();

		Assertions.assertFalse(list.isPresent());

	}

}
