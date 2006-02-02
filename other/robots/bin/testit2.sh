echo EVAL COMBAT TRACE $1 vs $2
combat2 trace $1 $2 >log
evallog.sh log
