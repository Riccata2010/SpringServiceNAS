package com.mio.SpringServiceDBManager.table.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ServiceOperation {

	@Autowired
	private RepositoryOperation repo;

	public void save(@NonNull EntityOperation entry) {
		Objects.requireNonNull(entry, "ERROR - entry in table is null!");
		repo.save(entry);
	}

	public EntityOperation getByName(@NonNull String name) {
		Objects.requireNonNull(name, "ERROR - name of service is null!");
		Optional<EntityOperation> op = repo.findById(name);
		return op.isPresent() ? op.get() : null;
	}

	public boolean existsByName(@NonNull String name) {
		Objects.requireNonNull(name, "ERROR - name of service is null!");
		Optional<EntityOperation> op = repo.findById(name);
		return op.isPresent();
	}

	public void delete(@NonNull EntityOperation entry) {
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

	public List<EntityOperation> getAll() {
		List<EntityOperation> ris = new ArrayList<>();
		repo.findAll().forEach(ris::add);
		return ris;
	}
}
