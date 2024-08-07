package com.vong.manidues.domain.commentlike;

import com.vong.manidues.domain.EntityStatus;
import com.vong.manidues.domain.IdentityBaseEntity;
import com.vong.manidues.domain.comment.Comment;
import com.vong.manidues.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
public class CommentLike extends IdentityBaseEntity {
    @ManyToOne(
            targetEntity = Comment.class
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
    )
    @JoinColumn(
            name = "member_id"
            , nullable = false
            , referencedColumnName = "id"
            , foreignKey = @ForeignKey(name = "fk_comment_like_member_member_id")
    )
    private Member member;

    protected void prePersist() {
        super.status = EntityStatus.ACTIVE;
    }
}
