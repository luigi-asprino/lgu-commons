package it.cnr.istc.stlab.lgu.commons.semanticweb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import it.cnr.istc.stlab.lgu.commons.semanticweb.utils.URIUtils;

public class URIUtilsTest {

	@Test
	public void test1() {
		assertEquals("%D9%85%2F%D9%81%D9%8A%D8%A7%D8%AA_%D8%A5%D9%8513", URIUtils.percentEncoding("م/فيات_إم13"));
		assertEquals("abc", URIUtils.percentEncoding("abc"));
		assertEquals("abc%D9%85%2F%D9%81%D9%8A%D8%A7%D8%AA_%D8%A5%D9%8513", URIUtils.percentEncoding("abcم/فيات_إم13"));

		System.out.println(URIUtils.normalizeURI("http://asb.com/[/asd"));
	}

}
