<h1 align="center">shell-java</h1>
 
<p align="center"><strong>A custom POSIX-style shell implementation in Java, built from scratch.</strong></p>
<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white" alt="Java 17+">
  <img src="https://img.shields.io/badge/Maven-3.6+-blue?logo=apachemaven&logoColor=white" alt="Maven 3.6+">
  <img src="https://img.shields.io/badge/Version-1.0.0-green" alt="Version 1.0.0">
  <img src="https://img.shields.io/badge/License-MIT-blue" alt="License: MIT">
  <img src="https://img.shields.io/badge/Tests-passing-brightgreen" alt="Tests passing">
</p>
<p align="center">
  Builtins · I/O redirection · pipelines · job control · history · variable expansion · aliases · tab completion
</p>
<p align="center">
  <a href="#features">Features</a> ·
  <a href="#requirements">Requirements</a> ·
  <a href="#building">Building</a> ·
  <a href="#usage-examples">Usage</a> ·
  <a href="#architecture">Architecture</a> ·
  <a href="#testing">Testing</a> ·
  <a href="#releases">Releases</a> ·
  <a href="#license">License</a>
</p>

---

A custom POSIX-style shell implementation in Java, built from scratch with support for
builtin commands, I/O redirection, pipelines, tab completion, job control, command history,
variable expansion, and aliases.

## Features

### Builtin Commands
| Command    | Description                                        |
|------------|----------------------------------------------------|
| `echo`     | Print arguments to stdout                          |
| `pwd`      | Print current working directory                    |
| `cd`       | Change directory                                   |
| `ls`       | List directory contents                            |
| `mkdir`    | Create directories                                 |
| `rmdir`    | Remove empty directories                           |
| `touch`    | Create files or update timestamps                  |
| `cat`      | Display file contents or read from stdin           |
| `type`     | Identify command type (builtin or PATH executable) |
| `exit`     | Exit the shell                                     |
| `history`  | Display command history                            |
| `fc`       | List or rerun history entries                      |
| `clear`    | Clear the terminal screen                          |
| `alias`    | Set or list command aliases                        |
| `unalias`  | Remove aliases                                     |
| `declare`  | Set or list variables                              |
| `export`   | Export variables to environment                    |
| `unset`    | Remove variables                                   |
| `jobs`     | List background jobs                               |
| `complete` | Register custom tab completion scripts             |

### Shell Features
- **I/O Redirection** — `>`, `>>`, `2>`, `2>>`
- **Pipelines** — `cmd1 | cmd2 | cmd3`
- **Background execution** — `command &`
- **Variable expansion** — `$VAR`, `${VAR}`, `$?`, `$$`, `$!`
- **Variable assignment** — `VAR=value`
- **History expansion** — `!!`, `!n`, `!prefix`
- **Tab completion** — builtins, PATH executables, files, directories, aliases
- **Command history** — persistent via `~/.shell_java_history`
- **Aliases** — recursive expansion with self-reference protection
- **Quote handling** — single quotes, double quotes, backslash escaping

### Environment Variables
| Variable      | Description                                                               |
|---------------|---------------------------------------------------------------------------|
| `HISTFILE`    | Custom history file path (default: `~/.shell_java_history`)               |
| `HISTSIZE`    | Maximum number of history entries (default: 1000)                         |
| `HISTCONTROL` | History filtering: `ignoredups`, `ignorespace`, `erasedups`, `ignoreboth` |
| `PATH`        | Directories to search for executables                                     |
| `PATHEXT`     | Executable extensions on Windows (e.g. `.exe;.bat;.cmd`)                  |

## Requirements

- Java 17+
- Maven 3.6+

## Building

```bash
mvn package
```

This produces `target/shell-java-<version>.jar`.

## Running

```bash
java -jar target/shell-java-1.0.0.jar
```

## Usage Examples

