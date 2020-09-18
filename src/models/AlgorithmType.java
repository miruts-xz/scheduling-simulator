package models;

public enum AlgorithmType {
    FCFS, SJF, SJFP,RoundRobin, PS;

    @Override
    public String toString() {
        return this.name();
    }
}
