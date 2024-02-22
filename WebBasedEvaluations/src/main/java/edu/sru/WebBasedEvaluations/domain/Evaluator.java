package edu.sru.WebBasedEvaluations.domain;

import java.util.*;

import javax.persistence.*;

import edu.sru.WebBasedEvaluations.company.Company;
/**Evaluator
 * is the user that doing  the Evaluation
 *
 */
@Entity
@Table(name ="Evaluator")
public class Evaluator {

    /**
      * id is auto generated
	 * group is the evaluation group the Evaluator  is associated with
	 * user  is the Evaluator  account
	 * sync determines if the evaluator after has to wait for the current evaluator to finish or not 
	 * level determine what role the evaluator has
	 * evalutationLog determine what evaluation Log is accosited with the evaluator 
     */
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	long id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="group_id")
	private Group group;
    
	private boolean sync = false;
    private boolean preview = false;
  
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "level")
    private EvalRole level;
    
    
    @OneToMany(mappedBy = "evaluator",cascade = CascadeType.ALL)
 	private List<EvaluationLog> evalutationLog= new ArrayList<>();
    
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "company")
    private Company company;
    
	public Evaluator() {
		
	}
	/**
	 * @param user is the evaluator
	 * @param group is the evaluation group the evaluator is associated  with 
	 * @param level is the role the evaluator  is responsible for 
	 */
	public Evaluator(User user ,Group group, EvalRole level, Company company) {
	
		this.user = user;
		this.group = group;
		this.level=level;
		this.company = company;
	
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
	
	
	
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public List<EvaluationLog> getEvalutationLog() {
		return evalutationLog;
	}
	public void setEvalutationLog(List<EvaluationLog> evalutationLog) {
		this.evalutationLog = evalutationLog;
	}
	public void appendEvalutationLog(EvaluationLog evalutationLog) {
		this.evalutationLog.add(evalutationLog);
	}
	public EvalRole getLevel() {
		return level;
	}
	public void setLevel(EvalRole level) {
		this.level = level;
	}
	public boolean isSync() {
		return sync;
	}
	public void setSync(boolean sync) {
		this.sync = sync;
	}
	public boolean isPreview() {
		return preview;
	}
	public void setPreview(boolean preview) {
		this.preview = preview;
	}
	
	
}
