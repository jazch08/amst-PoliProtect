package com.example.amst_proyecto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterItemHorario  extends RecyclerView.Adapter<AdapterItemHorario.ViewHolder>{
    private List<String> listHoraInicio;
    private List<String> listHoraFin;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public AdapterItemHorario(List<String> listHoraInicio, List<String> listHoraFin) {
        this.listHoraInicio = listHoraInicio;
        this.listHoraFin = listHoraFin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String horaInicio = listHoraInicio.get(position);
        String horaFin = listHoraFin.get(position);

        holder.itemTextViewInicio.setText(horaInicio);
        holder.itemTextViewFin.setText(horaFin);

        holder.itemView.findViewById(R.id.idItemHorarioLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listHoraInicio.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextViewInicio;
        TextView itemTextViewFin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextViewInicio = itemView.findViewById(R.id.idItemHorarioInicio);
            itemTextViewFin = itemView.findViewById(R.id.idItemHorarioFin);
        }
    }
}
