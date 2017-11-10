package addnote.vnps.addnotes.pojo;

/**
 * @author Puneet
 */
public class NoteDetails {
    String note;
    String color;
    String textColor;
    int pendingIntentId;
    String alertTime;
    String typeOfNote;
    String shoppingString = "";
    String fontSelected;
    String shoppingTitle = "";
    String lockTitle = "";
    int lockedOrNot;
    String lockPassword = "";
    int widgetId = 0;
    String param1 = "";
    String param2 = "";

    int _id;


    // Empty constructor
    public NoteDetails() {

    }

    // constructor
    public NoteDetails(String note, String textColor, String color, String alertTime, String fontSelected, int pendingID, String shoppingString,
                       String type, String titleLocked,
                       int lockedOrNot, String passwordLock, int widgetId, String param1, String param2) {
        this.note = note;
        this.textColor = textColor;
        this.color = color;
        this.alertTime = alertTime;
        this.fontSelected = fontSelected;
        this.pendingIntentId = pendingID;
        this.shoppingString = shoppingString;
        this.typeOfNote = type;
        this.lockTitle = titleLocked;
        this.lockedOrNot = lockedOrNot;
        this.lockPassword = passwordLock;
        this.widgetId = widgetId;
        this.param1 = param1;
        this.param2 = param2;
    }

    // constructor
    public NoteDetails(int id, String note, String textColor, String color,
                       String alertTime, int pendingID, String fontSelected, String shoppingString,
                       String type, String shoppingTitle,
                       String titleLocked, int lockedOrNot, String passwordLock, int widgetId, String param1, String param2) {
        this._id = id;
        this.note = note;
        this.textColor = textColor;
        this.color = color;
        this.alertTime = alertTime;
        this.pendingIntentId = pendingID;
        this.fontSelected = fontSelected;
        this.shoppingString = shoppingString;
        this.typeOfNote = type;
        this.shoppingTitle = shoppingTitle;
        this.lockTitle = titleLocked;
        this.lockedOrNot = lockedOrNot;
        this.lockPassword = passwordLock;
        this.widgetId = widgetId;
        this.param1 = param1;
        this.param2 = param2;
    }

    // constructor
    public NoteDetails(String note, String textColor, String color, String alertTime, int pendingID, String fontSelected, String shoppingString, String type, String shoppingTitle) {
        this.note = note;
        this.textColor = textColor;
        this.color = color;
        this.alertTime = alertTime;
        this.pendingIntentId = pendingID;
        this.fontSelected = fontSelected;
        this.shoppingString = shoppingString;
        this.typeOfNote = type;
        this.shoppingTitle = shoppingTitle;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    public String getLockPassword() {
        return lockPassword;
    }

    public void setLockPassword(String lockPassword) {
        this.lockPassword = lockPassword;
    }

    public int getLockedOrNot() {
        return lockedOrNot;
    }

    public void setLockedOrNot(int lockedOrNot) {
        this.lockedOrNot = lockedOrNot;
    }

    public String getLockTitle() {
        return lockTitle;
    }

    public void setLockTitle(String lockTitle) {
        this.lockTitle = lockTitle;
    }

    public String getShoppingTitle() {
        return shoppingTitle;
    }

    public void setShoppingTitle(String shoppingTitle) {
        this.shoppingTitle = shoppingTitle;
    }

    public String getTypeOfNote() {
        return typeOfNote;
    }

    public void setTypeOfNote(String typeOfNote) {
        this.typeOfNote = typeOfNote;
    }

    public String getShoppingString() {
        return shoppingString;
    }

    public void setShoppingString(String shoppingString) {
        this.shoppingString = shoppingString;
    }

    public String getFontSelected() {
        return fontSelected;
    }

    public void setFontSelected(String fontSelected) {
        this.fontSelected = fontSelected;
    }

    public int getPendingIntentId() {
        return pendingIntentId;
    }

    public void setPendingIntentId(int pendingIntentId) {
        this.pendingIntentId = pendingIntentId;
    }

    public String getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(String alertTime) {
        this.alertTime = alertTime;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
