/**
 * 
 */
package saturnexport.model;

/**
 * @author cmg
 *
 */
public class Module {
	protected String id;
	protected String code;
	protected String title;
	protected String year_descr;
	protected String semester;
	protected String level;
	protected String status_code;
	/** cons */
	public Module() {}
	/**
	 * @param id
	 * @param code
	 * @param title
	 * @param year_descr
	 * @param semester
	 * @param level
	 * @param status
	 */
	public Module(String id, String code, String title, String year_descr,
			String semester, String level, String status) {
		super();
		this.id = id;
		this.code = code;
		this.title = title;
		this.year_descr = year_descr;
		this.semester = semester;
		this.level = level;
		this.status_code = status;
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the year_descr
	 */
	public String getYear_descr() {
		return year_descr;
	}
	/**
	 * @param year_descr the year_descr to set
	 */
	public void setYear_descr(String year_descr) {
		this.year_descr = year_descr;
	}
	/**
	 * @return the semester
	 */
	public String getSemester() {
		return semester;
	}
	/**
	 * @param semester the semester to set
	 */
	public void setSemester(String semester) {
		this.semester = semester;
	}
	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}
	/**
	 * @return the status_code
	 */
	public String getStatus_code() {
		return status_code;
	}
	/**
	 * @param status_code the status_code to set
	 */
	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}
	
}
