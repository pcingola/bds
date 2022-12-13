# Tasks

Queued command execution with resource management.

A `task`expression, just like `sys` expression, also executes a command.
The main difference is that a `task` is "scheduled for execution" instead of executed immediately.
Task execution order is not guaranteed but `bds` provides a mechanism for creating task dependencies by means of `wait` statements.

A task expression either performs basic resource management or delegates resource management to cluster management tools.
The idea is that if you schedule a hundred tasks, but you are executing on your laptop which only has 4 CPUs, then `bds` will only execute 4 tasks at a time (assuming each task is declared to consume 1 CPU).
The rest of the tasks are queued for later execution.
As executing tasks finish and CPUs become available, the remaining tasks are executed.

Similarly, if you schedule 10,000 tasks for execution, but your cluster only has 1,000 cores, then only 1,000 tasks will be executed as a given time.
Again, other tasks are queued for later execution, but in this case, all the resource management is done by your cluster's workload management system (e.g. GridEngine, PBS, Torque, etc.).

**Warning:** Most cluster resource management do not guarantee that tasks are executed in the same order as queued.
Even if they do or if they are executed in the same host, a task can start execution and immediately be preempted.
So the next task in the queue can effectively start before the previous one.

There are different ways to execute tasks

| System    | Typical usage                                                                                                                | How it is done                                                                                                                                                                                                                                                                                                                                                                                                     |
| --------- | ---------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `aws`     | Running on an AWS EC2 instance                                                                                               | An AWS EC2 instance is created and the task runs in the instance.                                                                                                                                                                                                                                                                                                                                                  |
| `cluster` | Running on a cluster (GridEngine, Torque)                                                                                    | Tasks are scheduled for execution (using 'qsub' or equivalent command). Resource management is delegated to cluster workload management.                                                                                                                                                                                                                                                                           |
| `generic` | Enable user defined scripts to run, kill and find information on tasks                                                       | This 'generic' cluster allows the user to write/customize scripts that send jobs to the cluster system. It can be useful to either add cluster systems not currently supported by bds, or to customize parameters and scheduling options beyond what bds allows to customize in the config file. For details, see bds.config file and examples in the project's source code (directories `config/clusterGeneric`). |
| `local`   | Running on a single computer. E.g. programming and debugging on your laptop or running stuff on a server                     | A local queue is created, the total number of CPUs used by all tasks running is less or equal than the number of CPU cores available                                                                                                                                                                                                                                                                               |
| `moab`    | Running on a MOAB/PBS cluster                                                                                                | Tasks are scheduled for execution (using 'msub'). Resource management is delegated to cluster workload management.                                                                                                                                                                                                                                                                                                 |
| `pbs`     | Running on a PBS cluster                                                                                                     | Tasks are scheduled for execution (using 'msub'). Resource management is delegated to cluster workload management.                                                                                                                                                                                                                                                                                                 |
| `sge`     | Running on a SGE cluster                                                                                                     | Tasks are scheduled for execution (using 'qsub'). Resource management is delegated to cluster workload management.                                                                                                                                                                                                                                                                                                 |
| `slurm`   | Running on a SLURM cluster                                                                                                   | Tasks are scheduled for execution (using 'sbatch'). Resource management is delegated to cluster workload management.                                                                                                                                                                                                                                                                                               |
| `ssh`     | A server farm or a bunch of desktops or servers without a workload management system (e.g. computers in a University campus) | Basic resource management is performed by logging into all computers in the 'cluster' and monitoring resource usage.                                                                                                                                                                                                                                                                                               |

## How does it work

In escense a `task` command creates a shell script (i.e. a bash) and executes the script.
The script is simply all the `sys` lines whithin the `task`, put toghether, for example:

```
task {
    sys echo Hello
    sys echo The time is
    sys date
}
```

If we execute this script, we get something like:

```
$ bds -log test/z.bds
Hello
The time is
Tue Nov 29 10:19:52 EST 2022
```

