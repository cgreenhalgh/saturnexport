/**
 * 
 */
package saturnexport;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Logger;

import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONObject;

import saturnexport.model.Assessment;
import saturnexport.model.Module;
import saturnexport.model.School;
import saturnexport.model.SchoolComparator;
import saturnexport.model.Staff;
import saturnexport.model.Year;


/** Dump information from Saturn, in JSON format, to initialise feedback plugin planned for Moodle.
 * 
 * @author cmg
 *
 */
public class DumpForFeedback {
	// at least in the UK Saturn DB...
	private static final String ASP_TYPE_STANDARD = "000009";
	private static final String STATUS_LIVE = "Live";
	private static final String STATUS_DORMANT = "Dormant";
	static Logger logger = Logger.getLogger(DumpForFeedback.class.getName());


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 *
		 */
		if (args.length==0) {
			logger.log(Level.SEVERE, "Usage: <jdbc.properties> [<outfile> <year-code> <site-code> [<school-code> ...]]");
			logger.log(Level.INFO, "For example (UK, 2011/12, CS): etc/jdbc-local.properties export.json 000111 UK 000245");
			System.exit(-1);
		}
		Connection conn = JdbcUtils.getConnection(args[0]);
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			Map<String,School> schools = getSchools(conn);
			Map<String,Year> years = getYears(conn);
			Year year = args.length>2 ? years.get(args[2]) : null;
			if (args.length<4 || year==null) {
				if (args.length>2)
					logger.log(Level.SEVERE, "Unknown year code "+args[1]);
				System.out.println("Years:");
				
				for (String yc : getYearCodes(years)) {
					Year y = years.get(yc);
					System.out.println("Found year: "+y.getCode()+" "+y.getDescr());
				}
				System.out.println("Schools/units:");
				List<School> schoolList = new ArrayList<School>();
				schoolList.addAll(schools.values());
				Collections.sort(schoolList, new SchoolComparator());
				for (School s : schoolList) {
					System.out.println("Found School: "+s.getCode()+" "+s.getDescr());
				}
				System.exit(0);
			}
			File outfile = new File(args[1]);
			logger.log(Level.INFO, "Dump to "+outfile);
			
			File outfilecsv = new File(args[1]+".csv");
			logger.log(Level.INFO, "Also dump as CSV to "+outfilecsv);
			
