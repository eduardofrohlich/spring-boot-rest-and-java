package br.com.frohlich.integrationtests.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@XmlRootElement
@JsonPropertyOrder({"id", "author", "launch_date", "price", "title"})
public class BookVO extends RepresentationModel<BookVO> implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long key;
    private String author;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    @JsonProperty("launch_date")
    private Date launchDate;

    private Double price;
    private String title;

    public BookVO() {

    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BookVO bookVO = (BookVO) o;
        return Objects.equals(key, bookVO.key) && Objects.equals(author, bookVO.author) && Objects.equals(price, bookVO.price) && Objects.equals(launchDate, bookVO.launchDate) && Objects.equals(title, bookVO.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), key, author, price, launchDate, title);
    }
}
