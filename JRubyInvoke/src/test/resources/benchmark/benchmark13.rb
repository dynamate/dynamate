class S
  def foo
    self.class.name + ".foo()"
  end
end

class A < S
  
end

a = A.new

puts a.foo()