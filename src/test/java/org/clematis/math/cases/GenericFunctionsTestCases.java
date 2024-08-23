// Created: 05.12.2006 T 16:14:20
package org.clematis.math.cases;

/**
 * Tests cases with generic functions
 */
public class GenericFunctionsTestCases extends AlgorithmTestCases {
    public String[] getTests() {
        return tests;
    }

    private static final String[] tests = new String[]
        {
            /*
              // reference algorithm incorrectly calculates here!
              "$z=rand(2.8,3.6);" +
              "$z1=decimal(1, 10*($z-2.7));" +
              "$r=int($z1);" +
              "function: ptable($n)=switch($n, 0.0030, 0.0022, 0.0016, 0.0011, 0.0008, 0.0006, 0.0004, 0.0003, 0.0002, 0.0001, 0.0001);" +
              "$p=decimal(4, ptable($r)+($z1-$r)*(ptable($r+1)-ptable($r)));",

            "$v3=int(2+rint(5));" +
                "$v2=int(2*$v3);" +
                "$v1=int(3*$v3);" +
                "$m1=0.2+0.1*rint(5);" +
                "$m2=$m1+0.1+0.1*rint(3);" +
                "$m3=$m1;" +
                "function: v1($e, $v1, $v2, $m1, $m2)=($m1*$v1+$m2*$v2-$e*$m2*($v1-$v2))/($m1+$m2);" +
                "function: v2($v0, $e, $v1, $v2)=$v0+$e*($v1-$v2);" +
                "$v11=v1(0.85, $v1, $v2, $m1, $m2);" +
                "$v21=v2($v11, 0.85, $v1, $v2);" +
                "$v22=v1(0.6, $v21, $v3, $m2, $m3);" +
                "$v31=v2($v22, 0.6, $v21, $v3);" +
                /*
                 * Nest line causes exception, uncomment it to reproduce
                 * org.clematis.math.algorithm.AlgorithmException:
                 * Exception in $v12=switch(lt($v11, $v22), v1(0.85, $v1, $v11, $v22, $m1, $m2), "$v11"):
                 * Invalid call of generic function v1: the number of arguments does not match the number
                 * of formal arguments in line 13
                 */
                //"$v12=switch(lt($v11, $v22), v1("+/*0.85,*/" $v1, $v11, $v22, $m1, $m2), \"$v11\");" +
             //   "$v12=switch(lt($v11, $v22), v1(" +/*0.85,*/" $v1, $v11, $v22, $m1, $m2), $v11);" +
                //"$v23=switch(lt($v11, $v22), v2($v12, 0.85, $v11, $v22), \"$v22\");" +
            //    "$v23=switch(lt($v11, $v22), v2($v12, 0.85, $v11, $v22), $v22);" +
                //"$v24=switch(lt($v23, $v31), v1("+/*0.6,*/" $v2, $v23, $v31, $m2, $m3), \"$v23\");" +
             //   "$v24=switch(lt($v23, $v31), v1(" +/*0.6,*/" $v2, $v23, $v31, $m2, $m3), $v23);" +
                //"$v32=switch(lt($v23, $v31), v2($v24, 0.6, $v23, $v31), \"$v31\");" +
          /*      "$v32=switch(lt($v23, $v31), v2($v24, 0.6, $v23, $v31), $v31);" +
                "$b1=sig(3, $v32);",

            "$v = sum(i,1.0,10.0, \"i^2.0\" )",

            "function: fwn($a)=sig((if(gt($a, 1000), (if(gt($a, 10000), (if(gt($a, 100000), (if(gt($a, 1000000), (if(gt($a, 10000000), 8, 7)), 6)), 5)), 4)), 3)), $a);" +
                "$a=rint(11);" +
                "$b=int(switch($a, 130000, 140000, 150000, 160000, 170000, 180000, 190000, 200000, 210000, 220000, 230000));" +
                "$bd=fwn($b);" +
                "$c=int(switch($a, 37283, 40150, 43018, 45886, 48754, 51622, 54490, 57358, 60226, 63093, 65961));" +
                "$cd=fwn($c);",

            "$R=1000*rand(1, 5, 1);" +
                "$V=120;" +
                "$I=$V/$R;" +
                "condition: eq($I, decimal(3, $I));",

            "$r1=int(2+rint(8));" +
                "$r2=int(2+rint(8));" +
                "$r3=int(2+rint(8));" +
                "$r4=int(2+rint(8));" +
                "$r5=int(2+rint(8));" +
                "function: prl($r_1, $r_2)=$r_1*$r_2/($r_1+$r_2);" +
                "$r=sig(3, $r1+prl($r2, $r3+prl($r4, $r5)));",

            "function: F($x,$a)=-(abs($a-$x))^(1/3);" +
                "function: G($x,$a,$q)=($x-$a)*($x^2+$a*$x+$q);" +
                "$a  = int(1 + rint(5));" +
                "$q  = int(1 + rint(10));" +
                "$aq = int($a*$q);" +
                "$b  = int($q-$a^2);" +
                "$B  = int(abs($b));" +
                "$op1 = if(gt($b,0),\"+\", if(eq($b,0), \"\", \"-\"));" +
                "$x   = if(eq($b,0), \"\", \"x\");" +
                "$tB  = if(eq($b,0), \"\", if($B-1,\"$B\",\"\"));" +
                "$tp  = if($a-1,\"$a\",\"\");" +
                "$a0  = int($a - 10);" +
                "$a1  = int($a - 9);" +
                "$a2  = int($a - 8);" +
                "$a3  = int($a - 7);" +
                "$a4  = int($a - 6);" +
                "$a5  = int($a - 5);" +
                "$a6  = int($a - 4);" +
                "$a7  = int($a - 3);" +
                "$a8  = int($a - 2);" +
                "$a9  = int($a - 1);" +
                "$a10 = int($a);" +
                "$f0  = decimal(2,F($a0,$a));" +
                "$f1  = decimal(2,F($a1,$a));" +
                "$f2  = decimal(2,F($a2,$a));" +
                "$f3  = decimal(2,F($a3,$a));" +
                "$f4  = decimal(2,F($a4,$a));" +
                "$f5  = decimal(2,F($a5,$a));" +
                "$f6  = decimal(2,F($a6,$a));" +
                "$f7  = decimal(2,F($a7,$a));" +
                "$f8  = decimal(2,F($a8,$a));" +
                "$f9  = decimal(2,F($a9,$a));" +
                "$f10 = decimal(2,F($a10,$a));" +
                "$b5  = int($a + 5);" +
                "$b4  = int($a + 4);" +
                "$b3  = int($a + 3);" +
                "$b2  = int($a + 2);" +
                "$b1  = int($a + 1);" +
                "$g1  = decimal(2,G($b1,$a,$q));" +
                "$g2  = decimal(2,G($b2,$a,$q));" +
                "$g3  = decimal(2,G($b3,$a,$q));" +
                "$g4  = decimal(2,G($b4,$a,$q));" +
                "$g5  = decimal(2,G($b5,$a,$q))",*/

            "function: gggg($a) = switch($a, 0.10, 0.05, 0.025, 0.01, 0.005, 0.001);" +
                "function: tdist($a) = switch($a, 1.356, 1.782, 2.179, 2.681, 3.055, 3.930);" +
                "$n=14;" +
                "$xav=43; " +
                "$x2av=157.42;" +
                "$yav=572; " +
                "$y2av=23530; " +
                "$xy=1697.80;" +
                "$sxx=$x2av-($xav^2)/$n;" +
                "$sxy=$xy-$xav*$yav/$n;" +
                "$b1=$sxy/$sxx;" +
                "$b0=$yav/$n-$b1*$xav/$n;" +
                "$sst=$y2av-($yav^2)/$n;" +
                "$sse=$sst-$b1*$sxy;" +
                "$s2=$sse/($n-2);" +
                "$seb0=sqrt($s2*(1/$n+(($xav/$n)^2)/$sxx));" +
                "$seb1=sqrt($s2/$sxx);" +
                "$b10=0;" +
                "$t0b1=($b1-$b10)/$seb1;" +
                "$index1=1;" +
                "$index2=$index1+1;" +
                "$arg=gggg($index1);" +
                "$t=tdist($index2);" +
                "$a1=decimal(3,$s2);" +
                "$a2=decimal(3,$seb0);" +
                "$a3=decimal(3,$seb1);" +
                "$err=0.001;"
        };
}
