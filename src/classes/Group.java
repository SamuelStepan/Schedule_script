package classes;
import java.util.HashMap;
import java.util.Map;

/*
 * Class representing group of some category
 */
public class Group {
	public String name;
	public int id;
	public static int max_id = 0;
	public Team[] teams;
	public boolean created_matches = false;
	public Map<Integer, Map<Integer, Match>> group_matches = new HashMap<Integer, Map<Integer, Match>>();

	/*
	 * creates group of given name holding given teams, creates 2D map of matches group_matches
	 */
	public Group(String name, Team[] teams) {
		this.name = name;
		this.teams = teams;
		id = max_id++;
		for (Team team_A : teams) {
			Map<Integer, Match> team_A_matches = new HashMap<Integer, Match>();
			for (Team team_B : teams) {
				team_A_matches.put(team_B.id, new Match(team_A, team_B));
			}
			group_matches.put(team_A.id, team_A_matches);
		}
	}

	
	/** 
	 * @param match
	 */
	/*
	 * saves given match to 2d map group_matches
	 */
	public void save_match(Match match) {
		group_matches.get(match.team_A.id).replace(match.team_B.id, match);
		Match sym_match = new Match(match.team_B, match.team_A, match.referee_team, match.start, match.match_t,
				match.field); //copy of the match but team_A and team_B are swapped
		group_matches.get(match.team_B.id).replace(match.team_A.id, sym_match);
	}

	
	/** 
	 * @param team_s
	 * @return Team
	 */
	/*
	 * returns pointer to Team of given name
	 */
	public Team get_team(String team_s) {
		for (Team team : teams) {
			if (team.name.equals(team_s)) {
				return team;
			}
		}
		return null;
	}
}
