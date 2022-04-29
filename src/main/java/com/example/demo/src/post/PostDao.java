package com.example.demo.src.post;


import com.example.demo.src.post.model.GetPostImgRes;
import com.example.demo.src.post.model.GetPostsRes;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;
    private List<GetPostImgRes> getPostImgRes;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetPostsRes> selectPosts(int userIdx){
        String selectPostsQuery = "   SELECT p.postIdx as postIdx,\n" +
                "          u.userIdx as userIdx,\n" +
                "          u.nickName as nickName,\n" +
                "          u.profileImgUrl as profileImgUrl,\n" +
                "          p.content as content,\n" +
                "          IF(postLikeCount is null, 0, postLikeCount) as postLikeCount,\n" +
                "          IF(commentCount is null, 0, commentCount) as commentCount,\n" +
                "          case\n" +
                "               when timestampdiff(second, p.updatedAt, current_timestamp) < 60\n" +
                "                   then concat(timestampdiff(second, p.updatedAt, current_timestamp), '초 전')\n" +
                "               when timestampdiff(minute , p.updatedAt, current_timestamp) < 60\n" +
                "                   then concat(timestampdiff(minute, p.updatedAt, current_timestamp), '분 전')\n" +
                "               when timestampdiff(hour , p.updatedAt, current_timestamp) < 24\n" +
                "                   then concat(timestampdiff(hour, p.updatedAt, current_timestamp), '시간 전')\n" +
                "               when timestampdiff(day , p.updatedAt, current_timestamp) < 365\n" +
                "                   then concat(timestampdiff(day, p.updatedAt, current_timestamp), '일 전')\n" +
                "               else timestampdiff(year , p.updatedAt, current_timestamp)\n" +
                "           end as uploadTime,\n" +
                "          IF(pl.status = 'ACTIVE', 'Y', 'N') as likeOrNot\n" +
                "   FROM Post as p\n" +
                "       join User as u on u.userIdx = p.userIdx\n" +
                "       left join (select postIdx, count(postLikeidx) as postLikeCount from PostLike WHERE status = 'ACTIVE' group by postIdx) plc on plc.postIdx = p.postIdx\n" +
                "       left join (select postIdx, count(commentIdx) as commentCount from Comment WHERE status = 'ACTIVE' group by postIdx) c on c.postIdx = p.postIdx\n" +
                "       left join Follow as f on f.followeeIdx = p.userIdx and f.status = 'ACTIVE'\n" +
                "       left join PostLike as pl on pl.userIdx = f.followerIdx and pl.postIdx = p.postIdx\n" +
                "   WHERE f.followerIdx = ? and p.status = 'ACTIVE'\n" +
                "   group by p.postIdx;";

        int selectPostsParam = userIdx;
        return this.jdbcTemplate.query(selectPostsQuery,
                (rs,rowNum) -> new GetPostsRes(
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("contents"),
                        rs.getInt("postLikeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("updatedAt"),
                        rs.getString("likeOrNot"),
                        getPostImgRes = this.jdbcTemplate.query("SELECT pi.postImgUrlIdx, pi.imgUrl + \n" +
                                "FROM PostImgUrl as pi \n" + "join Post as p on p.postIdx = pi.postIdx \n" +
                                "WHERE pi.status = 'ACTIVE' and p.postIdx = ?;",
                                (rk, rownum)-> new GetPostImgRes(
                                        rk.getInt("postImgUrlIdx"),
                                        rk.getString("imgUrl")
                                ),rs.getInt("postIdx")
                                )
                ), selectPostsParam);
    }
    public GetUserRes getUsersByEmail(String email){
        String getUsersByEmailQuery = "select userIdx,name,nickName,email from User where email=?";
        String getUsersByEmailParams = email;
        return this.jdbcTemplate.queryForObject(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                getUsersByEmailParams);
    }

    public GetUserRes getUsersByIdx(int userIdx){
        String getUsersByIdxQuery = "select userIdx,name,nickName,email from User where userIdx=?";
        int getUsersByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                getUsersByIdxParams);
    }

    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

}
