package lk.rush.homeservicego.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.rush.homeservicego.R;
import lk.rush.homeservicego.models.Service;

public class AdminServiceAdapter extends RecyclerView.Adapter<AdminServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;

    // Interface so the Activity handles the delete action
    public interface OnDeleteClickListener {
        void onDelete(Service service, int position);
    }

    private OnDeleteClickListener listener;

    public AdminServiceAdapter(List<Service> serviceList, OnDeleteClickListener listener) {
        this.serviceList = serviceList;
        this.listener    = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);

        holder.tvName.setText(service.getName());
        holder.tvCategory.setText(service.getCategory());
        holder.tvPrice.setText("Rs. " + service.getPrice());

        // Pass delete click to the Activity
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(service, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    // Remove item from list after deletion
    public void removeItem(int position) {
        serviceList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvPrice;
        Button btnDelete;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName     = itemView.findViewById(R.id.tvServiceName);
            tvCategory = itemView.findViewById(R.id.tvServiceCategory);
            tvPrice    = itemView.findViewById(R.id.tvServicePrice);
            btnDelete  = itemView.findViewById(R.id.btnDeleteService);
        }
    }
}
