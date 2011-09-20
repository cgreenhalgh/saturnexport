/**
 * 
 */
package saturnexport.model;

import java.util.Comparator;

/**
 * @author cmg
 *
 */
public class SchoolComparator implements Comparator<School> {

	@Override
	public int compare(School s1, School s2) {
		return s1.getDescr().compareTo(s2.getDescr());
	}

}
