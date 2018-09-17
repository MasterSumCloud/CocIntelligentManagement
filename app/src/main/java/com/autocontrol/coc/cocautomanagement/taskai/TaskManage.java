package com.autocontrol.coc.cocautomanagement.taskai;

import java.util.ArrayList;

/**
 * Created by mac_py on 17/09/2018.
 */

public class TaskManage {
    private ArrayList<Task> tasksList = null;

    public TaskManage() {
        tasksList = new ArrayList<>();
    }

    public void cleanTask(){
        tasksList.clear();
    }

    public void addTask(Task task){
        tasksList.add(task);
    }

    public void removeTask(Task task){
        tasksList.remove(task);
    }

    public void removeTask(int position) {
        tasksList.remove(position);
    }
}
