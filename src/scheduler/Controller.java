package scheduler;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import models.AlgorithmType;
import models.ProcessModel;
import models.ProcessSimulation;

public class Controller {
    public Button addProcessButton;

    public TextField burstTime;
    public Button simulateButton;
    public ComboBox<AlgorithmType> algorithmType;
    public ListView<ProcessModel> processTabList;
    public ListView<ProcessSimulation> simulationTabList;
    public TextField processName;

}
