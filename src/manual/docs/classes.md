
# Classes

Bds has some basic object oriented model that help to modularize complex data analysis pipelenes.
Has you may expect, classes can contiain fields (class variables) and methods (class functions).
```
class A {
	string name
	int value
}
```

**`new` operator**
To create a new object you use the operator `new` followed by the class name and parameters for the constructor method.
A constructor method has the same name as the class and returns `void`.
If no constructor is provided in the class definition, a default (empty) method is created.
E.g.:
```
a := new A()    # Create object 'A' and invoke empty (default) constructor
```

Example of a constructor with parameters:
```
class A {
	int x

	# Constructor
	void A(int x) {
		this.x = x
	}
}

a := new A(42)
println "a: $a"
```
The output of this program would be (by default printing an object shows the fields):
```
a: { x: 42 }
```

**Inheritance**: A class can inherit from another class using `extends` keywors in the class definition. 

```
class A {
	int x
	void A(int x) { this.x = x }
}

class B extends A {
	int y=17
}

b := new B()
println "b: $b"
```
The output of this program is:
```
b: { x: 0, y: 17 }
```
