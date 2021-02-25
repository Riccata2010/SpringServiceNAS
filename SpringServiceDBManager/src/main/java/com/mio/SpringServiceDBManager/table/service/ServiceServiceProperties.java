package com.mio.SpringServiceDBManager.table.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ServiceServiceProperties {

	@Autowired
	private RepositoryServiceProperties repo;

	public void save(@NonNull EntityServiceProperties entry) {
		Objects.requireNonNull(entry, "ERROR - entry in table is null!");
		repo.save(entry);
	}

	public EntityServiceProperties getByName(@NonNull String name) {
		Objects.requireNonNull(name, "ERROR - name of service is null!");
		Optional<EntityServiceProperties> op = repo.findById(name);
		return op.isPresent() ? op.get() : null;
	}

	public boolean existsByName(@NonNull String name) {
		Objects.requireNonNull(name, "ERROR - name of service is null!");
		Optional<EntityServiceProperties> op = repo.findById(name);
		return op.isPresent();
	}

	public void delete(@NonNull EntityServiceProperties entry) {
		Objects.requireNonNull(entry, "ERROR - entry for delete is null!");
		repo.delete(entry);
	}

	public void deleteByName(@NonNull String name) {
		Objects.requireNonNull(name, "ERROR - name for delete is null!");
		repo.deleteById(name);
	}

	public void deleteAll() {
		repo.deleteAll();
	}

	public List<EntityServiceProperties> getAll() {
		List<EntityServiceProperties> ris = new ArrayList<>();
		repo.findAll().forEach(ris::add);
		return ris;
	}
}
