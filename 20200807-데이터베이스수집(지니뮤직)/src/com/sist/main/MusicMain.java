package com.sist.main;

import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.sist.dao.MusicDAO;
import com.sist.dao.MusicVO;

public class MusicMain {

	public static void main(String[] args) {
		MusicDAO dao = new MusicDAO();
		
		// 상세과정 : https://github.com/haenyilee/JAVA_Basic/wiki/Java_%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4_%EC%A7%80%EB%8B%88%EB%AE%A4%EC%A7%81%EB%8D%B0%EC%9D%B4%ED%84%B0%EC%88%98%EC%A7%91
		try {
			int k=1;
			for(int i=1;i<=4;i++)
			{
				try
				{
				Document doc = Jsoup.connect("https://www.genie.co.kr/chart/top200?ditc=D&ymd=20200807&hh=09&rtm=Y&pg="+i).get();
				// 소스 잘 읽혔나 확인용 : System.out.println(doc);
				Elements title=doc.select("td.info a.title");
        		Elements singer=doc.select("td.info a.artist");
        		Elements album=doc.select("td.info a.albumtitle");
        		Elements poster=doc.select("a.cover img");  // <>안에 있는 정보
        		Elements temp=doc.select("span.rank");
        		
        		for(int j=0;j<title.size();j++)
        		{
        			// html 정보 잘 긁혔는지 확인용
        			System.out.println("순위:"+k);
        			System.out.println("노래명:"+title.get(j).text());
        			System.out.println("가수명:"+singer.get(j).text());
        			System.out.println("앨범명:"+album.get(j).text());
        			System.out.println("포스터:"+poster.get(j).attr("src"));
        			//System.out.println("상태등폭:"+temp.get(j).text());
        			
        			// 상태등폭이 붙어서 출력되니까 각 값을 잘라주기
        			String str=temp.get(j).text();
        			String idcrement="";
        			String state="";
        			if(str.equals("유지"))
        			{
        				idcrement="0";
        				state="유지";
        			}
        			else if(str.equals("new"))
        			{
        				idcrement="0";
        				state="new";
        			}
        			else
        			{
        				// 숫자빼고 다 지워라 => 숫자값만 가져오기
        				idcrement=str.replaceAll("[^0-9]", "");
        				// 한글빼고 다 지워라 => 한글값만 가져오기
        				state=str.replaceAll("[^가-힣]", "");

        			}
        			System.out.println("상태:"+state);
        			System.out.println("등폭:"+idcrement);
        			// System.out.println("동영상키 :"+youtubeKeyData(title.get(j).text()));
        			System.out.println("-------------------------------");
        			
        			MusicVO vo = new MusicVO();
        			vo.setMno(k);
        			vo.setTitle(title.get(j).text());
        			vo.setSinger(singer.get(j).text());
        			vo.setAlbum(album.get(j).text());
        			vo.setPoster(poster.get(j).attr("src"));
        			vo.setState(state);
        			vo.setIdcrement(Integer.parseInt(idcrement));
        			vo.setKey(youtubeKeyData(title.get(j).text()));
        				
        			dao.musicInsert(vo); // 한줄씩 올라가게
        			
        			Thread.sleep(100);
        			
        			k++;
  
        		}
				}catch(Exception ex) {}

			}
		} catch (Exception e) {
			
		}
		
		// 유튜브 호출 잘되는지 한개만 확인해보기
		// youtubeKeyData("다시 여기 바닷가");
		

	}
	
	public static String youtubeKeyData(String title)
	{
		String key="";
		try {
			Document doc = Jsoup.connect("https://www.youtube.com/results?search_query="+title).get();
			Pattern p = Pattern.compile("/watch\\?v=[^가-힇]+");
			// 패턴을 doc와 매치시킨다.
			Matcher m = p.matcher(doc.toString());
			while(m.find())
			{
				// System.out.println(m.group());
				String str=m.group();
				
				// /watch?v=ESKfHHtiSjs","webPageType":"WEB_PAGE_TYPE_WATCH","rootVe":3832}},"watchEndpoint":{"videoId":"ESKfHHtiSjs"}},"badges":[{"metadataBadgeRenderer":{"style":"BADGE_STYLE_TYPE_SIMPLE","label":"
				// 에서 = 뒷부분부터 "까지만 잘라오기
				str=str.substring(str.indexOf("=")+1,str.indexOf("\""));
				key=str;
				break;
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return key;
	}

}
