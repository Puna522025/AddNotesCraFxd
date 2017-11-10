package addnote.vnps.addnotes.widget;

/**
 * Created by DELL on 5/20/2016.
 */
public class WidgetPOJO {


    String widgetNote;
    int widgetId;
    String colorWidget;
    String textColorWidget;
    String param2;

    public String getColorWidget() {
        return colorWidget;
    }

    public void setColorWidget(String colorWidget) {
        this.colorWidget = colorWidget;
    }

    public String getTextColorWidget() {
        return textColorWidget;
    }

    public void setTextColorWidget(String textColorWidget) {
        this.textColorWidget = textColorWidget;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }



    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    public String getWidgetNote() {
        return widgetNote;
    }

    public void setWidgetNote(String widgetNote) {
        this.widgetNote = widgetNote;
    }



    // constructor
    public WidgetPOJO(String note, int widgetId, String color, String textColor, String param2) {
        this.widgetNote = note;
        this.widgetId = widgetId;
        this.colorWidget = color;
        this.textColorWidget = textColor;
        this.param2 = param2;
    }

    // Empty constructor
    public WidgetPOJO() {

    }


}
