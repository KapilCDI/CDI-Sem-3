package com.example.flagapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private final List<ChatMessage> messages;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ChatMessageAdapter(List<ChatMessage> messages, OnItemClickListener listener) {
        this.messages = messages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        // Set message text and timestamp
        holder.messageText.setText(message.getMessageText());
        holder.timestamp.setText(
                new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date(message.getTimestamp()))
        );

        // Configure DP visibility and alignment
        if (message.isReceiveMessage()) {
            // Receiver message
            holder.senderDp.setVisibility(View.GONE);
            holder.receiverDp.setVisibility(View.VISIBLE);

        } else {
            // Sender message
            holder.senderDp.setVisibility(View.VISIBLE);
            holder.receiverDp.setVisibility(View.GONE);
        }

        // Set click listener for delete dialog
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestamp;
        ImageView senderDp, receiverDp;

        public ViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestamp = itemView.findViewById(R.id.timestamp);
            senderDp = itemView.findViewById(R.id.senderDp);
            receiverDp = itemView.findViewById(R.id.receiverDp);
        }
    }
}