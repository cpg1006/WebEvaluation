package edu.sru.WebBasedEvaluations.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sru.WebBasedEvaluations.domain.Archive;

@Repository
public interface ArchiveRepository extends CrudRepository<Archive,Long>{

}
