# Turkish-Inspired Programming Language

![Java](https://img.shields.io/badge/Java-21+-orange)
![Status](https://img.shields.io/badge/Status-In%20Progress-blue)
![Parser](https://img.shields.io/badge/Parser-Recursive%20Descent-green)
![Course](https://img.shields.io/badge/Course-Programming%20Languages-purple)



## Course Information 

- **Course:** Programming Language
- **Project:** Term Project
- **Implementation Language:** Java


## Overview
This project is a mini programming language inspired by Turkish syntax 
and keywords.
The goal is to design and implement 
the core front-end components of a compiler.

## Implemented Components
- Lexical Analyzer (Lexer)
- Recursive Descent Parser
- Symbol Table
- Syntax Error Detection
- Test Case Execution with Sample Source Files

## Language Features
- Variable Declarations
- Assignment Statements
- Arithmetic Expressions
- Conditional Statements (`if / else`)
- Loop Structures (`while`)
- Block Scope with Braces

## Project Structure
```text
src/
 ├── Main.java
 ├── Lexer.java
 ├── Parser.java
 ├── Token.java
 ├── TokenType.java
 └── SymbolTable.java

tests/
 ├── ArithmeticExample.txt
 ├── ConditionalExample.txt
 ├── LoopExample.txt
 └── VariableDeclarationExample.txt
```

## Sample Programs
Sample source codes are located in the `tests/` folder.

## How to Run
```bash
javac src/*.java
java -cp src Main
```

## Example Syntax
```text
tam x;
x = 5 + 3;

eger (x > 5) {
   yaz x;
}
```

## UML Architecture Diagram
```text
+-------------------+
|      Main.java    |
| Program Entry     |
+---------+---------+
          |
          v
+-------------------+
|     Lexer.java    |
| Source Scanner    |
+---------+---------+
          |
          v
+-------------------+
|     Token.java    |
| Token Objects     |
+---------+---------+
          |
          v
+-------------------+
|    Parser.java    |
| Syntax Analyzer   |
+----+---------+----+
     |         |
     v         v
+---------+  +------------------+
|TokenType|  | SymbolTable.java |
| Enum    |  | Variable Storage |
+---------+  +------------------+
```

## Component Responsibilities
- **Main.java** → Starts program execution and loads input files.
- **Lexer.java** → Converts source code into tokens.
- **Token.java** → Stores token type and value.
- **TokenType.java** → Defines all token categories using enum.
- **Parser.java** → Validates syntax using Recursive Descent Parsing.
- **SymbolTable.java** → Stores declared identifiers and metadata.


## Development Status
**In Progress** 🚧

## Authors
Programming Languages Course Project Team