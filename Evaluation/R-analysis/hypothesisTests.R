source("utils.R")
require("exactRankTests")
require("lsr")

# Test if our technique is better than the other technique
testHypothesis <- function(ourTechnique, ourTechniqueName, theOtherTechnique, theOtherTechniqueName) {
  testResults = wilcox.exact(ourTechnique, 
                    theOtherTechnique, 
                    paired=TRUE, 
                    alternative="greater", 
                    exact=TRUE,
                    conf.int=TRUE,
                    conf.level=0.99,
                    na.action=na.omit) # NA ???
  d = cohensD(ourTechnique, theOtherTechnique, method="paired")
  
  cat(paste("tested technique = ", ourTechniqueName, "\n"))
  cat(paste("reference technique = ", theOtherTechniqueName, "\n"))
  print(testResults)
  cat(paste("effect size (Cohen's d) = ", d, "\n"))
  cat("-----------------------------------------\n")
}

compareOurHeuristicsToAnother <- function(ourHeuristics, theOtherHeuristic, theOtherHeuristicName) {
  sapply(colnames(ourHeuristics), 
         function(heuristicName) {
           testHypothesis(ourHeuristics[,heuristicName],
                          heuristicName,
                          theOtherHeuristic[,1],
                          theOtherHeuristicName)
         })
}

compareBIGAndRBIG <- function(heuristicsBIG, heuristicsRBIG) {
  sapply(colnames(heuristicsBIG), 
         function(heuristicName) {
           heuristicBIG <- heuristicsBIG[,heuristicName]
           heuristicRBIG <- heuristicsRBIG[,heuristicName]
           if (identical(heuristicBIG, heuristicRBIG)) {
              print(paste("tested technique = ", heuristicName, " - RBIG"))
              print(paste("reference technique = ", heuristicName, " -BIG"))
              cat("Both vectors are the same, thus we do not compute the test\n")
              cat("-----------------------------------------\n")
           } else {
             testHypothesis(heuristicRBIG,
                            paste(heuristicName," - RBIG"),
                            heuristicBIG,
                            paste(heuristicName," - BIG")
             )  
           }
         })
}

