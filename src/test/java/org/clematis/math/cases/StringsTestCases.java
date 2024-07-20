// Created: 05.12.2006 T 15:52:16
package org.clematis.math.cases;

/**
 * Test cases that check up correct parsing of strings within math expressions.
 * There are mostly chemical formulas here.
 */
public class StringsTestCases extends AlgorithmTestCases {
    public String[] getTests() {
        return tests;
    }

    private static final String[] tests = new String[]
        {
            "$i=rint(2);" +
                "$n1=switch($i, \"frequency\", \"wavelength\");" +
                "$n2=switch($i, \"wavelength\", \"frequency\");" +
                "$j1=rint(7);" +
                "$j2=rint(4);" +
                "$waveu=switch($j1, \"&#197;\", \"nm\", \"?m\", \"mm\", \"cm\", \"m\", \"km\");" +
                "$wavef=switch($j1, 1e-10, 1e-9, 1e-6, 1e-3, 1e-2, 1, 1e3);" +
                "$frequ=switch($j2, \"Hz\", \"kHz\", \"MHz\", \"GHz\");" +
                "$freqf=switch($j2, 1, 1e3, 1e6, 1e9);" +
                "$num1=rand(1, 10, 3);" +
                "$u1=switch($i, \"$frequ\", \"$waveu\");" +
                "$u2=switch($i, \"$waveu\", \"$frequ\");" +
                "$fact1=switch($i, $freqf, $wavef);" +
                "$fact2=switch($i, $wavef, $freqf);" +
                "$ans=sig(4, 2.998e8/($num1*$fact1)/$fact2);",

            "$i=rint(10);" +
                "$sub1=switch($i, \"BaCO<sub>3</sub>\", \"H<sub>3</sub>PO<sub>4</sub>\", \"2H<sub>2</sub>S\", \"Fe<sub>2</sub>O<sub>3</sub>\", \"NH<sub>3</sub>\", \"SnO<sub>2</sub>\", \"KOH\", \"2NaBr\", \"IF<sub>5</sub>\", \"CO<sub>3</sub><sup>2-</sup>\");" +
                "$p1=switch($i,        2, 3, 0, 2, 0, 2, 2, 2, 0, 3);" +
                "$sub2=switch($i,    \"C\", \"3H<sub>2</sub>O\", \"3O<sub>2</sub>\", \"3CO\", \"H<sub>3</sub>O<sup>+</sup>\", \"H<sub>2</sub>\",  \"HCl\", \"Cl<sub>2</sub>\", \"H<sub>2</sub>\", \"H<sub>2</sub>O\");" +
                "$p2=switch($i,        2, 1, 0, 0, 3, 0, 0, 0, 1, 1);" +
                "$sub3=switch($i,  \"BaO\", \"PO<sub>4</sub><sup>3-</sup>\", \"2H<sub>2</sub>O\", \"2Fe\", \"NH<sub>4</sub><sup>+</sup>\", \"Sn\", \"KCl\", \"2NaCl\", \"HIO<sub>3</sub>\", \"HCO<sub>3</sub><sup>-</sup>\");" +
                "$p3=switch($i,        2, 3, 1, 2, 3, 2, 2, 2, 2, 3);" +
                "$sub4=switch($i,   \"CO\", \"3H<sub>3</sub>O<sup>+</sup>\", \"2SO<sub>2</sub>\", \"3CO<sub>2</sub>\", \"H<sub>2</sub>O\", \"2H<sub>2</sub>\", \"H<sub>2</sub>O\", \"Br<sub>2</sub>\", \"HCl\", \"OH<sup>-</sup>\");" +
                "$p4=switch($i,        0, 3, 0, 0, 1, 1, 1, 1, 0, 3);" +
                "$j=rint(3);" +
                "$sub=switch($j, switch($i, \"BaCO<sub>3</sub>\", \"H<sub>3</sub>PO<sub>4</sub>\", \"2H<sub>2</sub>S\", \"Fe<sub>2</sub>O<sub>3</sub>\", \"NH<sub>3</sub>\", \"SnO<sub>2</sub>\", \"KOH\", \"2NaBr\", \"IF<sub>5</sub>\", \"CO<sub>3</sub><sup>2-</sup>\"), switch($i,    \"C\", \"3H<sub>2</sub>O\", \"3O<sub>2</sub>\", \"3CO\", \"H<sub>3</sub>O<sup>+</sup>\", \"H<sub>2</sub>\",  \"HCl\", \"Cl<sub>2</sub>\", \"H<sub>2</sub>\", \"H<sub>2</sub>O\"), switch($i,  \"BaO\", \"PO<sub>4</sub><sup>3-</sup>\", \"2H<sub>2</sub>O\", \"2Fe\", \"NH<sub>4</sub><sup>+</sup>\", \"Sn\", \"KCl\", \"2NaCl\", \"HIO<sub>3</sub>\", \"HCO<sub>3</sub><sup>-</sup>\"), switch($i,   \"CO\", \"3H<sub>3</sub>O<sup>+</sup>\", \"2SO<sub>2</sub>\", \"3CO<sub>2</sub>\", \"H<sub>2</sub>O\", \"2H<sub>2</sub>\", \"H<sub>2</sub>O\", \"Br<sub>2</sub>\", \"HCl\", \"OH<sup>-</sup>\"));" +
                "$ph1=switch($p1, \"<sub>(<i>g</i>)</sub>\", \"<sub>(<i>l</i>)</sub>\", \"<sub>(<i>s</i>)\", \"<sub>(<i>aq</i>)</sub>\");" +
                "$ph2=switch($p2, \"<sub>(<i>g</i>)</sub>\", \"<sub>(<i>l</i>)</sub>\", \"<sub>(<i>s</i>)\", \"<sub>(<i>aq</i>)</sub>\");" +
                "$ph3=switch($p3, \"<sub>(<i>g</i>)</sub>\", \"<sub>(<i>l</i>)</sub>\", \"<sub>(<i>s</i>)\", \"<sub>(<i>aq</i>)</sub>\");" +
                "$ph4=switch($p4, \"<sub>(<i>g</i>)</sub>\", \"<sub>(<i>l</i>)</sub>\", \"<sub>(<i>s</i>)\", \"<sub>(<i>aq</i>)</sub>\");" +
                "$p1=rint(2);" +
                "$p2=rint(2);" +
                "$pcb=switch($j, $p1, $p2, $p3, $p4);" +
                "$pc=gt($pcb, 1)*($pcb-1)+not(gt($pcb, 1))*$pcb;" +
                "condition: ne($p1, $pc)*ne($p2, $pc)*ne($p1, $p2);" +
                "$name=switch($pc, \"Pressure\", \"Mole fraction\", \"Concentration\");" +
                "$name1=switch($p1, \"Pressure\", \"Mole fraction\", \"Concentration\");" +
                "$name2=switch($p2, \"Pressure\", \"Mole fraction\", \"Concentration\");" +
                "$unit=switch($pc, \"bar\", \"\", \"M\");" +
                "$unit1=switch($p1, \"bar\", \"\", \"M\");" +
                "$unit2=switch($p2, \"bar\", \"\", \"M\");",

            "$i=rint(10);" +
                "$compound=switch($i, \"PbF<sub>2</sub>\", \"BaF<sub>2</sub>\", \"Li<sub>3</sub>PO<sub>4</sub>\", \"Ce(IO<sub>3</sub>)<sub>3</sub>\", \"Ca<sub>3</sub>(PO<sub>4</sub>)<sub>2</sub>\", \"La<sub>2</sub>(C<sub>2</sub>O<sub>4</sub>)<sub>3</sub>\", \"Ag<sub>2</sub>CrO<sub>4</sub>\", \"MgCO<sub>3</sub>\", \"AuCl<sub>3</sub>\", \"BaSO<sub>4</sub>\");" +
                "$name=switch($i, \"lead (II) fluoride\", \"barium fluoride \", \"lithium phosphate\", \"cerium iodate\", \"calcium phosphate\", \"lanthanum oxalate\", \"silver chromate\", \"magnesium carbonate\", \"gold(III) chloride\", \"barium sulfate\");" +
                "$ion=switch($i, \"Pb<sup>2+</sup>\", \"Ba<sup>2+</sup>\", \"Li<sup>+</sup>\", \"Ce<sup>3+</sup>\", \"Ca<sup>2+</sup>\", \"La<sup>3+</sup>\", \"Ag<sup>+</sup>\", \"Mg<sup>2+</sup>\", \"Au<sup>3+</sup>\", \"Ba<sup>2+</sup>\");" +
                "$Ct=switch($i, \"2.0*10<sup>-3</sup>\", \"3.6*10<sup>-3</sup>\", \"9.9*10<sup>-3</sup>\", \"1.9*10<sup>-3</sup>\", \"3.4*10<sup>-7</sup>\", \"3.7*10<sup>-6</sup>\", \"3.0*10<sup>-5</sup>\", \"2.6*10<sup>-3</sup>\", \"3.2*10<sup>-7</sup>\", \"1.0*10<sup>-5</sup>\");" +
                "$C=switch($i, 2.0E-3, 3.6E-3, 9.9E-3, 1.9E-3, 3.4E-7, 3.7E-6, 3.0E-5, 2.6E-3, 3.2E-7, 1.0E-5);" +
                "$qcat=switch($i, 1, 1, 3, 1, 3, 2, 2, 1, 1, 1);" +
                "$qan=switch($i, 2, 2, 1, 3, 2, 3, 1, 1, 3, 1);" +
                "$answer=($qan/$qcat)^($qan)*$C^($qcat+$qan);",

            "$i=rint(10);" +
                "$vf=rint(3);" +
                "$substance=switch($i, \"Al<sub>2</sub>(SO<sub>4</sub>)<sub>3</sub>\", \"K<sub>2</sub>CO<sub>3</sub>\", \"RbOH\", \"Ca(H<sub>2</sub>PO<sub>4</sub>)<sub>2</sub>\", \"(NH<sub>4</sub>)<sub>2</sub>HPO<sub>4</sub>\", \"KF\", \"NaHSO<sub>4</sub>\", \"Ba(HCO<sub>3</sub>)<sub>2</sub>\", \"AlCl<sub>3</sub>\", \"NaNO<sub>2</sub>\");" +
                "$molmass=switch($i, 342.15, 138.21, 102.48, 234.05, 132.06, 58.10, 120.06, 259.37, 133.34, 69.00);" +
                "$pcat=switch($i, \"Al<sup>3+</sup>\", \"K<sup>+</sup>\", \"Rb<sup>+</sup>\", \"Ca<sup>2+</sup>\", \"NH<sub>4</sub><sup>+</sup>\", \"K<sup>+</sup>\", \"Na<sup>+</sup>\", \"Ba<sup>2+</sup>\", \"Al<sup>3+</sup>\",\"Na<sup>+</sup>\");" +
                "$pani=switch($i, \"SO<sub>4</sub><sup>2-</sup>\", \"CO<sub>3</sub><sup>2-</sup>\", \"OH<sup>-</sup>\", \"H<sub>2</sub>PO<sub>4</sub><sup>-</sup>\", \"HPO<sub>4</sub><sup>2-</sup>\", \"F<sup>-</sup>\", \"HSO<sub>4</sub><sup>-</sup>\", \"HCO<sub>3</sub><sup>-</sup>\", \"Cl<sup>-</sup>\", \"NO<sub>2</sub><sup>-</sup>\");" +
                "$volume=switch($vf, rand(1, 10, 3), rand(1, 10, 3), rand(1000, 10000, 3));" +
                "$volunits=switch($vf, \"mL\", \"L\", \"L\");" +
                "$units=switch($vf, \"mg\", \"g\", \"kg\");" +
                "$mass=rand(1,10,3);" +
                "$fact=switch($vf, 1, 1, 1000);" +
                "$indexcat=switch($i, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1);" +
                "$indexani=switch($i, 3, 1, 1, 2, 1, 1, 1, 2, 3, 1);" +
                "$mc=sig(3, $mass / $molmass * $fact * $indexcat / $volume);" +
                "$ma=sig (3, $mass / $molmass * $fact * $indexani / $volume);",

            "$i=rint(10);" +
                "$element=switch($i, \"P<sub>4</sub>\", \"S\", \"Na\", \"Fe\", \"Mg\", \"Al\", \"C\", \"K\", \"H<sub>2</sub>\", \"Ba\");" +
                "$oxide=switch($i, \"P<sub>4</sub>O<sub>10</sub>\", \"SO<sub>2</sub>\", \"Na<sub>2</sub>O<sub>2</sub>\", \"Fe<sub>3</sub>O<sub>4</sub>\", \"MgO\", \"Al<sub>2</sub>O<sub>3</sub>\", \"CO<sub>2</sub>\", \"KO<sub>2</sub>\", \"H<sub>2</sub>O\", \"BaO<sub>2</sub>\");" +
                "$name=switch($i, \"phosphorus\", \"sulfur\", \"sodium\", \"iron\", \"magnesium\", \"aluminum\", \"carbon\", \"potassium\", \"hydrogen\", \"barium\");" +
                "$m1=rand(1, 10, 3);" +
                "$m2=rand(1, 10, 3);" +
                "$mmm=switch($i, 123.90, 32.07, 22.99, 55.85, 24.31, 26.98, 12.01, 39.10, 2.02, 137.34);" +
                "$mx=switch($i, $mmm+10*16, $mmm+32, 2*$mmm+32, 3*$mmm+64, $mmm+16, 2*$mmm+48, $mmm+32, $mmm+32, $mmm+16, $mmm+32);" +
                "$il=switch($i, 1, 1, 2, 3, 2, 4, 1, 1, 2, 1); " +
                "$io=switch($i, 5, 1, 1, 2, 1, 3, 1, 1, 1, 1);" +
                "$ix=switch($i, 1, 1, 1, 1, 2, 2, 1, 1, 2, 1);" +
                "$massp=1*sig(4, gt($m1/$mmm/$il, $m2/32/$io)*($m2/32/$io*$ix*$mx)+lt($m1/$mmm/$il, $m2/32/$io)*($m1/$mmm/$il*$ix*$mx));" +
                "$exx=1*sig(4, gt($m1/$mmm/$il, $m2/32/$io)*(($m1/$mmm/$il-$m2/32/$io)*$il*$mmm)+lt($m1/$mmm/$il, $m2/32/$io)*($m2/32/$io-$m1/$mmm/$il)*$io*32);",

            "$i=rint(8);" +
                "$reac=switch($i," +
                "\"2ClO<sub>2</sub><sup>-</sup>(aq) + O<sub>2</sub>(g) = 2ClO<sub>3</sub><sup>-</sup>(aq)\"," +
                "\"4FeCl<sub>3</sub>(s) + 3O<sub>2</sub>(g) = 2Fe<sub>2</sub>O<sub>3</sub>(s) + 6Cl<sub>2</sub>(g)\"," +
                "\"3N<sub>2</sub>H<sub>4</sub>(l) + 4O<sub>3</sub>(g) = 6NO(g) + 6H<sub>2</sub>O(l)\"," +
                "\"H<sub>2</sub>(g) + O<sub>2</sub>(g) = H<sub>2</sub>O(l)\"," +
                "\"CaCO<sub>3</sub>(s) + H<sub>2</sub>SO<sub>4</sub>(l) = CaSO<sub>4</sub>(s) + H<sub>2</sub>O(g) + CO<sub>2</sub>(g)\"," +
                "\"NH<sub>3</sub>(g) + HCl(g) = NH<sub>4</sub>Cl(s)\"," +
                "\"CO(g) + H<sub>2</sub>O(g) = CO<sub>2</sub>(g) + H<sub>2</sub>(g)\"," +
                "\"Fe<sub>2</sub>O<sub>3</sub>(s) + 2Al(s) = Al<sub>2</sub>O<sub>3</sub>(s) + 2Fe(s)\"" +
                ");" +
                "$s=sig(4,switch($i,-83,328.7,365.3,-163.1,259,-284.6,-42.1,-38.5));",

            "$r  = rint(100);" +
                "$rf = int($r-3*int($r/3));" +
                "$rf1 = $rf+1-3*int(($rf+1)/3);" +
                "$rf2 = $rf+2-3*int(($rf+2)/3);" +
                "$quest1 = \"the <i>y</i>-axis\";" +
                "$quest2 = \"the origin\";" +
                "$quest3 = \"the <i>x</i>-axis\";" +
                "$quest  = switch($rf, \"$quest1\",\"$quest2\",\"$quest3\");" +
                "$x_fsgn= switch($rf,\"-\",\"-\",\"\");" +
                "$y_fsgn= switch($rf,\"\",\"-\",\"-\");" +
                "$A = int(6+rint(15));" +
                "$B = 0.5*(1+rint(4));" +
                "$c = rint(3);" +
                "$c1 = $c+1-3*int(($c+1)/3);" +
                "$c2 = $c+2-3*int(($c+2)/3);" +
                "$fun1  =  \"$A*x^3*e^(-1.5*x)\";" +
                "$fun1x = \"-$A*x^3*e^(-1.5*x)\";" +
                "$fun1y = \"$A*(-x)^3*e^(1.5*x)\";" +
                "$fun10 = \"-$A*(-x)^3*e^(1.5*x)\";" +
                "$fun2  = \"$B*x\";" +
                "$fun2x = \"-$B*x\";" +
                "$fun2y = \"-$B*x\";" +
                "$fun20 = \"$B*x\";" +
                "$fun3  = \"x^(0.5)\";" +
                "$fun3x = \"-x^(0.5)\";" +
                "$fun3y = \"(-x)^(0.5)\";" +
                "$fun30 = \"-(-x)^(0.5)\";" +
                "$fun  = switch($c, \"$fun1\",\"$fun2\",\"$fun3\");" +
                "$fun  = switch($c, \"$fun1\",\"$fun2\",\"$fun3\");" +
                "$f1   = switch($c1, \"$fun10\",\"$fun20\",\"$fun30\");" +
                "$f2   = switch($c2, \"$fun10\",\"$fun20\",\"$fun30\");" +
                "$sym  = switch($c, switch($rf,\"$fun1y\",\"$fun10\",\"$fun1x\"),switch($rf,\"$fun2y\",\"$fun20\",\"$fun2x\"),switch($rf,\"$fun3y\",\"$fun30\",\"$fun3x\"));" +
                "$sym1  = switch($c, switch($rf1,\"$fun1y\",\"$fun10\",\"$fun1x\"),switch($rf1,\"$fun2y\",\"$fun20\",\"$fun2x\"),switch($rf1,\"$fun3y\",\"$fun30\",\"$fun3x\"));" +
                "$sym2  = switch($c, switch($rf2,\"$fun1y\",\"$fun10\",\"$fun1x\"),switch($rf2,\"$fun2y\",\"$fun20\",\"$fun2x\"),switch($rf2,\"$fun3y\",\"$fun30\",\"$fun3x\"));" +
                "$interval  = switch($rf, \"-10,0\",\"-10,0\",\"0,10\");" +
                "$interval1  = switch($rf1, \"-10,0\",\"-10,0\",\"0,10\");" +
                "$interval2  = switch($rf2, \"-10,0\",\"-10,0\",\"0,10\");",

            "$i=rint(10);" +
                "$j=rint(2);" +
                "$num=rand(10,70,2);" +
                "$sentence=switch($i,\"Fungal laccase is an enzyme found in fungi that live on rotting wood. The enzyme is blue and contains\"," +
                "\"Polyethylene is a polymer used in production of packing material and molded articles. The polymer contains\"," +
                "\"Polypropylene is a polymer used in production of textile fibers and lightweight ropes. The polymer contains\"," +
                "\"Poly(vinyl chloride) is a polymer used in production of garden hoses and pipes. The polymer contains\"," +
                "\"Saran is a polymer used in production of food packaging and textile fibers. The polymer contains\"," +
                "\"Orlon is a polymer used in production of textile fibers. The polymer contains\"," +
                "\"Teflon is a polymer used in production of gaskets and valves. The polymer contains\"," +
                "\"Polystyrene is a polymer used in production of Styrofoam and molded articles. The polymer contains\"," +
                "\"Lucite is a polymer used in production of contact lenses and clear sheets for windows and optical uses. The polymer contains\"," +
                "\"Poly(ethyl acrylate) is a polymer used in production of latex paints. The polymer contains\");" +
                "$compound=switch($i,\"fungal laccase\",\"polyethylene\", \"polypropylene\", \"poly(vinyl chloride)\", \"Saran\", \"Orlon\", \"Teflon\", \"polystyrene\", \"Lucite\", \"poly(ethyl acrylate)\");" +
                "$pol=switch($i, \"enzyme\", \"polymer\", \"polymer\", \"polymer\", \"polymer\", \"polymer\", \"polymer\", \"polymer\", \"polymer\", \"polymer\");" +
                "$at=switch($j,\"carbon\",\"hydrogen\");" +
                "$at1=switch($j,\"copper\",\"copper\");" +
                "$at2=switch($j,\"carbon\",\"carbon\");" +
                "$perc=switch($i,0.400,switch($j,85.64,14.36),switch($j,85.63,14.37),switch($j,38.44,4.84),switch($j,24.78,2.08),switch($j,67.91,5.70),24.02,switch($j,92.26,7.74),switch($j,59.98,8.05),switch($j,59.98,8.05));" +
                "$atom=switch($i,\"$at1\",\"$at\",\"$at\",\"$at\",\"$at\",\"$at\",\"$at2\",\"$at\",\"$at\",\"$at\");" +
                "$mass=switch($i,63.55,switch($j,12.011,1.0079),switch($j,12.011,1.0079),switch($j,12.011,1.0079),switch($j,12.011,1.0079),switch($j,12.011,1.0079),12.011,switch($j,12.011,1.0079),switch($j,12.011,1.0079),switch($j,12.011,1.0079));" +
                "$ans=sig(3,$num*$perc/100*1000/($mass));",

            "$a=int(1+rint(5));" +
                "$rand=int($a-1);" +
                "$a1=int(switch($rand, 1, 0, 0, 1, 1));" +
                "$a2=int(switch($rand, 1, 0, 1, 0, 0));" +
                "$a3=int(switch($rand, 0, 1, 1, 1, 1));" +
                "$a4=int(switch($rand, 1, 0, 1, 0, 1));" +
                "$a5=int(switch($rand, 0, 1, 1, 1, 0));" +
                "$a6=int(switch($rand, 0, 1, 0, 0, 0));" +
                "$a7=int(switch($rand, 0, 0, 0, 1, 1));" +
                "$a8=int(switch($rand, 0, 1, 1, 1, 0));" +
                "$a9=int(switch($rand, 1, 0, 0, 0, 1));" +
                "$a10=int(switch($rand, 1, 1, 1, 1, 0));" +
                "$a11=int(switch($rand, 0, 0, 0, 0, 0));" +
                "$a12=int(switch($rand, 1, 1, 0, 0, 1));" +
                "$rand1=rint(5);" +
                "$a13=switch($rand1, \"qu/c11/ra07_a11_07_02a1.png\", \"qu/c11/ra07_a11_07_02a5.png\", \"qu/c11/ra07_a11_07_02a4.png\", \"qu/c11/ra07_a11_07_02a3.png\", \"qu/c11/ra07_a11_07_02a2.png\");" +
                "$a14=switch($rand1, \"qu/c11/ra07_a11_07_02a2.png\", \"qu/c11/ra07_a11_07_02a1.png\", \"qu/c11/ra07_a11_07_02a5.png\", \"qu/c11/ra07_a11_07_02a4.png\", \"qu/c11/ra07_a11_07_02a3.png\");" +
                "$a15=switch($rand1, \"qu/c11/ra07_a11_07_02a3.png\", \"qu/c11/ra07_a11_07_02a2.png\", \"qu/c11/ra07_a11_07_02a1.png\", \"qu/c11/ra07_a11_07_02a5.png\", \"qu/c11/ra07_a11_07_02a4.png\");" +
                "$a16=switch($rand1, \"qu/c11/ra07_a11_07_02a4.png\", \"qu/c11/ra07_a11_07_02a3.png\", \"qu/c11/ra07_a11_07_02a2.png\", \"qu/c11/ra07_a11_07_02a1.png\", \"qu/c11/ra07_a11_07_02a5.png\");" +
                "$a17=switch($rand1, \"qu/c11/ra07_a11_07_02a5.png\", \"qu/c11/ra07_a11_07_02a4.png\", \"qu/c11/ra07_a11_07_02a3.png\", \"qu/c11/ra07_a11_07_02a2.png\", \"qu/c11/ra07_a11_07_02a1.png\");" +
                "$ans1=int(switch($rand1, 1, 2, 3, 4, 5));" +
                "$ans2=int(switch($rand1, 2, 3, 4, 5, 1));" +
                "$ans3=int(switch($rand1, 3, 4, 5, 1, 2));" +
                "$ans4=int(switch($rand1, 4, 5, 1, 2, 3));" +
                "$ans5=int(switch($rand1, 5, 1, 2, 3, 4));" +
                "$ans=int(switch($rand, $ans1, $ans2, $ans3, $ans4, $ans5));",

            "$i=int(rand(0, 5));" +
                "$j=int(rand(0, 5));" +
                "$a=int($j+1);" +
                "$b=int(switch($i,90,-90,180,90,-90 ));" +
                "$f1=\"qu/c16/ra37_a16_p08-1r.jpg\";" +
                "$f1a=\"qu/c16/ra37_a16_p08-1ar.jpg\";" +
                "$f1b=\"qu/c16/ra37_a16_p08-1br.jpg\";" +
                "$f1c=\"qu/c16/ra37_a16_p08-1cr.jpg\";" +
                "$f2=\"qu/c16/ra37_a16_p08-2r.jpg\";" +
                "$f2a=\"qu/c16/ra37_a16_p08-2ar.jpg\";" +
                "$f3=\"qu/c16/ra37_a16_p08-3r.jpg\";" +
                "$f3a=\"qu/c16/ra37_a16_p08-3ar.jpg\";" +
                "$f=switch($i,\"$f1\",\"$f1\",\"$f1\",\"$f2\", \"$f3\");" +
                "$fa=switch($i,\"$f1a\",\"$f1b\",\"$f1c\",\"$f2a\", \"$f3a\");" +
                "$fw1=switch($i,\"$f3a\",\"$f1a\",\"$f1b\",\"$f1c\", \"$f2a\");" +
                "$fw2=switch($i,\"$f1b\",\"$f1c\",\"$f2a\",\"$f3a\", \"$f1a\");" +
                "$fw3=switch($i,\"$f1c\",\"$f2a\",\"$f3a\",\"$f1a\", \"$f1b\");" +
                "$fw4=switch($i,\"$f2a\",\"$f3a\",\"$f1a\",\"$f1b\", \"$f1c\");" +
                "$pic1=switch($j,\"$fa\",\"$fw1\",\"$fw2\",\"$fw3\", \"$fw4\");" +
                "$pic2=switch($j,\"$fw4\",\"$fa\",\"$fw1\",\"$fw2\", \"$fw3\");" +
                "$pic3=switch($j,\"$fw3\",\"$fw4\",\"$fa\",\"$fw1\", \"$fw2\");" +
                "$pic4=switch($j,\"$fw2\",\"$fw3\",\"$fw4\",\"$fa\", \"$fw1\");" +
                "$pic5=switch($j,\"$fw1\",\"$fw2\",\"$fw3\",\"$fw4\", \"$fa\");",

            "$i=rint(4);" +
                "$k=rint($i+2);" +
                "$picfj=switch($k,\"qu/c12/ra23_m1205061.gif\",\"qu/c12/ra23_m1205062.gif\",\"qu/c12/ra23_m1205063.gif\",\"qu/c12/ra23_m1205064.gif\",\"qu/c12/ra23_m1205065.gif\",\"qu/c12/ra23_m1205066.gif\");" +
                "$fold=switch($k, \"folds\", \"folds\", \"doesn't fold\", \"folds\", \"doesn't fold\", \"folds\");" +
                "$phrase=switch($i, " +
                "\"Imagine, that the face with <i>X</i> is the lower base of the cube. Then, the marked edges will be on the top face. <p align=center><img src='qu/c12/ra23_m1205061com.gif'/></p> Thus, number 5 will be opposite the <i>X</i>.\", " +
                "\"Imagine, that the face with <i>X</i> is the lower base of the cube. Then, the marked edges will be on the top face. <p align=center><img src='qu/c12/ra23_m1205062com.gif'/> </p> Thus, number 4 will be opposite the <i>X</i>.\", " +
                "\"Imagine, that the face with <i>X</i> is the lower base of the cube. Then, the marked edges will be on the top face. <p align=center><img src='qu/c12/ra23_m1205064com.gif'/> </p> Thus, number 1 will be opposite the <i>X</i>.\"," +
                "\"Imagine, that the face with <i>X</i> is the lower base of the cube. Then, the marked edges will be on the top face. <p align=center><img src='qu/c12/ra23_m1205066com.gif'/></p> Thus, number 2 will be opposite the <i>X</i>.\");" +
                "$ans=int(switch($i, 1, 2, 5, 3, 5, 4));",

            "$A=switch(rint(4),\"A\",\"B\",\"C\",\"F\",\"G\");" +
                "$B1=switch(rint(4),\"A\",\"B\",\"C\",\"F\",\"G\");" +
                "$B=if(eq($A,$B1),\"Q\", \"$B1\");",

            "$m=rand(-10.5, 0);" +
                "$ppm=(($m/1000)/10)*1e6;" +
                "$a1=gti($ppm,26);" +
                "$a2=lti($ppm,60);" +
                "$c=and($a1,$a2);" +
                "$answer=switch($c,\"no\",\"yes\");",

            "$i=rint(8);" +
                "$metal=switch($i, \"lithium\", \"lithium\", \"sodium\", \"sodium\", \"potassium\", \"potassium\", \"rubidium\", \"rubidium\");" +
                "$halogen=switch($i, \"fluorine\", \"chlorine\", \"fluorine\", \"chlorine\", \"fluorine\", \"chlorine\", \"fluorine\", \"chlorine\"); " +
                "$product=switch($i, \"lithium fluoride\", \"lithium chloride\", \"sodium fluoride\", \"sodium chloride\", \"potassium fluoride\", \"potassium chloride\", \"rubidium fluoride\", \"rubidium chloride\");" +
                "$elementm=switch($i, \"Li\", \"Li\", \"K\", \"K\", \"Na\", \"Na\", \"Rb\", \"Rb\");" +
                "$elementh=switch($i, \"F\", \"Cl\", \"F\", \"Cl\", \"F\", \"Cl\", \"F\", \"Cl\");" +
                "$EAm=switch($i, -59.6, -59.6, -52.9, -52.9, -48.4, -48.4, -46.9, -46.9);" +
                "$IE1m=switch($i, 520.2, 520.2, 495.5, 495.5, 418.8, 418.8, 403.0, 403.0);" +
                "$Hvapm=switch($i, 159.3, 159.3, 107.5, 107.5, 89.0, 89.0, 80.9, 80.9);" +
                "$BEm=switch($i, 110.2, 110.2, 73.6, 73.6, 54.6, 54.6, 48.9, 48.9);" +
                "$EAh=switch($i, -328, -348.5, -328, -348.5, -328, -348.5, -328, -348.5);" +
                "$IE1h=switch($i, 1681, 1251, 1681, 1251, 1681, 1251, 1681, 1251);" +
                "$Hvaph=switch($i, 79.4, 121.3, 79.4, 121.3, 79.4, 121.3, 79.4, 121.3);" +
                "$BEh=switch($i, 155, 240,  155, 240,  155, 240,  155, 240);" +
                "$LE=switch($i, -1036, -834, -910, -769, -808, -701, -774, -680);" +
                "$answer=sig(4, $Hvapm-2.48+$IE1m+$BEh/2+$EAh+$LE);",

            "$r  = rint(100);" +
                "$rf = int($r-3*int($r/3));" +
                "$rf1 = $rf+1-3*int(($rf+1)/3);" +
                "$rf2 = $rf+2-3*int(($rf+2)/3);" +
                "$quest1 = \"the <i>y</i>-axis\";" +
                "$quest2 = \"the origin\";" +
                "$quest3 = \"the <i>x</i>-axis\";" +
                "$quest  = switch($rf, \"$quest1\",\"$quest2\",\"$quest3\");" +
                "$x_fsgn= switch($rf,\"-\",\"-\",\"\");" +
                "$y_fsgn= switch($rf,\"\",\"-\",\"-\");" +
                "$A = int(6+rint(15));" +
                "$B = 0.5*(1+rint(4));" +
                "$c = rint(3);" +
                "$c1 = $c+1-3*int(($c+1)/3);" +
                "$c2 = $c+2-3*int(($c+2)/3);" +
                "$fun1  =  \"$A*x^3*e^(-1.5*x)\";" +
                "$fun1x = \"-$A*x^3*e^(-1.5*x)\";" +
                "$fun1y = \"$A*(-x)^3*e^(1.5*x)\";" +
                "$fun10 = \"-$A*(-x)^3*e^(1.5*x)\";" +
                "$fun2  = \"$B*x\";" +
                "$fun2x = \"-$B*x\";" +
                "$fun2y = \"-$B*x\";" +
                "$fun20 = \"$B*x\";" +
                "$fun3  = \"x^(0.5)\";" +
                "$fun3x = \"-x^(0.5)\";" +
                "$fun3y = \"(-x)^(0.5)\";" +
                "$fun30 = \"-(-x)^(0.5)\";" +
                "$fun  = switch($c, \"$fun1\",\"$fun2\",\"$fun3\");" +
                "$fun  = switch($c, \"$fun1\",\"$fun2\",\"$fun3\");" +
                "$f1   = switch($c1, \"$fun10\",\"$fun20\",\"$fun30\");" +
                "$f2   = switch($c2, \"$fun10\",\"$fun20\",\"$fun30\");" +
                "$sym  = switch($c, switch($rf,\"$fun1y\",\"$fun10\",\"$fun1x\"),switch($rf,\"$fun2y\",\"$fun20\",\"$fun2x\"),switch($rf,\"$fun3y\",\"$fun30\",\"$fun3x\"));" +
                "$sym1  = switch($c, switch($rf1,\"$fun1y\",\"$fun10\",\"$fun1x\"),switch($rf1,\"$fun2y\",\"$fun20\",\"$fun2x\"),switch($rf1,\"$fun3y\",\"$fun30\",\"$fun3x\"));" +
                "$sym2  = switch($c, switch($rf2,\"$fun1y\",\"$fun10\",\"$fun1x\"),switch($rf2,\"$fun2y\",\"$fun20\",\"$fun2x\"),switch($rf2,\"$fun3y\",\"$fun30\",\"$fun3x\"));" +
                "$interval  = switch($rf, \"-10,0\",\"-10,0\",\"0,10\");" +
                "$interval1  = switch($rf1, \"-10,0\",\"-10,0\",\"0,10\");" +
                "$interval2  = switch($rf2, \"-10,0\",\"-10,0\",\"0,10\");",
        };
}
