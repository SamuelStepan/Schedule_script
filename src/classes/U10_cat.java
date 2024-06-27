package classes;
/*
 * Child class of category representing category U10
 */
public class U10_cat extends Category{
	public U10_cat(Team[] teams, int time_option) {
		super(teams);
		name = "U10";
		// max_min = 40;
		time_options = new int[] { 6, 7, 8 };
		check_time_option(time_option);	
	}
}
