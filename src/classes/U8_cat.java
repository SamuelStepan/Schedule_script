package classes;
/*
 * Child class of category representing category U8
 */
public class U8_cat extends Category{
	public U8_cat(Team[] teams, int time_option) {
		super(teams);
		name = "U8";
		// max_min = 40;
		time_options = new int[] { 6, 7 };
		check_time_option(time_option);			
	}
}
