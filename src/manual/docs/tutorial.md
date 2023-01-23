# `bds` tutorial

## Introduxtion

**What is `bds`?**

`bds` is a simple, yet powerful, scripting language for parallel and distributed computing.
It is designed to be easy to learn and use, and to be very efficient. It is used for running large scale bioinformatics pipelines, but it can be used for any kind of parallel and distributed computing.

**Motivation**

The analysis of large biological datasets often requires complex processing pipelines that run for a long time on large computational infrastructures.
We designed and implemented a simple script-like programming language with a clean and minimalist syntax to develop and manage pipeline execution and provide robustness to various types of software and hardware failures as well as portability.

`bds` programming language for data processing pipelines, which improves abstraction from hardware resources and assists with robustness. Hardware abstraction allows `bds` pipelines to run without modification on a wide range of computer architectures, from a small laptop to multi-core servers, server farms, clusters and clouds.

`bds` achieves robustness by incorporating the concepts of absolute serialization and lazy processing, thus allowing pipelines to recover from errors.

By abstracting pipeline concepts at programming language level, `bds` simplifies implementation, execution and management of complex bioinformatics pipelines, resulting in reduced development and debugging cycles as well as cleaner code.

**Faster development cycle**

In our experience, using general-purpose programming languages to develop pipelines is notably slow owing to many architecture-specific details the programmer has to deal with.
Using an architecture agnostic language means that the pipeline can be developed and debugged on a regular laptop using a small sample dataset and deployed to a cluster to process large datasets without any code changes.
This significantly reduces the time and effort required for development cycles.

-   **Reduced development time** Spend less time debugging your work on big systems with a huge data volumes. Now you can debug the same jobs using a smaller sample on your computer. Get immediate feedback, debug, fix and deploy when it's done. Shorter development cycles means better software.
-   **System independent** Cross-system, seamless execution, the same program runs on a laptop, server, server farm, cluster or cloud. No changes to the program required. Work once.
-   **Easy to learn** The syntax is intuitive and it resembles the syntax of most commonly used programming languages. Reading the code is easy as pi.
-   **Automatic Checkpointing** If any task fails to execute, bds creates a checkpoint file, serializing all the information from the program. Want to restart were it stopped? No problem, just resume the execution from the checkpoint.
-   **Automatic logging** Everything is logged (`-log` command line option), no explicit actions required. Every time you execute a system command or a task, bds logs the executed commands, stdout &amp; stderr and exit codes.
-   **Clean stop with no mess behind** You have a bds running on a terminal and suddenly you realized there is something wrong... Just hit Ctrl-C. All scheduled tasks and running jobs will be terminated, removed from the queue, deallocated from the cluster. A clean stop allows you to focus on the problem at hand without having to worry about restoring a clean state.
-   **Task dependencies** In complex pipelines, tasks usually depend on each other. bds provides ways to easily manage task dependencies.
-   **Avoid re-work** Executing the pipeline over and over should not re-do jobs that were completed successfully and moreover are time consuming. Task dependency based on timestamps is a built-in functionality, thus making it easy to avoid starting from scratch every time.
-   **Built in debugger** Debugging is an integral part of programming, so it is part of `bds` language. Statements `breakpoint` and `debug` make debugging part of the language, instead of requiring platform specific tools.
-   **Built in test cases facility** Code testing is performed in everyday programming, so testing is built in `bds`.

## Tutorial

-

3. Tips and tricks for debugging: html report, sh scripts, state points, etc..
4. Demux [Sergey]
