package com.sinohb.common.adapter.recycleview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecycleViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int TYPE_NORMAL = 1;
    private static final int TYPE_FLAG = 100000;

    protected Context mContext;
    protected List<T> mDataList;
    private LayoutInflater mLayoutInflater;
    private List<View> mHeaderViews = new ArrayList<>();
    private List<View> mFooterViews = new ArrayList<>();

    private List<Integer> mHeaderViewTypes = new ArrayList<>();
    private List<Integer> mFooterViewTypes = new ArrayList<>();

    private OnItemClickListener mClickListener;

    private boolean isScroll = false;

    public BaseRecycleViewAdapter(Context context, List<T> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void replaceData(List<T> datats) {
        mDataList = datats;
        notifyDataSetChanged();
    }

    public T getData(int position) {
        return mDataList == null ? null : position >= mDataList.size() ? null : mDataList.get(position);
    }

    protected void removeItem(int pos) {
        mDataList.remove(pos);
        notifyDataSetChanged();
    }

    public void setScroll(boolean isScroll) {
        this.isScroll = isScroll;
        if (!isScroll) notifyDataSetChanged();
    }

    public boolean isScroll() {
        return isScroll;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViewTypes.contains(viewType)) {
            return new BaseViewHolder(mContext, mHeaderViews.get(viewType / TYPE_FLAG));
        }

        if (mFooterViewTypes.contains(viewType)) {
            int index = viewType / TYPE_FLAG - mDataList.size() - mHeaderViews.size();
            return new BaseViewHolder(mContext, mFooterViews.get(index));
        }

        //return new BaseViewHolder(mContext, mLayoutInflater.inflate(getItemLayoutId(viewType), parent, false));
        BaseViewHolder viewHolder = BaseViewHolder.buildViewHolder(mContext, parent, getItemLayoutId(viewType));
        initRes(viewHolder);
        return viewHolder;
    }

    protected void initRes(BaseViewHolder viewHolder) {

    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {

        if (isFooter(position) || isHeader(position))
            return;
        final int realPos = getRealPosition(position);
        if (realPos < mDataList.size()) {
            final T data = mDataList.get(realPos);
            onBind(holder, realPos, data);
        }
        if (mClickListener != null) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(holder.itemView, realPos);
                }
            });
        }
    }

    public int getRealDataSize() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public int getHeadAndFootSize() {
        return mHeaderViews.size() + mFooterViews.size();
    }

    public boolean hasHeader() {
        return mHeaderViews != null && !mHeaderViews.isEmpty();
    }

    @Override
    public int getItemCount() {
        return getRealDataSize() + mHeaderViews.size() + mFooterViews.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderViews.size() > 0 && position < mHeaderViews.size()) {
            //用position作为HeaderView 的   ViewType标记
            //记录每个ViewType标记
            mHeaderViewTypes.add(position * TYPE_FLAG);
            return position * TYPE_FLAG;
        }

        if (mFooterViews.size() > 0 && mFooterViews.size() > 0 && position >= getItemCount() - mFooterViews.size()) {
            //用position作为FooterView 的   ViewType标记
            //记录每个ViewType标记
            mFooterViewTypes.add(position * TYPE_FLAG);
            return position * TYPE_FLAG;
        }
        return TYPE_NORMAL;
    }

    public int getRealPosition(int position) {
        return position - mHeaderViews.size();
    }

    /**
     * 必须在setAdapter之前添加
     *
     * @param view header
     */
    public void addHeaderView(View view) {
        mHeaderViews.add(view);
    }

    /**
     * 必须在setAdapter之前添加
     *
     * @param view footer
     */
    public void addFooterView(View view) {
        mFooterViews.add(view);
    }

    private boolean isHeader(int position) {
        return mHeaderViews.size() > 0 && position < mHeaderViews.size();
    }

    private boolean isFooter(int position) {
        return mFooterViews.size() > 0 && position >= getItemCount() - mFooterViews.size();
    }

    /**
     * 当LayoutManager是GridLayoutManager时，设置header和footer占据的列数
     *
     * @param recyclerView recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isFooter(position) || isHeader(position))
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * 当LayoutManager是StaggeredGridLayoutManager时，设置header和footer占据的列数
     *
     * @param holder holder
     */
    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        final ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            params.setFullSpan(isHeader(holder.getLayoutPosition())
                    || isFooter(holder.getLayoutPosition()));
        }
    }

    /**
     * 绑定数据
     *
     * @param viewHolder holder
     * @param position   pos
     * @param data       数据源
     */
    public abstract void onBind(BaseViewHolder viewHolder, int position, T data);

    /**
     * item 布局id
     *
     * @param viewType item type
     * @return item 布局id
     */
    abstract public int getItemLayoutId(int viewType);

    public void notifyItemChange(int position) {
        notifyItemChanged(position + mHeaderViews.size() + mFooterViews.size(),0);
    }


    public interface OnItemClickListener {
        void onItemClick(View itemView, int pos);

        void onItemLongClick(View itemView, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }
}
