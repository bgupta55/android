package com.bhanwar.android.rss.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.bhanwar.android.rss.domain.Article;

public class RssHandler extends DefaultHandler
{

	// Feed and Article objects to use for temporary storage
	private Article currentArticle = new Article();
	private List<Article> articleList = new ArrayList<Article>();

	private Map<String, Article> map = new HashMap<String, Article>();

	// Number of articles added so far
	private int articlesAdded = 0;

	// Number of articles to download
	private static final int ARTICLES_LIMIT = 15;

	// Current characters being accumulated
	StringBuffer chars = new StringBuffer();

	public List<Article> getArticleList()
	{
		return articleList;
	}

	/*
	 * This method is called everytime a start element is found (an opening XML marker) here we always reset the characters StringBuffer as we are
	 * only currently interested in the the text values stored at leaf nodes
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes atts)
	{
		chars = new StringBuffer();
	}

	/*
	 * This method is called everytime an end element is found (a closing XML marker) here we check what element is being closed, if it is a relevant
	 * leaf node that we are checking, such as Title, then we get the characters we have accumulated in the StringBuffer and set the current Article's
	 * title to the value
	 * 
	 * If this is closing the "entry", it means it is the end of the article, so we add that to the list and then reset our Article object for the
	 * next one on the stream
	 * 
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (localName.equalsIgnoreCase("title"))
		{
			currentArticle.setTitle(chars.toString());
		}
		else if (localName.equalsIgnoreCase("description"))
		{
			currentArticle.setDescription(chars.toString());
			currentArticle.setEncodedContent("<html><body><div>" + chars.toString() + "</div></body></html>");
		}
		else if (localName.equalsIgnoreCase("pubDate"))
		{
			currentArticle.setPubDate(chars.toString());
		}
		else if (localName.equalsIgnoreCase("guid"))
		{
			currentArticle.setGuid(chars.toString());
		}
		else if (localName.equalsIgnoreCase("author"))
		{
			currentArticle.setAuthor("savemoneyindia");
		}
		else if (localName.equalsIgnoreCase("entry"))
		{

		}

		// Check if looking for article, and if article is complete
		if (localName.equalsIgnoreCase("guid"))
		{

			currentArticle.setAuthor("savemoneyindia");
			if (currentArticle != null && articleList.size() <= ARTICLES_LIMIT)
				articleList.add(currentArticle);

			currentArticle = new Article();

			// Lets check if we've hit our limit on number of articles
			articlesAdded++;
			if (articlesAdded >= ARTICLES_LIMIT)
			{
				// throw new SAXException();
			}
		}
	}

	/*
	 * This method is called when characters are found in between XML markers, however, there is no guarante that this will be called at the end of
	 * the node, or that it will be called only once , so we just accumulate these and then deal with them in endElement() to be sure we have all the
	 * text
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char ch[], int start, int length)
	{
		chars.append(new String(ch, start, length));
	}

	public Map<String, Article> getMap()
	{
		return map;
	}

	public void setMap(Map<String, Article> map)
	{
		this.map = map;
	}

}