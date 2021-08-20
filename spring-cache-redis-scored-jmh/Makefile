all: build benchmarks

build:
	mvn clean package

help: build
	java -jar target/benchmarks.jar -h

benchmarks:
	java -jar target/benchmarks.jar -bm all -f 1 -t max -tu ns -o target/jmh.txt
