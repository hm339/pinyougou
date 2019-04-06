package cn.itcast.core.service;

public interface SolrManagerService {

    public void saveItemToSolr(Long id);

    public void deleteSolrItem(Long id);
}
