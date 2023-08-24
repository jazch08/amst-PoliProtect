package com.example.amst_proyecto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterItemSelectReport extends RecyclerView.Adapter<AdapterItemSelectReport.ViewHolder>{
    private List<String> listSelectReport;
    private AdapterItemSelectReport.OnItemClickListener clickListener;
    private boolean itemSelected = false;
    private int positionSelected = -1;

    public interface OnItemClickListener {
        void onItemSelectReportClick(int position);
    }

    public void setOnItemClickListener(AdapterItemSelectReport.OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public AdapterItemSelectReport(List<String> listSelectReport) {
        this.listSelectReport = listSelectReport;
    }

    public void isSelected(boolean _itemSelected, int _positionSelected){
        itemSelected = _itemSelected;
        positionSelected = _positionSelected;
    }

    @NonNull
    @Override
    public AdapterItemSelectReport.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_report, parent, false);
        return new AdapterItemSelectReport.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterItemSelectReport.ViewHolder holder, int position) {
        String selectReport = listSelectReport.get(position);

        if(itemSelected && positionSelected==position){
            holder.itemView.findViewById(R.id.idItemSelectReportLayout).setBackgroundResource(R.drawable.item_selected);
        } else {
            holder.itemView.findViewById(R.id.idItemSelectReportLayout).setBackgroundResource(R.drawable.item_horario_background_click);
        }

        holder.itemTextViewSelectReport.setText(selectReport);

        holder.itemView.findViewById(R.id.idItemSelectReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onItemSelectReportClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listSelectReport.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextViewSelectReport;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextViewSelectReport = itemView.findViewById(R.id.idItemSelectReport);


        }
    }
}
