package com.gosqo.flyinheron.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "member_id_comment_id_unique"
                , columnNames = {"member_id", "comment_id"}
        )
)
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike extends IdentityBaseEntity {
    @ManyToOne(
            targetEntity = Comment.class
            , optional = false
            , fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "comment_id"
            , nullable = false
            , referencedColumnName = "id"
            , foreignKey = @ForeignKey(name = "fk_comment_like_comment_comment_id")
    )
    private Comment comment;

    @ManyToOne(
            targetEntity = Member.class
            , optional = false
            , fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "member_id"
            , nullable = false
            , referencedColumnName = "id"
            , foreignKey = @ForeignKey(name = "fk_comment_like_member_member_id")
    )
    private Member member;

    @Override
    @PrePersist
    public void prePersist() {
        this.status = EntityStatus.ACTIVE;
        this.comment.addLikeCount();
    }

    @Override
    public String toString() {
        return "CommentLike{" +
                "\nid=" + id +
                "\n, member=" + member +
                "\n, comment=" + comment +
                "\n, status=" + status +
                "\n, registeredAt=" + registeredAt +
                "\n, updatedAt=" + updatedAt +
                "\n, contentModifiedAt=" + contentModifiedAt +
                "\n, deletedAt=" + deletedAt +
                '}';
    }
}