testH1toH4 <- function(dir, fase=FALSE) {
  # Get data
  # Full synthesis and TOP 2
  fmRandBIG <- readCSV(dir,"random_fullsynthesis.csv")
  fmRandBIG <- aggregate(fmRandBIG[,-(1:2)], by=list(fmRandBIG$ID), mean, na.rm=TRUE)
  
  fmRandBIGFullSynthesis <- fmRandBIG[,"Random - Full Synthesis",drop=FALSE]
  fmRandBIGTOP2 <- fmRandBIG[,"Random - TOP 2",drop=FALSE]
  
  fmOnto <- readCSV(dir,"fullsynthesis.csv")
  fmOntoFullSynthesis <- fmOnto[,grep("Full Synthesis",names(fmOnto))]
  fmOntoFullSynthesis <- fmOntoFullSynthesis[,(1:length(names(fmOntoFullSynthesis)) - 1)]
  fmOntoTOP2 <- fmOnto[,grep("TOP 2", names(fmOnto))]
  fmOntoTOP2 <- fmOntoTOP2[,(1:length(names(fmOntoTOP2)) - 1)]
  
  
  
  # Full synthesis and TOP 2 on reduced BIG
  fmRandRBIG <- readCSV(dir,"random_fullsynthesisRBIG.csv")
  fmRandRBIG <- aggregate(fmRandRBIG[,-(1:2)], by=list(fmRandRBIG$ID), mean, na.rm=TRUE)
  
  fmRandRBIGFullSynthesis <- fmRandRBIG[,"Random - Full Synthesis",drop=FALSE]
  fmRandRBIGTOP2 <- fmRandRBIG[,"Random - TOP 2",drop=FALSE]
  
  fmFaseSATFullSynthesis <- fmOnto[,"Transitive reduction metric - Full Synthesis",drop=FALSE]
  
  fmFaseFullSynthesis <- if (fase) {
    readCSV(dir,"FASE.csv")[,-1,drop=FALSE]  
  } else {
    NULL
  }
  
  fmOntoLogic <- readCSV(dir,"fullsynthesisRBIG.csv")  
  fmOntoLogicFullSynthesis <- fmOntoLogic[,grep("Full Synthesis",names(fmOntoLogic))]
  fmOntoLogicFullSynthesis <- fmOntoLogicFullSynthesis[,(1:length(names(fmOntoLogicFullSynthesis)) - 1)]
  fmOntoLogicTOP2 <- fmOntoLogic[,grep("TOP 2", names(fmOntoLogic))]
  fmOntoLogicTOP2 <- fmOntoLogicTOP2[,(1:length(names(fmOntoLogicTOP2)) - 1)]
  
  
  # Fully automated synthesis 
  
  ## H1: ontological techniques are superior to random or logical heuristics 
  ## when opearting over the same logical structure
  cat("**********************************\n")
  cat("H1: ontological techniques are superior to random or logical heuristics\n\n\n")
  compareOurHeuristicsToAnother(fmOntoFullSynthesis, fmRandBIGFullSynthesis, "FMRAND_BIG")
  
  compareOurHeuristicsToAnother(fmOntoLogicFullSynthesis, fmRandRBIGFullSynthesis, "FMRAND_RBIG")
  compareOurHeuristicsToAnother(fmOntoLogicFullSynthesis, fmFaseSATFullSynthesis, "FMFASE_SAT")
  if (fase) {
    compareOurHeuristicsToAnother(fmOntoLogicFullSynthesis, fmFaseFullSynthesis, "FMFASE")
  }
  
  ## H2: the reduced BIG can improve the effectiveness
  cat("**********************************\n")
  cat("H2: the reduced BIG can improve the effectiveness\n\n\n")
  compareBIGAndRBIG(fmOntoFullSynthesis, fmOntoLogicFullSynthesis)
  compareBIGAndRBIG(fmRandBIGFullSynthesis, fmRandRBIGFullSynthesis)
  
  
  # Ranking lists
  
  ## H3: ontological techniques are superior to random heuristics 
  ## when operating over the same logical structure
  cat("**********************************\n")
  cat("H3: ontological techniques are superior to random heuristics\n\n\n")
  compareOurHeuristicsToAnother(fmOntoTOP2, fmRandBIGTOP2, "FMRAND_BIG")

  compareOurHeuristicsToAnother(fmOntoLogicTOP2, fmRandRBIGTOP2, "FMRAND_RBIG")
  
  ## H4: the reduced BIG can improve the quality of ranking lists
  cat("**********************************\n")
  cat("H4: the reduced BIG can improve the quality of ranking lists\n\n\n")
  compareBIGAndRBIG(fmOntoTOP2, fmOntoLogicTOP2)
  compareBIGAndRBIG(fmRandBIGTOP2, fmRandRBIGTOP2)
  
  
}

