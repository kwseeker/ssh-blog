package top.kwseeker.sshblog.service;

import top.kwseeker.sshblog.domain.Authority;

public interface AuthorityService {
    //根据id获取Authority
    Authority getAuthorityById(Long id);
}
