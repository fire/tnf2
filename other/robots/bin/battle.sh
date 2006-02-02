echo battle $1 vs $2
combat $3 $1 $2>delme
sort -nk2 delme>delme2
if diff delme delme2>delme3;then echo $2 wins;else echo $1 wins;fi
diff delme delme2>delme3
rm -f delme delme2 delme3
