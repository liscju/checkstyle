////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle;

import static junit.framework.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.FluentIterable;

/**
 * Validate commit message has proper structure ,see Issue #937
 * @author <a href="mailto:piotr.listkiewicz@gmail.com">liscju</a>
 */
public class CommitValidationTest {

    private static final List<String> USERS_EXCLUDED_FROM_VALIDATION =
            Arrays.asList("Roman Ivanov");

    private static final String ISSUE_COMMIT_MESSAGE_REGEX_PATTERN = "^Issue #\\d*. .*$";
    private static final String PR_COMMIT_MESSAGE_REGEX_PATTERN = "^Pull #\\d*. .*$";
    private static final String OTHER_COMMIT_MESSAGE_REGEXPATTERN =
            "^(minor|config|infra|doc|spelling): .*$";

    private static final String ACCEPTED_COMMIT_MESSAGE_REGEX_PATTERN =
              "(" + ISSUE_COMMIT_MESSAGE_REGEX_PATTERN + ")|"
              + "(" + PR_COMMIT_MESSAGE_REGEX_PATTERN + ")|"
              + "(" + OTHER_COMMIT_MESSAGE_REGEXPATTERN + ")";

    private static final Pattern ACCEPTED_COMMIT_MESSAGE_PATTERN =
            Pattern.compile(ACCEPTED_COMMIT_MESSAGE_REGEX_PATTERN, Pattern.DOTALL);

    private static final String NEWLINE_REGEX_PATTERN = "\r\n?|\n";

    private static final Pattern NEWLINE_PATTERN = Pattern.compile(NEWLINE_REGEX_PATTERN);

    private static final String CHECKSTYLE_MAIN_REPOSITORY_URL = "https://github.com/checkstyle/checkstyle.git";

    private static List<String> lastCommitsMessages;

    private static List<String> getCommitsMessagesToCheck() throws Exception {
        List<RevCommit> validCommitsToCheck = filterValidCommits(getAddedRevCommits());

        List<String> commitMessagesToCheck = new LinkedList<String>();
        for (RevCommit commit : validCommitsToCheck) {
            commitMessagesToCheck.add(commit.getFullMessage());
        }
        return commitMessagesToCheck;
    }

    private static List<RevCommit> filterValidCommits(List<RevCommit> revCommits) {
        List<RevCommit> filteredCommits = new LinkedList<>();
        for (RevCommit commit : revCommits) {
            String commitAuthor = commit.getAuthorIdent().getName();
            if (!USERS_EXCLUDED_FROM_VALIDATION.contains(commitAuthor)) {
                filteredCommits.add(commit);
            }
        }
        return filteredCommits;
    }

    private static List<RevCommit> getAddedRevCommits() throws IOException, GitAPIException {
        Repository repo = new FileRepositoryBuilder().findGitDir().build();

        ObjectId currentBranchId = repo.resolve(repo.getFullBranch());
        ObjectId checkstyleRemoteMasterId = resolveRemoteCheckstyleMasterBranchId(repo);

        Iterable<RevCommit> addedCommits =
                new Git(repo).log().addRange(checkstyleRemoteMasterId, currentBranchId).call();

        return FluentIterable.from(addedCommits).toList();
    }

    private static ObjectId resolveRemoteCheckstyleMasterBranchId(Repository repo) throws IOException {
        Config storedConfig = repo.getConfig();
        Set<String> remotes = storedConfig.getSubsections("remote");
        String foundCheckstyleRemoteName = null;
        for (String remoteName : remotes) {
            String url = storedConfig.getString("remote", remoteName, "url");
            if (url.equals(CHECKSTYLE_MAIN_REPOSITORY_URL)) {
                foundCheckstyleRemoteName = remoteName;
            }
        }

        return repo.resolve(foundCheckstyleRemoteName + "/master");
    }

    private static String getRulesForCommitMessageFormatting() {
        return "Proper commit message should adhere to the following rules:\n"
                + "\t1) Must match one of the following patterns:\n"
                + "\t\t" + ISSUE_COMMIT_MESSAGE_REGEX_PATTERN + "\n"
                + "\t\t" + PR_COMMIT_MESSAGE_REGEX_PATTERN + "\n"
                + "\t\t" + OTHER_COMMIT_MESSAGE_REGEXPATTERN + "\n"
                + "\t2) It contains only one line";
    }

    private static String getInvalidCommitMessageFormattingError(String invalidCommitMessage) {
        return "Commit message: \"" + invalidCommitMessage + "\" is invalid\n"
                + getRulesForCommitMessageFormatting();
    }

    @BeforeClass
    public static void setUp() throws Exception {
        lastCommitsMessages = getCommitsMessagesToCheck();
    }

    @Test
    public void testCommitMessageHasProperStructure() throws Exception {
        for (String commitMessage : lastCommitsMessages) {
            Matcher matcher = ACCEPTED_COMMIT_MESSAGE_PATTERN.matcher(commitMessage);
            assertTrue(getInvalidCommitMessageFormattingError(commitMessage),
                    matcher.matches());
        }
    }

    @Test
    public void testCommitMessageHasSingleLine() throws Exception {
        for (String commitMessage : lastCommitsMessages) {
            Matcher matcher = NEWLINE_PATTERN.matcher(commitMessage);
            if (matcher.find()) {
                boolean isFoundNewLineCharacterAtTheEndOfMessage = matcher.end() == commitMessage.length();
                assertTrue(getInvalidCommitMessageFormattingError(commitMessage),
                        isFoundNewLineCharacterAtTheEndOfMessage);
            }
        }
    }
}
