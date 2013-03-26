import sys

class X(object):
	def foo(self):
		return "X.foo"

class Y(object):
	def foo(self):
		return "Y.foo"

class Z(object):
	def foo(self):
		return "Z.foo"
        
def bar(obj):
	return obj.foo()        

N = int(sys.argv[1]) / 3

x = X()
y = Y()
z = Y()

for i in range(N):
	bar(x)
	
for i in range(N):
	bar(y)
	
for i in range(N):
	bar(z)
	
