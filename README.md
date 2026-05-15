# Autonomous Code Maintenance System

## Overview

Autonomous Code Maintenance System is an AI-driven remediation platform that:

* Detects relevant faulty code from logs
* Performs semantic code search using embeddings + PGVector
* Uses LLM-powered root-cause analysis
* Applies AST-safe patches
* Validates runtime behavior
* Triggers CI/CD validation
* Creates GitHub Pull Requests automatically
* Assigns reviewers dynamically
* Supports future multi-language extensibility

The system is currently implemented for Java and designed to scale for Python, Go, and additional languages through a plugin-based architecture.

---

# Core Features

## Current Features (Java)

* Log-based semantic code retrieval
* Cohere embeddings integration
* PGVector similarity search
* AST-based Java patching using JavaParser
* GitHub branch automation
* Automatic Pull Request generation
* Reviewer assignment
* Runtime validation inside Docker sandbox
* GitHub Actions compile validation
* Retry + rollback support
* Structured workflow engine

---

# High-Level Architecture

```text
                           ┌────────────────────┐
                           │   REST API Layer   │
                           │ AnalysisController │
                           └─────────┬──────────┘
                                     │
                                     ▼
                    ┌────────────────────────────────┐
                    │      Workflow Engine           │
                    │ AutoFixWorkflowEngine          │
                    └────────────────┬───────────────┘
                                     │
         ┌───────────────────────────┼────────────────────────────┐
         ▼                           ▼                            ▼
┌─────────────────┐      ┌────────────────────┐      ┌────────────────────┐
│ Workflow Stages │      │ Rollback Manager   │      │ Retry Strategy     │
│                 │      │                    │      │                    │
│ SearchCodeStage │      │ Delete Branch      │      │ RetryPolicy        │
│ AnalyzeLogs     │      │ Rollback           │      │                    │
│ CreateBranch    │      └────────────────────┘      └────────────────────┘
│ ApplyPatch      │
│ RuntimeValidate │
│ CompileValidate │
│ PullRequest     │
└────────┬────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│      Language Plugin Factory             │
└────────────────┬─────────────────────────┘
                 │
     ┌───────────┼────────────┬──────────────┐
     ▼           ▼            ▼              ▼
┌────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│ Java   │ │ Python   │ │ Go       │ │ Future   │
│ Plugin │ │ Plugin   │ │ Plugin   │ │ Plugins  │
└────┬───┘ └────┬─────┘ └────┬─────┘ └──────────┘
     │           │             │
     ▼           ▼             ▼
 AST Patch   Python AST     Go Parser
 Maven CI    Pytest CI      Go Test
 Runtime     Runtime        Runtime
 Validator   Validator      Validator

```

---

# Workflow Execution Flow

```text
Logs
  ↓
Semantic Embedding Generation
  ↓
PGVector Similarity Search
  ↓
Relevant File + Code Chunk
  ↓
LLM Log Analysis
  ↓
Issue + Root Cause + Patch Plan
  ↓
GitHub Repository Validation
  ↓
Create Fix Branch
  ↓
Read Full File from GitHub
  ↓
AST-based Safe Patch
  ↓
Commit Updated File
  ↓
Runtime Validation
  ↓
GitHub Actions Compile Validation
  ↓
PR Creation
  ↓
Reviewer Assignment
```

---

# Design Patterns Used

| Pattern                 | Purpose                                     |
| ----------------------- | ------------------------------------------- |
| Workflow Engine         | Orchestrates remediation lifecycle          |
| Strategy Pattern        | Language-specific validation/build/patching |
| Plugin Architecture     | Multi-language scalability                  |
| Factory Pattern         | Dynamic language resolution                 |
| Saga Pattern            | Rollback and recovery                       |
| Chain of Responsibility | Sequential execution stages                 |
| State Context           | Shared workflow state                       |

---

# Project Structure

```text
src/main/java/com/springAi/autonoumousCodeMaintenance
│
├── controller/
│
├── workflow/
│   ├── engine/
│   ├── stages/
│   ├── rollback/
│   └── context/
│
├── language/
│   ├── java/
│   ├── python/
│   └── go/
│
├── service/
├── serviceImpl/
├── strategy/
├── factory/
├── infra/
│   ├── github/
│   ├── cohere/
│   ├── docker/
│   └── vector/
│
├── model/
├── exception/
├── util/
└── config/
```

---

# Technology Stack

| Component        | Technology            |
| ---------------- | --------------------- |
| Backend          | Spring Boot           |
| Vector DB        | PostgreSQL + PGVector |
| Embeddings       | Cohere Embed v4       |
| LLM Analysis     | Cohere Chat           |
| AST Parsing      | JavaParser            |
| Runtime Sandbox  | Docker                |
| CI/CD Validation | GitHub Actions        |
| SCM              | GitHub API            |
| Build Tool       | Maven                 |

---

# Runtime Validation Architecture

```text
Modified Methods Detection
        ↓
Runtime Input Generation
        ↓
Docker Sandbox Execution
        ↓
Execution Result Validation
        ↓
Success / Failure
```

---

# GitHub Integration Flow

