package models;

public class AdvancedProcessModel {
    ProcessModel process;
    int startedExecuting;
    int endedExecuting;

    public AdvancedProcessModel(ProcessModel process, int startedExecuting, int endedExecuting) {
        this.process = process;
        this.startedExecuting = startedExecuting;
        this.endedExecuting = endedExecuting;
    }

    public ProcessModel getProcess() {
        return process;
    }

    public void setProcess(ProcessModel process) {
        this.process = process;
    }

    public int getStartedExecuting() {
        return startedExecuting;
    }

    public void setStartedExecuting(int startedExecuting) {
        this.startedExecuting = startedExecuting;
    }

    public int getEndedExecuting() {
        return endedExecuting;
    }

    public void setEndedExecuting(int endedExecuting) {
        this.endedExecuting = endedExecuting;
    }
}
