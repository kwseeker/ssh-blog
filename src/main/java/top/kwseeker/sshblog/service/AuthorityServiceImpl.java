package top.kwseeker.sshblog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import top.kwseeker.sshblog.domain.Authority;
import top.kwseeker.sshblog.repository.AuthorityRepository;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public Authority getAuthorityById(Long id) {
        // TODO: 为什么使用getOne()总是报错：Method threw 'org.hibernate.LazyInitializationException' exception.
        //return authorityRepository.getOne(id);
        // findOne()的实现和原来的也不同了，改成了通过Example查询
        Authority authority = new Authority(id, null);
        Example<Authority> example = Example.of(authority);
//        return authorityRepository.findOne(example).get();
        return authorityRepository.findOne(example).orElse(null);
    }
}
