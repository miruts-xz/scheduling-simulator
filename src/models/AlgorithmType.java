package models;

// Algorithm Type represents requested Scheduling algorithm type.
public enum AlgorithmType {
    FCFS, SJF, SJFP,RoundRobin, PS;
    @Override
    public String toString() {
        return this.name();
    }
}
