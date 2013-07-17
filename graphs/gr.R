require("reshape")
require("ggplot2")

#datums <- read.csv(commandArgs(TRUE)[1])
datums <- read.csv("pl.csv")
idx <- 3:length(datums[1,])
datums <- datums[c(1,2,which(sapply(datums[,idx], max) > 0)+2)]
idx <- 3:length(datums[1,])
hrz <- 0:(length(idx))
running <- apply(datums[idx], 1, cumsum)
r.d <- as.data.frame(t(running))
r.d$series = datums$series
r.d$message <- datums$message
datums <- rbind(datums, r.d)
datums$running <- c(rep(FALSE, 4), rep(TRUE, 4))

## make the sends plot.
## pull out the running sends columns
running.sends <- datums[(datums$series=="sends" & datums$running) ,]
## transpose
r.s.d.f <- as.data.frame(t(running.sends[,3:(2+length(idx))]), row.names='')
## set the column names
colnames(r.s.d.f) <- running.sends[,2]
## add in the priors
r.s.d.f = r.s.d.f + 2
r.s.d.f = rbind(data.frame(a=c(2), b=c(2)),r.s.d.f) 
## label the hours
r.s.d.f$hour <- hrz
## transform for ggplot
melted<- melt(r.s.d.f, id='hour')
colnames(melted) <- c('hour', 'message', 'sends')
## plot 'er up. 
ggplot(data=melted,aes(x=hour, y=sends, colour=message)) +
  geom_line() + geom_point()  + theme_bw() +
  scale_color_brewer(palette="Set1")
ggsave("sends.01.png", width=8, height=4.5, dpi=100)

## pull out the opens columns
opens <- datums[(datums$series=="opens" & !datums$running) ,]
## transpose
o.d.f <- as.data.frame(t(opens[,3:(2+length(idx))]), row.names='')
## set the column names
colnames(o.d.f) <- opens[,2]
## add in the priors
o.d.f = rbind(data.frame(a=c(1), b=c(1)),o.d.f) 
## label the hours
o.d.f$hour <- hrz
## transform for ggplot
melted.o<- melt(o.d.f, id='hour')
colnames(melted.o) <- c('hour', 'message', 'opens')
## plot 'er up. 
ggplot(data=melted.o,aes(x=hour, y=opens, colour=message)) +
  geom_line() + geom_point()  + theme_bw() +
  scale_color_brewer(palette="Set1")
ggsave("opens.01.png", width=8, height=4.5, dpi=100)

d = melted
running.opens <- datums[(datums$series=="opens" & datums$running) ,]
r.o.d.f <- as.data.frame(t(running.opens[,3:(2+length(idx))]), row.names='')
## set the column names
colnames(r.o.d.f) <- running.opens[,2]
## add in the priors
r.o.d.f = r.o.d.f + 1
r.o.d.f = rbind(data.frame(a=c(1), b=c(1)),r.o.d.f) 
## label the hours
r.o.d.f$hour <- hrz
## transform for ggplot
melted.o<- melt(r.o.d.f, id='hour')
colnames(melted.o) <- c('hour', 'message', 'opens')
d$opens = melted.o$opens

m = ddply(d, .(message,hour,opens,sends),
  function(row){
    c( row$open/row$send,
      qbeta(c(.025, .975), row$open, row$send-row$open))
  })
 
ggplot(data=m, aes(x=hour, y=V1, colour=message)) + theme_bw() +
  ylab("p(open)") +
  scale_color_brewer(palette="Set1") + 
  geom_line() + geom_point() + 
  geom_ribbon(aes(x=hour, ymin=V2, ymax=V3, colour=NA, fill=message),
              alpha=.1)  +
  scale_fill_brewer(palette="Set1")   
ggsave("popens.01.png", width=8, height=4.5, dpi=100)
