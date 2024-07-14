package com.example.toysshop.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.adapter.MessageAdapter;
import com.example.toysshop.databinding.ActivityChatBinding;
import com.example.toysshop.model.Chat;
import com.example.toysshop.model.Message;
import com.example.toysshop.model.UserChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityChatBinding binding;
    private FirebaseAuth auth;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private String currentUserId;
    private String chatWithUserId;

    private Uri imageUri;
    private  String url_user;
    private ActivityResultLauncher<Intent> resultLauncher;
    private DatabaseReference messagesDatabaseReference;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.setRefreshing(true);

        iNit();
        storageReference = FirebaseStorage.getInstance().getReference("chat_images");

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUserId = currentUser.getUid();

        //lay thong tin doi tac chat
        chatWithUserId = getIntent().getStringExtra("chatWithUserId");
        String user_name = getIntent().getStringExtra("user_name");
        url_user = getIntent().getStringExtra("url");
        Glide.with(this)
                        .load(url_user).placeholder(R.drawable.imag_user_default).into(binding.imageAvatar);
        binding.tvNameUser.setText(user_name);
        if (chatWithUserId == null) {
            Toast.makeText(this, "Chat partner not specified!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String chatNode = generateChatNode(currentUserId, chatWithUserId);
        messagesDatabaseReference = FirebaseDatabase.getInstance().getReference("messages").child(chatNode);

        binding.edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().trim().length() > 0) {
                    binding.btnSendMessage.setVisibility(View.VISIBLE);
                    binding.btnSendPhoto.setVisibility(View.GONE);
                } else {
                    binding.btnSendMessage.setVisibility(View.GONE);
                    binding.btnSendPhoto.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.btnSendPhoto.setOnClickListener(v->{
            openGallery();
        });
        //xu ly tac vu nguoi dung gui tin nhan
        binding.btnSendMessage.setOnClickListener(v->{
            String message = binding.edtMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message,false);
                binding.edtMessage.setText("");
            }
        });

        loadMessagesFromServer();

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        if (imageUri != null) {
                            // Gửi tin nhắn với hình ảnh
                            binding.swipeRefreshLayout.setRefreshing(true);
                            uploadImageToFirebase(imageUri);

                        }
                    }
                }
        );

        binding.imageAvatar.setOnClickListener(v->{
            Intent intent = new Intent(this, SeenImageActivity.class);
            intent.putExtra("image_item",url_user);
            startActivity(intent);
        });
        binding.btnBack.setOnClickListener(v->{
            finish();
        });

    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        sendMessage(imageUrl, true);
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        resultLauncher.launch(intent);

    }

    private void loadMessagesFromServer() {
        messageList = new ArrayList<>();
        messagesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                messageAdapter = new MessageAdapter(messageList, currentUserId);
                binding.recyclerview.setAdapter(messageAdapter);
                binding.recyclerview.scrollToPosition(messageList.size() - 1);
                binding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatActivity", "Failed to load messages", error.toException());
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void iNit() {
        auth = FirebaseAuth.getInstance();
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }

    private void sendMessage(String messageContent, boolean isImage) {
        String messageId = messagesDatabaseReference.push().getKey();
        if (messageId == null) {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
            return;
        }
        Message message = new Message(currentUserId, messageContent, getCurrentTimestamp(),isImage);
        message.setSent(true);
        messagesDatabaseReference.child(messageId).setValue(message)
                .addOnSuccessListener(aVoid -> {
                    // Tin nhắn đã được gửi thành công
                    updateChatList(chatWithUserId, messageContent, getCurrentTimestamp());

                })
                .addOnFailureListener(e -> {
                    // Gửi tin nhắn thất bại
                    Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    Log.e("ChatActivity", "Failed to send message", e);
                });

    }
    private void updateChatList(String chatWithUserId, String lastMessage, String lastMessageTime) {
        DatabaseReference chatListRef = FirebaseDatabase.getInstance().getReference("chatList").child(currentUserId).child(chatWithUserId);
        chatListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserChat userChat = snapshot.getValue(UserChat.class);
                if (userChat != null) {
                    userChat.setLastMessage(lastMessage);
                    userChat.setLastMessageTime(lastMessageTime);
                } else {
                    userChat = new UserChat();
                    userChat.setUserId(chatWithUserId);
                    userChat.setLastMessage(lastMessage);
                    userChat.setLastMessageTime(lastMessageTime);
                }
                chatListRef.setValue(userChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatActivity", "Failed to update chat list", error.toException());
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
    private String getCurrentTimestamp() {
        return new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onRefresh() {
        loadMessagesFromServer();

    }
}