




all: compile run

compile:
	javac *.java

run: compile
	java PtreeDoc FileOne.txt

clean:
	rm *.class