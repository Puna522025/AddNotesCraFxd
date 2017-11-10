package addnote.vnps.addnotes.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.pojo.ShoppingPojo;

/**
 * Created by 410181 on 12/31/2015.
 */
public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.CustomViewHolder> {

    ArrayList<ShoppingPojo> shoppingList;
    Context context;
    private static MyClickListener myClickListener;
    private static MyCheckedChangeListener myCheckedChangeListener;

    public ShoppingListAdapter(Context context, ArrayList<ShoppingPojo> shoppingList) {
        this.shoppingList = shoppingList;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row_shopping_list, viewGroup, false);

        return new CustomViewHolder(itemView);
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnCheckedChangeListener(MyCheckedChangeListener myCheckedChangeListener) {
        this.myCheckedChangeListener = myCheckedChangeListener;
    }

    public interface MyCheckedChangeListener {
        void onCheckedChanged(int position, CompoundButton buttonView, boolean isChecked);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if (shoppingList != null && shoppingList.size() > 0) {
            holder.itemToBuy.setText(shoppingList.get(position).getItemToBuy());
            holder.totalValue.setText("");

            if (!TextUtils.isEmpty(shoppingList.get(position).getValue()) || !TextUtils.isEmpty(shoppingList.get(position).getCountOfItemsToBuy())) {
                holder.rlExtraShoppingParams.setVisibility(View.VISIBLE);
                holder.value.setText(context.getString(R.string.shopping_value).concat(shoppingList.get(position).getValue()));
                holder.count.setText(context.getString(R.string.shopping_count).concat(shoppingList.get(position).getCountOfItemsToBuy()));
                if (!TextUtils.isEmpty(shoppingList.get(position).getValue()) && !TextUtils.isEmpty(shoppingList.get(position).getCountOfItemsToBuy())) {
                    int totalValue = Integer.parseInt(shoppingList.get(position).getValue()) *
                            Integer.parseInt(shoppingList.get(position).getCountOfItemsToBuy());
                    holder.totalValue.setText(context.getString(R.string.shopping_amount).concat(String.valueOf(totalValue)));
                }
            } else {
                holder.rlExtraShoppingParams.setVisibility(View.GONE);
                holder.value.setText(context.getString(R.string.shopping_value));
                holder.count.setText(context.getString(R.string.shopping_count));
            }
            if (shoppingList.get(position).getDoneOrNot().equalsIgnoreCase("yes")) {
                holder.isDone.setChecked(false);
                holder.crossLine.setVisibility(View.VISIBLE);
            } else if (shoppingList.get(position).getDoneOrNot().equalsIgnoreCase("no")) {
                holder.isDone.setChecked(true);
                holder.crossLine.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener, CompoundButton.OnCheckedChangeListener {

        public TextView value;
        public TextView itemToBuy;
        public TextView count;
        public TextView totalValue;
        public android.support.v7.widget.SwitchCompat isDone;
        public ImageView crossLine;
        public ImageView imgDeleteNote;
        public RelativeLayout rlExtraShoppingParams;

        public CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.value = (TextView) view.findViewById(R.id.value);
            this.itemToBuy = (TextView) view.findViewById(R.id.itemToBuy);
            this.count = (TextView) view.findViewById(R.id.count);
            this.totalValue = (TextView) view.findViewById(R.id.totalValueofItems);
            this.rlExtraShoppingParams = (RelativeLayout) view.findViewById(R.id.rlExtraShoppingParams);
            this.isDone = (android.support.v7.widget.SwitchCompat) view.findViewById(R.id.isDone);
            this.isDone.setTag(getAdapterPosition());
            this.crossLine = (ImageView) view.findViewById(R.id.crossLine);
            this.imgDeleteNote = (ImageView) view.findViewById(R.id.imgDeleteNote);
            this.isDone.setOnCheckedChangeListener(this);
            this.imgDeleteNote.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            myCheckedChangeListener.onCheckedChanged(getAdapterPosition(), buttonView, isChecked);
        }
    }
}
