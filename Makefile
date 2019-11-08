




all: compile run

compile:
	javac *.java

run: compile
	java PtreeDoc FileTwo.txt

clean:
	rm *.class