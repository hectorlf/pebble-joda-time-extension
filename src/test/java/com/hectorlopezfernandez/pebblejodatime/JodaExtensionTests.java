package com.hectorlopezfernandez.pebblejodatime;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

@RunWith(JUnit4.class)
public class JodaExtensionTests {

	private static PebbleEngine engine;

	@BeforeClass
	public static void setup() {
		JodaExtension je = new JodaExtension();
		Loader<String> l = new ClasspathLoader();
		engine = new PebbleEngine.Builder().loader(l).extension(je).build();
	}

	@Test
	public void testSimpleJodaFilter() throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate("simple-filter.pebble");
		Map<String, Object> context = new HashMap<>();
		context.put("date", fixedEight());
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, context);
		String output = writer.toString();
		Assert.assertTrue(output.contains("0808"));
	}

	@Test
	public void testSimpleJodaFilterWithEsPattern() throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate("simple-filter-es-pattern.pebble");
		Map<String, Object> context = new HashMap<>();
		context.put("date", fixedEight());
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, context);
		String output = writer.toString();
		Assert.assertEquals("08/08/0808,08/08/0808", output);
	}

	@Test
	public void testSimpleJodaFilterWithEsPatternAndLocale() throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate("simple-filter-es-pattern-locale.pebble");
		Map<String, Object> context = new HashMap<>();
		context.put("date", fixedEight());
		context.put("locale", esLocale());
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, context);
		String output = writer.toString();
		System.out.println(output);
		Assert.assertEquals("08/ago/0808,08/ago/0808", output);
	}

	@Test
	public void testSimpleJodaFilterWithEsPatternAndLocaleAndTimezone() throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate("simple-filter-es-pattern-locale-tz.pebble");
		Map<String, Object> context = new HashMap<>();
		context.put("date", fixedEight());
		context.put("locale", esLocale());
		context.put("timezone", madridTimezone());
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, context);
		String output = writer.toString();
		System.out.println(output);
		Assert.assertEquals("08/ago/0808,08/ago/0808", output);
	}

	@Test
	public void testSimpleJodaFilterWithEsStyleAndLocale() throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate("simple-filter-es-style-locale.pebble");
		Map<String, Object> context = new HashMap<>();
		context.put("date", fixedEight());
		context.put("locale", esLocale());
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, context);
		String output = writer.toString();
		System.out.println(output);
		Assert.assertEquals("08-ago-0808 8:08:08,08-ago-0808 8:08:08", output);
	}

	@Test
	public void testSimpleDateTime() throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate("simple-datetime.pebble");
		Map<String, Object> context = new HashMap<>();
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, context);
		String output = writer.toString();
		System.out.println(output);
		Assert.assertTrue(output.contains(String.valueOf(new DateTime().getYear())));
	}
	
	@Test
	public void testSimpleDateTimeFromDate() throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate("simple-datetime-date.pebble");
		Map<String, Object> context = new HashMap<>();
		context.put("date", fixedEight().toDate());
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, context);
		String output = writer.toString();
		System.out.println(output);
		Assert.assertTrue(output.contains("0808"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSimpleDateTimeFromString() throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate("simple-datetime-string.pebble");
		Map<String, Object> context = new HashMap<>();
		context.put("string", "08/08/0808");
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, context);
	}

	@Test
	public void testSimpleDateTimeFromStringAndPattern() throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate("simple-datetime-string-pattern.pebble");
		Map<String, Object> context = new HashMap<>();
		context.put("string", "08/08/0808");
		context.put("pattern", "dd/MM/yyyy");
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, context);
		String output = writer.toString();
		System.out.println(output);
		Assert.assertTrue(output.contains("0808"));
	}

	@Test
	public void testSimpleDateTimeFromStringAndPatternAndLocale() throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate("simple-datetime-string-pattern-locale.pebble");
		Map<String, Object> context = new HashMap<>();
		context.put("string", "08/ago/0808");
		context.put("pattern", "dd/MMM/yyyy");
		context.put("locale", esLocale());
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, context);
		String output = writer.toString();
		System.out.println(output);
		Assert.assertTrue(output.contains("0808"));
	}

	@Test
	public void testSimpleDateTimeFromStringAndPatternAndLocaleAndTimezone() throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate("simple-datetime-string-pattern-locale-tz.pebble");
		Map<String, Object> context = new HashMap<>();
		context.put("string", "08/ago/0808 08:08:08");
		context.put("pattern", "dd/MMM/yyyy HH:mm:ss");
		context.put("locale", esLocale());
		context.put("timezone", madridTimezone());
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, context);
		String output = writer.toString();
		System.out.println(output);
		Assert.assertTrue(output.contains("0808"));
	}

	@Test
	public void testSimpleDateTimeFromStringAndStyleAndLocale() throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate("simple-datetime-string-style-locale.pebble");
		Map<String, Object> context = new HashMap<>();
		context.put("string", "08-ago-0808 08:08:08");
		context.put("style", "MM");
		context.put("locale", esLocale());
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, context);
		String output = writer.toString();
		System.out.println(output);
		Assert.assertTrue(output.contains("0808"));
	}

	private DateTime fixedEight() {
		DateTime dt = new DateTime(808, 8, 8, 8, 8, 8, 8);
		return dt;
	}

	private Locale esLocale() {
		Locale l = Locale.forLanguageTag("es");
		return l;
	}

	private DateTimeZone madridTimezone() {
		DateTimeZone dtz = DateTimeZone.forID("Europe/Madrid");
		return dtz;
	}

}