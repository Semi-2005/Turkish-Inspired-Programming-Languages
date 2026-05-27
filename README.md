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

*Run* Main.java

## Example Syntax
```text
degisken x;
x = 5 + 3;

eger (x > 5) {
   yazdir(x);
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


## Sample Programs and Test Cases

Sample source codes and test files are located in the `tests/` folder. The project includes both **valid** test cases to demonstrate correct language syntax and **invalid** test cases to verify the error-handling capabilities of the Lexer, Parser, and Symbol Table.

### ✅ Valid Test Files
These files contain correct syntax, obey the language rules, and should be processed by the compiler without any errors:
- `ArithmeticExample.txt`: Demonstrates mathematical operations and assignments.
- `ConditionalExample.txt`: Demonstrates `eger` / `degilse` (if/else) logic, reversed parentheses `)(`, and block scoping `[[ ]]`.
- `LoopExample.txt`: Demonstrates the `dongu` (while) structure.
- `VariableDeclarationExample.txt`: Demonstrates variable definitions with language-specific data types (`tam`, `ondlk`, `cml`, etc.).

### ❌ Invalid Test Files
These files contain intentional lexical, syntactic, or semantic errors to test if the compiler correctly catches and reports them:
- `InvalidVariableDeclarationExample.txt`: Tests *Duplicate* variable declarations and *Undefined* variable assignments (Triggers SymbolTable exceptions).
- `InvalidConditionalExample.txt`: Tests incorrect block braces (e.g., `{` instead of `[[`), traditional parentheses `()`, and invalid comparison operators.
- `InvalidLoopExample.txt`: Tests unclosed string literals and missing end-of-statement markers (`:`).
- `InvalidArithmeticExample.txt`: Tests unrecognized characters (e.g., `^`) and incorrect assignment operators (Triggers Lexer errors).



## Development Status
**Complated** 

## Contributors
Programming Languages Course Project Team:
Semi Kazar      - 230316066
Ufuk Akkuzu     - 230316049
Berat Uzdil     - 230316043
Berkay Altunbag - 230316009