all : CalcDalton.h
CalcDalton.h : bin\CalcDalton.class
	javah -classpath bin; CalcDalton
clean :
	-del CalcDalton.h