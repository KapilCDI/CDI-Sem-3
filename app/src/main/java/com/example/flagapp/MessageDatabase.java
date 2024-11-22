
package com.example.flagapp;

import androidx.room.Database;

@Database(entities = {ChatMessage.class}, version = 1)
public abstract class MessageDatabase extends ChatRoom {
    public abstract ChatMessageDAO chatMessageDAO();
}