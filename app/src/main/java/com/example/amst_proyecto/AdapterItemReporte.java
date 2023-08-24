
package com.example.amst_proyecto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterItemReporte extends RecyclerView.Adapter<AdapterItemReporte.ViewHolder>{
    private List<String> listReportes;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemRutaClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public AdapterItemReporte(List<String> listReportes) {
        this.listReportes = listReportes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String reporte = listReportes.get(position);

        holder.itemTextViewReporte.setText(reporte);

        holder.itemView.findViewById(R.id.idItemReporteLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onItemRutaClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listReportes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextViewReporte;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextViewReporte = itemView.findViewById(R.id.idItemReporte);

        }
    }
}