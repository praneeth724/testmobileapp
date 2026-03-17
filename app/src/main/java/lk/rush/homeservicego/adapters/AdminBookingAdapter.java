package lk.rush.homeservicego.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.rush.homeservicego.R;
import lk.rush.homeservicego.models.Booking;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.AdminBookingViewHolder> {

    private List<Booking> bookingList;

    // Interface so AdminBookingsActivity can handle status updates
    public interface OnStatusUpdateListener {
        void onUpdateStatus(Booking booking, int position);
    }

    private OnStatusUpdateListener listener;

    public AdminBookingAdapter(List<Booking> bookingList, OnStatusUpdateListener listener) {
        this.bookingList = bookingList;
        this.listener    = listener;
    }

    @NonNull
    @Override
    public AdminBookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_booking, parent, false);
        return new AdminBookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvServiceName.setText(booking.getServiceName());
        holder.tvCustomerName.setText("Customer: " + booking.getUserName());
        holder.tvDate.setText("Date: " + booking.getBookingDate());
        holder.tvTime.setText("Time: " + booking.getBookingTime());
        holder.tvStatus.setText(booking.getStatus());

        // Colour the status badge
        switch (booking.getStatus()) {
            case "confirmed":
                holder.tvStatus.getBackground().setTint(Color.parseColor("#4CAF50")); // green
                break;
            case "completed":
                holder.tvStatus.getBackground().setTint(Color.parseColor("#9E9E9E")); // grey
                break;
            default: // pending
                holder.tvStatus.getBackground().setTint(Color.parseColor("#FF9800")); // orange
                break;
        }

        // Pass click to activity to show status options dialog
        holder.btnUpdateStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpdateStatus(booking, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // Update a single item after status change
    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    public static class AdminBookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvCustomerName, tvDate, tvTime, tvStatus;
        Button btnUpdateStatus;

        public AdminBookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName  = itemView.findViewById(R.id.tvServiceName);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvDate         = itemView.findViewById(R.id.tvDate);
            tvTime         = itemView.findViewById(R.id.tvTime);
            tvStatus       = itemView.findViewById(R.id.tvStatus);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}
