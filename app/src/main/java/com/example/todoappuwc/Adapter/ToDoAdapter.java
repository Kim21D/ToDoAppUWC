package com.example.todoappuwc.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoappuwc.AddTask;
import com.example.todoappuwc.MainActivity;
import com.example.todoappuwc.Model.ToDoModel;
import com.example.todoappuwc.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {
    private List<ToDoModel> todolist;
    private MainActivity activity;
    private FirebaseFirestore firestore;
    public ToDoAdapter(MainActivity activity, List<ToDoModel> todolist){
        this.todolist = todolist;
        this.activity = activity;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.task, parent, false);
        firestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    public void deleteTask(int pos){
        ToDoModel toDoModel = todolist.get(pos);
        firestore.collection("Task").document(toDoModel.TaskId).delete();
        todolist.remove(pos);
        notifyItemRemoved(pos);
    }

    public Context getContext(){
        return activity;
    }

    public void editTask(int pos){
        ToDoModel toDoModel = todolist.get(pos);
        Bundle bundle = new Bundle();
        bundle.putString("task", toDoModel.getTask());
        bundle.putString("date", toDoModel.getDate());
        bundle.putString("id", toDoModel.TaskId);

        AddTask addTask = new AddTask();
        addTask.setArguments(bundle);
        addTask.show(activity.getSupportFragmentManager(), addTask.getTag());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ToDoModel toDoModel = todolist.get(position);
        holder.checkBox.setText(toDoModel.getTask());
        if (!toDoModel.getDate().isEmpty()){
            holder.dueDate.setVisibility(View.VISIBLE);
            holder.dueDate.setText("Due Date on " + toDoModel.getDate());
        } else {
            holder.dueDate.setVisibility(View.INVISIBLE);
        }
        holder.checkBox.setChecked(toBoolean(toDoModel.getStatus()));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    firestore.collection("Task").document(toDoModel.TaskId).update("Status", 1);
                }else{
                    firestore.collection("Task").document(toDoModel.TaskId).update("Status", 0);
                }
            }
        });
    }

    private boolean toBoolean(int status){
        return status != 0;
    }
    @Override
    public int getItemCount() {
        return todolist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView dueDate;
        CheckBox checkBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dueDate = itemView.findViewById(R.id.date);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
