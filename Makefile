




all: compile run

compile:
	javac *.java

run: compile
	java PtreeDoc FileOne.txt f.agt

clean:
	rm *.class