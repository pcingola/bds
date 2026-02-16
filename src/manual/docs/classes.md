

# Classes

Bds has some basic object-oriented model that help to modularize complex data analysis pipelines.
As you may expect, classes can contain fields (class variables) and methods (class functions).
```
class A {
	string name
	int value
}
```

## Creating new objects

**`new` operator**
To create a new object you use the operator `new` followed by the class name and parameters for the constructor method.
A constructor method has the same name as the class and returns `void`.
If no constructor is provided in the class definition, a default (empty) method is created.
E.g.:
```
a := new A()    # Create object 'A' and invoke empty (default) constructor
```

## Constructor

The constructor is a method that has the same name as the class, returns `void` and can have parameters.

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

## Methods

Methods are functions defined inside a class. They can access the object's fields directly.

```
class Zzz {
	int i = 42

	int f(int x) {
		return i + x
	}
}

z := new Zzz()
println z.f(8)
```

Output:
```
50
```

## The `this` keyword

Inside a method, `this` refers to the current object. It is useful to disambiguate between a field and a parameter with the same name.

```
class Zzz {
	int i

	int f(int i) {
		return this.i + i
	}
}

z := new Zzz()
z.i = 2
println z.f(40)
```

Output:
```
42
```

## Inheritance

A class can inherit from another class using `extends` keywords in the class definition. 

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

A subclass can call methods from the parent class:

```
class A {
	int i = 42
	int f(int x)	return i + x
}

class B extends A {
	int j = 7
	int g(int x)	return j + f(x)
}

b := new B()
println b.g(1)
```

Output:
```
50
```

## Method overriding

A subclass can override a method from the parent class by defining a method with the same name and signature. Method dispatch is virtual, so the overridden method is called even when accessed through a parent type reference.

```
class A {
	int f(int x) {
		return 2 + x
	}
}

class B extends A {
	int f(int x) {
		return 10 + x
	}
}

A a = new B()
println a.f(4)
```

Output:
```
14
```

## The `super` keyword

Inside a method, `super` can be used to call the parent class's method.

```
class A {
	int f() { return 1 }
}

class B extends A {
	int f() { return super.f() + 1 }
}

ret := (new B()).f()
println "ret=$ret"
```

Output:
```
ret=2
```
