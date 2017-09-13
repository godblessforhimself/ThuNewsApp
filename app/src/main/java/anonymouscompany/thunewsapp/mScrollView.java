package anonymouscompany.thunewsapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * Created by Tony on 2017/9/13.
 */

public class mScrollView extends ScrollView {
    public mScrollView(Context context) {
        super(context);
    }

    public mScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public mScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public interface OnScrollChangedListener{
        public void onScrollChanged(int x, int y, int oldxX, int oldY);
    }

    private OnScrollChangedListener onScrollChangedListener;
    public void setOnScrollListener(OnScrollChangedListener onScrollChangedListener){
        this.onScrollChangedListener=onScrollChangedListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY){
        super.onScrollChanged(x, y, oldX, oldY);
        if(onScrollChangedListener!=null){
            onScrollChangedListener.onScrollChanged(x, y, oldX, oldY);
        }
    }
}
