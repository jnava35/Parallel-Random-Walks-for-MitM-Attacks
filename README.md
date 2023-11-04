# HW2 Spark/Graph X

NetID: jnava35@uic.edu | UIN: 660115946 | Repo for HW1 CS 441

# Setting up Project

1. Download repo from git
2. Open project in intelliJ 
3. Next we have to setup the SDK. The SDK we will be using is 1.8 and the Scala version we will be using is 2.13.10
   The reason for this is because Spark is compatible with certain versions
5. To do the above open intelij and go to `file -> Project Structure -> Modules -> Dependencies -> Module SDK` Select SDK Version 1.8
6. Add the netmodelsim.jar by doing the same step on step (5)
   `file -> Project Structure -> Modules -> Dependencies -> + -> JAR or Directories` and locate the netmodalsim.jar
8. Once we have the dependencies setup let use confirm we the correct scala version
9. In terminal type scala -version. It should be 2.13.10
10. Once you have confirmed we can do the following, from the terminal, run `sbt clean compile`

# Running the Project 


