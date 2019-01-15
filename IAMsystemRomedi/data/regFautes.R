### Author : Romain Griffier
########### A logistic regression to predic a typo : 

data <- read.table("dfFautePredictionSoundex.csv",sep = "\t",header=T, quote="")
data$firstLetterIdentical <- as.factor(data$firstLetterIdentical)
data$fauteOrtographe <- as.factor(data$fauteOrtographe)

table(data$levenshteinDistancePhonetic)

## aggregate 1,2 and 3 to the same category
data$levenshteinDistancePhonetic2 <- ifelse(data$levenshteinDistancePhonetic == 0, 0, 1)

### logistic regression : 
reg <- glm(formula = fauteOrtographe ~ firstLetterIdentical + wordLength + levenshteinDistancePhonetic2,
    data = data, family = binomial)
summary(reg)

## interpretation :  
exp(reg$coefficients)
### when first letter not identitical : higher risk of typo
## when word length higher : higher risk of typo
## when phonetic not the same : lower risk of typo 

## check the implemntation : 
coefs <- reg$coefficients
as.numeric(coefs)
data[1,]
vecteurRow1 <- c(1, 1, 22, 0)
value <- exp(sum(vecteurRow1 * coefs)) / (1 + exp(sum(vecteurRow1 * coefs)))
print(value)

## prediction : 
prob=predict(reg, type=c("response"))
data$prob=prob
library(pROC)
g <- roc(fauteOrtographe ~ prob,
         data = data)
plot(g)
g$sensitivities
value <- min(which(g$specificities > 0.94))
g$thresholds[value]
g$sensitivities[value]

data$probSupThreshold <- ifelse(data$prob > g$thresholds[value], 1, 0)
ftable(data$fauteOrtographe, data$probSupThreshold)
