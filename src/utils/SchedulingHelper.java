package utils;

import models.ProcessModel;
import models.AdvancedProcessModel;

import java.util.ArrayList;
import java.util.Comparator;

public class SchedulingHelper {
    // implements shortest job first algorithm

    public static ArrayList<ProcessModel> getSJFOrder(ArrayList<ProcessModel> processes) {
        ArrayList<ProcessModel> processModelsClone = (ArrayList<ProcessModel>) processes.clone();
        processModelsClone.sort(Comparator.comparingInt(ProcessModel::getArrivalTime));

        int totalElapsedTime = 0;
        ArrayList<ProcessModel> executedOrder = new ArrayList<ProcessModel>();
        while (!processModelsClone.isEmpty()) {
            ProcessModel processModel = getLeastBurstTime(totalElapsedTime, processModelsClone);
            executedOrder.add(processModel);
            processModelsClone.remove(processModel);
            totalElapsedTime += processModel.getBurstTime();
        }
        return executedOrder;
    }

    private static ProcessModel getLeastBurstTime(int totalElapsedTime, ArrayList<ProcessModel> processModelsClone) {
        int minIndex = 0;
        for (int i = 0; i < processModelsClone.size(); i++) {
            if (processModelsClone.get(i).getArrivalTime() <= totalElapsedTime && processModelsClone.get(i).getBurstTime() < processModelsClone.get(minIndex).getBurstTime()) {
                minIndex = i;
            }
        }
        return processModelsClone.get(minIndex);
    }

    public static ArrayList<ProcessModel> shortestJobFirst(ArrayList<ProcessModel> processes) {
        ArrayList<ProcessModel> executionOrder = getSJFOrder(processes);
        executionOrder.get(0).setWaitingTime(0);
        executionOrder.get(0).setTurnaroundTime(executionOrder.get(0).getBurstTime());
        for (int i = 1; i < executionOrder.size(); i++) {
            executionOrder.get(i).setWaitingTime(executionOrder.get(i - 1).getWaitingTime() + executionOrder.get(i - 1).getArrivalTime() + executionOrder.get(i - 1).getBurstTime() - executionOrder.get(i).getArrivalTime());
            executionOrder.get(i).setTurnaroundTime(executionOrder.get(i).getWaitingTime() + executionOrder.get(i).getBurstTime());
        }
        return executionOrder;
    }

    public static ArrayList<ProcessModel> firstComeFirstServe(ArrayList<ProcessModel> processes) {
        processes.sort(Comparator.comparingInt(ProcessModel::getArrivalTime));

        for (int i = 0; i < processes.size(); i++) {
            ProcessModel processModel = processes.get(i);

            int waitingTime = 0;
            int turnaroundTime;

            for (int j = 0; j < i; j++) {
                ProcessModel processModel1 = processes.get(j);
                waitingTime += processModel1.getBurstTime();

            }
            turnaroundTime = waitingTime + processModel.getBurstTime();
            processModel.setTurnaroundTime(turnaroundTime);
            processModel.setWaitingTime(waitingTime);

        }
        return processes;
    }

    public static ArrayList<ProcessModel> roundRobin(ArrayList<ProcessModel> processes, int timeQuantum) {

        ArrayList<AdvancedProcessModel> executingList = new ArrayList<>();
        int currentTime = 0;
        int index = 0;
        ProcessModel p = getProcessFor(index, processes);
        while (p != null) {
            if(p.isFinished()){
                index++;
                if(index == processes.size()){
                    index = 0;
                }
                p = getProcessFor(index, processes);
                continue;
            }
            if (p.getRemainingTime() < timeQuantum) {
                int remTime = p.getRemainingTime();
                p.setRemainingTime(0);
                p.setFinished(true);
                executingList.add(new AdvancedProcessModel(p,currentTime, currentTime+remTime));
                currentTime += remTime;
                p.setTurnaroundTime(currentTime-p.getArrivalTime());
                index++;
            } else {
                p.setRemainingTime(p.getRemainingTime()-timeQuantum);
                executingList.add(new AdvancedProcessModel(p,currentTime, currentTime+timeQuantum));
                currentTime += timeQuantum;
                index++;
            }
            if(index == processes.size()){
                index = 0;
            }
            p = getProcessFor(index, processes);
        }
        ArrayList<ProcessModel> pList = new ArrayList<>();

        for(int i = executingList.size()-1; i >= 0; i--){
            AdvancedProcessModel sjp = executingList.get(i);
            if (pList.contains(sjp.getProcess())) continue;

            ProcessModel pr = sjp.getProcess();
            pr.setWaitingTime(getWaitingTime(sjp, executingList));
            pList.add(pr);
        }
        return pList;
    }

    private static ProcessModel getProcessFor(int index, ArrayList<ProcessModel> processes) {
        boolean finished = true;
        for (int j = 0; j < processes.size(); j++) {
            if (!processes.get(j).isFinished()) {
                finished = false;
                break;
            }
        }
        if (finished) {
            return null;
        }
        return processes.get(index);
    }

