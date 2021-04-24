package com.example.welcome;

public class States {

    private boolean checked = false;
    private String taskText = null;
    private String priority = "";
    private int idTask; // Id from database for delete

    public States(int priority, String taskText, int id){
        this.priority = checkPriority(priority);
        this.taskText = taskText;
        this.checked = false;
        this.idTask = id;
    }

    public States(int priority, String taskText){
        this.priority = checkPriority(priority);
        this.taskText = taskText;
        this.checked = false;
    }

    private String checkPriority(int priority){
        if (priority == 50)
            return "Low";
        if(priority == 60)
            return "Medium";
        if(priority == 70)
            return "High";
        return "Zero";

    }


    public int getIdTask(){
        return idTask;
    }

    public String getTaskText(){
        return taskText;
    }


    public String getPriority(){
        return priority;
    }

    public boolean getChecked(){
        return checked;
    }

    public void setPriority(int priority){
        this.priority = checkPriority(priority);
    }


    public void setChecked(boolean checked){
        this.checked = checked;
    }
}
