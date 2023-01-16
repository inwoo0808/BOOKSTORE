package ddwu.mobile.finalproject.model;

import java.util.List;

public class BookRoot {
    public List<NaverBook> getItems() {
        return items;
    }

    public void setItems(List<NaverBook> items) {
        this.items = items;
    }

    private List<NaverBook> items;
}
