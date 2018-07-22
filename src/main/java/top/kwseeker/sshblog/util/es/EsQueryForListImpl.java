package top.kwseeker.sshblog.util.es;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

public class EsQueryForListImpl implements EsQueryForList {

//    @Autowired
//    ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public <E> List<E> executeSearchQuery(ElasticsearchTemplate elasticsearchTemplate ,String name, String content, Pageable pageable, Class<E> clazz) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
                matchPhraseQuery(name, content)).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, clazz);
    }
}
