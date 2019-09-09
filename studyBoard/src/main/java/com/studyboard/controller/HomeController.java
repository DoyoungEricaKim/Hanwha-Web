package com.studyboard.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.studyboard.model.APostDAO_Mybatis;
import com.studyboard.model.APostDTO;
import com.studyboard.model.MemberDAO_Mybatis;
import com.studyboard.model.MemberDTO;
import com.studyboard.model.PostDAO_Mybatis;
import com.studyboard.model.PostDTO;
import com.studyboard.model.QPostDAO_Mybatis;
import com.studyboard.model.QPostDTO;

@Controller
public class HomeController {
	
	@Autowired
	MemberDAO_Mybatis dao;
	
	@Autowired
	PostDAO_Mybatis pdao;
	
	@Autowired
	QPostDAO_Mybatis qdao;
	
	@Autowired
	APostDAO_Mybatis adao;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "login";
	}
	

	@RequestMapping(value = "/join", method = RequestMethod.GET)
	//�α��� �������� �̵�
	public String join_get() {
		
		return "join";
	}
	
	@RequestMapping(value = "/join", method = RequestMethod.POST)
	//ȸ������ form������ �޾Ƽ� �α��� �������� �̵�
	public String join_post(MemberDTO mem) {
		dao.insert(mem);
		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	//�α��� �������� �̵�
	public String login_get() {
		
		return "login";
	}
	
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	//�α��� ������ mypage�� �̵�
	public String login_check(MemberDTO mem, HttpServletRequest request) {
		MemberDTO tmp = dao.selectById(mem.getMid());
		if (tmp == null) {
			// ������ �������� ����
			return "/";
		} else if (!tmp.getPw().equals(mem.getPw())) {
			// ����� Ʋ��
			return "/";
		} else {
			// �α��� ����
			HttpSession session = request.getSession();
			session.setAttribute("mem", tmp);
			return "redirect:mypage";

		}
		 
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:login";
	}
	
	
	@RequestMapping("/mypage")
	public ModelAndView mypage(HttpSession session) {
		MemberDTO tmp = (MemberDTO) session.getAttribute("mem");
		MemberDTO mem = dao.selectById(tmp.getMid());
		ModelAndView mv = new ModelAndView();
		mv.addObject("postlist", pdao.selectAllByPoster(mem.getMname()));
		mv.setViewName("mypage");
		return mv;
	}

	@RequestMapping(value = "/postdetail", method = RequestMethod.GET)
	public String getPostDetail(int pid, Model model) {
		model.addAttribute("post", pdao.selectById(pid)	);
		return "postdetail";
	}
	

	@RequestMapping(value = "/editpost", method = RequestMethod.GET)
	public ModelAndView editPostGet(int pid) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("post", pdao.selectById(pid));
		mv.setViewName("/editpost");
		return mv;
	}

	
	@RequestMapping(value = "/editpost", method = RequestMethod.POST)
	public String editPostPost(PostDTO post) {
		pdao.editPost(post);
		return "redirect:postdetail?pid=" + post.getPid();
	}

	@RequestMapping("/postdelete")
	public String deptDelete(int pid) {
		pdao.deletePost(pid);
		return "redirect:mypage";
	}
	
	@RequestMapping(value="/insertpost", method = RequestMethod.GET)
	public String getinsert() {
		return "/insertpost";
	}
	
	@RequestMapping(value="/insertpost", method = RequestMethod.POST)
	public String insertPost(PostDTO post) {
		pdao.insertPost(post);
		PostDTO newPost = pdao.selectByTitle(post.getPtitle());
		return "redirect:postdetail?pid=" + newPost.getPid();
	}
	
	//QnA Board
	@RequestMapping("/qnaboard")
	public ModelAndView qnaboard() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("postlist", qdao.qselectAll());
		mv.setViewName("qnaboard");
		return mv;
	}
	@RequestMapping(value="/insertpost2", method = RequestMethod.GET)
	public String getinsert_qpost() {
		return "/insertpost2";
	}
	
	@RequestMapping(value="/insertpost2", method = RequestMethod.POST)
	public String insertPost_qpost(QPostDTO qpost) {
		qdao.insertqPost(qpost);
		QPostDTO newPost = new QPostDTO();
		newPost = qdao.qselectByTitle(qpost.getPtitle());
		return "redirect:postdetail2?pid=" + newPost.getPid();
	}
	

	@RequestMapping(value = "/postdetail2", method = RequestMethod.GET)
	public String getPostDetail2(int pid, Model model, HttpSession session) {
		QPostDTO qvo = qdao.qselectById(pid);
		MemberDTO tmp = (MemberDTO) session.getAttribute("mem");
		model.addAttribute("post", qdao.qselectById(pid));
		model.addAttribute("comments", adao.aselectById(pid));
		if(!qvo.getPoster().equals(tmp.getMname())) {
			qdao.updateHit(pid);
		}
		return "/postdetail2";
	}
	
	@RequestMapping(value = "/postdetail2", method = RequestMethod.POST)
	public String getPostDetail2post(APostDTO apost) {
		adao.insertaPost(apost);
		qdao.updateCom(apost.getPid());
		return "redirect:postdetail2?pid="+apost.getPid();
	}
	
	
	@RequestMapping(value = "/editpost2", method = RequestMethod.GET)
	public ModelAndView editPostGet2(int pid) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("post", qdao.qselectById(pid));
		mv.setViewName("/editpost2");
		return mv;
	}

	
	@RequestMapping(value = "/editpost2", method = RequestMethod.POST)
	public String editPostPost2(QPostDTO qpost) {
		qdao.editqPost(qpost);
		return "redirect:postdetail2?pid=" + qpost.getPid();
	}

	
	@RequestMapping("/postdelete2")
	public String deptDelete2(int pid) {
		qdao.qdeletePost(pid);
		return "redirect:qnaboard";
	}
	
	@RequestMapping("/postdelete3")
	public String deptDelete3(String pcontent) {
		APostDTO avo = adao.aselectByContent(pcontent);
		qdao.decreasecom(avo.getPid());
		adao.adeletePost(pcontent);
		return "redirect:postdetail2?pid=" + avo.getPid();
	}
	

	
	//Error handling
	@RequestMapping("/404")
	public String error404() {
		return "error404";
	}
	
}