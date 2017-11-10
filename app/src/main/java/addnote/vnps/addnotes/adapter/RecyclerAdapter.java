package addnote.vnps.addnotes.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import addnote.vnps.addnotes.R;

/**
 * Created by 410181 on 12/31/2015.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CustomViewHolder> {

    ArrayList<String> fontList;
    Context context;
    private int itemLayout;
    private static MyClickListener myClickListener;
    private String selection = "";

    public RecyclerAdapter(Context context, ArrayList<String> fontList, int itemLayout, String selection) {
        this.fontList = fontList;
        this.context = context;
        this.itemLayout = itemLayout;
        this.selection = selection;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row, viewGroup, false);

        return new CustomViewHolder(itemView);
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }
    public interface MyClickListener {
        void  onItemClick(int position, View v);
    }
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if(!this.selection.equalsIgnoreCase("TYPE")) {
            Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontList.get(position));
            holder.text.setTypeface(type);
            if (this.selection.equalsIgnoreCase(fontList.get(position))) {
                holder.tick.setVisibility(View.VISIBLE);
            } else {
                holder.tick.setVisibility(View.GONE);
            }
            Animation animation;
            if (position % 2 == 0) {
                animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            } else {
                animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
            }
            holder.card1.startAnimation(animation);
        } else {
            holder.text.setText(fontList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return fontList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
        public TextView text;
        public ImageView tick;
        public  android.support.v7.widget.CardView card1;
        public CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.text = (TextView) view.findViewById(R.id.textFont);
            this.tick = (ImageView) view.findViewById(R.id.tick);
            this.card1 = (android.support.v7.widget.CardView)view.findViewById(R.id.card1);
        }
        @Override
        public void onClick(View v) {

            myClickListener.onItemClick(getPosition(), v);
        }
    }
}
