package com.example.amst_proyecto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterItemRuta extends RecyclerView.Adapter<AdapterItemRuta.ViewHolder>{
    private List<String> listRutas;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemRutaClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public AdapterItemRuta(List<String> listRutas) {
        this.listRutas = listRutas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ruta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ruta = listRutas.get(position);

        holder.itemTextViewRuta.setText(ruta);

        holder.itemView.findViewById(R.id.idItemRutaLayout).setOnClickListener(new View.OnClickListener() {
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
        return listRutas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextViewRuta;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextViewRuta = itemView.findViewById(R.id.idItemRuta);

        }
    }
}