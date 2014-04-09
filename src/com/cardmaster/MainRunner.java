package com.cardmaster;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainRunner {

	private static String findPrice(String api_url) {

		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpGet get = new HttpGet(api_url);
		CloseableHttpResponse response;
		String return_string = "";
		String content = "";
		try {
			response = httpclient.execute(get);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				content = content + line;
			}

			Pattern p = Pattern.compile("\\$\\d{1,10}\\.*\\d{0,2}");
			Matcher m = p.matcher(content);
			while (m.find()) {
				String val = m.group();
				return_string = return_string + val + ",";
			}
			httpclient.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			content = null;
		} catch (IOException e) {
			e.printStackTrace();
			content = null;
		}
		return return_string + api_url + ",";
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.exit(-1);
		}

		Document doc;
		String set = "";
		int min_no = 1;
		int max_no = 1;
		Scanner in = new Scanner(System.in);
		System.out.print("����ϵ�е�Ӣ��Сд��д��ͨ��3λ:");
		set = in.nextLine();
		System.out.print("��ʼ�Ƶı�ţ�ͨ��Ϊ1:");
		min_no = Integer.parseInt(in.nextLine());
		System.out.print("�����Ƶı�ţ�ͨ��Ϊ�ÿ��������������粻֪����300�����Զ��ж�:");
		max_no = Integer.parseInt(in.nextLine());
		try {
			FileWriter fw = new FileWriter(set + ".csv", true);

			for (int i = min_no; i < max_no + 1; i++) {
				doc = Jsoup.connect(
						"http://magiccards.info/" + set + "/cn/" + i + ".html")
						.get();
				System.out.println("ץȡ�У���ǰϵ��[" + set + "]��[" + i + "]��");
				Elements newsHeadlines = doc.select("script");

				for (Element element : newsHeadlines) {
					String src = element.attr("src").toString();
					if (src.indexOf("sid", 0) > 0) {
						Thread.sleep(1000);

						String price_str = MainRunner.findPrice(src);
						String s = price_str + set + "," + i + "\r\n";
						fw.write(s, 0, s.length());
						fw.flush();
						break;
					}
				}
				Thread.sleep(3000);
			}
			System.out.println("ץȡ�۸���ϣ��������ͬһĿ¼�µ���ӦCSV�ļ���@CardMaster");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
