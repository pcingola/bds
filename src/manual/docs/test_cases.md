# Test cases

Because nobody writes perfect code...

## Test cases

`bds` provides a simple testing functionality.
Simply use the `-t` command line option and `bds` will run all functions `test*()`

A test is defined as a function that:

1. the name starts with `test`
2. has no parameters
3. returns `void`

A test will 'fail' if either of these happen:

1. there is an `error`
2. there are a failed `assert`, [see assert functions here](functions.md).
3. there is an exception

When any test fails, `bds -t` will return a non-zero exit code

## Test Cases Example

File <a href="bds/test_24.bds">test_24.bds</a>

```
#!/usr/bin/env bds

int twice(int n) {
    return 3 * n // Maybe I don't really know what "twice" means...
}

void test01() {
    print("Nice test code 01\n")
}

void test02() {
    i := 1
    i++
    assert("I can't add", 2, i)
}

void test03() {
    i := twice( 1 )
    assert("This is weird", 2, i)
}
```

When we execute the tests, we get

```
$ bds -t ./test_24.bds

Nice test code 01
00:00:00.002	Test 'test01': OK

00:00:00.003	Test 'test02': OK

00:00:00.004	test_24.bds:19,4: Expecting '2', but was '3': This is weird
Assertion failed: test_24.bds:19,4: Expecting '2', but was '3': This is weird
00:00:00.004	Test 'test03': FAIL

00:00:00.005	Totals
                  OK    : 2
                  ERROR : 1
```

## Coverage

You can set the minimum acceptable coverage, using the command line option `-coverageMin` and a ratio.

For instance, in this example, bds will fail (exit code `1`) if the coverage is less than `0.80` (80%)

```
$ bds -coverageMin 0.80 -t test_cases.bds
```

### Multiple test suites

When you want to run multiple tests files, you can tell `bds` to save and accumulate statistics to a file using the command line option `-coverageFile`

Example: Imagine you have two test suite files that test different functionality of a shared code

### Example: Multiple test suites

Let's imagine we have some shared code `shared.bds` and two test suites `test_suite_1.bds` and `test_suite_2.bds`.
We want to run both test suites and get aggregated coverage statistics for our `shared.bds` code.

File: `shared.bds`

```
string zzz(bool ok) {
    if( ok ) return 'OK'
    else return 'BAD'
}
```

File: `test_suite_1.bds`

```
include 'shared'
void test_01() { assert('OK', zzz(true)) }  # Tests one half of the function `zzz()`
```

File: `test_suite_2.bds`

```
include 'shared'
void test_02() {assert('BAD', zzz(true)) }  # Tests the other half of the function `zzz()`
```

**Running the test cases:**

Please keep in mind that:

1. Before running the first test file, you should delete previous statistic files.
2. If you are doing a `-coverageMin`, it should only be performed when running the last test file, because intermediate commands will fail to meet the minimum coverage.

```
# Make sure we delete old coverage statitics
rm -vf bds.coverage

# Run first tests suite and save coverage statitics to 'bds.coverage'
bds -coverage \
    -coverageFile bds.coverage \
    -t test_suite_1.bds

# Run second test suite.
# Since the file 'bds.coverage' was created by the previous command, bds will:
#   1) loaded the coverage statics from 'bds.coverage'
#   2) Update the new coverage statitics from 'test_suite_2.bds'
#   3) Save the updated coverage statitics
#
# Note: Since this is out final test suite, we add a minimum coverage requirement
#       (in this case 95%)
bds -coverage \
    -coverageFile bds.coverage \
    -coverageMin 0.95 \
    -t test_suite_2.bds
```

So the tests cases will succeed (exit code will be '0'), because accumulated coverage statistic between `test_suite_1.bds` and `test_suite_2.bds` is 100% (greater `-coverageMin` of 95% required).

### Covered line

If two expressions are on the same line but only one is executed in the tests, the line does NOT count as covered.

For example, in the following test case only tests one of the two conditions in the ternary expression, so the coverage will not count the line (`return` expression) as covered:

```
int myAbs(int n) {
    return n > 0 ? n : -n    # Ternary expression
}

testCase01() {
    assert( 1, myAbs(-1) )   # We only test one condition
}
```

Important: When calculating the coverage, the code within the test cases is excluded from the calculation. The statistics are summarized and shown in the `Test code` summary line of the repot.
But test cases code does not count in the coverage.

### Coverage report

For example, let's look at the following test case

```
int twiceAbs(int n) {
    if( n < 0 ) {
        return -2 * n
    } else {
        return 2 * n
    }
}

void test01() {
    fourtyTwo := twiceAbs( 21 )
    if( fourtyTwo < 0 ) {
        println("This line is NEVER executed")
    }
    assert(42, fourtyTwo)
}
```

If we run the tests with coverage we get:

```
$ bds -t -coverage twiceAbs.bds

00:00:00.005	Test 'test01': OK

00:00:00.008	Totals
                  OK    : 1
                  ERROR : 0
|                     File name                      | Covered /  Total  |     %   | Not covered intervals
+----------------------------------------------------+-------------------+---------+------------------------
|                    twiceAbs.bds                    |       3 /       4 |  75.00% | 5
+----------------------------------------------------+-------------------+---------+------------------------
|                     Test code                      |       4 /       5 |  80.00% |
|                       Total                        |       3 /       4 |  75.00% |
```

Each line shows some details:

-   `File name`: Shows the file
-   `Covered / Total`: Represent
    -   `Coverred`: The number of lines that are fully covered by the test cases.
    -   `Total`: Total lines of code analyzed. Note: Empty lines, blank lines, comments, and other non-code lines are not counted.
-   `%`: percentage of `Covered / Total`
-   List of intervals that the test cases did not covered (in this case, line `5` was not covered)

Summary lines: There are two summary lines:

-   `Test code`: These are statistics about the "test code", i.e. the code that implements the test cases. The statistics are the same as fr individual files.
-   `Total`: These are total statistics of the individual files (if you add the individual files statistics you get this result).
