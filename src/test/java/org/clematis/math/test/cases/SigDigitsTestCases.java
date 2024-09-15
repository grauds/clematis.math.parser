// Created: 05.12.2006 T 15:51:51
package org.clematis.math.test.cases;

/**
 * Test cases that check up significant digits correct handling
 */
public class SigDigitsTestCases extends AlgorithmTestCases {

    private static final String[] TESTS = new String[] {
        "$x=rint(3)*5+5;"
            + "$answer=sig(2,exp(($x+2.0938)/(-8.5894)))",

        "$a=cntsig(0);"
            + "$b=cntsig(-101.02);"
            + "$b=cntsig(101.02);",

        "$a=rand(0, 100000);"
            + "$b=rint(7);"
            + "$c=decimal($b, $a);"
            + "$ans=cntsig($c);",

        "$dt0 = int(rand(2,8));"
            + "$dt = $dt0/2;"
            + "$ans = sig(3,5/($dt));",

        "$w0=int(rand (60,80));"
            + "$w=$w0*10;"
            + "$wb0=int(rand (90,110));"
            + "$wb=$wb0*10;"
            + "$ans2=($w+$wb)*15.2;"
            + "$ans=sig(3,($w+$wb)*15.2);",

        "$l=rand(3.0,3.5,2); $l2=5/$l;"
            + "$ans1=sig(4,(e^(-5/$l)*e^(-5/$l)));"
            + "$ans2=sig(4,(e^(-10/$l)*e^(-10/$l)));"
            + "$p2=(e^(-$l2)*($l2)^2)/2;"
            + "$ans3=sig(4,$p2*$p2);",

        "$x1=rand(100,500,6);"
            + "$x2=rand(-99,100);"
            + "$x3=rint(4);"
            + "$x4=decimal($x3,$x2);"
            + "$plus=lt($x2,0);"
            + "$sign=switch($plus,\"+\",\" \");"
            + "$decimalx1=3;"
            + "$decimalx4=$x3;"
            + "$decimal=switch(lt(3, $x3), $x3, 3);"
            + "$sum=$x1+$x4;"
            + "$answer=cntsig(decimal($decimal,$sum))",

        "$Vm = rand(10, 25, 2);"
            + "$Zval = rand(40, 90, 2);"
            + "$Zdeg = rand(30, 50, 2);"
            + "function: F($x,$y)=$x + $y;"
            + "$x=F(3,5);"
            + "$Zrad = pi * $Zdeg / 180;"
            + "$Im = sig(3, $Vm / ($Zval * sqrt(3)));"
            + "$Ib = sig(3,$Zdeg - 120);"
            + "$Ic = sig(3,$Zdeg + 120);"
            + "$P = sig(3, ($Vm)^2*cos($Zrad)/$Zval);"
            + "$Iaans=sig(3,-$Zdeg);",

        "$x1=rand(1,100);"
            + "$x2=decimal(0,rand(1,3));"
            + "$x3=decimal($x2,$x1);"
            + "$x4=rand(10,100);"
            + "$x5=decimal(0,(rand(2,5)));"
            + "$x6=decimal($x5,$x4);"
            + "$x3sig=cntsig($x3);"
            + "$x6sig=cntsig($x6);"
            + "$minsig=switch(lt($x3sig, $x6sig), $x6sig, $x3sig);"
            + "$answer=sig($minsig, $x3*$x6);",

        "$SG=rand(0.50,1.50,2);"
            + "$pi=3.141592654;"
            + "$V2=sig(5,(sqrt(2.0*32.2*((2.5/$SG)-1.0)*0.8)));"
            + "$ans=sig(3,(20.0*$V2/144.0));"
            + "$error=sig(3,($ans/200.0))",

        "$v1=rand(30,45,2);"
            + "$v2=rand(55,80,2);"
            + "$t1=1/$v1;"
            + "$t2=1/$v2;"
            + "$ans=sig(3,2/($t1+$t2));"
            + "$eans=lsu(2,$ans);",

        "$i=.02;"
            + "$c=1;"
            + "$n=int(100 + rint(10)*100);"
            + "$fv=int(1000 + rint(10)*100);"
            + "$bp=sig(6,$n*$fv);"
            + "$one=sig(4, ($bp*.06*6/12));"
            + "$num1=1-(1+$i)^(-24);"
            + "$pv1=$c * ($num1/$i);"
            + "$factor1=$pv1;"
            + "$num2=1-(1+$i)^(-36);"
            + "$pv2=$c * ($num2/$i);"
            + "$factor2=$pv2;"
            + "$three=$factor1 + (6/12) * ($factor2-$factor1);"
            + "$fv3=1;"
            + "$pv3=$fv3 * (1 + $i)^(-24);"
            + "$factor3=$pv3;"
            + "$fv4=1;"
            + "$pv4=$fv3 * (1 + $i)^(-36);"
            + "$factor4=$pv4;"
            + "$four=$factor3 - (6/12) * ($factor3-$factor4);"
            + "$ip=sig(6, ( ($one*$three) + ($bp*$four) ) );"
            + "$prem=sig(5, $ip-$bp);"
            + "$ie1=sig(4, $ip * .04 * 6/12);"
            + "$p1=int($one - $ie1);"
            + "$ec=$ip + $ie1 - $one;"
            + "$ie2=sig(4, $ec*.04*6/12);"
            + "$p2=int($one-$ie2);"
            + "$ieyear=sig(5,$ie1+$ie2);",

        "$t1=rand(21,22,5);"
            + "$t2=sig(5,($t1+rand(2,7,5)));"
            + "$mass=rand(0.1,0.9,4);"
            + "$ans=sig(4,-1981.0000);"
            + "$tol=lsu(4,$ans);"
            + "$hc=sig(4,(-$ans*(1E+3)*($mass/480.9)/($t2-$t1)));",

        "$static=0;"
            + "$m=sig(2, if($static, 5, rand(3.5, 6.5, 2)));"
            + "$v=sig(2, if($static, 5, rand(3.5, 6.5, 2)));"
            + "$angdeg=sig(2, if($static, 30, rand(20, 40, 2)));"
            + "$mu=sig(2, if($static, .4, rand(.25, .5, 2)));"
            + "$ang = (pi/180)*$angdeg;"
            + "$d1=sig(2, $v*$v/(19.6*sin($ang)));"
            + "$d2t=$v*$v/(19.6*($mu*cos($ang)+sin($ang)));"
            + "$d2=sig(2, $d2t);"
            + "$Etht=9.8*$mu*$m*$d2t*cos($ang);"
            + "$Eth=sig(2, $Etht);"
            + "$vb=sig(2, sqrt(abs(19.6*$d2t*sin($ang)-(2*$Etht/$m))));"
            + "$errw1 = lsu(2,$d1 );"
            + "$errw2 = lsu(2,$d2 );"
            + "$errw3 = lsu(2,$Eth );"
            + "$errw4 = lsu(2,$vb )"
    };

    public String[] getTests() {
        return TESTS;
    }
}
