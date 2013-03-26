class X
  def foo
    "X.foo()"
  end
end

class Y
  def foo
    "Y.foo()"
  end
end

class Z
  def foo
    "Z.foo()"
  end
end

def bar(obj)
	obj.foo()
end

x = X.new
y = Y.new
z = Z.new

N = Integer(ARGV[0]) / 3

for i in 0..N
	bar(x)
	bar(y)
	bar(z)
end
