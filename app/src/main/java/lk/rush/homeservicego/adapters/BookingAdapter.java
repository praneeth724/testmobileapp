package lk.rush.homeservicego.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.rush.homeservicego.R;
import lk.rush.homeservicego.models.Booking;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvServiceName.setText(booking.getServiceName());
        holder.tvDate.setText("Date: " + booking.getBookingDate());
        holder.tvTime.setText("Time: " + booking.getBookingTime());
        holder.tvStatus.setText(booking.getStatus());

        // Change badge colour based on booking status
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
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvDate, tvTime, tvStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvBookingServiceName);
            tvDate        = itemView.findViewById(R.id.tvBookingDate);
            tvTime        = itemView.findViewById(R.id.tvBookingTime);
            tvStatus      = itemView.findViewById(R.id.tvStatus);
        }
    }
}
