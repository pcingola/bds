# Test cases

Because nobody writes perfect code... 

## Test cases

`bds` provides a simple testing functionality. 
Simply use the `-t` command line option and `bds` will run all functions `test*()` (i.e. that is functions whose names start with 'test' and have no arguments).

File <a href="bds/test_24.bds">test_24.bds</a>
```
#!/usr/bin/env bds

int twice(int n)    return 3 * n    // Looks like I don't really know what "twice" means...

void test01() {
    print("Nice test code 01\n")
}

void test02() {
    i := 1
    i++
    if( i != 2 )    error("I can't add")
}

void test03() {
    i := twice( 1 )
    if( i != 2 )    error("This is weird")
}
```

When we execute the tests, we get
```
$ bds -t ./test_24.bds 

Nice test code 01
00:00:00.002	Test 'test01': OK

00:00:00.003	Test 'test02': OK

00:00:00.004	Error: This is weird
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

Running the test cases:

**Important:**
1. Before running the first test file, you should delete previous statistic files.
2. If you are doing a `-coverageMin`, it should only be performed when running the last test file, because intermediate commands will fail to meet the minimum coverage.

```
# Make sure we delete old coverage statitics
rm -vf bds.coverage

# Run first tests file and save coverage statitics to 'bds.coverage'
bds -coverage \
    -coverageFile bds.coverage \ 
    -t test_suite_1.bds

# Run second tests file.
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

So the tests cases will success and exit code will be '0', because accumulated coverage statistic is 100% (greater `-coverageMin` of 95% required).
