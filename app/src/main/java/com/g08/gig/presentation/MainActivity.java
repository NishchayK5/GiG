//package com.g08.gig.presentation;
//
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.g08.gig.R;
//import com.g08.gig.application.GiGApp;
//import com.g08.gig.business.services.ClientUserService;
//import com.g08.gig.models.User;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainActivity extends AppCompatActivity {
//
//    private ClientUserService userService;
//
//    private EditText firstNameInput;
//    private EditText lastNameInput;
//    private EditText emailInput;
//
//    private UserAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        userService = ((GiGApp) getApplication()).getClientUserService();
//
//        firstNameInput = findViewById(R.id.firstNameInput);
//        lastNameInput = findViewById(R.id.lastNameInput);
//        emailInput = findViewById(R.id.emailInput);
//
//        Button addButton = findViewById(R.id.addButton);
//
//        RecyclerView list = findViewById(R.id.userList);
//        adapter = new UserAdapter(new ArrayList<>());
//        list.setLayoutManager(new LinearLayoutManager(this));
//        list.setAdapter(adapter);
////        addButton.setOnClickListener(v -> {
////            try {
////                userService.addClientUser(
////                        firstNameInput.getText().toString(),
////                        lastNameInput.getText().toString(),
////                        emailInput.getText().toString()
////                );
////
////                clearInputs();
////                refreshList();
////            } catch (com.g08.gig.business.validation.ValidationException ex) {
////                emailInput.setError(ex.getMessage());
////                emailInput.requestFocus();
////            }
////        });
//        refreshList();
//    }
//
//    private void refreshList() {
//        //List<User> users = userService.getAllUsers();
//        //adapter.setItems(users);
//    }
//
//    private void clearInputs() {
//        firstNameInput.setText("");
//        lastNameInput.setText("");
//        emailInput.setText("");
//    }
//
//}
