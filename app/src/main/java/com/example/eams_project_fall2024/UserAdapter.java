package com.example.eams_project_fall2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList; // This will hold the list of users
    private Context context; // This will hold the context for inflating views

    // Constructor
    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getName()); // Update this line based on your User class properties
        // Set other user fields as necessary
    }

    @Override
    public int getItemCount() {
        return userList.size(); // Return the size of the user list
    }

    // ViewHolder class
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView; // Define your TextView and other UI elements

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.firstNameTextView); // Update with your actual view IDs
            // Initialize other views here
        }
    }
}
