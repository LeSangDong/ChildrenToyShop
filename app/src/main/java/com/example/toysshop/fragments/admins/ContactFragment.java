package com.example.toysshop.fragments.admins;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.adapter.UserChatAdapter;
import com.example.toysshop.databinding.FragmentContactBinding;
import com.example.toysshop.model.User;
import com.example.toysshop.model.UserChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ContactFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

   private FragmentContactBinding binding;
   private UserChatAdapter userChatAdapter;
    Set<String> userIds = new HashSet<>();
    List<UserChat> userList = new ArrayList<>();
    private   String lastMessage;
    private    String chatWithUserId;
    private String lastMessageTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentContactBinding.inflate(inflater,container,false);
       return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));


        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.setRefreshing(true);

        loadUserChat();





    }

    private void loadUserChat() {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("account");

        messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIds.clear();
                userList.clear();
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot messageSnapshot : chatSnapshot.getChildren()) {
                        String userId = messageSnapshot.child("userId").getValue(String.class);
                        if (userId != null) {
                            userIds.add(userId);
                        }
                    }
                }

                for (String userId : userIds) {
                    usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String role = snapshot.child("role").getValue(String.class);
                            if ("user".equals(role)) {
                                String name = snapshot.child("info").child("name").getValue(String.class);
                                String avatarUrl = snapshot.child("info").child("avatarUrl").getValue(String.class);
                                String email = snapshot.child("info").child("email").getValue(String.class);

                                if (name != null) {
                                    UserChat userChat = new UserChat(userId, name, email, avatarUrl);
                                    getLastMessage(userChat);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getLastMessage(UserChat userChat) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String chatNode = generateChatNode(userChat.getUserId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference lastMessageRef = FirebaseDatabase.getInstance().getReference("messages").child(chatNode);

        lastMessageRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                     lastMessage = messageSnapshot.child("message").getValue(String.class);
                     lastMessageTime = messageSnapshot.child("timestamp").getValue(String.class);
                    String senderUserId = messageSnapshot.child("userId").getValue(String.class);
                    Boolean isImage = messageSnapshot.child("image").getValue(Boolean.class);
                    if (isImage != null && isImage) {
                        lastMessage = "Đã gửi 1 ảnh";
                    }

                    if (senderUserId != null && !senderUserId.equals(currentUserId)) {
                        lastMessage =  ""+lastMessage;
                    } else {
                        lastMessage = "Bạn: " + lastMessage;
                    }

                    userChat.setLastMessage(lastMessage);
                    userChat.setLastMessageTime(lastMessageTime != null ? lastMessageTime : "N/A");
                }

                userList.add(userChat);
                Collections.sort(userList);
                if (userChatAdapter == null) {
                    userChatAdapter = new UserChatAdapter(userList);
                    binding.recyclerview.setAdapter(userChatAdapter);
                } else {
                    userChatAdapter.notifyDataSetChanged();
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private String generateChatNode(String userId1, String userId2) {
        if (userId1.compareTo(userId2) > 0) {
            return userId2 + "_" + userId1;
        } else {
            return userId1 + "_" + userId2;
        }
    }

    @Override
    public void onRefresh() {
        loadUserChat();
    }
}