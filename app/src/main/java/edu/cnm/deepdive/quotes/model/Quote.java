package edu.cnm.deepdive.quotes.model;

import com.google.gson.annotations.Expose;
import java.net.URL;
import java.util.Date;

public class Quote implements Content {

  @Expose
  private Long id;

  @Expose
  private Date created;

  @Expose
  private Date updated;

  @Expose
  private String text;

  @Expose
  private Source source;

  @Expose
  private User contributor;

  @Expose
  private URL href;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  public User getContributor() {
    return contributor;
  }

  public void setContributor(User contributor) {
    this.contributor = contributor;
  }

  public URL getHref() {
    return href;
  }

  public void setHref(URL href) {
    this.href = href;
  }

  public boolean isEditableBy(User user) {
    return user != null
        && contributor != null
        && user.getId().equals(contributor.getId());
  }

}
