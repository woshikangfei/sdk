package com.github.jdsjlzx.recyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.github.jdsjlzx.interfaces.IRefreshHeader;

import java.util.ArrayList;
import java.util.List;

public class LRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public LRecyclerViewAdapter(RecyclerView.Adapter innerAdapter) {
    }

    public void setRefreshHeader(IRefreshHeader refreshHeader){
        throw new RuntimeException("stub!!!");
    }

    public RecyclerView.Adapter getInnerAdapter() {
        throw new RuntimeException("stub!!!");
    }

    public void addHeaderView(View view) {
        throw new RuntimeException("stub!!!");
    }

    public void addFooterView(View view) {
        throw new RuntimeException("stub!!!");
    }
    public View getFooterView() {
        throw new RuntimeException("stub!!!");
    }

    public View getHeaderView() {
        throw new RuntimeException("stub!!!");
    }

    public ArrayList<View> getHeaderViews() {
        throw new RuntimeException("stub!!!");
    }

    public void removeHeaderView() {
        throw new RuntimeException("stub!!!");
    }

    public void removeFooterView() {
        throw new RuntimeException("stub!!!");
    }

    public int getHeaderViewsCount() {
        throw new RuntimeException("stub!!!");
    }

    public int getFooterViewsCount() {
        throw new RuntimeException("stub!!!");
    }

    public boolean isHeader(int position) {
        throw new RuntimeException("stub!!!");
    }

    public boolean isRefreshHeader(int position) {
        throw new RuntimeException("stub!!!");
    }

    public boolean isFooter(int position) {
        throw new RuntimeException("stub!!!");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        throw new RuntimeException("stub!!!");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        throw new RuntimeException("stub!!!");
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        throw new RuntimeException("stub!!!");
    }

    @Override
    public int getItemCount() {
        throw new RuntimeException("stub!!!");
    }

    @Override
    public int getItemViewType(int position) {
        throw new RuntimeException("stub!!!");
    }

    @Override
    public long getItemId(int position) {
        throw new RuntimeException("stub!!!");
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        throw new RuntimeException("stub!!!");
    }

    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        throw new RuntimeException("stub!!!");
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        throw new RuntimeException("stub!!!");
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        throw new RuntimeException("stub!!!");
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public int getAdapterPosition(boolean isCallback, int position) {
        throw new RuntimeException("stub!!!");
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        throw new RuntimeException("stub!!!");
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener itemLongClickListener) {
        throw new RuntimeException("stub!!!");
    }

    public interface SpanSizeLookup {
        int getSpanSize(GridLayoutManager gridLayoutManager, int position);
    }

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        throw new RuntimeException("stub!!!");
    }
}
