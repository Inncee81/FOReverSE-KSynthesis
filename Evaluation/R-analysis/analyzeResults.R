
readCSV <- function(dir,file) {
  read.csv(paste(dir,file,sep=""),check.name=FALSE, na.strings="N/A")
}

computeStats <- function(input, output, header) {
  data <- input[,-1]
  mean <- colMeans(data, na.rm=TRUE)
  median <- apply(data, 2, median, na.rm=TRUE)
  min <- apply(data, 2, min, na.rm=TRUE)
  max <- apply(data, 2, max, na.rm=TRUE)
  
  stats <- data.frame(mean,median,min,max)
  
  write(header,output,append=TRUE)
  write.table(stats, output, sep=",", col.names=NA, append=TRUE)
  write("",output,append=TRUE)
}


dir <- "../output/ICSE/"

# create output file
output <- paste(dir,"results.csv",sep="")
file.create(output)

# compute stats on the evaluation results
computeStats(readCSV(dir,"stats.csv"),output,"Stats on input FMs")

computeStats(readCSV(dir,"fullsynthesis.csv"),output,"Full synthesis")
computeStats(readCSV(dir,"random_fullsynthesis.csv"),output,"Full synthesis (random)")
computeStats(readCSV(dir,"fullsynthesisRBIG.csv"),output,"Full synthesis on reduced BIG")
computeStats(readCSV(dir,"random_fullsynthesisRBIG.csv"),output,"Full synthesis on reduced BIG (random)")


computeStats(readCSV(dir,"clustering.csv"),output,"Clusters")
computeStats(readCSV(dir,"random_clustering.csv"),output,"Clusters (random)")
computeStats(readCSV(dir,"clusteringRBIG.csv"),output,"Clusters on reduced BIG")
computeStats(readCSV(dir,"random_clusteringRBIG.csv"),output,"Clusters on reduced BIG (random)")

computeStats(readCSV(dir,"cliques.csv"),output,"Cliques")

computeStats(readCSV(dir,"fgroups.csv"),output,"Feature groups")
computeStats(readCSV(dir,"fgroupsRBIG.csv"),output,"Feature groups on reduced BIG")


