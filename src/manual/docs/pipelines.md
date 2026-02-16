# Creating data pipelines 
In order to create data pipelines, you need to execute 'tasks' and coordinate execution dependencies. Here we show how to do it in `bds`
            
### Executing a task 
The most basic operation is to execute a task, which is done using a `task` expression.
`bds` takes care of executing a task on different environments (local computer, server, cluster, etc.), so you don't need to focus on mundane details (such as cluster queue monitoring, or querying remote computers for resources).


In this toy example, we schedule 10 tasks for execution. 
I'm running this on a computer that only has 8 CPUs, so not all tasks can execute in parallel.

File `test_04.bds`
```
#!/usr/bin/env bds

for( int i=0 ; i < 10 ; i++ ) {
    task echo Hi $i ; sleep 1 ; echo Done $i ; sleep 1
}
```

Note that bds interpolates variable `$i` (string interpolation simply means replacing by the value of a variable within the string)

```
$ ./test_04.bds
Hi 1
Hi 6
Hi 2
Hi 4
Hi 3
Hi 7
Hi 0
Hi 5
Done 1
Done 6
Done 2
Done 4
Done 3
Done 7
Done 0
Done 5
Hi 9
Hi 8
Done 9
Done 8
```

In this case, we are running on an 8 core computer, so the first 8 tasks get executed in parallel. 
The rest is executed when the first tasks finish.

**Warning:** Task execution order is not guaranteed (e.g. a cluster scheduler can decide to run tasks out of order).
We'll see how to coordinate tasks later.

<iframe width="560" height="315" src="https://www.youtube.com/embed/ehFfU8vLwi8" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>


### Running on a cluster 
Here we show how exactly the same script is run on a cluster, keep in mind that not a single line of code changed. 
We copy tha same script (used in the previous section) to a cluster. 
We execute it, but now the tasks are scheduled using MOAB, Torque, PBS, SLURM or Grid Engine.
        
```
$ bds -s cluster test_04.bds
```

<iframe width="560" height="315" src="https://www.youtube.com/embed/o47wxUdYzvk" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
        
### Task dependencies

In a typical pipeline we must be able to control task execution which depends on previously executed tasks.
`bds` provides several mechanisms for this.

### Waiting for tasks: `wait`

The simplest form of execution control is to wait until one or more tasks finish before executing the next task(s).
See the [`wait` section](wait.md) for details and examples.

<iframe width="560" height="315" src="https://www.youtube.com/embed/vTekXn3sKzs" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

### Dependency operator `<-`

The dependency operator provides a simple way to see if a file needs to be updated (i.e. recalculated) with respect to some inputs, similar to `make`.
See the [dependency operator section](dep_op.md) for details and examples.

<iframe width="560" height="315" src="https://www.youtube.com/embed/oSjhkRuc0I8" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

### Complex dependencies: `dep` and `goal`

For complex task scheduling interdependencies, `bds` provides `dep` and `goal` statements.
`dep` defines a task declaratively (without evaluating if it should run), and `goal` triggers execution of all dependencies necessary to produce a given output.
See the [`goal` section](goal.md) for details and examples.
