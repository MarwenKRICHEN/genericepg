package genericepg.duna.project.adapter;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marius Duna on 9/15/2016.
 */
public abstract class GenericChannelsAdapter<V> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<V> horizontalList;

    public abstract RecyclerView.ViewHolder setViewHolder(ViewGroup parent);

    public abstract void onBindData(RecyclerView.ViewHolder holder, int position);

    public GenericChannelsAdapter(ArrayList<V> horizontalList) {
        this.horizontalList = horizontalList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = setViewHolder(parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindData(holder, position);
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    public V getItem(int position) {
        return horizontalList.get(position);
    }
}