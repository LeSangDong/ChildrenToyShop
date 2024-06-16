package com.example.toysshop.fragments.admins;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.toysshop.R;
import com.example.toysshop.activitys.GetStartedActivity;
import com.example.toysshop.activitys.MainAdminActivity;
import com.example.toysshop.databinding.FragmentAccountBinding;
import com.google.firebase.auth.FirebaseAuth;


public class AccountFragment extends Fragment {

   private FragmentAccountBinding binding;
   private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentAccountBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init
        initialized();

        binding.tvLogout.setOnClickListener(v->{
            auth.signOut();
            startActivity(new Intent(requireActivity(), GetStartedActivity.class));
            requireActivity().finish();
        });
    }

    private void initialized() {
        auth = FirebaseAuth.getInstance();
    }
}