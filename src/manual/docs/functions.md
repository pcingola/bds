# Predefined functions 
 bds provides some predefined functions.
                
| Function                                                                | Meaning                                                                                                                                             |
|-------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| int abs(int x)                                                          | Absolute value of a number                                                                                                                          |
| real abs(real x)                                                        | Absolute value of a number                                                                                                                          |
| real acos(real x)                                                       | The trigonometric arc-cosine of a number                                                                                                            |
| real asin(real x)                                                       | The trigonometric arc-sine of a number                                                                                                              |
| real atan(real x)                                                       | The trigonometric arc-tangent of a number                                                                                                           |
| real atan2(real x)                                                      | Returns the angle theta from the conversion of rectangular coordinates (x, y) to polar coordinates (r, theta).                                      |
| assert(bool expr)                                                       | Used for testing: Throw an error if `expr` is `false`                                                                                               |
| assert(string msg, bool expr)                                           | Used for testing: Throw error message `msg` if `expr` is `false`                                                                                    |
| assert(string msg, bool expected, bool result)                          | Used for testing: Throw error message `msg` if `result` value is not equal to `expected` (compare `bool`)                                           |
| assert(int expected, int result)                                        | Used for testing: Throw error message `msg` if `result` value is not equal to `expected` (compare `int`)                                            |
| assert(string msg, int expected, int result)                            | Used for testing: Throw error message if `result` value is not equal to `expected` (compare `int`)                                                  |
| assert(string expected, string result)                                  | Used for testing: Throw error message if `result` value is not equal to `expected` (compare `string`)                                               |
| assert(string msg, string expected, string result)                      | Used for testing: Throw error message `msg` if `result` value is not equal to `expected` (compare `string`)                                         |
| real cbrt(real x)                                                       | The cube root of a number                                                                                                                           |
| real ceil(real x)                                                       | The ceiling of a number                                                                                                                             |
| [string{} config(string fileName)](#config-function)                    | Read and parse 'fileName', return &lt;name,value&gt; pairs in a map.                                                                                |
| [string{} config(string fileName, string{} defaults)](#config-function) | Same as `string{} config(string fileName)`, but using `defaults` as default values (if not found in fileName)                                       |
| real copySign(real x, real y)                                           | Returns the first floating-point argument with the sign of the second floating-point argument                                                       |
| real cos(real x)                                                        | The trigonometric cosine of an angle                                                                                                                |
| real cosh(real x)                                                       | The hyperbolic cosine of an angle                                                                                                                   |
| real exp(real x)                                                        | Return e^x                                                                                                                                          |
| real exppm1(real x)                                                     | Return e^x-1                                                                                                                                        |
| real floor(real x)                                                      | The floor of a number                                                                                                                               |
| int getExponent(real x)                                                 | exponent used in the representation of a real                                                                                                       |
| string getModulePath()                                                  | Return the absolute path to the file currently executing                                                                                            |
| string getVar(string name)                                              | Get variable's value represented as a string, e.g.: `int i=42; getVar('i') # Output "42"`                                                           |
| string getVar(string name, string default)                              | Get variable's value represented as a string or a default value if not set. E.g. `getVar('i', 'zzz') # Output "zzz" if variable 'i' is not defined` |
| bool hasVar(string name)                                                | Is the variable 'name' defined? This can be used to check the existence of environment variables, which are inherited into the global scope         |
| real hypot(real x, real y)                                              | Returns sqrt(x2 +y2) without intermediate overflow or underflow.                                                                                    |
| real IEEEremainder(real x, real y)                                      | Computes the remainder operation on two arguments as prescribed by the IEEE 754 standard..                                                          |
| [json(string fileName)](#json)                                          | Read JSON file and set all matching variables with JSON's values                                                                                    |
| [json(string fileName, object)](#json)                                  | Read JSON file and set object's fields with JSON's values                                                                                           |
| [json(string fileName, object, string[] subFieldNames)](#json)          | Read JSON file and set object's fields with JSON's values in subFields                                                                              |
| log(string msg)                                                         | Log 'msg' (i.e. show to stderr)                                                                                                                     |
| logd(string msg)                                                        | Log 'msg' (i.e. show to stderr), also show current file name and line number                                                                        |
| real log(real x)                                                        | Natural logarithm of a number                                                                                                                       |
| real log10(real x)                                                      | Logarithm (base 10) of a number                                                                                                                     |
| real log1p(real x)                                                      | Natural logarithm of '1+x'                                                                                                                          |
| int max(int n1, int n2)                                                 | Maximum of two numbers                                                                                                                              |
| real max(real n1, real n2)                                              | Maximum of two numbers                                                                                                                              |
| int min(int n1, int n2)                                                 | Minimum of two numbers                                                                                                                              |
| real min(real n1, real n2)                                              | Minimum of two numbers                                                                                                                              |
| real nextAfter(real x, real y)                                          | Returns the number adjacent to the first argument in the direction of the second argument                                                           |
| real nextUp(real x)                                                     | Returns the floating-point value adjacent to d in the direction of positive infinity.                                                               |
| real pow(real x, real y)                                                | Return x^y                                                                                                                                          |
| print( expr )                                                           | Show to stdout (same as 'print' statement)                                                                                                          |
| printErr( expr )                                                        | Show to stderr                                                                                                                                      |
| printHelp()                                                             | Print automatically generated help message (see 'help' statement)                                                                                   |
| real rand()                                                             | Random number [0, 1] interval                                                                                                                       |
| int randInt()                                                           | Random number (64 bits)                                                                                                                             |
| int randInt(int range)                                                  | Random number [0, range] interval                                                                                                                   |
| void randSeed(int seed)                                                 | Set random seed (for current thread)                                                                                                                |
| int[] range(min, max)                                                   | A list of numbers between [min, max] inclusive                                                                                                      |
| int[] range(min, max, step)                                             | A list of numbers between [min, min+step, min+2*step, ... ]. Includes max if min+N*step = max                                                       |
| real[] range(min, max, step)                                            | A list of numbers between [min, min+step, min+2*step, ... ]. Includes max if min+N*step = max                                                       |
| real rint(real x)                                                       | Returns the real value that is closest in value to the argument and is equal to a mathematical integer                                              |
| int round(real x)                                                       | Rounded number                                                                                                                                      |
| real scalb(real x, int sf)                                              | Return x * 2^sf rounded                                                                                                                             |
| real signum(real x)                                                     | The sign function of a number                                                                                                                       |
| real sin(real x)                                                        | The trigonometric sine of an angle                                                                                                                  |
| real sinh(real x)                                                       | The hyperbolic-sine of an angle                                                                                                                     |
| sleep( int seconds )                                                    | Sleep for 'seconds'                                                                                                                                 |
| sleep( real seconds )                                                   | Sleep for '1000 * seconds' milliseconds. E.g. sleep(0.5) sleeps for half a second                                                                   |
| real sqrt(real x)                                                       | The square root of a number                                                                                                                         |
| real tan(real x)                                                        | The trigonometric tangent of an angle                                                                                                               |
| real tanh(real x)                                                       | The hyperbolic tangent of an angle                                                                                                                  |
| string[] tasksDone()                                                    | Return a list of all task IDs that finished executing                                                                                               |
| string[] tasksRunning()                                                 | Return a list of all task IDs that are currentl executing                                                                                           |
| string[] tasksToRun()                                                   | Return a list of all task IDs that scheduled for execution                                                                                          |
| int time()                                                              | Return the milliseconds elapsed since epoch                                                                                                         |
| real toDegrees(real x)                                                  | Convert x radians to degrees                                                                                                                        |
| real toRadians(real x)                                                  | Convert x degrees to radians                                                                                                                        |
| int toInt(bool b)                                                       | Convert boolean to int                                                                                                                              |
| int toInt(real r)                                                       | Convert real to int                                                                                                                                 |
| string type(expr)                                                       | Get the expression's resulting type name, e.g. 'int' or 'string'                                                                                    |
| real ulp(real r)                                                        | Returns the size of an ulp of the argument                                                                                                          |

### Config function

Reading a config file can be done using `config` function which parses a file and returns `<name, value>' pairs in a map.
Parsing: Lines starting with '#' are ignored, so are blank lines.
Name/Value delimiters can be any of ':', '=' or '\t' (the first one found in each line will be used).
The following are valid and equivalent:

```
name : value
name = value
name \t value
```

**Usage example**

Code snippet:
```
cfg := config('myfile.config')
println cfg
```

File contents `myfile.config`
```
name1 : value_1
name2 = value_2_without_quotes
name2_quotes = "value_2 with quotes"
name3   value_3
```

Program output:
```
{ name1 => value_1, name2 => value_2_without_quotes, name2_quotes => "value_2 with quotes", name3 => value_3 }
```

# JSON

The `json(fileName)` function reads a JSON file and sets all matching variables (in current and upper scopes) to the values from the JSON file.

For details see:
- [Matching rules](#json-matching-rules): How name matching is done 
- [Variable initializarion rules](#json-variable-initialization-rules): How objects or lists are initialized

### JSON: Setting global variables

The `json(fileName)` function will try to find variable's names in the current and upper scopw, all the way up to the global scope. 

**Example:** In this code snippet
```
string firstName, lastName

# This statement sets the variables that match entries in the JSON file
json("json_example_01.json")    

pritnln "firstName=$firstName, lastName=lastName"
```

Here is the content of `json_example_01.json`:
```
{
    "firstName": "John",
    "lastName": "Smith",
    "age": 25,
}
```

In the code snippet, the function `json("json_example_01.json")` sets the variables `firstName` and `lastName` because they match entries in the JSON file.
The output of the program, would be:
```
firstName=John, lastName=Smith
```

### JSON: Setting objects' fields

The `json(fileName)` function also set fields inside objects (recursively).

**Example:** In this code, we have classes and lists.
The JSON file matches the names of local variables so their fields are set according to the values in the JSON file

```
class Address {
    string streetAddress, city, state
    int postalCode
}

class Phone {
    string type, number
}

string firstName, lastName
age := 1
address := new Address()
Phone[] phoneNumbers

json('json_example_02.json')

println "firstName = '$firstName', lastName = '$lastName', age = '$age'"
println "address = '$address'"
println "phoneNumbers = $phoneNumbers"
```

The JSON file `json_example_02.json` is:
```
{
    "firstName": "John",
    "lastName": "Smith",
    "age": 25,
    "address": {
        "streetAddress": "21 2nd Street",
        "city": "New York",
        "state": "NY",
        "postalCode": 10021
    },
    "phoneNumbers": [
        {
            "type": "home",
            "number": "212 555-1234"
        },
        {
            "type": "fax",
            "number": "646 555-4567"
        }
    ]
}
```

And the program's output is:
```
firstName = 'John', lastName = 'Smith', age = '25'
address = '{ city: New York, postalCode: 10021, state: NY, streetAddress: 21 2nd Street }'
phoneNumbers = [{ number: 212 555-1234, type: home }, { number: 646 555-4567, type: fax }]
```

bds correctly populates objects (`address`) and arrays of objects (`phoneNumbers[]`)

### JSON: Setting specific Objects

Using `json(fileName, objbect)` you can set a specific `object` with the JSON values from JSON's file `fileName`.

**Example:** In this example we load the data into the object `person` using the `json(fileName, person)` function.
(note that we are using the JSON file `json_example_02.json` from the previous example)
```
fileName := "json_example_02.json"

class Address {
    string streetAddress, city, state
    int postalCode
}

class Phone {
    string type, number
}

class Person {
    string firstName, lastName
    int age
    Address address
    Phone[] phoneNumbers
}

person := new Person()
json('json_example_02.json', person)

println person
```

The output the program is `bds`'s representation of `person` object:
```
{ address: { city: New York, postalCode: 10021, state: NY, streetAddress: 21 2nd Street }, age: 25, firstName: John, lastName: Smith, phoneNumbers: [{ number: 212 555-1234, type: home }, { number: 646 555-4567, type: fax }] }
```

### JSON: Setting specific Objects

Using `json(fileName, objbect)` you can set a specific `object` with the JSON values from JSON's file `fileName`.

**Example:** In this example we load the data into the object `person` using the `json(fileName, person)` function.
(note that we are using the JSON file `json_example_02.json` from the previous example)

```
fileName := "json_example_02.json"

class Address {
    string streetAddress, city, state
    int postalCode
}

class Phone {
    string type, number
}

class Person {
    string firstName, lastName
    int age
    Address address
    Phone[] phoneNumbers
}

person := new Person()
json('json_example_02.json', person)

println person
```

### JSON: Using sub-fields in the JSON file

Sometimes we need to use a specific sub-field from the JSON file, we can provide a list of subfields using `json(fileName, objbect, subFields)`.
The sub-fields selected in sequence, from the root of the JSON file.

**Example:** If we want to select the `address` for person ID `id_001` in the following JSON file

File `json_example_03.json`:
```
{
   "id_001": {
       "firstName": "John", "lastName": "Smith", "age": 25,
       "address": {
           "streetAddress": "21 2nd Street",
           "city": "New York",
           "state": "NY",
           "postalCode": 10021
       },
   },
   "id_002": {
       "firstName": "Peter", "lastName": "Peterson", "age": 52,
       "address": { "streetAddress": "234 Washingont", "city": "San Francisco", "state": "CA", "postalCode": 20010 },
   }
}
```

We can use this code to load JSON entry `id_001.address`:
```
class Address {
    string streetAddress, city, state
    int postalCode
}

# Create a new object type 'Address'
Address addr = new Address()  

# The parameter ['id_001', 'address'] tells JSON to select the
# entry "id_001.address" (i.e. fist select entry 'id_001', from that 
# select entry 'address'). This selected entry is used to set object 'addr'
json('json_example_03.json', addr, ['id_001', 'address']) 
```

### JSON Matching rules

In order to make `json` more general, the `bds` variable names and JSON field names don't have to be equal.
For example, the variable name could be `firstName` whereas the JSON field `first_name`.

How does `json` match variable names (or object's field names) with JSON's names?
A scope variable `name` or object field `name` is matched to `JSON name` according to the following rules (when there is ambiguity, the first matching rule wins):

1. Exact match: `JSON name` equals to `name` (e.g. `myName` in JSON matches `myName` in bds)
1. Lowercase match: `JSON name` in lowercase is equal to `name` in lowercase (e.g. `myNAME` in JSON matches `myName` in bds)
1. AlphaNum match: `JSON name` without any non-alpha-numeric chars, in lowercase is equal to `name` without any non-alpha-numeric chars lowercase (e.g. `my-NAME 2` in JSON matches `my_name_2` in bds)
1. First search variables in local scope, then in all parent scopes. The last scope in the search is the Global Scope.

### JSON Variable initialization rules

When a variable or a field is not initialized (e.g. `null`) the `json` function will initialize it according to these rules: 

1. **Variables:** If a variable is `null`, it **WILL NOT** be initialized (i.e. the value will not be set).
1. **Object's fields:** If an object field is `null`, it **WILL** be initialized.
    1. A new object will be created
    1. Constructor methods **ARE NEVER** invoked, even if an empty-parameter constructor method does exist.
1. **List:** If there is a list of objects, each new JSON object will be **APPENDED** to the list
    1. The list will not be emptied or re-initialized
    1. New objects will be created and appended to the list
    