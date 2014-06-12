source("utils.R")


testHypothesis <- function(ourTechnique, ourTechniqueName, theOtherTechnique, theOtherTechniqueName) {
  testResults = wilcox.test(ourTechnique, 
                    theOtherTechnique, 
                    paired=TRUE, 
                    alternative="greater", 
                    conf.int=TRUE,
                    conf.level=0.99,
                    na.action=na.omit) # NA ???
  print(paste("our technique = ", ourTechniqueName))
  print(paste("the other technique = ", theOtherTechniqueName))
  print(testResults)
  cat("-----------------------------------------\n")
}

testAllHypotheses <- function(dir, fase=FALSE) {
  # Full synthesis
  fmrandBIG <- readCSV(dir,"random_fullsynthesis.csv")
  fmrandBIG <- aggregate(fmrandBIG[,-(1:2)], by=list(fmrandBIG$ID), mean, na.rm=TRUE)
  fmrandBIGFullSynthesis <- fmrandBIG[,"Random - Full Synthesis"]
  fmrandBIGTOP2 <- fmrandBIG[,"Random - TOP 2"]
  
  fmonto <- readCSV(dir,"fullsynthesis.csv")
  fmontoFullSynthesis <- fmonto[,grep("Full Synthesis",names(fmonto))]
  fmontoTOP2 <- fmonto[,grep("TOP 2", names(fmonto))]
  
  sapply(colnames(fmontoFullSynthesis), 
         function(heuristicName) {
           testHypothesis(fmontoFullSynthesis[,heuristicName],
                          heuristicName,
                          fmrandBIGFullSynthesis,
                          "random")
         })
  
  # Full synthesis on reduced BIG
  if (fase) {
    fmfase <- readCSV(dir,"FASE.csv")  
  }
  fmrandRBIG <- readCSV(dir,"random_fullsynthesisRBIG.csv")
  fmrandRBIGFullSynthesis <- fmrandRBIG[,"Random - Full Synthesis"]
  fmrandRBIGTOP2 <- fmrandRBIG[,"Random - TOP 2"]
  fmontoLOGIC <- readCSV(dir,"fullsynthesisRBIG.csv")  
  
}

testAllHypotheses("../output/ICSE/")
#testAllHypotheses("../output/ESE/SPLOT/", fase=TRUE)
#testAllHypotheses("../output/ESE/PCMs/")