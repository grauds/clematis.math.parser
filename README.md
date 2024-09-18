# Clematis Math Parser

[![Gradle Package](https://github.com/grauds/clematis.math.parser/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/grauds/clematis.math.parser/actions/workflows/gradle-publish.yml)

This library can parse and calculate mathematical expressions standalone and organized in algorithm sequences.

## Quick Start

Checkout the code and run the build:

```
git 'https://github.com/grauds/clematis.math.parser.git'
cd clematis.math.parser
chmod +x ./gradlew
./gradlew build
```

Create a reader and start parsing:

```java
AlgorithmReader reader = new AlgorithmReader();
// optionally use JavaCC based parser
reader.setParameterFactory(new AdvancedParameterFactory());

Algorithm algorithm = reader.createAlgorithm(
        """
           $r=int(1+rint(5));
           $l=int(2+rint(5));
           $c0=4*$l*$r^2;
           $c=int(switch(rint(3), $c0-1-rint($c0-1), $c0, $c0+1+rint($c0)));
           $b1=$c/$r;
           $c1=$l*$c;
           $d=$b1^2-4*$c1;
           $s=int(not(gt($d, 0))+lt($d, 0)+1)"""

);
algorithm.calculateParameters();
    
```

## Algorithms

Algorithm is a collection of mathematical equations with a parameter in it's left part and a mathematical expression in it's right part. Each line must end with a semicolon, like below:

```
$x = sin(rand(8));
```
In this example parameter ```$x``` is a product of ```sin``` funtion which takes a random argument generated with ```rand``` function. Parameters can also be spelled with curly braces:
```
${x} = sin(rand(8));
```
### Parameters

Each line of algorithm declares only one parameter. All parameters declared on the previous lines are visible to the following lines:
```
${x} = sin(rand(8));
${y} = ${x} + 89;
```
Parameter names can contain digits and underscores but must always start with a letter after a dollar sign and optional curly braces:
```
${my_param_1} = $r1 * $random_4;
```
Greek letters can also be used but only with advanced parser, for instance:
```
$\\alpha = sin(rand(9));
```


### Operations

All basic mathematical operations are supported in algorithms with their normal priorities: ``` +, -, *, /, ^```. Expressions can be grouped with any type of braces: ``` [], {}, ()```.

### Numbers

Formatting of decimal numbers can be done with the help of decimal places and significant digits formatters. These functions, along with simple rounding can be used in any place of an algorithm to control precision of results and avoid machine induced digital trails like 19.345555...

| Signature | Definition |
| -------- | -------- |
| cntSig(str)     |    Returns the number of significant digits of it's string argument   |
| Decimal(n, x) | Rounds decimal number to ```n``` decimal places | 
| Lsu(n, x) | Returns the least significant unit of x in the n'th place |
| Sig(n, x) | Returns x expressed as a floating point number to n significant digits |

### Randomization Functions

One of the main functions of algorithms is to accept some initial set of random values and to display the results. This allows debugging of algorithmic black box and there is a need to determine the safe and reasonable ranges for the initial values. To control this, randomization functions were introduced:

| Signature | Definition |
| -------- | -------- |
| Rand(m, n) | Returns a randomly chosen real number between ```m``` and ```n``` (inclusive). |
| rInt(n) | Returns a random integer between 0 and ```n```|
| rList(a, b, c...) | Chooses items from the argument list randomly |


### Conditions

Some parameters can serve as conditions, with ability to restart the calculation of the whole algorithm with a new set of randomized values if the condition is not met. The example of such line is below:

```
condition: eq($I, decimal(3, $I));
```

There is no need to name the parameter as it never supposed to be referenced from somewhere. Also, there is no need to store its value for the same reason. Condition value is discarded as soon as it is compared to boolean truth.

### Algebraic Functions

There is a couple of algebraic functions included for type conversion and summing up descreet functions' values:

| Signature | Definition |
| -------- | -------- |
| Int(x) | Returns an integer instead of a double by casting the machine types. |
| sum(i, start, stop, expr) | Returns the sum of the expression ```expr``` as the variable ```i``` runs through the ```start``` to ```stop``` (inclusive).

For example, the function can count the sum of all values of parabola:

```
$s = sum(i, 1, $n, "i^2");
```
Note, that varible ```i``` must not have a dollar sign, it is a declaration of internal counter variable, not of a parameter.


### Transcendental Functions

Algorithms support exponent, logarithms, square roots and some other non algebraic functions:

| Name | Definition |
| -------- | -------- |
| abs     |   \|x\|   |
| exp     |   e^(x)   |
| ln     |   ln(x)   |
| log     |   log(x)   |
| sqrt     |   x^(1/2)   |


### Trigonometric Functions

There are none trigonometric functions supported in the library along with their reverse functions:

| Name | Definition |
| -------- | -------- |
| sin     |   sin(x)   |
| cos     |   cos(x)   |
| tan     |   tg(x)   |
| cot     |   ctg(x)   |
| sec     |   1/cos(x)   |
| csc     |   1/sin(x)   |
| arcsin     |   arcsin(x)   |
| arccos     |   arccos(x)   |
| arctan     |   arctg(x)   |


### Hyperbolic Functions

Hyperbolic functions are analogues of the ordinary trigonometric functions, but defined using the hyperbola rather than the circle. The reverse hyperbolic functions are also supported:


| Name | Definition |
| -------- | -------- |
| sinh     |   sinh(x) = ( e^x - e^(-x) ) / 2   |
| cosh     |   cosh(x) = ( e^x + e^(-x) ) / 2   |
| tanh     |   tanh(x) = sinh(x) / cosh(x)   |
| arcsinh     |   arcsinh(x)   |
| arccosh     |   arccosh(x)  |
| arctanh     |   arctanh(x)   |

Note that cotangent and its reverse function are not supported in algorithms, use tangent instead:

```
$a = 1 / tanh(5);
```

### Logic Functions

There is a good set of logical operators supported in the algorithms. This makes easier to organise conditional switches and introduce different scenatios of calculation depending on the previous results:

| Signature | Definition |
| -------- | -------- |
| and(A, B...) | A & B ? 1.0 : 0.0 |
|  eq(A, B) | A == B ? 1.0 : 0.0 |
| gt(A, B) | A > B ? 1.0 : 0.0 |
| gti(A, B) | A >= B ? 1.0 : 0.0 |
| lt(A, B) | A < B ? 1.0 : 0.0 |
| lti(A, B) | A <= B ? 1.0 : 0.0 |
| ne(A, B) | A != B ? 1.0 : 0.0 |
| not(A) | A == 0.0 ? 1.0 : 0.0 |
| or(A, B, ...) |  A \|\| B ? 1.0 : 0.0 |

### Selector Functions

As logic operators, selectors help to conditionally select values for the parameters:

| Signature | Definition |
| -------- | -------- |
| If(A, B, C) | ( A != 0) ? B : C |
| Switch(A, B, C...) | Returns either B or C or any other argument by number in the first argument. Note, that the number should not exceed the number of parameters and start with 1. |

For instance, it can be used in the randomization of prime numbers like below:
```
$prime = switch(rint(5), 2, 3, 5, 7, 11);
```

### Gauss Error Function

This is an example of special purpose function which uses Apache Commons Math library to calculate the value. There are more functions like this available in backing library so the set can be expanded by adding wrapper functions.

| Signature | Definition |
| -------- | -------- |
| erf(x) | erfc(x) = 2/&radic;&pi; <sub>x</sub>&int;<sup>&infin;</sup> e<sup>-t<sup>2</sup></sup>dt |

### Generic Functions

Another way to expand function set is to add generic functions. This is done in the algorithm itself and visible to all line following the line there the function is declared. For example:

```!
function: myFunc($e, $v1, $v2, $m1, $m2)=($m1*$v1+$m2*$v2-$e*$m2*($v1-$v2))/($m1+$m2);
```

The line has to start with ```function:``` word to begin the declaration. Then the keyword is followed by the function signature. The parameters in the signature are local to the function and are not visible outside of it. Also, function cannot see other parameters from the algorithm, i.e. all the parameters have to be declared in the signature. The rest is the same as with the rest of the code, function sees all the library functions and can work with the same operators.

