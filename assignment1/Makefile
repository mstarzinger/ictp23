.PHONY: run clean
classname = Factorizer

run: $(classname).class
	java $(classname)

%.class: %.java
	javac $<

clean:
	rm $(classname)*.class
