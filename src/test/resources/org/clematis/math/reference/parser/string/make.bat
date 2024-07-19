call jjtree stringmath.jjt
call javacc stringmath.jj
call javac -cp .; *.java
cd ..\..\..\..\..\..\..\..
call java -cp .; org.clematis.math.parsers.string.StringMathParser
cd org\clematis\math\parser\string