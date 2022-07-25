package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcTemplateMemberRepository implements MemberRepository{

    private final JdbcTemplate jdbcTemplate;

    @Autowired  // JdbcTemplateMemberRepository 생성자가 하나면 @Autowired 생략 가능
    public JdbcTemplateMemberRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public Member Save(Member member) {

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);

        // insert 쿼리를 작성할 필요없음
        // member테이블과 id 컬럼만 지정해주면 자동으로 insert문 완성
        jdbcInsert.withTableName("member").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("name", member.getName());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        member.setId(key.longValue());

        return member;


    }

    @Override
    public Optional<Member> findById(Long id) {
        List<Member> result = jdbcTemplate.query("select * from member where id = ?", memberRowMapper(), id);
        return result.stream().findAny(); // List로 받아 해당 id를 가진것만 stream().findAny()로 찾아 리턴. 리턴값이 단건(Optional<Member>) 임.
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = jdbcTemplate.query("select * from member where name = ?", memberRowMapper(), name);
        return result.stream().findAny();       // List로 받아 해당 name를 가진것만 stream().findAny()로 찾아 리턴. 리턴값이 단건(Optional<Member>) 임.
    }

    @Override
    public List<Member> findAll() {
        List<Member> result = jdbcTemplate.query("select * from member", memberRowMapper());
        return result;  // 전체를 리턴해야 하므로 List 전체를 리턴.  리턴값이 List 임.
    }


    private RowMapper<Member> memberRowMapper(){
        return new RowMapper<Member>() {
            @Override
            public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return member;
            }
        };
    }
}
