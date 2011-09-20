/**
 * 
 */
package saturnexport.model;

/**
 * @author cmg
 *
 */
public class Assessment {
	protected String type;
	protected String status;
	protected int percent;
	protected String requirements;
	/** cons */
	public Assessment() {}
	/**
	 * @param type
	 * @param status
	 * @param percent
	 * @param requirements
	 */
	public Assessment(String type, String status, int percent,
			String requirements) {
		super();
		this.type = type;
		this.status = status;
		this.percent = percent;
		this.requirements = requirements;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the percent
	 */
	public int getPercent() {
		return percent;
	}
	/**
	 * @param percent the percent to set
	 */
	public void setPercent(int percent) {
		this.percent = percent;
	}
	/**
	 * @return the requirements
	 */
	public String getRequirements() {
		return requirements;
	}
	/**
	 * @param requirements the requirements to set
	 */
	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}
	
}
