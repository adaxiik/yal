# Yet Another Language

small interpreted language to try out kotlin and ANTLR

it supports:
- int, float, string and bool
- int to float type aliasing
- simple type checks
- some binary ops
- `if`, `while`
-  I/O

# Example

```
if (3<4) 
    write "condition was true";
else 
    write "condition was false";

if (true) {
    write "inside";
    write "second";
    write "if";
}

int a, b;

while(a < 10) {
 write "a=", a;
 a = a + 1;
}

a = 0;

read b;

while(a < b) {
 write "a=", a, ", b=", b;
 a = a + 1;
}
```

# run
```bash
$  ./gradlew run --args path/to/file.yal
```

# instruction set

| Instruction | Description                                                                                                                                           |
|:-----------:|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| add         | binary +                                                                                                                                              |
| sub         | binary -                                                                                                                                              |
| mul         | binary *                                                                                                                                              |
| div         | binary /                                                                                                                                              |
| mod         | binary %                                                                                                                                              |
| uminus      | unary -                                                                                                                                               |
| concat      | binary . - concatenation od strings                                                                                                                   |
| and         | binary &&                                                                                                                                             |
| or          | binary \|\|                                                                                                                                           |
| gt          | binary >                                                                                                                                              |
| lt          | binary <                                                                                                                                              |
| eq          | binary == - campares two values                                                                                                                       |
| not         | unary ! - negating boolean value                                                                                                                      |
| itof        | Instruction takes int value from the stack, converts it to float and returns it to stack.                                                             |
| push T x    | Instruction pushs the value x of type T. Where T represents I - int, F - float, S - string, B - bool. Example: push I 10, push B true, push S "A B C " |
| pop         | Instruction takes on value from the stack and discards it.                                                                                            |
| load id     | Instruction loads value of variable id on stack.                                                                                                      |
| save id     | Instruction takes value from the top of the stack and stores it into the variable with name id                                                        |
| label n     | Instruction marks the spot in source code with unique number n                                                                                        |
| jmp n       | Instruction jumps to the label defined by unique number n                                                                                             |
| fjmp n      | Instruction takes boolean value from the stack and if it is false, it will perform a jump to a label with unique number n                             |
| print n     | Instruction takes n values from stack and prints them on standard output                                                                              |
| read T      | Instruction reads value of type T (I - int, F - float, S - string, B - bool) from standard input and stores in on the stack                           |

full language specification can be found [here](http://behalek.cs.vsb.cz/wiki/index.php/PLC_Project)
