package org.projetmanager.projetmanager.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.projetmanager.projetmanager.dto.GitStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GitService {

    public static final Logger LOGGER = LoggerFactory.getLogger(GitService.class);

    public GitStatusDto getStatus(Path pathProject) throws GitAPIException, IOException {
        Path gitPath = pathProject.resolve(".git");
        if (Files.exists(gitPath)) {
            try (Repository repository = new FileRepositoryBuilder()
                    .setGitDir(gitPath.toFile())
                    .build()) {
                try (Git git = new Git(repository)) {
                    Status status = git.status().call();
                    LOGGER.info("Added: {}", status.getAdded());
                    List<Ref> tmp = git.branchList().call();
                    LOGGER.info("branches: {}", tmp);

                    GitStatusDto gitStatusDto = new GitStatusDto();
                    gitStatusDto.setBranches(new ArrayList<>());
                    gitStatusDto.setClean(status.isClean());
                    if (!CollectionUtils.isEmpty(tmp)) {
                        String debutBranche = "refs/heads/";
                        for (Ref ref : tmp) {
                            String name = ref.getName();
                            if (StringUtils.hasText(name)) {
                                if (name.startsWith(debutBranche)) {
                                    name = name.substring(debutBranche.length());
                                }
                                gitStatusDto.getBranches().add(name);
                            }
                            LOGGER.info("name: {}", name);
                            if (ref.getObjectId() != null) {
                                String tmp3 = ref.getObjectId().getName();
                                LOGGER.info("commit: {}", tmp3);
//                                    if (StringUtils.hasText(tmp3) && gitStatusDto.getLastCommit() == null) {
//                                        gitStatusDto.setLastCommit(tmp3);
//                                    }
                                RevWalk walk = new RevWalk(repository);
                                RevCommit commit = walk.parseCommit(ref.getObjectId());
                                String commitId = commit.getName();
                                LOGGER.info("commit name: {}", commit.getName());
                                LOGGER.info("commit2: {}", commit);
                                LOGGER.info("commit time: {}", commit.getCommitTime());
                                PersonIdent committerIdent = commit.getCommitterIdent();
                                LocalDateTime date = toLocalDateTime(committerIdent.getWhen());
                                String user = committerIdent.getName();
                                LOGGER.info("commit user: {}", committerIdent.getName());
                                LOGGER.info("commit date: {}", committerIdent.getWhen());
                                LOGGER.info("commit date2: {}", date);

                                if (StringUtils.hasText(commitId) && gitStatusDto.getLastCommit() == null) {
                                    gitStatusDto.setLastCommit(commitId);
                                    gitStatusDto.setDateTimeLastCommit(date);
                                    gitStatusDto.setAuteur(user);
                                }
                            }
                        }
                    }
                    return gitStatusDto;
                }
            }
        } else {
            return null;
        }
    }

    private LocalDateTime toLocalDateTime(Date dateToConvert) {
        if (dateToConvert == null) {
            return null;
        } else {
            return dateToConvert.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
    }
}
