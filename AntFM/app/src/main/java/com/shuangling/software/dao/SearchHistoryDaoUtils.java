package com.shuangling.software.dao;

import com.shuangling.software.MyApplication;
import com.shuangling.software.entity.SearchHistory;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.List;

/**
 * Created by Administrator on 2018-03-15.
 */

public class SearchHistoryDaoUtils {
    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param searchHistory
     */
    public static void insertSearchHistory(SearchHistory searchHistory) {
        MyApplication.getDaoInstant().getSearchHistoryDao().insert(searchHistory);
    }




    public static void insertOrReplace(SearchHistory searchHistory)
    {
        MyApplication.getDaoInstant().getSearchHistoryDao().insertOrReplace(searchHistory);
    }
    /**
     * 删除数据
     *
     * @param id
     */
    public static void deleteSearchHistory(long id) {
        MyApplication.getDaoInstant().getSearchHistoryDao().deleteByKey(id);
    }

    /**
     * 更新数据
     *
     * @param searchHistory
     */
    public static void updateSearchHistory(SearchHistory searchHistory) {
        MyApplication.getDaoInstant().getSearchHistoryDao().update(searchHistory);
    }

    /**
     * 查询条件为Type=TYPE_LOVE的数据
     *
     * @return
     */
//    public static List<Material> queryMaterial() {
//        return MyApplication.getDaoInstant().getMaterialDao().queryBuilder().where(MaterialDao.Properties.MeterialType.eq(Shop.TYPE_LOVE)).list();
//    }

    /**
     * 查询全部数据
     */
    public static List<SearchHistory> queryAll() {

        //return MyApplication.getDaoInstant().getMaterialDao().loadAll();
        QueryBuilder<SearchHistory> builder = MyApplication.getDaoInstant().getSearchHistoryDao().queryBuilder()
                .orderDesc(SearchHistoryDao.Properties.CreateTime);
        return builder.build().list();
    }


    public static void cleanAll() {

        //return MyApplication.getDaoInstant().getMaterialDao().loadAll();
        MyApplication.getDaoInstant().getSearchHistoryDao().deleteAll();
    }


    public static List<SearchHistory> queryAll(String str) {

        //return MyApplication.getDaoInstant().getMaterialDao().loadAll();
        QueryBuilder<SearchHistory> builder = MyApplication.getDaoInstant().getSearchHistoryDao().queryBuilder()
                .where(SearchHistoryDao.Properties.HistoryString.eq(str))
                .orderDesc(SearchHistoryDao.Properties.CreateTime);
        return builder.build().list();
    }

}
