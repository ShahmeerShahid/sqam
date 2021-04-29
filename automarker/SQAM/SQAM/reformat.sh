for directory in ./*/
do 
    echo "Reformating - $directory"
    cd $directory
    sed -i 's/-- Query 1 statements/-- START Query 1/g' a2.sql
    sed -i 's/-- Query 2 statements/-- END Query 1\n-- START Query 2/g' a2.sql
    sed -i 's/-- Query 3 statements/-- END Query 2\n-- START Query 3/g' a2.sql
    sed -i 's/-- Query 4 statements/-- END Query 3\n-- START Query 4/g' a2.sql
    sed -i 's/-- Query 5 statements/-- END Query 4\n-- START Query 5/g' a2.sql
    sed -i 's/-- Query 6 statements/-- END Query 5\n-- START Query 6/g' a2.sql
    sed -i 's/-- Query 7 statements/-- END Query 6\n-- START Query 7/g' a2.sql
    sed -i 's/-- Query 8 statements/-- END Query 7\n-- START Query 8/g' a2.sql
    sed -i 's/-- Query 9 statements/-- END Query 8\n-- START Query 9/g' a2.sql
    sed -i 's/-- Query 10 statements/-- END Query 9\n-- START Query 10/g' a2.sql
    echo '-- END Query 10' >> a2.sql
    cd ..
done