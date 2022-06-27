# Resources

Task resources refer to CPUs and memory consumed by each `task`.
Whenever a `task` is defined, we should set the resources used by the task, so that bds can correctly account for them. 
This allows bds to correctly schedule each `task` when the resources are available.

**Example:**

For instance, imagine that you have a laptop with 8 CPUs and 16G of memory, and you want to run five tasks which consume 8 CPUs and 2G of memory each: 
```
for(int i=0; i < 5; i++ ) {
    task (cpus := 8, mem := 2 * G) sys my_command_cpus param_1 param_2
}   
```
In this example `bds` will run only one task at a time, because each `task` consumes all available CPUs (the limitation in not on the memory).

Similarly, in the same laptop with 8 CPUs and 16G of RAM, if you want to run these tasks, each consuming 8G of memory:
```
for(int i=0; i < 10; i++ ) {
    task (mem := 8 * G) sys my_command_mem param_1 param_2
}
```
In this case `bds` will run two tasks in parallel, because each `task` consumes half the available memory.

**Clusters:** If these tasks are running in a cluster environment, each task would be issued with the appropriate CPUs and memory parameters, so that the cluster system can schedule the job in an appropriate node (keep in mind tha most cluster systems will kill task that consume more resources that the ones declared).

### Basic resources

The basic resources you can add to a task are:

- `cpus`: Number of CPUs / cores consumed by the task. The default is `cpus := 1`
- `mem`: Amount of memory used by the task. The default value is `mem := -1`, which doesn't use the resource (e.g. in a cluster system it will use the cluster's default setting).

### Custom reosurces

There are other resources than `cpus` and `mem` that you might need to define.
Typical examples are special hardware accelerator cards (GPUs, TPUs, FFPGA, etc.).
So `bds` provides a generic mechanism to define them using the `addResources()` built-in function.

# Adding a new resource

The function `addResource(...)` lets you defined a new resource.

```
addResources(resourceName, count):
```
- `resourceName`: A string defining a resource name (e.g. 'gpus'). This string MUST be a valid name.
- `count`: The number of resources available (an integer number).

How it works:
- When running the `addResurces`, a number of `count` new resources will be attached to the current execution system (as defined by the current value of `system` variable (by default is `system := 'local'`)
- A global variable with the same name as `resourceName` and type `int` is created, so it can be used in `task` statements (the variable is set to zero, so by default all tasks consume zero of these new resources)  

**Example:** Adding 8 GPUs (to the local system) and using them in a task that consumes 1 GPU and 2 CPUs
```
addResource('gpus', 8)  # Here 8 gpus are defined
                        # Also a global variable is created `gpus := 0`

for(int i=0; i < 5; i++ ) {
    # Each task consumes 1 GPU and 2 CPUs
    task (gpus := 1, cpus := 2 ) sys my_gpu_command parameter_1 parameter_2 
}
```


