# Wait 

Task coordination mechanisms rely on waiting for some tasks to finish before starting new ones.

## Tasks are asynchronous

A key concept, is that tasks are asynchronous, which means that task execution order is not guaranteed.

For example, executing the following program (file <a href="bds/test_13.bds">test_13.bds</a>)
```
for( int i=0 ; i < 10 ; i++ ) task echo BEFORE $i
for( int i=0 ; i < 10 ; i++ ) task echo AFTER $i
```
you may see:
```
$ ./test_13.bds
BEFORE 0
BEFORE 4    <-- Notice, tasks are out of order
BEFORE 3
BEFORE 2
BEFORE 1
BEFORE 5
BEFORE 7
BEFORE 6
BEFORE 8
AFTER 1
AFTER 0
BEFORE 9    <-- Notice this 'BEFORE' task is executed after 'AFTER' task started 
AFTER 6
AFTER 5
AFTER 4
AFTER 3
AFTER 2
AFTER 7
AFTER 8
AFTER 9
```

This is because the `task` statement only schedules a task to be executed, but it's up to scheduler to decide when to execute the task.
Schedulers can, and often do, re-order the tasks to be executed.

## Waiting for tasks

If we need some kind of "barrier" to wait for tasks, we use the `wait` statement

If a task must be executed after another task finishes, we can introduce a `wait` statement.
File <a href="bds/test_13.bds">test_13.bds</a>
```
for( int i=0 ; i < 10 ; i++ ) task echo BEFORE $i

wait    # Wait until ALL scheduled tasks finish
print("We are done waiting, continue...\n")

for( int i=0 ; i < 10 ; i++ ) task echo AFTER $i
```

Now, we are sure that all tasks 'AFTER' really run after 'BEFORE'
```
$ ./test_14.bds 
BEFORE 0
BEFORE 2
BEFORE 1
BEFORE 4
BEFORE 7
BEFORE 3
BEFORE 6
BEFORE 5
BEFORE 8
BEFORE 9
We are done waiting, continue...
AFTER 0
AFTER 2
AFTER 4
AFTER 1
AFTER 3
AFTER 5
AFTER 6
AFTER 8
AFTER 7
AFTER 9
```

### Waiting for one task to finish

We can also wait for a specific task to finish by providing a task ID `wait taskId`, e.g.:
```
string tid = task echo Hi
wait tid	# Wait only for one task
```

### Waiting for a list of tasks to finish

You can `wait` for a list of tasks by providing a list of `taskIds`. 

For instance, in this program, we create a list of two task IDs and `wait` on the list:
```
string[] tids

for( int i=0 ; i < 10 ; i++ ) {
	# Tasks that wait a random amount of time
	int sleepTime = randInt( 5 )
	string tid = task echo BEFORE $i ; sleep $sleepTime ; echo DONE $i

	# We only want to wait for the first two tasks
	if( i < 2 ) tids.add(tid)
}

# Wait for all tasks in the lists (only the first two tasks)
wait tids
print("End of wait\n")
```

When we run it, we get:
```
$ bds z.bds
BEFORE 2
BEFORE 0
BEFORE 7
BEFORE 5
BEFORE 6
BEFORE 4
BEFORE 3
BEFORE 1
DONE 0          <- First task finished
DONE 3
DONE 4
DONE 5
DONE 6
DONE 7
BEFORE 8
BEFORE 9
DONE 1          <- Second task finished
End of wait     <- Wait finished here: we were waiting for the first two tasks
DONE 2
DONE 8
DONE 9
```

**Note:** There is an implicit `wait` statement at the end of the program. 
So a program does not exit until all tasks have finished running.

### Waiting for ALL tasks to finish

A single `wait` statement without any arguments, will `wait` for ALL tasks to finish.

For example:
```
for( int i=0 ; i < 10 ; i++ ) task echo BEFORE $i
wait  # This will wait for ALL tasks
println "All tasks finished!"
```

### Implicit wait statement

What happens if there are tasks runnign, but there is not `wait` statement?

For example, in the following program there is a long-running task, but there is no `wait` statement
```
task echo "TASK START"; sleep 5; echo "TASK END"
println "All tasks scheduled, program end!"
```

So the program execution will finish before the `task` finishes running.
What happens with the `task` that are still executing when `bds` finishes executing the program?
`bds` introduces an implicit `wait` statement at the end of every program.

If you execute the program above from the command line, you'll see:
```
$ bds test/z.bds
All tasks scheduled, program end!
TASK START
TASK END
$            <- Note that the command line prompt is AFTER the task finished
```
In the above example, the implicit `wait` allows `bds` to let the tasks finish.

