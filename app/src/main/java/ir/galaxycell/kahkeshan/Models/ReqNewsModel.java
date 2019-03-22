package ir.galaxycell.kahkeshan.Models;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Admin on 05/11/2017.
 */
public class ReqNewsModel
{
    public String subject,descriptions,link;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
