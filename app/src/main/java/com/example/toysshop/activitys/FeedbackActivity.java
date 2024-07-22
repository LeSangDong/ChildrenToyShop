package com.example.toysshop.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivityFeedbackBinding;
import com.example.toysshop.model.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FeedbackActivity extends AppCompatActivity {
    private ActivityFeedbackBinding binding;
    private FirebaseAuth auth;
    private String name_product;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iNit();
        //getNameProduct
        name_product = getIntent().getStringExtra("name");
        productId = getIntent().getIntExtra("product_id",-1);
        if(name_product != null){
            binding.tvName.setText(name_product);
        }
        else{
            binding.tvName.setText("");
        }

        binding.btnSendFeedback.setOnClickListener(v->{
            binding.tvBtn.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            getFeedback();
        });
        binding.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> updateReaction((int) rating));

        updateReaction((int) binding.ratingBar.getRating());
    }

    private void updateReaction(int rating) {

        switch (rating) {
            case 1:
                binding.iconReaction.setVisibility(View.VISIBLE);
                binding.iconReaction.setImageResource(R.drawable.icon_te);
                binding.tvNameReaction.setText("Rất tệ");
                break;
            case 2:
                binding.iconReaction.setVisibility(View.VISIBLE);
                binding.iconReaction.setImageResource(R.drawable.te_2sao);
                binding.tvNameReaction.setText("Tệ");
                break;
            case 3:
                binding.iconReaction.setVisibility(View.VISIBLE);
                binding.iconReaction.setImageResource(R.drawable.icon_3sao);
                binding.tvNameReaction.setText("Hài lòng");
                break;
            case 4:
                binding.iconReaction.setVisibility(View.VISIBLE);
                binding.iconReaction.setImageResource(R.drawable.icon_4sao);
                binding.tvNameReaction.setText("Tốt");
                break;
            case 5:
                binding.iconReaction.setVisibility(View.VISIBLE);
                binding.iconReaction.setImageResource(R.drawable.icon_5sao);
                binding.tvNameReaction.setText("Cực kì hài lòng");
                break;
            default:
                binding.iconReaction.setVisibility(View.GONE);
                binding.tvNameReaction.setText("");
                break;
        }

    }

    private void getFeedback() {
        float rating = binding.ratingBar.getRating();
        String feedback = binding.edtFeedback.getText().toString().trim();
        if (feedback.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nhận xét", Toast.LENGTH_SHORT).show();
            binding.tvBtn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
            return;
        }
        if(rating<1){
            Toast.makeText(this, "Vui lòng đánh giá sao.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Toy/" + productId + "/reviews");
        String reviewId = databaseReference.push().getKey();
        Review review = new Review(userId, rating, feedback);
        databaseReference.child(reviewId).setValue(review)
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        Toast.makeText(this, "Đánh giá của bạn đã được gửi", Toast.LENGTH_SHORT).show();
                        binding.edtFeedback.setText("");
                        binding.ratingBar.setRating(0);
                        binding.tvBtn.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                        calculateAverageRating(productId);

                    }
                    else{
                        Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        binding.tvBtn.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                    }

                });


    }

    private void calculateAverageRating(int productId) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("Toy/" + productId + "/reviews");

        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalReviews = 0;
                float sumRatings = 0;

                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    if (review != null) {
                        sumRatings += review.getRating();
                        totalReviews++;
                    }
                }

                float averageRating = (totalReviews > 0) ? (sumRatings / totalReviews) : 0;

                // Cập nhật số sao trung bình trong cơ sở dữ liệu
                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Toy/" + productId);
                productRef.child("star").setValue(averageRating);
                productRef.child("count_feedback").setValue(totalReviews);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void iNit() {
        auth = FirebaseAuth.getInstance();
    }
}