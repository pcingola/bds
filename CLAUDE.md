# BDS - BigDataScript

A scripting language for big data pipeline orchestration. Programs run seamlessly on laptops, servers, clusters (SGE, SLURM, PBS, MOAB), and cloud (AWS). Version 3.6.

Author: Pablo Cingolani. Repository: https://github.com/pcingola/bds

## Project overview

BDS is a statically typed language with a custom virtual machine (bdsVm, a stack machine) that supports full program serialization (checkpoints). The language provides task scheduling, dependency management (DAG-based), parallel execution, and automatic resource management across different compute environments.

The final `bds` binary is a Go executable concatenated with a Java JAR file. The Go layer handles OS-level operations (process management, signal handling), and the Java layer implements the compiler, VM, and runtime.

## Architecture

The codebase is Java (554 source files) under `src/org/bds/` with a Go wrapper under `go/bds/`.

Key packages:
- `lang/` -- Language AST nodes: expressions, statements, types, values, native classes/functions/methods
- `compile/` -- Compiler (ANTLR4-based parser + type checker + bytecode generator)
- `vm/` -- bdsVm virtual machine (stack-based bytecode interpreter)
- `executioner/` -- Task executioners for different systems (local, cluster, cloud, SSH)
- `cluster/` -- Cluster management, host resources, health monitoring
- `data/` -- Data access layer (local files, S3, HTTP, FTP, SFTP)
- `task/` -- Task lifecycle management
- `run/` -- Runtime execution orchestration
- `report/` -- HTML/YAML report generation
- `scope/`, `symbol/` -- Variable scoping and symbol tables
- `antlr/` -- Generated ANTLR4 lexer/parser (from `grammars/bds.g4` and `grammars/bdsLexerRules.g4`)
- `languageServer/` -- LSP implementation for IDE support
- `osCmd/` -- OS command execution (local, SSH, AWS)

Entry point: `org.bds.Bds.main()` in `src/org/bds/Bds.java`.

## Build

Prerequisites: Java 12+, Maven, Ant, Go 1.16+.

```
# Full build (Maven deps + Ant compile + Go build + combine binary)
scripts/make.sh

# Install to ~/.bds
scripts/install.sh
```

The build process: `mvn dependency:copy-dependencies` -> `ant` (compiles Java + builds JAR) -> `scripts/make_go.sh` (builds Go binary) -> concatenate Go binary + JAR into final `build/bds` executable.

## Tests

Tests use JUnit 4. Test suites are in `src/org/bds/test/`:
- `TestSuiteUnit.java` -- Unit tests (~22 test classes covering language, VM, tasks, functions, etc.)
- `TestSuiteIntegration.java` -- Integration tests (checkpoints, clusters, remote data, etc.)

Test data files (`.bds` scripts) are in `test/`.

There is no test runner script in `scripts/`. Tests are run via Maven or IDE.

## Configuration

- Runtime config: `~/.bds/bds.config` (system type, cluster settings, resource defaults)
- Grammar files: `grammars/bds.g4`, `grammars/bdsLexerRules.g4`
- BDS standard library: `src/resources/libraries/stdlib.bds`
- Report templates: `src/resources/templates/`

## Documentation

Source documentation is in `src/manual/docs/` (35 markdown files). Built documentation is in `docs/`. Build docs with `scripts/make_documentation.sh`.

## CI

GitHub Actions workflow (`.github/workflows/ant.yml`) builds for Linux and macOS, triggered manually. It compiles Java with Ant, builds Go, concatenates the binary, and creates a release with tarballs.
