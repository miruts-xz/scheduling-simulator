package scheduler;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.AlgorithmType;
import models.ProcessModel;
import utils.SchedulingHelper;

import java.util.ArrayList;

public class Controller {
    public Button addProcessButton;

    public ArrayList<ProcessModel> processes = new ArrayList<>();
    public TextField burstTime;
    public Button simulateButton;
    public ComboBox<AlgorithmType> algorithmType;
    public TextField processName;
    public Label simulationTab;
    public Label appTitle;
    public Label ProcessTab;
    public TableView<ProcessModel> processTable;
    public TableView<ProcessModel> simulationTable;
    public TableColumn<ProcessModel, Integer> sProcessNoCol;
    public TableColumn<ProcessModel, String> sProcessNameCol;
    public TableColumn<ProcessModel, Integer> waitingTimeCol;
    public TableColumn<ProcessModel, Integer> turnaroundTimeCol;
    public TableColumn<ProcessModel, Integer> processNoCol;
    public TableColumn<ProcessModel, String> processNameCol;
    public TableColumn<ProcessModel, Integer> burstTimeCol;
    public Label timeQuantum;
    public Slider timeQuantumSlider;
    public Label quantumLabel;
    public TextField arrivalTime;
    public Label arrivalTimeError;
    public TableColumn<ProcessModel, Integer> arrivalTimeCol;
    public Label averageWT;
    public Label AverageTAT;
    public TextField priority;
    public TableColumn<ProcessModel, Integer> priorityCol;
    public Button resetButton;

    @FXML
    private void initialize() {
        algorithmType.getItems().addAll(AlgorithmType.FCFS, AlgorithmType.PS, AlgorithmType.RoundRobin, AlgorithmType.SJF, AlgorithmType.SJFP);
        processNoCol.setCellValueFactory(new PropertyValueFactory<ProcessModel, Integer>("processNumber"));
        processNameCol.setCellValueFactory(new PropertyValueFactory<ProcessModel, String>("processName"));
        burstTimeCol.setCellValueFactory((new PropertyValueFactory<ProcessModel, Integer>("burstTime")));
        arrivalTimeCol.setCellValueFactory(new PropertyValueFactory<ProcessModel, Integer>("arrivalTime"));
        int value = (int) timeQuantumSlider.getValue();


        priorityCol.setCellValueFactory(new PropertyValueFactory<ProcessModel, Integer>("priority"));
        sProcessNoCol.setCellValueFactory(new PropertyValueFactory<>("processNumber"));
        sProcessNameCol.setCellValueFactory(new PropertyValueFactory<>("processName"));
        waitingTimeCol.setCellValueFactory((new PropertyValueFactory<ProcessModel, Integer>("waitingTime")));
        turnaroundTimeCol.setCellValueFactory((new PropertyValueFactory<ProcessModel, Integer>("turnaroundTime")));

        arrivalTime.setText(String.valueOf(0));
        algorithmType.setValue(AlgorithmType.FCFS);
        timeQuantum.setText(String.valueOf(value));
        timeQuantumSlider.valueProperty().addListener(
                (observable, oldValue, newValue) -> timeQuantum.setText(String.valueOf(newValue.intValue())));

        arrivalTime.textProperty().addListener((observable, oldValue, newValue) -> arrivalTimeError.setVisible(false));

    }


    @FXML
    private void simulate() {
        AlgorithmType algorithmType = this.algorithmType.getSelectionModel().getSelectedItem();
        ArrayList<ProcessModel> simulations;
        adjustArrivalTime();
        switch (algorithmType) {
            case SJF:
                simulations = SchedulingHelper.shortestJobFirst(processes);
                averageWT.setText(String.valueOf(getAverageWaiting(simulations)));
                AverageTAT.setText(String.valueOf(getAverageTurnaround(simulations)));
                simulationTable.getItems().setAll(simulations);
                break;
            case RoundRobin:
                simulations = SchedulingHelper.roundRobin(processes, Integer.parseInt(timeQuantum.getText()));
                AverageTAT.setText(String.valueOf(getAverageTurnaround(simulations)));
                averageWT.setText(String.valueOf(getAverageWaiting(simulations)));
                simulationTable.getItems().setAll(simulations);
                break;
            case FCFS:
                simulations = SchedulingHelper.firstComeFirstServe(processes);
                averageWT.setText(String.valueOf(getAverageWaiting(simulations)));
                AverageTAT.setText(String.valueOf(getAverageTurnaround(simulations)));
                simulationTable.getItems().setAll(simulations);
                break;
            case SJFP:
                simulations = SchedulingHelper.shortestJobFirstPreemptive(processes);
                averageWT.setText(String.valueOf(getAverageWaiting(simulations)));
                AverageTAT.setText(String.valueOf(getAverageTurnaround(simulations)));
                simulationTable.getItems().setAll(simulations);
                break;
            case PS:
                simulations = SchedulingHelper.priorityScheduling(processes);
                averageWT.setText(String.valueOf(getAverageWaiting(simulations)));
                AverageTAT.setText(String.valueOf(getAverageTurnaround(simulations)));
                simulationTable.getItems().setAll(simulations);
                break;
            default:


        }
    }

    private int getAverageWaiting(ArrayList<ProcessModel> simulations) {
        int total = 0;
        for (ProcessModel p :
                simulations) {
            total += p.getWaitingTime();
        }
        return total / simulations.size();
    }

    private int getAverageTurnaround(ArrayList<ProcessModel> simulations) {
        int total = 0;
        for (ProcessModel p :
                simulations) {
            total += p.getTurnaroundTime();
        }
        return total / simulations.size();
    }

    private void adjustArrivalTime() {
        System.out.println("PROCESSES");
        System.out.println(processes);
        int initialArrival = processes.get(0).getArrivalTime();
        for (int i = 0; i < processes.size(); i++) {
            int arrivalTime = processes.get(i).getArrivalTime();
            int newArrival = arrivalTime - initialArrival;
            if (newArrival < 0) {
                newArrival = arrivalTime + 60 - initialArrival;
            }
            processes.get(i).setArrivalTime(newArrival);
        }
    }

    @FXML
    private void addNewProcess() {

        int currentQuantity = processTable.getItems().size();

        ProcessModel processModel;

        try {
            processModel = new ProcessModel(Integer.parseInt(burstTime.getText()), processName.getText(), Integer.parseInt(arrivalTime.getText()));
            processModel.setPriority(Integer.parseInt(priority.getText()));
            processModel.setProcessNumber(currentQuantity);
            processTable.getItems().add(processModel);
            processes.add(processModel);
            System.out.println(processes);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    @FXML
    public void onReset(ActionEvent actionEvent) {
        processes.clear();
        simulationTable.getItems().setAll();
        processTable.getItems().setAll();
        processName.setText("");
        burstTime.setText("");
        arrivalTime.setText("0");
        priority.setText("0");
    }
}
