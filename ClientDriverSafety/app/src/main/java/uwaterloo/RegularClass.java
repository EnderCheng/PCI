package uwaterloo;

import android.content.Context;
import android.content.res.Resources;

public class RegularClass {
    private Context context;

    public RegularClass(Context current){
        this.context = current;
    }

    public Resources findResource(){
        return context.getResources();
    }
}
