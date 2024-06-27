package classes;
import java.util.Map;
import java.util.TreeMap;

/*
 * Class representing field
 */
public class Field {
	public int id;
	public static int max_id = 0;
	public String name;
	public Map<Integer, Match> matches = new TreeMap<Integer, Match>();
	public int curr_time; //variable that holds time of possible start of the new match at this field

	/*
	 * creates field of given name
	 */
	public Field(String name) {
		this.name = name;
		id = max_id++;
		curr_time = 0;
	}

	
	/** 
	 * @param match
	 */
	/*
	 * saves given match and updates curr_time (time representing possible start of new match)
	 */
	public void add_match(Match match) {
		matches.put(match.id, match);
		curr_time = match.start + match.match_t;
	}
	
	
	/** 
	 * @return String
	 */
	/*
	 * Override of function toString() for debugging
	 */
	public String toString() {
		return name + " (id = " + id + ")";
	}
}
