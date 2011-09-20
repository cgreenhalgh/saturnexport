/**
 * 
 */
package saturnexport.model;

/**
 * @author cmg
 *
 */
public class Staff {
	protected String id;
	protected String name;
	protected String org_code;
	protected String org_descr;
	protected String relationship;
	/** cons */
	public Staff() {}
	/**
	 * @param id
	 * @param name
	 * @param org_code
	 * @param org_descr
	 * @param relationship
	 */
	public Staff(String id, String name, String org_code, String org_descr,
			String relationship) {
		super();
		this.id = id;
		this.name = name;
		this.org_code = org_code;
		this.org_descr = org_descr;
		this.relationship = relationship;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the org_code
	 */
	public String getOrg_code() {
		return org_code;
	}
	/**
	 * @param org_code the org_code to set
	 */
	public void setOrg_code(String org_code) {
		this.org_code = org_code;
	}
	/**
	 * @return the org_descr
	 */
	public String getOrg_descr() {
		return org_descr;
	}
	/**
	 * @param org_descr the org_descr to set
	 */
	public void setOrg_descr(String org_descr) {
		this.org_descr = org_descr;
	}
	/**
	 * @return the relationship
	 */
	public String getRelationship() {
		return relationship;
	}
	/**
	 * @param relationship the relationship to set
	 */
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	
}
