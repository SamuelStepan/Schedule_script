package classes;
/*
 * Class representing team
 */
public class Team {
	public String name;
	public String coach;
	public int id;
	public static int max_id = 0;
	public int points;
	// public int time_played;
	public int time_to_prepare; //time that is needed before the match for team to prepare, it might differ for each category
	// public int max_played_time;

	/*
	 * creates team of given name with given coach and time to prepare before match (in minutes)
	 */
	public Team(String name, String coach, int time_to_prepare) {
		this.name = name;
		this.coach = coach;
		id = max_id++;
		points = 0;
		// time_played = 0;
		this.time_to_prepare = time_to_prepare;
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
