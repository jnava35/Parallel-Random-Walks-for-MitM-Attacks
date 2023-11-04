# HW2 Spark/Graph X

NetID: jnava35@uic.edu | UIN: 660115946 | Repo for HW1 CS 441

# Setting up Project

1. Download repo from git
2. Open project in intelliJ 
3. Next we have to setup the SDK. The SDK we will be using is 1.8 and the Scala version we will be using is `2.13.10`
   The reason for this is because Spark is compatible with certain versions
5. To do the above open intelij and go to `file -> Project Structure -> Modules -> Dependencies -> Module SDK` Select SDK Version 1.8
6. Add the netmodelsim.jar by doing the same step on step (5)
   `file -> Project Structure -> Modules -> Dependencies -> + -> JAR or Directories` and locate the netmodalsim.jar
8. Once we have the dependencies setup let use confirm we the correct scala version
9. In terminal type `scala -version` it should be `2.13.10`
10. Once you have confirmed we can do the following, from the terminal, run `sbt clean compile`

# Running the Project

1. Initialize Spark
2. Load Perturbed Graph
3. Generate Random Walks
4. Calculate Attack Statistics
5. Estimate Precision and Recall
6. Save Attack Statistics
7. Stop Spark
8. Analyze Results
 
Next once you have ran the program and have the result you will 
need to deploy it on aws. More info on project here: 
[HW2 Main Textbook Assignment](https://github.com/0x1DOCD00D/CS441_Fall2023/blob/main/Homework2.md)


# Key Variables and Definitions1
1. numRandomWalks: Number of random walks to generate.
2. maxSteps: Maximum number of steps in a random walk.
3. similarityThreshold: Threshold for similarity scores.
4. perturbedGraph: Option containing the perturbed NetGraph.
5. netPerturbedGraph: The loaded NetGraph.
6. randomWalkResults: Results of random walks as RDD.
7. successfulAttacks: Count of successful attacks.
8. failedAttacks: Count of failed attacks.
9. valuableDataNodes: Count of valuable data nodes in the NetGraph.
10. precision: Precision of the attack.
11. recall: Recall of the attack.
12. randomWalkStream: Stream of nodes in a random walk.
13. similarityScoresRDD: RDD of similarity scores.
14. weightedSum: Weighted sum of similarity scores.
15. totalValue: Total weight value used in calculating the overall similarity score.
16. overallSimilarityScore: Overall similarity score between original and perturbed nodes.