Let's take a look at the files that `bds -log` created, one of them is the shell script that gets executed:

```
$ ls z.bds.20221129_101952_164/*.sh
z.bds.20221129_101952_164/task.z.line_2.id_1.6a8d85248781efbf.sh
```

If we look into the script we see (comemnts added):

```
#!/bin/bash -eu             | This will make the shell script stop on errors or undefined variables
set -o pipefail             | Also will the shell script will stop if any item in a 'piped' command fails
                            |
cd '/$HOME/bds_test'        | Change directory to where the script is executed
                            |
# SYS command. line 3       | Comment on which line of bds code produced the next command
echo Hello                  | Command from sys
# SYS command. line 4       | Comment on which line of bds code produced the next command
echo The time is            | Command from sys
# SYS command. line 5       | Comment on which line of bds code produced the next command
date                        | Command from sys
# Checksum: 577b1238        | Checksum
```

This information can be usful for debugging so you can check if the shell script is executing the code you are expecting.

**Logging**: `bds` logs the script's SDTOUT, STDERR, and exit code to files so you can revie them later.
Note: The `-log` command line option will keep all log files for all tasks. Otherwise they will be cleaned / deleted by `bds`.
Please take a look at the [Logging](logging.md) chapter for more details.

## Scheduling tasks

A task is scheduled by means of a `task` expression.
A `task` expression returns a task ID, a string representing a task.

E.g.: File <a href="bds/test_09.bds">test_09.bds</a>

```
tid := task echo Hello
print("Task is $tid\n")
```

Running we get:

```
$ ./test_09.bds
Task is test_09.bds.20140730_214947_810/task.line_3.id_1
Hello
```

`task` is non-blocking, which means that `bds` continues execution immediately without waiting for the task to finish.
So, many tasks can be scheduled by simply invoking a task statement many times.

Once a `task` is scheduled, execution order depends on the underliying system and there is absolutely no guarantee about execution order (unless you use a `wait` statements or other dependency mechanism).

E.g., this example shows clearly all the tasks are NOT executed in order, even on local computers:
File <a href="bds/test_10.bds">test_10.bds</a>

```
#!/usr/bin/env bds

for( int i=0 ; i < 10 ; i++ ) task echo Hi $i
```

```
$ ./test_10.bds
Hi 0
Hi 5
Hi 4
Hi 3
Hi 2
Hi 1
Hi 7
Hi 6
Hi 9
Hi 8
```

## Resource consumption and task options

Often `task` requires many CPUs or resources.
In such case, we should inform the resource management system in order to get an efficient allocation of resources (plus many cluster systems kill tasks that fail to report resources correctly).

E.g., In this example we allocate 4 CPUs per task and run it on an 8-core computer, so obviously only 2 tasks can run at the same time:
File <a href="bds/test_11.bds">test_11.bds</a>

```
#!/usr/bin/env bds

for( int i=0 ; i < 10 ; i++ ) {
    # Inform resource management that we need 4 core on each of these tasks
    task ( cpus := 4 ) {
        sys echo Hi $i ; sleep 1; echo Done $i
    }
}
```

Executing on my 8-core laptop, you can see that only 2 tasks are executed each time (each task is declared to require 4 cpus)

```
$ ./test_11.bds
Hi 0
Hi 1
Done 0
Done 1
Hi 3
Hi 2
Done 2
Done 3
Hi 4
Hi 5
Done 4
Done 5
Hi 6
Hi 7
Done 6
Done 7
Hi 9
Hi 8
Done 8
Done 9
```

## Task options and resources

List of resources or task options

