package classes;
/*
 * Child class of category representing category U12
 */
public class U12_cat extends Category {
	public U12_cat(Team[] teams,int time_option) {
		super(teams);
		name = "U12";
		// max_min = 60;
		time_options = new int[] { 7, 8, 9 };
		check_time_option(time_option);	
	}
	
}
