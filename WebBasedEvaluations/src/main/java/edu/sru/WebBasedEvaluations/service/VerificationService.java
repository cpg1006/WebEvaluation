package edu.sru.WebBasedEvaluations.service;

import edu.sru.WebBasedEvaluations.domain.User;

/**
 * Service interface class for saving a verification token.
 * Date 4/21/2022
 * @author Dalton Stenzel
 *
 */
public interface VerificationService {

	//User registerUser(UserModel userModel);

	void saveVerificationTokenForUser(String token, User user);

}
