# Special operators 

These are statements, operators, and expressions that are unique to `bds`.
We just enumerate them here, but we explain details on what they mean and how they work in the following sections. 
This list is intended as a reference for people that are already familiar with these concepts, so don't despair if you don't understand what they mean.
				
				
### Dependency operator
Dependency operator `<-` is true if any left-hand side file needs to be updated with respect to any right-hand side file
```
# Evaluate dependency 
if( 'out.txt' <- 'in.txt' )    print("File out.txt needs to be updated\n")
```

### checkpoint
Create a checkpoint. Optional expression is a file name
```
# Wait for all tasks to finish
checkpoint "program.chp"
```

### dep
Define dependency tasks. Tasks are not scheduled for execution until `goal` decides which dependencies must be executed to satisfy an output.
```
# Define a dependency (not executed until a 'goal' requires it)
dep( out <- in ) sys cat $in > $out
```

### goal
Define a task DAG objective. Triggers execution of all `dep` tasks necessary to produce the given output.
```
# Execute all dependencies needed to produce 'output.txt'
goal "output.txt"
```

### kill
Kill a running task by its task ID.
```
tid := task echo "Long running task"; sleep 100
kill tid
```

### par (parallel)
Execute code in parallel. Both `par` and `parallel` keywords are valid.
```
par {
    for( int i=0 ; i < 10 ; i++ ) {
        print("This is executed in parallel: $i\n")
    }
}
```
					or just call a function in parallel
```
par doSomething(arg1, arg2)
```

### sys
Execute an OS command. Execution is immediate and local (i.e. in the same computer as `bds` is running). It's intended for executing fast OS commands (not heavyweight processing).
```
# Execute an "ls" command
sys ls -al
```

### task
Schedule an OS command for execution. Depending on the value of `system`, the execution can be local, in a cluster, remote server, etc.
```
# Execute bwa command (create an index of the human genome)
task bwa index hg19.fasta
```

### wait
Wait for task(s) to finish. It can be one task, a list of tasks or all tasks (if no expression)
```
# Wait for all tasks to finish
wait

# Wait for one task to finish
wait taskId

# Wait for several tasks to finish
wait listOfTaskIDs
```
