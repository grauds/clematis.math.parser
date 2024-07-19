// Created: 05.12.2006 T 16:23:01
package org.clematis.math.cases;

/**
 * Test cases of just plain algorithms
 */
public class PlainTestCases extends AlgorithmTestCases
{
    public String[] getTests()
    {
        return tests;
    }

    private static String[] tests = new String[]
    {
        "$n=(35+rint(20));"+
        "$n=$n + 1;"+
        "$n=rint(20);"+
        "$ans=$n/720;",

        "$d2t=0.793333514913;" +
        "$ang=0.366519142919;" +
        "$Etht=15.024636047524;" +
        "$m=4.5;"+
        // square root of negative base, comment abs to reproduce
        "$a=sig(2, sqrt(abs(19.6*$d2t*sin($ang)-(2*$Etht/$m))))",
            
        "$n=35+rint(20);" +
        "$n1=$n-1;" +
        "$n2=$n-2;" +
        "$n3=$n-3;" +
        "$n4=$n-4;" +
        "$n5=$n-5;" +
        "$n6=$n-6;" +
        "$ans=($n*$n1*$n2*$n3*$n4*$n5)/720;",

        "$x1=rand(45,55,3);" +
        "$x2=rand(1,2,3);" +
        "$n=(6*$x2^2)/(4*pi*($x1*10^(-6))^2);" +
        "$m=2600*4*pi/3*($x1*10^(-6))^3;" +
        "$a=1000000*((6*$x2^2)/(4*pi*($x1*10^(-6))^2))*(2600*4*pi/3*($x1*10^(-6))^3);" +
        "$ea=lsu(2,$a);",
            
        "$cra=0.1;" +
        "$ca=0.4;" +
        "$cr=$cra+$ca;" +
        "$P=80;" +
        "$xcr=int(rand(10, $P-10));" +
        "$xca=int($P-$xcr);" +
        "$cap=int($cr*$P);" +
        "$B=-$cra*$xca+$cap;" +
        "$bcap=-$B+$cr*$P;",

        "$static=0;" +
        "$W1=sig(2, if($static, 600, rand(500, 700, 2)));" +
        "$R=sig(2, if($static, 20, rand(15, 25, 2)));" +
        "$angdeg=sig(2, if($static, 20, rand(15, 30, 2)));" +
        "$ang=(pi/180)*$angdeg; $c=rand(.15, .3, 2);" +
        "$vB=sig(2, if($static, 8, sqrt(9.8*$c*$R+19.6*$R*(1-cos($ang)))));" +
        "$KB=.5*$vB*$vB;" +
        "$KA1=$KB-9.8*$R*(1-cos($ang));" +
        "$vA1=sig(2, sqrt(2*$KA1));" +
        "$KB2=9.8*$R*(1-cos($ang));" +
        "$vB2=sig(2, sqrt(2*$KB2));" +
        "$W2=sig(2, if($static, 700, rand(1.3*$W1, 1.6*$W1, 2)));" +
        "$errw1 = lsu(2,$vA1 );" +
        "$errw2 = lsu(2,$vB2 )"
    };
}
