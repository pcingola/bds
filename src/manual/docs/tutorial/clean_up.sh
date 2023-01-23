#!/bin/bash

# Clean up and create input files
rm -vf *.txt 
rm -vf example_{in,out}.*
rm -vf *.chp
rm -rvf example_*.bds.*

echo "Creating file: 'in.txt'"
date > "in.txt"

for i in {0..3}
do 
	echo "Creating file: 'example_in.$i'"
	echo "Hello $i" > example_in.$i
done