```text
Validate Repository
        ↓
Create Branch
        ↓
Read GitHub File
        ↓
Apply AST Patch
        ↓
Commit Changes
        ↓
Trigger GitHub Actions
        ↓
Wait for Build
        ↓
Create Pull Request
        ↓
Assign Reviewer
```

---

# Current Implemented Features (As of Now)

The following features are fully implemented and working in the current API-based system.

## Implemented End-to-End Workflow

```text
API Request
    ↓
Log Analysis
    ↓
Semantic Code Search (PGVector)
    ↓
Relevant File Detection
    ↓
AI Root Cause Analysis
    ↓
AST-based Fix Generation
    ↓
GitHub Repository Validation
    ↓
Branch Creation
    ↓
Read File from GitHub
    ↓
Apply AST Patch
    ↓
Commit Updated File
    ↓
Runtime Validation
    ↓
GitHub Actions Build Validation
    ↓
Automatic Pull Request Creation
    ↓
Reviewer Assignment
```

---

# Implemented Components

## 1. Semantic Code Search

Implemented using:

* Cohere Embeddings API
* PostgreSQL + PGVector
* Similarity search using vector distance

Capabilities:

* Logs are converted into embeddings
* Relevant code chunks are retrieved semantically
* File path + code chunk returned
* Supports repository-scale code search

---

## 2. GitHub Repository Integration

Implemented features:

* GitHub token authentication
* Repository validation
* File retrieval from GitHub API
* Branch creation
* File update commits
* Pull Request generation
* Reviewer assignment
* Branch rollback on failure

Current configuration:

```properties
github.token=
github.repo=
github.base-branch=main
```

---

## 3. AST-based Java Patching

Implemented using:

* JavaParser

Capabilities:

* Parses Java source safely
* Detects affected methods
* Modifies exact AST nodes
* Avoids unsafe regex replacement
* Preserves Java syntax structure

Current support:

* Java language only

---

## 4. Runtime Validation Engine

Implemented flow:

```text
Modified Method Detection
        ↓
Runtime Input Generation
        ↓
Docker Sandbox Execution
        ↓
Execution Result Validation
```

Capabilities:

* Detects modified methods
* Generates runtime inputs dynamically
* Executes updated code in Docker sandbox
* Detects runtime failures before PR creation

---

## 5. CI/CD Validation

Implemented using:

* GitHub Actions workflow polling

Capabilities:

* Waits for compile/build completion
* Detects workflow status
* Fetches failure logs automatically
* Prevents invalid PR creation

---

## 6. Automatic Pull Request Generation

Implemented capabilities:

* Creates dedicated fix branch
* Commits updated code
* Creates PR automatically
* Adds structured PR description
* Assigns reviewer dynamically
* Uses repo owner as fallback reviewer

---

# Current System Architecture

```text
                REST API Layer
                       ↓
             AutoFixServiceImpl
                       ↓
         ┌─────────────┼─────────────┐
         ↓             ↓             ↓
  Semantic Search   AI Analysis   GitHub Service
         ↓             ↓             ↓
     PGVector       Cohere AI    GitHub API
                                       ↓
                            AST Patch + PR Flow
```

---

# Current Runtime Flow

```text
Logs
  ↓
Generate Embedding
  ↓
PGVector Similarity Search
  ↓
Relevant File Path + Code
  ↓
AI Root Cause Analysis
  ↓
Generate Fix
  ↓
Validate GitHub Repo
  ↓
Create Branch
  ↓
Read File from GitHub
  ↓
Apply AST Patch
  ↓
Update File
  ↓
Runtime Validation
  ↓
GitHub Actions Compile Validation
  ↓
Create Pull Request
  ↓
Assign Reviewer
```

---

# Current Project Structure

```text
src/main/java/com/springAi/autonoumousCodeMaintenance
│
├── controller/
├── model/
├── service/
├── serviceImpl/
├── util/
├── exception/
└── config/
```

---

# Current Limitations

The current implementation is intentionally Java-focused.

Current limitations:

* Java-only AST patching
* Single-file fix flow
* API-driven orchestration only
* No distributed workflow engine yet
* No plugin abstraction yet
* No multi-language runtime validators yet

These are planned for the next architectural refactor.

---

# Planned Next Architecture Evolution

Future architecture goals:

* Workflow Engine
* Plugin-based language support
* Python + Go support
* Strategy-based validators
* Distributed execution
* Multi-file coordinated fixes
* Intelligent diff generation
* Retry policies
* Saga rollback orchestration

---

# Conclusion

This project currently functions as an API-driven autonomous remediation system capable of:

* Understanding runtime logs,
* Locating faulty code semantically,
* Generating AST-safe fixes,
* Validating runtime behavior,
* Validating CI/CD compilation,
* Creating GitHub Pull Requests automatically.

The next architectural phase focuses on transforming the current implementation into a fully scalable, workflow-driven, language-agnostic remediation platform.

This project evolves beyond a traditional backend service into a complete AI-driven autonomous remediation platform capable of:

* Understanding logs,
* Locating faulty code,
* Generating safe patches,
* Validating behavior,
* Creating production-grade GitHub pull requests automatically.
