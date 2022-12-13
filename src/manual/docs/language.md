# Language

Learning bds language (`bds`) is almost trivial, all the statements and expression and data types do what you expect.

bds is really simple and you should be able to code within a few minutes. This section is intended as a reference, so just glance through it.

### Comments

The usual statements are available

```
// Single line comment

# Another single line comment

/*
   Multi-line comment
*/
```

### Statements

Statements can be terminated either by semicolon or by a new line.

```
# Two statements
print "Hi\n"; print "Bye\n";

# Two statements, same as before but using lines instead of semicolon
print "Hi\n" 
print "Bye\n"
```

### `break`

Breaks from current loop

```
for( int i=0 ; i < 10 ; i++ ) {
    if( i == 5 ) break;	   // Finish when we reach 5
}
```

### `breakpoint`

Inserts a debugging breakpoint. I.e. when the statement is executed, `bds` switches execution to debug mode (STEP)

```
breakpoint "Program execution will switch do debug mode here!\n"
```

### `continue`

Continue at the end of the current loop

```
for( int i=0 ; i < 10 ; i++ ) {
    if( i == 5 ) continue;	// Skip value 5
}
```

### `debug`

Show a debug message on STDERR only if `bds` is running in 'debug' mode (otherwise the statement is ignored).

```
debug "Show this message only if we are in debug mode!\n"
```

### `dep`

Define a task dependency in a declarative manner.
[See details in the "goal" section](goal.md)

### `error`

Show an error message and exit the program

```
if( num <= 0 )	error "Number MUST be positive\n"
```

### `exit`

Exit program, with an `exitValue` that is returned to the operating system, as the command line's exit code

```
exit 1
```

How exit value is calculated:

- If the program uses `exit value`, the value is used
- The exit code will be non-zero if any of the following happens:
    - There is a fatal error
    - There is an (un-caught) exception
    - A `task` fails, is killed, or otherwise finishes un-successfully (unless 'canFail' is set to true in the task)

### `for`

Similar to C or Java `for` loops

```
for( int i=0 ; i < 10 ; i++ ) print("$i\n")
```

or

```
for( int i=0 ; i < 10 ; i++ ) {
    print("$i\n")
}
```

### `for (lists)`

Java-like for iterator on lists

```
string[] mylist

// ... some code to populate the list

for( string s : mylist ) print("$s\n")
```

### `goal`

Define a task DAG objective in a declarative maner.
[See details in the "goal" section](goal.md)

### `if / else`

It does exactly what you expect

```
if( i < 10 )	print("Less than ten\n")
```

					or

```
if( i < 10 ) {
    print("Less than ten\n")
} else if( i <= 20 ) {
    print("Between ten and twenty\n")
} else {
    print("More than twenty\n")
}
```

### `include`

Include source code from another file

```
include "mymodule"

// ... use functions from 'mymodule.bds'
```

### `kill`

Kill a task

```
kill taskId
```

### `par`

Execute a bds thread in parallel.
[See details in the "par" section](par.md)

### `print / println`

Print to `STDOUT`

```
print "Show this mesage without a new line at the end."
println "This one gets a new line at the end."
```

### `return`

Return from a function. Optional expression is a return value.

```
// Define a function
int twice(int n) {
	return( 2 * n )
}
```

### `sys`

Execute a command line. [See details in the "sys" section](sys.md)

### `switch`

Switch statements are similar to multiple `if / else if` statements

```
in := 'x'
out := 1

switch( in ) {
    case 'a': 
        out *= 3
        break

    case 'z'+'x':   # Note that the 'case' expressions are evaluated at run time (you can even call functions here)
        out *= 5    # Note that this falls through to "case 'b'"

    case 'b':
        out *= 7
        break

    default:        # You can define 'default' anywhere (no need to do it after 'case')
        out *= 100
}
```

### `task`

A `task` expression defines a task to be executed.

See details in the following sections:

- [Task](task.md)
- [Task detached](task_detached.md)
- [Task improper](task_imp.md)
- [Task cloud AWS](task_aws.md)
- [Task resources](task_resources.md)

### Ternary operator `cond ? exprTrue : exprFalse`

The ternary oprator is ` cond ? exprTrue : exprFalse`

This operator evaluates `cond`:

- If `cond` is `true` then it evaluates and returns `exprTrue`.
- If `cond` is `false, it evalautes and return `exprFalse`.

For example, here we calculate the "sign" of number `x`

```
sign = ( x >= 0 ? 1 : -1 )
```

### `throw`

Throws an exception, that might be captured by a `try` / `catch` block:

```
class MyException extends Exception { }

void myFunction() {
    if( rand() < 0.5) throw new MyException()
}
```

Any object can be thrown, it does not have to be a subtype of `Exception`, but it is better if it is.

For example, you can throw a `string`:

```
throw "This will be wrapped into an exception"
```

### `try` / `catch` / `finally`

The `try` / `catch` / `finally` combination is used to to capture exceptions. For example:

```
class MyException extends Exception { }

// Half of the time this function fails and thorws an exception
void myFunction() {
    if( rand() < 0.5) throw new MyException()
}

try {
    // This code block might throw an exception
    myFunction() 
} catch(MyException e) {
    // This block is executes only if the `try` block
    // throws an exception of type `MyException`
    println "MyException was thrown: $e"
} finally {
    // This code is ALWAYS executed
    println "Finally, we are finished"
} 
```

### Variable assignment

` var = expr ` evaluates expression 'expr' and assign result to 'var'

```
i = j + 1
s = "Hello " + world
```

### Variable assignment (multiple)

` ( var1, var2, ..., varN ) = expr ` evaluates expression 'expr' (which must return a list) and assign results to 'var1', 'var2', etc. If the list size is less than the number of variables, variables are assigned default values (e.g. '0' for int). If the list has more values, they are ignored.

```
(name, value) = line.split('\t')
```

### Variable declarations

Declare variable 'var' as type 'type'

```
int i      # 'i' is an 64 bit int variable
real r     # 'r' is a double-precision floating-point number
string s   # 's' is a string
```

`type varName = expr` declares variable 'var' as type 'type', evaluate expression and assign result to initialize 'var'.

```
int i = 42
real r = 3.1415927
string s = "Hello!"
```

` varName := expr ` declares variable 'var', use type inference, evaluate expression 'expr' and assign result to initialize 'var'

```
i := 42
r := 3.1415927
s := "Hello!"
```

### `wait`

Wait for a task, a list of tasks, or all tasks to finish exxecution.
[See details in the "wait" section](wait.md)

### `warning`

Show a warning message

```
if( num <= 0 )	warning "Number should be positive\n"
```

### `while`

Typical `while` iterator

```
while( i < 10 ) i++
```

## Function definition

Here is a simple (and useless), function definition example:

```
// Define a function
int sumPositive(int n) {
    if( n <= 0 )	return 0

    int sum = 0
    for( int i=0 ; i <= n ; i++ ) sum = sum + i
    return sum
}

// Function definition in one line
int twice(int n)    return( 2 * n )

// Main
n := 5
print("The sum is : " + sumPositive( twice(n) ) + "\n" )
```

Obviously, if you run it

```
$ bds z.bds 
The sum is : 55
```

