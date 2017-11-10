package addnote.vnps.addnotes.pojo;

/**
 * @author Puneet
 */
public class ShoppingPojo {


    String itemToBuy;
    String value;
    String countOfItemsToBuy;
    String doneOrNot;

    public String getDoneOrNot() {
        return doneOrNot;
    }

    public void setDoneOrNot(String doneOrNot) {
        this.doneOrNot = doneOrNot;
    }

    public String getCountOfItemsToBuy() {
        return countOfItemsToBuy;
    }

    public void setCountOfItemsToBuy(String countOfItemsToBuy) {
        this.countOfItemsToBuy = countOfItemsToBuy;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getItemToBuy() {
        return itemToBuy;
    }

    public void setItemToBuy(String itemToBuy) {
        this.itemToBuy = itemToBuy;
    }

}
