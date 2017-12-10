package com.vm.mcontact.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vm.mcontact.R;
import com.vm.mcontact.model.Contact;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by VanManh on 09-Dec-17.
 */

public class RCAdapter extends RecyclerView.Adapter<RCAdapter.mViewHolder> {

    private List<Contact> contacts;
    private RCItemClickListener listener;

    public RCAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void setOnRCItemClickListener(RCItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new mViewHolder(v);
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        Contact c = contacts.get(position);
        holder.bind(c);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_phone_num)
        TextView tvPhoneNum;

        public mViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Contact c) {
            tvName.setText(c.getName());
            tvPhoneNum.setText(c.getPhoneNum());
        }

        @Override
        public void onClick(View v) {
            listener.itemClickListener(v, getAdapterPosition());
        }
    }
}
