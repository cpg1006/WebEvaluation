

package edu.sru.WebBasedEvaluations.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import edu.sru.WebBasedEvaluations.domain.PasswordResetToken;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.repository.PasswordTokenRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;


/**
 * UserServiceImpl class on how verification/password reset tokens are created and saved.
 * @author Dalton Stenzel
 *
 */
@Service
public class UserServiceImpl implements VerificationService{
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordTokenRepository passwordTokenRepository;
	
	/*
	@Override
	public User registerUser(UserModel userModel) {
		User user = new User();
		user.setEmail((userModel.getEmail()));
		user.setName(userModel.getName());
		user.setRoles("USER");
		user.setPassword(BCrypt.hashpw(userModel.getPassword(), BCrypt.gensalt()));
		userRepository.save(user);
		return user;
	}
	*/

	/**
	 *Method for saving a verification token to the database for when a password reset is taking place.
	 */
	@Override
	public void saveVerificationTokenForUser(String token, User user) {
		PasswordResetToken passwordResetToken= new PasswordResetToken(token, user);
		passwordTokenRepository.save(passwordResetToken);
		// TODO Auto-generated method stub
		
	}

	/*
	public void createPasswordResetTokenForUser(User user, String token) {
	    PasswordResetToken myToken = new PasswordResetToken(token, user);

	    passwordTokenRepository.save(myToken);
	}*/

}
