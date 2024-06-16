package com.example.toysshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.toysshop.R;

public class PaymentMethodAdapter extends BaseAdapter {

    private Context context;
    private String[] paymentMethods;
    private int[] paymentIcons;
    private LayoutInflater inflater;

    public PaymentMethodAdapter(Context context, String[] paymentMethods, int[] paymentIcons) {
        this.context = context;
        this.paymentMethods = paymentMethods;
        this.paymentIcons = paymentIcons;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return   paymentMethods.length;
    }

    @Override
    public Object getItem(int position) {
        return paymentMethods[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_item_payment, parent, false);
        }
        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textView = convertView.findViewById(R.id.tv_name_payment);

        imageView.setImageResource(paymentIcons[position]);
        textView.setText(paymentMethods[position]);

        return convertView;

    }
}
