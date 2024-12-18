#Function to obtain maximal values
obtaining_maximals <- function(matrix) {
  n <- nrow(matrix)
  maximals <- vector("logical", n)
  
  for (i in 1:n) {
    if (all(matrix[, i] > 0.05)) {
      maximals[i] <- TRUE
    }
  }
  return(which(maximals))
}

folder <- "files/Matrices/Epsilon"
files <- list.files(folder, pattern = "\\.csv$", full.names = TRUE)

maximal_count_208 <- rep(0, 208)
maximal_count_16 <- rep(0, 16)
maximal_count_192 <- rep(0, 192)

header_ref <- NULL

for (file in files) {
  print(paste("Processing file:", file))
  
  matrix <- read.csv(file, header = TRUE, sep = ";", stringsAsFactors = FALSE)
  
  if (is.null(header_ref)) {
    header_ref <- colnames(matrix)
  } 
  
  column_map <- match(header_ref, colnames(matrix))
  
  matrix_values <- as.matrix(matrix)
  matrix_values <- apply(matrix_values, 2, as.numeric)
  
  matrix_values <- matrix_values[, column_map, drop = FALSE]
  
  if (nrow(matrix_values) >= 208 && ncol(matrix_values) >= 208) {
    
    submatrix <- matrix_values  
    
    submatrix_16 <- submatrix[1:16, 1:16]
    submatrix_192 <- submatrix[17:208, 17:208]
    
    indices_maximal_208 <- obtaining_maximals(submatrix)
    indices_maximal_16 <- obtaining_maximals(submatrix_16)
    indices_maximal_192 <- obtaining_maximals(submatrix_192)
    
    maximal_count_208[indices_maximal_208] <- maximal_count_208[indices_maximal_208] + 1
    maximal_count_16[indices_maximal_16] <- maximal_count_16[indices_maximal_16] + 1
    maximal_count_192[indices_maximal_192] <- maximal_count_192[indices_maximal_192] + 1
  }
}

show_config <- function(ID_Type_C) {
  config_psos <- unique(PSOs[PSOs$ID_Type_C == ID_Type_C, c("ID_Type_C", "Pop_Size", "Num_Iters", "Initialisation", "Best_Dir")])
  
  config_gens <- unique(Gens[Gens$ID_Type_C == ID_Type_C, c("ID_Type_C", "Pop_Size", "Num_Iters", "Initialisation", "Selection", "Crossover", "Mutation", "Replacement")])
  
  if (nrow(config_psos) > 0) {
    print(config_psos)
  }
  
  if (nrow(config_gens) > 0) {
    print(config_gens)
  }
}

show_summarised_count <- function(maximal_count, submatrix_name) {
  indices_count <- which(maximal_count > 0)
  values_count <- maximal_count[indices_count]
  
  if (length(indices_count) > 0) {
    for (i in 1:length(indices_count)) {
      cat(paste0("X", indices_count[i], " is maximal ", values_count[i], " times\n"))
      if(submatrix_name == "moved") {
        show_config(indices_count[i]+16)
      }
      else {
        show_config(indices_count[i])
      }
    }
  }
}


show_summarised_count(maximal_count_208, "fine")
show_summarised_count(maximal_count_16, "fine")
show_summarised_count(maximal_count_192, "moved")

