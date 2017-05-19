#!/bin/bash
# Run script in base folder of project outside of src

java -cp .:src/jomp1.0b.jar jomp.compiler.Jomp src/JOMP/naiveJOMP src/JOMP/movingCircleJOMP
javac -cp .:src/jomp1.0b.jar:src/habanero-java-lib-0.1.2.jar:src/jsc.jar:src/ src/TestingAlgo/Main.java
javac -cp .:src/jomp1.0b.jar:src/ src/JOMP/naiveJOMP.java
javac -cp .:src/jomp1.0b.jar:src/ src/JOMP/movingCircleJOMP.java


cd src/
java -cp .:jomp1.0b.jar:habanero-java-lib-0.1.2.jar:jsc.jar -Dhj.numWorkers=$1 -Djomp.threads=4 TestingAlgo/Main
cd ..

find . -type f -name '*.class' -delete
find . -type f -name '*JOMP.java' -delete
