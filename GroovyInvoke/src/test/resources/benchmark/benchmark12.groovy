class X {
  def foo() {
    "X.foo()"
  }
}

class Y {
  def foo() {
    "Y.foo()"
  }
}

class Z {
  def foo() {
    "Z.foo()"
  }
}

def bar(obj) {
	obj.foo()
}

x = new X()
y = new Y()
z = new Z()

N = Integer.parseInt(args[0]) / 3

for (i in 0..N) {
	bar(x)
}

for (i in 0..N) {
	bar(y)
}

for (i in 0..N) {
	bar(z)
}