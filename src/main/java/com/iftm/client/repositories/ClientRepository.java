package com.iftm.client.repositories;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iftm.client.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
	
	@Query("select distinct obj from Client obj where "
			+ "obj.income >= :income") 
	Page<Client> findByIncome(Double income, Pageable pageable);
	
//	List<Client> findByNameContainingIgnoreCase(String name);
	
	@Query("select obj from Client obj where lower(obj.name) like lower(concat('%', :name,'%'))")
	Page<Client> findByName(String name, Pageable pageable);
	
	@Query("select obj from Client obj where year(obj.birthDate)=?1")
	Page<Client> findByBirthDate(String birthdate, Pageable pageable);
}
