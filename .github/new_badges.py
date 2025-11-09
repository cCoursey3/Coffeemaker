from datetime import datetime

iterationFile = open(".github/iteration.txt")
iteration = iterationFile.read()

dateSvgFile = open(".github/badges/date.svg")
dateSvg = dateSvgFile.read()

iterationSvgFile = open(".github/badges/iteration.svg")
iterationSvg = iterationSvgFile.read()

finalIterationSvg = iterationSvg.replace("//ITER//", iteration)
finalDateSvg = dateSvg.replace("//DATE//", datetime.now().strftime("%m/%d/%Y"))

open(".github/badges/date_gen.svg", "w").write(finalDateSvg)
open(".github/badges/iteration_gen.svg", "w").write(finalIterationSvg)