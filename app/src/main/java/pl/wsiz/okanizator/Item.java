package pl.wsiz.okanizator;

public class Item {
    private String textitem;
    private String email;
    public Item(){
        //needed
    }

   public Item(String textitem, String email) {
       this.email=email;
       this.textitem=textitem;
   }

    public String getTextitem() {
        return textitem;
    }

    public void setTextitem(String textitem) {
        this.textitem = textitem;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
