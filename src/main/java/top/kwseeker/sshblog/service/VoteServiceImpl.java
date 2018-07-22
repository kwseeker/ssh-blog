package top.kwseeker.sshblog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kwseeker.sshblog.domain.Vote;
import top.kwseeker.sshblog.repository.VoteRepository;

import javax.transaction.Transactional;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    private VoteRepository voteRepository;

    //根据id获取vote
    @Override
    public Vote getVoteById(Long id) {
        return voteRepository.getOne(id);
    }

    //删除vote
    @Override
    @Transactional
    public void removeVote(Long id) {
        voteRepository.deleteById(id);
    }
}
