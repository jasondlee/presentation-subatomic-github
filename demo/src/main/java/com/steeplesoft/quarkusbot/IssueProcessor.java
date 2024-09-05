package com.steeplesoft.quarkusbot;

import java.io.IOException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import com.steeplesoft.quarkusbot.config.BotConfigFile;
import io.quarkiverse.githubapp.ConfigFile;
import io.quarkiverse.githubapp.event.Issue;
import io.quarkiverse.githubapp.event.IssueComment;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueChanges;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHUser;

public class IssueProcessor {
    private static final String TAG = "[bot]";

    public static final String SUBJECT_CANT_COMMENT = "You are not allowed to comment";
    public static final String BODY_CANT_COMMENT = """
            You are not allowed on this issue. If you feel this is in error, please contact the repository owner.

            Your comment will not be seen.
            """.stripIndent();

    @Inject
    MessagingService messagingService;

    public static final String EDITING_COMMENTS_IS_NOT_ALLOWED = "Editing comments is not allowed.";

    void onOpen(@Issue.Opened GHEventPayload.Issue issuePayload) throws IOException {
        issuePayload.getIssue().comment("Hello from my GitHub App");
    }

    void onEdit(@Issue.Edited GHEventPayload.Issue payload) throws IOException {
        GHIssue issue = payload.getIssue();
        String newComment = issue.getBody();

        if (!payload.getSender().getType().equals("Bot")) {
            GHIssueChanges changes = payload.getChanges();
            String oldComment = changes.getBody().getFrom();

            issue.setBody(newComment + "\nProcessed by " + TAG);
        }
    }

    void enforceCanComment(@IssueComment.Created GHEventPayload.IssueComment payload,
                           @ConfigFile("bot-config.yml") BotConfigFile configFile) throws IOException {
        GHIssueComment comment = payload.getComment();
        GHUser user = payload.getSender();
        String login = user.getLogin();
        if (!user.getType().equals("Bot") && !configFile.canComment.contains(login)) {
            comment.delete();
            messagingService.sendEmail(user.getEmail(), SUBJECT_CANT_COMMENT, BODY_CANT_COMMENT);
        }
    }
}
