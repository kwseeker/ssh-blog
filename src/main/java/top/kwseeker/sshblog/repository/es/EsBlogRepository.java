package top.kwseeker.sshblog.repository.es;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.kwseeker.sshblog.domain.es.EsBlog;

//ES 查询方法构造 https://docs.spring.io/spring-data/elasticsearch/docs/3.1.0.M3/reference/html/#repositories.query-methods.details
public interface EsBlogRepository extends ElasticsearchRepository<EsBlog, String> {

    //模糊查询去重
    Page<EsBlog> findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(
            String title, String Summary, String content, String tags, Pageable pageable);

    Page<EsBlog> findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContaining(
            String title, String Summary, String content, Pageable pageable);

    Page<EsBlog> findDistinctEsBlogBySummaryContainingAndContentContaining(
            String Summary, String content, Pageable pageable);

    EsBlog findByBlogId(Long blogId);
}
