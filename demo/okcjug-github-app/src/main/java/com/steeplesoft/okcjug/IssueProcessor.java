package com.steeplesoft.okcjug;

import java.io.IOException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import com.steeplesoft.okcjug.config.OkcJugBotConfigFile;
import io.quarkiverse.githubapp.ConfigFile;
import io.quarkiverse.githubapp.event.Issue;
import io.quarkiverse.githubapp.event.IssueComment;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHUser;

public class IssueProcessor {
    public static final String SUBJECT_CANT_COMMENT = "You are not allowed to comment";
    public static final String BODY_CANT_COMMENT = """
            You are not allowed on this issue. If you feel this is in error, please contact the repository owner.
                                
            Your comment will not be seen.
            """.stripIndent();
    @Inject
    MessagingService messagingService;

    public static final String EDITING_COMMENTS_IS_NOT_ALLOWED = "Editing comments is not allowed.";
    @Inject
    Foo foo;

    void onOpen(@Issue.Opened GHEventPayload.Issue issuePayload,
                @ConfigFile("okcjug-bot.yml") OkcJugBotConfigFile configFile) throws IOException {
        issuePayload.getIssue().comment("Hello from my GitHub App");

        foo.bar(configFile);
    }

    void onEdit(@IssueComment.Edited GHEventPayload.IssueComment payload,
                @ConfigFile("okcjug-bot.yml") OkcJugBotConfigFile configFile) throws IOException {
        GHIssue issue = payload.getIssue();
        GHEventPayload.CommentChanges changes = payload.getChanges();

        String oldComment = changes.getBody().getFrom();
        String newComment = payload.getComment().getBody();
    }

    void enforceCanComment(@IssueComment.Created GHEventPayload.IssueComment payload,
                           @ConfigFile("okcjug-bot.yml") OkcJugBotConfigFile configFile) throws IOException {
        GHIssueComment comment = payload.getComment();
        GHUser user = payload.getSender();
        String login = user.getLogin();
        if (!user.getType().equals("Bot") && !configFile.canComment.contains(login)) {
            comment.delete();
            messagingService.sendEmail(user.getEmail(), SUBJECT_CANT_COMMENT, BODY_CANT_COMMENT);
        }
    }

    @RequestScoped
    public static class Foo {

        public void bar(OkcJugBotConfigFile configFile) {
            System.err.println(configFile.toString());
        }

    }
}
