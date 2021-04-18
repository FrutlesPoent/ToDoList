package com.example.welcome;

public class States {

    private boolean checked = false;
    private String taskText = null;
    private String priority = "0";
    private int idTask; // Id from database for delete


//    public States(int priority, String taskText){
//        this.priority = priority;
//        this.taskText = taskText;
//        this.checked = false;
//    }

    public States(String priority, String taskText, int id){
        this.priority = priority;
        this.taskText = taskText;
        this.checked = false;
        this.idTask = id;
    }

    public States(String priority, String taskText){
        this.priority = priority;
        this.taskText = taskText;
        this.checked = false;
    }


    public int getIdTask(){
        return idTask;
    }

    public String getTaskText(){
        return taskText;
    }

//    public int getPriority(){
//        return priority;
//    }

    public String getPriority(){
        return priority;
    }

    public boolean getChecked(){
        return checked;
    }

    public void setPriority(String priority){
        this.priority = priority;
    }


    public void setChecked(boolean checked){
        this.checked = checked;
    }
}
