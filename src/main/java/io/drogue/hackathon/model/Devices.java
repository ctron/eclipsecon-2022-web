package io.drogue.hackathon.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(
        indexes = @Index(columnList = ("claimedBy"), unique = true)
)
public class Devices {
    @Id
    private String id;

    private String claimedBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClaimedBy(String claimedBy) {
        this.claimedBy = claimedBy;
    }

    public String getClaimedBy() {
        return claimedBy;
    }
}
