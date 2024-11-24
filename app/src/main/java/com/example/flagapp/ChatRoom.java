package com.example.flagapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatRoom extends AppCompatActivity {

    private static final String TAG = "ChatRoom";
    private ChatMessageDAO mDAO;
    private MessageDatabase db;
    private ChatMessageAdapter myAdapter;
    private Button sendButton, receiveButton;
    private EditText messageEditText;
    private ChatMessage recentlyDeletedMessage;

    // ViewModel instance
    private final ChatRoomViewModel chatRoomViewModel = new ChatRoomViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        sendButton = findViewById(R.id.sendButton);
        receiveButton = findViewById(R.id.receiveButton);
        messageEditText = findViewById(R.id.messageEditText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe changes to the message list in the ViewModel
        chatRoomViewModel.getMessages().observe(this, new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(List<ChatMessage> messages) {
                if (myAdapter == null) {
                    myAdapter = new ChatMessageAdapter(messages, position -> showDeleteDialog(position));
                    recyclerView.setAdapter(myAdapter);
                } else {
                    myAdapter.notifyDataSetChanged();
                }
            }
        });

        // Handle Send button click
        sendButton.setOnClickListener(v -> {
            String text = messageEditText.getText().toString();
            if (text.isEmpty()) return;

            long timestamp = System.currentTimeMillis();
            ChatMessage newMessage = new ChatMessage(text, timestamp, false); // "false" means sent message
            addMessageToViewAndDatabase(newMessage);
        });

        // Handle Receive button click
        receiveButton.setOnClickListener(v -> {
            String text = messageEditText.getText().toString();
            if (text.isEmpty()) return;

            long timestamp = System.currentTimeMillis();
            ChatMessage newMessage = new ChatMessage(text, timestamp, true); // "true" means received message
            addMessageToViewAndDatabase(newMessage);
        });
    }

    private void addMessageToViewAndDatabase(ChatMessage newMessage) {
        // Add the message to the ViewModel
        chatRoomViewModel.addMessage(newMessage);

        // Add the message to the database
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            mDAO.insertMessage(newMessage);
        });

        messageEditText.setText(""); // Clear the input field
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Message")
                .setMessage("Do you want to delete this message?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    recentlyDeletedMessage = chatRoomViewModel.getMessages().getValue().get(position);
                    chatRoomViewModel.removeMessage(position); // Remove from ViewModel

                    // Delete from database in a background thread
                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(() -> {
                        mDAO.deleteMessage(recentlyDeletedMessage);
                    });

                    showUndoSnackbar();
                })
                .setNegativeButton("No", null)
                .show();
    }



    private void showUndoSnackbar() {
        Snackbar.make(findViewById(R.id.recyclerView), "Message deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> {
                    if (recentlyDeletedMessage != null) {
                        chatRoomViewModel.addMessage(recentlyDeletedMessage); // Add back to ViewModel

                        // Reinsert the message into the database
                        Executor thread = Executors.newSingleThreadExecutor();
                        thread.execute(() -> {
                            mDAO.insertMessage(recentlyDeletedMessage);
                        });
                    }
                }).show();
    }
}
