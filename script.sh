#!/bin/bash
# Run script in base folder of project outside of src
java -cp .:src/jomp1.0b.jar jomp.compiler.Jomp src/JOMP/naiveJOMP
java -cp .:src/jomp1.0b.jar jomp.compiler.Jomp src/JOMP/movingCircleJOMP
javac -cp .:src/jomp1.0b.jar:src/habanero-java-lib-0.1.2.jar:src/ src/TestingAlgo/Main.java
cd src/
java -cp .:jomp1.0b.jar:habanero-java-lib-0.1.2.jar -Djomp.threads=4 TestingAlgo/Main
cd ..

