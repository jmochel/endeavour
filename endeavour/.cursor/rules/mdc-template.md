---
title: "RULE TITLE HERE"
slug: "XXX-rule-slug"
version: "0.1.0"
status: "draft"
owners:
  - "team-or-guild-name"
tags:
  - "java"
  - "rules"
  - "best-practices"
applies_to:
  - "Java 17+"
last_updated: "2025-10-24"
visibility: "public"
lintable: true
---

# RULE TITLE HERE (.mdc)

<!-- Context: Define the scope of this rules file, including what it covers and what it excludes. -->

## 1) Context / Scope
- What this file covers (scope).
- What is out of scope.
- Normative language: **MUST / SHOULD / MAY**.

---

<!-- Principles: List durable, high-level rules that rarely change. Keep short and value-driven. -->

## 2) Principles
- High-level durable principles.  
- These rarely change.

---

<!-- Design Decisions: Capture reasoning, trade-offs, and references to standards (Effective Java, team conventions, etc.). -->

## 3) Design Decisions / Philosophy
- Why we prefer certain practices over alternatives.  
- References to industry standards or key texts.

---

<!-- General Approaches: Patterns and practices that guide usage broadly, not tied to a single technical detail. -->

## 4) General Approaches / Patterns
- Broad recommended practices.  
- Example: “Package by feature, not by layer.”

---

<!-- Specific Topics: Each important area of design/testing/etc. gets its own section with rules + examples. -->

## 5) Specific Topics (Rules)
### 5.1 Construction
- Rules with short rationale + code snippet.

### 5.2 Exceptions
- Rules with short rationale + code snippet.

### 5.3 Other sections
- Add more topical sections as needed.

---

<!-- Technical details: Add framework/library notes, dependencies, annotations, etc. -->

## 6) Technical Details / Tooling
- Maven/Gradle dependencies.  
- Specific annotations or APIs.

---

<!-- Lintable: Identify rules that can be automated via ArchUnit, Checkstyle, Sonar, etc. -->

## 7) Lintable / Automatable Rules
- Machine-enforceable constraints (ArchUnit, Checkstyle, Sonar).

---

<!-- Checklist: Provide reviewers with a quick way to verify compliance without rereading the whole doc. -->

## 8) Checklist (PR / Code Review)
- A quick bullet list for reviewers.  
- “Did you preserve causes when wrapping exceptions?”

---

<!-- Examples: Show good and bad code to clarify intent. -->

## 9) Examples
**Good Example**
```java
// minimal example here
```

**Bad Example**
```java
// anti-pattern here
```

---

<!-- Anti-Patterns: Common mistakes to avoid, with rationale for why they’re harmful. -->

## 10) Anti-Patterns & Smells
- Common pitfalls to avoid.  
- Why they are harmful.

<!-- End of skeleton -->