```bash
# Basic commands
$ echo "hello world"
hello world

$ ls
src  target  pom.xml

# I/O redirection
$ echo hello > out.txt
$ cat out.txt
hello

$ echo world >> out.txt
$ cat out.txt
hello
world

# Pipelines
$ echo hello world | cat
hello world

# Variable expansion
$ NAME=world
$ echo "hello $NAME"
hello world

$ echo $?
0

# Background jobs
$ ping -n 10 127.0.0.1 &
[1] 12345
$ jobs
[1]+  Running                 ping -n 10 127.0.0.1 &

# Aliases
$ alias ll='ls'
$ ll
src  target  pom.xml

# History
$ history
    1  echo hello
    2  ls
    3  history

$ !!
history
```

## Architecture

```
src/main/java/
├── Main.java
├── Shell.java
├── alias/
│   └── AliasManager.java
├── command/
│   ├── CommandRegistry.java
│   ├── Executable.java
│   ├── ExternalCommand.java
│   ├── ProcessBuilderFactory.java
│   └── builtin/
│       ├── Builtin.java
│       ├── BuiltinFactory.java
│       ├── AliasBuiltin.java
│       ├── CatBuiltin.java
│       ├── CdBuiltin.java
│       ├── ClearBuiltin.java
│       ├── CompleteBuiltin.java
│       ├── DeclareBuiltin.java
│       ├── EchoBuiltin.java
│       ├── ExitBuiltin.java
│       ├── ExportBuiltin.java
│       ├── FcBuiltin.java
│       ├── HistoryBuiltin.java
│       ├── JobsBuiltin.java
│       ├── LsBuiltin.java
│       ├── MkdirBuiltin.java
│       ├── PwdBuiltin.java
│       ├── RmdirBuiltin.java
│       ├── TouchBuiltin.java
│       ├── TypeBuiltin.java
│       ├── UnaliasBuiltin.java
│       └── UnsetBuiltin.java
├── completer/
│   ├── CandidateCollector.java
│   └── SystemBinaryCompleter.java
├── history/
│   └── HistoryManager.java
├── jobs/
│   ├── Job.java
│   ├── JobManager.java
│   └── JobStatus.java
├── pipe/
│   ├── Pipeline.java
│   └── PipelineResult.java
├── redirect/
│   ├── OutputWriter.java
│   ├── Redirect.java
│   └── RedirectType.java
└── util/
    ├── Environment.java
    ├── ParseResult.java
    ├── PathResolver.java
    ├── Tokenizer.java
    └── UnterminatedQuoteException.java
```

## Testing

```bash
mvn test
```

Unit tests cover:
- `Tokenizer` — quoting, escaping, pipes, variable expansion, redirects
- `PathResolver` — PATH scanning, PATHEXT extension matching
- `HistoryManager` — HISTSIZE, HISTCONTROL, persistence
- `JobManager` — job tracking, reaping, numbering
- `AliasManager` — expansion, recursion, self-reference protection
- `Environment` — variable management, exit codes
- `OutputWriter` — stdout, file, append, stderr redirection
- `CommandRegistry` — builtin registration and lookup

## Releases

| Version  | Features                                                                                |
|----------|-----------------------------------------------------------------------------------------|
| `v0.1.0` | REPL, basic builtins (`exit`, `echo`, `pwd`, `cd`, `type`)                              |
| `v0.2.0` | File operations (`ls`, `mkdir`, `rmdir`, `touch`, `cat`), quoting                       |
| `v0.3.0` | I/O redirection (`>`, `>>`, `2>`, `2>>`)                                                |
| `v0.4.0` | Tab completion with JLine3, `complete` builtin                                          |
| `v0.5.0` | Job control (`&`, `jobs`)                                                               |
| `v0.6.0` | Command history (`history`, `fc`, `clear`, `HISTFILE`, `HISTSIZE`, `HISTCONTROL`)       |
| `v0.7.0` | Pipes (`\|`), variable expansion (`$VAR`, `$?`, `$$`, `$!`), `declare`/`export`/`unset` |
| `v1.0.0` | Aliases (`alias`/`unalias`), unit tests                                                 |

## License

MIT — see [LICENSE](LICENSE) for details.