## Task failure

What happens when a `task` fails?
When a task fails, a `WaitException` error is raised.

We need to remember that the `task` command does NOT actually execute a task, it only schedules the `task`.
So, where is the exception thrown?
When a task fails, the `WaitException` is thrown in any `wait` statement that waiting for tasks to finish. 

For example, the following code shows where the `WaitException` is thrown:

```
# This task will fail because "my_unknown_command" does not exist
task my_unknown_command input.txt > output.txt

wait  # A WaitException will be thrown here because the task fails

println "This will never be executed!"
```

If you execute the code, the output is (output edited for readability):
```
$ bds task_28.bds
... line 7: my_unknown_command: command not found
...
Fatal error: task_28.bds. WaitException thrown: Error in wait statement, file task_28.bds, line 5
```
### Task failure on implicit `wait`

What happens if there is a task error, but there is no `wait` statements (i.e. the program has finished executing)?
In that case an `error` will be produced by the [implicit wait statement](#implicit-wait-statement) that `bds` introduces after every program.

There is no point in throwing an exception during an "implicit wait", because this is out of the program's code, so there is no way to catch exceptions from "implicit wait statements".

Example:
```
# This task will fail because "my_unknown_command" does not exist
# Note: It will fail at the "implicit wait" added by bds at the end of the program.
# Since the implicit 'wait' is after the program's end, there is no exception
# thrown, only an error.
task my_unknown_command
```

executing the above code, you get ():
```
$ bds task_29.bds
task.task_29.line_4.id_1.35e61e0f12d941dd.sh: line 7: my_unknown_command: command not found
00:00:00.632	ERROR: Task failed:
	Program & line     : 'task_29.bds', line 4
	Task Name          : 'null'
	Task ID            : 'task_29.bds.20221213_071909_868/task.task_29.line_4.id_1.35e61e0f12d941dd'
	Task PID           : '84742'
	Task hint          : 'my_unknown_command'
	Task resources     : 'cpus: 1	mem: -1.0 B	timeout: 86400	wall-timeout: 86400'
	State              : 'ERROR'
	Dependency state   : 'ERROR'
	Retries available  : '1 / 0'
	Retries available  : '1'
	Input files        : '[]'
	Output files       : '[]'
	Script file        : 'task_29.bds.20221213_071909_868/task.task_29.line_4.id_1.35e61e0f12d941dd.sh'
	Exit status        : '1'
	StdErr (10 lines)  :
		task_29.bds.20221213_071909_868/task.task_29.line_4.id_1.35e61e0f12d941dd.sh: line 7: my_unknown_command: command not found
```

As shown above, an error is produces, instead of an exception, i.e. there is no " WaitException thrown" message. 

### Catching wait exceptions

Since `wait` statements throws an exception when the `task` fails, it is possible to handle task failures using `try` / `catch` / `finally` blocks.

For example:
```
captured := false

try {
    # This task will fail because "my_unknown_command" does not exist
    task my_unknown_command

    # Wait exception has to be inside the 'try' clause because
    # this is where the exception is thrown in case of errors
    wait
    
    println "This will NOT be executed"  # Code after exception is thrown
} catch( WaitException e) {
    captured = true
    println "Wait exception captured"
}

println "This WILL be executed: captured = $captured"
exit 0  # Force exit code, otherwise bds returns non-zero on task failure
```

Executing the code, will output (edited for readibility):
```
$ bds task_31.bds

task.task_31.line_6.id_1.sh: line 7: my_unknown_command: command not found
00:00:00.691	ERROR: Task failed:
	Program & line     : 'task_31.bds', line 6
	Task Name          : 'null'
	Task ID            : 'task.task_31.line_6.id_1.3e45862489d7bb51'
	Task PID           : '90485'
	Task hint          : 'my_unknown_command'
	Task resources     : 'cpus: 1	mem: -1.0 B	timeout: 86400	wall-timeout: 86400'
...
Wait exception captured                   <- This message is from the 'catch' code
This will be executed: captured = true    <- This code would not be executed if we didn't cature the WaitException
...
```

**IMPORTANT:** `bds` will produce a non-zero `exitCode` when a task fails.
If you want your program to return a zero `exitCode` to the command line when you've handled the `WaitException`, you need to explicitly add an `exit` statement.

If we look at the previous program's exit code:
```
# In bash, the '$?' variable shows the exit code of the
# previouly executed command (which was the bds program)

$ echo "Exit code: $?"
Exit code: 0
```

So, we were able to `catch` the WaitException produce as an error in the `task`, and handle the exception and program's `exitCode`.