| Variable                   | Default value | Resource / Task options                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| -------------------------- | ------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `allowEmpty`               | false         | If true, empty files are allowed in task's outputs. This means that a task producing empty files does not result in program termination and checkpointing.                                                                                                                                                                                                                                                                                               |
| `canFail`                  | false         | If true, a task is allowed to fail. This means that a failed task execution does not result in program termination and checkpointing.                                                                                                                                                                                                                                                                                                                    |
| `cpus`                     | 1             | Number of CPU (cores) used by the process.                                                                                                                                                                                                                                                                                                                                                                                                               |
| `detached`                 | false         | If true, the task will be detached, i.e. independent from the bds script originating it                                                                                                                                                                                                                                                                                                                                                                  |
| `mem`                      | 0             | Maximum amount of memory in bytes used by the process (0 means no restrictions or use cluster default)                                                                                                                                                                                                                                                                                                                                                   |
| `node`                     |               | If possible this task should be executed on a particular cluster node. This option is only used for cluster systems and ignored on any other systems.                                                                                                                                                                                                                                                                                                    |
| `queue`                    |               | Queue name of preferred execution queue (only for cluster systems).                                                                                                                                                                                                                                                                                                                                                                                      |
| `retry`                    | 0             | Number of times a task can be re-executed until it's considered failed.                                                                                                                                                                                                                                                                                                                                                                                  |
| `strictRemoteDependencies` | false         | If `true`, remote task dependencies are NOT downloaded to a local `tmp` directory, or uploaded to the remote location                                                                                                                                                                                                                                                                                                                                    |
| `taskName`                 |               | Assign a task name. This adds a label to the task as well as the taskId returned by `task` expression. Task ID is used to create log files related to the task (shell script, STDOUT, STDERR and exitCode files) so those file names are also changed. This makes it easier to find tasks in the final report and log files (it has no effect other than that). Note: If taskName contains non-allowed characters, they are sanitized (replaced by `_`). |
| `timeout`                  | 0             | Time in seconds that a task is allowed to execute (e.g. when running on a cluster). Ignored if zero or less. If process runs more than `timeout` seconds, it is killed. Zero means no limit.                                                                                                                                                                                                                                                             |
| `walltimeout`              | 0             | Time in seconds since the task is dispatched to the processing environment. E.g. in busy clusters a task can spend a long time being scheduled (cluster's PENDING state) until the task is run (cluster's RUNNING state), `walltimeout` limits the sum of those times (as oposed to `timeout` that only limits the RUNNIG state time). Zero means no limit.                                                                                              |

## Conditional execution

Conditional execution of tasks can, obviously, be achieved using an `if` statement.
Since conditional execution is so common, we allow for some syntactic sugar by `task( expression1, expression2, ... ) { ... }`.
where `expression1`, `expression2`, etc. are either boolean expressions or variable declarations.
The task is executed only if all `bool` expressions are `true`.

So the following programs are equivalent

```
shouldExec := true
if( shouldExec ) {
    task( cpus := 4 ) {
        sys echo RUNNING
    }
}
```

Is the same as:

```
shouldExec := true

task( shouldExec, cpus := 4 ) {
    sys echo RUNNING
}
```

**Note:** This feature is particularly useful when combined with the dependency operator `<-`

# Task re-execution

Imagine you have a `task` that given an input data (file `in := "in.txt"`), generates an output file (`out := "out.txt"`) but takes a long time to calculate (several minutes or hours).
A simple diagram would be:
```
            +------+
in.txt >----| Task |---> out.txt
            +------+                   
```

When the program is being re-executed, you might not need to re-run the task.
For example, if you've already run the `task` before, and the output file has already been created, then you don't need to process the input file again.
This is where the dependency operator (`<-`) is very useful.

See details in ["Dependency operator" section](dep_op.md).

**Note:** In complex data analysis pipelines, this situation happens very often.
That's why `bds` has mechanisms to address this issue.

A task conditional on a dependency operator, for example `task( out <- in ) ...`, will be executed only when you need to process the output data.
More strictly, the task will only be executed when the output file (`out`) either does not exist, or has a timestamp before the input file `in`. 

For instance, the following `task` will be executed only if `out.txt` needs to be updated with respect to `in.txt`:

