# How to Run

Run `./script.sh` for the code. You can modify the values in `TestingAlgo.Values` to see their effect on the code.

## Code Logic

The code starts from `main`, where a dataset is taken as string input. The dataset must be in the project folder and should be formatted as `(x,y)` in degrees or as input columns in `TestingAlgo.Values`.

A Gridfile is constructed from the dataset and is used as a parameter. You have 8 options to run on the dataset, which consist of 4 frameworks of 2 algorithms. To run each framework individually, use the commands below:

- **Naive Serial Version**: `runNaiveTester(gridFile, events);`
- **Naive Habenero Java Version**: `runNaiveTesterHJ(gridFile, events);`
- **Naive Fork Join Pool Version**: `runNaiveTesterFJP(gridFile, events);`
- **Naive Java Open-MP Version**: `runNaiveTesterJOMP(gridFile, events);`
- **Moving Circle Serial Version**: `runMovingCircleTester(gridFile, events);`
- **Moving Circle Habenero Java Version**: `runMovingCircleTesterHJ(gridFile, events);`
- **Moving Circle Java Fork Join Pool Version**: `runMovingCircleTesterJvFP(gridFile, events);`
- **Moving Circle Java Open-MP Version**: `runMovingCircleTesterJOMP(gridFile, events);`

### Requirements for Running

To run the above, you need to provide a gridfile of the dataset and events from the dataset. To construct these, see below:

#### Events
```java
String filename = "your_file_name";
ArrayList<Events> events = readDataFile(fileName);
```

#### Gridfile
```java
GridFile gridFile = new GridFile();
GridCell gridCell = new GridCell(gridFile, minLat, maxLat, minLon, maxLon);
gridCell = gridCell.getGridCell(gridFile);
gridFile.make(events, gridCell);
```

*Values of `minLat`, `maxLat`, `minLon`, and `maxLon` can be `360`, `-360`, `360`, `-360`.*

## Experiments for Analysis

Apart from the standalone functions, there are several experiments designed for analysis. Details are given below:

- **Comparing all frameworks of Naive**: `exp1_phase1(gridFile, events)`
- **Comparing all frameworks of Naive (Monte Carlo)**: `exp1_phase2(events.size(), area)`
- **Comparing all frameworks of Moving Circle**: `exp2_phase1(gridFile, events)`
- **Comparing all frameworks of Moving Circle (Monte Carlo)**: `exp2_phase2(events.size(), area)`
- **Comparing run-times with varying growth rates**: `exp3(gridFile, events)`

## Visual Representation of Top Circles

To print the top circles or visualize them, follow these steps:

In both algorithms, the top `n` circles are printed and visualized. The values can be changed in `TestingAlgo/Values.java`, where the parameters are `top_circles_for_visualize` and `top_circles_for_print`.

To avoid the window popup for visualization (in case the value is zero), comment the following lines:

- In `Naive/Naive.java`, line number 282: 
  ```java
  vis.drawCircles(events, core_circles, "Naive");
  ```
  (inside the function `afterNaive()`)

- In `Moving_Circle/MovingCircle.java`, line number 350:
  ```java
  vis.drawCircles(events, moving_circles_for_visualize, "Moving circle");
  ```
  (inside the function `visualizedata()`)

## Part 2 - Spatial-Scan-Statistics-Linear

Click [here](https://github.com/guroosh/Spatial-Scan-Statistics-Linear) to see the 2nd part of the project.

## Contact

If you have any doubts, drop a mail to [chaudhary14032@iiitd.ac.in](mailto:chaudhary14032@iiitd.ac.in) with an appropriate subject line.
