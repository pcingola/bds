# Documentation Review Checklist

## Typo fixes

- [ ] `download.md`: Fix "directoy", "direcotory", "Maual", "complile"
- [ ] `about.md`: Fix "commnand" -> "command"
- [ ] `language.md`: Fix "maner" -> "manner", "exxecution" -> "execution"
- [ ] `data_types.md`: Fix `** Methods **` formatting (two places), fix broken table pipe in Maps type row
- [ ] `task.md`: Fix ~10 typos: "Acyclik", "succesfully", "avaialble", "dependecies", "beggining", "readbility", "toghether", "comemnts", "usful", "Reosurces" (x2)
- [ ] `task_aws.md`: Fix "tataskResources" -> "taskResources" (4 occurrences), "quque" -> "queue"
- [ ] `bdsconfig.md`: Remove stray `</table>` tag at end of file
- [ ] `goal.md`: Fix "nescesary" -> "necessary"
- [ ] `reports.md`: Fix "injested", "whihc", "usefull", "Coverred"
- [ ] `bds_vm.md`: Fix "serializetion", "corresponsing", "corrently"
- [ ] `logging.md`: Fix "pourposes", "usefull"
- [ ] `global_vars.md`: Fix missing backtick on `args` row

## Incorrect technical content

- [ ] `download.md`: Runtime requirement says "Java 12" but compile requirement also says "Java 12" -- consistent but verify this is current
- [ ] `bdsconfig.md`: First sentence says "The `config.bds` file" but the actual file is `bds.config`
- [ ] `data_types.md`: Task functions documented as `getTasksDone()`, `getTasksRunning()`, `getTasksToRun()` but the actual names are `tasksDone()`, `tasksRunning()`, `tasksToRun()`
- [ ] `task_resources.md`: Function name shown as `addResources()` should be `addResource()` (singular)
- [ ] `hello.md`: Example output for test_02.bds shows `hello world` (lowercase) but `sys echo Hello world` produces `Hello world` (capital H)
- [ ] `reports.md`: Says reports created "when run using `-log`" but `-reportHtml` is true by default. Clarify reports are created by default, `-log` controls whether log files are kept
- [ ] `language.md`: Ternary operator description has broken backtick: "If `cond` is `false, it evalautes..."
- [ ] `index.md`: Uses `&amp;` HTML entity that won't render in markdown
- [ ] `tutorial.md`: Title has "Introduxtion" instead of "Introduction"
- [ ] `classes.md`: "pipelenes" -> "pipelines", "Has you may expect" -> "As you may expect"
- [ ] `bdsconfig.md`: SSH section says "shh" instead of "ssh"

## Content improvements

- [ ] `classes.md`: Very thin -- only covers fields, constructors, basic inheritance. Add: methods, `this` keyword, method overriding
- [ ] `index.md`: Update "experimental and under heavy development" disclaimer -- this is version 3.6
- [ ] `download.md`: Replace outdated manual `virtualenv`/`mkdocs` setup section with reference to `scripts/make_documentation.sh`
- [ ] `pipelines.md`: Heavily duplicates content word-for-word from `task.md`, `dep_op.md`, `wait.md`, `goal.md` -- trim and link instead

## Broken links

- [ ] `hello.md`: Dead `<a href="bds/test_XX.bds">` links -- files don't exist in repo
- [ ] `test_cases.md`: Dead link to `bds/test_24.bds`
- [ ] Multiple files (`dep_op.md`, `wait.md`, `par.md`, `goal.md`, `cleanup.md`, etc.): Dead `<a href="bds/test_XX.bds">` links throughout