testH5and6 <- function(dir) {
  # Get data
  # Precision = % correct clusters
  # Coverage = % features in a correct cluster
  
  # Clustering on BIG
  fmRandBIG <-readCSV(dir, "random_clustering.csv")
  fmRandBIG <- aggregate(fmRandBIG[,-(1:2)], by=list(fmRandBIG$ID), mean, na.rm=TRUE)
  fmRandBIGPrecision <- fmRandBIG[,"Random - % correct clusters",drop=FALSE] 
  fmRandBIGCoverage <- fmRandBIG[,"Random - % features in a correct cluster",drop=FALSE] 
  
  fmOnto <- readCSV(dir, "clustering.csv")
  fmOntoPrecision <- fmOnto[,grep("% correct clusters",names(fmOnto))]
  fmOntoPrecision <- fmOntoPrecision[,(1:length(names(fmOntoPrecision)) - 1)]
  fmOntoCoverage <- fmOnto[,grep("% features in a correct cluster",names(fmOnto))]
  fmOntoCoverage <- fmOntoCoverage[,(1:length(names(fmOntoCoverage)) - 1)]
  
  # Clustering on reduced BIG
  fmRandRBIG <- readCSV(dir, "random_clusteringRBIG.csv")
  fmRandRBIG <- aggregate(fmRandRBIG[,-(1:2)], by=list(fmRandRBIG$ID), mean, na.rm=TRUE)
  fmRandRBIGPrecision <- fmRandRBIG[,"Random - % correct clusters",drop=FALSE]
  fmRandRBIGCoverage <- fmRandRBIG[,"Random - % features in a correct cluster",drop=FALSE]
  
  fmOntoLogic <- readCSV(dir, "clusteringRBIG.csv")
  fmOntoLogicPrecision <- fmOntoLogic[,grep("% correct clusters",names(fmOntoLogic))]
  fmOntoLogicPrecision <- fmOntoLogicPrecision[,(1:length(names(fmOntoLogicPrecision)) - 1)]
  fmOntoLogicCoverage <- fmOntoLogic[,grep("% features in a correct cluster",names(fmOntoLogic))]
  fmOntoLogicCoverage <- fmOntoLogicCoverage[,(1:length(names(fmOntoLogicCoverage)) - 1)]
  
  # Clusters
  ## H5: ontological techniques are superior to random heuristics 
  ## when computing clusters over the same logical structure
  cat("**********************************\n")
  cat("H5: ontological techniques are superior to random heuristics \n\n\n")
  compareOurHeuristicsToAnother(fmOntoPrecision, fmRandBIGPrecision, "FMRAND_BIG - precision")
  compareOurHeuristicsToAnother(fmOntoCoverage, fmRandBIGCoverage, "FMRAND_BIG - coverage")
  
  compareOurHeuristicsToAnother(fmOntoLogicPrecision, fmRandRBIGPrecision, "FMRAND_RBIG - precision")
  compareOurHeuristicsToAnother(fmOntoLogicCoverage, fmRandRBIGCoverage, "FMRAND_RBIG - coverage")
  
  
  ## H6: the reduced BIG can improve the quality of the clusters
  cat("**********************************\n")
  cat("H6: the reduced BIG can improve the quality of the clusters\n\n\n")
  compareBIGAndRBIG(fmRandBIGPrecision, fmRandRBIGPrecision)
  compareBIGAndRBIG(fmRandBIGCoverage, fmRandRBIGCoverage)
  
  compareBIGAndRBIG(fmOntoPrecision, fmOntoLogicPrecision)
  compareBIGAndRBIG(fmOntoCoverage, fmOntoLogicCoverage)
}

testH7 <- function(dir) {
  # Get data
  fgroupsBIG <- readCSV(dir, "fgroups.csv")
  fgroupsBIGPrecision <- fgroupsBIG[,grep("% correct clusters",names(fgroupsBIG))]
  fgroupsBIGCoverage <- fgroupsBIG[,grep("% features in a correct cluster",names(fgroupsBIG))]
    
  fgroupsRBIG <- readCSV(dir, "fgroupsRBIG.csv")
  fgroupsRBIGPrecision <- fgroupsRBIG[,grep("% correct clusters",names(fgroupsBIG))]
  fgroupsRBIGCoverage <- fgroupsRBIG[,grep("% features in a correct cluster",names(fgroupsBIG))]
    
  # Logical feature groups
  ## H7: the reduced BIG can improve the quality of logical feature groups
  cat("**********************************\n")
  cat("H7: the reduced BIG can improve the quality of logical feature groups\n\n\n")
  compareBIGAndRBIG(fgroupsBIGPrecision, fgroupsRBIGPrecision)
  compareBIGAndRBIG(fgroupsBIGCoverage, fgroupsRBIGCoverage)
}


testAllHypotheses <- function(dir, fase=FALSE) {
  output <- paste(dir,"tests.txt",sep="")
  write("", file=output,append=FALSE) # erase previous results
  sink(output,append=TRUE) # redirect standard output to a file
  testH1toH4(dir, fase)
  testH5and6(dir)
  testH7(dir)
  sink()
}

cat("tests for ICSE (SPLOT)\n")
testAllHypotheses("../output/ICSE/")
cat("tests for ESE (SPLOT)\n")
testAllHypotheses("../output/ESE/SPLOT/", fase=TRUE)
cat("tests for ESE (PCM)\n")
testAllHypotheses("../output/ESE/PCMs/")