dir = "../output/ICSE/"
input = read.csv(paste(dir,"stats.csv",sep=""),check.name=FALSE)

data = input[,-1]

mean = colMeans(data)
median = apply(data,2,median)
min = apply(data,2,min)
max=apply(data,2,max)

stats = data.frame(mean,median,min,max)


summary = summary(input)
output = paste(dir,"results.csv",sep="")
write.csv(stats, output)
