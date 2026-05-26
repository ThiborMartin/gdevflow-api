package br.com.gdevflow.api.gdevflow_api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40, columnDefinition = "VARCHAR(40) DEFAULT 'IN_PROGRESS'")
    private ProjectStatus status = ProjectStatus.IN_PROGRESS;

    // Legacy column kept in sync to support existing PostgreSQL schemas.
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean closed = false;

    @Column
    private LocalDateTime completedAt;

    public Project(String name, String description, User owner, User client) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.client = client;
        this.status = ProjectStatus.IN_PROGRESS;
        this.closed = false;
        this.completedAt = null;
    }

    @PrePersist
    @PreUpdate
    void syncLegacyClosedColumn() {
        this.closed = this.status == ProjectStatus.COMPLETED;
    }
}
