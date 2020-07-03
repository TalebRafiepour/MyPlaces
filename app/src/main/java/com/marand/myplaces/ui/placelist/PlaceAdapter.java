package com.marand.myplaces.ui.placelist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.marand.myplaces.R;
import com.marand.myplaces.model.Item;
import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private static final String TAG = PlaceAdapter.class.getSimpleName();
    private List<Item> mItems = new ArrayList<>();

    @NonNull
    @Override
    public PlaceAdapter.PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceAdapter.PlaceViewHolder holder, int position) {
        holder.bind(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        private TextView mName_text, mAddress_text;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            mName_text = itemView.findViewById(R.id.name_text);
            mAddress_text = itemView.findViewById(R.id.address_text);
        }

        private void bind(Item item) {
            mName_text.setText(item.getVenue().getName());
            mAddress_text.setText(item.getVenue().getLocation().getAddress());
        }
    }

// -------------------------------------------------------------------------------------------------

    public void setItems(List<Item> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

}
