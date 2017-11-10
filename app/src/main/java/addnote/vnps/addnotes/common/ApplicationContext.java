package addnote.vnps.addnotes.common;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by pkapo8 on 07-Jun-17.
 */

public class ApplicationContext {

    private static Context mContext;

    public static void setContext(@NonNull Context context){
        mContext = context;
    }

    public static Context getApplicationContext(){
        return mContext;
    }
}
