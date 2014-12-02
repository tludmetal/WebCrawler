

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sewn.crawler.LinkedData;
import com.sewn.crawler.ResultPrinter;
import com.sewn.crawler.Robot;

public class Crawler {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader reader;
		String lineRead;
		Crawler cr= new Crawler("http://www.dcs.bbk.ac.uk/~martin/sewn/ls3/");
		File file = new File("..\\crawler.txt");
		File res = new File("..\\results.txt");
		String content = ResultPrinter.Print(root);
		String results = ResultPrinter.PrintResults(root);
		reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("To print crawler.txt press enter...");
		lineRead = reader.readLine();
		System.out.println(content);
		reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("To print results.txt press enter...");
		lineRead = reader.readLine();
		System.out.println(results);
 
		try (FileOutputStream fop = new FileOutputStream(file)) {
 
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			// get the content in bytes
			byte[] contentInBytes = content.getBytes();
 
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
			System.out.println("Links printed");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (FileOutputStream fop = new FileOutputStream(res)) {
			 
			// if file doesn't exists, then create it
			if (!res.exists()) {
				res.createNewFile();
			}
 
			// get the content in bytes
			byte[] contentInBytes = results.getBytes();
 
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
			System.out.println("Results printed");
			System.out.println("Two files were generated on the root of the project. Results.txt and Crawler.txt");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String url;
	private static LinkedData root;
	private HashSet<String> visited;
	private Robot robotChecker;

	private String[] extensions = new String[] { "html", "htm" };

	private static String _OUTOFDOMAIN = "The site is out of the domain of crawling";
	private static String _DISALLOWED = "This link is disallowed by server's admin";
	private static String _NOTAWEBSITE = "This link is not a web page";
	private static String _ALREADYINDEXED = "This link has already been indexed";
	private static String _NOTHTTP = "This is not an HTTP link";

	
//	JFL: Makes a crawler, starts the connection, validates the URL and rear Robots
	public Crawler(String url) {
		this.url = url;
		try {
			visited = new HashSet<String>();
			validateUrl();
			root = new LinkedData(this.url);
			readRobots();
			parse();
			//System.out.println(ResultPrinter.PrintResults(root));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readRobots() {
		String domain = root.getDirectory();

		if (!domain.endsWith("/"))
			domain += "/";

		robotChecker = new Robot(domain + "robots.txt");
	}

	
	//JLF:Add html page in case if it is necessary
	private void validateUrl() {
		if (url.endsWith("/"))
			url += "index.html";

		else if (!this.url.contains(".htm") && !this.url.contains(".html"))
			this.url += "/index.html";

	}

	public LinkedData parse() throws IOException {

		generateRoot();

		for (LinkedData child : root.getChildren())
			parse(child);

		return root;

	}

	private void parse(LinkedData node) throws IOException {
		if (meetProperties(node)) {
			HashSet<String> childLinks = getLinks(node.getAbsoluteLink());

			node.visited = true;

			if (childLinks == null || childLinks.isEmpty())
				return;

			for (String link : childLinks)
				node.addChild(link);

			for (LinkedData child : node.getChildren())
				parse(child);
		}
	}

	private Boolean meetProperties(LinkedData node) {
		if (!isHttpLink(node))
			return false;

		if (!isInRootDomain(node))
			return false;

		if (!isAllowedByRobotstxt(node))
			return false;

		if (!isWebSite(node))
			return false;

		if (isAlreadyVisited(node))
			return false;

		return true;

	}

	private Boolean isHttpLink(LinkedData node) {

		if (node.getProtocol().equals("http"))
			return true;

		node.reasonNotVisited = _NOTHTTP;
		return false;

	}

	private Boolean isAlreadyVisited(LinkedData node) {
		if (visited.contains(node.getAbsoluteLink())) {
			node.reasonNotVisited = _ALREADYINDEXED;
			return true;
		}

		return false;
	}

	private Boolean isInRootDomain(LinkedData node) {
		if (root.isParentOf(node))
			return true;

		node.reasonNotVisited = _OUTOFDOMAIN;
		return false;
	}

	private Boolean isAllowedByRobotstxt(LinkedData node) {
		if (robotChecker.visit(node.getAbsoluteLink()))
			return true;

		node.reasonNotVisited = _DISALLOWED;
		return false;
	}

	private Boolean isWebSite(LinkedData node) {
		if (node.hasExtension(extensions))
			return true;

		node.reasonNotVisited = _NOTAWEBSITE;
		return false;
	}

	private void generateRoot() throws IOException {
		HashSet<String> result = getLinks(url);

		for (String item : result)
			root.addChild(item);

		root.visited = true;

	}

	private HashSet<String> getLinks(String url) throws IOException {
		//System.out.println(url);
		String content = obtainHTML(url);
		if (content == null)
			return new HashSet<String>();

		HashSet<String> result = parseDocument(Jsoup.connect(url).get());

		return result;
	}

	private String obtainHTML(String url) {

		String content = null;

		try {
			content = parseDocument(Jsoup.connect(url).get()).toString();
		} catch (Exception e) {
			System.out.println(url);
		}
		visited.add(url);
		return content;
	}

	private HashSet<String> parseDocument(Document doc) {
		HashSet<String> hrefTags = new HashSet<String>();		
		// get all links
		Elements links = doc.select("a[href]");
		if (links == null || links.size() == 0)
			return hrefTags;
		for (Element link : links) {
			// get the value from href attribute
			hrefTags.add(link.attr("href"));
		}
		return hrefTags;
	}

}
