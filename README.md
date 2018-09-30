# loomomakeathon
This is a project to make a tour at uw makerspace for uw makeathon competition. We build this software based on the VLSsample code giveb by segway.com, the producer of loomo.
However, we change most of the components and used speaker and pose 2D to realize the function.
Hope someone might need it...

## Regression Analysis
The code for locomotion supposed not that complex. However, unfortunately, our loomo robot has some problems that cannot go straight with corresponding command, so the coordinate of Navigation Mode completely messed up so that we have to deal with complicated regression analysis and more than 100 times trials in order to make it able to navigate in a narrow and small indoor space.
