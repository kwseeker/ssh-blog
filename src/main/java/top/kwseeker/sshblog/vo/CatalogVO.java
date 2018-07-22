package top.kwseeker.sshblog.vo;

import top.kwseeker.sshblog.domain.Catalog;

import java.io.Serializable;

public class CatalogVO implements Serializable {

    private static final Long SerialVersionUID = 1L;

    private String username;
    private Catalog catalog;

    public CatalogVO() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }
}
