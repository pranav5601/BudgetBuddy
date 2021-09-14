package com.example.budgetbuddy.helper;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;
import androidx.annotation.NonNull;
import com.example.budgetbuddy.R;
import java.util.Objects;
public class Loader extends Dialog {
    public Loader(@NonNull Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.loader, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
}
