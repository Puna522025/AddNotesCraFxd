package addnote.vnps.addnotes.common;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.addnotes.view.AddNotesView;
import addnote.vnps.addnotes.pojo.ShoppingPojo;

/**
 * Created by DELL on 4/9/2016.
 */
public class CommonUtilities {

    public static boolean isNetworkAvailable() {
        Context context = ApplicationContext.getApplicationContext();
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && (connectivityManager.getActiveNetworkInfo().isConnected() ||
                connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting())  ;
    }

    public static void changeThemeColors(String themeColor, Toolbar toolbar, FloatingActionButton saveButton, Window window,
                                         boolean mainOrNot, FloatingActionButton fabAction,
                                         FloatingActionButton fabAdd, FloatingActionButton fabDelete,
                                         FloatingActionButton fab_share, ImageView imageBackground, RelativeLayout rlBackgroundNote) {

       Context context = ApplicationContext.getApplicationContext();

        if (mainOrNot) {
            imageBackground.setVisibility(View.VISIBLE);
        }
        //IronMan
        if (themeColor.equalsIgnoreCase(context.getString(R.string.redYellow))) {
            changeTheme(R.color.redYellowToolBar, R.color.redYellowStatusBar, R.color.redYellowAccent, R.color.redYellowFab2, R.color.redYellowFab3,
                    R.color.redYellowFab4, toolbar, saveButton, window, mainOrNot, fabAction, fabAdd, fabDelete, fab_share);
            if (mainOrNot) {
               // imageBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.iron_man));
                Picasso.with(context)
                        .load(R.drawable.iron_man).fit()
                        .into(imageBackground);
                rlBackgroundNote.setBackground(ContextCompat.getDrawable(context, R.drawable.app_bg3_c));
            }
        }
        //White Joker
        if (themeColor.equalsIgnoreCase(context.getString(R.string.whiteJoker))) {
            changeTheme(R.color.WhiteJokerToolBar, R.color.WhiteJokerStatusBar, R.color.WhiteJokerAccent, R.color.WhiteJokerFab2,
                    R.color.WhiteJokerFab3, R.color.WhiteJokerFab4, toolbar, saveButton,
                    window, mainOrNot, fabAction, fabAdd, fabDelete, fab_share);
            if (mainOrNot) {
                Picasso.with(context)
                        .load(R.drawable.joker_c).fit()
                        .into(imageBackground);
                //imageBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.joker_c));
                rlBackgroundNote.setBackgroundColor(Color.parseColor("#EDEDED"));
            }
        }
        //Deadpool
        if (themeColor.equalsIgnoreCase(context.getString(R.string.blackRed))) {
            changeTheme(R.color.BlackRedToolBar, R.color.BlackRedStatusBar, R.color.BlackRedAccent, R.color.BlackRedFab2,
                    R.color.BlackRedFab3, R.color.BlackRedFab4, toolbar, saveButton,
                    window, mainOrNot, fabAction, fabAdd, fabDelete, fab_share);
            if (mainOrNot) {
               // imageBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.deadpool_cc));
                Picasso.with(context)
                        .load(R.drawable.deadpool_cc).fit()
                        .into(imageBackground);
                rlBackgroundNote.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black));
            }
        }
        //Neon Batman
        if (themeColor.equalsIgnoreCase(context.getString(R.string.neonBlue))) {
            changeTheme(R.color.NeonBlueToolBar, R.color.NeonBlueStatusBar, R.color.NeonBlueAccent, R.color.NeonBlueFab2,
                    R.color.NeonBlueFab3,R.color.NeonBlueFab4, toolbar, saveButton,
                    window, mainOrNot, fabAction, fabAdd, fabDelete, fab_share);
            if (mainOrNot) {
               // imageBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.xa_c));
                Picasso.with(context)
                        .load(R.drawable.xa_c).fit()
                        .into(imageBackground);
                rlBackgroundNote.setBackgroundColor(Color.parseColor("#120F20"));
            }
        }
        //Superman
        else if (themeColor.equalsIgnoreCase(context.getString(R.string.orangeRed))) {
            changeTheme(R.color.OrangeRedToolBar, R.color.OrangeRedStatusBar, R.color.OrangeRedAccent, R.color.OrangeRedFab2,
                    R.color.OrangeRedFab3, R.color.OrangeRedFab4, toolbar, saveButton,
                    window, mainOrNot, fabAction, fabAdd, fabDelete, fab_share);
            if (mainOrNot) {
                Picasso.with(context)
                        .load(R.drawable.super_man_logo).fit()
                        .into(imageBackground);
             //   imageBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.super_man_logo));
                rlBackgroundNote.setBackground(ContextCompat.getDrawable(context, R.drawable.app_bg3_c));
            }
        }
        //HULK
        else if (themeColor.equalsIgnoreCase(context.getString(R.string.purpleGreen))) {
            changeTheme(R.color.PurpleGreenToolBar, R.color.PurpleGreenStatusBar, R.color.PurpleGreenAccent, R.color.PurpleGreenFab2,
                    R.color.PurpleGreenFab3, R.color.PurpleGreenFab4, toolbar, saveButton,
                    window, mainOrNot, fabAction, fabAdd, fabDelete, fab_share);
            if (mainOrNot) {
                Picasso.with(context)
                        .load(R.drawable.hulk).fit()
                        .into(imageBackground);
                //imageBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.hulk));
                rlBackgroundNote.setBackground(ContextCompat.getDrawable(context, R.drawable.app_bg3_c));
            }
        }
        //Cap America
        else if (themeColor.equalsIgnoreCase(context.getString(R.string.pinkYellow))) {
            changeTheme(R.color.PinkYellowToolBar, R.color.PinkYellowStatusBar, R.color.PinkYellowAccent, R.color.PinkYellowFab2,
                    R.color.PinkYellowFab3, R.color.PinkYellowFab4, toolbar, saveButton,
                    window, mainOrNot, fabAction, fabAdd, fabDelete, fab_share);
            if (mainOrNot) {
               // imageBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.cap_america));
                Picasso.with(context)
                        .load(R.drawable.cap_america).fit()
                        .into(imageBackground);
                rlBackgroundNote.setBackground(ContextCompat.getDrawable(context, R.drawable.app_bg3_c));
            }
        }
        //BatMan
        else if (themeColor.equalsIgnoreCase(context.getString(R.string.greenBrown))) {
            changeTheme(R.color.GreenBrownToolBar, R.color.GreenBrownStatusBar, R.color.GreenBrownAccent, R.color.GreenBrownFab2,
                    R.color.GreenBrownFab3, R.color.GreenBrownFab4, toolbar, saveButton,
                    window, mainOrNot, fabAction, fabAdd, fabDelete, fab_share);
            if (mainOrNot) {
                //imageBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.batman_logo));
                Picasso.with(context)
                        .load(R.drawable.batman_logo).fit()
                        .into(imageBackground);
                rlBackgroundNote.setBackground(ContextCompat.getDrawable(context, R.drawable.back3_c));
            }
        }
        //Minions
        else if (themeColor.equalsIgnoreCase(context.getString(R.string.blue))) {
            changeTheme(R.color.BlueToolBar, R.color.BlueStatusBar, R.color.BlueAccent, R.color.BlueFab2, R.color.BlueFab3,
                    R.color.BlueFab4, toolbar, saveButton, window, mainOrNot, fabAction, fabAdd, fabDelete, fab_share);
            if (mainOrNot) {
                //imageBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.minion2));
                Picasso.with(context)
                        .load(R.drawable.minion2).fit()
                        .into(imageBackground);
                rlBackgroundNote.setBackground(ContextCompat.getDrawable(context, R.drawable.app_bg3_c));
            }
        }
        else if (themeColor.equalsIgnoreCase(context.getString(R.string.pink_hello_kitty))) {
            changeTheme(R.color.PinkKittyToolBar, R.color.PinkKittyStatusBar, R.color.PinkKittyAccent, R.color.PinkKittyFab2,
                    R.color.PinkKittyFab3, R.color.PinkKittyFab4, toolbar, saveButton,
                    window, mainOrNot, fabAction, fabAdd, fabDelete, fab_share);
            if (mainOrNot) {
                //imageBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.hellokitty));
                Picasso.with(context)
                        .load(R.drawable.hellokitty).fit()
                        .into(imageBackground);
                rlBackgroundNote.setBackground(ContextCompat.getDrawable(context, R.drawable.app_bg3_c));
            }
        }
        else if (themeColor.equalsIgnoreCase(context.getString(R.string.orange))) {
            changeTheme(R.color.OrangeToolBar, R.color.OrangeStatusBar, R.color.OrangeAccent, R.color.OrangeFab2, R.color.OrangeFab3,
                    R.color.OrangeFab4, toolbar, saveButton, window, mainOrNot, fabAction, fabAdd, fabDelete, fab_share);
            if (mainOrNot) {
                imageBackground.setVisibility(View.GONE);
                rlBackgroundNote.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            }
        }
        else if (themeColor.equalsIgnoreCase(context.getString(R.string.deepPurple))) {
            changeTheme(R.color.DeepPurpleToolBar, R.color.DeepPurpleStatusBar, R.color.DeepPurpleAccent, R.color.DeepPurpleFab2, R.color.DeepPurpleFab3,
                    R.color.DeepPurpleFab4, toolbar, saveButton, window, mainOrNot, fabAction, fabAdd, fabDelete, fab_share);
            if (mainOrNot) {
                imageBackground.setVisibility(View.GONE);
                rlBackgroundNote.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            }
        }
    }

    private static void changeTheme(int toolbarColor, int statusBar, int accent, int fab2, int fab3, int fab4,
                                    Toolbar toolbar, FloatingActionButton saveButton, Window window,
                                    boolean mainOrNot, FloatingActionButton fabAction, FloatingActionButton fabAdd,
                                    FloatingActionButton fabDelete, FloatingActionButton fab_share) {

        Context context = ApplicationContext.getApplicationContext();

        toolbar.setBackgroundColor(ContextCompat.getColor(context, toolbarColor));
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor((ContextCompat.getColor(context, statusBar)));
        if(!mainOrNot) {
            saveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, accent)));
        }
        else
        {
            fabAction.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, accent)));
            fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, fab2)));
            fabDelete.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, fab3)));
            fab_share.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, fab4)));
        }

    }

    public static void startActivityFromIntro(FragmentActivity activity){
        Intent intent = new Intent(activity , AddNotesView.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
