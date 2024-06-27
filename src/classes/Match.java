package classes;

/*
 * Class representing match
 */
public class Match {
	public Team team_A;
	public Team team_B;
	public Team referee_team;
	public String referee;
	public int start; 	//start of match in minutes
	public int match_t;		//time of length of match in minutes
	public Field field;
	public int[] score = {};
	public int id;
	public static int max_id = 0;

	/*
	 * creates match on given field between given team_A and team_B with given referee, that starts at start and lasts match_t (both in minutes)
	 */
	Match(Team team_A, Team team_B, Team referee_team, int start, int match_t, Field field) {
		this.team_A = team_A;
		this.team_B = team_B;
		this.referee_team = referee_team;
		referee = referee_team.coach;
		this.start = start;
		this.match_t = match_t + team_A.time_to_prepare;
		// this.team_A.time_played += match_t;
		// this.team_B.time_played += match_t;
		this.field = field;
		id = max_id++;
	}

	/*
	 * this shall be called only for creating empty match for group matches
	 */
	Match(Team team_A, Team team_B) {
		this.team_A = team_A;
		this.team_B = team_B;
	}

	
	/** 
	 * @param score
	 */
	/*
	 * saves given score (int array of 2 elements) and based on score updates points of team_A and team_B(2 for winning, one for draw)
	 */
	public void set_score(int[] score) {
		this.score = score;
		if (score[0] > score[1]) {
			team_A.points += 2;
		} else if (score[0] == score[1]) {
			team_A.points += 1;
			team_B.points += 1;
		} else
			team_B.points += 2;
	}

	
	/** 
	 * @return String
	 */
	/*
	 * Override of function toString() for debugging
	 */
	public String toString() {
		return team_A.toString() + " " + team_B.toString() + " at " + start + "-" + (match_t + start);
	}
	
}
