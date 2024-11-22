package com.example.flagapp;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ChatRoomViewModel extends ViewModel {

    // LiveData for the list of messages
    private MutableLiveData<ArrayList<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());

    // Getter for LiveData
    public MutableLiveData<ArrayList<ChatMessage>> getMessages() {
        return messages;
    }

    // Add a new message to the list
    public void addMessage(ChatMessage message) {
        ArrayList<ChatMessage> currentMessages = messages.getValue();
        if (currentMessages != null) {
            currentMessages.add(message);
            messages.setValue(currentMessages); // Notify observers
        }
    }

    // Remove a message from the list
    public void removeMessage(int position) {
        ArrayList<ChatMessage> currentMessages = messages.getValue();
        if (currentMessages != null && position >= 0 && position < currentMessages.size()) {
            currentMessages.remove(position);
            messages.setValue(currentMessages); // Notify observers
        }
    }
}
