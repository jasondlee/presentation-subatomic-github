package com.steeplesoft.okcjug;

import static io.quarkiverse.githubapp.testing.GitHubAppTesting.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import io.quarkiverse.githubapp.testing.GitHubAppTest;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHEvent;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@QuarkusTest
@GitHubAppTest
@ExtendWith(MockitoExtension.class)
public class IssueProcessorTest {
    private MessagingService messagingService;

    @BeforeEach
    public void setup() {
        messagingService = Mockito.mock(MessagingService.class);
        QuarkusMock.installMockForType(messagingService, MessagingService.class);
    }

    @Test
    void testIssueOpened() throws IOException {
        given()
                .github(mocks -> {
                    mocks.configFile("okcjug-bot.yml")
                            .fromClasspath("/okcjug-bot.yml");
                })
                .when()
                .payloadFromClasspath("/issue-opened.json")
                .event(GHEvent.ISSUES)
                .then()
                .github(mocks -> {
                    verify(mocks.issue(2106979380))
                            .comment("Hello from my GitHub App");
                });
    }

    @Test
    public void testEnforceCanCommentWithInvalidUser() throws IOException {
        given()
                .github(mocks -> mocks.configFile("okcjug-bot.yml")
                        .fromString("""
                                canComment:
                                  - nobody
                                """.stripIndent()))
                .when().payloadFromClasspath("/issue-comment-created.json")
                .event(GHEvent.ISSUE_COMMENT)
                .then().github(mocks -> {
                    verify(messagingService)
                            .sendEmail(any(), matches(IssueProcessor.SUBJECT_CANT_COMMENT), matches(IssueProcessor.BODY_CANT_COMMENT));
                });
    }

    @Test
    public void testEnforceCanCommentWithValidUser() throws IOException {
        given()
                .github(mocks -> {
                    mocks.configFile("okcjug-bot.yml")
                            .fromString("""
                                    canComment:
                                      - jasondlee
                                    """.stripIndent());
                })
                .when()
                .payloadFromClasspath("/issue-comment-created.json")
                .event(GHEvent.ISSUE_COMMENT)
                .then()
                .github(mocks -> {
                    verify(messagingService, never())
                            .sendEmail(any(), any(), any());
                });
    }
}
