#!/bin/bash
#
# Use this shell script to compile (if necessary) your code and then execute it. Belw is an example of what might be found in this file if your program was written in Python 3.7
# python3.7 ./src/consumer_complaints.py ./input/complaints.csv ./output/report.csv

cd $(dirname $0)
javac -d bin ./src/ConsumerComplaints.java ./src/InvalidFormatException.java
java -Dfile.encoding=UTF-8 -classpath bin ConsumerComplaints ./input/complaints.csv ./output/report.csv
