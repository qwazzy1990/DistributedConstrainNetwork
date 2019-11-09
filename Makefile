




all: compile run

compile:
	javac *.java

run: compile
	java PtreeDoc BDWCN.mas

clean:
	rm *.class