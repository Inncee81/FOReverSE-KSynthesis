source("utils.R")

boxplotFullSynthesis <- function(dir,caption,fase=FALSE) {
  input <- readCSV(dir,"fullsynthesis.csv")
  inputRBIG <- readCSV(dir,"fullsynthesisRBIG.csv")
  names(inputRBIG) <- paste(names(inputRBIG)," - RBIG")
  mergedInputs <- if (fase) {
    inputFASE <- readCSV(dir,"FASE.csv")
    cbind(input,inputRBIG,inputFASE)
  } else {
    cbind(input,inputRBIG)
  }
  data <- mergedInputs[,grep("Full Synthesis",names(mergedInputs))] # select only data from full synthesis
  
  par(mai=c(1.2,7,0.2,0.2)) # set margins
  boxplot(data[,-1], horizontal=TRUE, las=1, xlab=caption)  
}

boxplotFullSynthesis("../output/ESE/PCMs/","ESE-PCMs")
boxplotFullSynthesis("../output/ESE/SPLOT/","ESE-SPLOT",fase=TRUE)
boxplotFullSynthesis("../output/ICSE/","ICSE-SPLOT")

