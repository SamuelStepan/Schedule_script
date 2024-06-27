package classes;
import java.util.HashMap;
import java.util.Map;

/**
 * abstract class representing category
 */
public abstract class Category {
    public String name;
    public Team[] teams;
    public Map<Integer,Group> groups = new HashMap<>();
    // public int max_min;
    public int[] time_options; //array containing options for lenght of match
    public int time_option;
    // public static Field[] fields;
    public boolean ok_loaded = true;
    public String[] abc = new String[] { "A", "B", "C", "D" }; //for each category can be up to 4 groups, this is variable to help me name them when created
    public int id;
    public static int max_id = 0;
    public int basic_group_size;
    public boolean created_matches = false;
    
    /**
     * checks if number of given teams is correct, then creates all groups based on number of teams 
     * @param teams
     */
    public Category(Team[] teams) {
        this.teams = teams;
        id = max_id++;
        if (teams.length < 3 || teams.length > 20) {
            ok_loaded = false;
            new Exception("Number of teams is incorrect");
        } else {
            //creating groups
            int num_groups = teams.length % 5 == 0 ? teams.length / 5 : (teams.length / 5) + 1; //if number of teams is not divisible by 5, I must add one more group
            basic_group_size = teams.length / num_groups;
            int remain_div = teams.length % num_groups; //this tells me how many groups will have 1 more team than basic group size
            int coppy_index = 0; //variable that I will use for loading subteams of teams to groups
            for (int i = 0; i < num_groups; i++) {
                int group_size = i < remain_div ? basic_group_size + 1 : basic_group_size;
                Team[] group_teams = new Team[group_size];
                System.arraycopy(teams, coppy_index, group_teams, 0, group_size);
                coppy_index += group_size;
                Group new_group = new Group("Group " + abc[i], group_teams);
                groups.put(new_group.id, new_group);
            }
        }

    }
    
    /**
     * returns id of smallest group in category, that does not have created matches, if all groups have created matches, returns -1
     * @return
     */
    public int get_smallest_group() {
        int smallest_group = 6; //size of smallest group, each group has atleast 5 teams
        int id_smallest_group = -1;
        for (Map.Entry<Integer, Group> entry : groups.entrySet()) {
            if (!entry.getValue().created_matches && entry.getValue().teams.length < smallest_group) {
                smallest_group = entry.getValue().teams.length;
                id_smallest_group = entry.getKey();
            }
        }
        groups.get(id_smallest_group).created_matches = true;
        return id_smallest_group;
    }
    
    /**
     * if given time_option (in minutes) is time of the match that can be played for this category, saves him
     * @param time_option representing time of the match
     */
    protected void check_time_option(int time_option) {
        boolean ok_time_option = false;
        for (int option : time_options) {
            if (option == time_option)
                ok_time_option = true;
        }
        if (!ok_time_option) {
            time_option = time_options[0];
            ok_loaded = false;
            new Exception("Incorect time option");
        } else
            this.time_option = time_option;
    }
    
    
    /** 
     * @param name
     * @return Group
     */
    /*
     * returns pointer to group of given name
     */
    public Group get_group(String name) {
        for (Group group : groups.values()) {
            if (group.name.equals(name)) {
                return group;
            }
        }
        return null;
    }
}
