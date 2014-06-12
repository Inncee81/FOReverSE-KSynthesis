readCSV <- function(dir,path) {
  fullPath = paste(dir,path,sep="")
  if (file.exists(fullPath)) {
    read.csv(fullPath,check.name=FALSE, na.strings="N/A") 
  } else {
    NULL
  }
}