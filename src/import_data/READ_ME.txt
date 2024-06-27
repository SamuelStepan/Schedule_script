Here you can import teams for each category (first column must be name of team, second column must be name of coach) 
and import fields (it must be in a form of table, cell 0,0 must have number of teams, then first row (without first cell)
should have names of the fields(same for the first column) and then we have something like matrix where we enter
how long it takes to go from field i to field j, (if connection is impossible, enter -1), for example if we have on 3rd row 
field Brno_Bystrc_hriste_A and on 2nd column Brno_Bystr_hriste_B and it takes 2 minutes to travel from one field to another,
then we enter to cell 3,2 and 2,3 (because of symetry) number 2.