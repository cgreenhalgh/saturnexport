/**
 * 
 */
package saturnexport.model;

/**
 * @author cmg
 *
 */
public class Year {
	protected String code;
	protected String descr;
	/** cons */
	public Year() {}
	/**
	 * @param code
	 * @param descr
	 */
	public Year(String code, String descr) {
		super();
		this.code = code;
		this.descr = descr;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the descr
	 */
	public String getDescr() {
		return descr;
	}
	/**
	 * @param descr the descr to set
	 */
	public void setDescr(String descr) {
		this.descr = descr;
	}
	
}
