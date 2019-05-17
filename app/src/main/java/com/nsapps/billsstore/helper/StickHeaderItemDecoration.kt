package com.nsapps.billsstore.helper

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nsapps.billsstore.R

class StickHeaderItemDecoration(var headerHeight: Int, var sticky: Boolean, var sectionCallback: SectionCallback) :
    RecyclerView.ItemDecoration() {
    private var headerView: View? = null
    private var header: TextView? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildAdapterPosition(view);
        if (sectionCallback.isSection(pos)) {
            outRect.top = headerHeight;
        }

    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        if (headerView == null) {
            headerView = inflateHeaderView(parent) as View
            header = headerView!!.findViewById(R.id.list_item_section_text)
            fixLayoutSize(headerView!!, parent);
        }

        var previousHeader = "";
        for (i in 0..parent.getChildCount() - 1) {
            var child = parent.getChildAt(i);
            var position = parent.getChildAdapterPosition(child);

            var title = sectionCallback.getSectionHeader(position);
            header!!.setText(title);
            if (!previousHeader.equals(title) || sectionCallback.isSection(position)) {
                drawHeader(c, child, headerView!!)
                previousHeader = title.toString()
            }
        }
    }


    private fun drawHeader(c: Canvas, child: View, headerView: View) {
        c.save();
        if (sticky) {
            c.translate(0f, Math.max(0, child.getTop() - headerView.getHeight()).toFloat())
        } else {
            c.translate(0f, child.getTop() - headerView.getHeight().toFloat())
        }
        headerView.draw(c);
        c.restore();
    }

    private fun inflateHeaderView(parent: RecyclerView): View {
        return LayoutInflater.from(parent.getContext())
            .inflate(R.layout.recycler_section_header, parent, false);
    }

    /**
     * Measures the header view to make sure its size is greater than 0 and will be drawn
     * https://yoda.entelect.co.za/view/9627/how-to-android-recyclerview-item-decorations
     */
    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        var widthSpec = View.MeasureSpec.makeMeasureSpec(
            parent.getWidth(),
            View.MeasureSpec.EXACTLY
        );
        var heightSpec = View.MeasureSpec.makeMeasureSpec(
            parent.getHeight(),
            View.MeasureSpec.UNSPECIFIED
        );

        var childWidth = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.getPaddingLeft() + parent.getPaddingRight(),
            view.getLayoutParams().width
        );
        var childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.getPaddingTop() + parent.getPaddingBottom(),
            view.getLayoutParams().height
        );

        view.measure(childWidth, childHeight);

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    interface SectionCallback {

        fun isSection(position: Int): Boolean

        fun getSectionHeader(position: Int): CharSequence
    }
}

