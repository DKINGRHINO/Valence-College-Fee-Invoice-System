```markdown
# Valence College Fee Invoice System

A Java-based console application for managing students, courses, and fee invoices at Valence College (a fictional institution). Developed solo as the final project for **COP 3330: Object-Oriented Programming** at the University of Central Florida (Spring 2024).

This project demonstrates object-oriented programming principles, including inheritance (student hierarchy: Undergrad, MS, PhD), polymorphism (fee calculations), file I/O (reading/writing course data), exception handling (e.g., invalid IDs), and interactive menus for student/course management.

## Project Overview

- **Key Features**:
  - Student types: Undergraduate (FL residency, GPA discounts over $500), MS (graduate courses only), PhD (research fees, lab supervision discounts: 50% for 2 labs, health/ID only for 3+).
  - Course/Lab management: Parses `lec.txt` for lectures/labs (format: CN, Prefix, Title, Level, Modality, Location, HasLab, Credits; labs listed below if YES).
  - Fee calculations: $120.25/credit for FL undergrads (double for out-of-state), $300/credit for MS, $700 research for PhD; +$35 health/ID; handles discounts and rules.
  - Interactive menus: Add/search/delete students/courses, add labs, print invoices/lists (sorted by type).
  - File I/O: Loads/updates `lec.txt`; assumes valid inputs but checks for duplicates/invalids.
  - Custom exception: `IdException` for invalid student IDs.

- **Example Outputs** (from project spec):
  - Undergrad invoice with courses, credits, fees, and GPA discount.
  - MS invoice with graduate courses.
  - PhD invoice with advisor, research, supervised labs, and minimal fees if supervising 3+ labs.
  - Student lists sorted by type (PhD, MS, Undergrad).

## My Implementation

Implemented the full application solo:
- Designed class hierarchy: `Student` base with `UndergradStudent`, `GraduateStudent` (`MsStudent`, `PhdStudent` subclasses).
- Handled file parsing (`lec.txt`) into mapped ArrayLists for courses/labs, including modality (F2F, Mixed, Online) and lab assignments.
- Built console menus for student/course management (add, delete, search, print) using Scanner.
- Implemented fee logic with overrides for printInvoice, conditionals for discounts/residency/supervision, and total cost calculations.
- Added error handling (e.g., invalid IDs, duplicates) and clean formatting for outputs.

## How to Build and Run

### Requirements
- Java JDK (e.g., 17+)
- Command-line compiler (e.g., javac)

### Compilation
```bash
javac ProjectDriver.java
```

### Execution
```bash
java ProjectDriver
```
- Ensure `lec.txt` is in the same directory (sample included in repo).
- Follow on-screen menus (e.g., add students with ID format like "kj2959", enroll in courses by CRN, print invoices).

## Credits & Attribution

- Project guidelines and `lec.txt` format provided by UCF COP 3330 teaching staff (Dr. Hatim Boustique).
- Implemented as a solo academic project – shared here for portfolio and demonstration purposes only.

## License / Usage Note

This code was developed as part of coursework at the University of Central Florida. It is shared publicly for portfolio purposes and to demonstrate OOP concepts in Java. Feel free to view and learn from it, but please do not use it to complete active assignments in COP 3330 or similar courses.

---

**Christopher Otto**  
Computer Science B.S. Candidate, Class of 2027  
University of Central Florida