```
in  := 'in.txt'
out := 'out.txt'

task( out <- in , cpus := 4 ) {
    sys echo "Creating output '$out'"
    sys echo $in > $out
}
```

If you execute the program for the first time, the task will run and the output `out.txt` will be created.
But if you execute it the second time, no task will be executed, because the output file already exists, and it doesn't need to be updated respect to the input file.

## Task dependencies

Using task dependencies (i.e. the dependency operator `<-` in a `task` statement), `bds` will internally create and resolve a directed acyclic graph (DAG) of tasks and will execute the tasks only when all the previous tasks have been succesfull.

For example, imagine you have three tasks where the inputs of the third task are the outputs of the other two tasks:

-   `task_1`: Has input file `in_1.txt`, and output file `out_1.txt`
-   `task_2`: Has input file `in_2.txt`, and output file `out_2.txt`
-   `task_3`: Has input files `out_1.txt`, and `out_2.txt`. Output file `out_3.txt`

Note that `task_3` cannot execute until both `task_1` and `task_2` are succesfully executed, because the inputs of `task_3` are the outpus of both `task_1` and `task_2`

In this case, the `bds` code would be something like this:

```
# Define input and output file names
in_1 := 'in_1.txt'
out_1 := 'out_1.txt'

in_2 := 'in_2.txt'
out_2 := 'out_2.txt'

out_3 := 'out_3.txt'

# Create input files for the exampl, if they don't exist
if( !in_1.exists() )    in_1.write("IN 1\n")
if( !in_2.exists() )    in_2.write("IN 2\n")

# Define tasks with their dependencies
task(out_1 <- in_1, taskName := 'task_1') {
    sys cat $in_1 > $out_1
    sys echo "TASK 1" >> $out_1
}

task(out_2 <- in_2, taskName := 'task_2') {
    sys cat $in_2 > $out_2
    sys echo "TASK 2" >> $out_2
}

# Note: "task_3" inputs are the output files from "task_1" and "task_2"
task(out_3 <- [out_1, out_2], taskName := 'task_3') {
    sys cat $out_1 >> $out_3
    sys cat $out_2 >> $out_3
    sys echo "TASK 3" >> $out_3
}
```

The task's DAG would be:

```
              +--------+
in_1.txt >----| Task 1 |---> out_1.txt >---+
              +--------+                   |
                                           |    +--------+
                                           +----| Task 3 |---> out_3.txt
                                           |    +--------+
              +--------+                   |
in_2.txt >----| Task 2 |---> out_2.txt >---+
              +--------+
```

Internally, `bds` will resolve the DAG to make sure all tasks are executed as soon as possible, but only when the dependecies succeeded.
So in this example, `task_1` and `task_2` will execute in parallel (assuming there are enough CPUs avaialble), once both of them finished succesfully, `task_3` will be executed.

## Remote dependencies

`bds` takes care to downloading and uploading remote dependencies for you.

A task's inputs and outputs dependencies can be remote files, for example files in AWS S3.
In that case, `bds` will add code to download the remote files, change the command lines to local files names, and then upload the results (if the outputs are remote).

For example, in the following code:

```
# You should change the "my_bucket" to a real bucket you have access to
in := "s3://my_bucket/tmp/in.txt"
out := "s3://my_bucket/tmp/out.txt"

# Note that 'cat' unix command cannot operate on S3 files
task(out <- in) {
    sys cat $in > $out
    sys echo "ADD DATA" >> $out
}
```

If you run this program, `bds` will:

1. Create a shell script for the `task`
1. Add code to download the remote input files at the beggining of the shell script
1. Replace all the input / output file names by the location where the files are downloaded (in a temp directory)
1. Add code to upload the remote output files at the end of the shell script
1. Execute the script

Let's run the script using something like `bds -log z.bds`, and now let's take a look at the shell script created by `bds` for the `task` (edited for readbility):

