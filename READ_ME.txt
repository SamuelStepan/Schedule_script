Ve složce src jsou 3 složky, složka classes obsahuje všechny soubory s příponou .java. Dále src obsahuje složku import_data, která slouží
k načítání souboru do programu a expor_data, do které bude program exportovat výsledky. Pro import a export jsem zvolil excelové soubory
s příponou .xlsx. V složce import_data jsou 3 vzorové soubory jak zadat teamy pro konkrétní kategorie, teamy lze libovolně přepisovat, 
mazat či přidávat, ale je důležité dodržet daný styl (viz textový soubor READ_ME), dále je zde vzorový soubor na načítání informací ohledně
hřišť, jelikož jsem chtěl uvažovat program, který by zohlednil možnost jedné skupiny hrát na 2 hrištích zároveň tak soubor "Fields.xlsx" 
obsahuje něco jako matici/tabulku, která by měla reprezentovat čas přesunu z hriště x na hřiště y (předpokládám tedy, že se jedná o krátký
přesun, třeba 2 minuty a čas přesunu z x do y by měl být stejný jak čas přesunu z y do x), pokud chceme nastavit, že se nemůže hrát současně
na hřišti x a y, do matice na pozici [x,y] a [y,x] dáme -1, -1 musí být i na diagonále. Do složky expor_data se po prvním spuštění programu
vyexportují 2 excelové soubory, jeden s podrobnými informacemi o každém zápasu každé skupiny z každé kategorie ("categories_matches_data.xlsx")
a další kde budou tabulky výsledků zápasu pro každou skupinu z každé kategorie (jednotlivé kategorie jsou v různých excelovských listech),
tedy soubor "categories_matches_score.xlsx".  Pro vkládání výsledků zápasů zkopírujte soubor "categories_matches_data.xlsx" a vložte jej do
složky import_data (nebo lze pro testování mnou napsaných teamu a hřišť zkopírovat stejnojmenný soubor z import_data/testing_data, kde už
jsou napsané nějaké výsledky), potom můžete výsledky libovolných zápasů zapsat do tohoto souboru (pro konkrétní zápas mezi teamem x a teamem
y napište počet gólů teamu x do sloupce Score_T a teamu y do sloupce Score_O. Při opětovném spuštěním souboru se tyto výsledky načtou, v
export_data se aktualizují již vytvořené soubory a vyexportuje se další soubor "categories_ranking.xlsx", který obsahuje tabulky žebříčků
a bodů pro jednotlivé skupiny všech kategorii při současných výsledcích. 
Když otevřete projekt tak v mainovém souboru App.class je hned na začátku několik statických proměnných, které většinou představují volitelné
parametry turnaje. rest_t představuje nutný čas mezi dvěma zápasy pro každý team, potom proměnné pro každou kategorii time_to_prepare_uX,
představuje nutný čas navíc před začátkem zápasu na přípravu (nebo jakousi rezervu), a match_t_uX představuje délku zápasu pro danou kategorii,
posledně proměnné tourney_start_hours (záčátek turnaje v hodinách) a tourney_start_mins (začátek turnaje v minutách), pokud chceme začít
třeba v 9:30 tak tourney_starts_hours = 9 a tourney_start_mins = 30.
