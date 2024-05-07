
# Distributed Matrix Multilpication

This project aims to do distributed matrix multiplication using MPI for distribution to nodes

**Tech:** Java, MPI

## Configuration

- VM options must be added on the project:

```bash
 -jar $MPJ_HOME$\lib\starter.jar -np 3
```
- MPJ Home must be added as an environnement variable
```bash
  MPJ_HOME=C:\mpj
```
- MPI jar lib must be added to the classpath:
to add MPI jar libs (on Intellij) to the classpath, click on the project setting then -> Libraries -> then click on the plus button, then select the lib folder under your MPJ_HOME

