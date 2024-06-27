package classes;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.io.File;  
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;  
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import javax.print.DocFlavor.STRING;

public class App {
    //2d array signaling how much time it costs to travel from one field to another(when index i=j the time is 0),
    //for algorithm to work properly, travel time can´t be more than rest time(rest_t), if it is, then fields are
    //not conected and it shall be set to -1
    public static int[][] travel_t_fields;

    public static int num_cat = 3; //number of categories
    public static int rest_t = 6;     //minimal time needed for team to rest between two matches, I assume its less than minimal length of match
    public static int time_to_prepare_u8 = 3;
    public static int match_t_u8 = 6;
    public static int time_to_prepare_u10 = 2;
    public static int match_t_u10 = 8;
    public static int time_to_prepare_u12 = 2;
    public static int match_t_u12 = 9;
    public static int num_fields;
    public static Field[] fields;
    public static Category[] categories = new Category[num_cat];
    public static int tourney_start_hours = 9;
    public static int tourney_start_mins = 0;

    
    /** 
     * funtion that returns string representing real time of match
     * @param time_in_min   time in minutes from start of the tourney
     * @return String
     */
    public static String get_time(int time_in_min) {
        time_in_min += tourney_start_mins + 60*tourney_start_hours;
        int hours = time_in_min / 60;
        int mins = time_in_min % 60;
        String hours_s = hours < 10 ? "0" + Integer.toString(hours) : Integer.toString(hours);
        String mins_s = mins < 10 ? "0" + Integer.toString(mins) : Integer.toString(mins);
        return hours_s + ":" + mins_s;
    }
    
    
    /** 
     * returns id of category with smallest group size (category does not have created matches yet)
     * @return int
     */
    public static int get_smallest_cat() {
        int smallest_group_size = 6; // size of smallest basic group size in some category
        int index_min_group = -1;
        for (int i = 0; i < 3; i++) {
            if (!categories[i].created_matches && categories[i].basic_group_size < smallest_group_size) {
                smallest_group_size = categories[i].basic_group_size;
                index_min_group = i;
            }
        }
        categories[index_min_group].created_matches = true; //after calling this function all matches for each group of category shall be created
        return index_min_group;
    }
    
    
    /** 
     * returns index of field which has the smallest starting time for match
     * @return int
     */
    public static int get_free_field() {
        int smallest_time = Integer.MAX_VALUE;
        int index_field = -1;
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].curr_time < smallest_time) {
                smallest_time = fields[i].curr_time;
                index_field = i;
            }
        }
        return index_field;
    }

    
    /** 
     * function returns true if there is some connected field from fields[index_field_from]
     * @param index_field_from
     * @return boolean
     */
    public static boolean is_conected_field(int index_field_from) {
        for (int i = 0; i < fields.length; i++)
            if (travel_t_fields[index_field_from][i] != -1)
                return true;
        return false;
    }
    
    
    /** 
     * if there is some field that is conected to fields[index_field_from] and it would be better to start next match in some other
     * field, then function returns index of that field. Else returns -1 (none conected fields or it´s not optimal)
     * @param index_field_from
     * @return int
     */
    public static int get_free_conected_field(int index_field_from) {
        int index_con_field = -1;    //index of conected free field
        int con_field_min_time = Integer.MAX_VALUE;   //minimum time of possible start of match of conected field (including travel time)
        for (int i = 0; i < fields.length; i++) {
            if (index_field_from != i) {
                if (travel_t_fields[index_field_from][i] != -1
                        && fields[i].curr_time + travel_t_fields[index_field_from][i] < con_field_min_time) {
                    index_con_field = i;
                    con_field_min_time = fields[i].curr_time + travel_t_fields[index_field_from][i];
                }
            }
        }
        if (con_field_min_time < fields[index_field_from].curr_time)
            return index_con_field;
        else
            return -1;
    }

    
    /** 
     * for given group creates all matches and saves them
     * @param group
     * @param match_t
     */
    public static void create_matches_group(Group group, int match_t) {
        int index_field_1 = -1;
        int index_field_2 = -1;
        int referee_index = -1;
        int[][][] matches;
        int[][] referees;
        int index_team_A;
        int index_team_B;
        boolean both_matches_per_round = false;
        int team_size = group.teams.length;
                switch (team_size) {
                    //category has only 1 group with 3 teams so we fill matches only in one field and we must give teams rest after match
                    case 3:
                        index_field_1 = get_free_field();
                        int m_rest = 0; //match rest
                        for (int i = 0; i < team_size; i++) {
                            for (int j = 0; j < team_size; j++) {
                                if (j > i) {
                                    for (int k = 0; k < team_size; k++) {
                                        if (k != i && k != j) {
                                            referee_index = k;
                                            break;
                                        }
                                    }
                                    if (i + j > 1)
                                        m_rest = rest_t;

                                    Match new_match = new Match(group.teams[i], group.teams[j],
                                            group.teams[referee_index],
                                            fields[index_field_1].curr_time + m_rest, match_t, fields[index_field_1]);
                                    fields[index_field_1].add_match(new_match);
                                    group.save_match(new_match);
                                }
                            }
                        }
                        break;
                    //category has only 1 group with 4 or 5 teams so we can use 2 fields with rest after every round or only 1 field with rest after every second match (end of round)
                    case 4:
                    case 5:
                        index_field_1 = get_free_field();
                        int num_rounds = -1;
                        //3d array, first [] is for round, second [] is for 1st or 2nd match in that round, 3rd [] is for team_A at index 0 and team_B at index 1
                        if (team_size == 4) {
                            matches = new int[][][] { { { 0, 3 }, { 1, 2 } }, { { 3, 2 }, { 0, 1 } },
                                    { { 1, 3 }, { 2, 0 } } };
                            referees = new int[][] { { 1, 3 }, { 0, 2 }, { 0, 1 } }; //array holding information of index of referee team for each match
                            num_rounds = 3;
                        } else { //num_teams == 5 
                            matches = new int[][][] { { { 0, 1 }, { 2, 3 } }, { { 0, 4 }, { 1, 3 } }, //int this way none team plays two matches after each other
                                    { { 2, 4 }, { 0, 3 } }, { { 1, 4 }, { 0, 2 } }, { { 3, 4 }, { 1, 2 } } };
                            referees = new int[][] { { 4, 4 }, { 2, 2 }, { 1, 1 }, { 3, 3 }, { 0, 0 } }; //array holding information of index of referee team for each match
                            num_rounds = 5;
                        }

                        //there is some connected field that we might use
                        if (is_conected_field(index_field_1)) {
                            both_matches_per_round = false;
                            int[] match_rest = new int[] { 0, 0 };
                            for (int i = 0; i < num_rounds; i++) {
                                //playing teams of first match of the round
                                index_team_A = matches[i][0][0];
                                index_team_B = matches[i][0][1];
                                if (team_size == 4)
                                    referee_index = index_team_A; //if for second match of the current round we can use another field, we must use referee from playing field
                                else { //num_teams == 5
                                    referee_index = referees[i][0];
                                }

                                //first match of the round
                                Match new_match_1 = new Match(group.teams[index_team_A],
                                        group.teams[index_team_B],
                                        group.teams[referee_index], fields[index_field_1].curr_time + match_rest[0],
                                        match_t, fields[index_field_1]);

                                fields[index_field_1].add_match(new_match_1);
                                group.save_match(new_match_1);

                                if (!both_matches_per_round) //if we have already found some second field to use, we no longer search for another one
                                    index_field_2 = get_free_conected_field(index_field_1);

                                //playing teams of second match of the round
                                index_team_A = matches[i][1][0];
                                index_team_B = matches[i][1][1];

                                //there is connected field but some match is alredy in it or it is not efficient to travel
                                if (index_field_2 == -1) {
                                    if (team_size == 4) {
                                        new_match_1.referee_team = group.teams[referees[i][0]]; //in this situation we can use referee from other team for the first match of the round
                                        new_match_1.referee = new_match_1.referee_team.coach;
                                    } //else we do not need to change referee of the first match

                                    referee_index = referees[i][1];
                                    Match new_match_2 = new Match(group.teams[index_team_A],
                                            group.teams[index_team_B],
                                            group.teams[referee_index],
                                            fields[index_field_1].curr_time + match_rest[1],
                                            match_t, fields[index_field_1]);

                                    fields[index_field_1].add_match(new_match_2);
                                    group.save_match(new_match_2);
                                    both_matches_per_round = false;
                                    if (num_rounds == 3) { //if num_rounds == 5, then there is no need for pause between matches
                                        match_rest[0] = rest_t;
                                        match_rest[1] = 0;
                                    }
                                } else { //there is another field that we shall use
                                    if (team_size == 4)
                                        referee_index = index_team_B; //we must use referee from playing team
                                    else
                                        referee_index = referees[i][1]; //we can use referee from non playing team

                                    Match new_match_2 = new Match(group.teams[index_team_A],
                                            group.teams[index_team_B],
                                            group.teams[referee_index], new_match_1.start, match_t, 
                                            fields[index_field_2]);

                                    fields[index_field_2].add_match(new_match_2);
                                    group.save_match(new_match_2);
                                    //now we need pause after end of the round
                                    match_rest[0] = rest_t;
                                    match_rest[1] = rest_t;
                                    both_matches_per_round = true;
                                }
                            }
                        } else { //there is none connected field with fields[index_field_1], if teams size is 4, we must give teams rest after every second match (end of round)
                            int[] match_rest = new int[] { 0, 0 }; //first round does not need rest before start of the match
                            for (int i = 0; i < num_rounds; i++) {
                                if (i != 0 && num_rounds == 3) { //if num_rounds == 5 or it´s first round(i==1), then there is no need for pause after end of the round
                                    match_rest[0] = rest_t;
                                    match_rest[1] = 0;
                                }
                                for (int j = 0; j < 2; j++) {
                                    index_team_A = matches[i][j][0];
                                    index_team_B = matches[i][j][1];
                                    referee_index = referees[i][j]; //we can use referee from non playing team
                                    Match new_match = new Match(group.teams[index_team_A],
                                            group.teams[index_team_B],
                                            group.teams[referee_index],
                                            fields[index_field_1].curr_time + match_rest[j],
                                            match_t, fields[index_field_1]);

                                    fields[index_field_1].add_match(new_match);
                                    group.save_match(new_match);
                                }
                            }
                        }
                        break;
                    default:
                        new Exception("Number of teams at given group is wrong");
                        break;
                }
    }

    
    /** 
     * function that creates all matches for all groups of every category
     */
    public static void create_matches() {
        for (int i = 0; i < num_cat; i++) {
            int index_cat = get_smallest_cat(); //index of category with smallest basic group size that has not yet created all matches
            for (int j = 0; j < categories[index_cat].groups.size(); j++) {
                int id_group = categories[index_cat].get_smallest_group();   //id of smallest group in current category
                create_matches_group(categories[index_cat].groups.get(id_group), categories[index_cat].time_option);
            }
        }
    }

    
    /** 
     * function that prints out to console matches of given field
     * @param field
     */
    public static void print_matches(Field field) {
        System.out.println("Matches on the " + field.toString() + " :");
        for (Map.Entry<Integer, Match> entry : field.matches.entrySet()) {
            Match match = entry.getValue();
            System.out.println("Playing " + match.toString());
            System.out.println(" ");
        }
    }
    
    /**
     * this function exports for each category one excel sheet with table of matches for each group with score of each match (possibly unplayed)
     */
    public static void export_matches_score() {

        // Blank workbook
        XSSFWorkbook excel_exp_cat = new XSSFWorkbook();

        for (int i = 0; i < num_cat; i++) {

            // Creating a blank Excel sheet
            XSSFSheet sheet_exp_cat = excel_exp_cat.createSheet("Groups of " + categories[i].name);

            //row object
            XSSFRow row;
            int row_pos = 0; //position of row in sheet

            //for each group we create one one table
            for (Group group : categories[i].groups.values()) {

                int col_pos = 0;
                int row_loc = 0; //local row with respect to current table
                Cell cell;
                //loop though one row of table of matches
                for (Map<Integer, Match> matches : group.group_matches.values()) {
                    col_pos = 0;
                    row = sheet_exp_cat.createRow(row_pos++);
                    if (row_loc == 0) { //at first row will be name of the group at first cell and then names of the teams
                        cell = row.createCell(col_pos++);
                        cell.setCellValue((String) group.name);
                        for (Match match : matches.values()) {
                            cell = row.createCell(col_pos++);
                            cell.setCellValue((String) match.team_B.name);
                        }
                        col_pos = 0;
                        row = sheet_exp_cat.createRow(row_pos++);
                        row_loc++;
                    }
                    //first cell shall be name of the team, other cells shall be score of match  
                    for (Match match : matches.values()) {
                        if (col_pos == 0) {
                            cell = row.createCell(col_pos++);
                            cell.setCellValue((String) match.team_A.name);
                        }
                        cell = row.createCell(col_pos++);
                        String score_txt = match.score.length == 0 ? ""
                                : Integer.toString(match.score[0]) + ":" + Integer.toString(match.score[1]);
                        score_txt = (col_pos - 1) == row_loc ? "X" : score_txt;
                        cell.setCellValue((String) score_txt);
                    }
                    col_pos = 0;
                    row_loc++;
                }
                //after table of one group we skip 3 rows
                row_pos = row_pos + 3;
            }
        }
        // writing the workbook into the file...
        FileOutputStream out_excel_cat = null;
        try {
            out_excel_cat = new FileOutputStream(new File("src\\export_data\\categories_matches_score.xlsx"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            excel_exp_cat.write(out_excel_cat);
            out_excel_cat.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    /**
     * this function exports for each category one excel sheet with table of matches for each group with data of the match (start, match length, playing field, score)
     */
    public static void export_matches_data() {

        // Blank workbook
        XSSFWorkbook excel_exp_cat = new XSSFWorkbook();

        //for each category we create one sheet
        for (int i = 0; i < num_cat; i++) {

            // Creating a blank Excel sheet
            XSSFSheet sheet_exp_cat = excel_exp_cat.createSheet("Groups of " + categories[i].name);

            //row object
            XSSFRow row;
            int row_pos = 0; //position of row in sheet

            //we print table of matches for each group
            for (Group group : categories[i].groups.values()) {

                int col_pos = 0;
                Cell cell;
                String[] header = { "Team", "Oponnent", "Referee", "Start", "End", "Field", "Score_T",
                        "Score_O" };

                //first row shall have only name of the group
                row = sheet_exp_cat.createRow(row_pos++);
                cell = row.createCell(0);
                cell.setCellValue(group.name);
                
                //second row shall have headers
                row = sheet_exp_cat.createRow(row_pos++);
                for (String head : header) {
                    cell = row.createCell(col_pos++);
                    cell.setCellValue((String) head);
                }

                int teams_printed = 0;
                //loop though one row of table of matches
                for (Map<Integer, Match> matches : group.group_matches.values()) {
                    col_pos = 0;
                    int matches_printed = 0;

                    //prints only matches that were not printed before
                    for (Match match : matches.values()) {
                        if (matches_printed > teams_printed) {
                            row = sheet_exp_cat.createRow(row_pos++);
                            cell = row.createCell(col_pos++);
                            cell.setCellValue((String) match.team_A.name);
                            String[] data_string = { match.team_B.name, match.referee,
                                    get_time(match.start + match.team_A.time_to_prepare),
                                    get_time(match.start + match.match_t), match.field.name,
                                    match.score.length == 0 ? "" : Integer.toString(match.score[0]),
                                    match.score.length == 0 ? "" : Integer.toString(match.score[1]) };

                            for (String data_s : data_string) {
                                cell = row.createCell(col_pos++);
                                cell.setCellValue((String) data_s);
                            }

                        }
                        col_pos = 0;
                        matches_printed++;
                    }
                    teams_printed++;
                }
                //after table of one group we skip 3 rows
                row_pos = row_pos + 3;
            }
        }
        // writing the workbook into the file...
        FileOutputStream out_excel_cat = null;
        try {
            out_excel_cat = new FileOutputStream(new File("src\\export_data\\categories_matches_data.xlsx"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            excel_exp_cat.write(out_excel_cat);
            out_excel_cat.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    /**
     * function that imports score from file categories_matches_data.xlxs from folder import_data
     */
    public static void import_score() {
        FileInputStream file_score = null;
        try {
            file_score = new FileInputStream(new File("src\\import_data\\categories_matches_data.xlsx"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        XSSFWorkbook excel_score = null;
        try {
            excel_score = new XSSFWorkbook(file_score);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int i = 0; i < categories.length; i++) {
            XSSFSheet sheet_cat_score = excel_score.getSheetAt(i);

            Iterator<Row> row_iter = sheet_cat_score.iterator(); //iterator throw sheet_fields

            //we load next row 
            while (row_iter.hasNext()) {
                Row row = row_iter.next();
                Iterator<Cell> cell_iterator = row.cellIterator(); //iterator through cells at current row

                if (cell_iterator.hasNext()) {
                    Cell cell = cell_iterator.next();
                    
                    //we check if at first cell of the row is name of group, if it is, then we load score for each match
                    if (cell.getStringCellValue().startsWith("Group")) {
                        Group curr_group = categories[i].get_group(cell.getStringCellValue());
                        row_iter.next();

                        int num_matches;
                        if (curr_group.teams.length == 3) {
                            num_matches = 3;
                        } else if (curr_group.teams.length == 4) {
                            num_matches = 6;
                        } else { //5 teams
                            num_matches = 10;
                        }

                        //we load score of each match (we can know how many are there from group size)
                        for (int j = 0; j < num_matches; j++) {
                            row = row_iter.next();
                            cell_iterator = row.cellIterator();

                            Team team_A = curr_group.get_team(cell_iterator.next().getStringCellValue());
                            Team team_B = curr_group.get_team(cell_iterator.next().getStringCellValue());
                            //score is on 6th and 7th column (index starts from 0)
                            for (int k = 0; k < 4; k++) {
                                cell_iterator.next();
                            }
                            //if there is some score we load it and updates our data
                            if (cell_iterator.hasNext()) {
                                cell = cell_iterator.next();
                                if (!(cell == null || cell.getCellTypeEnum() == CellType.BLANK || cell.getCellTypeEnum() == CellType.STRING)) {
                                    int score_A = (int) cell.getNumericCellValue();
                                    if (cell_iterator.hasNext()) {
                                        cell = cell_iterator.next();
                                        if (!(cell == null || cell.getCellTypeEnum() == CellType.BLANK || cell.getCellTypeEnum() == CellType.STRING)) {
                                            int score_B = (int) cell.getNumericCellValue();
                                            curr_group.group_matches.get(team_A.id).get(team_B.id)
                                                    .set_score(new int[] { score_A, score_B });
                                            curr_group.group_matches.get(team_B.id).get(team_A.id)
                                                    .set_score(new int[] { score_B, score_A });
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * function that export ranking of each group of each category into excel file "categories_ranking.xlsx" to folder export_data
     */
    public static void export_ranking() {
        // Blank workbook
        XSSFWorkbook excel_exp_cat = new XSSFWorkbook();

        //for each category we print ranking for each group
        for (int i = 0; i < num_cat; i++) {

            // Creating a blank Excel sheet
            XSSFSheet sheet_exp_cat = excel_exp_cat.createSheet("Groups of " + categories[i].name);

            //row object
            XSSFRow row;
            int row_pos = 0; //position of row in sheet

            //we print ranking for each group of category
            for (Group group : categories[i].groups.values()) {

                int col_pos = 0;
                int row_loc = 0; //local row with respect to current table

                Cell cell;
                
                //we create copy of teams of the group
                Team[] teams = new Team[group.teams.length];
                System.arraycopy(group.teams, 0, teams, 0, group.teams.length);

                //sort teams by ranking
                for (int j = 0; j < teams.length; j++) {
                    for (int k = j; k < teams.length; k++) {
                        if (teams[j].points < teams[k].points) {
                            Team team_to_swap = teams[j];
                            teams[j] = teams[k];
                            teams[k] = team_to_swap;
                        }
                    }
                }

                //first row shall have name of the group at first cell
                row = sheet_exp_cat.createRow(row_pos++);
                cell = row.createCell(0);
                cell.setCellValue((String) group.name);
                
                //second row shall have headers
                String[] headers = new String[] { "Team", "Points", "Rank" };
                row = sheet_exp_cat.createRow(row_pos++);
                col_pos = 0;
                for (String header : headers) {
                    cell = row.createCell(col_pos++);
                    cell.setCellValue((String) header);
                }

                //for each team we print his score and ranking
                int rank = 1;
                for (int k = 0; k < teams.length; k++) {
                    row = sheet_exp_cat.createRow(row_pos++);
                    col_pos = 0;
                    if (k > 0) {
                        rank = teams[k].points < teams[k - 1].points ? k + 1 : rank;
                    }
                    String[] data = new String[] { teams[k].name, Integer.toString(teams[k].points),
                            Integer.toString(rank) };
                    for (int j = 0; j < 3; j++) {
                        cell = row.createCell(col_pos++);
                        cell.setCellValue((String) data[j]);
                    }
                }
                //after printing ranking of the group we skip 3 rows
                row_pos = row_pos + 3;
            }
        }
        // writing the workbook into the file...
        FileOutputStream out_excel_cat = null;
        try {
            out_excel_cat = new FileOutputStream(new File("src\\export_data\\categories_ranking.xlsx"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            excel_exp_cat.write(out_excel_cat);
            out_excel_cat.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
    /** 
     * main function, shall import teams for each category and data of fields from folder import_data,
     * exports data about matches, after user creates score of matches it also exports ranking
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        //section that imports teams for each category from excel sheets Teams_cat_U8.xlsx, Teams_cat_U10.xlsx, Teams_cat_U12.xlsx from import_data folder

        //obtaining input bytes from a file for each category 
        FileInputStream file_teams_U8 = new FileInputStream(new File("src\\import_data\\Teams_cat_U8.xlsx"));
        FileInputStream file_teams_U10 = new FileInputStream(new File("src\\import_data\\Teams_cat_U10.xlsx"));
        FileInputStream file_teams_U12 = new FileInputStream(new File("src\\import_data\\Teams_cat_U12.xlsx"));

        //creating workbooks instances that refers to .xlsx file  
        XSSFWorkbook excel_teams_U8 = new XSSFWorkbook(file_teams_U8);
        XSSFWorkbook excel_teams_U10 = new XSSFWorkbook(file_teams_U10);
        XSSFWorkbook excel_teams_U12 = new XSSFWorkbook(file_teams_U12);

        //creating a Sheets object to retrieve the objects  
        XSSFSheet sheet_teams_U8 = excel_teams_U8.getSheetAt(0);
        XSSFSheet sheet_teams_U10 = excel_teams_U10.getSheetAt(0);
        XSSFSheet sheet_teams_U12 = excel_teams_U12.getSheetAt(0);

        //iterators over excel sheets
        Iterator<Row> iter_U8 = sheet_teams_U8.iterator();
        Iterator<Row> iter_U10 = sheet_teams_U10.iterator();
        Iterator<Row> iter_U12 = sheet_teams_U12.iterator();

        //variables that we shall use in next loop 
        Iterator<Row> curr_iter = iter_U8; 
        int time_to_prep = time_to_prepare_u8;
        Team[] curr_teams_cat = new Team[20];
        int curr_teams_cat_num = 0;

        //now we load teams for each category
        for (int i = 0; i < 3; i++) {
            //based on given category we set some variables
            switch (i) {
                case 0:
                    curr_iter = iter_U8;
                    time_to_prep = time_to_prepare_u8;
                    curr_teams_cat = new Team[20];
                    curr_teams_cat_num = 0;

                    break;
                case 1:
                    curr_iter = iter_U10;
                    time_to_prep = time_to_prepare_u10;
                    curr_teams_cat = new Team[20];
                    curr_teams_cat_num = 0;
                    break;
                case 2:
                    curr_iter = iter_U12;
                    time_to_prep = time_to_prepare_u12;
                    curr_teams_cat = new Team[20];
                    curr_teams_cat_num = 0;
                    break;
                default:
                    break;
            }

            //loading team name and coach name from excel
            while (curr_iter.hasNext()) {
                Row row = curr_iter.next();
                String team_name = row.getCell(0).getStringCellValue();
                String coach_name = row.getCell(1).getStringCellValue();
                curr_teams_cat[curr_teams_cat_num] = new Team(team_name, coach_name, time_to_prep);
                curr_teams_cat_num++;
            }

            //cutting from curr_teams unused cells
            Team[] teams_to_load = new Team[curr_teams_cat_num];
            System.arraycopy(curr_teams_cat, 0, teams_to_load, 0, curr_teams_cat_num);

            //we can now create category
            switch (i) {
                case 0:
                    categories[i] = new U8_cat(teams_to_load, match_t_u8);
                    break;
                case 1:
                    categories[i] = new U10_cat(teams_to_load, match_t_u10);
                    break;
                case 2:
                    categories[i] = new U12_cat(teams_to_load, match_t_u12);
                    break;
            }
        }

        //section that imports fields and travel time from field to field from excel file Fields.xlsx from folder import_data

        FileInputStream file_fields = new FileInputStream(new File("src\\import_data\\Fields.xlsx"));

        XSSFWorkbook excel_fields = new XSSFWorkbook(file_fields);

        XSSFSheet sheet_fields = excel_fields.getSheetAt(0);

        Iterator<Row> field_iter = sheet_fields.iterator(); //iterator throw sheet_fields

        int i_row = 0;
        int j_col = 0;

        while (field_iter.hasNext()) {
            Row row = field_iter.next();
            Iterator<Cell> cell_iterator = row.cellIterator(); //iterator through cells at current row
            j_col = 0;
            while (cell_iterator.hasNext()) {
                Cell cell = cell_iterator.next();

                if (i_row == 0 && j_col == 0) { //first cell of sheet is number of fields
                    num_fields = (int) cell.getNumericCellValue();
                    fields = new Field[num_fields];
                    travel_t_fields = new int[num_fields][num_fields];
                } else if (i_row == 0) { //first row of sheet are names of fields (with exception of 1st cell)
                    fields[j_col - 1] = new Field(cell.getStringCellValue());
                } else if (j_col != 0) { //from second row are travel times from field to field (with exception of 1st cell)
                    travel_t_fields[i_row - 1][j_col - 1] = (int) cell.getNumericCellValue();
                }
                j_col++;
            }
            i_row++;
        }

        //now we can create matches
        create_matches();

        //prints matches on fields chronologicaly
        for (Field field : fields) {
            print_matches(field);
        }

        //we export excel files of score and data of each match into folder export_data
        export_matches_score();
        export_matches_data();

        //after user takes file categories_matches_data.xlsx from folder export_data and puts it into folder import_data we loads score from it(if there is any), we print ranking and update files
        File f = new File("src\\import_data\\categories_matches_data.xlsx");
        if(f.exists() && !f.isDirectory()) { 
            import_score();
            export_matches_score();
            export_matches_data();
            export_ranking();
        }
        
    }
}
