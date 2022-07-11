package genericepg.duna.project.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import genericepg.duna.project.R;
import genericepg.duna.project.listener.RecyclerItemClickListener;
import genericepg.duna.project.model.BaseProgramModel;
import genericepg.duna.project.observable.ObservableRecyclerView;
import genericepg.duna.project.observable.Subject;
import genericepg.duna.project.utils.Utils;

/**
 * Created by Marius Duna on 9/9/2016.
 */
public abstract class GenericEpgAdapter<T extends BaseProgramModel> extends RecyclerView.Adapter<GenericEpgAdapter.EpgViewHolder> {
    private RecyclerView.RecycledViewPool recycledViewPool;
    private ArrayList<ArrayList<T>> channelsList;
    private Subject subject;
    private RecyclerItemClickListener.OnItemClickListener listener;
    private RecyclerItemClickListener.OnScrolledListener scrolledListener;

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public class EpgViewHolder<T extends BaseProgramModel> extends RecyclerView.ViewHolder {
        private ObservableRecyclerView recyclerView;

        public EpgViewHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.horizontal_recycler_view);
            recyclerView.setSubject(subject);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(recyclerView.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (listener != null) {
                        listener.onItemClick(view, position);
                        //You can put in your generic view an UID for each channel and program in order to identify the channel clicked also
                    }
                }
            }));
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Log.d("ScrollerListener", "dx: " + dx);
                    if (scrolledListener != null)
                        scrolledListener.onScrolled(dx, dy);
                }
            });
        }

        public void setList(ArrayList<T> horizontalList) {
            GenericProgramsAdapter horizontalAdapter = programsCreator(horizontalList, subject);
            recyclerView.setAdapter(horizontalAdapter);
            recyclerView.setSubject(subject);
            Log.d("POS", "Recycled new view at pos: " + subject.getInitialPosition());
        }

        public void initialScroll() {
            ArrayList<BaseProgramModel> list = ((GenericProgramsAdapter) recyclerView.getAdapter()).getArrayList();
            final int initialPosition = Utils.getInitialPositionInList(subject.getCurrentTime(), list);
            if (initialPosition == -1) return;
            final float initialOffset = Utils.getInitialProgramOffsetPx(list.get(initialPosition).getStartTime(), subject.getSystemTime(), recyclerView.getContext());
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(initialPosition, -(int) (initialOffset + subject.getInitialPosition()));
        }
    }

    public GenericEpgAdapter(ArrayList<ArrayList<T>> verticalList, RecyclerItemClickListener.OnItemClickListener listener, RecyclerItemClickListener.OnScrolledListener scrolledListener) {
        this.channelsList = verticalList;
        this.listener = listener;
        this.scrolledListener = scrolledListener;
        recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.setMaxRecycledViews(R.layout.vrecycler_view_item, 20);
    }

    @Override
    public EpgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        ObservableRecyclerView programRow = view.findViewById(R.id.horizontal_recycler_view);
        programRow.setRecycledViewPool(recycledViewPool);
        return new EpgViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.vrecycler_view_item;
    }

    @Override
    public void onBindViewHolder(@NonNull GenericEpgAdapter.EpgViewHolder holder, int position) {
        holder.setList(channelsList.get(position));
        holder.initialScroll();
    }

    @Override
    public int getItemCount() {
        return channelsList.size();
    }

    public abstract <T extends BaseProgramModel> GenericProgramsAdapter programsCreator(ArrayList<T> programList, Subject subject);
}