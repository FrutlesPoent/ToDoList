package com.example.welcome;

public class States {

    private boolean checked = false;
    private String taskText = null;
    private int priority = 0;


    public States(int priority, String taskText){
        this.priority = priority;
        this.taskText = taskText;
        this.checked = false;
    }


    public String getTaskText(){
        return taskText;
    }

    public int getPriority(){
        return priority;
    }

    public boolean getChecked(){
        return checked;
    }

    public void setPriority(int priority){
        this.priority = priority;
    }

    public void setChecked(boolean checked){
        this.checked = checked;
    }
}
