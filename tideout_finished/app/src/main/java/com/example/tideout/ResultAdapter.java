package com.example.tideout;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private List<ResultModel> resultList;
    private Context context;

    public ResultAdapter(Context context, List<ResultModel> resultList) {
        this.context = context;
        this.resultList = resultList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView itemSubtitle;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.itemTitle);
            itemSubtitle = view.findViewById(R.id.itemSubtitle);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResultModel result = resultList.get(position);
        String combinedText = result.getName();
        holder.textView.setText(combinedText + " \uD83C\uDF0A");

        holder.itemSubtitle.setText(result.getRegion());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BeachDetail.class);
            intent.putExtra("objectid", result.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }


}
