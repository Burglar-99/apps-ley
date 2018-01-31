package mx.com.azteca.home.view.ipati.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import mx.com.azteca.home.util.Componente;


public class ContentView extends LinearLayout {

    private Componente.Tool tool;


    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTool(Componente.Tool tool) {
        this.tool = tool;
    }

    public Componente.Tool getTool() {
        return tool;
    }
}
