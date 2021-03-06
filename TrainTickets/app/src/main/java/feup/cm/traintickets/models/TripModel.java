package feup.cm.traintickets.models;

import java.util.List;

public class TripModel {

    private int id;
    private String description;
    private String direction;
    private String increment;
    private List<StepModel> steps;
    private TrainModel train;

    public TripModel(int id, String description, String direction, String increment, TrainModel train,
                     List<StepModel> steps) {
        this.id = id;
        this.description = description;
        this.direction = direction;
        this.increment = increment;
        this.train = train;
        this.steps = steps;
    }
    public TripModel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public String getDirection() {
        return direction;
    }
    public String getIncrement() {
        return increment;
    }
    public List<StepModel> getSteps() {
        return steps;
    }
    public TrainModel getTrain() {
        return train;
    }
}