```
#!/bin/bash -eu
set -o pipefail
...

# Download commands
bds -download "s3://my_bucket/tmp/in.txt" "/tmp/bds/s3/my_bucket/tmp/in.txt"

# SYS command. line 6
cat /tmp/bds/s3/my_bucket/tmp/in.txt > /tmp/bds/s3/my_bucket/tmp/out.txt
# SYS command. line 7
echo "ADD DATA" >> /tmp/bds/s3/my_bucket/tmp/out.txt

# Upload commands
bds -upload "/tmp/bds/s3/my_bucket/tmp/out.txt" "s3://my_bucket/tmp/out.txt"
```

Note that:

-   There are download and upload commands (`bds -download ...` and `bds -upload ...`)
-   In the original `sys` command (`cat $in > $out`) the remote S3 files were replaced by the local file names.
    So instead of:

```
cat s3://my_bucket/tmp/in.txt > s3://my_bucket/tmp/out.txt
```

Obviously the unix command `cat` does not handle S3 file, so the command was changed to:

```
cat /tmp/bds/s3/my_bucket/tmp/in.txt > /tmp/bds/s3/my_bucket/tmp/out.txt
```

# Strict remote dependencies

In some cases, the commands in a `task` can handle remote files natively, so we don't want `bds` to handle those dependencies for us (e.g. do not download / upload remote files).

We can use the `task` option `strictRemoteDependencies`.
When set to `true`, the commands in the task will not be changed in any way (no files will be downloaded ot uploaded).

For example, in the follwowing program:

```
in := "s3://my_bucket/tmp/in.txt"
out := "s3://my_bucket/tmp/out.txt"

task(out <- in, strictRemoteDependencies := true) {
    sys echo Input file: $in
    sys echo Output file: $out
    sys aws s3 cp $in $out
}
```

Since `strictRemoteDependencies = true`, there is no remote file manipulation by `bds`.
So when we execute the program, we can see the paths are the original ones:

```
$ ./test/z.bds
Input file: s3://my_bucket/tmp/in.txt
Output file: s3://my_bucket/tmp/out.txt
copy: s3://my_bucket/tmp/in.txt to s3://my_bucket/tmp/out.txt
```

## Syntax sugar

There are many ways to write task expressions, here we show some examples.

-   A simple task

```
task echo RUNNING
```

-   The same simple task

```
task {
    sys echo RUNNING
}
```

-   A simple, multi-line task (a backslash at the end of the line continues in the next line, just like in a shell script)

```
task cat file.txt \
        | grep "^results" \
        | cut -f 2 \
        | sort \
        > out.txtx
```

-   A more complex multi-line task (`sys` commands are just multiple lines in a bash script)

```
task {
    sys cat file.txt | grep "^results" > out.txt
    sys cat other.txt | grep "^exclude" > words.txt
    sys grep -v -f words.txt out.txt > excluded.txt
    sys wc -l excluded.txt
}
```

-   A task with dependencies

```
task ( out <- in ) {
    sys cat $in | grep "^results" > $out
    sys cat other.txt | grep "^exclude" > words.txt
    sys grep -v -f words.txt $out > excluded.txt
    sys wc -l excluded.txt
}
```

-   A task with multiple inputs and outputs dependencies

```
task ( [out1, out2] <- [in1, in2] ) {
    sys cat $in1 | grep "^results" > $out1
    sys cat $in1 $in2 | wc -l > $out2
}
```

-   A task with multiple inputs and outputs dependencies, using 4 CPUs and declaring a local variable 'tmp'

```
task ( [out1, out2] <- [in1, in2] , cpus := 4 , tmp := "$in1.tmp" ) {
    sys cat $in1 | grep "^results" > $out1
    sys cat $in1 $in2 > $tmp
    sys wc -l $tmp | wc -l > $out2
}
```

-   A task with a label (`taskName`) is easier to find in the report

```
task ( out <- in, cpus := 4 , taskName := "Filter results" ) {
    sys cat $in | grep "^results" > $out
}
```

