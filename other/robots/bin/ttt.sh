for name in ls *
do if test -x $name
then combat 9 $1 $name
fi
done
