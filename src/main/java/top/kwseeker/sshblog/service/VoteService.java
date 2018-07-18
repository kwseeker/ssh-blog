package top.kwseeker.sshblog.service;

import top.kwseeker.sshblog.domain.Vote;

public interface VoteService {

    //根据id获取vote
    Vote getVoteById(Long id);

    //删除vote
    void removeVote(Long id);
}
