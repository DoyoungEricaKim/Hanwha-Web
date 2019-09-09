package com.studyboard.model;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class APostDAO_Mybatis {

	@Autowired
	SqlSession session;


	public List<APostDTO> aselectById(int pid) {
		return session.selectList("com.studyboard.aselectbyid", pid);
	}


	public List<APostDTO> aseletAll() {
		return session.selectList("com.studyboard.aselectall");
	}


	public int insertaPost(APostDTO apost) {
		return session.insert("com.studyboard.ainsert", apost);
	}


	public List<Integer> countCom() {
		return session.selectList("com.studyboard.countcom");
	}


	public int adeletePost(String pcontent) {
		return session.delete("com.studyboard.adelete", pcontent);
	}


	public APostDTO aselectByContent(String pcontent) {
		return session.selectOne("com.studyboard.aselectbycontent", pcontent);
	}
	
	

	
	
}