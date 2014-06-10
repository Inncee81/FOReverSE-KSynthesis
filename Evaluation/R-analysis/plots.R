
readCSV <- function(dir,path) {
  fullPath = paste(dir,path,sep="")
  if (file.exists(fullPath)) {
    read.csv(fullPath,check.name=FALSE, na.strings="N/A") 
  } else {
    NULL
  }
}



boxplotFullSynthesis <- function(dir,caption) {
  input <- readCSV(dir,"fullsynthesis.csv")
  inputRBIG <- readCSV(dir,"fullsynthesisRBIG.csv")
  names(inputRBIG) <- paste(names(inputRBIG)," - RBIG")
  mergedInputs <- cbind(input,inputRBIG)
  data <- mergedInputs[,grep("Full Synthesis",names(mergedInputs))] # select only data from full synthesis
  
  par(mai=c(1.2,7,0.2,0.2)) # set margins
  boxplot(data[,-1], horizontal=TRUE, las=1, xlab=caption)  
}

boxplotFullSynthesis("../output/ESE/PCMs/","ESE-PCMs")
# boxplotFullSynthesis("../output/ESE/SPLOT/","ESE-SPLOT")
boxplotFullSynthesis("../output/ICSE/","ICSE-SPLOT")

