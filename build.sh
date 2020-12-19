javac -encoding UTF-8 -classpath instrument/lib/javassist.jar instrument/*.java &&\
jar cvfm agent.jar instrument/MANIFEST.MF instrument/MyAgent.class &&\
java -javaagent:agent.jar instrument.HelloWorld