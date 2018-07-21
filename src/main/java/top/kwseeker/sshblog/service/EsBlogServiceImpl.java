package top.kwseeker.sshblog.service;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
//import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import top.kwseeker.sshblog.domain.User;
import top.kwseeker.sshblog.domain.es.EsBlog;
import top.kwseeker.sshblog.repository.es.EsBlogRepository;
import top.kwseeker.sshblog.vo.TagVO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

@Service
public class EsBlogServiceImpl implements EsBlogService {

    @Autowired
    private EsBlogRepository esBlogRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private static final Pageable TOP_5_PAGEABLE = new PageRequest(0, 5);
    private static final String EMPTY_KEYWORD = "";

    //删除Blog
    @Override
    public void removeEsBlog(String id) {
        esBlogRepository.deleteById(id);
    }

    //更新EsBlog
    @Override
    public EsBlog updateEsBlog(EsBlog esBlog) {
        return esBlogRepository.save(esBlog);
    }

    //根据id获取Blog
    @Override
    public EsBlog getEsBlogByBlogId(Long blogId) {
        return esBlogRepository.findByBlogId(blogId);
    }

    //最新博客列表，分页
    @Override
    public Page<EsBlog> listNewestEsBlogs(String keyword, Pageable pageable) {
        Page<EsBlog> pages = null;
        //根据创建时间降序排序
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");    //排序规则
        if(pageable.getSort() == null) {
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort); //创建带排序规则的分页对象
        }
        //分页查询 标题、摘要、内容、标签中含有 keyword 的博客条目
        pages = esBlogRepository.findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(
                keyword, keyword, keyword, keyword, pageable);
        return pages;
    }

    //最热博客列表，分页
    @Override
    public Page<EsBlog> listHotestEsBlogs(String keyword, Pageable pageable) {
        //根据阅读量，评论量，点赞量、创建时间依次降序排序
        Sort sort = new Sort(Sort.Direction.DESC,"readSize","commentSize","voteSize","createTime");
        if (pageable.getSort() == null) {
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }
        return esBlogRepository.findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(
                keyword, keyword, keyword, keyword, pageable);
    }

    //博客列表，分页
    @Override
    public Page<EsBlog> listEsBlogs(Pageable pageable) {
        return esBlogRepository.findAll(pageable);
    }

    //最新前5
    @Override
    public List<EsBlog> listTop5NewestEsBlogs() {
        Page<EsBlog> page = listNewestEsBlogs(EMPTY_KEYWORD, TOP_5_PAGEABLE);
        return page.getContent();
    }

    //最热前5
    @Override
    public List<EsBlog> listTop5HotestEsBlogs() {
        Page<EsBlog> page = listHotestEsBlogs(EMPTY_KEYWORD, TOP_5_PAGEABLE);
        return page.getContent();
    }

    //最热前30标签
    @Override
    public List<TagVO> listTop30Tags() {

        List<TagVO> list = new ArrayList<>();
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withSearchType(SearchType.QUERY_THEN_FETCH)
                .withIndices("blog").withTypes("blog")
                .addAggregation(AggregationBuilders.terms("tags").field("tags").order(Terms.Order.count(false)).size(30))
                .build();

        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        StringTerms modelTerms =  (StringTerms)aggregations.asMap().get("tags");

        Iterator<Bucket> modelBucketIt = modelTerms.getBuckets().iterator();
        while (modelBucketIt.hasNext()) {
            Bucket actiontypeBucket = modelBucketIt.next();

            list.add(new TagVO(actiontypeBucket.getKey().toString(),
                    actiontypeBucket.getDocCount()));
        }
        return list;
    }

    //最热前12用户
    @Override
    public List<User> listTop12Users() {

        List<String> usernamelist = new ArrayList<>();
        // given
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withSearchType(SearchType.QUERY_THEN_FETCH)
                .withIndices("blog").withTypes("blog")
                .addAggregation(AggregationBuilders.terms("users").field("username").order(Terms.Order.count(false)).size(12))
                .build();
        // when
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        StringTerms modelTerms =  (StringTerms)aggregations.asMap().get("users");

        Iterator<Bucket> modelBucketIt = modelTerms.getBuckets().iterator();
        while (modelBucketIt.hasNext()) {
            Bucket actiontypeBucket = modelBucketIt.next();
            String username = actiontypeBucket.getKey().toString();
            usernamelist.add(username);
        }
        List<User> list = userService.listUsersByUsernames(usernamelist);

        return list;
    }
}
