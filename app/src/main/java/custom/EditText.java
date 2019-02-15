package custom;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by mhssn on 6/26/2017.
 */

public class EditText extends android.support.v7.widget.AppCompatEditText {
    public EditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/Cairo-Bold.ttf");
        this.setTypeface(typeface);
    }
}
