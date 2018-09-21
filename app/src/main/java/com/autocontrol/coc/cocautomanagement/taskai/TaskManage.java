package com.autocontrol.coc.cocautomanagement.taskai;

import android.content.Context;

import com.autocontrol.coc.cocautomanagement.task.TaskDetail;

import java.util.ArrayList;

/**
 * Created by mac_py on 17/09/2018.
 */

public class TaskManage {
    private Context mAppContext;
    private ArrayList<Task> tasksList = null;

    public TaskManage(Context applicationContext) {
        tasksList = new ArrayList<>();
        this.mAppContext = applicationContext;
    }

    public void cleanTask() {
        tasksList.clear();
    }

    public void addTask(Task task) {
        tasksList.add(task);
    }

    public void removeTask(Task task) {
        tasksList.remove(task);
    }

    public void removeTask(int position) {
        tasksList.remove(position);
    }

    public boolean hasTask() {
        return tasksList.size() > 0;
    }

    public ArrayList<Task> getTasksList (){
        return tasksList;
    }

    public void executeTask() {
        while (hasTask()) {
            Task task = tasksList.get(0);
            TaskDetail taskDetail = new TaskDetail(mAppContext);
            taskDetail.startTask(task.getTaskType());
            removeTask(0);
        }
    }
}
