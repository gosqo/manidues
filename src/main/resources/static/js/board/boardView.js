import { BoardView } from "../libs/board/BoardView.js";
import { State } from "../libs/state/StateManage.js";
import { Board } from "../libs/board/Board.js";
import { CommentLike } from "../libs/commentLike/CommentLike.js";
import { Comment } from "../libs/comment/Comment.js";
import { TokenUtility } from "../libs/token/TokenUtility.js";
import { AuthChecker } from "../libs/token/AuthChecker.js";

document.addEventListener("DOMContentLoaded", async () => {
    const boardId = Board.Utility.getBoardId();

    if (location.pathname !== (`/board/${boardId}`)) return;

    State.replaceCurrentState();
    
    if (AuthChecker.hasAuth()) {
        try {
            TokenUtility.invalidateRefreshTokenInLocalStorage();
            TokenUtility.forceReissueAccessToken();
        } catch (error) {
            console.error(error);
        }
    }

    BoardView.DOM.present();
    Comment.DOM.addRegisterEvent();
    Comment.DOM.addModifyButtonInModalEvent();
    Comment.getComments();
    CommentLike.initPageUnloadHandler();
});

window.addEventListener("popstate", () => {
    State.replaceCurrentBodyWith(history.state.body);
    State.Event.dispatchDOMContentLoaded();
});
