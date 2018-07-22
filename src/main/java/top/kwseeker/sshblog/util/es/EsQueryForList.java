package top.kwseeker.sshblog.util.es;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.util.List;

public interface EsQueryForList {

    <E> List<E> executeSearchQuery(ElasticsearchTemplate elasticsearchTemplate, String name, String content, Pageable pageable, Class<E> clazz);
}
