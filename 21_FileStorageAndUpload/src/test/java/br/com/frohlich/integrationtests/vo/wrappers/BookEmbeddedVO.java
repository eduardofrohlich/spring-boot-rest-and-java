package br.com.frohlich.integrationtests.vo.wrappers;

import br.com.frohlich.integrationtests.vo.BookVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class BookEmbeddedVO {

    @JsonProperty("bookVOList")
    private List<BookVO> books;

    public BookEmbeddedVO() {
    }

    public List<BookVO> getbooks() {
        return books;
    }

    public void setbooks(List<BookVO> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookEmbeddedVO that = (BookEmbeddedVO) o;
        return Objects.equals(books, that.books);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(books);
    }
}
