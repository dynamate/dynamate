class Duck:
	clazz = "Duck"
	def quack (self, x):
		print self.clazz + ": quack " + x

class NoDuck:
	clazz = "NoDuck"
	def quack (self, x):
		print self.clazz + ": no quack " + x


def bar(obj, x):
	obj.quack(x)
	
ducks = [Duck(), NoDuck()]

bar(ducks[0], "x")
bar(ducks[1], "y")
bar(ducks[0], "z")