    public static ArrayList<ProcessModel> priorityScheduling(ArrayList<ProcessModel> processes) {
        processes.sort(Comparator.comparingInt(ProcessModel::getPriority));
        processes.get(0).setWaitingTime(0);
        processes.get(0).setTurnaroundTime(processes.get(0).getBurstTime());

        for (int i = 1; i < processes.size(); i++) {
            ProcessModel p = processes.get(i);
            p.setWaitingTime(processes.get(i - 1).getWaitingTime() + processes.get(i - 1).getBurstTime());
            p.setTurnaroundTime(processes.get(i).getWaitingTime() + processes.get(i).getBurstTime());
        }
        return processes;
    }

    public static ArrayList<ProcessModel> shortestJobFirstPreemptive(ArrayList<ProcessModel> processes) {
        processes.sort(Comparator.comparingInt(ProcessModel::getArrivalTime));

        ArrayList<AdvancedProcessModel> executingProcessList = new ArrayList<>();

        int currentTime = 0;
        int index = 0;
        ProcessModel p = getProcessForTime(currentTime, processes);

        while (p != null && !p.isFinished()) {
            if (executingProcessList.size() >= 1) {
                if (executingProcessList.get(index - 1).getProcess() == p) {
                    executingProcessList.get(index - 1).getProcess().setRemainingTime(executingProcessList.get(index - 1).getProcess().getRemainingTime() - 1);
                    executingProcessList.get(index - 1).setEndedExecuting(executingProcessList.get(index - 1).getEndedExecuting() + 1);
                    if (executingProcessList.get(index - 1).getProcess().getRemainingTime() <= 0) {
                        executingProcessList.get(index - 1).getProcess().setFinished(true);
                    }
                    currentTime += 1;
                } else {
                    p.setRemainingTime(p.getRemainingTime() - 1);
                    executingProcessList.add(new AdvancedProcessModel(p, currentTime, currentTime + 1));

                    if (p.getRemainingTime() <= 0) {
                        p.setFinished(true);
                    }
                    index++;
                    currentTime += 1;
                }
            } else {
                executingProcessList.add(new AdvancedProcessModel(p, currentTime, currentTime + 1));
                p.setRemainingTime(p.getRemainingTime() - 1);
                if (p.getRemainingTime() <= 0) {
                    p.setFinished(true);
                }
                executingProcessList.get(index).setProcess(p);
                index++;
                currentTime += 1;
            }
            p = getProcessForTime(currentTime, processes);
        }
        ArrayList<ProcessModel> pList = new ArrayList<>();
        for (int i = 0; i < executingProcessList.size(); i++) {
            AdvancedProcessModel sjp = executingProcessList.get(i);
            if (pList.contains(sjp.getProcess())) continue;

            ProcessModel pr = sjp.getProcess();
            pr.setWaitingTime(getWaitingTime(sjp, executingProcessList));
            pr.setTurnaroundTime(getTurnaroundTime(sjp, executingProcessList));
            pList.add(pr);
        }
        return pList;
    }

    private static int getTurnaroundTime(AdvancedProcessModel sjp, ArrayList<AdvancedProcessModel> executingProcessList) {
        int tat = 0;
        AdvancedProcessModel last = sjp;
        for (int i = 0; i < executingProcessList.size(); i++) {
            if (sjp.getProcess() == executingProcessList.get(i).getProcess()) {
                last = executingProcessList.get(i);
            }
        }
        tat = last.getEndedExecuting() - last.getProcess().getArrivalTime();
        return tat;
    }

    private static int getWaitingTime(AdvancedProcessModel sjp, ArrayList<AdvancedProcessModel> executingProcessList) {
        int total = 0;
        ArrayList<AdvancedProcessModel> sameProcessList = new ArrayList<>();
        for (int i = 0; i < executingProcessList.size(); i++) {
            AdvancedProcessModel sjfp = executingProcessList.get(i);
            if (sjfp.getProcess().equals(sjp.getProcess())) {
                sameProcessList.add(sjfp);
            }
        }
        for (int j = 0; j < sameProcessList.size(); j++) {
            if (j == 0) {
                total += (sameProcessList.get(j).getStartedExecuting() - sameProcessList.get(j).getProcess().getArrivalTime());
            } else {
                total += (sameProcessList.get(j).getStartedExecuting() - sameProcessList.get(j - 1).getEndedExecuting());
            }
        }
        return total;
    }

    private static ProcessModel getProcessForTime(int currentTime, ArrayList<ProcessModel> processes) {
        boolean finished = true;
        for (int j = 0; j < processes.size(); j++) {
            if (!processes.get(j).isFinished()) {
                finished = false;
                break;
            }
        }
        if (finished) {
            return null;
        }
        int minIndex = 0;
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).getArrivalTime() <= currentTime && processes.get(i).getRemainingTime() < processes.get(minIndex).getRemainingTime() && !processes.get(i).isFinished()) {
                minIndex = i;
            }
        }
        return processes.get(minIndex);
    }
}
