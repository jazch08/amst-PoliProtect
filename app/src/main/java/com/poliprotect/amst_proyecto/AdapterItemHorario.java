package com.poliprotect.amst_proyecto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.poliprotect.amst_proyecto.R;

import java.util.List;

public class AdapterItemHorario  extends RecyclerView.Adapter<AdapterItemHorario.ViewHolder>{
    private List<String> listHora;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemHorarioClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public AdapterItemHorario(List<String> listHora) {
        this.listHora = listHora;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String hora = listHora.get(position);
        holder.itemTextViewHora.setText(hora);;

        holder.itemView.findViewById(R.id.idItemRutaLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onItemHorarioClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listHora.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextViewHora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextViewHora = itemView.findViewById(R.id.idItemHora);

        }
    }
}
