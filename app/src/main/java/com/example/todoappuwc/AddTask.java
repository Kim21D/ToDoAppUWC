package com.example.todoappuwc;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTask extends BottomSheetDialogFragment {
    public static final String TAG = "AddNewTask";

    private String dueDate = "";
    private String id = "";
    private String dueDateUpdate = "";
    private String task = "";
    private TextView setDueDate;
    private EditText taskInfo;
    private Button createButton;
    private FirebaseFirestore firestore;
    private Context context;

    public static AddTask instance(){
        return new AddTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDueDate = view.findViewById(R.id.set_data);
        taskInfo = view.findViewById(R.id.task_info);
        createButton = view.findViewById(R.id.save_button);
        firestore = FirebaseFirestore.getInstance();
        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            task = bundle.getString("task");
            id = bundle.getString("id");
            dueDate = bundle.getString("date");
            taskInfo.setText(task);
            setDueDate.setText(dueDate);
            if(task.length() > 0 ){
                createButton.setEnabled(false);
                createButton.setBackgroundColor(Color.GRAY);
            }
        }
        createButton.setEnabled(false);
        createButton.setBackgroundColor(Color.GRAY);
        taskInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    createButton.setEnabled(false);
                    createButton.setBackgroundColor(Color.GRAY);
                } else {
                    createButton.setEnabled(true);
                    createButton.setBackgroundColor(Color.GREEN);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        boolean finalIsUpdate = isUpdate;
        setDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalIsUpdate) {
                    createButton.setEnabled(true);
                    createButton.setBackgroundColor(Color.GREEN);
                }

                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                int day = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        setDueDate.setText(d + "." + (m + 1) + "." + y);
                        dueDate = d + "." + (m + 1) + "." + y;

                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = taskInfo.getText().toString();
                if (finalIsUpdate) {
                    firestore.collection("Task").document(id).update("task", task, "date", dueDate);
                    Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();
                } else {
                    if (task.isEmpty()) {
                        Toast.makeText(context, "Task Info was empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> tasks = new HashMap<>();
                        tasks.put("task", task);
                        tasks.put("date", dueDate);
                        tasks.put("Status", 0);
                        tasks.put("time", FieldValue.serverTimestamp());

                        firestore.collection("Task").add(tasks).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Task Created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.getMessage();
                            }
                        });
                    }
                }
                dismiss();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if ( activity instanceof OnDialogCloseListener){
            ((OnDialogCloseListener)activity).onDialogClose(dialog);
        }
    }
}
