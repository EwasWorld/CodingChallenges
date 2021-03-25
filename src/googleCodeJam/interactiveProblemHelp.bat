rem https://kotlinlang.org/docs/command-line.html#create-and-run-an-application
rem download and unzip kotlin_compiler.zip and put it on the path

rem compile the kotlin into a jar
kotlinc myprogram.kt -include-runtime -d problem4.jar

rem to run it on the command line
rem java -jar problem4.jar

rem to run the interactive test
rem `interactive_runner.py` is like the tool that joins stdin if one to stdout of the other and vice versa
rem `local_testing_tool.py 0` is the test given in the problem and it's input as explained in a comment at the top
python interactive_runner.py python3 local_testing_tool.py 0 -- java -jar problem4.jar