			PrintWriter csvpw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfilecsv), "UTF-8"));
			csvpw.println("year,semester,level,modulecode,moduletitle,status,staff,assesstype,assessrequirements,assesspercent,assessstatus,standardexam,cwsubmissioninfo,cwdeadline,feedbackinfo,feedbackdue");
			
			logger.log(Level.INFO,"Dumping year "+year.getCode()+" ("+year.getDescr()+")");
			String siteCode = args[3];
			logger.log(Level.INFO, "Using site code "+siteCode);
			
			JSONObject dump = new JSONObject();
			JSONObject header = new JSONObject();
			dump.put("header", header);
			header.put("timestamp", System.currentTimeMillis()/1000);
			header.put("file_version", "1.0");
			header.put("exporter_version", "0.1");
			JSONArray sites = new JSONArray();
			header.put("sites", sites);
			sites.put(siteCode);
			JSONArray yeararr = new JSONArray();
			header.put("years", yeararr);			
			JSONObject yearobj = new JSONObject();
			yeararr.put(yearobj);
			yearobj.put("code", year.getCode());
			yearobj.put("descr", year.getDescr());			
			
			List<School> schoolList = new ArrayList<School>();
			if (args.length==4) {
				logger.log(Level.INFO, "Dumping all schools...");
				schoolList.addAll(schools.values());
				Collections.sort(schoolList, new SchoolComparator());
			} 
			else {
				for (int ai=4; ai<args.length; ai++) {
					String sc =args[ai];
					School s = schools.get(sc);
					if (s==null) {
						logger.log(Level.SEVERE, "Could not find school "+sc);
						System.exit(-1);
					}
					schoolList.add(s);
				}
			}
			JSONArray orgarr = new JSONArray();
			header.put("orgs", orgarr);
			
			JSONArray modulearr = new JSONArray();
			dump.put("modules", modulearr);
			
			Map<String,String> statuses = getStatuses(conn);
			
			for (School s : schoolList) {
				logger.log(Level.INFO, "Dumping school "+s.getCode()+" "+s.getDescr());
				JSONObject orgobj = new JSONObject();
				orgarr.put(orgobj);
				orgobj.put("code", s.getCode());
				orgobj.put("descr", s.getDescr());
				// TODO ...
				
				List<Module> modules = getModules(conn, year, s, statuses);
				for (Module m : modules) {
					if (STATUS_DORMANT.equals(statuses.get(m.getStatus_code())))
					{
						logger.log(Level.INFO, "Skip dormant module "+m.getCode()+" ("+statuses.get(m.getStatus_code())+")");
						continue;
					}
					JSONObject mobj = new JSONObject();
					modulearr.put(mobj);
					
					mobj.put("id", m.getId());
					mobj.put("code", m.getCode());
					mobj.put("title", m.getTitle());
					mobj.put("org", orgobj);
					mobj.put("site_code", siteCode);
					JSONObject taught = new JSONObject();
					taught.put("year", m.getYear_descr());
					taught.put("semester", m.getSemester());
					mobj.put("taught", taught);
					String status = statuses.get(m.getStatus_code());
					if (status!=null)
						mobj.put("status", status);
					else
						logger.log(Level.WARNING, "Unknown status code "+m.getStatus_code());
					mobj.put("level", m.getLevel());
					
					JSONArray assessarr = new JSONArray();
					mobj.put("assess", assessarr);
					List<Assessment> as = getAssessments(conn, m);
					for (Assessment a : as) {
						JSONObject aobj = new JSONObject();
						assessarr.put(aobj);
						aobj.put("type", a.getType());
						aobj.put("requirements", a.getRequirements());
						aobj.put("percent", a.getPercent());
						aobj.put("status", a.getStatus());
					}
					
					JSONArray staffarr = new JSONArray();
					mobj.put("staff", staffarr);
					List<Staff> staff = getStaff(conn, m, year);
					for (Staff st : staff) {
						JSONObject stobj = new JSONObject();
						staffarr.put(stobj);
						stobj.put("id", st.getId());
						stobj.put("name", st.getName());
						JSONObject storg = new JSONObject();
						storg.put("code", st.getOrg_code());
						storg.put("descr", st.getOrg_descr());
						stobj.put("org", storg);
						stobj.put("relationship", st.getRelationship());
					}
					
					// CSV...
					StringBuffer staffString = new StringBuffer();
					for (Staff st : staff) 
					{
						if (staffString.length()>0)
							staffString.append("; ");
						staffString.append(escape(st.getName()));
					}
					for (Assessment a : as) {
					    // year
						csvpw.print("\"'"+m.getYear_descr()+"\",");
						// semester
						csvpw.print(m.getSemester()+",");
					    // level
						csvpw.print(m.getLevel()+",");
					    // modulecode
						csvpw.print(m.getCode()+",");
					    // moduletitle
						csvpw.print(escape(m.getTitle())+",");
					    // (status)
						csvpw.print(statuses.get(m.getStatus_code())+",");
					    // staff (concat?)
						csvpw.print(escape(staffString.toString())+",");
					    // assesstype
						csvpw.print(escape(a.getType())+",");
					    // assessrequirements
						csvpw.print(escape(a.getRequirements())+",");
					    // assesspercent
						csvpw.print(a.getPercent()+",");
					    // assessstatus
						csvpw.print(a.getStatus()+",");
					    // standardexam (Y/N)
						csvpw.print(",");
					    // cwsubmissioninfo
						csvpw.print(",");
					    // cwdeadline
						csvpw.print(",");
					    // feedbackinfo
						csvpw.print(",");
					    // feedbackdue
						csvpw.println();
					}
				}
			}
			
			Writer w = new OutputStreamWriter(new FileOutputStream(outfile), "UTF-8");
			dump.write(w);
			w.close();
			
			csvpw.close();
			
			logger.log(Level.INFO, "Written to "+outfile);
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error", e);
		}
		finally {
			tidy(rs, stmt);
			try { conn.close(); } catch (Throwable ignore) {}
		}
	}
	private static String escape(String name) {
		if (name==null)
			return "";
		else if (name.contains(",") || name.contains("\""))
		{
			StringBuffer sb = new StringBuffer();	
			sb.append('"');
			for (int i=0; i<name.length(); i++) {
				char c = name.charAt(i);
				if (c=='"') 
					sb.append("\"\"");
				else if (c=='\n')
					sb.append("\\n");
				else
					sb.append(c);
			}
			sb.append('"');
			return sb.toString();
		}
		else
			return name;
	}
	private static Map<String, School> getSchools(Connection conn) throws SQLException {
		Map<String,School> schools = new HashMap<String,School>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			// example: get known schools from saturn3 using stored procedure mod_get_schools
			stmt = conn.prepareStatement("{call mod_get_schools()}", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
			while(rs.next()) {
				School s= new School(rs.getString(1), rs.getString(2));
				schools.put(s.getCode(), s);
			}
		} 
		finally {
			tidy(rs, stmt);
		}
		return schools;
	}
	private static Map<String, Year> getYears(Connection conn) throws SQLException {
		Map<String,Year> years = new HashMap<String,Year>();

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			// example: get known schools from saturn3 using stored procedure mod_get_schools
			stmt = conn.prepareStatement("{call mod_year_list()}", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
			while(rs.next()) {
				Year y = new Year(rs.getString(1), rs.getString(2));
				years.put(y.getCode(), y);
			}
		} 
		finally {
			tidy(rs, stmt);
		}
		return years;
	}
	private static TreeSet<String> getYearCodes(Map<String,Year> map) {
		TreeSet<String> ts = new TreeSet<String>();
		ts.addAll(map.keySet());
		return ts;
	}
	private static TreeSet<String> getSchoolCodes(Map<String,School> map) {
		TreeSet<String> ts = new TreeSet<String>();
		ts.addAll(map.keySet());
		return ts;
	}
	private static Map<String,String> getStatuses(Connection conn) throws SQLException {
		Map<String,String> statuses = new HashMap<String,String>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			// example: get known schools from saturn3 using stored procedure mod_get_schools
			stmt = conn.prepareStatement("{call mod_get_status_list()}", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
			while(rs.next()) {
				// nb descr, code
				statuses.put(rs.getString(2), rs.getString(1));
			}
		} 
		finally {
			tidy(rs, stmt);
		}
		return statuses;

	}
	private static List<Module> getModules(Connection conn, Year year, School school, Map<String,String> statuses) throws SQLException {
		List<Module> modules = new LinkedList<Module>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {			
			stmt = conn.prepareStatement("{call mod_get_crs_asp(?,?,?)}", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, year.getCode());
			stmt.setString(2, school.getCode());
			stmt.setNull(3, java.sql.Types.CHAR);
			rs = stmt.executeQuery();
			while(rs.next()) {
				//crs_id/1 year_id mnem/3 title/4 asp_type_descr crs_id1 asp_type_id taught_lid semester/9 level_lid Level/11 org_name year_descr/13 status_lid/14 suitable_outside_school new_module fy_module_list
				modules.add(new Module(rs.getString(1), rs.getString(3), rs.getString(4), rs.getString(13), rs.getString(9), rs.getString(11), rs.getString(14)));
			}
		}
		finally {
			tidy(rs, stmt);
		}
		return modules;
	}
	private static List<Assessment> getAssessments(Connection conn, Module m) throws SQLException {
		List<Assessment> as = new LinkedList<Assessment>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {			
			stmt = conn.prepareStatement("{call mod_get_cmod_assess(?)}", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, m.getId());
			//logger.log(Level.INFO,"call mod_get_cmod_assess("+m.getCode()+")");
			rs = stmt.executeQuery();
			while(rs.next()) {
				//logger.log(Level.INFO,"-> assess "+rs.getString(2));
				//assess_type_lid assess_type/2 status_lid status/4 percent/5 requirements/6 is_default re_sit_status_lid re_sit_status length assess_timestamp
				as.add(new Assessment(rs.getString(2), rs.getString(4), rs.getInt(5), rs.getString(6)));
			}
		}
		finally {
			tidy(rs, stmt);
		}
		return as;
	}
	private static List<Staff> getStaff(Connection conn, Module m, Year y) throws SQLException {
		List<Staff> as = new LinkedList<Staff>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {			
			stmt = conn.prepareStatement("{call mod_get_crs_asp_staff(?,?,?)}", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, m.getId());
			stmt.setString(2, y.getCode());
			stmt.setString(3, ASP_TYPE_STANDARD);
			//logger.log(Level.INFO,"call mod_get_cmod_assess("+m.getCode()+")");
			rs = stmt.executeQuery();
			while(rs.next()) {
				//logger.log(Level.INFO,"-> assess "+rs.getString(2));
				//staff_id/1 staff_name/2 org_id/3 org_name/4  relationship_lid rel/6 start_date email end_date stafftimestamp
				as.add(new Staff(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(6)));
			}
		}
		finally {
			tidy(rs, stmt);
		}
		return as;
	}
	public static void tidy(ResultSet rs, Statement stmt) {
		if (rs!=null)
			try { rs.close(); } catch (Throwable ignore) {}
		if (stmt!=null)
			try { stmt.close(); } catch (Throwable ignore) {}
	}

}
