/**
 * 
 */
package saturnexport.model;

/**
 * @author cmg
 *
 */
public class School {
	protected String code;
	protected String descr;
	/** cons */
	public School() {}
	/**
	 * @param code
	 * @param descr
	 */
	public School(String code, String descr) {
		super();
		this.code = code;
		this.descr = descr;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	
}
