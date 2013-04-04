Dynamate Framework

============================================================
COMPILING
============================================================
You can compile the code manually with javac or using the supplied Eclipse project files.

1. Compile the Dynamate framework using the lib and classes directories as classpath dependencies

2. Compile the individual integration code for each client using dynamate's bin, lib and classes directories as classpath dependencies

3. Compile the BenchmarkSuite using the lib directory as a classpath dependency

============================================================
RUNNING BENCHMARKS
============================================================
The supplied BenchmarkSuite project executes some benchmarks for the client integration using the original implementation and the dynamate modified implementation. The BenchmarkSuite will start new java processes for each benchmark. The benchmark runners are located in the src/impl directory and can be started by invoking the desired *Runner class.
The vm argument WORKSPACE must be passed (using the -D switch) pointing to the root dynamate directory containing the framework and the individual integrations. The argument NUMBER_OF_RUNS can be passed to control the total number of invokations of each benchmark (will be set to 1 if not passed).

Exemplary Invokation of the JRuby benchmarks:

    java -DWORKSPACE=/home/user/dynamate -DNUMBER_OF_RUNS=3 -cp bin impl.jruby.JRubyRunner
    
============================================================
LICENSE
============================================================
Copyright (c) 2013, Kamil Erhard
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name Dynamate nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
