package edu.sru.WebBasedEvaluations.domain;

import edu.sru.WebBasedEvaluations.repository.UserRepository;

/** Simple domain for holding reset password data
 * @author Dalton Stenzel
 *
 */
public class ResetPassword {
	
	  private String email;
	  private String password;
	  private String passwordCheck;
	  private String oldPassword;

	  public String getEmail() {
	    return email;
	    
	  }

	  

	  public void setEmail(String email) {
	    this.email = email;
	  }
	  



	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPasswordCheck() {
		return passwordCheck;
	}
	public void setPasswordCheck(String passwordCheck) {
		this.passwordCheck = passwordCheck;
	}
	
	public void setOldPassword(String oldPass) {
		this.oldPassword = oldPass;
	}
	
	public String getOldPassword() {
		return oldPassword;
	}
}
