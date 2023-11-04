import NetGraphAlgebraDefs.NetGraph.logger
import NetGraphAlgebraDefs.{NetGraph, NodeObject}
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD

import scala.collection.JavaConverters.*
import collection.parallel.CollectionConverters.ImmutableSeqIsParallelizable
import collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable
import collection.parallel.CollectionConverters.seqIsParallelizable
import collection.parallel.CollectionConverters.IterableIsParallelizable

object randomWalk {
  val numRandomWalks = 50
  val maxSteps = 500

  // Saving the output
  val outputPath = "C:\\Users\\fatjj\\Desktop\\output"

  val similarityThreshold = 0.8

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
      .appName("randomWalk")
      .master("local")
      .getOrCreate()

    try {
      // Load in the ngs.perturbed file
      val perturbedGraph: Option[NetGraph] = NetGraph.load("NetGameSimNetGraph_26-10-23-23-39-25.ngs.perturbed", "C:\\Users\\fatjj\\Desktop\\")
      val netPerturbedGraph: NetGraph = perturbedGraph.get


      // Generate a random walk using spark
      val randomWalkResults = generateRandomWalks(netPerturbedGraph, numRandomWalks, maxSteps, spark)
      // Calculate the stasticis of the attack
      val (successfulAttacks, failedAttacks) = calculateAttacks(randomWalkResults)

      //
      // Estimate precision and recall
      val valuableDataNodes = netPerturbedGraph.sm.nodes().count(node => node.containsValuableData)
      val precision = successfulAttacks.toDouble / (successfulAttacks + failedAttacks)
      val recall = successfulAttacks.toDouble / valuableDataNodes
      // Getting the output
      saveAttackStats(precision, recall, outputPath, spark)
    } finally {
      // Stop
      spark.stop()
      println("Successful Stop")
    }
  }
  // We are generate random walks in parallelism using spark
  def generateRandomWalks(graph: NetGraph, numRandomWalks: Int, maxSteps: Int, spark: SparkSession): RDD[List[NodeObject]] = {
    // Initialize and how are we going to store random walks
    val randomWalkResults = (1 to numRandomWalks).par.map { _ =>
       // Get a random node
      val randomNode = getRandomNode(graph)

      // Performing random walk
      randomWalk(graph, randomNode, maxSteps)
    }.toList
    // Cnvert the list to a ditribuuted spark rdd
    spark.sparkContext.parallelize(randomWalkResults)
  }
  // Get a random node from the graph
  def getRandomNode(graph: NetGraph): NodeObject = {
    // Get the nodes from the list
    val nodesList = graph.sm.nodes().toList
    // Set an index on a random node
    val randomIndex = scala.util.Random.nextInt(nodesList.length)
    nodesList(randomIndex) // Return index
  }

  // Perform a random walk on the graph
  logger.info("Begin random walk")
  def randomWalk(graph: NetGraph, startNode: NodeObject, maxSteps: Int): List[NodeObject] = {
    def randomWalkStream(node: NodeObject, steps: Int): Stream[NodeObject] = {

      //Performing operations once up to the maximum steps of 1000
      if (steps >= maxSteps) Stream.empty
      else {
        // Try to find a random node connected from current node
        graph.getRandomConnectedNode(node) match {
          case Some((nextNode, _)) =>
            // If a connected node is found add it to the stream and we will continue wih the walk
            node #:: randomWalkStream(nextNode, steps + 1)
          case None =>
            // No connected node found so return
            Stream.empty
        }
      }
    }
    // Inittiate random walk from the startNode.
    // convert the result to a list.
    val walkStream: Stream[NodeObject] = randomWalkStream(startNode, 0)
    val path: List[NodeObject] = walkStream.take(maxSteps).toList
  // Return list
    path
  }

  // Calculate attack statistics based on similarity scores
  def calculateAttacks(walkResults: RDD[List[NodeObject]]): (Int, Int) = {
    // Get a similarity score for pairs of nodes

    val similarityScoresRDD = walkResults.flatMap(path => {
      path.sliding(2).map {
        //---
        case List(perturbedNode, originalNode) =>
          // Similarity score between perturbed and orignal
          val similarityScore = simRank(originalNode, perturbedNode)
          (similarityScore, originalNode, perturbedNode)
        case _ =>
          // No nodes to compare
          (0.0, null, null)
      }
    })
    //
    // CHeck if the attack was successful based similarity code
    // If not it is a failed attack
    // Return the results of how many succedded and how many failed
    val successfulAttacks = similarityScoresRDD.filter(_._1 >= similarityThreshold).count().toInt
    val failedAttacks = walkResults.count().toInt - successfulAttacks
    (successfulAttacks, failedAttacks)
  }

  // simRank function
  def simRank(originalNode: NodeObject, perturbedNode: NodeObject): Double = {
    // Set the initial score of 1 for each of the attributes
    val childrenWeight = 1.0
    val propsWeight = 1.0
    val currentDepthWeight = 1.0
    val propValueRangeWeight = 1.0
    val maxDepthWeight = 1.0
    val maxBranchingFactorWeight = 1.0
    val maxPropertiesWeight = 1.0
    val storedValueWeight = 1.0

    // So here we are comparing the properties of the nodes(orignal and perturbed) if they are similar
    // they are going to be assigned a score
    val similarityChildren = compareProperty(originalNode.children, perturbedNode.children)
    val similarityProps = compareProperty(originalNode.props, perturbedNode.props)
    val similarityCurrentDepth = compareProperty(originalNode.currentDepth, perturbedNode.currentDepth)
    val similarityPropValueRange = compareProperty(originalNode.propValueRange, perturbedNode.propValueRange)
    val similarityMaxDepth = compareProperty(originalNode.maxDepth, perturbedNode.maxDepth)
    val similarityMaxBranchingFactor = compareProperty(originalNode.maxBranchingFactor, perturbedNode.maxBranchingFactor)
    val similarityMaxProperties = compareProperty(originalNode.maxProperties, perturbedNode.maxProperties)
    val similarityStoredValue = compareProperty(originalNode.storedValue, perturbedNode.storedValue)

    // Based on all the attributes of that comparison of node attributes from the above we are
    // going to sum them to come up with a value that will be used to see their similarities
    val weightedSum = (childrenWeight * similarityChildren) +
      (propsWeight * similarityProps) +
      (currentDepthWeight * similarityCurrentDepth) +
      (propValueRangeWeight * similarityPropValueRange) +
      (maxDepthWeight * similarityMaxDepth) +
      (maxBranchingFactorWeight * similarityMaxBranchingFactor) +
      (maxPropertiesWeight * similarityMaxProperties) +
      (storedValueWeight * similarityStoredValue)


    // Here we are going get that sum from the above and divide that by the total attributes
    val totalValue = childrenWeight + propsWeight + currentDepthWeight + propValueRangeWeight +
      maxDepthWeight + maxBranchingFactorWeight + maxPropertiesWeight + storedValueWeight
    // Here is where we get our final answer of what the similarity score is
    val overallSimilarityScore = weightedSum / totalValue

    overallSimilarityScore // Return the similarity between (original, perturbed)
  }

  // Compare two properties and return a similarity score
  // So if similar return 1 else 0
  def compareProperty(property1: Any, property2: Any): Double = if (property1 == property2) 1.0 else 0.0

  // Save attack information to a CSV file
  def saveAttackStats(precision: Double, recall: Double, outputPath: String, spark: SparkSession): Unit = {
    val attackStatsDF = Seq(("precision", precision), ("recall", recall)).toDF("metric", "value")
    attackStatsDF.write
      .option("header", "true")
      .csv("C:\\Users\\fatjj\\Desktop\\attackInformation.csv")
  }
}
