package edu.sru.WebBasedEvaluations.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sru.WebBasedEvaluations.domain.PasswordResetToken;
import edu.sru.WebBasedEvaluations.domain.User;

@Repository
public interface PasswordTokenRepository extends CrudRepository<PasswordResetToken,Long>{
	public PasswordResetToken findByToken(String token);
	public User findByid(long l);



}
